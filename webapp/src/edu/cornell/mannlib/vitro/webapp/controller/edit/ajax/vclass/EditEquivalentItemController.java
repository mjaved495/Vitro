package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class EditEquivalentItemController extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String oldEqClassURI = req.getParameter("oldEqClassURI");
		String newEqClassURI = req.getParameter("newEqClassURI");
		String vclassURI = req.getParameter("vclassURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        VClassDao vcwDao = wadf.getVClassDao();
		
		vcwDao.removeEquivalentClass(vclassURI, oldEqClassURI);
		vcwDao.addEquivalentClass(vclassURI, newEqClassURI);
		
		res.getWriter().print(newEqClassURI);
	}
}
