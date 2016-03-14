package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.dataproperty.AddDomainItemController;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class EditNameController extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(AddDomainItemController.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String uri = req.getParameter("uri");
		String newName = req.getParameter("newName");
		String type = req.getParameter("type");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        if(type.equals("vclass")) {
    		VClassDao vcwDao = wadf.getVClassDao();
            
            VClass vcl;
    		try {
    			vcl = (VClass)vcwDao.getVClassByURI(URLDecoder.decode(uri, "UTF-8"));
    	        vcl.setName(newName);
    	        vcwDao.updateVClass(vcl);
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    		}
        }
        else if(type.equals("objprop")) {
        	ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
        	
        	ObjectProperty op;
        	try {
        		op = (ObjectProperty)opDao.getObjectPropertyByURI(URLDecoder.decode(uri, "UTF-8"));
        		op.setLabel(newName);
        		log.info("New name: " + newName);
        		log.info("OP's label: " + op.getLabel());
        		opDao.updateObjectProperty(op);
        	}
        	catch (UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        else if(type.equals("dataprop")) {
        	DataPropertyDao dpDao = wadf.getDataPropertyDao();
        	
        	DataProperty dp;
        	
        	try {
        		dp = (DataProperty)dpDao.getDataPropertyByURI(URLDecoder.decode(uri, "UTF-8"));
        		dp.setName(newName);
        		dpDao.updateDataProperty(dp);
        	}
        	catch (UnsupportedEncodingException e) {
        		e.printStackTrace();
        	}
        }
        
        res.getWriter().println(newName);
	}
}
