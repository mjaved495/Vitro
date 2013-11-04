/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sdb.util.Pair;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyInstance;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;

public class PropertyDaoJena extends JenaBaseDao implements PropertyDao {
	
	protected static final Log log = LogFactory.getLog(PropertyDaoJena.class.getName());
	protected static final String FAUX_PROPERTY_FLAG = "FAUX";
	
    private static final Map<String, String> NAMESPACES = new HashMap<String, String>() {{
        put("afn", VitroVocabulary.AFN);
        put("owl", VitroVocabulary.OWL);
        put("rdf", VitroVocabulary.RDF);
        put("rdfs", VitroVocabulary.RDFS);
        put("vitro", VitroVocabulary.vitroURI);
        put("vitroPublic", VitroVocabulary.VITRO_PUBLIC);
    }};
    
    protected static final String PREFIXES;
    static {
        String prefixes = "";
        for (String key : NAMESPACES.keySet()) {
            prefixes += "PREFIX " + key + ": <" + NAMESPACES.get(key) + ">\n";
        }
        PREFIXES = prefixes;
        log.debug("Query prefixes: " + PREFIXES);
    }
    
    protected RDFService rdfService;
    protected DatasetWrapperFactory dwf;
    
    public PropertyDaoJena(RDFService rdfService,
                           DatasetWrapperFactory dwf, 
                           WebappDaoFactoryJena wadf) {
        super(wadf);
        this.rdfService = rdfService;
        this.dwf = dwf;
    }
    
    @Override
    protected OntModel getOntModel() {
    	return getOntModelSelector().getTBoxModel();
    }
    
    protected RDFService getRDFService() {
        return this.rdfService;
    }

	public void addSuperproperty(ObjectProperty property, ObjectProperty superproperty) {
    	addSuperproperty(property.getURI(),superproperty.getURI());
    }
    
    public void addSuperproperty(String propertyURI, String superpropertyURI) {
		getOntModel().enterCriticalSection(Lock.WRITE);
		try {
			getOntModel().add(getOntModel().getResource(propertyURI),RDFS.subPropertyOf,getOntModel().getResource(superpropertyURI));
		} finally {
			getOntModel().leaveCriticalSection();
		}
	}
    
    public void removeSuperproperty(ObjectProperty property, ObjectProperty superproperty) {
    	removeSuperproperty(property.getURI(),superproperty.getURI());
    }
    
    public void removeSuperproperty(String propertyURI, String superpropertyURI) {
    	getOntModel().enterCriticalSection(Lock.WRITE);
    	try {
    		if (getOntModel().contains(getOntModel().getResource(propertyURI),RDFS.subPropertyOf,getOntModel().getResource(superpropertyURI))) {
    			getOntModel().remove(getOntModel().getResource(propertyURI),RDFS.subPropertyOf,getOntModel().getResource(superpropertyURI));
    		}
    	} finally {
    		getOntModel().leaveCriticalSection();
    	}
    }
    
    public void addSubproperty(ObjectProperty property, ObjectProperty subproperty) {
    	addSuperproperty(subproperty, property);
    }
    
    public void addSubproperty(String propertyURI, String subpropertyURI) {
    	addSuperproperty(subpropertyURI, propertyURI);
    }
    
    public void removeSubproperty(ObjectProperty property, ObjectProperty subproperty) {
    	removeSuperproperty(subproperty, property);
    }
    
    public void removeSubproperty(String propertyURI, String subpropertyURI) {
    	removeSuperproperty(subpropertyURI,propertyURI);
    }
    
    public List <String> getSubPropertyURIs(String propertyURI) {
    	List<String> subURIs = new LinkedList<String>();
    	getOntModel().enterCriticalSection(Lock.READ);
    	try {
            Iterator subIt = getOntModel().getOntProperty(propertyURI).listSubProperties(true);
            while (subIt.hasNext()) {
                try {
                    OntProperty prop = (OntProperty) subIt.next();
                    subURIs.add(prop.getURI());
                } catch (Exception cce) {}
            }
	    } catch (Exception e) {
	    	log.error(e, e); 
    	} finally {
    		getOntModel().leaveCriticalSection();
    	}
        return subURIs;
    }

    private void getAllSubPropertyURIs(String propertyURI, HashSet<String> subtree){
        List<String> directSubproperties = getSubPropertyURIs(propertyURI);     
        Iterator<String> it=directSubproperties.iterator();
        while(it.hasNext()){
            String uri = it.next();
            if (!subtree.contains(uri)) {
            	subtree.add(uri);
            	getAllSubPropertyURIs(uri,subtree);
            }
        }
    }

