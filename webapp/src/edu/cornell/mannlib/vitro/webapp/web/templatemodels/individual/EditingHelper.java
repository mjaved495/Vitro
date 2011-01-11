/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ServletIdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.RequestPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class EditingHelper {
    
    private static final Log log = LogFactory.getLog(EditingHelper.class);  
    
    private VitroRequest vreq;
    private ServletContext servletContext;
    private PolicyIface policy;
    private IdentifierBundle ids;
    
    protected EditingHelper(VitroRequest vreq, ServletContext servletContext) {
        this.vreq = vreq;
        this.servletContext = servletContext;
        setPolicy();
        setIds();
    }
    
    private void setPolicy() {
        policy = RequestPolicyList.getPolicies(vreq);
        if( policy == null || ( policy instanceof PolicyList && ((PolicyList)policy).size() == 0 )){
            policy = ServletPolicyList.getPolicies( servletContext );
            if( policy == null || ( policy instanceof PolicyList && ((PolicyList)policy).size() == 0 )){            
                log.error("No policy found in request at " + RequestPolicyList.POLICY_LIST);
            }
        }           
    }
    
    private void setIds() {
        ids = (IdentifierBundle)ServletIdentifierBundleFactory
            .getIdBundleForRequest(vreq, vreq.getSession(), servletContext);
        
        if (ids == null) {
            log.error("No IdentifierBundle objects for request");
        }
    }
    
    protected PolicyIface getPolicy() {
        return policy;
    }
    
    protected IdentifierBundle getIds() {
        return ids;
    }

}
