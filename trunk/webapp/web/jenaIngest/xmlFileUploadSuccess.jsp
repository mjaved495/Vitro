<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ page import="com.hp.hpl.jena.rdf.model.ModelMaker" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.net.URLEncoder" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%
  

%>
<p>Uploaded XML files and converted to RDF.</p>
<p>Loaded <%= request.getAttribute("statementCount") %> statements to the model <%= request.getAttribute("targetModel") %>.</p>

<p><a href="ingest">Ingest Home</a></p>
       
    