    public List<String> getAllSubPropertyURIs(String propertyURI) {
    	HashSet<String> nodeSet = new HashSet<String>();
    	nodeSet.add(propertyURI);
    	getAllSubPropertyURIs(propertyURI, nodeSet);
    	nodeSet.remove(propertyURI);
    	List<String> outputList = new LinkedList<String>();
    	outputList.addAll(nodeSet);
    	return outputList;
    }
    
    public List <String> getSuperPropertyURIs(String propertyURI, boolean direct) {
       	List<String> supURIs = new LinkedList<String>();
    	getOntModel().enterCriticalSection(Lock.READ);
    	try {
            Iterator supIt = getOntModel().getOntProperty(propertyURI).listSuperProperties(direct);
            while (supIt.hasNext()) {
                try {
                    OntProperty prop = (OntProperty) supIt.next();
                    supURIs.add(prop.getURI());
                } catch (Exception cce) {}
            }
	    } catch (Exception e) {
	    	log.error(e, e); 
    	} finally {
    		getOntModel().leaveCriticalSection();
    	}
        return supURIs;
    }

    private void getAllSuperPropertyURIs(String propertyURI, HashSet<String> subtree){
        List<String> directSuperproperties = getSuperPropertyURIs(propertyURI,true);     
        Iterator<String> it=directSuperproperties.iterator();
        while(it.hasNext()){
            String uri = it.next();
            if (!subtree.contains(uri)) {
            	subtree.add(uri);
            	getAllSuperPropertyURIs(uri,subtree);
            }
        }
    }

    public List<String> getAllSuperPropertyURIs(String propertyURI) {
    	HashSet<String> nodeSet = new HashSet<String>();
    	nodeSet.add(propertyURI);
    	getAllSuperPropertyURIs(propertyURI, nodeSet);
    	nodeSet.remove(propertyURI);
    	List<String> outputList = new LinkedList<String>();
    	outputList.addAll(nodeSet);
    	return outputList;
    }

	public void addSubproperty(Property property, Property subproperty) {
		addSubproperty(property.getURI(), subproperty.getURI());
	}

	public void addSuperproperty(Property property, Property superproperty) {
		addSuperproperty(property.getURI(), superproperty.getURI());
	}

	public void removeSubproperty(Property property, Property subproperty) {
		removeSubproperty(property.getURI(), subproperty.getURI());	
	}

	public void removeSuperproperty(Property property, Property superproperty) {
		removeSuperproperty(property.getURI(), superproperty.getURI());
	}

	public void addEquivalentProperty(String propertyURI,
			String equivalentPropertyURI) {
		if (propertyURI == null || equivalentPropertyURI == null) {
			throw new RuntimeException("cannot assert equivalence of anonymous properties");
		}
		OntModel ontModel = getOntModel();	
		ontModel.enterCriticalSection(Lock.WRITE);
		try {
			Resource property = ontModel.getResource(propertyURI);
			Resource equivalentProperty = ontModel.getResource(equivalentPropertyURI);
			ontModel.add(property, OWL.equivalentProperty, equivalentProperty);
			ontModel.add(equivalentProperty, OWL.equivalentProperty, property);
		} finally {
			ontModel.leaveCriticalSection();
		}
	}

	public void addEquivalentProperty(Property property,
			Property equivalentProperty) {
		addEquivalentProperty(property.getURI(), equivalentProperty.getURI());
	}

	public List<String> getEquivalentPropertyURIs(String propertyURI) {
       	List<String> equivURIs = new LinkedList<String>();
    	getOntModel().enterCriticalSection(Lock.READ);
    	try {
            StmtIterator eqStmtIt = getOntModel().listStatements(getOntModel().getResource(propertyURI), OWL.equivalentProperty, (RDFNode) null);
            while (eqStmtIt.hasNext()) {
                Statement eqStmt = eqStmtIt.nextStatement();
                RDFNode prop = eqStmt.getObject();
                if (prop.isResource() && ((Resource) prop).getURI() != null) {
                	equivURIs.add(((Resource) prop).getURI());
                }
            }
	    } catch (Exception e) {
	    	log.error(e, e); 
    	} finally {
    		getOntModel().leaveCriticalSection();
    	}
        return equivURIs;
	}

