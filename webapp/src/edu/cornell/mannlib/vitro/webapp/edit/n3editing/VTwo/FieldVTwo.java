/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
public class FieldVTwo {

    public enum OptionsType {
        LITERALS, 
        HARDCODED_LITERALS,
        STRINGS_VIA_DATATYPE_PROPERTY, 
        INDIVIDUALS_VIA_OBJECT_PROPERTY, 
        INDIVIDUALS_VIA_VCLASS, 
        CHILD_VCLASSES, 
        CHILD_VCLASSES_WITH_PARENT,
        VCLASSGROUP,
        FILE, 
        UNDEFINED, 
        DATETIME, 
        DATE,
        TIME
    };

    public static String RDF_XML_LITERAL_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral";
        
    private boolean newResource;

    private static Log log = LogFactory.getLog( FieldVTwo.class ); 
       
    private String name;
    
    /**
     * List of basic validators.  See BaiscValidation.
     */
    private List <String> validators;

    /**
     * What type of options is this?
     */
    private OptionsType optionsType;
    
    /**
     * Special class to use for option type
     */
    private Class customOptionType;
    
     /**
     * Used for  building Options when OptionsType is INDIVIDUALS_VIA_OBJECT_PROPERTY
     */
    private String predicateUri;
     /**
     * Used for  building Options when OptionsType is INDIVIDUALS_VIA_VCLASS
     */
    private String objectClassUri;
    
    /**
     * Used for holding the expected/required datatype of the predicate when the predicate is a datatype propertyl.
     * this can be a explicit URI or a qname.
     * example:
     *  "this is the literal"^^<http://someuri.com/v1.2#type23>
     *  or
     *  "this is the literal"^^someprefix:type23
     */
    private String rangeDatatypeUri;
    
    /**
     * Used for holding the language of the literal when the predicate is a datatype property.
     * This is the lang of the literal.  lang strings must be: [a-z]+(-[a-z0-9]+)*
     */
    private String rangeLang;

    /**
     * If this is a Select and it is of OptionsType LITERALS, these are the literals.
     */
    private List<List<String>> literalOptions;

    /**
     * Strings of N3 to add to model.
     */
    private List <String> assertions;

    /**
     * JSON configuration that was used to build this object.
     */ 
    private String originalJson;
    
    /**
     * Do not attempt to set the retractions when configuring a Field; they get built by the
     * edit processing object.
     *
     * The strings in this list should be N3 for statements that need to be retracted to affect an update.
     * Per Field retractions are necessary since we only want to retract for fields that have changed.
     * The Model should be checked to make sure that all of the retractions exist so we are changing the
     * statements that existed when this edit was configured.
     *
     * These retractions are just the assertions with the values subistituted in from before the change.
     */
    private List <String> retractions;

    private Map<String, String> queryForExisting;

    /**
     * Property for special edit element.
     */
    private EditElementVTwo editElement=null;;
        
    /* *********************** Constructors ************************** */
    
    public FieldVTwo() {}
        
    private static String[] parameterNames = {"editElement","newResource","validators","optionsType","predicateUri","objectClassUri","rangeDatatypeUri","rangeLang","literalOptions","assertions"};
    static{  Arrays.sort(parameterNames); }
    
    public FieldVTwo setEditElement(EditElementVTwo editElement){
        this.editElement = editElement;
        return this;
    }
       
    /* ****************** Getters and Setters ******************************* */

    public String getName(){
        return name;
    }
    
    public List<String> getRetractions() {
        return retractions;
    }

    public FieldVTwo setRetractions(List<String> retractions) {
        this.retractions = retractions;
        return this;
    }

    public List<String> getAssertions() {
        return assertions;
    }

    public FieldVTwo setAssertions(List<String> assertions) {
        this.assertions = assertions;
        return this;
    }

    public FieldVTwo setAssertions( String ... assertions ){        
        return setAssertions( Arrays.asList( assertions ));
    }
    
    public boolean isNewResource() {
        return newResource;
    }
    public FieldVTwo setNewResource(boolean b) {
        newResource = b;
        return this;
    }

