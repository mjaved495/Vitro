package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.vclass;

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
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class VClassInfoController extends HttpServlet {
	public void doGet (HttpServletRequest req, HttpServletResponse response) throws IOException {
		Hashtable<String, Object> responseObject = new Hashtable<String, Object>();
		
		VitroRequest request = new VitroRequest(req);
        
        // get the VClass
        
        WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
        
        VClassDao vcwDao = wadf.getVClassDao();
        VClass vcl = (VClass)vcwDao.getVClassByURI(URLDecoder.decode(request.getParameter("uri"), "UTF-8"));
        
        if (vcl == null) {
        	vcl = request.getUnfilteredWebappDaoFactory()
        	        .getVClassDao().getTopConcept();
        }
        
        VClassDao vcDao = wadf.getVClassDao();
        VClassDao displayVcDao = wadf.getVClassDao();
      
        Ontology ont = null;
        
        if(vcl.getNamespace() != null) {
        	ont = wadf.getOntologyDao().getOntologyByURI(vcl.getNamespace());
        }
        
        if(ont == null) {
        	responseObject.put("ontology", "(unspecified)");
        }
        else {
        	responseObject.put("ontology", ont.getName());
        }
       
        String hiddenFromDisplay = (vcl.getHiddenFromDisplayBelowRoleLevel() == null ? "(unspecified)"
				: vcl.getHiddenFromDisplayBelowRoleLevel().getDisplayLabel());
		String prohibitedFromUpdate = (vcl
				.getProhibitedFromUpdateBelowRoleLevel() == null ? "(unspecified)"
				: vcl.getProhibitedFromUpdateBelowRoleLevel().getUpdateLabel());
		String hiddenFromPublish = (vcl.getHiddenFromPublishBelowRoleLevel() == null ? "(unspecified)"
				: vcl.getHiddenFromPublishBelowRoleLevel().getDisplayLabel());
		
		responseObject.put("displayLevel", hiddenFromDisplay);
		responseObject.put("updateLevel", prohibitedFromUpdate);
		responseObject.put("publishLevel", hiddenFromPublish);
		
        String group = (vcl.getGroup() == null ? "(unspecified)" : vcl.getGroup().getPublicName());
        
        responseObject.put("group", group);
        
        List<VClass> superVClasses = getVClassesForURIList(
                vcDao.getSuperClassURIs(vcl.getURI(),false), displayVcDao);
        
        List<Hashtable<String, String>> superclassesInfo = new ArrayList<Hashtable<String, String>>();
        for(VClass superclass : superVClasses) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", superclass.getURI());
        	info.put("name", superclass.getName());
        	superclassesInfo.add(info);
        }
        
        responseObject.put("superclasses", superclassesInfo);

        List<VClass> subVClasses = getVClassesForURIList(
                vcDao.getSubClassURIs(vcl.getURI()), displayVcDao);
        
        List<Hashtable<String, String>> subclassesInfo = new ArrayList<Hashtable<String, String>>();
        for(VClass subclass : subVClasses) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", subclass.getURI());
        	info.put("name", subclass.getName());
        	subclassesInfo.add(info);
        }
        
        responseObject.put("subclasses", subclassesInfo);
        
        List<VClass> eqVClasses = getVClassesForURIList(
                vcDao.getEquivalentClassURIs(vcl.getURI()), displayVcDao);
        
        List<Hashtable<String, String>> eqclassesInfo = new ArrayList<Hashtable<String, String>>();
        for(VClass eqclass : eqVClasses) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", eqclass.getURI());
        	info.put("name", eqclass.getName());
        	eqclassesInfo.add(info);
        }
        
        responseObject.put("eqclasses", eqclassesInfo);
        
        List<VClass> djVClasses = getVClassesForURIList(
                vcDao.getDisjointWithClassURIs(vcl.getURI()), displayVcDao);

        List<Hashtable<String, String>> disjointsInfo = new ArrayList<Hashtable<String, String>>();
        for(VClass disjoint : djVClasses) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", disjoint.getURI());
        	info.put("name", disjoint.getName());
        	disjointsInfo.add(info);
        }
        
        responseObject.put("disjoints", disjointsInfo);
        
        responseObject.put("label", vcl.getName());
        
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(responseObject));

	}
	
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
}
