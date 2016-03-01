package edu.cornell.mannlib.vitro.webapp.controller.edit.ajax;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelNames.TBOX_ASSERTIONS;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;

public class AddRestrictionController extends HttpServlet {
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
                processCreate(request, epo, ontModel);
            } catch (NullPointerException e) {
                res.getWriter().println("error: nullpointerexception");
            }
		} catch (Exception e) {
	    	res.getWriter().println("error");
	    }
           
	}
	
	private void processCreate(VitroRequest request, EditProcessObject epo, OntModel origModel) {
		Model temp = ModelFactory.createDefaultModel();
        Model dynamicUnion = ModelFactory.createUnion(temp, origModel);
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dynamicUnion);
        
        OntProperty onProperty = ontModel.getOntProperty( (String) request.getParameter("onProperty") );
        
        String conditionTypeStr = request.getParameter("conditionType");
        
        String restrictionTypeStr = (String) epo.getAttribute("restrictionType");
        Restriction rest = null;
        
        OntClass ontClass = ontModel.getOntClass( (String) epo.getAttribute("VClassURI") );
        
        String roleFillerURIStr = request.getParameter("ValueClass");
        Resource roleFiller = null;
        if (roleFillerURIStr != null) {
            roleFiller = ontModel.getResource(roleFillerURIStr);
        }               
        
        int cardinality = -1;
        String cardinalityStr = request.getParameter("cardinality");
        if (cardinalityStr != null) {
            cardinality = Integer.decode(cardinalityStr); 
        }
        
        if (restrictionTypeStr.equals("allValuesFrom")) {
            rest = ontModel.createAllValuesFromRestriction(null,onProperty,roleFiller);
        } else if (restrictionTypeStr.equals("someValuesFrom")) {
            rest = ontModel.createSomeValuesFromRestriction(null,onProperty,roleFiller);
        } else if (restrictionTypeStr.equals("hasValue")) {
            String valueURI = request.getParameter("ValueIndividual");
            if (valueURI != null) {
                Resource valueRes = ontModel.getResource(valueURI);
                if (valueRes != null) {
                    rest = ontModel.createHasValueRestriction(null, onProperty, valueRes);
                }
            } else {
                String valueLexicalForm = request.getParameter("ValueLexicalForm");
                if (valueLexicalForm != null) {
                    String valueDatatype = request.getParameter("ValueDatatype");
                    Literal value = null;
                    if (valueDatatype != null && valueDatatype.length() > 0) {
                        RDFDatatype dtype = null;
                        try {
                            dtype = TypeMapper.getInstance().getSafeTypeByName(valueDatatype);
                        } catch (Exception e) {
                            //log.warn ("Unable to get safe type " + valueDatatype + " using TypeMapper");
                        }
                        if (dtype != null) {
                            value = ontModel.createTypedLiteral(valueLexicalForm, dtype);
                        } else {
                            value = ontModel.createLiteral(valueLexicalForm);
                        }
                    } else {
                        value = ontModel.createLiteral(valueLexicalForm);
                    }
                    rest = ontModel.createHasValueRestriction(null, onProperty, value);
                }
            }
        } else if (restrictionTypeStr.equals("minCardinality")) {
            rest = ontModel.createMinCardinalityRestriction(null,onProperty,cardinality);
        } else if (restrictionTypeStr.equals("maxCardinality")) {
            rest = ontModel.createMaxCardinalityRestriction(null,onProperty,cardinality);
        } else if (restrictionTypeStr.equals("cardinality")) {
            rest = ontModel.createCardinalityRestriction(null,onProperty,cardinality);
        }
        
        if (conditionTypeStr.equals("necessary")) {
            ontClass.addSuperClass(rest);
        } else if (conditionTypeStr.equals("necessaryAndSufficient")) {
            ontClass.addEquivalentClass(rest);
        }
        
        origModel.add(temp);
	}
}
