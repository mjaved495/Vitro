package edu.cornell.mannlib.vitro.webapp.controller.edit;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DatatypePropertyPageController extends HttpServlet {
private static final Log log = LogFactory.getLog(ClassPageController.class.getName());
	
	public void doPost (HttpServletRequest req, HttpServletResponse response) throws IOException {
		if(req.getParameter("uri") == null) {
			response.getWriter().println("404 Not Found");
			return;
		}
		
		VitroRequest request = new VitroRequest(req);
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
		
		String propertyURI = request.getParameter("uri");
		
		DataPropertyDao dpDao = wadf.getDataPropertyDao();
		DataProperty dp = dpDao.getDataPropertyByURI(propertyURI);
		VClassDao vcDao = wadf.getVClassDao();
		
		request.setAttribute("dataProperty", dp);
		
		if(dp.getNamespace() != null) {
        	 Ontology ont = wadf.getOntologyDao().getOntologyByURI(dp.getNamespace());
             
             request.setAttribute("ontology",  ont);
             request.setAttribute("allClasses", ont.getVClassesList());
             request.setAttribute("allProperties", dpDao.getAllDataProperties());
		}
		
		List<DataProperty> superproperties = getPropsForURIList(dpDao.getAllSuperPropertyURIs(propertyURI), dpDao);
		List<DataProperty> subproperties = getPropsForURIList(dpDao.getAllSubPropertyURIs(propertyURI), dpDao);
		List<DataProperty> eqproperties = getPropsForURIList(dpDao.getEquivalentPropertyURIs(propertyURI), dpDao);
		
		VClass domain = vcDao.getVClassByURI(dp.getDomainVClassURI());
		VClass range = vcDao.getVClassByURI(dp.getRangeVClassURI());
		
		List<VClass> domains = new ArrayList<VClass>();
		domains.add(domain);
		
		List<VClass> ranges = new ArrayList<VClass>();
		ranges.add(range);
		
		request.setAttribute("superproperties", superproperties);
		request.setAttribute("subproperties", subproperties);
		request.setAttribute("eqproperties", eqproperties);
		request.setAttribute("domains", domains);
		request.setAttribute("ranges", ranges);
		
		String blankJsp = "/templates/edit/blank.jsp";
		
		RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp", blankJsp);
        request.setAttribute("formJsp","/templates/edit/specific/datatype_property.jsp");
        request.setAttribute("colspan","4");
        request.setAttribute("scripts","/templates/edit/js/datatype_property.js");
        request.setAttribute("title","Datatype Property Editing Form");
		
        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("DatatypePropertyPageController could not forward to view.", e);
            throw new RuntimeException(e);
        }
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse response) throws IOException {
		doPost(req, response);
	}
	
	private static List<DataProperty> getPropsForURIList(List<String> propURIs, DataPropertyDao dpDao) {
        List<DataProperty> props = new ArrayList<DataProperty>();
        Iterator<String> urIt = propURIs.iterator();
        while (urIt.hasNext()) {
            String propURI = urIt.next();
            DataProperty dp = dpDao.getDataPropertyByURI(propURI);
            if (dp != null) {
                props.add(dp);
            }
        }
        return props;
    }
}
