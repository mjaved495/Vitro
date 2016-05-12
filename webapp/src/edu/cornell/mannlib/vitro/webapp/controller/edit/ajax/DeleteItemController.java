package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DeleteItemController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String type = req.getParameter("type");
		String relationship = req.getParameter("relationship");
		String itemURI = req.getParameter("itemURI");
		String myURI = req.getParameter("uri");
		
		WebappDaoFactory wadf =  ModelAccess.on(getServletContext()).getWebappDaoFactory();
		
		if(type == null || relationship == null || itemURI == null || myURI == null) {
			return;
		}
		
		if(type.equals("dataprop")) {
			DataPropertyDao dpDao = wadf.getDataPropertyDao();
			
			if(relationship.equals("super")) {
				dpDao.removeSuperproperty(myURI, itemURI);
			}
			else if(relationship.equals("sub")) {
				dpDao.removeSubproperty(myURI, itemURI);
			}
			else if(relationship.equals("eq")) {
				dpDao.removeEquivalentProperty(myURI, itemURI);
			}
			else if(relationship.equals("domain")) {
				DataProperty dp = dpDao.getDataPropertyByURI(myURI);
				dp.setDomainClassURI("");
				dpDao.updateDataProperty(dp);
			}
			else if(relationship.equals("range")) {
				DataProperty dp = dpDao.getDataPropertyByURI(myURI);
				dp.setRangeDatatypeURI("");
				dpDao.updateDataProperty(dp);
			}
		}
		else if(type.equals("objprop")) {
			ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
			
			if(relationship.equals("super")) {
				opDao.removeSuperproperty(myURI, itemURI);
			}
			else if(relationship.equals("sub")) {
				opDao.removeSubproperty(myURI, itemURI);
			}
			else if(relationship.equals("eq")) {
				opDao.removeEquivalentProperty(myURI, itemURI);
			}
			else if(relationship.equals("domain")) {
				ObjectProperty op = opDao.getObjectPropertyByURI(myURI);
				op.setDomainVClassURI("");
				opDao.updateObjectProperty(op);
			}
			else if(relationship.equals("range")) {
				ObjectProperty op = opDao.getObjectPropertyByURI(myURI);
				op.setRangeVClassURI("");
				opDao.updateObjectProperty(op);
			}
		}
		else if(type.equals("vclass")) {
			VClassDao vcDao = wadf.getVClassDao();
			
			if(relationship.equals("super")) {
				vcDao.removeSuperclass(myURI, itemURI);
			}
			else if(relationship.equals("sub")) {
				vcDao.removeSubclass(myURI, itemURI);
			}
			else if(relationship.equals("eq")) {
				vcDao.removeEquivalentClass(myURI, itemURI);
			}
			else if(relationship.equals("disjoint")) {
				vcDao.removeDisjointWithClass(myURI, itemURI);
			}
		}
		
	}
}
