<?xml version="1.0" encoding="UTF-8"?>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<!-- TODO: replace /vivo with base URL, add onclicks to edit and delete within superclasses, equivalent, disjoint, etc. -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

<link rel="stylesheet" type="text/css" href="/vivo/css/ontology_editor.css"/> <!-- TODO replace /vivo with some base URL -->

<tr class="editformcell">
    <td valign="bottom" colspan="2">
         <h3 class="blue">${VClass.getName()} <img src="/vivo/images/edit.png" onclick="editClass()" class="action"> </img>    <input type="submit" class="delete" name="_delete" value="Delete" onclick="deleteClass()"></input>  </h3>
    </td>
    <td valign="bottom" colspan="1">
        Last update: June 02, 2015
    </td>
</tr>

<tr class="editformcell">
    <td class="editing-form-action">
         <input type="text" readonly="true" value="${VClass.getURI()}"></input>
    </td>
    <td class="editing-form-action">
       <input type="checkbox"></input> Edit URI
     </td>
</tr>
        
    </td>
    
</tr>

<tr class="editformcell">
	<td valign="bottom" colspan="4">
        <!-- TODO make this scrollable -->
		<b>Subclass of:</b> <img src="/vivo/images/new.png" onclick="editSuperclasses()" class="action"></img> <br/>
        <table>
            <c:forEach items="${superclasses}" var="superclass">
                <tr>
        	       <td class="item-detail"><p>${superclass.getName()}</p></td> <td class="item-actions"> <img src="/vivo/images/edit.png" class="action"> </img> <img src="/vivo/images/delete.png" class="action"></img> </td>
                </tr>
            </c:forEach>
        </table>
	</td>
</tr>
<tr><td colspan="4"><hr class="formDivider"/></td></tr>
<tr class="editformcell">
	<td valign="bottom" colspan="4">
		<b>Equivalent classes:</b> <img src="/vivo/images/new.png" onClick="editEquivalentClasses()" class="action"></img> <br/>
        <c:forEach items="${equivalentClasses}" var="eqClass">
            <p>${eqClass.getName()} <img src="/vivo/images/edit.png" class="action"> </img> <img src="/vivo/images/delete.png" class="action"></img></p>
        </c:forEach>
	</td>
</tr>
<tr><td colspan="4"><hr class="formDivider"/></td></tr>
<tr class="editformcell">
	<td valign="top" colspan="4">
		<b>Disjoint classes:</b> <img src="/vivo/images/new.png" onClick="editDisjointClasses()"></img> <br/>
	    <c:forEach items="${disjointClasses}" var="djClass">
            <p>${djClass.getName()} <img src="/vivo/images/edit.png" class="action"> </img> <img src="/vivo/images/delete.png" class="action"></img></p>
        </c:forEach>
	</td>
</tr>
<tr class="editformcell">
    <td valign="top" colspan="2">
        <p><input type="submit" class="submit" name="_subject" value="Raw Statements with This Resource as Subject"></input></p>
        <p><input type="submit" class="submit" name="_object" value="Raw Statements with This Resource as Object"></input></p>
    </td>
</tr>

</jsp:root>
