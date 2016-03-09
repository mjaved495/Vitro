package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.dataproperty;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.edit.ClassPageController;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class AddDomainItemController extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(AddDomainItemController.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String domainURI = req.getParameter("domainURI");
		String propertyURI = req.getParameter("propertyURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
		DataPropertyDao dpDao = wadf.getDataPropertyDao();
		
		log.info(domainURI);
		
		DataProperty dp = dpDao.getDataPropertyByURI(propertyURI);
		dp.setDomainClassURI(domainURI);
		dpDao.updateDataProperty(dp);
		
		log.info(dp.getDomainClassURI());
		
		res.getWriter().print(domainURI);
	}
}
