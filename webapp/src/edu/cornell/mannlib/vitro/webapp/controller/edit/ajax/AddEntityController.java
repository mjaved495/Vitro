package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;

import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class AddEntityController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String name = req.getParameter("name");
		String supertypeURI = req.getParameter("supertype");
		String type = req.getParameter("type");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
		
		if(type.equals("vclass")) {
			VClassDao vcDao = wadf.getVClassDao();
			
			VClass vcl = new VClass();
			vcl.setName(name);
			try {
				vcDao.insertNewVClass(vcl);
			} catch (InsertException e) {
				e.printStackTrace();
			}
			vcDao.addSuperclass(vcl.getURI(), supertypeURI);
			
			res.getWriter().print(vcl.getURI());
		}
		else if(type.equals("objprop")) {
			ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
			
			ObjectProperty op = new ObjectProperty();
			op.setLocalName(name);
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
			dp.setLocalName(name);
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
