package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.apache.jena.atlas.logging.Log;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.edit.ClassPageController;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;

import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class AddEntityController extends HttpServlet {
	private static final org.apache.commons.logging.Log log = LogFactory.getLog(AddEntityController.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String uri = req.getParameter("uri");
		String supertypeURI = req.getParameter("supertype");
		String type = req.getParameter("type");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
		
		if(type.equals("vclass")) {
			VClassDao vcDao = wadf.getVClassDao();
			
			VClass vcl = new VClass(uri);
			vcl.setName(vcl.getLocalName());
			
			try {
				vcDao.insertNewVClass(vcl);
				log.info(vcDao.getVClassByURI(uri));
				log.info(vcl.getURI());
				vcDao.addSuperclass(vcl.getURI(), supertypeURI);
				vcDao.addSubclass(supertypeURI, vcl.getURI());
				log.info(vcDao.getAllSuperClassURIs(vcl.getURI()));
			} catch (InsertException e) {
				e.printStackTrace();
			}
			
			res.getWriter().print(vcl.getURI());
		}
		else if(type.equals("objprop")) {
			ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
			
			ObjectProperty op = new ObjectProperty();
			op.setURI(uri);
			op.setLabel(op.getLocalName());
			try {
				opDao.insertObjectProperty(op);
			}
			catch(InsertException e) {
				e.printStackTrace();
			}
			opDao.addSuperproperty(op.getURI(), supertypeURI);
		}
		else if(type.equals("dataprop")) {
			DataPropertyDao dpDao = wadf.getDataPropertyDao();
			
			DataProperty dp = new DataProperty();
			dp.setURI(uri);
			dp.setLabel(dp.getLocalName());
			try {
				dpDao.insertDataProperty(dp);
			}
			catch(InsertException e) {
				e.printStackTrace();
			}
			dpDao.addSuperproperty(dp.getURI(), supertypeURI);
		}
	}
}
