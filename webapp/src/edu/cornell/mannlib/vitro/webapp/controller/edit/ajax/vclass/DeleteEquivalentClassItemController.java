package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.vclass;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DeleteEquivalentClassItemController extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String eqClassURI = req.getParameter("eqClassURI");
		String vclassURI = req.getParameter("vclassURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        VClassDao vcwDao = wadf.getVClassDao();
        
        vcwDao.removeEquivalentClass(vclassURI, eqClassURI);
        
		res.getWriter().print("done");
	}
}