	public void removeEquivalentProperty(String propertyURI, String equivalentPropertyURI) {
		if (propertyURI == null || equivalentPropertyURI == null) {
			throw new RuntimeException("cannot remove equivalence axiom about anonymous properties");
		}
		OntModel ontModel = getOntModel();	
		ontModel.enterCriticalSection(Lock.WRITE);
		try {
			Resource property = ontModel.getResource(propertyURI);
			Resource equivalentProperty = ontModel.getResource(equivalentPropertyURI);
			ontModel.remove(property, OWL.equivalentProperty, equivalentProperty);
			ontModel.remove(equivalentProperty, OWL.equivalentProperty, property);
		} finally {
			ontModel.leaveCriticalSection();
		}
	}

	public void removeEquivalentProperty(Property property,
			Property equivalentProperty) {
		removeEquivalentProperty(property, equivalentProperty);
	}
	
	protected void removeABoxStatementsWithPredicate(Property predicate) {
		// DO NOT issue a removeAll() with a null (wildcard) in predicate position!
		if (predicate == null) {
			log.debug("Cannot remove ABox statements with a null predicate.");
			return;
		} else {
			removeABoxStatementsWithPredicate(predicate.getURI());
		}
	}
	
	protected void removeABoxStatementsWithPredicate(String predicateURI) {
		if (predicateURI == null) {
			log.debug("Cannot remove ABox statements with null predicate URI.");
			return;
		}
        OntModel aboxModel = getOntModelSelector().getABoxModel();
        aboxModel.enterCriticalSection(Lock.WRITE);
        try {
        	aboxModel.getBaseModel().notifyEvent(new EditEvent(getWebappDaoFactory().getUserURI(),true));
        	aboxModel.removeAll((Resource) null, aboxModel.getProperty(predicateURI), (RDFNode) null);
        } finally {
        	aboxModel.getBaseModel().notifyEvent(new EditEvent(getWebappDaoFactory().getUserURI(),false));
        	aboxModel.leaveCriticalSection();
        }
    		
	}
	
	/**
	 * Finds the classes that have a definition involving a restriction
	 * on the given property. 
	 *
	 * @param   propertyURI  identifier of a property
	 * @return  a list of VClass objects representing the classes that have
	 *          definitions involving a restriction on the given property.
	 */

    public List <VClass> getClassesWithRestrictionOnProperty(String propertyURI) {
    	
    	if (propertyURI == null) {
    		log.warn("getClassesWithRestrictionOnProperty: called with null propertyURI");
    		return null;
    	}
    	    	
		OntModel ontModel = getOntModel();	
		ontModel.enterCriticalSection(Lock.READ);
		
		HashSet<String> classURISet = new HashSet<String>();
		
		try {
			Resource targetProp = ontModel.getResource(propertyURI);
			   
			if (targetProp != null) {
			
			    StmtIterator stmtIter = ontModel.listStatements((Resource) null, OWL.onProperty, targetProp);
	
			    while (stmtIter.hasNext()) {
				   Statement statement = stmtIter.next();
				   
				   if ( statement.getSubject().canAs(OntClass.class) ) {
					   classURISet.addAll(getRestrictedClasses(statement.getSubject().as(OntClass.class)));
				   } else {
					   log.warn("getClassesWithRestrictionOnProperty: Unexpected use of onProperty: it is not applied to a class");
				   }
			    }
			} else {
	    		log.error("getClassesWithRestrictionOnProperty: Error: didn't find a Property in the ontology model for the URI: " +  propertyURI);				
			}
		} finally {
			ontModel.leaveCriticalSection();
		}

		List<VClass> classes = new ArrayList<VClass>();
		Iterator<String> iter = classURISet.iterator();
		
		VClassDao vcd = getWebappDaoFactory().getVClassDao();
		
		while (iter.hasNext()) {
		
		   String curi = iter.next();
		   VClass vc = vcd.getVClassByURI(curi);
		  
		   if (vc != null) {
		       classes.add(vc);	  
		   } else {
			   log.error("getClassesWithRestrictionOnProperty: Error: no VClass found for URI: " + curi);
		   }	
		}
       
        return (classes.size()>0) ? classes : null;
    }

	/**
	 * Find named classes to which a restriction "applies"
	 * @param   resourceURI  identifier of a class
	 * @return  set of class URIs
	 * 
	 * Note: this method assumes that the caller holds a read lock on
	 * the ontology model.
	 */

