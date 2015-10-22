package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassDaoJena;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class EditSubclassItemController extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String newSubclassURI = req.getParameter("newSubclassURI");
		String oldSubclassURI = req.getParameter("oldSubclassURI");
		String vclassURI = req.getParameter("superclassURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        VClassDao vcwDao = wadf.getVClassDao();
		
		vcwDao.removeSubclass(vclassURI, oldSubclassURI);
		vcwDao.addSubclass(vclassURI, newSubclassURI);
		
		res.getWriter().print(newSubclassURI);
	}
}
