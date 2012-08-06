/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

//Returns the appropriate n3 based on data getter
public abstract class ProcessDataGetterAbstract implements ProcessDataGetterN3 {
	
	public ProcessDataGetterAbstract(){
		
	}
	
    //placeholder so need "?" in front of the variable
    public String getDataGetterVar(int counter) {
    	return "?" + getDataGetterVarName(counter);
    }
    
    //Just the var name, no "?"
    public String getDataGetterVarName(int counter) {
    	return "dataGetter" + counter;
    }
    
    public String getPrefixes() {
    	return "@prefix display: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#> . \n" + 
    			"@prefix rdfs:<http://www.w3.org/2000/01/rdf-schema#> . \n";
    }
  
   public String getVarName(String base, int counter) {
	   return base + counter;
   }
   
   //For use within n3 strings, need a "?"
   public String getN3VarName(String base, int counter) {
	   return "?" + getVarName(base, counter);
   }
   
   //For handling encoded single and double quotes
   //For fixed html and sparql data getters, replaces encoded quotes with escaped quotes
   //Can be overridden in other processors if need be
   public String replaceEncodedQuotesWithEscapedQuotes(String inputStr) {
	   return inputStr.replaceAll("&#39;", "\'").replaceAll("&quot;", "\"");
   }
   
   //Return name of new resources
   public List<String> getNewResources(int counter) {
	   //Each data getter requires a new resource
	   List<String> newResources = new ArrayList<String>();
	   newResources.add("dataGetter" + counter);
	   return newResources;
   }
   
   protected String getSparqlPrefix() {
		  return  "PREFIX display: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#> \n" + 
				  "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
   
   }
   
   //For existing values
   protected  Map<String, List<Literal>> existingLiteralValues = new HashMap<String, List<Literal>>();
   protected Map<String, List<String>> existingUriValues = new HashMap<String, List<String>>();
   public Map<String, List<Literal>> retrieveExistingLiteralValues() {
	  return existingLiteralValues;
   }
   public Map<String, List<String>> retrieveExistingUriValues() {
	  return existingUriValues;
   }
	   
	//Data getter var needs to be included in uris in scope
   public void populateExistingDataGetterURI(String dataGetterURI, int counter) {
	   existingUriValues.put(this.getVarName("dataGetter", counter), new ArrayList<String>(Arrays.asList(dataGetterURI)));

   }
   

 
}