    public HashSet<String> getRestrictedClasses(OntClass ontClass) {
    	
        HashSet<String> classSet = new HashSet<String>();
  
        List<OntClass> classList = ontClass.listEquivalentClasses().toList();
        classList.addAll(ontClass.listSubClasses().toList());
        
        Iterator<OntClass> it = classList.iterator();
		         
        while (it.hasNext()) {
        	OntClass oc = it.next();
        	
        	if (!oc.isAnon()) {
        		classSet.add(oc.getURI());
        	} else {
        	    classSet.addAll(getRestrictedClasses(oc));
        	}
        }
        		
        return classSet;
    }
     
    protected ResultSet getPropertyQueryResults(String queryString) {        
        log.debug("SPARQL query:\n" + queryString);
        
        // RY Removing prebinding due to Jena bug: when isLiteral(?object) or 
        // isURI(?object) is added to the query as a filter, the query fails with prebinding
        // but succeeds when the subject uri is concatenated into the query string.
        //QuerySolutionMap subjectBinding = new QuerySolutionMap();
        //subjectBinding.add("subject", ResourceFactory.createResource(subjectUri));
                
        // Run the SPARQL query to get the properties
        
        try {
            return ResultSetFactory.fromJSON(
                    getRDFService().sparqlSelectQuery(
                            queryString, RDFService.ResultFormat.JSON));
        } catch (RDFServiceException e) {
            throw new RuntimeException(e);
        }
        
//        DatasetWrapper w = dwf.getDatasetWrapper();
//        Dataset dataset = w.getDataset();
//        dataset.getLock().enterCriticalSection(Lock.READ);
//        ResultSet rs = null;
//        try {
//            QueryExecution qexec = QueryExecutionFactory.create(
//                    query, dataset); //, subjectBinding);
//            try {
//                rs = new ResultSetMem(qexec.execSelect());
//            } finally {
//                qexec.close();
//            }
//        } finally {
//            dataset.getLock().leaveCriticalSection();
//            w.close();
//        }
//        return rs;
    }
    
    /**
     * requires SPARQL 1.1 (or ARQ) property path support
     * @param vclassURI
     * @return list of property resources with union domains that include the vclass
     */
    protected List<Resource> getPropertiesWithAppropriateDomainFor(String vclassURI) {
        List<Resource> propertyResList = new ArrayList<Resource>();
    	String queryStr = 
    		      "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                  "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n" +
                  "PREFIX owl:   <http://www.w3.org/2002/07/owl#> \n\n " +
                  "SELECT ?p WHERE { \n" +
                  "  { \n" +
                  "    ?p rdfs:domain <" + vclassURI + "> . \n" +
                  "  } UNION { \n" +
                  "    ?parent rdfs:domain <" + vclassURI + "> . \n" +
                  "    ?p rdfs:subPropertyOf* ?parent. \n" + 
                  "    OPTIONAL { \n" +
                  "      ?p rdfs:domain ?childDomain \n" +
                  "    } \n" +
                  "    FILTER (!bound(?childDomain)) \n" +
                  "  } UNION { \n" +
                  "    ?f rdf:first <" + vclassURI + "> . \n" +
                  "    ?u rdf:rest* ?f . \n" +
                  "    ?d owl:unionOf ?u . \n" +
                  "    ?p rdfs:domain ?d . \n" +
                  "  } UNION { \n" +
                  "    ?f rdf:first <" + vclassURI + "> . \n" +
                  "    ?u rdf:rest* ?f . \n" +
                  "    ?d owl:unionOf ?u . \n" +
                  "    ?parent rdfs:domain ?d . \n" +
                  "    ?p rdfs:subPropertyOf* ?parent. \n" + 
                  "    OPTIONAL { \n" +
                  "      ?p rdfs:domain ?childDomain \n" +
                  "    } \n" +
                  "    FILTER (!bound(?childDomain)) \n" +
                  "  } \n" +
                  "  FILTER(?p != owl:bottomDataProperty \n" +
                  "      && ?p != owl:bottomObjectProperty) \n" +
                  "}";
    	Query q = QueryFactory.create(queryStr, Syntax.syntaxSPARQL_11);
    	QueryExecution qe = QueryExecutionFactory.create(
    			q, getOntModelSelector().getTBoxModel());
    	try {
    	    ResultSet rs = qe.execSelect();
    	    while (rs.hasNext()) {
    	    	QuerySolution qs = rs.nextSolution();
    	    	propertyResList.add(qs.getResource("p"));
    	    }
    	} finally {
    		qe.close();
    	}
    	return propertyResList;
    }
    
