package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteDisjointClassController extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String disjointClassURI = req.getParameter("disjointClassURI");
		String vclassURI = req.getParameter("vClassURI");
	}
}
