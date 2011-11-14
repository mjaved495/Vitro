/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.controller;

import static edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils.getPredicateUri;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditSubmissionUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.EditConfigurationGenerator;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.edit.EditConfigurationTemplateModel;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.edit.MultiValueEditSubmissionTemplateModel;
/**
 * This servlet is intended to handle all requests to create a form for use
 * by the N3 editing system.  It will examine the request parameters, determine
 * which form to use, execute a EditConfiguration setup, and evaluate the
 * view indicated by the EditConfiguration.
 * 
 * Do not add code to this class to achieve some behavior in a 
 * form.  Try adding the behavior logic to the code that generates the
 * EditConfiguration for the form.  
 */
public class EditRequestDispatchController extends FreemarkerHttpServlet {
    private static final long serialVersionUID = 1L;
    public static Log log = LogFactory.getLog(EditRequestDispatchController.class);
    
    final String DEFAULT_OBJ_FORM = "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultObjectPropertyFormGenerator";
    final String DEFAULT_DATA_FORM = "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultDataPropertyFormGenerator";
    //TODO: Create this generator
    final String RDFS_LABEL_FORM = "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.RDFSLabelGenerator";
    final String DEFAULT_DELETE_FORM = "edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultDeleteGenerator";
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
      
    	try{
        WebappDaoFactory wdf = vreq.getWebappDaoFactory();
         //check some error conditions and if they exist return response values
         //with error message
         if(isErrorCondition(vreq)){
        	 return doHelp(vreq, getErrorMessage(vreq));
         }
        
         //if edit form needs to be skipped to object instead
         if(isSkipEditForm(vreq)) {
        	 return processSkipEditForm(vreq);
         }
     
        //Get the edit generator name
         String editConfGeneratorName = processEditConfGeneratorName(vreq);

        //forward to create new handled in default object property form generator
        
         //session attribute 
         setSessionRequestFromEntity(vreq);
         //Test
 
         /****  make new or get an existing edit configuration ***/         
         EditConfigurationVTwo editConfig = setupEditConfiguration(editConfGeneratorName, vreq);
         
         
         //what template?
         String template = editConfig.getTemplate();
        
         //Get the multi value edit submission object
         MultiValueEditSubmission submission = getMultiValueSubmission(vreq, editConfig);
         MultiValueEditSubmissionTemplateModel submissionTemplateModel = new MultiValueEditSubmissionTemplateModel(submission);
         
         //what goes in the map for templates?
         Map<String,Object> templateData = new HashMap<String,Object>();
         EditConfigurationTemplateModel etm = new EditConfigurationTemplateModel( editConfig, vreq);
         templateData.put("editConfiguration", etm);
         templateData.put("editSubmission", submissionTemplateModel);
         //Corresponding to original note for consistency with selenium tests and 1.1.1
         templateData.put("title", "Edit");
         templateData.put("submitUrl", getSubmissionUrl(vreq));
         templateData.put("cancelUrl", etm.getCancelUrl());
         templateData.put("editKey", editConfig.getEditKey());
         //This may change based on the particular generator? Check if true
         templateData.put("bodyClasses", "formsEdit");
         return new TemplateResponseValues(template, templateData);
         }catch(Throwable th){
        	
        	 HashMap<String,Object> map = new HashMap<String,Object>();
        	 map.put("errorMessage", th.toString());
        	 log.error(th,th);
        	 return new TemplateResponseValues("error-message.ftl", map);
        
         }
    }
    


	private MultiValueEditSubmission getMultiValueSubmission(VitroRequest vreq, EditConfigurationVTwo editConfig) {
		return EditSubmissionUtils.getEditSubmissionFromSession(vreq.getSession(), editConfig);
	}

	//TODO: should more of what happens in this method
    //happen in the generators?
	private EditConfigurationVTwo setupEditConfiguration(String editConfGeneratorName,
			VitroRequest vreq) {	    	    	    
    	HttpSession session = vreq.getSession();
    	EditConfigurationVTwo editConfig = 
    	    makeEditConfigurationVTwo( editConfGeneratorName, vreq, session);

        //edit key is set here, NOT in the generator class
    	String editKey = EditConfigurationUtils.getEditKey(vreq);  
        editConfig.setEditKey(editKey);        

        //put edit configuration in session so it can be accessed on form submit.
        EditConfigurationVTwo.putConfigInSession(editConfig, session);
        
        Model model = (Model) getServletContext().getAttribute("jenaOntModel");
        
        if( editConfig.getSubjectUri() == null)
            editConfig.setSubjectUri( EditConfigurationUtils.getSubjectUri(vreq));
        if( editConfig.getPredicateUri() == null )
            editConfig.setPredicateUri( EditConfigurationUtils.getPredicateUri(vreq));
        
        String objectUri = EditConfigurationUtils.getObjectUri(vreq);
        String dataKey = EditConfigurationUtils.getDataPropKey(vreq);
        if (objectUri != null && ! objectUri.trim().isEmpty()) { 
            // editing existing object
            if( editConfig.getObject() == null)
                editConfig.setObject( EditConfigurationUtils.getObjectUri(vreq));
            editConfig.prepareForObjPropUpdate(model);
        } else if( dataKey != null ) { // edit of a data prop
            //do nothing since the data prop form generator must take care of it
        } else{
            //this might be a create new or a form
            editConfig.prepareForNonUpdate(model);
        }
        
		return editConfig;
	}

	private void setSessionRequestFromEntity(VitroRequest vreq) {
		HttpSession session = vreq.getSession();
		String subjectUri = vreq.getParameter("subjectUri");
		if(session.getAttribute("requestedFromEntity") == null) {
			session.setAttribute("requestedFromEntity", subjectUri);
		}
		
	}

	//Additional forwards.. should they be processed here to see which form should be forwarded to
	//e.g. default add individual form etc. and additional scenarios
	//TODO: Check if additional scenarios should be checked here
	private String processEditConfGeneratorName(VitroRequest vreq) {
		 WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    	//use default object property form if nothing else works
        String editConfGeneratorName = DEFAULT_OBJ_FORM;
        String predicateUri =  getPredicateUri(vreq);
        String formParam = getFormParam(vreq);
        //Handle deletion before any of the other cases
        if(isDeleteForm(vreq)) {
        	editConfGeneratorName = DEFAULT_DELETE_FORM;
        }
        // *** handle the case where the form is specified as a request parameter ***
        //TODO: Substitute the original line in again which checks for null predicate, currently overriding
        //in order to test
        //else if( predicateUri == null && ( formParam != null && !formParam.isEmpty()) ){
        else if(  formParam != null && !formParam.isEmpty() ){
            //form parameter must be a fully qualified java class name of a EditConfigurationVTwoGenerator implementation.
            editConfGeneratorName = formParam;              
        } else if(isVitroLabel(predicateUri)) { //in case of data property
        	editConfGeneratorName = RDFS_LABEL_FORM;
        } else{
        	String customForm = getCustomForm(predicateUri, wdf);
        	if(customForm != null && !customForm.isEmpty()) {
        		editConfGeneratorName = customForm;
        	}
        }
        return editConfGeneratorName;
	}
	
	
	

	private String getCustomForm(String predicateUri, WebappDaoFactory wdf) {
		Property prop = getPropertyByUri(predicateUri, wdf);
		return prop.getCustomEntryForm();
	}

	private Property getPropertyByUri(String predicateUri, WebappDaoFactory wdf) {
		Property p = null;
		p = wdf.getObjectPropertyDao().getObjectPropertyByURI(predicateUri);
		if(p == null) {
			p = wdf.getDataPropertyDao().getDataPropertyByURI(predicateUri);
		}
		return p;
	}


	private boolean isVitroLabel(String predicateUri) {
		return predicateUri.equals(VitroVocabulary.LABEL);
	}


	//if skip edit form
	private boolean isSkipEditForm(VitroRequest vreq) {
		 //Certain predicates may be annotated to change the behavior of the edit
        //link.  Check for this annotation and, if present, simply redirect 
        //to the normal individual display for the object URI instead of bringing
        //up an editing form.
        //Note that we do not want this behavior for the delete link (handled above).
        // This might be done in the custom form jsp for publicaitons already.
        // so maybe this logic shouldn't be here?
        WebappDaoFactory wdf = vreq.getWebappDaoFactory();
        String predicateUri = vreq.getParameter("predicateUri");
        boolean isEditOfExistingStmt = isEditOfExistingStmt(vreq);
        return (isEditOfExistingStmt && (wdf.getObjectPropertyDao().skipEditForm(predicateUri)));
	}

	//TODO: Implement below correctly or integrate
    private ResponseValues processSkipEditForm(VitroRequest vreq) {
        String redirectPage = vreq.getContextPath() + "/individual";
        String objectUri = EditConfigurationUtils.getObjectUri(vreq);
        String subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
        String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
        redirectPage += "uri=" + URLEncoder.encode(objectUri) + 
        	"&relatedSubjectUri=" + URLEncoder.encode(subjectUri) + 
        	"&relatingPredicateUri=" + URLEncoder.encode(predicateUri);
        return new RedirectResponseValues(redirectPage, HttpServletResponse.SC_SEE_OTHER);
		
	}

	//Check error conditions
    //TODO: Do we need both methods or jsut one?
    private boolean isErrorCondition(VitroRequest vreq) {
    	 String subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
         String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
         String formParam = getFormParam(vreq);
         //if no form parameter, then predicate uri and subject uri must both be populated
    	if (formParam == null || "".equals(formParam)) {
            if ((predicateUri == null || predicateUri.trim().length() == 0)) {
            	return true;
            }
            if (subjectUri == null || subjectUri.trim().length() == 0){
            	return true;
                        
            }
        }
    	
    	//Check predicate - if not vitro label and neither data prop nor object prop return error
    	WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    	//TODO: Check if any error conditions are not met here
    	//At this point, if there is a form paramter, we don't require a predicate uri
    	if(formParam == null 
    			&& predicateUri != null 
    			&& !EditConfigurationUtils.isObjectProperty(predicateUri, vreq) 
    			&& !isVitroLabel(predicateUri)
    			&& !EditConfigurationUtils.isDataProperty(predicateUri, vreq))
    	{
    		return true;
    	}
    	return false;
    }
    
    private String getErrorMessage(VitroRequest vreq) {
    	String errorMessage = null;
    	 String subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
         String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
         String formParam = getFormParam(vreq);
         if (formParam == null || "".equals(formParam)) {
             if ((predicateUri == null || predicateUri.trim().length() == 0)) {
            	 errorMessage = "No form was specified, both predicateUri and"
                     + " editform are empty. One of these is required"
                     + " by editRequestDispatch to choose a form.";
             }
             if (subjectUri == null || subjectUri.trim().length() == 0){
                 return "subjectUri was empty. If no editForm is specified," +
                 		" it is required by EditRequestDispatch.";                
             }
         }
         return errorMessage;
    }
    
	//should return null
	private String getFormParam(VitroRequest vreq) {
		String formParam = (String) vreq.getParameter("editForm");
		return formParam;
	}
    
    private boolean isEditOfExistingStmt(VitroRequest vreq) {
        String objectUri = vreq.getParameter("objectUri");

    	WebappDaoFactory wdf = vreq.getWebappDaoFactory();
    	if(objectUri != null) {
        	Individual object = wdf.getIndividualDao().getIndividualByURI(objectUri);
        	return (object != null);
    	}
    	return false;
    }
    
    //Check whether command is delete and either process or save
    //Original code involved doing a jsp forward
    //TODO: Check how to integrate deletion
    private boolean isDeleteForm(VitroRequest vreq) {
    	String command = vreq.getParameter("cmd");
        if ("delete".equals(command)) {
       	 	return true;
        }
        return false;

    }
    
    
    //
    
    private EditConfigurationVTwo makeEditConfigurationVTwo(
            String editConfGeneratorName, VitroRequest vreq, HttpSession session) {
    	
    	EditConfigurationGenerator EditConfigurationVTwoGenerator = null;
    	
        Object object = null;
        try {
            Class classDefinition = Class.forName(editConfGeneratorName);
            object = classDefinition.newInstance();
            EditConfigurationVTwoGenerator = (EditConfigurationGenerator) object;
        } catch (InstantiationException e) {
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }    	
        
        if(EditConfigurationVTwoGenerator == null){
        	throw new Error("Could not find EditConfigurationVTwoGenerator " + editConfGeneratorName);        	
        } else {
            return EditConfigurationVTwoGenerator.getEditConfiguration(vreq, session);
        }
        
    }

    
    private ResponseValues doHelp(VitroRequest vreq, String message){
        //output some sort of help message for the developers.
        
    	HashMap<String,Object> map = new HashMap<String,Object>();
   	 map.put("errorMessage", "help is not yet implemented");
   	 return new TemplateResponseValues("error-message.ftl", map);    }
    
    
    //Get submission url
    private String getSubmissionUrl(VitroRequest vreq) {
    	return vreq.getContextPath() + "/edit/process";
    }
    
    
}
