package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames.TBOX_ASSERTIONS;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class DeleteRestrictionController extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

		VitroRequest request = new VitroRequest(req);
		
	    try {
			OntModel ontModel = ModelAccess.on(getServletContext())
					.getOntModel(TBOX_ASSERTIONS);
		    
            HashMap epoHash = null;
            EditProcessObject epo = null;
            try {
                epoHash = (HashMap) request.getSession().getAttribute("epoHash");
                epo = (EditProcessObject) epoHash.get(request.getParameter("_epoKey"));
                processDelete(request, ontModel);
            } catch (NullPointerException e) {
                res.getWriter().println("error: nullpointerexception");
            }
		} catch (Exception e) {
	    	res.getWriter().println("error");
	    }
           
	}
	
	private void processDelete(VitroRequest request, OntModel ontModel) {
		String restId = request.getParameter("restrictionId");
        
        if (restId != null) {
            
            OntClass restrictedClass = ontModel.getOntClass( request.getParameter( "classUri" ) );
            
            OntClass rest = null;
            
            for ( Iterator i = restrictedClass.listEquivalentClasses(); i.hasNext(); ) {
                OntClass equivClass = (OntClass) i.next();
                if (equivClass.isAnon() && equivClass.getId().toString().equals(restId)) {
                    rest = equivClass;
                }
            }
            
            if ( rest == null ) { 
                for ( Iterator i = restrictedClass.listSuperClasses(); i.hasNext(); ) {
                    OntClass  superClass = (OntClass) i.next();
                    if (superClass.isAnon() && superClass.getId().toString().equals(restId)) {
                        rest = superClass;
                    }
                }   
            }
            
            /**
             * removing by graph subtraction so that statements with blank nodes
             * stick together and are processed appropriately by the bulk update
             * handler
             */
            if ( rest != null ) {
                Model temp = ModelFactory.createDefaultModel();
                temp.add(rest.listProperties());
                ontModel.getBaseModel().remove(temp);                
            }
            
        }
	}
}
