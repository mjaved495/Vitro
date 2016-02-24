package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.dataproperty;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
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
        DataPropertyDao dpDao = wadf.getDataPropertyDao();
		VClassDao vcDao = wadf.getVClassDao();
        
		DataProperty dp = dpDao.getDataPropertyByURI(propertyURI);
		dp.setDomainVClassURI(newDomainURI);
		dpDao.updateDataProperty(dp);
		
		res.getWriter().print(newDomainURI);
	}
}
