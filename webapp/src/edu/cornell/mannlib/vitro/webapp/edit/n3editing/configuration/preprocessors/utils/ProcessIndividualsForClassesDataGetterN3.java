/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
//Returns the appropriate n3 for selection of classes from within class group
public  class ProcessIndividualsForClassesDataGetterN3 extends ProcessClassGroupDataGetterN3 {
	private static String classType = "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.IndividualsForClassesDataGetter";
	protected JSONObject values = null;
	int classCount = 0;
	protected static String individualClassVarNameBase = "classesSelectedInClassGroup";
	private Log log = LogFactory.getLog(ProcessIndividualsForClassesDataGetterN3.class);

	public ProcessIndividualsForClassesDataGetterN3(JSONObject jsonObject){
		this.values = jsonObject;
		if(values != null && values.containsKey(individualClassVarNameBase)) {
			//Check how many individual classes are in json object
			JSONArray ja = values.getJSONArray(individualClassVarNameBase);
			classCount = ja.size();
		}
	}
	//Pass in variable that represents the counter 

	//TODO: ensure correct model returned
	//We shouldn't use the ACTUAL values here but generate the n3 required
    public List<String> retrieveN3Required(int counter) {
    	
    	List<String> classGroupN3 = this.retrieveN3ForTypeAndClassGroup(counter);
    	classGroupN3.addAll(this.addIndividualClassesN3(counter));
    	return classGroupN3;
    	
    }
    
    
    protected List<String> addIndividualClassesN3(int counter) {
		List<String> classN3 = new ArrayList<String>();
		if(classCount > 0) {
			classN3.add(generateIndividualClassN3(counter));
		}
		return classN3;
	}
    
   protected String generateIndividualClassN3(int counter) {
    	String dataGetterVar = getDataGetterVar(counter);
    	String n3 = dataGetterVar + " <" + DisplayVocabulary.GETINDIVIDUALS_FOR_CLASS + "> ";
    	//Consider a multi-valued field - in this case single field with multiple values
    	n3 += getN3VarName(individualClassVarNameBase, counter);
    	/*
    	int i;
    	for(i  = 0; i < classCount; i++) {
    		if(i != 0) {
    			n3+= ",";
    		}
    		n3 += getN3VarName(individualClassVarNameBase + counter, classCount);
    	}*/
    	n3 += " .";
    	return n3;
    	
    }
	public List<String> retrieveN3Optional(int counter) {
    	return null;
    }
    
    //These methods will return the literals and uris expected within the n3
    //and the counter is used to ensure they are numbered correctly 
    
    public List<String> retrieveLiteralsOnForm(int counter) {
    	//no literals, just the class group URI
    	List<String> literalsOnForm = new ArrayList<String>();
    	return literalsOnForm;
    	
    }
    
     
    public List<String> retrieveUrisOnForm(int counter) {
    	//get class group uris
    	List<String> urisOnForm = super.retrieveUrisOnForm(counter);
    	//now get individual classes selected uri
    	//urisOnForm.addAll(getIndividualClassesVarNames(counter));
    	//here again,consider multi-valued
    	urisOnForm.add(getVarName(individualClassVarNameBase, counter));
    	return urisOnForm;
    	
    }
    
   private List<String> getIndividualClassesVarNames(int counter) {
		List<String> individualClassUris = new ArrayList<String>();
		int i;
		for(i = 0; i < classCount; i++) {
			individualClassUris.add(getVarName(individualClassVarNameBase + counter, classCount));
		}
		return individualClassUris;
		
	}
   
   public List<FieldVTwo> retrieveFields(int counter) {
	   List<FieldVTwo> fields = super.retrieveFields(counter);
	   fields.add(new FieldVTwo().setName(getVarName(individualClassVarNameBase, counter)));
	   //Add fields for each class selected
	/*   List<String> classVarNames = getIndividualClassesVarNames(counter);
	   for(String v:classVarNames) {
		   fields.add(new FieldVTwo().setName(v));

	   }*/
	   return fields;
   }
   
   //These var names  match the names of the elements within the json object returned with the info required for the data getter
   
   public List<String> getLiteralVarNamesBase() {
	   return Arrays.asList();   
   }

   //these are for the fields ON the form
   public List<String> getUriVarNamesBase() {
	   return Arrays.asList("classGroup", individualClassVarNameBase);   
   }
   
   @Override
   public String getClassType() {
	   return classType;
   }
   
   //Existing values
   //TODO: Correct
   
   public void populateExistingValues(String dataGetterURI, int counter, OntModel queryModel) {
	   //First, put dataGetterURI within scope as well
	   existingUriValues.put(this.getDataGetterVar(counter), new ArrayList<String>(Arrays.asList(dataGetterURI)));
	   //Sparql queries for values to be executed
	   //And then placed in the correct place/literal or uri
	   String querystr = getExistingValuesSparqlQuery(dataGetterURI);
	   QueryExecution qe = null;
       try{
           Query query = QueryFactory.create(querystr);
           qe = QueryExecutionFactory.create(query, queryModel);
           ResultSet results = qe.execSelect();
           while( results.hasNext()){
        	   QuerySolution qs = results.nextSolution();
        	   Literal saveToVarLiteral = qs.getLiteral("saveToVar");
        	   Literal htmlValueLiteral = qs.getLiteral("htmlValue");
        	   //Put both literals in existing literals
        	   existingLiteralValues.put(this.getVarName("saveToVar", counter),
        			   new ArrayList<Literal>(Arrays.asList(saveToVarLiteral, htmlValueLiteral)));
           }
       } catch(Exception ex) {
    	   log.error("Exception occurred in retrieving existing values with query " + querystr, ex);
       }
	   
	   
   }
  
   
   //?dataGetter a FixedHTMLDataGetter ; display:saveToVar ?saveToVar; display:htmlValue ?htmlValue .
   protected String getExistingValuesSparqlQuery(String dataGetterURI) {
	   String query = this.getSparqlPrefix() + "SELECT ?saveToVar ?htmlValue WHERE {" + 
			   "<" + dataGetterURI + "> display:saveToVar ?saveToVar . \n" + 
			   "<" + dataGetterURI + "> display:htmlValue ?htmlValue . \n" + 
			   "}";
	   return query;
   }

   
   public JSONObject getExistingValuesJSON(String dataGetterURI, OntModel queryModel) {
	   JSONObject jo = new JSONObject();
	   return jo;
   }

}


