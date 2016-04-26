package edu.cornell.mannlib.vitro.webapp.controller.edit;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Datatype;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.DatatypeDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

public class DatatypePropertyPageController extends BaseEditController {
private static final Log log = LogFactory.getLog(DatatypePropertyPageController.class.getName());
	
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
		
		// retrieve list of Resource objects corresponding to XML schema datatypes using reflection
		
		Field[] fields = XSD.class.getDeclaredFields();
		List<Resource> datatypes = new ArrayList<Resource>();
		for(Field field : fields) {
			if(java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
				try {
					Resource r = (Resource)field.get(null);
					datatypes.add(r);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(dp.getNamespace() != null) {
        	 Ontology ont = wadf.getOntologyDao().getOntologyByURI(dp.getNamespace());
             
             request.setAttribute("ontology",  ont);
             request.setAttribute("allClasses", vcDao.getAllVclasses());
             request.setAttribute("allProperties", dpDao.getAllDataProperties());
             request.setAttribute("allDatatypes", datatypes);
		}
		
		String hiddenFromDisplay = (dp.getHiddenFromDisplayBelowRoleLevel() == null ? "(unspecified)"
				: dp.getHiddenFromDisplayBelowRoleLevel().getDisplayLabel());
		String prohibitedFromUpdate = (dp
					.getProhibitedFromUpdateBelowRoleLevel() == null ? "(unspecified)"
					: dp.getProhibitedFromUpdateBelowRoleLevel().getUpdateLabel());
		String hiddenFromPublish = (dp.getHiddenFromPublishBelowRoleLevel() == null ? "(unspecified)"
					: dp.getHiddenFromPublishBelowRoleLevel().getDisplayLabel());
	
		request.setAttribute("displayLevel", hiddenFromDisplay);
		request.setAttribute("updateLevel", prohibitedFromUpdate);
		request.setAttribute("publishLevel", hiddenFromPublish);

		List<DataProperty> superproperties = getPropsForURIList(dpDao.getAllSuperPropertyURIs(propertyURI), dpDao);
		List<DataProperty> subproperties = getPropsForURIList(dpDao.getAllSubPropertyURIs(propertyURI), dpDao);
		List<DataProperty> eqproperties = getPropsForURIList(dpDao.getEquivalentPropertyURIs(propertyURI), dpDao);
		
		request.setAttribute("superproperties", superproperties);
		request.setAttribute("subproperties", subproperties);
		request.setAttribute("eqproperties", eqproperties);
		
		VClass domain = vcDao.getVClassByURI(dp.getDomainVClassURI());
		VClass range = vcDao.getVClassByURI(dp.getRangeVClassURI());
		
		List<VClass> domains = new ArrayList<VClass>();
		List<Datatype> ranges = new ArrayList<Datatype>();
		
		if(dp.getDomainVClassURI() != null && dp.getDomainVClassURI() != "") {
			domains.add(vcDao.getVClassByURI(dp.getDomainVClassURI()));
		}
		
		DatatypeDao dtDao = wadf.getDatatypeDao();
		log.info(dp.getRangeDatatypeURI());
		
		if(dp.getRangeDatatypeURI() != null && dp.getRangeDatatypeURI() != "") {
			ranges.add(dtDao.getDatatypeByURI(dp.getRangeDatatypeURI())); // why does this return null?
		}
		
		log.info(ranges);
		
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
		if (!isAuthorizedToDisplayPage(req, response,
				SimplePermission.DO_BACK_END_EDITING.ACTION)) {
        	return;
        }
		doPost(req, response);
	}
	
	private static boolean isValidURI(String uriString) {
		URL url;
		try {
			url = new URL(uriString);
		}
		catch(Exception e) {
			return false;
		}
		return "http".equals(url.getProtocol());
	}
	
	private static List<DataProperty> getPropsForURIList(List<String> propURIs, DataPropertyDao dpDao) {
        List<DataProperty> props = new ArrayList<DataProperty>();
        Iterator<String> urIt = propURIs.iterator();
        while (urIt.hasNext()) {
            String propURI = urIt.next();
            if(isValidURI(propURI)) {
            	 DataProperty dp = dpDao.getDataPropertyByURI(propURI);
                 if (dp != null) {
                     props.add(dp);
                 }
            }
        }
        return props;
    }
}
