/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.reasoner.plugin;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.reasoner.ReasonerPlugin;
import edu.cornell.mannlib.vitro.webapp.reasoner.SimpleReasoner;

/**
 * handles rules of the form
 * assertedProp1(?x, ?y) ^ assertedProp2(?y, ?z) -> inferredProp(?x, ?z)
 *
 */
public abstract class SimpleBridgingRule implements ReasonerPlugin {
	
	private Property assertedProp1;
	private Property assertedProp2;
	private String   queryStr;
	private SimpleReasoner simpleReasoner;
	
	protected SimpleBridgingRule(String assertedProp1, String assertedProp2, String inferredProp) {
		this.assertedProp1 = ResourceFactory.createProperty(assertedProp1);
        this.assertedProp2 = ResourceFactory.createProperty(assertedProp2);
        
        this.queryStr = "CONSTRUCT { \n" +
                        "  ?x <" + inferredProp + "> ?z \n" +
                        "} WHERE { \n" +
                        "  ?x <" + assertedProp1 + "> ?y . \n" +
                        "  ?y <" + assertedProp2 + "> ?z \n" +
                        "}";
	}
	
	public boolean isInterestedInAddedStatement(Statement stmt) {
		return isRelevantPredicate(stmt);
	}
	
	public boolean isInterestedInRemovedStatement(Statement stmt) {
		return isRelevantPredicate(stmt);
	}
	
	public void addedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
		if (ignore(stmt)) {
			return;
		}
        Model inf = constructInferences(stmt, aboxAssertionsModel);
        StmtIterator sit = inf.listStatements();
        while(sit.hasNext()) {
        	Statement s = sit.nextStatement();
        	if (simpleReasoner != null) simpleReasoner.addInference(s,aboxInferencesModel);
        }     
	}
	
	private boolean ignore(Statement stmt) {
		return (
				(stmt.getSubject().isAnon() || stmt.getObject().isAnon())
			    // can't deal with blank nodes
		||
		        (!stmt.getObject().isResource()) 
			    // don't deal with literal values
	    );
	}

	private Model constructInferences(Statement stmt, Model aboxAssertionsModel) {
		String queryStr = new String(this.queryStr);
		if (stmt.getPredicate().equals(assertedProp1)) {
			queryStr = queryStr.replace(
					"?x", "<" + stmt.getSubject().getURI() + ">");
			queryStr = queryStr.replace(
					"?y", "<" + ((Resource) stmt.getObject()).getURI() + ">");
		} else if (stmt.getPredicate().equals(assertedProp2)) {
			queryStr = queryStr.replace(
					"?y", "<" + stmt.getSubject().getURI() + ">");
			queryStr = queryStr.replace(
					"?z", "<" + ((Resource) stmt.getObject()).getURI() + ">");			
		} else {
			// should never be here
			return ModelFactory.createDefaultModel();
		}
		Query query = QueryFactory.create(queryStr);
		QueryExecution qe = QueryExecutionFactory.create(query, aboxAssertionsModel);
		try {
			return qe.execConstruct();
		} finally {
			qe.close();
		}
		
	}
	
    public void removedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
		if (ignore(stmt)) {
			return;
		}
		Model m = ModelFactory.createDefaultModel();
		m.add(stmt);
		Model union = ModelFactory.createUnion(m, aboxAssertionsModel);
		
        Model inf = constructInferences(stmt, union);
        StmtIterator sit = inf.listStatements();
        while(sit.hasNext()) {
        	Statement s = sit.nextStatement();
        	if (simpleReasoner != null) simpleReasoner.removeInference(s,aboxInferencesModel);
        }     
    }
	
    private boolean isRelevantPredicate(Statement stmt) {
		return (assertedProp1.equals(stmt.getPredicate())
				|| assertedProp2.equals(stmt.getPredicate()));
    }
    
    public void setSimpleReasoner(SimpleReasoner simpleReasoner) {
    	this.simpleReasoner = simpleReasoner;
    }

    public SimpleReasoner getSimpleReasoner() {
    	return this.simpleReasoner;
    }
}

