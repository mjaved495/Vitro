package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditEquivalentItemController extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String eqClassURI = req.getParameter("eqClassURI");
		String currentClassURI = req.getParameter("vClassURI");
	}
}
