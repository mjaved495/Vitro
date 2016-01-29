package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;

public class GetPropHierarchyUtils {
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
	
	public static PropHierarchyNode generateFullTree(ObjectProperty root, ObjectPropertyDao opDao) {
		List<ObjectProperty> superproperties = getSuperproperties(opDao, root);
		PropHierarchyNode currentNode = new PropHierarchyNode(root);
		while(superproperties.size() > 0) {
			currentNode = new PropHierarchyNode(superproperties.get(0));
			superproperties = getSuperproperties(opDao, currentNode.getProp());
		}
		return generateSubTree(currentNode.getProp(), opDao);
	}
	
	private static PropHierarchyNode generateSubTree(ObjectProperty root, ObjectPropertyDao opDao) {
		List<ObjectProperty> subproperties = getSuperproperties(opDao, root);
		PropHierarchyNode currentNode = new PropHierarchyNode(root);
		for(ObjectProperty subproperty: subproperties) {
			currentNode.addChild(generateSubTree(subproperty, opDao));
		}
		return currentNode;
	}
	
	public static String jsonTree(ObjectProperty root, ObjectPropertyDao opDao) {
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
		PropHierarchyNode tree = generateFullTree(root, opDao);
		return gson.toJson(tree);
	}
	
}
