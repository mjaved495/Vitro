package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.objproperty;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class ObjectPropertyInfoController extends HttpServlet {
	public void doGet (HttpServletRequest req, HttpServletResponse response) throws IOException {
		Hashtable<String, Object> responseObject = new Hashtable<String, Object>();
		
		VitroRequest request = new VitroRequest(req);
        
        // get the VClass
        
        WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
        
        ObjectPropertyDao opDao = wadf.getObjectPropertyDao();
        ObjectProperty op = (ObjectProperty)opDao.getObjectPropertyByURI(URLDecoder.decode(request.getParameter("uri"), "UTF-8"));
        
        VClassDao vcDao = wadf.getVClassDao();
        
        ObjectPropertyDao opwDao = wadf.getObjectPropertyDao();
        ObjectPropertyDao displayOpDao = wadf.getObjectPropertyDao();
      
        Ontology ont = null;
        
        if(op.getNamespace() != null) {
        	ont = wadf.getOntologyDao().getOntologyByURI(op.getNamespace());
        }
        
        if(ont == null) {
        	responseObject.put("ontology", "(unspecified)");
        }
        else {
        	responseObject.put("ontology", ont.getName());
        }
       
        String hiddenFromDisplay = (op.getHiddenFromDisplayBelowRoleLevel() == null ? "(unspecified)"
				: op.getHiddenFromDisplayBelowRoleLevel().getDisplayLabel());
		String prohibitedFromUpdate = (op
				.getProhibitedFromUpdateBelowRoleLevel() == null ? "(unspecified)"
				: op.getProhibitedFromUpdateBelowRoleLevel().getUpdateLabel());
		String hiddenFromPublish = (op.getHiddenFromPublishBelowRoleLevel() == null ? "(unspecified)"
				: op.getHiddenFromPublishBelowRoleLevel().getDisplayLabel());
		
		responseObject.put("displayLevel", hiddenFromDisplay);
		responseObject.put("updateLevel", prohibitedFromUpdate);
		responseObject.put("publishLevel", hiddenFromPublish);
		
        /*String group = (op.getGroup() == null ? "(unspecified)" : vcl.getGroup().getPublicName());
        
        responseObject.put("group", group);*/
        
        List<ObjectProperty> superproperties = getPropsForURIList(
                opDao.getAllSuperPropertyURIs(op.getURI()), displayOpDao);
        
        List<Hashtable<String, String>> superpropertiesInfo = new ArrayList<Hashtable<String, String>>();
        for(ObjectProperty prop : superproperties) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", prop.getURI());
        	info.put("name", prop.getLabel());
        	superpropertiesInfo.add(info);
        }
        
        responseObject.put("superproperties", superpropertiesInfo);

        List<ObjectProperty> subproperties = getPropsForURIList(
                opDao.getSubPropertyURIs(op.getURI()), displayOpDao);
        
        List<Hashtable<String, String>> subpropertiesInfo = new ArrayList<Hashtable<String, String>>();
        for(ObjectProperty prop: subproperties) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", prop.getURI());
        	info.put("name", prop.getLabel());
        	subpropertiesInfo.add(info);
        }
        
        responseObject.put("subproperties", subpropertiesInfo);
        
        List<ObjectProperty> eqprops = getPropsForURIList(
                opDao.getEquivalentPropertyURIs(op.getURI()), displayOpDao);
        
        List<Hashtable<String, String>> eqpropsInfo = new ArrayList<Hashtable<String, String>>();
        for(ObjectProperty prop : eqprops) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", prop.getURI());
        	info.put("name", prop.getLabel());
        	eqpropsInfo.add(info);
        }
        
        responseObject.put("eqprops", eqpropsInfo);
        
        ObjectProperty inverse = opDao.getObjectPropertyByURI(op.getURIInverse());
        
        Hashtable<String, String> inverseInfo = new Hashtable<String, String>();
        if(inverse != null) {
        	inverseInfo.put("uri", inverse.getURI());
        	inverseInfo.put("name", inverse.getLabel());
        }
        else {
        	inverseInfo.put("uri", "");
            inverseInfo.put("name", "");
        }
        
        responseObject.put("inverse", inverseInfo);
        
        // attributes of the property
        
        responseObject.put("transitive", op.getTransitive());
        responseObject.put("symmetric", op.getSymmetric());
        responseObject.put("functional", op.getFunctional());
        responseObject.put("inverseFunctional", op.getInverseFunctional());
        
        VClass domain = vcDao.getVClassByURI(op.getDomainVClassURI());
        VClass range = vcDao.getVClassByURI(op.getRangeVClassURI());
        
        Hashtable<String, String> domainInfo = new Hashtable<String, String>();
        if(domain != null) {
        	domainInfo.put("uri", domain.getURI());
            domainInfo.put("name", domain.getName());
        }
        else {
        	domainInfo.put("uri", "");
            domainInfo.put("name", "");
        }
        
        responseObject.put("domain", domainInfo);
        
        Hashtable<String, String> rangeInfo = new Hashtable<String, String>();
        if(range != null) {
        	rangeInfo.put("uri", range.getURI());
            rangeInfo.put("name", range.getName());
        }
        else {
        	rangeInfo.put("uri", "");
        	rangeInfo.put("name", "");
        }
        
        responseObject.put("range", rangeInfo);

        responseObject.put("label", op.getLabel());
        
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(responseObject));

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
}
