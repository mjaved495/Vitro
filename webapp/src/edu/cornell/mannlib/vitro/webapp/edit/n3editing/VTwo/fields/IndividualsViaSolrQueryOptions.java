/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields;

import java.util.ArrayList;
import java.util.Collections;
<<<<<<< HEAD
=======
import java.util.Comparator;
>>>>>>> d82c894... updates for deletion and setting up method of getting field options for individuals with a certain most specific type
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
<<<<<<< HEAD
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
=======
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.pellet.PelletListener;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultObjectPropertyFormGenerator;
>>>>>>> d82c894... updates for deletion and setting up method of getting field options for individuals with a certain most specific type
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;

/*
 * This runs a solr query to get individuals of a certain class instead of relying on the dao classes. 
 * Also it gets individuals that belong to the most specific type(s) specified.
 */
public class IndividualsViaSolrQueryOptions extends IndividualsViaVClassOptions implements FieldOptions {
	private Log log = LogFactory.getLog(IndividualsViaSolrQueryOptions.class);	

<<<<<<< HEAD
    private ServletContext servletContext;
    public IndividualsViaSolrQueryOptions(ServletContext context, String ... vclassURIs) throws Exception {
        super(vclassURIs);           
=======
    public static final String LEFT_BLANK = "";
    private List<String> vclassURIs;    
    private String defaultOptionLabel;    
    private ServletContext servletContext;
    public IndividualsViaSolrQueryOptions(ServletContext context, String ... vclassURIs) throws Exception {
        super();           
>>>>>>> d82c894... updates for deletion and setting up method of getting field options for individuals with a certain most specific type
        this.servletContext = context;
    }
    
    @Override
    protected Map<String,Individual> getIndividualsForClass(String vclassURI, WebappDaoFactory wDaoFact ){
    	Map<String, Individual> individualMap = new HashMap<String, Individual>();
    	try {
	    	SolrServer solrServer = SolrSetup.getSolrServer(servletContext);
	
			//solr query for type count.    		
			SolrQuery query = new SolrQuery();
			if( VitroVocabulary.OWL_THING.equals( vclassURI )){
				query.setQuery( "*:*" );    			
			}else{
				query.setQuery( VitroSearchTermNames.MOST_SPECIFIC_TYPE_URIS + ":" + vclassURI);
			}
<<<<<<< HEAD
			 query.setStart(0)
             .setRows(1000);
	        query.setFields(VitroSearchTermNames.URI); // fields to retrieve

			QueryResponse rsp = solrServer.query(query);
			SolrDocumentList docs = rsp.getResults();
			long found = docs.getNumFound();
			if(found > 0) {
				for (SolrDocument doc : docs) {
					try {
						String uri = doc.get(VitroSearchTermNames.URI).toString();
						Individual individual = wDaoFact.getIndividualDao().getIndividualByURI(uri);
						if (individual == null) {
							log.debug("No individual for search document with uri = " + uri);
						} else {
							individualMap.put(individual.getURI(), individual);
							log.debug("Adding individual " + uri + " to individual list");
						} 
					}
					catch(Exception ex) {
						log.error("An error occurred retrieving the individual solr query resutls", ex);
					}
=======
			query.setRows(0);	
			QueryResponse rsp = solrServer.query(query);
			SolrDocumentList docs = rsp.getResults();
			long found = docs.getNumFound();
			for (SolrDocument doc : docs) {
				String uri = doc.get(VitroSearchTermNames.URI).toString();
				Individual individual = wDaoFact.getIndividualDao().getIndividualByURI(uri);
				if (individual == null) {
					log.debug("No individual for search document with uri = " + uri);
				} else {
					individualMap.put(individual.getURI(), individual);
					log.debug("Adding individual " + uri + " to individual list");
>>>>>>> d82c894... updates for deletion and setting up method of getting field options for individuals with a certain most specific type
				}
			}

    	} catch(Exception ex) {
    		log.error("Error occurred in executing solr query ", ex);
    	}
        return individualMap;        
    }
    
    @Override
    public Map<String, String> getOptions(
            EditConfigurationVTwo editConfig, 
            String fieldName, 
            WebappDaoFactory wDaoFact) throws Exception {              
        
    	 Map<String, Individual> individualMap = new HashMap<String, Individual>();

<<<<<<< HEAD
         for( String vclassURI : vclassURIs){
=======
         for( String vclassURI : this.vclassURIs){
>>>>>>> d82c894... updates for deletion and setting up method of getting field options for individuals with a certain most specific type
             individualMap.putAll(  getIndividualsForClass( vclassURI, wDaoFact) );
         }

         //sort the individuals 
         List<Individual> individuals = new ArrayList<Individual>();
         individuals.addAll(individualMap.values());
         Collections.sort(individuals);

         Map<String, String> optionsMap = new HashMap<String,String>();
         
         if (defaultOptionLabel != null) {
             optionsMap.put(LEFT_BLANK, defaultOptionLabel);
         }

         if (individuals.size() == 0) {     
        	//return empty map, unlike individualsViaVclass
             return optionsMap ;
         } else {
             for (Individual ind : individuals) {                
                 if (ind.getURI() != null) {
                     optionsMap.put(ind.getURI(), ind.getName().trim());
                 }
             }
         }
         return optionsMap;
        
    }
    
    
}


