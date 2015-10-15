package edu.cornell.mannlib.vitro.webapp.controller.edit;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntClass;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassDaoJena;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

@SuppressWarnings("serial")
public class ClassPageController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ClassPageController.class.getName());
	
	public void doPost (HttpServletRequest req, HttpServletResponse response) {
		VitroRequest request = new VitroRequest(req);
		
		// not sure if the below two lines are useful yet
		
		EditProcessObject epo = super.createEpo(request, FORCE_NEW);
        request.setAttribute("epoKey", epo.getKey());
        
        // get the VClass
        
        VClassDao vcwDao = ModelAccess.on(getServletContext())
        		.getWebappDaoFactory(ASSERTIONS_ONLY).getVClassDao();
        VClass vcl = (VClass)vcwDao.getVClassByURI(request.getParameter("uri"));
        
        if (vcl == null) {
        	vcl = request.getUnfilteredWebappDaoFactory()
        	        .getVClassDao().getTopConcept();
        }

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("VClass",vcl);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("formJsp","/templates/edit/specific/class.jsp");
        request.setAttribute("colspan","4");
        request.setAttribute("scripts","/templates/edit/classpage.js");
        request.setAttribute("title","Class Editing Form");
        
        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("VclassRetryController could not forward to view.", e);
            throw new RuntimeException(e);
        }
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse response) {
		doPost(req, response);
	}
}
