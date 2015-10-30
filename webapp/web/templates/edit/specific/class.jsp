<?xml version="1.0" encoding="UTF-8"?>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<!-- TODO: replace /vivo with base URL, add onclicks to edit and delete within superclasses, equivalent, disjoint, etc. -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css"/>
<link rel="stylesheet" type="text/css" href="/vivo/css/ontology_editor.css"/> <!-- TODO replace /vivo with some base URL -->

<style>

.ui-resizable-helper { border: 1px dotted gray; }
</style>

<input type="hidden" id="vclass-uri" data-vclass-uri="${VClass.getURI()}"/>

<div class="tree-container">
    <div class="item">
        <table>
            <tr class="editformcell">
                <td valign="top" colspan="4">
                    <h4>Class hierarchy</h4>
                    <table>
                        <tr>
                            <td><p><b>Superclasses</b></p>
                            <c:choose>
                                <c:when test="${empty superclasses}">
                                    <p>None</p>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${superclasses}" var="superclass">
                                        <p><a href="#">${superclass.getName()}</a></p>
                                    </c:forEach>
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
                                    <c:forEach items="${siblings}" var="sibling">
                                       <p><a href="#">${sibling.getName()}</a></p>
                                    </c:forEach>
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
                                    <c:forEach items="${subclasses}" var="subclass">
                                        <p><a href="#">${subclass.getName()}</a></p>
                                    </c:forEach>
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

<div class="item-container">
    <div class="item">
        <table>
            <tr>
                <td valign="bottom" colspan="2">
                     <h3 class="blue"><span class="vclass-label">${VClass.getName()}</span> <img src="/vivo/images/edit.png" class="action action-edit action-edit-vclass-label" title="Edit class label" onclick="editClass()"></img>   <input type="submit" class="delete action-delete-vclass" name="_delete" value="Delete"></input>  </h3>
                </td>
                <td valign="bottom" colspan="1">

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
                   <input type="checkbox" id="uri-check"></input> Edit URI
                 </td>
            </tr>
        </table>
    </div>

    <div class="item">
        <table>
            <tr>
            	<td valign="bottom" colspan="4">
                    <!-- TODO make this scrollable -->
            		<b>Subclass of:</b> <img src="/vivo/images/new.png" title="Add a superclass" class="action action-add action-add-superclass"></img> <br/>
                    <table>
                        <c:forEach items="${superclasses}" var="superclass">
                            <tr class="class-item">
                    	       <td class="item-detail" id="editable-item-detail" title="${superclass.getURI()}" data-superclass-uri="${superclass.getURI()}"><p>${superclass.getName()}</p></td> 
                               <td class="item-spacer"></td>
                               <td class="item-action"> <img src="/vivo/images/edit.png" class="action action-edit action-edit-superclass" title="Edit/replace with different class"> </img></td>
                               <td class="item-action"> <img src="/vivo/images/delete.png" class="action action-delete action-delete-superclass" title="Remove this"></img> </td>
                            </tr>
                        </c:forEach>
                    </table>
            	</td>
            </tr>
        </table>
    </div>

    <div class="item">
        <table>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
            <tr class="editformcell">
            	<td valign="bottom" colspan="4">
            		<b>Equivalent classes:</b> <img src="/vivo/images/new.png" title="Add equivalent class" class="action action-add action-add-eqclass"></img> <br/>
                    <table>
                        <c:forEach items="${equivalentClasses}" var="eqClass">
                            <tr class="class-item">
                                <td class="item-detail" id="editable-item-detail" title="${eqClass.getURI()}" data-eqclass-uri="${eqClass.getURI()}"><p>${eqClass.getName()}</p></td> 
                                <td class="item-spacer"></td>
                                <td class="item-action"><img src="/vivo/images/edit.png" class="action action-edit action-edit-eqclass" title="Edit/replace with different class"> </img></td> 
                                <td class="item-action"> <img src="/vivo/images/delete.png" class="action action-delete action-delete-eqclass" title="Remove this"></img></td></tr>
                        </c:forEach>
                    </table>
            	</td>
            </tr>
        </table>
    </div>

    <div class="item">
        <table>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
            <tr class="editformcell">
            	<td valign="top" colspan="4">
            		<b>Disjoint classes:</b> <img src="/vivo/images/new.png" title="Add disjoint class" class="action action-add action-add-disjoint"></img> <br/>
                    <table>
                	    <c:forEach items="${disjointClasses}" var="djClass">
                            <tr class="class-item">
                                <td class="item-detail" id="editable-item-detail" title="${djClass.getURI()}" data-disjoint-uri="${djClass.getURI()}"><p>${djClass.getName()}</p></td> 
                                <td class="item-spacer"></td>
                                <td class="item-action"><img src="/vivo/images/edit.png" class="action action-edit action-edit-disjoint" title="Edit/replace with different class"> </img></td> 
                                <td class="item-action"> <img src="/vivo/images/delete.png" class="action action-delete action-delete-disjoint" title="Remove this"></img></td>
                            </tr>
                        </c:forEach>
                    </table>
            	</td>
            </tr>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
        </table>
    </div>

    <div class="item raw-statements">
        <hr/>
        <p><b>Raw Statements</b></p>
        <p><input type="submit" class="submit" name="_subject" value="Resource as Subject"></input>
        <input type="submit" class="submit" name="_object" value="Resource as Object"></input></p>
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
            </tr>
        </table>
    </div>
</div>

</jsp:root>
