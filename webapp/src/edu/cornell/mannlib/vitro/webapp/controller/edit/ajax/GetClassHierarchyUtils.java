package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.edit.ClassPageController;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class GetClassHierarchyUtils {
	
	private static final Log log = LogFactory.getLog(GetClassHierarchyUtils.class.getName());
	
	private static List<VClass> getVClassesForURIList(List<String> vclassURIs, VClassDao vcDao) {
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
	
	private static List<VClass> getSubclasses(VClassDao vcDao, VClass root) {
		return getVClassesForURIList(
                vcDao.getSubClassURIs(root.getURI()), vcDao);
	}
	
	private static List<VClass> getSuperclasses(VClassDao vcDao, VClass root) {
		return getVClassesForURIList(
				vcDao.getSuperClassURIs(root.getURI(), false), vcDao);
	}
	
	public static ClassHierarchyNode generateFullTree(VClass startPoint, VClassDao vcDao) {
		VClass thing = vcDao.getTopConcept();
		ClassHierarchyNode fullTree = generateSubTree(null, thing, vcDao);
		List<VClass> allClasses = vcDao.getAllVclasses();
		for(VClass vcl : allClasses) {
			if(vcDao.getSuperClassURIs(vcl.getURI(), false).size() == 0 && !fullTree.children().contains(vcl)) {
				fullTree.addChild(generateSubTree(null, vcl, vcDao));
			}
		}
		return fullTree;
		
	}
	
	private static ClassHierarchyNode generateSubTree(VClass superclass, VClass root, VClassDao vcDao) {
		List<VClass> subclasses = getSubclasses(vcDao, root);
		ClassHierarchyNode currentNode = new ClassHierarchyNode(superclass, root);
		for(VClass subclass : subclasses) {
			currentNode.addChild(generateSubTree(root, subclass, vcDao));
		}
		return currentNode;
	}
	
	public static String jsonTree(VClass root, VClassDao vcDao) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes fieldAttributes) {
				return fieldAttributes.getName().equals("parent");
			}
			@Override 
			public boolean shouldSkipClass(Class<?> myClass) {
				return false;
			}
		});
		Gson gson = gsonBuilder.create();
		ClassHierarchyNode tree = generateFullTree(root, vcDao);
		return gson.toJson(tree);
	}
	
}