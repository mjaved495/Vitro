package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.vclass;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

@SuppressWarnings("serial")
public class EditVClassLabelController extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String label = req.getParameter("label");
			/* WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory();
			VClassDao vcwDao = wadf.getVClassDao();
			try {
				VClass vclassForEditing = (VClass)vcwDao.getVClassByURI(req.getParameter("uri"));
				vclassForEditing.setName(label);
				try {
					res.getWriter().print(label);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			catch(NullPointerException e1) {
				try {
					res.sendError(400);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} */
	}
}
