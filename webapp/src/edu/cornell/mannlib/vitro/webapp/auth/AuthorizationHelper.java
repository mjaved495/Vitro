/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RequestIdentifiers;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.RequestPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AuthorizationHelper {

    private static final Log log = LogFactory.getLog(AuthorizationHelper.class);
    
    private VitroRequest vreq;
    
    public AuthorizationHelper(VitroRequest vreq) {
        this.vreq = vreq;
    }

    public boolean isAuthorizedForRequestedAction(RequestedAction action) {
        PolicyIface policy = getPolicies();
        PolicyDecision dec = policy.isAuthorized(getIdentifiers(), action);
        if (dec != null && dec.getAuthorized() == Authorization.AUTHORIZED) {
            log.debug("Authorized because self-editing.");
            return true;
        } else {
            log.debug("Not Authorized even though self-editing: "
                    + ((dec == null) ? "null" : dec.getMessage() + ", "
                            + dec.getDebuggingInfo()));
            return false;
        }
    }

    /**
     * Get the policy from the request, or from the servlet context.
     */
    private PolicyIface getPolicies() {
        ServletContext servletContext = vreq.getSession().getServletContext();

        PolicyIface policy = RequestPolicyList.getPolicies(vreq);
        if (isEmptyPolicy(policy)) {
            policy = ServletPolicyList.getPolicies(servletContext);
            if (isEmptyPolicy(policy)) {
                log.error("No policy found in request at "
                        + RequestPolicyList.POLICY_LIST);
                policy = new PolicyList();
            }
        }

        return policy;
    }

    /**
     * Is there actually a policy here?
     */
    private boolean isEmptyPolicy(PolicyIface policy) {
        return policy == null
                || (policy instanceof PolicyList && ((PolicyList) policy)
                        .size() == 0);
    }

    private IdentifierBundle getIdentifiers() {
        return RequestIdentifiers.getIdBundleForRequest(vreq);
    }

}
