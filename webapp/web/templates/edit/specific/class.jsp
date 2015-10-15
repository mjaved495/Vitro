<?xml version="1.0" encoding="UTF-8"?>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

<tr class="editformcell">
    <td valign="bottom" colspan="3">
        <h3 class="blue">${VClass.getName()} <img src="/vivo/images/edit.png" onclick="editClass()"> </img>    <input type="submit" class="delete" name="_delete" value="Delete" onclick="deleteClass()"></input>  </h3>

        <input type="text" readonly="true" value="${VClass.getURI()}"></input>
        <input type="checkbox"></input> Edit URI
    </td>
    <td valign="bottom" colspan="1">
        Last update: June 02, 2015
    </td>
</tr>

<tr class="editformcell">
	<td valign="bottom" colspan="4">
        <!-- TODO make this scrollable -->
		<b>Subclass of:</b> <img src="/vivo/images/new.png" onclick="editSuperclasses()"></img> <br/>
        <c:forEach items="${superclasses}" var="superclass">
    	    <p>${superclass.getName()}/p>
        </c:forEach>
	</td>
</tr>
<tr><td colspan="4"><hr class="formDivider"/></td></tr>
<tr class="editformcell">
	<td valign="bottom" colspan="4">
		<b>Equivalent classes:</b> <img src="/vivo/images/new.png" onClick="editEquivalentClasses()"></img> <br/>
        <c:forEach items="${equivalentClasses}" var="eqClass">
            <p>${eqClass.getName()}/p>
        </c:forEach>
	</td>
</tr>
<tr><td colspan="4"><hr class="formDivider"/></td></tr>
<tr class="editformcell">
	<td valign="top" colspan="4">
		<b>Disjoint classes:</b> <img src="/vivo/images/new.png" onClick="editDisjointClasses()"></img> <br/>
	    <c:forEach items="${disjointClasses}" var="djClass">
            <p>${djClass.getName()}/p>
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
