/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.BaseTemplateModel;

/** 
 * Represents the property statement list for a single property of an individual.
 */
public abstract class PropertyTemplateModel extends BaseTemplateModel {
    
    private String name;
    protected Property property;
    
    PropertyTemplateModel(Property property) {
        this.name = property.getLabel();
        this.property = property;
    }
    
    /* Access methods for templates */
    
    public String getAddLink() {
        return null;
    }
    
    public abstract String getType();
    
    public String getName() {
        return name;
    }
    
//    protected String getUri() {
//        return property.getURI();
//    }
    
    public abstract String addLink();
    
    public abstract String editLink();
    
    public abstract String deleteLink();
 
}
