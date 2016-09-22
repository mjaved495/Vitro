package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.atlas.logging.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class GetPropHierarchyTree extends HttpServlet {
	
	public class PropSerializer implements JsonSerializer<ObjectProperty> {
		@Override
		public JsonElement serialize(ObjectProperty src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.getLocalName());
		}
	}
	
	private List<PropHierarchyNode> getChildren(PropHierarchyNode propNode, ObjectPropertyDao opDao) {
		List<PropHierarchyNode> result = new ArrayList<PropHierarchyNode>();
		List<String> uris = opDao.getSubPropertyURIs(propNode.getProp().getURI());
		for(String uri : uris) {
			result.add(new PropHierarchyNode(propNode.getProp(), opDao.getObjectPropertyByURI(uri)));
		}
		return result;
	}
	
	private PropHierarchyNode addChildrenRecursively(PropHierarchyNode propNode, ObjectPropertyDao opDao) {
		List<PropHierarchyNode> children = getChildren(propNode, opDao);
		if(children.size() == 0) {
			return propNode;
		}
		else {
			for(PropHierarchyNode child : children) {
				propNode.addChild(addChildrenRecursively(child, opDao));
			}
			return propNode;
		}
	}
	
	public List<PropHierarchyNode> treeify(List<PropHierarchyNode> propList, ObjectPropertyDao opDao) {
		List<PropHierarchyNode> tree = new ArrayList<PropHierarchyNode>();
		List<PropHierarchyNode> result = new ArrayList<PropHierarchyNode>();
		for(PropHierarchyNode prop : propList) {
			ObjectProperty op = prop.getProp();
			if(opDao.getSuperPropertyURIs(op.getURI(), false).size() == 0) {
				tree.add(prop);
			}
		}
		for(PropHierarchyNode prop : tree) {
			result.add(addChildrenRecursively(prop, opDao));
		}
		return tree;
	}
	
	public String jsonTree(ObjectProperty root, ObjectPropertyDao opDao) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		gsonBuilder.registerTypeAdapter(ObjectProperty.class, new PropSerializer());
		Gson gson = gsonBuilder.create();
		List<PropHierarchyNode> propList = GetPropHierarchyUtils.generatePropList(opDao);
		List <PropHierarchyNode> tree = treeify(propList, opDao); 
		PropHierarchyNode parent = new PropHierarchyNode("All Properties", "objproperty");
		for(PropHierarchyNode node : tree) {
			parent.addChild(node);
		}
		return gson.toJson(parent);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		VitroRequest request = new VitroRequest(req);
        
        WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
        
        ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
        ObjectProperty op = (ObjectProperty)opDao.getObjectPropertyByURI(request.getParameter("uri"));
		res.getWriter().println(jsonTree(op, opDao));
	}
}