    public List<PropertyInstance> getAllPossiblePropInstForIndividual(String individualURI) {
    	Individual ind = getWebappDaoFactory().getIndividualDao().getIndividualByURI(individualURI);
    	VClassDao vcDao = getWebappDaoFactory().getVClassDao();
    	
    	List<VClass> allTypes = ind.getVClasses(false); // include indirect types
        
        Set<String> allSuperclassURIs = new HashSet<String>();
        
        for (VClass type : allTypes) {
            String classURI = type.getURI();
            if (classURI != null) {
                allSuperclassURIs.add(type.getURI());
            }
            for (String equivURI : vcDao.getEquivalentClassURIs(classURI)) {
                allSuperclassURIs.add(equivURI);
                allSuperclassURIs.addAll(vcDao.getAllSuperClassURIs(equivURI));
            }
            allSuperclassURIs.addAll(vcDao.getAllSuperClassURIs(classURI));
        }
        
        List<VClass> vclasses = new ArrayList<VClass>();
        for(String vclassURI : allSuperclassURIs) {
            VClass vclass = vcDao.getVClassByURI(vclassURI);
            if (vclass != null) {
                vclasses.add(vclass);
            }
        }
    	return getAllPropInstByVClasses(vclasses);
    	
    }
    
    /*
     * sorts VClasses so that subclasses come before superclasses
     */
    private class VClassHierarchyRanker implements Comparator<VClass> {
    	private VClassDao vcDao;
    	public VClassHierarchyRanker(VClassDao vcDao) {
    		this.vcDao = vcDao;
    	}
    	public int compare(VClass vc1, VClass vc2) {
    		if (vcDao.isSubClassOf(vc1, vc2)) {
    			return -1;
    		} else if (vcDao.isSubClassOf(vc2, vc1)) {
    		    return 1;
    		} else {
    		    return 0;
    		}
    	}
    }
    
    
    public List<PropertyInstance> getAllPropInstByVClass(String classURI) {
        if (classURI==null || classURI.length()<1) {
            return null;
        }
        
        VClassDao vcDao = getWebappDaoFactory().getVClassDao();
        
        Set<String> allSuperclassURIs = new HashSet<String>();
       
        allSuperclassURIs.add(classURI);
        for (String equivURI : vcDao.getEquivalentClassURIs(classURI)) {
            allSuperclassURIs.add(equivURI);
            allSuperclassURIs.addAll(vcDao.getAllSuperClassURIs(equivURI));
        }
        allSuperclassURIs.addAll(vcDao.getAllSuperClassURIs(classURI));
        
        List<VClass> vclasses = new ArrayList<VClass>();
        for(String vclassURI : allSuperclassURIs) {
            VClass vclass = vcDao.getVClassByURI(vclassURI);
            if (vclass != null) {
                vclasses.add(vclass);
            }
        }
        return getAllPropInstByVClasses(vclasses);
    }
    
    private void updatePropertyRangeMap(Map<String, Resource[]> map, 
                                        String propURI, 
                                        Resource[] ranges) {
        Resource[] existingRanges = map.get(propURI);
        if (existingRanges == null) {
            map.put(propURI, ranges);
        } else if (existingRanges[0] == null && existingRanges[1] != null) {
            existingRanges[0] = ranges[0];
            map.put(propURI, existingRanges);
        } else if (existingRanges[0] != null && existingRanges[1] == null) {
            existingRanges[1] = ranges[1];
            map.put(propURI, existingRanges);            
        }
    }
    
    private List<OntClass> listSuperClasses(OntClass ontClass) {
        return relatedClasses(ontClass, RDFS.subClassOf);
    }
    
    private List<OntClass> listEquivalentClasses(OntClass ontClass) {
        return relatedClasses(ontClass, OWL.equivalentClass);
    }
     
    private List<OntClass> relatedClasses(OntClass ontClass,
            com.hp.hpl.jena.rdf.model.Property property) {
        List<OntClass> classes = new ArrayList<OntClass>();
        StmtIterator closeIt = ontClass.listProperties(property);
        try {
            while (closeIt.hasNext()) {
                Statement stmt = closeIt.nextStatement();
                if (stmt.getObject().canAs(OntClass.class)) {
                    classes.add(stmt.getObject().as(OntClass.class));
                }
            }
        } finally {
            closeIt.close();
        }
        return classes;
    }
    
