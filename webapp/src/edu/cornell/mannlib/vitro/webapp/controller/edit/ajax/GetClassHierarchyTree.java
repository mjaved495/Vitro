package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class GetClassHierarchyTree extends HttpServlet {
	
	private List<VClass> getVClassesForURIList(List<String> vclassURIs, VClassDao vcDao) {
        List<VClass> vclasses = new ArrayList<VClass>();
        Iterator<String> urIt = vclassURIs.iterator();
        while (urIt.hasNext()) {
            String vclassURI = urIt.next();
            VClass vclass = vcDao.getVClassByURI(vclassURI);
            if (vclass != null) {
                vclasses.add(vclass);
            }
        }
        return vclasses;
    }
	
	private List<VClass> getSubclasses(VClassDao vcDao, VClass root) {
		return getVClassesForURIList(
                vcDao.getSubClassURIs(root.getURI()), vcDao);
	}
	
	private ClassHierarchyNode generateTree(VClass root, VClassDao vcDao) {
		List<VClass> subclasses = getSubclasses(vcDao, root);
		ClassHierarchyNode currentNode = new ClassHierarchyNode(root);
		for(VClass subclass : subclasses) {
			currentNode.addChild(generateTree(subclass, vcDao));
		}
		return currentNode;
	}
	
	private String jsonTree(VClass root, VClassDao vcDao) {
		Gson gson = new Gson();
		return gson.toJson(generateTree(root, vcDao));
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		VitroRequest request = new VitroRequest(req);
        
        WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
        
        VClassDao vcwDao = wadf.getVClassDao();
        VClass vcl = (VClass)vcwDao.getVClassByURI(request.getParameter("uri"));
		res.getWriter().println(jsonTree(vcl, vcwDao));
	}
}
