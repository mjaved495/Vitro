<?xml version="1.0" encoding="UTF-8"?>
<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:edLnk="http://vitro.mannlib.cornell.edu/vitro/tags/PropertyEditLink" version="2.0">

<jsp:directive.page import="edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary"/>
<jsp:directive.page import="edu.cornell.mannlib.vitro.webapp.beans.User"/>

<div class="editingForm">

<jsp:include page="/templates/edit/fetch/vertical.jsp"/>

<div align="center">
<table class="form-background" border="0" cellpadding="2" cellspacing="2">
<tr align="center">
    <td valign="bottom">
        <form action="listUsers" method="get">
        <input type="hidden" name="home" value="${portalBean.portalId}" />
            <input type="submit" class="form-button" value="See All User Accounts"/>
        </form>
    </td>
    <td valign="bottom" align="center">
        <form action="editForm" method="get">
            <input name="home" type="hidden" value="${portalBean.portalId}" />
            <input name="uri" type = "hidden" value="${user.URI}" />
            <input type="submit" class="form-button" value="Edit User Account"/>
        <input type="hidden" name="controller" value="User"/>
        </form>
         <form action="editForm" method="get">
            <input name="home" type="hidden" value="${portalBean.portalId}" />
            <input name="uri" type = "hidden" value="${user.URI}" />
            <input name="Md5password" type="hidden" value=""/>
            <input name="OldPassword" type="hidden" value=""/>
            <input type="submit" class="form-button" value="Reset Password"/>
            <input type="hidden" name="controller" value="User"/>
        </form>
    </td>
    <td valign="bottom">
        <form action="editForm" method="get">
            <input name="home" type="hidden" value="${portalBean.portalId}" />
        <input type="hidden" name="controller" value="User"/>
            <input type="submit" class="form-button" value="Add New User Account"/>
        </form>
    </td>            
</tr>
</table>

</div>
</div>
</jsp:root>