    private static final int DEPTH_LIMIT = 20;
    
    private List<Restriction> getRelatedRestrictions(OntClass ontClass) {
        List<Restriction> restList = new ArrayList<Restriction>();
        addRelatedRestrictions(ontClass, restList, DEPTH_LIMIT);
        return restList;
    }
    
    private void addRelatedRestrictions(OntClass ontClass, 
            List<Restriction> relatedRestrictions, int limit) {
        limit--;
        if (ontClass.isRestriction()) {
            relatedRestrictions.add(ontClass.as(Restriction.class));
        } else if (ontClass.isIntersectionClass()) {
            IntersectionClass inter = ontClass.as(IntersectionClass.class);
            Iterator<? extends OntClass> operIt = inter.listOperands();
            while (operIt.hasNext()) {
                OntClass operand = operIt.next();
                if (!relatedRestrictions.contains(operand) && limit > 0) {
                    addRelatedRestrictions(operand, relatedRestrictions, limit);
                }
            }   
        } else {
            List<OntClass> superClasses = listSuperClasses(ontClass);
            superClasses.addAll(listEquivalentClasses(ontClass));
            for (OntClass sup : superClasses) {
                if (sup.isAnon() && !sup.equals(ontClass) 
                        && !relatedRestrictions.contains(ontClass) && limit > 0) {
                    addRelatedRestrictions(sup, relatedRestrictions, limit);
                }
            }
        }
    }
    
    public List<PropertyInstance> getAllPropInstByVClasses(List<VClass> vclasses) {
        
        List<PropertyInstance> propInsts = new ArrayList<PropertyInstance>();
        
        if(vclasses == null || vclasses.isEmpty()) {
            return propInsts;
        }
        
        Collections.sort(vclasses, new VClassHierarchyRanker(this.getWebappDaoFactory().getVClassDao()));
        
        OntModel ontModel = getOntModelSelector().getTBoxModel();
        
        try {
        
        	ontModel.enterCriticalSection(Lock.READ);
        	
        	// map object property URI to an array of two resources:
        	// the first is the "allValuesFrom" resource and the second is
        	// "someValuesFrom"
        	Map<String, Resource[]> applicableProperties = 
        	        new HashMap<String, Resource[]>();
        	
        	try {
		        for (VClass vclass : vclasses) {
		            if (vclass.isAnonymous()) {
		                continue; 
		            }
		            String VClassURI = vclass.getURI();
		            
		        	OntClass ontClass = getOntClass(ontModel,VClassURI);
		        	if (ontClass == null) {
		        	    continue;  
		        	}
	        	    List<Restriction> relatedRestrictions = getRelatedRestrictions(ontClass);
	        	    for (Restriction rest : relatedRestrictions) {
		        	    // find properties in restrictions
	        			// TODO: check if restriction is something like
	        			// maxCardinality 0 or allValuesFrom owl:Nothing,
	        			// in which case the property is NOT applicable!
	        			OntProperty onProperty = rest.getOnProperty();
	        			if (onProperty != null) {
	        			    Resource[] ranges = new Resource[2];
	        			    if (rest.isAllValuesFromRestriction()) {
	        			        ranges[0] = (rest.asAllValuesFromRestriction()).getAllValuesFrom();
	        			    } else if (rest.isSomeValuesFromRestriction()) {
                                ranges[1] = (rest.asSomeValuesFromRestriction()).getSomeValuesFrom();
                            }
	        				updatePropertyRangeMap(applicableProperties, onProperty.getURI(), ranges);
		        		}
	        	    }
	        		
	        	    List<Resource> propertyList = 
	        	    	    getPropertiesWithAppropriateDomainFor(VClassURI);
	        		for (Resource prop : propertyList) {
	        		    if (prop.getNameSpace() != null 
	        		            && !NONUSER_NAMESPACES.contains(
	        		                    prop.getNameSpace()) ) {
	        		        StmtIterator rangeSit = prop.listProperties(
	        		                RDFS.range);
	        		        Resource rangeRes = null;
	        		        while (rangeSit.hasNext()) {    
	        		            Statement s = rangeSit.nextStatement();
	        		            if (s.getObject().isURIResource()) {
	        		                rangeRes = (Resource) s.getObject();
	        		            }
	        		        }
	        		        Resource[] ranges = new Resource[2];
	        		        ranges[0] = rangeRes;
	        		        updatePropertyRangeMap(
	        		                applicableProperties, prop.getURI(), ranges);
	        		        
	        		    }
	        		}
	        		
	        	}
		             
        	} catch (Exception e) {
        		log.error("Unable to get applicable properties " +
        		          "by examining property restrictions and domains", e);
        	}
        	
        	// make the PropertyInstance objects
	        for (String propertyURI : applicableProperties.keySet()) {
	            OntProperty op = ontModel
	                    .getOntProperty(propertyURI);
	            if (op == null) {
	                continue;
	            } 
	            Resource[] foundRanges = applicableProperties.get(propertyURI);
	            Resource rangeRes = (foundRanges[0] != null) 
	                    ? foundRanges[0]
	                            : (op.getRange() == null && foundRanges[1] != null)
	                            ? foundRanges[1]
	                                    : op.getRange();
	            Resource domainRes = op.getDomain();
	                            propInsts.add(getPropInst(op, domainRes, rangeRes));
	            List<Pair<String,String>> additionalFauxSubpropertyDomainAndRangeURIs = 
	                    getAdditionalFauxSubpropertyDomainAndRangeURIsForPropertyURI(
	                            propertyURI);
	            for (Pair<String,String> domainAndRangeURIs : 
	                    additionalFauxSubpropertyDomainAndRangeURIs) {
	                boolean applicablePropInst = false;
	                if (rangeRes == null ||  
	                        !getWebappDaoFactory().getVClassDao().isSubClassOf(
	                            rangeRes.getURI(), domainAndRangeURIs.getRight())) { 
	                    if (domainAndRangeURIs.getLeft() == null) {
	                        applicablePropInst = true;
	                    } else {
    	                    for(VClass vclass : vclasses) {
    	                        if (vclass.getURI() != null && vclass.getURI().equals(
    	                                domainAndRangeURIs.getLeft())) {
    	                            applicablePropInst = true;
    	                            break;
    	                        }
    	                    }
	                    }
	                    if (applicablePropInst) {
    	                    propInsts.add(getPropInst(
                                    op,
                                    ResourceFactory.createResource(domainAndRangeURIs.getLeft()),
                                    ResourceFactory.createResource(domainAndRangeURIs.getRight()) 
                                    ));
	                    }
	                }
	            }
	        }
	        
        } finally {
        	ontModel.leaveCriticalSection();
        }
        
        // add any faux properties with applicable domain where the predicate URI
        // is not already on the list
        List<ObjectProperty> stragglers = getAdditionalFauxSubpropertiesForVClasses(
                vclasses, propInsts);
        for (ObjectProperty op : stragglers) {
            propInsts.add(makePropInst(op));
        }
        
        return propInsts;
        
    }
    
