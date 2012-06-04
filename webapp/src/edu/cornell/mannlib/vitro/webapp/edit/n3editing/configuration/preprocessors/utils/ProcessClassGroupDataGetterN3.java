/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
//Returns the appropriate n3 based on data getter
public  class ProcessClassGroupDataGetterN3 extends ProcessDataGetterAbstract {
	private static String classType = "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData";
	private static  String classGroupVarBase = "classGroup";
	public ProcessClassGroupDataGetterN3(){
		
	}
	//Pass in variable that represents the counter 

	//TODO: ensure correct model returned
	//We shouldn't use the ACTUAL values here but generate the n3 required
    public List<String> retrieveN3Required(int counter) {
    	return this.retrieveN3ForTypeAndClassGroup(counter);
    	
    }
    public List<String> retrieveN3Optional(int counter) {
    	return null;
    }
    
    public List<String> retrieveN3ForTypeAndClassGroup(int counter) {
    	String n3ForType = this.getN3ForTypePartial(counter);
    	String n3 = n3ForType +"; \n" + 
    		"<" + DisplayVocabulary.FOR_CLASSGROUP + "> " + getN3VarName(classGroupVarBase, counter) + " .";
    	List<String> n3List = new ArrayList<String>();
    	n3List.add(getPrefixes() + n3);
    	return n3List;
    }
    
    public String getN3ForTypePartial(int counter) {
    	String dataGetterVar = getDataGetterVar(counter);
    	String n3 = dataGetterVar + " a <" + getClassType() + ">";
    	return n3;
    }
    
    //These methods will return the literals and uris expected within the n3
    //and the counter is used to ensure they are numbered correctly 
    
    public List<String> retrieveLiteralsOnForm(int counter) {
    	//no literals, just the class group URI
    	List<String> literalsOnForm = new ArrayList<String>();
    	return literalsOnForm;
    	
    }
    
     
    public List<String> retrieveUrisOnForm(int counter) {
    	List<String> urisOnForm = new ArrayList<String>();
    	//Class group is a URI
    	urisOnForm.add(getVarName("classGroup", counter));
    	return urisOnForm;
    	
    }
    
   public List<FieldVTwo> retrieveFields(int counter) {
	   List<FieldVTwo> fields = new ArrayList<FieldVTwo>();
	   fields.add(new FieldVTwo().setName(getVarName("classGroup", counter)));
	   return fields;
   }
   
   //These var names  match the names of the elements within the json object returned with the info required for the data getter
   
   public List<String> getLiteralVarNamesBase() {
	   return Arrays.asList();   
   }

   //these are for the fields ON the form
   public List<String> getUriVarNamesBase() {
	   return Arrays.asList("classGroup");   
   }

   //This class can be extended so returning type here
   public String getClassType() {
	   return classType;
   }

}