    public List <String> getValidators() {
        return validators;
    }
    public FieldVTwo setValidators(List <String> v) {
        validators = v;
        return this;
    }

    public OptionsType getOptionsType() {
        return optionsType;
    }
    public FieldVTwo setOptionsType(OptionsType ot) {
        optionsType = ot;
        return this;
    }
    public FieldVTwo setOptionsType(String s) {
        setOptionsType( getOptionForString(s));
        return this;
    }

    public static OptionsType getOptionForString(String s){
        if( s== null || s.isEmpty() )
            return OptionsType.UNDEFINED;
        if ("LITERALS".equals(s)) {
            return FieldVTwo.OptionsType.LITERALS;
        } else if ("HARDCODED_LITERALS".equals(s)) {
            return FieldVTwo.OptionsType.HARDCODED_LITERALS;
        } else if ("STRINGS_VIA_DATATYPE_PROPERTY".equalsIgnoreCase(s)) {
            return FieldVTwo.OptionsType.STRINGS_VIA_DATATYPE_PROPERTY;
        } else if ("INDIVIDUALS_VIA_OBJECT_PROPERTY".equalsIgnoreCase(s)) {
            return FieldVTwo.OptionsType.INDIVIDUALS_VIA_OBJECT_PROPERTY;
        } else if ("INDIVIDUALS_VIA_VCLASS".equalsIgnoreCase(s)) {
            return FieldVTwo.OptionsType.INDIVIDUALS_VIA_VCLASS;
        } else if ("DATETIME".equalsIgnoreCase(s)) {
            return FieldVTwo.OptionsType.DATETIME;
        } else if ("CHILD_VCLASSES".equalsIgnoreCase(s)) {            
            return FieldVTwo.OptionsType.CHILD_VCLASSES;
        } else if ("CHILD_VCLASSES_WITH_PARENT".equalsIgnoreCase(s)) {            
            return FieldVTwo.OptionsType.CHILD_VCLASSES_WITH_PARENT;  
        } else if ("VCLASSGROUP".equalsIgnoreCase(s)) {            
            return FieldVTwo.OptionsType.VCLASSGROUP;              
        } else if ("FILE".equalsIgnoreCase(s)) {
            return FieldVTwo.OptionsType.FILE;            
        } else if ("DATE".equalsIgnoreCase(s)) {
            return FieldVTwo.OptionsType.DATE;
        } else if ("TIME".equalsIgnoreCase(s)) {
            return FieldVTwo.OptionsType.TIME;
        } else {
            return FieldVTwo.OptionsType.UNDEFINED;
        } 
    }
    
    public String getPredicateUri() {
        return predicateUri;
    }
    public FieldVTwo setPredicateUri(String s) {
        predicateUri = s;
        return this;
    }

    public String getObjectClassUri() {
        return objectClassUri;
    }
    public FieldVTwo setObjectClassUri(String s) {
        objectClassUri = s;
        return this;
    }
    
    public String getRangeDatatypeUri() {
        return rangeDatatypeUri;
    }
    public FieldVTwo setRangeDatatypeUri(String r) {
        if( rangeLang != null && rangeLang.trim().length() > 0 )
            throw new IllegalArgumentException("A Field object may not have both rangeDatatypeUri and rangeLanguage set");
        
        rangeDatatypeUri = r;
        return this;
    }

    public List <List<String>> getLiteralOptions() {
        return literalOptions;
    }
    public FieldVTwo setLiteralOptions(List<List<String>> literalOptions) {
        this.literalOptions = literalOptions;
        return this;
    }
     
    public String getRangeLang() {
        return rangeLang;
    }

    public FieldVTwo setRangeLang(String rangeLang) {
        if( rangeDatatypeUri != null && rangeDatatypeUri.trim().length() > 0)
            throw new IllegalArgumentException("A Field object may not have both rangeDatatypeUri and rangeLanguage set");
        
        this.rangeLang = rangeLang;
        return this;
    }

    public EditElementVTwo getEditElement(){
        return editElement;
    }
        
    public FieldVTwo setName(String name){
        this.name = name;    
        return this;
    }

}
