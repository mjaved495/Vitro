/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.PageDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

public class PageDaoJena extends JenaBaseDao implements PageDao {

    final static Log log = LogFactory.getLog(PageDaoJena.class);
    
    static protected Query pageQuery;
    static protected Query pageTypeQuery;
    static protected Query pageDataGettersQuery;
    static protected Query pageMappingsQuery;
    static protected Query homePageUriQuery;
    static protected Query classGroupPageQuery;
    static protected Query classIntersectionPageQuery;
    static protected Query individualsForClassesQuery;
    static protected Query individualsForClassesRestrictedQuery;


    static final String prefixes = 
        "PREFIX rdf:   <" + VitroVocabulary.RDF +"> \n" +
        "PREFIX rdfs:  <" + VitroVocabulary.RDFS +"> \n" + 
        "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#> \n" +
        "PREFIX display: <" + DisplayVocabulary.DISPLAY_NS +"> \n";
    
    
    static final protected String pageQueryString = 
        prefixes + "\n" +
        "SELECT ?pageUri ?bodyTemplate ?urlMapping ?title WHERE {\n" +
        "    ?pageUri rdf:type <" + DisplayVocabulary.PAGE_TYPE + ">.\n"+               
        "    OPTIONAL { ?pageUri <" + DisplayVocabulary.REQUIRES_BODY_TEMPLATE + "> ?bodyTemplate }.\n"+
        "    OPTIONAL { ?pageUri <" + DisplayVocabulary.TITLE + "> ?title }.\n"+
        "    OPTIONAL { ?pageUri <" + DisplayVocabulary.URL_MAPPING + "> ?urlMapping . }\n"+                
        "} \n" ;
    
    static final protected String pageTypeQueryString = 
        prefixes + "\n" +
        "SELECT ?type WHERE {\n" +
        "    ?pageUri rdf:type ?type .\n"+                              
        "} \n" ;
    
    //Get data getters
    static final protected String pageDataGettersQueryString = 
        prefixes + "\n" +
        "SELECT ?dataGetter WHERE {\n" +
        "    ?pageUri display:hasDataGetter ?dg .\n"+    
        " 	 ?dg rdf:type ?dataGetter . \n" +
        "} \n" ;
    static final protected String pageMappingsQueryString = 
        prefixes + "\n" +
        "SELECT ?pageUri ?urlMapping WHERE {\n" +
        "    ?pageUri rdf:type <" + DisplayVocabulary.PAGE_TYPE + "> .\n"+                       
        "    ?pageUri <" + DisplayVocabulary.URL_MAPPING + "> ?urlMapping . \n"+                
        "} \n" ;
                          
    static final protected String homePageUriQueryString = 
        prefixes + "\n" +
        "SELECT ?pageUri WHERE {\n" +
        "    ?pageUri rdf:type <" + DisplayVocabulary.HOME_PAGE_TYPE + "> .\n"+                
        "} \n" ;

    static final protected String classGroupPageQueryString = 
        prefixes + "\n" +
        "SELECT ?classGroup WHERE { ?pageUri <" + DisplayVocabulary.FOR_CLASSGROUP + "> ?classGroup . }";
    
    static final protected String classIntersectionPageQueryString = 
    	prefixes + "\n" + 
        "SELECT ?classIntersection ?label ?class ?WHERE { ?pageUri <" + DisplayVocabulary.HAS_CLASS_INTERSECTION + "> ?classIntersection . \n " + 
        " ?classIntersection <" + DisplayVocabulary.CLASS_INTERSECTION + "> ?class . \n "+
        " ?classIntersection rdfs:label ?label . \n" +
        "}";
    	//prefixes + "\n" + 
        //"SELECT ?classIntersection WHERE { ?pageUri <" + DisplayVocabulary.CLASS_INTERSECTION + "> ?classIntersection . }";

    //Query to get what classes are to be employed on the page 
    static final protected String individualsForClassesDataGetterQueryString = 
    	prefixes + "\n" + 
    	 "SELECT ?dg ?class ?restrictClass WHERE {\n" +
         "    ?pageUri display:hasDataGetter ?dg .\n"+    
         " 	 ?dg rdf:type <" + DisplayVocabulary.CLASSINDIVIDUALS_PAGE_TYPE + ">. \n" + 
         " ?dg <" + DisplayVocabulary.GETINDIVIDUALS_FOR_CLASS + "> ?class . \n" +
         "    ?dg <"+ DisplayVocabulary.RESTRICT_RESULTS_BY + "> ?restrictClass .\n" +    
         "} \n" ;
        
