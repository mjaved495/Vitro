/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.tboxreasoner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;

/**
 * Listens for changes on a model. When a change is announced, it is passed
 * along to the reasoner driver, if the configuration says that it is worthy.
 * 
 * It is possible to "suspend" the listener, so it will ignore any changes. This
 * is useful when the reasoner itself makes changes to the models, so those
 * changes do not trigger additional reasoning.
 */
public class ConfiguredReasonerListener implements ModelChangedListener {
	private static final Log log = LogFactory
			.getLog(ConfiguredReasonerListener.class);

	private final ReasonerConfiguration reasonerConfiguration;
	private final TBoxReasonerDriver reasonerDriver;
	private final DrivingPatternMap drivingPatternMap;

	private final AtomicBoolean suspended = new AtomicBoolean();

	public ConfiguredReasonerListener(
			ReasonerConfiguration reasonerConfiguration,
			TBoxReasonerDriver reasonerDriver) {
		this.reasonerConfiguration = reasonerConfiguration;
		this.reasonerDriver = reasonerDriver;

		this.drivingPatternMap = new DrivingPatternMap(
				reasonerConfiguration.getInferenceDrivingPatternAllowSet());
	}

	public Suspension suspend() {
		if (!suspended.compareAndSet(false, true)) {
			throw new IllegalStateException("Listener is already suspended.");
		}
		return new Suspension();
	}

	public class Suspension implements AutoCloseable {
		@Override
		public void close() {
			boolean wasSuspended = suspended.compareAndSet(true, false);
			if (!wasSuspended) {
				log.warn("Listener was already not suspended.");
			}
		}
	}

	/**
	 * Lists of patterns, mapped by their predicates.
	 */
	public class DrivingPatternMap extends
			HashMap<Property, List<ReasonerStatementPattern>> {

		public DrivingPatternMap(Set<ReasonerStatementPattern> patternSet) {
			if (patternSet != null) {
				for (ReasonerStatementPattern pat : patternSet) {
					Property p = pat.getPredicate();
					if (!containsKey(p)) {
						put(p, new LinkedList<ReasonerStatementPattern>());
					}
					get(p).add(pat);
				}
			}
		}
	}

	// ----------------------------------------------------------------------
	// Implement the ModelChangedListener methods. Delegate to the methods that
	// check criteria.
	// ----------------------------------------------------------------------

	@Override
	public void addedStatement(Statement s) {
		tryAdd(s);
	}

	@Override
	public void addedStatements(Statement[] statements) {
		for (Statement stmt : statements) {
			tryAdd(stmt);
		}
	}

	@Override
	public void addedStatements(List<Statement> statements) {
		for (Statement stmt : statements) {
			tryAdd(stmt);
		}
	}

	@Override
	public void addedStatements(StmtIterator statements) {
		for (Statement stmt : statements.toList()) {
			tryAdd(stmt);
		}
	}

	@Override
	public void addedStatements(Model m) {
		for (Statement stmt : m.listStatements().toList()) {
			tryAdd(stmt);
		}
	}

	@Override
	public void removedStatement(Statement s) {
		tryRemove(s);
	}

	@Override
	public void removedStatements(Statement[] statements) {
		for (Statement stmt : statements) {
			tryRemove(stmt);
		}
	}

	@Override
	public void removedStatements(List<Statement> statements) {
		for (Statement stmt : statements) {
			tryRemove(stmt);
		}
	}

	@Override
	public void removedStatements(StmtIterator statements) {
		for (Statement stmt : statements.toList()) {
			tryRemove(stmt);
		}
	}

	@Override
	public void removedStatements(Model m) {
		for (Statement stmt : m.listStatements().toList()) {
			tryRemove(stmt);
		}
	}

	@Override
	public void notifyEvent(Model m, Object event) {
		if (event instanceof EditEvent) {
			EditEvent ee = (EditEvent) event;
			if (!ee.getBegin()) {
				this.reasonerDriver.runSynchronizer();
			}
		}
	}

	// ----------------------------------------------------------------------
	// Check the criteria to determine whether each addition or removal should
	// be passed to the reasoner.
	//
	// When the listener is suspended, nothing is passed on.
	// ----------------------------------------------------------------------

