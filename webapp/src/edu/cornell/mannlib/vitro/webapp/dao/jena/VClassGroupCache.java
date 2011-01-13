/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.dao.VClassGroupDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.WebappDaoFactoryFiltering;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilterUtils;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;
import edu.cornell.mannlib.vitro.webapp.flags.PortalFlag;

public class VClassGroupCache implements ServletContextListener{
    
    /* This is the cache of VClassGroups.  It is a portal id to list of VClassGroups  */
    private transient ConcurrentHashMap<Integer, List<VClassGroup>> _groupListMap;
        
    private transient ConcurrentLinkedQueue<String> _rebuildQueue;            
    private RebuildGroupCacheThread _cacheRebuildThread;
    private ServletContext context;
    
    private static final Log log = LogFactory.getLog(VClassGroupCache.class);
    
    private static boolean ORDER_BY_DISPLAYRANK = true;
    private static boolean INCLUDE_UNINSTANTIATED = true;
    private static boolean INCLUDE_INDIVIDUAL_COUNT = true;
    
    public VClassGroupCache(){}
    
    /** 
     * Use getVClassGroupCache(ServletContext) to get a VClassGroupCache.
     */
    private VClassGroupCache(ServletContext context) {
        this.context = context;
        this._groupListMap = new ConcurrentHashMap<Integer, List<VClassGroup>>();
        this._rebuildQueue = new ConcurrentLinkedQueue<String>();
       
        VClassGroupCacheChangeListener bccl = new VClassGroupCacheChangeListener(this);
        ModelContext.getJenaOntModel(context).register(bccl);
        ModelContext.getBaseOntModel(context).register(bccl);
        ModelContext.getInferenceOntModel(context).register(bccl);
        ModelContext.getUnionOntModelSelector(context).getABoxModel().register(bccl);
       
        _rebuildQueue.add(REBUILD_EVERY_PORTAL);
        _cacheRebuildThread = new RebuildGroupCacheThread(this);
        _cacheRebuildThread.setDaemon(true);
        _cacheRebuildThread.start();
        _cacheRebuildThread.informOfQueueChange();       
    }
     
    public static VClassGroupCache getVClassGroupCache(ServletContext sc){
        return (VClassGroupCache) sc.getAttribute("VClassGroupCache");
    }
        
    public List<VClassGroup> getGroups( int portalId ){
        return getGroups(getVCGDao(),portalId );
    }
    
    /**
     * May return null.
     */
    public VClassGroup getGroup( int portalId, String vClassGroupURI ){
        if( vClassGroupURI == null || vClassGroupURI.isEmpty() )
            return null;
        List<VClassGroup> cgList = getGroups(portalId);
        for( VClassGroup cg : cgList ){
            if( vClassGroupURI.equals( cg.getURI()))
                return cg;
        }
        return null;
    }
    
    public void clearGroupCache(){
        _groupListMap = new ConcurrentHashMap<Integer, List<VClassGroup>>();
    }   
    
    
    private List<VClassGroup> getGroups( VClassGroupDao vcgDao , int portalId, boolean includeIndividualCount ){
        List<VClassGroup> groupList = _groupListMap.get(portalId);
        if( groupList == null ){
            log.debug("needed to build vclassGroups for portal " + portalId);
            // Get all classgroups, each populated with a list of their member vclasses            
            List<VClassGroup> groups = 
                vcgDao.getPublicGroupsWithVClasses(ORDER_BY_DISPLAYRANK, INCLUDE_UNINSTANTIATED, includeIndividualCount); 

            // remove classes that have been configured to be hidden from search results
            vcgDao.removeClassesHiddenFromSearch(groups);
            
            // now cull out the groups with no populated classes            
            //vcgDao.removeUnpopulatedGroups(groups);
            
            return groups;
        } else {
            return groupList;
        }
    }   
        
    private List<VClassGroup> getGroups( VClassGroupDao vcgDao, int portalId) {
        return getGroups( vcgDao, portalId, INCLUDE_INDIVIDUAL_COUNT);
    }   
    
    private void requestCacheUpdate(String portalUri){
        log.debug("requesting update for portal " + portalUri);
        _rebuildQueue.add(portalUri);
        _cacheRebuildThread.informOfQueueChange();
    }