    private PropertyInstance makePropInst(ObjectProperty op) {
        PropertyInstance pi = new PropertyInstance();
        pi.setDomainClassURI(op.getDomainVClassURI());
        pi.setRangeClassURI(op.getRangeVClassURI());
        pi.setSubjectSide(true);
        pi.setPropertyURI(op.getURI());
        pi.setPropertyName(op.getLabel());
        pi.setDomainPublic(op.getDomainPublic());
        return pi;
    }
    
    private PropertyInstance getPropInst(OntProperty op, Resource domainRes, 
            Resource rangeRes) {  
        if (log.isDebugEnabled() && domainRes != null && rangeRes != null) {
            log.debug("getPropInst() op: " + op.getURI() + " domain: " + 
                domainRes.getURI() + " range: " + rangeRes.getURI());
        }
        PropertyInstance pi = new PropertyInstance();
        String domainURIStr = (domainRes != null && !domainRes.isAnon()) ? 
                domainURIStr = domainRes.getURI()
                : null;
        if (rangeRes == null) {
            pi.setRangeClassURI(OWL.Thing.getURI()); // TODO see above
        } else {
            String rangeClassURI;
            if (rangeRes.isAnon()) {
                rangeClassURI = PSEUDO_BNODE_NS + rangeRes.getId()
                        .toString();
            } else {
                rangeClassURI = rangeRes.getURI();
            }
            pi.setRangeClassURI(rangeClassURI);
            VClass range = getWebappDaoFactory().getVClassDao()
                    .getVClassByURI(rangeClassURI);
            if (range == null) {
                range = new VClass();
                range.setURI(rangeClassURI);
                range.setName(range.getLocalName());
            }
            pi.setRangeClassName(range.getName());
        }
        pi.setDomainClassURI(domainURIStr);
        if (domainURIStr != null) {
            VClass domain = getWebappDaoFactory().getVClassDao()
                    .getVClassByURI(domainURIStr);
            if (domain == null) {
                domain = new VClass();
                domain.setURI(domainURIStr);
                domain.setName(domain.getLocalName());
            }
            pi.setDomainClassName(domain.getName());
        }
        pi.setSubjectSide(true);
        pi.setPropertyURI(op.getURI());
        pi.setPropertyName(getLabelOrId(op)); // TODO
        pi.setRangePublic(getLabelOrId(op));
        pi.setDomainPublic(getLabelOrId(op));
        return pi;
    }
    
