package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.objproperty;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class CheckboxController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String isObjectProp = req.getParameter("objprop");
		String propertyURI = req.getParameter("propertyURI");
		String attribute = req.getParameter("attribute");
		String value = req.getParameter("value");
		
		boolean checkboxValue;
		
		if(value.equals("true")) {
			checkboxValue = true;
		}
		else {
			checkboxValue = false;
		}
		
		VitroRequest request = new VitroRequest(req);
        
        WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
        
        if(isObjectProp.equals("true")) {
        	ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
            ObjectProperty op = (ObjectProperty)opDao.getObjectPropertyByURI(propertyURI);
    		
    		if(attribute.equals("transitive")) {
    			op.setTransitive(checkboxValue);
    		}
    		else if(attribute.equals("symmetric")) {
    			op.setSymmetric(checkboxValue);
    		}
    		else if(attribute.equals("functional")) {
    			op.setFunctional(checkboxValue);
    		}
    		else if(attribute.equals("inverse_functional")) {
    			op.setInverseFunctional(checkboxValue);
    		}
    		
    		opDao.updateObjectProperty(op);
        }
        else {
        	DataPropertyDao dpDao = wadf.getDataPropertyDao();
        	DataProperty dp = (DataProperty)dpDao.getDataPropertyByURI(propertyURI);
        	
        	if(attribute.equals("functional")) {
        		dp.setFunctional(checkboxValue);
        	}
        	
        	dpDao.updateDataProperty(dp);
        	
        }
		
		res.getWriter().println("ok");
	}
}