    protected synchronized void refreshGroupCache() {
       long start = System.currentTimeMillis();
       try{
           boolean rebuildAll = false;
           HashSet<String> portalURIsToRebuild = new HashSet<String>();
           String portalUri;
           while ( null != (portalUri = _rebuildQueue.poll()) ){
               if( portalUri.equals(REBUILD_EVERY_PORTAL)){
                   rebuildAll = true;
                   _rebuildQueue.clear();
                   break;
               }else{
                   portalURIsToRebuild.add(portalUri);
               }
           }
                      
           WebappDaoFactory wdFactory = (WebappDaoFactory)context.getAttribute("webappDaoFactory");
           if( wdFactory == null ){
               log.error("Unable to rebuild cache: could not get 'webappDaoFactory' from Servletcontext");
               return;
           }

           Collection<Portal> portals;
           if( rebuildAll ){
               portals = wdFactory.getPortalDao().getAllPortals();
           }   else {
               portals = new LinkedList<Portal>();
               for( String uri : portalURIsToRebuild){
                   Portal p =wdFactory.getPortalDao().getPortalByURI(uri);
                   if( p!= null)
                       portals.add(wdFactory.getPortalDao().getPortalByURI(uri));
               }
           }
           
           for(Portal portal : portals){
               rebuildCacheForPortal(portal,wdFactory);
           }
           log.info("rebuilt ClassGroup cache in " + (System.currentTimeMillis() - start) + " msec");
       }catch (Exception ex){
           log.error("could not rebuild cache", ex);
       }
    }

    protected synchronized void rebuildCacheForPortalUri(String uri){        
        WebappDaoFactory wdFactory = (WebappDaoFactory)context.getAttribute("webappDaoFactory");
        if( wdFactory == null ){
            log.error("Unable to rebuild cache: could not get 'webappDaoFactory' from Servletcontext");
            return;
        }
        Portal portal = wdFactory.getPortalDao().getPortalByURI(uri);
        rebuildCacheForPortal(portal,wdFactory);
    }

    protected synchronized void rebuildCacheForPortal(Portal portal, WebappDaoFactory wdFactory){
        VitroFilters vFilters = null;
        
        boolean singlePortalApplication = wdFactory.getPortalDao().getAllPortals().size() == 1;
        
        if ( singlePortalApplication ) {
            if ( vFilters == null ) 
                vFilters = VitroFilterUtils.getDisplayFilterByRoleLevel(RoleLevel.PUBLIC, wdFactory);
        } else if ( portal.isFlag1Filtering() ){
            PortalFlag pflag = new PortalFlag(portal.getPortalId());
            if( vFilters == null)
                vFilters = VitroFilterUtils.getFilterFromPortalFlag(pflag);
            else
                vFilters = vFilters.and( VitroFilterUtils.getFilterFromPortalFlag(pflag));
        }
        
        WebappDaoFactory filteringDaoFactory ;
        
        if( vFilters !=null ){
            filteringDaoFactory = new WebappDaoFactoryFiltering(wdFactory,vFilters);
        }else{
            filteringDaoFactory = wdFactory;
        }
        _groupListMap.remove(portal.getPortalId());
        if ( !singlePortalApplication ) {
            _groupListMap.put(portal.getPortalId(), 
                    getGroups(filteringDaoFactory.getVClassGroupDao(),portal.getPortalId()));
        } else {
            List<VClassGroup> unfilteredGroups = getGroups(wdFactory.getVClassGroupDao(), portal.getPortalId(), INCLUDE_INDIVIDUAL_COUNT);
            List<VClassGroup> filteredGroups = getGroups(filteringDaoFactory.getVClassGroupDao(),portal.getPortalId(), !INCLUDE_INDIVIDUAL_COUNT);
            _groupListMap.put(portal.getPortalId(), removeFilteredOutGroupsAndClasses(unfilteredGroups, filteredGroups));
            // BJL23:  You may be wondering, why this extra method?  
            // Can't we just use the filtering DAO?
            // Yes, but using the filtered DAO involves an expensive method
            // called correctVClassCounts() that requires each individual
            // in a VClass to be retrieved and filtered.  This is fine in memory,
            // but awful when using a database.  We can't (yet) avoid all
            // this work when portal filtering is involved, but we can
            // short-circuit it when we have a single portal by using
            // the filtering DAO only to filter groups and classes,
            // and the unfiltered DAO to get the counts.
        }        
    }
    
