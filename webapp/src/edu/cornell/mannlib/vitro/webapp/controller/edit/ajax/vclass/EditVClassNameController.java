package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.vclass;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class EditVClassNameController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String vclassURI = req.getParameter("uri");
		String newClassName = req.getParameter("newClassName");
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
        VClassDao vcwDao = wadf.getVClassDao();
        
        VClass vcl;
		try {
			vcl = (VClass)vcwDao.getVClassByURI(URLDecoder.decode(vclassURI, "UTF-8"));
	        vcl.setName(newClassName);
	        vcwDao.updateVClass(vcl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
		res.getWriter().println("ok");
	}
}
