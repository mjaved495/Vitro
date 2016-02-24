package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.dataproperty;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class EditSubpropertyItemController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String oldSubpropertyURI = req.getParameter("oldSubpropertyURI");
		String newSubpropertyURI = req.getParameter("newSubpropertyURI");
		String propertyURI = req.getParameter("propertyURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        DataPropertyDao dpDao = wadf.getDataPropertyDao();
		
		dpDao.removeSubproperty(propertyURI, oldSubpropertyURI);
		dpDao.addSubproperty(propertyURI, newSubpropertyURI);
		
		res.getWriter().print(newSubpropertyURI);
	}
}
