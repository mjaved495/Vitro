package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteSubclassController extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String subclassURI = req.getParameter("subclassURI");
		String superclassURI = req.getParameter("superclassURI"); // should be the URI of the currently edited VClass
		
	}
}
