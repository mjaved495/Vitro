package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteSuperclassController extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String superclassURI = req.getParameter("superclassURI");
		String vclassURI = req.getParameter("vclassURI"); // should be the URI of the currently edited VClass
		
	}
}