    private List<VClassGroup> removeFilteredOutGroupsAndClasses(List<VClassGroup> unfilteredGroups, List<VClassGroup> filteredGroups) {
        List<VClassGroup> groups = new ArrayList<VClassGroup>();
        Set<String> allowedGroups = new HashSet<String>();
        Set<String> allowedVClasses = new HashSet<String>();
        for (VClassGroup group : filteredGroups) {
            if (group.getURI() != null) {
                allowedGroups.add(group.getURI());
            }
            for (VClass vcl : group) {
                if (vcl.getURI() != null) {
                    allowedVClasses.add(vcl.getURI());
                }
            }
        }
        for (VClassGroup group : unfilteredGroups) {
            if (allowedGroups.contains(group.getURI())) {
                groups.add(group);
            }
            List<VClass> tmp = new ArrayList<VClass>();
            for (VClass vcl : group) {
                if (allowedVClasses.contains(vcl.getURI())) {
                    tmp.add(vcl);
                }
            }
            group.setVitroClassList(tmp);
        }
        return groups;
    }
    



    /* ******************  Jena Model Change Listener***************************** */
    private class VClassGroupCacheChangeListener extends StatementListener {
        private VClassGroupCache cache = null;
        public VClassGroupCacheChangeListener(VClassGroupCache cache){
            this.cache=cache;
        }

        public void addedStatement(Statement stmt) {
            checkAndDoUpdate(stmt); 
        }
        
        public void removedStatement(Statement stmt) {
            checkAndDoUpdate(stmt);     
        }

        private void checkAndDoUpdate(Statement stmt){
            if( stmt==null ) return;
            if( log.isDebugEnabled()){
                log.debug("subject: " + stmt.getSubject().getURI());
                log.debug("predicate: " + stmt.getPredicate().getURI());
            }
            if( RDF.type.getURI().equals( stmt.getPredicate().getURI())  ){
                requestCacheUpdate(REBUILD_EVERY_PORTAL);
            } else if( VitroVocabulary.PORTAL_FLAG1FILTERING.equals( stmt.getPredicate().getURI())){
                requestCacheUpdate(stmt.getSubject().getURI());
            } else if( VitroVocabulary.IN_CLASSGROUP.equals( stmt.getPredicate().getURI() )){
                requestCacheUpdate(REBUILD_EVERY_PORTAL);
            }
        }
    }
    /* ******************** RebuildGroupCacheThread **************** */
    protected class RebuildGroupCacheThread extends Thread {
        VClassGroupCache cache;
        boolean die = false;
        boolean queueChange = false;
        long queueChangeMills = 0;
        private boolean awareOfQueueChange = false;

        RebuildGroupCacheThread(VClassGroupCache cache) {
        	super("VClassGroupCache.RebuildGroupCacheThread");
            this.cache = cache;
        }
        public void run() {
            while(true){
                try{
                    synchronized (this){
                        if( _rebuildQueue.isEmpty() ){
                             log.debug("rebuildGroupCacheThread.run() -- queye empty, sleep");
                             wait(1000 * 60 );
                        }
                        if( die ) {
                            log.debug("doing rebuildGroupCacheThread.run() -- die()");
                            return;
                        }
                        if( queueChange && !awareOfQueueChange){
                            log.debug("rebuildGroupCacheThread.run() -- awareOfQueueChange, delay start of rebuild");
                            awareOfQueueChange = true;
                            wait(200);
                        }
                    }

                    if( awareOfQueueChange && System.currentTimeMillis() - queueChangeMills > 200){
                        log.debug("rebuildGroupCacheThread.run() -- refreshGroupCache()");
                        cache.refreshGroupCache();
                        synchronized( this){
                            queueChange = false;
                        }
                        awareOfQueueChange = false;
                    }else {
                        synchronized( this ){
                            wait(200);
                        }
                    }
                }   catch(InterruptedException e){}
            }


        }

        synchronized void informOfQueueChange(){
            queueChange = true;
            queueChangeMills = System.currentTimeMillis();
            this.notifyAll();
        }

        synchronized void kill(){
            die = true;
            notifyAll();
        }
    }

    protected VClassGroupDao getVCGDao(){
        if( context == null ){
            log.error("Context was not set for VClassGroupCache");
            return null;
        }
        WebappDaoFactory wdf =(WebappDaoFactory)context.getAttribute("webappDaoFactory");
        if( wdf == null ){
            log.error("Cannot get webappDaoFactory from context");
            return null;
        }else
            return wdf.getVClassGroupDao();
    }
    
    protected static String REBUILD_EVERY_PORTAL ="Rebuild every portal.";

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        if( _cacheRebuildThread != null )
            _cacheRebuildThread.kill();        
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {        
        arg0.getServletContext().setAttribute("VClassGroupCache",  new VClassGroupCache(arg0.getServletContext()) );
    }
}
