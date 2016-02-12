package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class GetURIController extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String type = req.getParameter("type");
		String name = req.getParameter("name");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
		
		String uri = "";
		
		if(type.equals("class")) {
			VClassDao vcDao = wadf.getVClassDao();
			List<VClass> allVClasses = vcDao.getAllVclasses();
			for(VClass vcl : allVClasses) {
				if(vcl.getName().equals(name)) {
					uri = vcl.getURI();
				}
			}
		}
		else if(type.equals("property")) {
			ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
			List<ObjectProperty> allObjectProperties = opDao.getAllObjectProperties();
			for(ObjectProperty op : allObjectProperties) {
				if(op.getLabel().equals(name)) {
					uri = op.getURI();
				}
			}
		}
		
		res.getWriter().print(uri);
	}
}
