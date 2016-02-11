package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.edit.ClassPageController;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class AddSubpropertyItemController extends HttpServlet {
	private static final Log log = LogFactory.getLog(ClassPageController.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String subpropertyURI = req.getParameter("subpropertyURI");
		String propertyURI = req.getParameter("propertyURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
		
		opDao.addSubproperty(propertyURI, subpropertyURI);
		log.debug(opDao.getAllSubPropertyURIs(propertyURI));
		
		res.getWriter().print(subpropertyURI);
	}
}
