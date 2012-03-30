/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;

/**
 * Should we allow the user to edit this DataPropertyStatement in this model?
 */
public class EditDataPropertyStatement extends
		AbstractDataPropertyStatementAction {
	public EditDataPropertyStatement(String subjectUri, String predicateUri) {
		super(subjectUri, predicateUri);
	}

	public EditDataPropertyStatement(DataPropertyStatement dps) {
		super(dps);
	}
}
