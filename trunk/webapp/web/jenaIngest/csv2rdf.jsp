<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="com.hp.hpl.jena.ontology.Individual" %>
<%@ page import="com.hp.hpl.jena.ontology.OntModel" %>
<%@ page import="com.hp.hpl.jena.rdf.model.ModelMaker" %>
<%@ page import="com.hp.hpl.jena.shared.Lock" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>

<%

    ModelMaker maker = (ModelMaker) request.getSession().getAttribute("vitroJenaModelMaker");
    if (maker == null) {
        maker = (ModelMaker) getServletContext().getAttribute("vitroJenaModelMaker");
    }

%>

    <p><a href="ingest">Ingest Home</a></p>

    <h2>Convert CSV to RDF</h2>

    <form action="ingest" method="get"i>
        <input type="hidden" name="action" value="csv2rdf"/>

	<p><input type="radio" name="separatorChar" value="comma" checked="checked"/> comma separated 
	<input type="radio" name="separatorChar" value="tab"/> tab separated </p>

    <input type="text" style="width:80%;" name="csvUrl"/>
    <p>CSV file URL (e.g. "file:///")</p>

    <input type="text" name="namespace"/>
    <p>Namespace in which to generate resources</p>

	<input type="text" name="tboxNamespace"/>
    <p>Namespace in which to generate class and properties</p>

<!-- 
 <input type="checkbox" name="discardTbox"/> do not add TBox or RBox to result model
-->

    <input type="text" name="typeName"/>
    <p>Class Name for Resources</p>

    <select name="destinationModelName">
        <option value="vitro:baseOntModel">webapp assertions</option>
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
	String modelName = (String) it.next();
        %> <option value="<%=modelName%>"><%=modelName%></option>
        <%    
    }
%>    <option value="">(none)</option>
    </select>
    <p>Destination Model</p>

   <select name="tboxDestinationModelName">
        <option value="vitro:baseOntModel">webapp assertions</option>
<%
    for (Iterator it = maker.listModels(); it.hasNext(); ) {
	String modelName = (String) it.next();
        %> <option value="<%=modelName%>"><%=modelName%></option>
        <%    
    }
%>    <option value="">(none)</option>
    </select>
    <p>Destination Model for TBox</p>

    <input type="submit" value="Convert CSV"/>
