package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.hp.hpl.jena.ontology.ConversionException;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.GetPropHierarchyTree.PropSerializer;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class GetDatatypePropertyHierarchyTree extends HttpServlet {
	public class PropSerializer implements JsonSerializer<ObjectProperty> {
		@Override
		public JsonElement serialize(ObjectProperty src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.getLocalName());
		}
	}
	
	private List<PropHierarchyNode> getChildren(PropHierarchyNode propNode, DataPropertyDao dpDao) {
		List<PropHierarchyNode> result = new ArrayList<PropHierarchyNode>();
		List<String> uris = dpDao.getSubPropertyURIs(propNode.getProp().getURI());
		for(String uri : uris) {
			result.add(new PropHierarchyNode(dpDao.getDataPropertyByURI(uri)));
		}
		return result;
	}
	
	private PropHierarchyNode addChildrenRecursively(PropHierarchyNode propNode, DataPropertyDao dpDao) {
		List<PropHierarchyNode> children = getChildren(propNode, dpDao);
		if(children.size() == 0) {
			return propNode;
		}
		else {
			for(PropHierarchyNode child : children) {
				propNode.addChild(addChildrenRecursively(child, dpDao));
			}
			return propNode;
		}
	}
	
	public List<PropHierarchyNode> treeify(List<PropHierarchyNode> propList, DataPropertyDao dpDao) {
		List<PropHierarchyNode> tree = new ArrayList<PropHierarchyNode>();
		List<PropHierarchyNode> result = new ArrayList<PropHierarchyNode>();
		for(PropHierarchyNode prop : propList) {
			DataProperty dp = prop.getDataProp();
			List<String> superproperties;
			if(!(dp.getURI().contains("file:///"))) {
				superproperties = dpDao.getAllSuperPropertyURIs(dp.getURI());
			}
			else {
				superproperties = new ArrayList<String>();
			}
			
			if(superproperties.size() == 0) {
				tree.add(prop);
			}
		}
		for(PropHierarchyNode prop : tree) {
			result.add(addChildrenRecursively(prop, dpDao));
		}
		return tree;
	}
	
	public String jsonTree(DataProperty root, DataPropertyDao dpDao) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		gsonBuilder.registerTypeAdapter(ObjectProperty.class, new PropSerializer());
		Gson gson = gsonBuilder.create();
		List<PropHierarchyNode> propList = GetPropHierarchyUtils.generateDataPropList(dpDao);
		List <PropHierarchyNode> tree = treeify(propList, dpDao); 
		PropHierarchyNode parent = new PropHierarchyNode("All Properties");
		for(PropHierarchyNode node : tree) {
			parent.addChild(node);
		}
		return gson.toJson(parent);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		VitroRequest request = new VitroRequest(req);
        
        WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
        
        DataPropertyDao dpDao = wadf.getDataPropertyDao();
        DataProperty dp = (DataProperty)dpDao.getDataPropertyByURI(request.getParameter("uri"));
		res.getWriter().println(jsonTree(dp, dpDao));
	}
}
