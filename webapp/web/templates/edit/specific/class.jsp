<?xml version="1.0" encoding="UTF-8"?>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<!-- TODO: replace /vivo with base URL, add onclicks to edit and delete within superclasses, equivalent, disjoint, etc. -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

<link rel="stylesheet" type="text/css" href="/vivo/css/ontology_editor.css"/> <!-- TODO replace /vivo with some base URL -->

<!-- <link href="/vivo/css/bootstrap.min.css" rel="stylesheet"/>
<script src="/vivo/js/bootstrap.min.js"></script> -->

<input type="hidden" id="vclass-uri" data-vclass-uri="${VClass.getURI()}"/>

<tr class="editformcell">
    <td valign="bottom" colspan="2">
         <h3 class="blue"><span class="vclass-label">${VClass.getName()}</span> <img src="/vivo/images/edit.png" class="action action-edit" title="Edit class label" onclick="editClass()"></img>    <input type="submit" class="delete" name="_delete" value="Delete" onclick="deleteClass()"></input>  </h3>
    </td>
    <td valign="bottom" colspan="1">
        Last update: June 02, 2015
    </td>
</tr>

<tr class="editformcell">
    <td id="uri-field">
         <input type="text" readonly="true" value="${VClass.getURI()}" id="uri"></input>
    </td>
    <td id="uri-checkbox">
       <input type="checkbox" onclick="toggleURIEditable()"></input> Edit URI
     </td>
</tr>

<tr class="editformcell">
	<td valign="bottom" colspan="4">
        <!-- TODO make this scrollable -->
		<b>Subclass of:</b> <img src="/vivo/images/new.png" onclick="editSuperclasses()" title="Add a superclass" class="action action-add"></img> <br/>
        <table>
            <c:forEach items="${superclasses}" var="superclass">
                <tr class="class-item">
        	       <td class="item-detail" id="editable-item-detail" title="${superclass.getURI()}" data-superclass-uri="${superclass.getURI()}"><p>${superclass.getName()}</p></td> 
                   <td class="item-spacer"></td>
                   <td class="item-action"> <img src="/vivo/images/edit.png" class="action action-edit action-edit-superclass" title="Replace with different class"> </img></td>
                   <td class="item-action"> <img src="/vivo/images/delete.png" class="action action-delete action-delete-superclass" title="Remove this superclass"></img> </td>
                </tr>
            </c:forEach>
        </table>
	</td>
</tr>
<tr><td colspan="4"><hr class="formDivider"/></td></tr>
<tr class="editformcell">
	<td valign="bottom" colspan="4">
		<b>Equivalent classes:</b> <img src="/vivo/images/new.png" onClick="editEquivalentClasses()" title="Add equivalent class" class="action action-add"></img> <br/>
        <table>
            <c:forEach items="${equivalentClasses}" var="eqClass">
                <tr class="class-item">
                    <td class="item-detail" id="editable-item-detail" title="${eqClass.getURI()}" data-eqclass-uri="${eqClass.getURI()}"><p>${eqClass.getName()}</p></td> 
                    <td class="item-spacer"></td>
                    <td class="item-action"><img src="/vivo/images/edit.png" class="action action-edit action-edit-eqclass" title="Replace with different class"> </img></td> 
                    <td class="item-action"> <img src="/vivo/images/delete.png" class="action action-delete action-delete-eqclass" title="Remove this equivalent class"></img></td></tr>
            </c:forEach>
        </table>
	</td>
</tr>
<tr><td colspan="4"><hr class="formDivider"/></td></tr>
<tr class="editformcell">
	<td valign="top" colspan="4">
		<b>Disjoint classes:</b> <img src="/vivo/images/new.png" onClick="editDisjointClasses()" title="Add disjoint class" class="action action-add"></img> <br/>
        <table>
    	    <c:forEach items="${disjointClasses}" var="djClass">
                <tr class="class-item">
                    <td class="item-detail" id="editable-item-detail" title="${djClass.getURI()}"><p>${djClass.getName()}</p></td> 
                    <td class="item-spacer"></td>
                    <td class="item-action"><img src="/vivo/images/edit.png" class="action action-edit" title="Replace with different class"> </img></td> 
                    <td class="item-action"> <img src="/vivo/images/delete.png" class="action action-delete" title="Remove this disjoint class"></img></td>
                </tr>
            </c:forEach>
        </table>
	</td>
</tr>
<tr class="editformcell">
    <td valign="top" colspan="2">
        <p><input type="submit" class="submit" name="_subject" value="Raw Statements with This Resource as Subject"></input></p>
        <p><input type="submit" class="submit" name="_object" value="Raw Statements with This Resource as Object"></input></p>
    </td>
</tr>



</jsp:root>
