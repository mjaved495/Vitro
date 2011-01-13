/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.controller;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.Classes2ClassesDao;
import edu.cornell.mannlib.vitro.webapp.dao.OntologyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.web.ContentType;



public class EntityURLController extends VitroHttpServlet {
	 private static final Log log = LogFactory.getLog(EntityURLController.class.getName());
	 
public void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
	 
	String url = req.getRequestURI().substring(req.getContextPath().length());
	ContentType contentType = checkForRequestType(req.getHeader("accept"));
	
	if(Pattern.compile("^/entityurl/$").matcher(url).matches()){
		String redirectURL = null;
		if ( RDFXML_MIMETYPE.equals(contentType.getMediaType()))
			redirectURL = "/entityurl/entityurl.rdf";
		else if( N3_MIMETYPE.equals(contentType.getMediaType()))
			redirectURL = "/entityurl/entityurl.n3";
	    else if ( TTL_MIMETYPE.equals(contentType.getMediaType()))
	    	redirectURL = "/entityurl/entityurl.ttl";
		
		 String hn = req.getHeader("Host");
	        if (req.isSecure()) {
	            res.setHeader("Location", res.encodeURL("https://" + hn
	                    + req.getContextPath() + redirectURL));
	            log.info("doRedirect by using HTTPS");
	        } else {
	            res.setHeader("Location", res.encodeURL("http://" + hn
	                    + req.getContextPath() + redirectURL));
	            log.info("doRedirect by using HTTP");
	        }
	       res.setStatus(res.SC_SEE_OTHER);
		return;
	}
	
	List<Individual> inds = (List<Individual>)getServletContext().getAttribute("inds");
	Model model = ModelFactory.createDefaultModel();
	if(inds != null){
		System.out.println("Into the loop");
		Iterator<Individual> itr = (Iterator<Individual>)inds.iterator();
		Individual ind = null;
		Resource resource = null;
		while(itr.hasNext()){
			ind = itr.next();
			resource = ResourceFactory.createResource(ind.getURI());
			RDFNode node = (RDFNode) ResourceFactory.createResource((String) getServletContext().getAttribute("classuri"));
			model.add(resource, RDF.type, node);
		}
	}
	
	if(contentType != null){
		String format = ""; 
		if ( RDFXML_MIMETYPE.equals(contentType.getMediaType()))
			format = "RDF/XML";
		else if( N3_MIMETYPE.equals(contentType.getMediaType()))
			format = "N3";
		else if ( TTL_MIMETYPE.equals(contentType.getMediaType()))
			format ="TTL";
		res.setContentType(contentType.getMediaType());
		model.write(res.getOutputStream(), format);
	}
}
public void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
	doGet(req,res);
}

protected ContentType checkForRequestType(String acceptHeader) {		
	try {
		//check the accept header			
		if (acceptHeader != null) {
			List<ContentType> actualContentTypes = new ArrayList<ContentType>();				
			actualContentTypes.add(new ContentType( XHTML_MIMETYPE ));
			actualContentTypes.add(new ContentType( HTML_MIMETYPE ));				
			
			actualContentTypes.add(new ContentType( RDFXML_MIMETYPE ));
			actualContentTypes.add(new ContentType( N3_MIMETYPE ));
			actualContentTypes.add(new ContentType( TTL_MIMETYPE ));
			
							
			ContentType best = ContentType.getBestContentType(acceptHeader,actualContentTypes);
			if (best!=null && (
					RDFXML_MIMETYPE.equals(best.getMediaType()) || 
					N3_MIMETYPE.equals(best.getMediaType()) ||
					TTL_MIMETYPE.equals(best.getMediaType()) ))
				return best;				
		}
	}
	catch (Throwable th) {
		log.error("problem while checking accept header " , th);
	}
	return null;
}
}
