<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.EditSubmission" %>
<%@ page import="edu.cornell.mannlib.vitro.webapp.controller.Controllers" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@page import="org.apache.commons.logging.Log"%>
<%@page import="org.apache.commons.logging.LogFactory"%>
<%@page import="com.hp.hpl.jena.rdf.model.ResourceFactory"%>
<%@page import="com.hp.hpl.jena.rdf.model.Property"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jstl/functions" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/string-1.1" prefix="str" %>


<%
    /* Clear any cruft from session. */
    String resourceToRedirectTo = null;	
    String urlPattern = null;
    String predicateLocalName = null;
    String predicateAnchor = "";
    if( session != null ) {
        EditConfiguration editConfig = EditConfiguration.getConfigFromSession(session,request);
        //In order to support back button resubmissions, don't remove the editConfig from session.
        //EditConfiguration.clearEditConfigurationInSession(session, editConfig);
        
        EditSubmission editSub = EditSubmission.getEditSubmissionFromSession(session,editConfig);        
        EditSubmission.clearEditSubmissionInSession(session, editSub);
        
        if( editConfig != null ){
            String predicateUri = editConfig.getPredicateUri();            
            if( predicateUri != null ){
            	try{
            		Property prop = ResourceFactory.createProperty(predicateUri);
            		predicateLocalName = prop.getLocalName();
            	}catch (com.hp.hpl.jena.shared.InvalidPropertyURIException e){            		
            		log.debug("could not convert predicateUri into a valid URI",e);
            	}            	            	
            }                        
                        
            if( editConfig.getEntityToReturnTo() != null && editConfig.getEntityToReturnTo().startsWith("?") ){            	
            	resourceToRedirectTo = (String)request.getAttribute("entityToReturnTo");            
            }else{            
            	resourceToRedirectTo = editConfig.getEntityToReturnTo();
            }
            
        	//if there is no entity to return to it is likely a cancel
        	if( resourceToRedirectTo == null || resourceToRedirectTo.length() == 0 )
        		resourceToRedirectTo = editConfig.getSubjectUri();
            
        }
        
        //set up base URL
        String cancel = request.getParameter("cancel");
        String urlPatternToReturnTo = null;
        String urlPatternToCancelTo = null;
        if (editConfig != null) {
            urlPatternToReturnTo = editConfig.getUrlPatternToReturnTo();
            urlPatternToCancelTo = request.getParameter("url");
        }
        // If a different cancel return path has been designated, use it. Otherwise, use the regular return path.
        if ("true".equals(cancel) && !StringUtils.isEmpty(urlPatternToCancelTo)) {
            urlPattern = urlPatternToCancelTo;
        }
        else if (!StringUtils.isEmpty(urlPatternToReturnTo)) {
        	urlPattern = urlPatternToReturnTo;       
        } else {
        	urlPattern = "/individual";       	
        }
        
        //looks like a redirect to a profile page, try to add anchor for property that was just edited.
        if( urlPattern.endsWith("individual") || urlPattern.endsWith("entity") ){        	
       		if( predicateLocalName != null && predicateLocalName.length() > 0){
       			predicateAnchor = "#" + predicateLocalName;
       			request.setAttribute("predicateAnchor", predicateAnchor);
       		}
        }
    }
    
    if( resourceToRedirectTo != null ){ %>   	
	    <c:url context="/" var="encodedUrl" value="<%=urlPattern%>">              
	       <c:param name="uri" value="<%=resourceToRedirectTo%>" />
	       <c:param name="extra" value="true"/>  <%--  For ie6 --%>
	    </c:url>
	    <c:redirect url="${encodedUrl}${predicateAnchor}" />                    
    <% } else { %>
        <c:redirect url="<%= Controllers.LOGIN %>" />
    <% } %>

<%!
Log log = LogFactory.getLog("edu.cornell.mannlib.vitro.webapp.edit.postEditCleanUp.jsp");
%>