	public void tryAdd(Statement stmt) {
		if (suspended.get()) {
			return;
		}

		if (isDataProperty(stmt)) {
			if (reasonOnAllDataProperties() || hasCardinalityPredicate(stmt)) {
				addIt(stmt);
				return;
			} else {
				return;
			}
		}

		if (predicateIsInVitroNamespace(stmt)
				|| statementMatchesDenyPattern(stmt)) {
			return;
		}

		if (thereAreNoDrivingPatterns() || statementMatchesDrivingPattern(stmt)) {
			addIt(stmt);
			return;
		}
	}

	public void tryRemove(Statement stmt) {
		if (suspended.get()) {
			return;
		}

		if (isDataProperty(stmt)) {
			if (reasonOnAllDataProperties() || hasCardinalityPredicate(stmt)) {
				removeIt(stmt);
				return;
			} else {
				return;
			}
		}

		if (actOnObjectPropertyDeclarations() && declaresObjectProperty(stmt)) {
			deleteObjectProperty(stmt);
			return;
		}

		if (actOnDataPropertyDeclarations() && declaresDataProperty(stmt)) {
			deleteDataProperty(stmt);
			return;
		}

		if (statementMatchesDenyPattern(stmt)) {
			return;
		}

		if (thereAreNoDrivingPatterns() || statementMatchesDrivingPattern(stmt)) {
			removeIt(stmt);
			return;
		}
	}

	private boolean isDataProperty(Statement stmt) {
		return stmt.getObject().isLiteral();
	}

	private boolean reasonOnAllDataProperties() {
		return reasonerConfiguration.getReasonOnAllDatatypePropertyStatements();
	}

	private boolean predicateIsInVitroNamespace(Statement stmt) {
		return stmt.getPredicate().getURI().indexOf(VitroVocabulary.vitroURI) == 0;
	}

	private boolean statementMatchesDenyPattern(Statement stmt) {
		Set<ReasonerStatementPattern> denyPatterns = reasonerConfiguration.inferenceDrivingPatternDenySet;
		if (denyPatterns == null) {
			return false;
		}

		ReasonerStatementPattern stPat = ReasonerStatementPattern
				.objectPattern(stmt);

		for (ReasonerStatementPattern pat : denyPatterns) {
			if (pat.matches(stPat)) {
				return true;
			}
		}

		return false;
	}

	private boolean thereAreNoDrivingPatterns() {
		return reasonerConfiguration.inferenceDrivingPatternAllowSet == null;
	}

	private boolean statementMatchesDrivingPattern(Statement stmt) {
		List<ReasonerStatementPattern> drivePatterns = drivingPatternMap
				.get(stmt.getPredicate());
		if (drivePatterns == null) {
			return false;
		}

		ReasonerStatementPattern stPat = ReasonerStatementPattern
				.objectPattern(stmt);

		for (ReasonerStatementPattern pat : drivePatterns) {
			if (pat.matches(stPat)) {
				return true;
			}
		}

		return false;
	}

	private boolean actOnObjectPropertyDeclarations() {
		return reasonerConfiguration.getQueryForAllObjectProperties();
	}

	private boolean declaresObjectProperty(Statement stmt) {
		return stmt.getPredicate().equals(RDF.type)
				&& stmt.getObject().equals(OWL.ObjectProperty);
	}

	private boolean actOnDataPropertyDeclarations() {
		return reasonerConfiguration.getQueryForAllDatatypeProperties();
	}

	private boolean declaresDataProperty(Statement stmt) {
		return stmt.getPredicate().equals(RDF.type)
				&& stmt.getObject().equals(OWL.DatatypeProperty);
	}

	private void addIt(Statement stmt) {
		this.reasonerDriver.addStatement(stmt);
	}

	private void removeIt(Statement stmt) {
		this.reasonerDriver.removeStatement(stmt);
	}

	private void deleteObjectProperty(Statement stmt) {
		this.reasonerDriver.deleteObjectProperty(stmt);
	}

	private void deleteDataProperty(Statement stmt) {
		this.reasonerDriver.deleteDataProperty(stmt);
	}

	// The pattern matching stuff needs to get reworked.
	// It originally assumed that only resources would be in object
	// position, but cardinality axioms will have e.g. nonNegativeIntegers.
	// This is a temporary workaround: all cardinality statements will
	// be exposed to Pellet, regardless of configuration patterns.
	private boolean hasCardinalityPredicate(Statement stmt) {
		return (stmt.getPredicate().equals(OWL.cardinality)
				|| stmt.getPredicate().equals(OWL.minCardinality) || stmt
				.getPredicate().equals(OWL.maxCardinality));
	}

}
