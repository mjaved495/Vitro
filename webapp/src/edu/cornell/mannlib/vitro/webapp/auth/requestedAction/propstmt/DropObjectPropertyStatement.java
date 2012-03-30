/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;

/**
 * Should we allow the user to delete this ObjectPropertyStatement from this
 * model?
 */
public class DropObjectPropertyStatement extends AbstractObjectPropertyStatementAction {
	public DropObjectPropertyStatement(String sub, String pred, String obj) {
		super(sub, pred, obj);
	}
	
	public DropObjectPropertyStatement(ObjectPropertyStatement ops) {
		super(ops);
	}
}
