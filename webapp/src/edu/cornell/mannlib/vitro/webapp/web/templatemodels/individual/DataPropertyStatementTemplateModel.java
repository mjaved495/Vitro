/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.BaseTemplateModel;

public class DataPropertyStatementTemplateModel extends BaseTemplateModel {
    
    private static final Log log = LogFactory.getLog(DataPropertyStatementTemplateModel.class);  
    
    private DataPropertyStatement statement;
    private EditingPolicyHelper policyHelper;

    DataPropertyStatementTemplateModel(DataPropertyStatement statement, EditingPolicyHelper policyHelper) {
        this.statement = statement;
        this.policyHelper = policyHelper;
    }
    
    /* Access methods for templates */
    
    public String getValue() {
        return statement.getData();
    }
    
    public String getEditUrl() {
        String editUrl = "";
        return editUrl;
    }
    
    public String getDeleteUrl() {
        String deleteUrl = "";
        return deleteUrl;
    }

}
