package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class MakeSiblingsDisjointController extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse res) throws IOException {
        
        VClassDao vcwDao = ModelAccess.on(getServletContext())
        		.getWebappDaoFactory(ASSERTIONS_ONLY).getVClassDao();
		VClass vcl = (VClass)vcwDao.getVClassByURI(request.getParameter("uri"));
		
		VClassDao vcDao = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY).getVClassDao();
		VClassDao displayVcDao = ModelAccess.on(getServletContext()).getWebappDaoFactory().getVClassDao();
		
		List<VClass> superVClasses = getVClassesForURIList(
	                vcDao.getSuperClassURIs(vcl.getURI(),false), displayVcDao);
		
		List<VClass> siblingVClasses = new ArrayList<VClass>();
        
        for(VClass vClass : superVClasses) {
        	List<VClass> vClassSubclasses = getVClassesForURIList(
        		vcDao.getSubClassURIs(vClass.getURI()), displayVcDao);
        	for(VClass subclass : vClassSubclasses) {
        		if(!(siblingVClasses.contains(subclass)) && !(subclass.equals(vcl))) {
        			siblingVClasses.add(subclass);
        		}
        	}
        }
        
        for(VClass vClass : siblingVClasses) {
        	vcwDao.addDisjointWithClass(vcl.getURI(), vClass.getURI());
        }
        
        res.getWriter().println("done");
	}
	
	private List<VClass> getVClassesForURIList(List<String> vclassURIs, VClassDao vcDao) {
        List<VClass> vclasses = new ArrayList<VClass>();
        Iterator<String> urIt = vclassURIs.iterator();
        while (urIt.hasNext()) {
            String vclassURI = urIt.next();
            VClass vclass = vcDao.getVClassByURI(vclassURI);
            if (vclass != null) {
                vclasses.add(vclass);
            }
        }
        return vclasses;
    }
}
