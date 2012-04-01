/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;

/**
 * A base class for requested actions that involve adding, editing, or dropping
 * data property statements from a model.
 */
public abstract class AbstractDataPropertyStatementAction extends
		AbstractPropertyStatementAction {
	private final String subjectUri;
	private final String predicateUri;

	public AbstractDataPropertyStatementAction(OntModel ontModel,
			String subjectUri, String predicateUri) {
		super(ontModel);
		this.subjectUri = subjectUri;
		this.predicateUri = predicateUri;
	}

	public AbstractDataPropertyStatementAction(OntModel ontModel,
			DataPropertyStatement dps) {
		super(ontModel);
		this.subjectUri = (dps.getIndividual() == null) ? dps
				.getIndividualURI() : dps.getIndividual().getURI();
		this.predicateUri = dps.getDatapropURI();
	}

	public String getSubjectUri() {
		return subjectUri;
	}

	public String getPredicateUri() {
		return predicateUri;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": <" + subjectUri + "> <"
				+ predicateUri + ">";
	}
}
