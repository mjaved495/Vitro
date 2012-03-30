/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;

/**
 * Should we allow the user to add this DataPropertyStatement to this model?
 */
public class AddDataPropertyStatement extends
		AbstractDataPropertyStatementAction {

	public AddDataPropertyStatement(String subjectUri, String predicateUri) {
		super(subjectUri, predicateUri);
	}

	public AddDataPropertyStatement(DataPropertyStatement dps) {
		super(dps);
	}

}
