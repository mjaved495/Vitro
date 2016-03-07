package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax.dataproperty;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.ReasoningOption.ASSERTIONS_ONLY;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DataPropertyInfoController extends HttpServlet {
	public void doGet (HttpServletRequest req, HttpServletResponse response) throws IOException {
		
		Hashtable<String, Object> responseObject = new Hashtable<String, Object>();
		
		VitroRequest request = new VitroRequest(req);
        
        WebappDaoFactory wadf = ModelAccess.on(getServletContext()).getWebappDaoFactory(ASSERTIONS_ONLY);
        
        DataPropertyDao dpDao = wadf.getDataPropertyDao();
        DataProperty dp = (DataProperty)dpDao.getDataPropertyByURI(URLDecoder.decode(request.getParameter("uri"), "UTF-8"));
        
        VClassDao vcDao = wadf.getVClassDao();
      
        Ontology ont = null;
        
        if(dp.getNamespace() != null) {
        	ont = wadf.getOntologyDao().getOntologyByURI(dp.getNamespace());
        }
        
        if(ont == null) {
        	responseObject.put("ontology", "(unspecified)");
        }
        else {
        	responseObject.put("ontology", ont.getName());
        }
       
        String hiddenFromDisplay = (dp.getHiddenFromDisplayBelowRoleLevel() == null ? "(unspecified)"
				: dp.getHiddenFromDisplayBelowRoleLevel().getDisplayLabel());
		String prohibitedFromUpdate = (dp
				.getProhibitedFromUpdateBelowRoleLevel() == null ? "(unspecified)"
				: dp.getProhibitedFromUpdateBelowRoleLevel().getUpdateLabel());
		String hiddenFromPublish = (dp.getHiddenFromPublishBelowRoleLevel() == null ? "(unspecified)"
				: dp.getHiddenFromPublishBelowRoleLevel().getDisplayLabel());
		
		responseObject.put("displayLevel", hiddenFromDisplay);
		responseObject.put("updateLevel", prohibitedFromUpdate);
		responseObject.put("publishLevel", hiddenFromPublish);
		
        List<DataProperty> superproperties = getPropsForURIList(
                dpDao.getAllSuperPropertyURIs(dp.getURI()), dpDao);
        
        List<Hashtable<String, String>> superpropertiesInfo = new ArrayList<Hashtable<String, String>>();
        for(DataProperty prop : superproperties) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", prop.getURI());
        	info.put("name", prop.getLabel());
        	superpropertiesInfo.add(info);
        }
        
        responseObject.put("superproperties", superpropertiesInfo);

        List<DataProperty> subproperties = getPropsForURIList(
                dpDao.getSubPropertyURIs(dp.getURI()), dpDao);
        
        List<Hashtable<String, String>> subpropertiesInfo = new ArrayList<Hashtable<String, String>>();
        for(DataProperty prop: subproperties) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", prop.getURI());
        	info.put("name", prop.getLabel());
        	subpropertiesInfo.add(info);
        }
        
        responseObject.put("subproperties", subpropertiesInfo);
        
        List<DataProperty> eqprops = getPropsForURIList(
                dpDao.getEquivalentPropertyURIs(dp.getURI()), dpDao);
        
        List<Hashtable<String, String>> eqpropsInfo = new ArrayList<Hashtable<String, String>>();
        for(DataProperty prop : eqprops) {
        	Hashtable<String, String> info = new Hashtable<String, String>();
        	info.put("uri", prop.getURI());
        	info.put("name", prop.getLabel());
        	eqpropsInfo.add(info);
        }
        
        responseObject.put("eqprops", eqpropsInfo);
        
        // attributes of the property
        
        responseObject.put("functional", dp.getFunctional());
        
        VClass domain = vcDao.getVClassByURI(dp.getDomainVClassURI());
        VClass range = vcDao.getVClassByURI(dp.getRangeVClassURI());
        
        Hashtable<String, String> domainInfo = new Hashtable<String, String>();
        if(domain == null) {
        	domainInfo.put("uri", "");
        	domainInfo.put("name", "");
        }
        else {
        	 domainInfo.put("uri", domain.getURI());
             domainInfo.put("name", domain.getName());
        }
        
        responseObject.put("domain", domainInfo);
        
        Hashtable<String, String> rangeInfo = new Hashtable<String, String>();
        if(range == null) {
        	rangeInfo.put("uri", "");
        	rangeInfo.put("name", "");
        }
        else {
        	rangeInfo.put("uri", range.getURI());
            rangeInfo.put("name", range.getName());
        }
        
        
        responseObject.put("range", rangeInfo);

        responseObject.put("label", dp.getLabel());
        
        Gson gson = new Gson();
        response.getWriter().println(gson.toJson(responseObject));

	}
	
	private static List<DataProperty> getPropsForURIList(List<String> propURIs, DataPropertyDao dpDao) {
        List<DataProperty> props = new ArrayList<DataProperty>();
        Iterator<String> urIt = propURIs.iterator();
        while (urIt.hasNext()) {
            String propURI = urIt.next();
            DataProperty dp = dpDao.getDataPropertyByURI(propURI);
            if (dp != null) {
                props.add(dp);
            }
        }
        return props;
    }
}
