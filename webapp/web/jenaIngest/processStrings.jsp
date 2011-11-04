<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="com.hp.hpl.jena.ontology.Individual" %>
<%@ page import="com.hp.hpl.jena.ontology.OntModel" %>
<%@ page import="com.hp.hpl.jena.rdf.model.ModelMaker" %>
<%@ page import="com.hp.hpl.jena.shared.Lock" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseAdvancedDataToolsPages" %>
<% request.setAttribute("requestedActions", new UseAdvancedDataToolsPages()); %>
<vitro:confirmAuthorization />

<%

    ModelMaker maker = (ModelMaker) request.getSession().getAttribute("vitroJenaModelMaker");
    if (maker == null) {
        maker = (ModelMaker) getServletContext().getAttribute("vitroJenaSDBModelMaker");
    }

%>
    <h2><a class="ingestMenu" href="ingest">Ingest Menu</a> > Process Property Value Strings</h2>

    <form action="ingest" method="get"i>
        <input type="hidden" name="action" value="processStrings"/>

    <input type="text" style="width:80%;" name="className"/>
    <p>String processor class</p>
    
    <input type="text" name="methodName"/>
    <p>String processor method</p>

    <input type="text" name="propertyName"/>
    <p>Property URI</p>

    <input type="text" name="newPropertyName"/>
    <p>New Property URI</p>

    <select name="destinationModelName">
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
	String modelName = (String) it.next();
        %> <option value="<%=modelName%>"/><%=modelName%></option>
        <%    
    }
%>   
    </select>
    <input type="checkbox" name="processModel" value="TRUE"/> apply changes directly to this model
    <p>model to use</p>
   
    <select name="additionsModel">
		<option value="">none</option>
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
	String modelName = (String) it.next();
        %> <option value="<%=modelName%>"><%=modelName%></option>
        <%    
    }
%>   
	</select>
    <p>model in which to save added statements</p>

    <select name="retractionsModel">
		<option value="">none</option>
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
	String modelName = (String) it.next();
        %> <option value="<%=modelName%>"><%=modelName%></option>
        <%    
    }
%>   
	</select>
    <p>model in which to save retracted statements</p>

    <input class="submit" type="submit" value="Process property values"/>
