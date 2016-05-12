package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.dataproperty;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DeleteDataPropertyController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String propertyURI = req.getParameter("propertyURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
       	DataPropertyDao dpDao = wadf.getDataPropertyDao();
        
        DataProperty dp = dpDao.getDataPropertyByURI(propertyURI);
		
        String nextURI = "";
        
        if(dpDao.getSuperPropertyURIs(propertyURI, true).size() > 0) {
        	nextURI = dpDao.getSuperPropertyURIs(propertyURI, true).get(0);
        }
        else {
        	nextURI = dpDao.getAllDataProperties().get(0).getURI();
        }
        
        dpDao.deleteDataProperty(dp);
		
		res.getWriter().print(nextURI);
	}
}
