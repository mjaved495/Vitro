package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.objproperty;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.vclass.DeleteVClassController;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DeleteObjectPropertyController extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String propertyURI = req.getParameter("propertyURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
       	ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
        
        ObjectProperty op = opDao.getObjectPropertyByURI(propertyURI);
		
        String nextURI = "";
        
        if(opDao.getSuperPropertyURIs(propertyURI, true).size() > 0) {
        	nextURI = opDao.getSuperPropertyURIs(propertyURI, true).get(0);
        }
        else {
        	nextURI = opDao.getAllObjectProperties().get(0).getURI();
        }
        
        opDao.deleteObjectProperty(op);
		
		res.getWriter().print(nextURI);
	}
}
