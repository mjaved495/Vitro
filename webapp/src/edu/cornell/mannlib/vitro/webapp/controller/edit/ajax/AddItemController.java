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

public class AddItemController extends HttpServlet {
	
	private boolean isValid(String uri) {
		boolean result = true;
		result = result && !uri.contains("file:///");
		result = result && uri != null;
		return result;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) {
		String type = req.getParameter("type");
		String relationship = req.getParameter("relationship");
		String itemURI = req.getParameter("itemURI");
		String myURI = req.getParameter("uri");
		
		WebappDaoFactory wadf =  ModelAccess.on(getServletContext()).getWebappDaoFactory();
		
		if(type == null || relationship == null || !isValid(itemURI) || !isValid(myURI)) {
			return;
		}
		
		if(type.equals("dataprop")) {
			
			DataPropertyDao dpDao = wadf.getDataPropertyDao();
			
			if(relationship.equals("super")) {
				dpDao.addSuperproperty(myURI, itemURI);
			}
			else if(relationship.equals("sub")) {
				dpDao.addSubproperty(myURI, itemURI);
			}
			else if(relationship.equals("eq")) {
				dpDao.addEquivalentProperty(myURI, itemURI);
			}
			else if(relationship.equals("domain")) {
				DataProperty dp = dpDao.getDataPropertyByURI(myURI);
				dp.setDomainClassURI(itemURI);
				dpDao.updateDataProperty(dp);
			}
			else if(relationship.equals("range")) {
				DataProperty dp = dpDao.getDataPropertyByURI(myURI);
				dp.setRangeDatatypeURI(itemURI);
				dpDao.updateDataProperty(dp);
			}
		}
		else if(type.equals("objprop")) {
			
			ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
			
			if(relationship.equals("super")) {
				opDao.addSuperproperty(myURI, itemURI);
			}
			else if(relationship.equals("sub")) {
				opDao.addSubproperty(myURI, itemURI);
			}
			else if(relationship.equals("eq")) {
				opDao.addEquivalentProperty(myURI, itemURI);
			}
			else if(relationship.equals("inverse")) {
				ObjectProperty op = opDao.getObjectPropertyByURI(myURI);
				op.setURIInverse(itemURI);
		        opDao.updateObjectProperty(op);
			}
			else if(relationship.equals("domain")) {
				ObjectProperty op = opDao.getObjectPropertyByURI(myURI);
				op.setDomainVClassURI(itemURI);
				opDao.updateObjectProperty(op);
			}
			else if(relationship.equals("range")) {
				ObjectProperty op = opDao.getObjectPropertyByURI(myURI);
				op.setRangeVClassURI(itemURI);
				opDao.updateObjectProperty(op);
			}
		}
		else if(type.equals("vclass")) {
			
			VClassDao vcDao = wadf.getVClassDao();
			
			if(relationship.equals("super")) {
				vcDao.addSuperclass(myURI, itemURI);
			}
			else if(relationship.equals("sub")) {
				vcDao.addSubclass(myURI, itemURI);
			}
			else if(relationship.equals("eq")) {
				vcDao.addEquivalentClass(myURI, itemURI);
			}
			else if(relationship.equals("disjoint")) {
				vcDao.addDisjointWithClass(myURI, itemURI);
			}
			
		}
	}
}
