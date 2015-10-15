package edu.cornell.mannlib.vitro.webapp.controller.edit;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

@SuppressWarnings("serial")
public class ClassPageController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(ClassPageController.class.getName());
	
	public void doPost (HttpServletRequest req, HttpServletResponse response) {
		VitroRequest request = new VitroRequest(req);
		RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
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
