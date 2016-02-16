package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.objproperty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class EditPropNameController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String propURI = req.getParameter("uri");
		String newPropertyName = req.getParameter("newPropertyName");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
        
       	ObjectProperty op;
		try {
			op = (ObjectProperty)opDao.getObjectPropertyByURI(URLDecoder.decode(propURI, "UTF-8"));
	        op.setLabel(newPropertyName);
	        opDao.updateObjectProperty(op);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
		res.getWriter().println(newPropertyName);
	}
}
