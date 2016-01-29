package edu.cornell.mannlib.vitro.webapp.controller.edit;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class ObjectPropertyPageController extends BaseEditController {

	private static final Log log = LogFactory.getLog(ClassPageController.class.getName());
	
	public void doPost (HttpServletRequest req, HttpServletResponse response) throws IOException {
		if(req.getParameter("uri") == null) {
			response.getWriter().println("");
			return;
		}
		
		VitroRequest request = new VitroRequest(req);
		
		WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
		
		ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
		ObjectProperty op = (ObjectProperty) opDao.getObjectPropertyByURI(request.getParameter("uri"));
		
		request.setAttribute("objectProperty", op);
		
		if(op.getNamespace() != null) {
        	 Ontology ont = wadf.getOntologyDao().getOntologyByURI(op.getNamespace());
             
             request.setAttribute("ontology",  ont);
             request.setAttribute("allClasses", ont.getVClassesList());
             request.setAttribute("allProperties", ont.getPropsList());
		}
		
		List<Object> superproperties = new ArrayList<Object>();
		List<Object> siblings = new ArrayList<Object>();
		List<Object> subproperties = new ArrayList<Object>();
		List<Object> inverses = new ArrayList<Object>();
		
		List<Object> domains = new ArrayList<Object>();
		List<Object> ranges = new ArrayList<Object>();
		
		request.setAttribute("superproperties", superproperties);
		request.setAttribute("siblings", siblings);
		request.setAttribute("subproperties", subproperties);
		request.setAttribute("inverses", inverses);
		
		request.setAttribute("domains", domains);
		request.setAttribute("ranges", ranges);
		
		String blankJsp = "/templates/edit/blank.jsp";
		
		RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp", blankJsp);
        request.setAttribute("formJsp","/templates/edit/specific/object_property.jsp");
        request.setAttribute("colspan","4");
        request.setAttribute("scripts","/templates/edit/js/object_property.js");
        request.setAttribute("title","Object Property Editing Form");
		
        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("ObjectPropertyPageController could not forward to view.", e);
            throw new RuntimeException(e);
        }
	}
	
	public void doGet (HttpServletRequest req, HttpServletResponse response) throws IOException {
		doPost(req, response);
	}
	
	private static List<ObjectProperty> getPropsForURIList(List<String> propURIs, ObjectPropertyDao opDao) {
        List<ObjectProperty> props = new ArrayList<ObjectProperty>();
        Iterator<String> urIt = propURIs.iterator();
        while (urIt.hasNext()) {
            String propURI = urIt.next();
            ObjectProperty op = opDao.getObjectPropertyByURI(propURI);
            if (op != null) {
                props.add(op);
            }
        }
        return props;
    }
	
	private static List<ObjectProperty> getSubproperties(ObjectPropertyDao opDao, ObjectProperty root) {
		return getPropsForURIList(
                opDao.getSubPropertyURIs(root.getURI()), opDao);
	}
	
	private static List<ObjectProperty> getSuperproperties(ObjectPropertyDao opDao, ObjectProperty root) {
		return getPropsForURIList(
				opDao.getSuperPropertyURIs(root.getURI(), false), opDao);
	}
	
	public List<ObjectProperty> dfsTraversal(ObjectPropertyDao opDao, ObjectProperty root) {
		// preorder traversal (depth-first search)
		List<ObjectProperty> result = new ArrayList<ObjectProperty>();
		if(getSubproperties(opDao, root).size() == 0) {
			result.add(root);
			return result;
		}
		else {
			for(ObjectProperty subproperty : getSubproperties(opDao, root)) {
				result.addAll(dfsTraversal(opDao, subproperty));
			}
			return result;
		}
	}
	
	public List<ObjectProperty> getVClassesInOntology(ObjectPropertyDao opDao, ObjectProperty op) {
		// travel up in class hierarchy tree until reaching root
		
		ObjectProperty currentProp = op;
		List<ObjectProperty> superproperties = getSuperproperties(opDao, currentProp);
		while(superproperties.size() > 0) {
			currentProp = superproperties.get(0);
			superproperties = getSuperproperties(opDao, currentProp);
		}
		
		// now at root
		// preorder traversal (depth-first search)
		
		return dfsTraversal(opDao, currentProp);
	}
}
