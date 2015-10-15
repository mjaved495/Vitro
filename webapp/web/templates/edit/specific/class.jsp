<?xml version="1.0" encoding="UTF-8"?>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

<tr class="editformcell">
    <td valign="bottom" colspan="2">
        <h5>FacultyMember</h5> | <p><b>CONCEPT</b> <img src="${urls.base}/images/edit.png"> <input type="submit" class="delete" name="_delete" value="Delete"></input> </img> </p>

        <input type="text" readonly="true">http://www.vivo.cornell.edu/hr.owl/FacultyMember</input>
        <input type="checkbox"></input> Edit URI
    </td>
    <td valign="bottom" colspan="2">
        Last update: June 02, 2015
    </td>
</tr>

<tr class="editformcell">
	<td valign="bottom" colspan="4">
        <!-- TODO make this scrollable -->
		<b>Subclass of:</b> <img src="${urls.base}/images/new.png"></img> <br/>
	    <p>Employee</p>
        <p>FacultyMember</p>
	</td>
</tr>
<tr><td colspan="4"><hr class="formDivider"/></td></tr>
<tr class="editformcell">
	<td valign="bottom" colspan="4">
		<b>Equivalent classes:</b> <img src="${urls.base}/images/new.png"></img> <br/>
        <p>Person and hasPosition some FacultyPosition</p>
	</td>
</tr>
<tr><td colspan="4"><hr class="formDivider"/></td></tr>
<tr class="editformcell">
	<td valign="top" colspan="4">
		<b>Disjoint classes:</b> <img src="${urls.base}/images/new.png"></img> <br/>
	    <p>NonFacultyAcademic</p>
        <p>Librarian</p>
	</td>
</tr>
<tr class="editformcell">
    <td valign="top" colspan="2">
        <p><input type="submit" class="submit" name="_subject" value="Raw Statements with This Resource as Subject"></input></p>
        <p><input type="submit" class="submit" name="_object" value="Raw Statements with This Resource as Object"></input></p>
    </td>
    <td>
        <p><input type="submit" class="submit" name="_submit" value="Submit Changes"></input> <input type="submit" class="cancel" name="_cancel" value="Cancel"></input></p>
    </td>
</tr>

</jsp:root>
