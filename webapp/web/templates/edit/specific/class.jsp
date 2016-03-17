<% /* ?xml version="1.0" encoding="UTF-8"?> */ %>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<!-- TODO: replace /vivo with base URL, add onclicks to edit and delete within superclasses, equivalent, disjoint, etc. -->

<% /* jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0" */ %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css"/>
<link rel="stylesheet" type="text/css" href="/vivo/css/ontology_editor.css"/> <!-- TODO replace /vivo with some base URL -->
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"/>

<input type="hidden" id="vclass-uri" data-vclass-uri="${VClass.getURI()}" value="${VClass.getURI()}"/>

<c:choose>
    <c:when test="${empty allClasses}">
        <input type="hidden" class="class-option-data" data-uri="" value="None"/>
    </c:when>
    <c:otherwise>
        <c:forEach items="${allClasses}" var="vclass">
            <input type="hidden" class="class-option-data" data-uri="${vclass.getURI()}" value="${vclass.getName()}"/>
        </c:forEach>
    </c:otherwise>
</c:choose>

<!-- old tree structure
<div class="tree-container">
    <div class="item">
        <table>
            <tr class="editformcell">
                <td valign="top" colspan="4">
                    <table>
                        <tr>
                            <td><p><b>Superclasses</b></p>
                            <c:choose>
                                <c:when test="${empty superclasses}">
                                    <p>None</p>
                                </c:when>
                                <c:otherwise>
                                    <div class="scroll-list">
                                        <c:forEach items="${superclasses}" var="superclass">
                                            <p><a href="#">${superclass.getName()}</a></p>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td><p><b>Sibling classes</b></p>
                            <c:choose>
                                <c:when test="${empty siblings}">
                                    <p>None</p>
                                </c:when>
                                <c:otherwise>
                                    <div class="scroll-list">
                                        <c:forEach items="${siblings}" var="sibling">
                                           <p><a href="#">${sibling.getName()}</a></p>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td><p><b>Subclasses</b></p>
                            <c:choose>
                                <c:when test="${empty subclasses}">
                                    <p>None</p>
                                </c:when>
                                <c:otherwise>
                                    <div class="scroll-list">
                                        <c:forEach items="${subclasses}" var="subclass">
                                            <p><a href="#">${subclass.getName()}</a></p>
                                        </c:forEach>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </div>
</div>
-->

<div class="tree-container">
    <div class="item" id="new-class-container">
        <p style="text-align:center;"><a href="#" class="add-vclass">Add New Class</a></p>
    </div>
    <hr/>
    <div class="item">
        <div id="tree">
            <p>Loading tree...</p>
        </div>
    </div>
</div>

