/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RoleIdentifier;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.SelfEditingIdentifierFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ServletIdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.filters.VitroRequestPrep;

import org.apache.xerces.util.XMLChar;

public class EditN3Utils {

    public static String getEditorUri(HttpServletRequest request, HttpSession session, ServletContext context){
        String editorUri = "Unknown N3 Editor";
        boolean selfEditing = VitroRequestPrep.isSelfEditing(request);
        IdentifierBundle ids =
            ServletIdentifierBundleFactory.getIdBundleForRequest(request,session,context);           
        
        if( selfEditing )
            editorUri = SelfEditingIdentifierFactory.getSelfEditingUri(ids);
        else
            editorUri = RoleIdentifier.getUri(ids);
        
        return editorUri;        
    }
    
    /**
     * Strips from a string any characters that are not valid in XML 1.0
     * @param in
     * @return
     */
    public static String stripInvalidXMLChars(String in) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (!XMLChar.isInvalid(c)) {
                out.append(c);
            }
        }
        return out.toString();
    }
    
    
//    public static void addModTimes( Model additions, Model retractions, Model contextModel ){    	    	
//    	Property modtime = ResourceFactory.createProperty(VitroVocabulary.MODTIME);
//    	Date time = Calendar.getInstance().getTime();
//    	
//    	//get all resources in additions and retractions that are not types
//    	additions.listStatements()
//    	
//    	Lock lock = contextModel.getLock();
//	    try {
//        
//            String existingValue = null;
//            Statement stmt = res.getProperty(modtime);
//            if (stmt != null) {
//                RDFNode object = stmt.getObject();
//                if (object != null && object.isLiteral()){
//                    existingValue = ((Literal)object).getString();
//                }
//            }
//            String formattedDateStr = xsdDateTimeFormat.format(time);
//            if ( (existingValue!=null && value == null) || (existingValue!=null && value != null && !(existingValue.equals(formattedDateStr)) ) ) {
//                model.removeAll(res, modtime, null);
//            }
//            if ( (existingValue==null && value != null) || (existingValue!=null && value != null && !(existingValue.equals(formattedDateStr)) ) ) {
//                model.add(res, modtime, formattedDateStr, XSDDatatype.XSDdateTime);
//            }
//	        
//	    } catch (Exception e) {
//	        log.error("Error in updatePropertyDateTimeValue");
//	        log.error(e);
//	    }
//    }
//    
//    private static List<URIResource>
}