    private List<ObjectProperty> getAdditionalFauxSubpropertiesForVClasses(
            List<VClass> vclasses, List<PropertyInstance> propInsts) {
        
        List<ObjectProperty> opList = new ArrayList<ObjectProperty>();
        if (vclasses.size() == 0) {
            return opList;
        }
        ObjectPropertyDao opDao = getWebappDaoFactory().getObjectPropertyDao();
        String propQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "PREFIX config: <http://vitro.mannlib.cornell.edu/ns/vitro/ApplicationConfiguration#> \n" +
                "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> \n" +
                "SELECT ?property ?domain ?range WHERE { \n" +
                "    ?context config:configContextFor ?property . \n" +
                "    ?context config:qualifiedByDomain ?domain . \n" +
                "    ?context config:qualifiedBy ?range . \n";
        for(PropertyInstance propInst : propInsts) {
            propQuery += "    FILTER (?property != <" + propInst.getPropertyURI() + "> ) \n";
        }
        Iterator<VClass> classIt = vclasses.iterator();
        if(classIt.hasNext()) {
            propQuery += "    FILTER ( \n";
            propQuery += "        (?domain = <" + OWL.Thing.getURI() + "> )\n";
            while (classIt.hasNext()) {
                VClass vclass = classIt.next();
                if(vclass.isAnonymous()) {
                    continue;
                }
                propQuery += "       || (?domain = <" + vclass.getURI() + "> ) \n";
            }
            propQuery += ") \n";
        }
        propQuery += "} \n";
        log.debug(propQuery);
        Query q = QueryFactory.create(propQuery);
        QueryExecution qe = QueryExecutionFactory.create(
                q, getOntModelSelector().getDisplayModel());
        try {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution qsoln = rs.nextSolution();
                String propertyURI = qsoln.getResource("property").getURI();
                String domainURI = qsoln.getResource("domain").getURI();
                String rangeURI = qsoln.getResource("range").getURI();
                opList.add(opDao.getObjectPropertyByURIs(
                        propertyURI, domainURI, rangeURI, null));
            }  
        } finally {
            qe.close();
        } 
        return opList;
    }
    
    private List<Pair<String,String>> getAdditionalFauxSubpropertyDomainAndRangeURIsForPropertyURI(String propertyURI) {
        List<Pair<String,String>> domainAndRangeURIs = new ArrayList<Pair<String,String>>();
        String propQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "PREFIX config: <http://vitro.mannlib.cornell.edu/ns/vitro/ApplicationConfiguration#> \n" +
                "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> \n" +
                "SELECT ?domain ?range WHERE { \n" +
                "    ?context config:configContextFor <" + propertyURI + "> . \n" +
                "    ?context config:qualifiedBy ?range . \n" +
                "    OPTIONAL { ?context config:qualifiedByDomain ?domain } \n" +
                "}"; 

        Query q = QueryFactory.create(propQuery);
        QueryExecution qe = QueryExecutionFactory.create(q, getOntModelSelector().getDisplayModel());
        try {
            ResultSet rs = qe.execSelect();
            while (rs.hasNext()) {
                QuerySolution qsoln = rs.nextSolution();
                Resource rangeRes = qsoln.getResource("range");
                String rangeURI = rangeRes.getURI();
                Resource domainRes = qsoln.getResource("domain");
                String domainURI = null;
                if (domainRes != null && !domainRes.isAnon()) {
                    domainURI = domainRes.getURI();
                } 
                domainAndRangeURIs.add(new Pair<String,String>(domainURI, rangeURI));
            }  
        } finally {
            qe.close();
        }
        return domainAndRangeURIs;
    }
    
    
}
