/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;

/**
 * Should we allow the user to add this ObjectPropertyStatement to this model?
 */
public class AddObjectPropertyStatement extends
		AbstractObjectPropertyStatementAction {
	public AddObjectPropertyStatement(String uriOfSub, String uriOfPred,
			String uriOfObj) {
		super(uriOfSub, uriOfPred, uriOfObj);
	}

	public AddObjectPropertyStatement(ObjectPropertyStatement ops) {
		super(ops);
	}
}
