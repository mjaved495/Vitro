package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.dataproperty;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class AddRangeItemController extends HttpServlet {
	
	private static final Log log = LogFactory.getLog(AddRangeItemController.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String rangeURI = req.getParameter("rangeURI");
		String propertyURI = req.getParameter("propertyURI");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        VClassDao vcDao = wadf.getVClassDao();
		DataPropertyDao dpDao = wadf.getDataPropertyDao();
		
		DataProperty dp = dpDao.getDataPropertyByURI(propertyURI);
		dp.setRangeDatatypeURI(rangeURI);
		dpDao.updateDataProperty(dp);
		
		log.info(dp.getRangeDatatypeURI());
		
		res.getWriter().print(rangeURI);
	}
}
