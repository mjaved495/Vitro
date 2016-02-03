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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
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
	
	public String jsonTree(ObjectProperty root, ObjectPropertyDao opDao) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		gsonBuilder.registerTypeAdapter(ObjectProperty.class, new PropSerializer());
		Gson gson = gsonBuilder.create();
		//PropHierarchyNode tree = GetPropHierarchyUtils.generateFullTree(root, opDao);
		//return gson.toJson(tree);
		return "";
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
