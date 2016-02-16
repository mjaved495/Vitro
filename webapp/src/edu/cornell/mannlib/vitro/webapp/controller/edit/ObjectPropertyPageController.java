package edu.cornell.mannlib.vitro.webapp.controller.edit;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class ObjectPropertyPageController extends BaseEditController {

	private static final Log log = LogFactory.getLog(ClassPageController.class.getName());
	
	public void doPost (HttpServletRequest req, HttpServletResponse response) throws IOException {
		if(req.getParameter("uri") == null) {
			response.getWriter().println("");
			return;
		}
		
		VitroRequest request = new VitroRequest(req);
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
		
		ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
		ObjectProperty op = (ObjectProperty) opDao.getObjectPropertyByURI(request.getParameter("uri"));
		
		VClassDao vcDao = wadf.getVClassDao();
		
		request.setAttribute("objectProperty", op);
		
		if(op.getNamespace() != null) {
        	 Ontology ont = wadf.getOntologyDao().getOntologyByURI(op.getNamespace());
             
             request.setAttribute("ontology",  ont);
		}

        request.setAttribute("allClasses", vcDao.getAllVclasses());
		request.setAttribute("allProperties", opDao.getAllObjectProperties());
		
		List<ObjectProperty> superproperties = getPropsForURIList(opDao.getAllSuperPropertyURIs(op.getURI()), opDao);
		List<ObjectProperty> subproperties = getPropsForURIList(opDao.getSubPropertyURIs(op.getURI()), opDao);
		List<ObjectProperty> eqproperties = getPropsForURIList(opDao.getEquivalentPropertyURIs(op.getURI()), opDao);
		List<Object> inverses = new ArrayList<Object>();
		
		List<Object> domains = new ArrayList<Object>();
		if(op.getDomainVClassURI() != null) {
			domains.add(vcDao.getVClassByURI(op.getDomainVClassURI()));
		}
		
		List<Object> ranges = new ArrayList<Object>();
		if(op.getRangeVClassURI() != null) {
			ranges.add(vcDao.getVClassByURI(op.getRangeVClassURI()));
		}
		
		request.setAttribute("superproperties", superproperties);
		request.setAttribute("subproperties", subproperties);
		request.setAttribute("eqproperties", eqproperties);
		log.debug(eqproperties);
		request.setAttribute("inverses", inverses);
		
		request.setAttribute("domains", domains);
		request.setAttribute("ranges", ranges);
		
		String blankJsp = "/templates/edit/blank.jsp";
		
		RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp", blankJsp);
        request.setAttribute("formJsp","/templates/edit/specific/object_property.jsp");
        request.setAttribute("colspan","4");
        request.setAttribute("scripts","/templates/edit/js/object_property.js");
        request.setAttribute("title","Object Property Editing Form");
		
        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("ObjectPropertyPageController could not forward to view.", e);
            throw new RuntimeException(e);
        }
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse response) throws IOException {
		doPost(req, response);
	}
	
	private static List<ObjectProperty> getPropsForURIList(List<String> propURIs, ObjectPropertyDao opDao) {
        List<ObjectProperty> props = new ArrayList<ObjectProperty>();
        Iterator<String> urIt = propURIs.iterator();
        while (urIt.hasNext()) {
            String propURI = urIt.next();
            ObjectProperty op = opDao.getObjectPropertyByURI(propURI);
            if (op != null) {
                props.add(op);
            }
        }
        return props;
    }
	
}
