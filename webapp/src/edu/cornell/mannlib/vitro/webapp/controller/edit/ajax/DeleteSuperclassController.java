package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DeleteSuperclassController extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String superclassURI = req.getParameter("superclassURI");
		String vclassURI = req.getParameter("vclassURI"); // should be the URI of the currently edited VClass
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        VClassDao vcwDao = wadf.getVClassDao();
        
        vcwDao.removeSuperclass(vclassURI, superclassURI);
		List<String> strings = vcwDao.getAllSuperClassURIs(vclassURI);
        
		res.getWriter().print("done");
	}
}
