/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.policy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasRoleLevel;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.querymodel.QueryFullModel;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;

/**
 * Check the users role level to determine whether they are allowed to use
 * restricted pages.
 */
public class UseRestrictedPagesByRoleLevelPolicy implements PolicyIface {
	private static final Log log = LogFactory
			.getLog(UseRestrictedPagesByRoleLevelPolicy.class);

	@Override
	public PolicyDecision isAuthorized(IdentifierBundle whoToAuth,
			RequestedAction whatToAuth) {
		if (whoToAuth == null) {
			return defaultDecision("whomToAuth was null");
		}
		if (whatToAuth == null) {
			return defaultDecision("whatToAuth was null");
		}

		RoleLevel userRole = HasRoleLevel.getUsersRoleLevel(whoToAuth);

		PolicyDecision result;
		if (whatToAuth instanceof QueryFullModel) {
			result = isAuthorized(whatToAuth, RoleLevel.PUBLIC, userRole);

		} else {
			result = defaultDecision("Unrecognized action");
		}

		log.debug("decision for '" + whatToAuth + "' is " + result);
		return result;
	}

	/** Authorize if user's role is at least as high as the required role. */
	private PolicyDecision isAuthorized(RequestedAction whatToAuth,
			RoleLevel requiredRole, RoleLevel currentRole) {
		if (isRoleAtLeast(requiredRole, currentRole)) {
			return authorized("User may view page: " + whatToAuth
					+ ", requiredRole=" + requiredRole + ", currentRole="
					+ currentRole);
		} else {
			return defaultDecision("User may not view page: " + whatToAuth
					+ ", requiredRole=" + requiredRole + ", currentRole="
					+ currentRole);
		}
	}

	private boolean isRoleAtLeast(RoleLevel required, RoleLevel current) {
		return (current.compareTo(required) >= 0);
	}

	/** If the user is explicitly authorized, return this. */
	private PolicyDecision authorized(String message) {
		String className = this.getClass().getSimpleName();
		return new BasicPolicyDecision(Authorization.AUTHORIZED, className
				+ ": " + message);
	}

	/** If the user isn't explicitly authorized, return this. */
	private PolicyDecision defaultDecision(String message) {
		return new BasicPolicyDecision(Authorization.INCONCLUSIVE, message);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + hashCode();
	}
}