<div class="item-container">
    <div class="item">
        <table>
            <tr>
                <td valign="bottom" colspan="2">
                     <span class="vclass-label"><span id="name">${VClass.getName()}</span>   <b class="concept">(CLASS)</b> <i class="fa fa-pencil action-edit-name"></i> </span>
                </td>
                <td valign="bottom" colspan="2" id="edit-delete-vclass">
                    <p><input type="submit" class="delete action-delete-vclass" name="_delete" value="Delete"></input></p>
                </td>
            </tr>
        </table>
    </div>

    <div class="item">
        <table>
            <tr>
                <td id="uri-field">
                     <input type="text" readonly="true" value="${VClass.getURI()}" id="uri"></input>
                </td>
                <td id="uri-checkbox">

                   <p><input type="checkbox" id="uri-check"></input> Edit URI</p>
                 </td>
            </tr>
        </table>
    </div>

    <div class="item">
        <table>
            <tr>
            	<td valign="bottom" colspan="4">
                    <!-- TODO make this scrollable -->
            	   <p><b>Superclasses:</b> <span class="fa fa-plus action action-add-superclass" id="action-add-superclass"></span></p>
                   <div class="scroll-list">
                    <table id="superclass-table">
                            <c:forEach items="${superclasses}" var="superclass">
                                <tr class="class-item">
                        	       <td class="item-detail" id="editable-item-detail" title="${superclass.getURI()}" data-superclass-uri="${superclass.getURI()}"><p>${superclass.getName()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-superclass" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-superclass" title="Remove this"></i> </td>
                                </tr>
                            </c:forEach>
                    </table>
                    </div>
            	</td>
            </tr>
        </table>
    </div>

     <div class="item">
        <table>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
            <tr>
                <td valign="bottom" colspan="4">
                    <!-- TODO make this scrollable -->
                    <p><b>Subclasses:</b> <span class="fa fa-plus action action-add-subclass" id="action-add-subclass"></span></p>
                    <div class="scroll-list">
                    <table id="subclass-table">
                        <c:forEach items="${subclasses}" var="subclass">
                            <tr class="class-item">
                               <td class="item-detail" id="editable-item-detail" title="${subclass.getURI()}" data-subclass-uri="${subclass.getURI()}"><p>${subclass.getName()}</p></td> 
                               <td class="item-spacer"></td>
                               <td class="item-action"> <i class="fa fa-pencil action action-edit-subclass" title="Edit/replace"> </i></td>
                               <td class="item-action"> <i class="fa fa-trash action action-delete-subclass" title="Remove this"></i> </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                </td>
            </tr>
        </table>
    </div>

    <div class="item">
        <table>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
            <tr class="editformcell">
            	<td valign="bottom" colspan="4">
            		<p><b>Equivalent classes:</b> <span class="fa fa-plus action action-add-eqclass" id="action-add-eqclass"></span></p>
                    <div class="scroll-list">
                        <table id="eqclass-table">
                            <c:forEach items="${equivalentClasses}" var="eqClass">
                                <tr class="class-item">
                                    <td class="item-detail" id="editable-item-detail" title="${eqClass.getURI()}" data-eqclass-uri="${eqClass.getURI()}"><p>${eqClass.getName()}</p></td> 
                                    <td class="item-spacer"></td>
                                    <td class="item-action"><i class="fa fa-pencil action action-edit-eqclass" title="Edit/replace"> </i></td> 
                                    <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-eqclass" title="Remove this"></i></td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
            	</td>
            </tr>
        </table>
    </div>

    <div class="item">
        <table>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
            <tr class="editformcell">
            	<td valign="top" colspan="4">
            		<p><b>Disjoint classes:</b> <span class="fa fa-plus action action-add-disjoint" id="action-add-disjoint"></span></p>
                    <div class="scroll-list">
                        <table id="disjoint-table">
                    	    <c:forEach items="${disjointClasses}" var="djClass">
                                <tr class="class-item">
                                    <td class="item-detail" id="editable-item-detail" title="${djClass.getURI()}" data-disjoint-uri="${djClass.getURI()}"><p>${djClass.getName()}</p></td> 
                                    <td class="item-spacer"></td>
                                    <td class="item-action"><i class="fa fa-pencil action action-edit-disjoint" title="Edit/replace"></i></td> 
                                    <td class="item-action"> <i class="fa fa-trash action action-delete-disjoint" title="Remove this"></i></td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
            	</td>
            </tr>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
        </table>
    </div>

    <div class="item">
        <h4>Raw Statements</h4>
            <c:set var="query" 
                 value="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                        SELECT ?pred ?graph ?obj
                        WHERE 
                        {
                          GRAPH ?graph { <${entity.URI}> ?pred ?obj }
                        } ORDER BY ?graph ?pred
                        limit 10000"/>
            <form action="admin/sparqlquery" method="get">
                <input type="hidden" name="query" value="${query}"/>
                <input type="hidden" name="resultFormat" value="text/plain"/>
                <input type="submit" class="submit" value="Class as subject"/>
            </form>
            <c:set var="query" 
                 value="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                        SELECT ?sub ?pred ?graph
                        WHERE 
                        {
                          GRAPH ?graph { ?sub ?pred <${entity.URI}> }
                        } ORDER BY ?graph ?pred
                        limit 10000"/>
          <form action="admin/sparqlquery" method="get">
            <input type="hidden" name="query" value="${query}"/>
            <input type="hidden" name="resultFormat" value="text/plain"/>
            <input type="submit" class="submit" value="Class as object"/></p>
          </form>
    </div>

    <div class="item">
        <table>
        <form action="" method="post" id="restriction-form">
        <input type="hidden" name="VClassURI" value="${VClass.getURI()}"/>
        <tr><td colspan="4"><hr class="formDivider"/></td></tr>
            <tr class="restriction-field"><td><p><b>Restriction type:</b> <select id="restrictionType" name="restrictionType">
                <option value="allValuesFrom">all values from</option>
                <option value="someValuesFrom">some values from</option>
                <option value="hasValue">has value</option>
                <option value="minCardinality">minimum cardinality</option>
                <option value="maxCardinality">maximum cardinality</option>
                <option value="cardinality">cardinality</option>
            </select></p></td></tr>
            <tr class="restriction-field"><td><p><b>Condition type:</b> <select id="conditionType" name="conditionType">
                <option value="necessary">necessary</option>
                <option value="necessaryAndSufficient">necessary and sufficient</option>
            </select></p></td></tr>
            <tr class="restriction-field"><td><p><b>Property to restrict:</b> <select id="onProperty" name="onProperty">
                <c:forEach items="${allProperties}" var="property">
                    <option value="${property.getURI()}">${property.getLabel()}</option>
                </c:forEach>
            </select></p></td></tr>
                <tr class="restriction-field" id="restriction-container"><td><p><b>All values from:</b> <select id="ValueClass" name="ValueClass">
                    <c:forEach items="${allClasses}" var="vclass">
                        <option value="${vclass.getURI()}">${vclass.getName()}</option>
                    </c:forEach>
                </select></p></td></tr>
            <tr><td><input type="submit" class="submit" id="add-restriction" value="Add restriction"></td></tr>
        </table>
        </form>
    </div>

</div>

<div class="info-container">
    <div class="item">
        <p class="right-pane-item"><b>Ontology:</b> <br> <span id="ontology-name">${ontology.getName()}</span></p>
        <p class="right-pane-item"><b>Class group:</b> <span id="class-group">${classGroup}</span></p>
        <hr/>
        <p class="right-pane-item"><b>Display level:</b> <span id="display-level">${displayLevel}</span></p>
        <p class="right-pane-item"><b>Update level:</b> <span id="update-level">${updateLevel}</span></p>
        <p class="right-pane-item"><b>Publish level:</b> <span id="publish-level">${publishLevel}</span></p>
    </div>
</div>

<div class="stretch-panel">
    <div class="stretch-panel-header">
        <p><b>Composite operations</b></p>
    </div>
    <div class="stretch-panel-body">
        <table class="stretch-panel-table">
            <tr>
                <td><p><a href="#">Move class in hierarchy</a></p></td>
                <td><p><a href="#">Merge class into</a></p></td>
                <td><p><a href="#">Move instances into</a></p></td>
            </tr>
            <tr>
                <td><p><a href="#">Split class into</a></p></td>
                <td><p><a href="#">Specialize</a></p></td>
                <td><p><a href="#">Generalize</a></p></td>
            </tr>
            <tr>
                <td><p><a href="#">Make sibling classes disjoint</a></p></td>
                <td><p><a href="#">Add new individual of this class</a></p></td>
                <td><p><a href="#">Show inferred individuals</a></p></td>
            </tr>
            <tr>
                <td><p><a href="#">Show asserted individuals</a></p></td>
            </tr>
        </table>
    </div>
</div>

<% /* </jsp:root> */ %>
