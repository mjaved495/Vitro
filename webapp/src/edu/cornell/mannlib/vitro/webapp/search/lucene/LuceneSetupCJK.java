/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.search.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.search.BooleanQuery;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.WebappDaoFactoryFiltering;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilterUtils;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.SearchReindexingListener;
import edu.cornell.mannlib.vitro.webapp.search.beans.ContextNodesInclusionFactory;
import edu.cornell.mannlib.vitro.webapp.search.beans.IndividualProhibitedFromSearch;
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch;
import edu.cornell.mannlib.vitro.webapp.search.indexing.IndexBuilder;

/**
 * Setup objects for lucene searching and indexing.
 *
 * The indexing and search objects, IndexBuilder and Searcher are found by the
 * controllers IndexController and SearchController through the servletContext.
 * This object will have the method contextInitialized() called when the tomcat
 * server starts this webapp.
 *
 *  The contextInitialized() will try to find the lucene index directory,
 *  make a LueceneIndexer and a LuceneSearcher.  The LuceneIndexer will
 *  also get a list of Obj2Doc objects so it can translate object to lucene docs.
 *
 * To execute this at context creation put this in web.xml:
    <listener>
        <listener-class>
            edu.cornell.mannlib.vitro.search.setup.LuceneSetup
        </listener-class>
    </listener>

 * @author bdc34
 *
 */
public class LuceneSetupCJK implements javax.servlet.ServletContextListener {
		private static String indexDir = null;
        private static final Log log = LogFactory.getLog(LuceneSetupCJK.class.getName());
        private static final String PROPERTY_VITRO_HOME = "vitro.home.directory";
    	private static final String LUCENE_SUBDIRECTORY_NAME = "luceneIndex";

        /**
         * Gets run to set up DataSource when the webapp servlet context gets created.
         */
        @Override
		@SuppressWarnings("unchecked")
        public void contextInitialized(ServletContextEvent sce) {
            ServletContext context = sce.getServletContext();
            log.info("**** Running "+this.getClass().getName()+".contextInitialized()");
            try{
            indexDir = getIndexDirName(sce);
            log.info("Lucene indexDir: " + indexDir);

            setBoolMax();
            
            HashSet dataPropertyBlacklist = new HashSet<String>();
            context.setAttribute(LuceneSetup.SEARCH_DATAPROPERTY_BLACKLIST, dataPropertyBlacklist);
            
            HashSet objectPropertyBlacklist = new HashSet<String>();
            objectPropertyBlacklist.add("http://www.w3.org/2002/07/owl#differentFrom");
            context.setAttribute(LuceneSetup.SEARCH_OBJECTPROPERTY_BLACKLIST, objectPropertyBlacklist);
            
            //This is where to get a LucenIndex from.  The indexer will
            //need to reference this to notify it of updates to the index
            LuceneIndexFactory lif = LuceneIndexFactory.setup(context, indexDir);            
            String liveIndexDir = lif.getLiveIndexDir(context);
            
            //here we want to put the LuceneIndex object into the application scope
            LuceneIndexer indexer = new LuceneIndexer(indexDir, liveIndexDir, null, getAnalyzer());            
            context.setAttribute(LuceneSetup.ANALYZER, getAnalyzer());
            
            OntModel displayOntModel = (OntModel) sce.getServletContext().getAttribute("displayOntModel");
            Entity2LuceneDoc translator = new Entity2LuceneDoc( 
                    new ProhibitedFromSearch(DisplayVocabulary.PRIMARY_LUCENE_INDEX_URI, displayOntModel),
                    new IndividualProhibitedFromSearch(context),
                    new ContextNodesInclusionFactory(DisplayVocabulary.CONTEXT_NODES_URI, displayOntModel, context));                                  
            indexer.addObj2Doc(translator);     
                                              
            indexer.setLuceneIndexFactory(lif);
            
            //This is where the builder gets the list of places to try to 
            //get objects to index. It is filtered so that non-public text
            //does not get into the search index.            
            WebappDaoFactory wadf = 
                (WebappDaoFactory) context.getAttribute("webappDaoFactory");
			VitroFilters vf = VitroFilterUtils.getPublicFilter(context);
            wadf = new WebappDaoFactoryFiltering(wadf,vf);
            
            List sources = new ArrayList();
            sources.add(wadf.getIndividualDao());

            IndexBuilder builder = new IndexBuilder(context,indexer,sources);

            // here we add the IndexBuilder with the LuceneIndexer
            // to the servlet context so we can access it later in the webapp.
            context.setAttribute(IndexBuilder.class.getName(),builder);
            
            //set up listeners so search index builder is notified of changes to model            
            OntModel baseOntModel = (OntModel)sce.getServletContext().getAttribute("baseOntModel");
            OntModel jenaOntModel = (OntModel)sce.getServletContext().getAttribute("jenaOntModel");
            SearchReindexingListener srl = new SearchReindexingListener( builder );
            ModelContext.registerListenerForChanges(sce.getServletContext(), srl);
        	
            }catch(Exception ex){
                log.error("Could not setup lucene full text search." , ex);
            }
            
            log.debug("**** End of "+this.getClass().getName()+".contextInitialized()");
        }

        /**
         * Gets run when the webApp Context gets destroyed.
         */
        @Override
		public void contextDestroyed(ServletContextEvent sce) {
        	
            log.info("**** Running "+this.getClass().getName()+".contextDestroyed()");
            IndexBuilder builder = (IndexBuilder)sce.getServletContext().getAttribute(IndexBuilder.class.getName());
        	builder.stopIndexingThread();
        }

        /**
         * In wild card searches the query is first broken into many boolean searches
         * OR'ed together.  So if there is a query that would match a lot of records
         * we need a high max boolean limit for the lucene search.
         *
         * This sets some static method in the lucene library to achieve this.
         */
        public static void setBoolMax() {
            BooleanQuery.setMaxClauseCount(16384);
        }

    	/**
    	 * Gets the name of the directory to store the lucene index in. The
    	 * {@link ConfigurationProperties} should have a property named
    	 * 'vitro.home.directory' which has the parent directory of the directory to
    	 * store the lucene index for this clone in. If the property is not found,
    	 * an exception will be thrown.
    	 * 
    	 * @return a string that is the directory to store the lucene index.
    	 * @throws IllegalStateException
    	 *             if the property is not found, 
    	 *             or if the home directory does not exist.
    	 * @throws IOException
    	 *             if the directory doesn't exist and we fail to create it.
    	 */
		private String getIndexDirName(ServletContextEvent cte) throws IOException {
			String homeDirName = ConfigurationProperties.getBean(cte).getProperty(
					PROPERTY_VITRO_HOME);
			if (homeDirName == null) {
				throw new IllegalStateException(PROPERTY_VITRO_HOME
						+ " not found in properties file.");
			}
	
			File homeDir = new File(homeDirName);
			if (!homeDir.exists()) {
				throw new IllegalStateException("Vitro home directory '"
						+ homeDir.getAbsolutePath() + "' does not exist.");
			}
	
			File luceneDir = new File(homeDir, LUCENE_SUBDIRECTORY_NAME);
			if (!luceneDir.exists()) {
				boolean created = luceneDir.mkdir();
				if (!created) {
					throw new IOException(
							"Unable to create Lucene index directory at '"
									+ luceneDir + "'");
				}
			}
	
			return luceneDir.getPath();
		}

    /**
     * Gets the analyzer that will be used when building the indexing
     * and when analyzing the incoming search terms.
     *
     * @return
     */
    private Analyzer getAnalyzer() {
        return new CJKAnalyzer();        
    }
    
}
