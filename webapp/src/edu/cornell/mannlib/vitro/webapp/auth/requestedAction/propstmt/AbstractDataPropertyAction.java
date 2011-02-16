/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.VisitingPolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestActionConstants;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;

/**
 * A base class for requestion actions that relate to data properties.
 */
public abstract class AbstractDataPropertyAction implements RequestedAction {
	private final String subjectUri;
	private final String predicateUri;

	public AbstractDataPropertyAction(String subjectUri, String predicateUri) {
		this.subjectUri = subjectUri;
		this.predicateUri = predicateUri;
	}

	public String getSubjectUri() {
		return subjectUri;
	}

	public String getPredicateUri() {
		return predicateUri;
	}

	@Override
	public String getURI() {
		return RequestActionConstants.actionNamespace
				+ this.getClass().getName();
	}

	@Override
	public abstract PolicyDecision accept(VisitingPolicyIface policy,
			IdentifierBundle whoToAuth);

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": <" + subjectUri + "> <"
				+ predicateUri + ">";
	}
}
