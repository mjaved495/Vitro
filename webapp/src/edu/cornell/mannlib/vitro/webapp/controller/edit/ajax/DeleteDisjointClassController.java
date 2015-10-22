package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteDisjointClassController extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String oldDisjointClassURI = req.getParameter("oldDisjointClassURI");
		String newDisjointClassURI = req.getParameter("newDisjointClassURI");
		String vclassURI = req.getParameter("vclassURI");
	}
}
