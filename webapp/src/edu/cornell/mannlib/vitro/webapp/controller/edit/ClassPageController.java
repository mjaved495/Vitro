package edu.cornell.mannlib.vitro.webapp.controller.edit;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntClass;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassDaoJena;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

@SuppressWarnings("serial")
public class ClassPageController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ClassPageController.class.getName());
	
	public void doPost (HttpServletRequest req, HttpServletResponse response) {
		VitroRequest request = new VitroRequest(req);
        
        // get the VClass
        
        WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
        
        VClassDao vcwDao = wadf.getVClassDao();
        VClass vcl = (VClass)vcwDao.getVClassByURI(request.getParameter("uri"));
        
        if (vcl == null) {
        	vcl = request.getUnfilteredWebappDaoFactory()
        	        .getVClassDao().getTopConcept();
        }
        
        VClassDao vcDao = wadf.getVClassDao();
        ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
        VClassDao displayVcDao = wadf.getVClassDao();
      
        if(vcl.getNamespace() != null) {
        	log.debug("namespace is " + vcl.getNamespace());
        	 Ontology ont = wadf.getOntologyDao().getOntologyByURI(vcl.getNamespace());
             
             request.setAttribute("ontology",  ont);
             // log.debug("allClasses is " + ont.getVClassesList().size() + " elements long");
        }
        else {
        	log.debug("namespace is null");
        	log.debug("uri is " + vcl.getURI());
        }
        
        List<VClass> allClasses = vcDao.getAllVclasses();
        request.setAttribute("allClasses", allClasses);
        request.setAttribute("allProperties", opDao.getAllObjectProperties());
        
        Individual ent = request.getUnfilteredAssertionsWebappDaoFactory().getIndividualDao().getIndividualByURI(vcl.getURI());
        request.setAttribute("entity", ent);
        
        String hiddenFromDisplay = (vcl.getHiddenFromDisplayBelowRoleLevel() == null ? "(unspecified)"
				: vcl.getHiddenFromDisplayBelowRoleLevel().getDisplayLabel());
		String prohibitedFromUpdate = (vcl
				.getProhibitedFromUpdateBelowRoleLevel() == null ? "(unspecified)"
				: vcl.getProhibitedFromUpdateBelowRoleLevel().getUpdateLabel());
		String hiddenFromPublish = (vcl.getHiddenFromPublishBelowRoleLevel() == null ? "(unspecified)"
				: vcl.getHiddenFromPublishBelowRoleLevel().getDisplayLabel());
		
		request.setAttribute("displayLevel", hiddenFromDisplay);
		request.setAttribute("updateLevel", prohibitedFromUpdate);
		request.setAttribute("publishLevel", hiddenFromPublish);
        
        List<VClass> superVClasses = getVClassesForURIList(
                vcDao.getSuperClassURIs(vcl.getURI(),false), displayVcDao);
        sortForPickList(superVClasses, request);
        request.setAttribute("superclasses",superVClasses);

        List<VClass> subVClasses = getVClassesForURIList(
                vcDao.getSubClassURIs(vcl.getURI()), displayVcDao);
        sortForPickList(subVClasses, request);
        request.setAttribute("subclasses",subVClasses);
        
        /* generate a list of sibling classes */
        
        List<VClass> siblingVClasses = new ArrayList<VClass>();
        
        for(VClass vClass : superVClasses) {
        	List<VClass> vClassSubclasses = getVClassesForURIList(
        		vcDao.getSubClassURIs(vClass.getURI()), displayVcDao);
        	for(VClass subclass : vClassSubclasses) {
        		if(!(siblingVClasses.contains(subclass)) && !(subclass.equals(vcl))) {
        			siblingVClasses.add(subclass);
        		}
        	}
        }
        
        String group = (vcl.getGroup() == null ? "(unspecified)" : vcl.getGroup().getPublicName());
        request.setAttribute("classGroup", group);
        
        sortForPickList(siblingVClasses, request);
        request.setAttribute("siblings", siblingVClasses);
            
        List<VClass> djVClasses = getVClassesForURIList(
                vcDao.getDisjointWithClassURIs(vcl.getURI()), displayVcDao);
        sortForPickList(djVClasses, request);
        request.setAttribute("disjointClasses",djVClasses);

        List<VClass> eqVClasses = getVClassesForURIList(
                vcDao.getEquivalentClassURIs(vcl.getURI()), displayVcDao);
        sortForPickList(eqVClasses, request);
        request.setAttribute("equivalentClasses",eqVClasses);
        
        String blankJsp = "/templates/edit/blank.jsp";
        
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("VClass",vcl);
        request.setAttribute("bodyJsp", blankJsp);
        request.setAttribute("formJsp","/templates/edit/specific/class.jsp");
        request.setAttribute("colspan","4");
        request.setAttribute("scripts","/templates/edit/js/class.js");
        request.setAttribute("title","Class Editing Form");
        
        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("ClassPageController could not forward to view.", e);
            throw new RuntimeException(e);
        }
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse response) {
		doPost(req, response);
	}
	
	private List<VClass> getVClassesForURIList(List<String> vclassURIs, VClassDao vcDao) {
        List<VClass> vclasses = new ArrayList<VClass>();
        Iterator<String> urIt = vclassURIs.iterator();
        while (urIt.hasNext()) {
            String vclassURI = urIt.next();
            VClass vclass = vcDao.getVClassByURI(vclassURI);
            if (vclass != null) {
                vclasses.add(vclass);
            }
        }
        return vclasses;
    }
	
	private List<VClass> getSuperclasses(VClassDao vcDao, VClass root) {
		return getVClassesForURIList(
                vcDao.getSuperClassURIs(root.getURI(),false), vcDao);
	}
	
	private List<VClass> getSubclasses(VClassDao vcDao, VClass root) {
		return getVClassesForURIList(
                vcDao.getSubClassURIs(root.getURI()), vcDao);
	}
	
	public List<VClass> dfsTraversal(VClassDao vcDao, VClass root) {
		// preorder traversal (depth-first search)
		List<VClass> result = new ArrayList<VClass>();
		if(getSubclasses(vcDao, root).size() == 0) {
			result.add(root);
			return result;
		}
		else {
			for(VClass subclass : getSubclasses(vcDao, root)) {
				result.addAll(dfsTraversal(vcDao, subclass));
			}
			return result;
		}
	}
	
	public List<VClass> getVClassesInOntology(VClassDao vcDao, VClass vcl) {
		// travel up in class hierarchy tree until reaching root
		
		VClass currentVClass = vcl;
		List<VClass> superclasses = getSuperclasses(vcDao, currentVClass);
		while(superclasses.size() > 0) {
			currentVClass = superclasses.get(0);
			superclasses = getSuperclasses(vcDao, currentVClass);
		}
		
		// now at root
		// preorder traversal (depth-first search)
		
		return dfsTraversal(vcDao, currentVClass);
	}
}
