package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.edit.ClassPageController;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class AddDomainItemController extends HttpServlet {
	private static final Log log = LogFactory.getLog(ClassPageController.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String domainURI = req.getParameter("domainURI");
		String propertyURI = req.getParameter("propertyURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        VClassDao vcDao = wadf.getVClassDao();
		ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
		
		ObjectProperty op = opDao.getObjectPropertyByURI(propertyURI);
		op.setDomainVClassURI(domainURI);
		opDao.updateObjectProperty(op);
		
		res.getWriter().print(domainURI);
	}
}
