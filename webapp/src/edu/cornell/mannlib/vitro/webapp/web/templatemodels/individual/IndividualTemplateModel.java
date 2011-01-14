/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Link;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.BaseTemplateModel;

public class IndividualTemplateModel extends BaseTemplateModel {
    
    private static final Log log = LogFactory.getLog(IndividualTemplateModel.class);
    
    private static final String PATH = Route.INDIVIDUAL.path();
    
    protected Individual individual;
    protected VitroRequest vreq;
    protected UrlBuilder urlBuilder;
    protected GroupedPropertyList propertyList = null;
    protected LoginStatusBean loginStatusBean = null;
    private EditingPolicyHelper policyHelper = null;

    public IndividualTemplateModel(Individual individual, VitroRequest vreq, LoginStatusBean loginStatusBean) {
        this.individual = individual;
        this.vreq = vreq;
        this.loginStatusBean = loginStatusBean;
        // Needed for getting portal-sensitive urls. Remove if multi-portal support is removed.
        this.urlBuilder = new UrlBuilder(vreq.getPortal());
        
        // If editing, create a helper object to check requested actions against policies
        if (isEditable(loginStatusBean)) {
            policyHelper = new EditingPolicyHelper(vreq, getServletContext());
        } 
    }

    /** 
     * Return true iff the user is editing. 
     * These tests may change once self-editing issues are straightened out. What we really need to know
     * is whether the user can edit this profile, not whether in general he/she is an editor.
     */
    private boolean isEditable(LoginStatusBean loginStatusBean) { 
        boolean isSelfEditing = VitroRequestPrep.isSelfEditing(vreq);
        boolean isCurator = loginStatusBean.isLoggedInAtLeast(LoginStatusBean.CURATOR);
        return isSelfEditing || isCurator;
    }
    
    
    /* These methods perform some manipulation of the data returned by the Individual methods */
    
    public String getProfileUrl() {
        return UrlBuilder.getIndividualProfileUrl(individual, vreq.getWebappDaoFactory());
    }
    
    public String getVisualizationUrl() {
        return isPerson() ? getUrl(Route.VISUALIZATION_AJAX.path(), "uri", getUri()) : null;
    }

    // This remains as a convenience method for getting the image url. We could instead use a custom list 
    // view for mainImage which would provide this data in the query results.
    public String getImageUrl() {
        String imageUrl = individual.getImageUrl();
        return imageUrl == null ? null : getUrl(imageUrl);
    }

    // This remains as a convenience method for getting the thumbnail url. We could instead use a custom list 
    // view for mainImage which would provide this data in the query results.
    public String getThumbUrl() {
        String thumbUrl = individual.getThumbUrl();
        return thumbUrl == null ? null : getUrl(thumbUrl);
    } 
    
    public String getLinkedDataUrl() {
        String defaultNamespace = vreq.getWebappDaoFactory().getDefaultNamespace();
        String uri = getUri();
        return uri.startsWith(defaultNamespace) ? uri + "/" + getLocalName() + ".rdf" : null;
    }
    
    // RY Used for the rdf link on the individual page. Is it correct that this is not the same
    // as getLinkedDataUrl()?
    public String getRdfUrl() {
        return getProfileUrl() + "/" + getLocalName() + ".rdf";
    }
    
    public String getEditUrl() {
        return urlBuilder.getPortalUrl(Route.INDIVIDUAL_EDIT, "uri", getUri());
    }

    // RY We should not have references to a specific ontology in the vitro code!
    // Figure out how to move this out of here.
    // We could subclass IndividualTemplateModel in the VIVO code and add the isPerson()
    // and getVisualizationUrl() methods there, but we still need to know whether to
    // instantiate the IndividualTemplateModel or the VivoIndividualTemplateModel class.
    public boolean isPerson() {
        return individual.isVClass("http://xmlns.com/foaf/0.1/Person");        
    }
    
    public boolean isOrganization() {
        return individual.isVClass("http://xmlns.com/foaf/0.1/Organization");        
    }
    
    public GroupedPropertyList getPropertyList() {
        if (propertyList == null) {
            propertyList = new GroupedPropertyList(individual, vreq, policyHelper);
        }
        return propertyList;
    }
    
    public boolean getShowEditingLinks() {
        // RY This will be improved later. What is important is not whether the user is a self-editor,
        // but whether he has editing privileges on this profile.
        return VitroRequestPrep.isSelfEditing(vreq) || 
            loginStatusBean.isLoggedInAtLeast(LoginStatusBean.NON_EDITOR);
    }
    
    public boolean getShowAdminPanel() {
        return loginStatusBean.isLoggedInAtLeast(LoginStatusBean.EDITOR);
    }
 
    public DataPropertyStatementTemplateModel getNameStatement() {
        String propertyUri = VitroVocabulary.LABEL; // rdfs:label
        DataPropertyStatementTemplateModel dpstm = new DataPropertyStatementTemplateModel(getUri(), propertyUri, vreq, policyHelper);
        
        // If the individual has no rdfs:label, return the local name. It will not be editable (this replicates previous behavior;
        // perhaps we would want to allow a label to be added. But such individuals do not usually have their profiles viewed or
        // edited directly.
        if (dpstm.getValue() == null) {
            dpstm.setValue(getLocalName());
        }
        
        return dpstm;
    }
    
    /* These methods simply forward to the methods of the wrapped individual. It would be desirable to 
     * implement a scheme for proxying or delegation so that the methods don't need to be simply listed here. 
     * A Ruby-style method missing method would be ideal. 
     * Update: DynamicProxy doesn't work because the proxied object is of type Individual, so we cannot
     * declare new methods here that are not declared in the Individual interface. 
     */
    
    public String getName() {           
        return individual.getName();
    }

    public String getMoniker() {
        return individual.getMoniker();
    }

    public String getUri() {
        return individual.getURI();
    }
    
    public List<String> getKeywords() {
        return individual.getKeywords();
    }
    
    public String getKeywordString() {
        // Since this is a display method, the implementation should be moved out of IndividualImpl to here.
        return individual.getKeywordString();
    }
    
    public String getLocalName() {
        return individual.getLocalName();
    }
    
    @Deprecated
    public String getDescription() {
        return individual.getDescription();
    }
    
    @Deprecated
    public String getBlurb() {
        return individual.getBlurb();
    }   
    
}
