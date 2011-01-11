/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.EditObjPropStmt;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.BaseTemplateModel;

public class ObjectPropertyStatementTemplateModel extends BaseTemplateModel {
    
    private static final Log log = LogFactory.getLog(ObjectPropertyStatementTemplateModel.class); 
    
    private static final String EDIT_PATH = "edit/editRequestDispatch.jsp";
    
    private String subjectUri; // we'll use these to make the edit links
    private String propertyUri;
    private Map<String, String> data;
    
    private EditingHelper editLinkHelper;
    private String objectUri = null;
    private ObjectPropertyStatement objectPropertyStatement = null;


    ObjectPropertyStatementTemplateModel(String subjectUri, String propertyUri, 
            String objectKey, Map<String, String> data, EditingHelper editLinkHelper) {
        this.subjectUri = subjectUri;
        this.propertyUri = propertyUri;
        this.data = data;
        this.editLinkHelper = editLinkHelper;
        
        // If the editLinkHelper is non-null, we are in edit mode, so create the necessary objects.
        if (this.editLinkHelper != null) {
            objectUri = data.get(objectKey);
            objectPropertyStatement = new ObjectPropertyStatementImpl(subjectUri, propertyUri, objectUri);
        }

    }

    
    /* Access methods for templates */

    public Object get(String key) {
        return data.get(key);
    }
    
    public String getEditUrl() {
        String editUrl = "";
        RequestedAction action = new EditObjPropStmt(objectPropertyStatement);
        PolicyDecision decision = editLinkHelper.getPolicy().isAuthorized(editLinkHelper.getIds(), action);
        if (decision != null && decision.getAuthorized() == Authorization.AUTHORIZED) {
            ParamMap params = new ParamMap(
                    "subjectUri", subjectUri,
                    "predicateUri", propertyUri,
                    "objectUri", objectUri);
            editUrl = UrlBuilder.getUrl(EDIT_PATH, params);
        }
        
        return editUrl;
    }
    
    public String getDeleteUrl() {
        String deleteUrl = "";
        return deleteUrl;
    }
}
