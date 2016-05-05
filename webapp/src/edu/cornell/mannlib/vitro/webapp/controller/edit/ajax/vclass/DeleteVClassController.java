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
		
        String immediateParentURI; // we need this to know where to redirect to after the deletion
        
        // remove all relationships, then delete the class
        
        immediateParentURI = vcwDao.getSuperClassURIs(vclassURI, true).get(0);
        
        for(String subclassURI : vcwDao.getSubClassURIs(vcl.getURI())) {
        	vcwDao.removeSubclass(vcl, vcwDao.getVClassByURI(subclassURI));
        	for(String superclassURI : vcwDao.getSuperClassURIs(vcl.getURI(), true)) {
        		vcwDao.addSuperclass(vcl, vcwDao.getVClassByURI(superclassURI));
        	}
        }
        
        for(String superclassURI : vcwDao.getSuperClassURIs(vcl.getURI(), true)) {
        	vcwDao.removeSuperclass(vcl, vcwDao.getVClassByURI(superclassURI));
        }
        
        for(String eqclassURI : vcwDao.getEquivalentClassURIs(vcl.getURI())) {
        	vcwDao.removeEquivalentClass(vcl.getURI(), eqclassURI);
        }
        
        for(String djclassURI : vcwDao.getDisjointWithClassURIs(vcl.getURI())) {
        	vcwDao.removeDisjointWithClass(vcl.getURI(), djclassURI);
        }
		
        vcwDao.deleteVClass(vcl);
		
		res.getWriter().print(immediateParentURI);
	}
}
