package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class EditDomainItemController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String oldDomainURI = req.getParameter("oldDomainURI");
		String newDomainURI = req.getParameter("newDomainURI");
		String propertyURI = req.getParameter("propertyURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
		VClassDao vcDao = wadf.getVClassDao();
        
		ObjectProperty op = opDao.getObjectPropertyByURI(propertyURI);
		op.setDomainVClassURI(newDomainURI);
		opDao.updateObjectProperty(op);
		
		res.getWriter().print(newDomainURI);
	}
}