    //Given a data getter, check if results are to be restricted by class
    //Also possible to merge these two into the same query
    
    static final protected String individualsForClassesRestrictedQueryString =     
    	prefixes + "\n" +    
    	 "SELECT ?restrictClass WHERE {\n" +
         "    ?dg <"+ DisplayVocabulary.RESTRICT_RESULTS_BY + "> ?restrictClass .\n" +    
         "} \n" ;
    //	 "    ?pageUri display:hasDataGetter ?dg .\n"+    
	
    static{
        try{    
            pageQuery=QueryFactory.create(pageQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for pageQuery " + th.getMessage());
            log.error(pageQueryString);
        }
        try{
            pageTypeQuery = QueryFactory.create(pageTypeQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for pageTypeQuery " + th.getMessage());
            log.error(pageTypeQueryString);
        }
        try{
            pageDataGettersQuery = QueryFactory.create(pageDataGettersQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for pageTypeQuery " + th.getMessage());
            log.error(pageDataGettersQueryString);
        }
        try{    
            pageMappingsQuery=QueryFactory.create(pageMappingsQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for pageMappingsQuery " + th.getMessage());
            log.error(pageMappingsQueryString);
        }   
        try{    
            homePageUriQuery=QueryFactory.create(homePageUriQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for homePageUriQuery " + th.getMessage());
            log.error(homePageUriQueryString);
        }
        try{    
            classGroupPageQuery=QueryFactory.create(classGroupPageQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for classGroupPageQuery " + th.getMessage());
            log.error(classGroupPageQueryString);
        }
        try{    
            classIntersectionPageQuery=QueryFactory.create(classIntersectionPageQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for classIntersectionPageQuery " + th.getMessage());
            log.error(classIntersectionPageQueryString);
        }  
        
        try{    
            individualsForClassesQuery=QueryFactory.create(individualsForClassesDataGetterQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for individualsForClassesQuery " + th.getMessage());
            log.error(individualsForClassesDataGetterQueryString);
        }  
        
        try{    
            individualsForClassesRestrictedQuery=QueryFactory.create(individualsForClassesRestrictedQueryString);
        }catch(Throwable th){
            log.error("could not create SPARQL query for individualsForClassesRestrictedQuery " + th.getMessage());
            log.error(individualsForClassesDataGetterQueryString);
        }  
    }        
    
    public PageDaoJena(WebappDaoFactoryJena wadf) {
        super(wadf);
    }
   
    @Override
    public Map<String, String> getPageMappings() {
        Model displayModel = getOntModelSelector().getDisplayModel();
        Map<String,String> rv = new HashMap<String,String>();
        displayModel.enterCriticalSection(false);
        try{
        QueryExecution qexec = QueryExecutionFactory.create( pageQuery, displayModel );        
            try{            
                ResultSet resultSet = qexec.execSelect();
                while(resultSet.hasNext()){
                    QuerySolution soln = resultSet.next();
                    rv.put(nodeToString(soln.get("urlMapping")) , nodeToString( soln.get("pageUri") ));
                }
            }finally{
                qexec.close();
            }
        }finally{
            displayModel.leaveCriticalSection();
        }
        return rv; 
    }
    
    /**
     * Gets information about a page identified by a URI.   
     */
    @Override
    public Map<String, Object> getPage(String pageUri) {     
      //setup query parameters
      QuerySolutionMap initialBindings = new QuerySolutionMap();
      initialBindings.add("pageUri", ResourceFactory.createResource(pageUri));
      
      List<Map<String, Object>> list; 
      Model displayModel = getOntModelSelector().getDisplayModel();
      displayModel.enterCriticalSection(false);
      try{
          QueryExecution qexec = QueryExecutionFactory.create(pageQuery,displayModel,initialBindings );
          try{
              list = executeQueryToCollection( qexec );
          }finally{
              qexec.close();
          }
      }finally{
          displayModel.leaveCriticalSection();
      }
      
      if( list == null ){
          log.error("executeQueryToCollection returned null.");
          return Collections.emptyMap();
      }
      if( list.size() == 0 ){
          log.debug("no page found for " + pageUri);
          return Collections.emptyMap();          
      }
      
      if( list.size() > 1 )
          log.debug("multiple results found for " + pageUri + " using only the first.");
      Map<String,Object> pageData = list.get(0);
      
      //now get the rdf:types for the page
      //Changing to get the data getters for the page (already know that it's a page type)
      List<String> dataGetters = new ArrayList<String>();
      displayModel.enterCriticalSection(false);
      try{
          QueryExecution qexec = QueryExecutionFactory.create(pageDataGettersQuery, displayModel, initialBindings);
          try{
              ResultSet rs = qexec.execSelect();
              while(rs.hasNext()){
                  QuerySolution soln = rs.next();
                  dataGetters.add( nodeToString( soln.get("dataGetter" ) ));
              }
          }finally{
              qexec.close();
          }
      }finally{
          displayModel.leaveCriticalSection();
      }
      
      if( list == null )
          log.error("could not get data getters for page " + pageUri);
      else
          pageData.put("dataGetters", dataGetters);
      
      return pageData;
    }
    
    @Override
    public String getHomePageUri(){
        Model displayModel = getOntModelSelector().getDisplayModel();
        List<String> rv = new ArrayList<String>();
        displayModel.enterCriticalSection(false);
        try{
            QueryExecution qexec = QueryExecutionFactory.create( homePageUriQuery, displayModel );
            try{                
                ResultSet resultSet = qexec.execSelect();        
                while(resultSet.hasNext()){
                    QuerySolution soln = resultSet.next();
                    rv.add( nodeToString(soln.get("pageUri")) );        
                }
                if( rv.size() == 0 ){
                    log.error("No display:HomePage defined in display model.");
                    return null;
                }
                if( rv.size() > 1 ){
                    log.error("More than one display:HomePage defined in display model.");
                    for( String hp : rv ){
                        log.error("home page: " + hp);
                    }
                }
            }finally{
                qexec.close();            
            }
        }finally{
            displayModel.leaveCriticalSection();
        }
        return rv.get(0);
    }
    
    /**
     * Gets a URI for display:forClassGroup for the specified page.
     * Only one value is expected in the model.
     * This may return null if there is no ClassGroup associated with the page. 
     * @param pageUri
     * @return
     */
    @Override
    public String getClassGroupPage(String pageUri) {        
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("pageUri", ResourceFactory.createResource(pageUri));
        
        Model displayModel = getOntModelSelector().getDisplayModel();
        try{                    
            QueryExecution qexec = QueryExecutionFactory.create( classGroupPageQuery, displayModel , initialBindings);
            try{
                List<String> classGroupsForPage = new ArrayList<String>();
                ResultSet resultSet = qexec.execSelect();        
                while(resultSet.hasNext()){
                    QuerySolution soln = resultSet.next();
                    classGroupsForPage.add( nodeToString(soln.get("classGroup")) );        
                }
                if( classGroupsForPage.size() == 0 ){
                    log.debug("No classgroup info defined in display model for "+ pageUri);
                    return null;
                }
                if( classGroupsForPage.size() > 1 ){
                    log.error("More than one display:forClassGroup defined in display model for page " + pageUri);            
                }        
                return classGroupsForPage.get(0);
            }finally{
                qexec.close();
            }            
        }finally{
            displayModel.leaveCriticalSection();
        }
    }
    
    /**
     * Get the set of intersections for page - each intersection has a label and includes names of the classes for class intersection.  Multiple classes possible.
     */
    public Map<String, List<String>> getClassIntersections(String pageUri) {
    	Map<String, List<String>> classIntersectionsMap = new HashMap<String, List<String>>();
    	 QuerySolutionMap initialBindings = new QuerySolutionMap();
         initialBindings.add("pageUri", ResourceFactory.createResource(pageUri));
         
         Model displayModel = getOntModelSelector().getDisplayModel();
         try{
             QueryExecution qexec = QueryExecutionFactory.create( classIntersectionPageQuery, displayModel , initialBindings);
             try{
                //Assuming unique labels or could use URI itself?
                 //TODO: Review whether to use labels or URIs
                 
                
                 ResultSet resultSet = qexec.execSelect();        
                 while(resultSet.hasNext()){
                     QuerySolution soln = resultSet.next();
                     //Results format should be ?page hasClassIntersection <a>. <a> intersectsWithClass ?c; <a> intersects With Class ?e.
                     String intersectionLabel = nodeToString(soln.get("label"));
                     
                     //first time encountering label, set up
                     if(!classIntersectionsMap.containsKey(intersectionLabel)) {
                    	classIntersectionsMap.put(intersectionLabel, new ArrayList<String>());
                     } 
                     
                     List<String> classes = classIntersectionsMap.get(intersectionLabel);
                     classes.add(nodeToString(soln.get("class")));
                     //classIntersections.add( nodeToString(soln.get("classIntersection")) );        
                 }
                 if( classIntersectionsMap.size() == 0 ){
                     log.debug("No class intersections info defined in display model for "+ pageUri);
                     return null;
                 }
                    
            	return classIntersectionsMap;
             }finally{
                 qexec.close();
             }
         }finally{
             displayModel.leaveCriticalSection();
         }
    }
   
    
    /*
     * Get the classes for which to get individuals returned. This should return a list of class uris. 
     * Return a list of classes to be returned along with any restrictions to be applied.
     * Assumption: The page has a single data getter for classes and restrictions - all classes and restrictions
     * for any data getters for the page will be lumped together. For multiple data getters, will need to return
     * data for each one separately in the same map structure as below.
     *      * Get restriction class to be applied as filter to individuals for page.
     * Although to be used specifically for internal class filtering and will usually be one class returned,
     * allowing for multiple classes to be returned.
     */
    public Map<String, List<String>> getClassesAndRestrictionsForPage(String pageUri) {
   	 	Map<String, List<String>> classesAndRestrictions = new HashMap<String, List<String>>();
    	QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("pageUri", ResourceFactory.createResource(pageUri));
        List<String> classes = new ArrayList<String>();
        
        Model displayModel = getOntModelSelector().getDisplayModel();
        try{
            QueryExecution qexec = QueryExecutionFactory.create( individualsForClassesQuery, displayModel , initialBindings);
            try{
                HashMap<String, String> restrictClassesPresentMap = new HashMap<String, String>();
                List<String>  restrictClasses = new ArrayList<String>();
               
                ResultSet resultSet = qexec.execSelect();        
                while(resultSet.hasNext()){
                    QuerySolution soln = resultSet.next();
                    String dg = nodeToString(soln.get("dg"));
                    classes.add(nodeToString(soln.get("class")));
                    String restrictClass = nodeToString(soln.get("restrictClass"));
                    if(!restrictClassesPresentMap.containsKey(restrictClass)) {
                    	restrictClasses.add(restrictClass);
                    	restrictClassesPresentMap.put(restrictClass, "true");
                    }
                }
                
                if( classes.size() == 0 ){
                    log.debug("No classes  defined in display model for "+ pageUri);
                    return null;
                }
                classesAndRestrictions.put("classes", classes);  
                classesAndRestrictions.put("restrictClasses", restrictClasses);
                return classesAndRestrictions;
            }finally{
                qexec.close();
            }
        }finally{
            displayModel.leaveCriticalSection();
        }
    }
    
    
   
    
    /* *************************** Utility methods ********************************* */
    
    /**
     * Converts a sparql query that returns a multiple rows to a list of maps.
     * The maps will have column names as keys to the values.
     * This method will not close qexec.
     */
    protected List<Map<String, Object>> executeQueryToCollection(
            QueryExecution qexec) {
        List<Map<String, Object>> rv = new ArrayList<Map<String, Object>>();
        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            rv.add(querySolutionToMap(soln));
        }
        return rv;
    }
    
    protected Map<String,Object> querySolutionToMap( QuerySolution soln ){
        Map<String,Object> map = new HashMap<String,Object>();
        Iterator<String> varNames = soln.varNames();
        while(varNames.hasNext()){
            String varName = varNames.next();
            map.put(varName, nodeToObject( soln.get(varName)));
        }
        return map;
    }
    
    static protected Object nodeToObject( RDFNode node ){
        if( node == null ){
            return "";
        }else if( node.isLiteral() ){
            Literal literal = node.asLiteral();
            return literal.getValue();
        }else if( node.isURIResource() ){
            Resource resource = node.asResource();
            return resource.getURI();
        }else if( node.isAnon() ){  
            Resource resource = node.asResource();
            return resource.getId().getLabelString(); //get b-node id
        }else{
            return "";
        }
    }

    static protected String nodeToString( RDFNode node ){
        if( node == null ){
            return "";
        }else if( node.isLiteral() ){
            Literal literal = node.asLiteral();
            return literal.getLexicalForm();
        }else if( node.isURIResource() ){
            Resource resource = node.asResource();
            return resource.getURI();
        }else if( node.isAnon() ){  
            Resource resource = node.asResource();
            return resource.getId().getLabelString(); //get b-node id
        }else{
            return "";
        }
    }
    protected Map<String,Object> resultsToMap(){
        return null;
    }


    

}
