package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.vclass;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.edit.DatatypePropertyPageController;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DeleteVClassController extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(DeleteVClassController.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String vclassURI = req.getParameter("vclassURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        VClassDao vcwDao = wadf.getVClassDao();
        
        VClass vcl = vcwDao.getVClassByURI(vclassURI);
		
        String nextURI;
        
        if(vcwDao.getSuperClassURIs(vclassURI, true).size() > 0) {
        	nextURI = vcwDao.getSuperClassURIs(vclassURI, true).get(0);
        }
        else {
        	nextURI = vcwDao.getAllVclasses().get(0).getURI();
        }
        
        vcwDao.deleteVClass(vcl);
		
		res.getWriter().print(nextURI);
	}
}
