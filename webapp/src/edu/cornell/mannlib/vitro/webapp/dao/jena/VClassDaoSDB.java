/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

public class VClassDaoSDB extends VClassDaoJena {

	private DatasetWrapperFactory dwf;
	
    public VClassDaoSDB(DatasetWrapperFactory datasetWrapperFactory, 
                WebappDaoFactoryJena wadf) {
        super(wadf);
        this.dwf = datasetWrapperFactory;
    }
    
    protected DatasetWrapper getDatasetWrapper() {
    	return dwf.getDatasetWrapper();
    }
    
    @Deprecated
    public void addVClassesToGroup(VClassGroup group, boolean includeUninstantiatedClasses, boolean getIndividualCount) {
        getOntModel().enterCriticalSection(Lock.READ);
        try {
            if ((group != null) && (group.getURI() != null)) {
                Resource groupRes = ResourceFactory.createResource(group.getURI());
                AnnotationProperty inClassGroup = getOntModel().getAnnotationProperty(VitroVocabulary.IN_CLASSGROUP);
                if (inClassGroup != null) {
                    ClosableIterator annotIt = getOntModel().listStatements((OntClass)null,inClassGroup,groupRes);
                    try {
                        while (annotIt.hasNext()) {
                            try {
                                Statement annot = (Statement) annotIt.next();
                                Resource cls = (Resource) annot.getSubject();
                                VClass vcw = (VClass) getVClassByURI(cls.getURI());
                                if (vcw != null) {
                                    boolean classIsInstantiated = false;
                                    if (getIndividualCount) {
                                    	Model aboxModel = getOntModelSelector().getABoxModel();
                                    	aboxModel.enterCriticalSection(Lock.READ);
                                    	int count = 0;
                                    	try {
                                    		String countQueryStr = "SELECT COUNT(*) WHERE \n" +
                                    		                       "{ GRAPH ?g { ?s a <" + cls.getURI() + "> } } \n";
                                    		Query countQuery = QueryFactory.create(countQueryStr, Syntax.syntaxARQ);
                                    		DatasetWrapper w = getDatasetWrapper();
                                    		Dataset dataset = w.getDataset();
                                    		dataset.getLock().enterCriticalSection(Lock.READ);
                                    		try {
                                        		QueryExecution qe = QueryExecutionFactory.create(countQuery, dataset);
                                        		ResultSet rs = qe.execSelect();
                                        		count = Integer.parseInt(((Literal) rs.nextSolution().get(".1")).getLexicalForm());
                                    		} finally {
                                    		    dataset.getLock().leaveCriticalSection();
                                    		    w.close();
                                    		}
                                    	} finally {
                                    		aboxModel.leaveCriticalSection();
                                    	}
                                    	vcw.setEntityCount(count);
                                    	classIsInstantiated = (count > 0);
                                    } else if (includeUninstantiatedClasses == false) {
                                        // Note: to support SDB models, may want to do this with 
                                        // SPARQL and LIMIT 1 if SDB can take advantage of it
                                    	Model aboxModel = getOntModelSelector().getABoxModel();
                                    	aboxModel.enterCriticalSection(Lock.READ);
                                    	try {
	                                        ClosableIterator countIt = aboxModel.listStatements(null,RDF.type,cls);
	                                        try {
	                                            if (countIt.hasNext()) {
	                                            	classIsInstantiated = true;
	                                            }
	                                        } finally {
	                                            countIt.close();
	                                        }
                                    	} finally {
                                    		aboxModel.leaveCriticalSection();
                                    	}
                                    }
                                    
                                    if (includeUninstantiatedClasses || classIsInstantiated) {
                                        group.add(vcw);
                                    }
                                }
                            } catch (ClassCastException cce) {cce.printStackTrace();}
                        }
                    } finally {
                        annotIt.close();
                    }
                }
            }
            java.util.Collections.sort(group.getVitroClassList());
        } finally {
            getOntModel().leaveCriticalSection();
        }
    }
    
}
