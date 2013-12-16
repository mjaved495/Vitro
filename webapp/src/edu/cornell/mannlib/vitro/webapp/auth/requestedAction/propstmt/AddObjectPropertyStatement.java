/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Property;

/**
 * Should we allow the user to add this ObjectPropertyStatement to this model?
 */
public class AddObjectPropertyStatement extends
		AbstractObjectPropertyStatementAction {
	public AddObjectPropertyStatement(OntModel ontModel, String uriOfSub,
			Property predicate, String uriOfObj) {
		super(ontModel, uriOfSub, predicate, uriOfObj);
	}

	public AddObjectPropertyStatement(OntModel ontModel,
			ObjectPropertyStatement ops) {
		super(ontModel, ops);
	}
}
