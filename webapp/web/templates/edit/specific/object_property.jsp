<?xml version="1.0" encoding="UTF-8"?>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<!-- TODO: replace /vivo with base URL, add onclicks to edit and delete within superclasses, equivalent, disjoint, etc. -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

<link rel="stylesheet" type="text/css" href="/vivo/css/ontology_editor.css"/> <!-- TODO replace /vivo with some base URL -->
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"/>

<div class="tree-container">
	<div class="item">
		<table>
			<tr class="editformcell">
				<td valign="top" colspan="4">
					<table>
						<tr>
							<td><p><b>Superproperties:</b></p>
							<c:choose>
								<c:when test="${empty superproperties}">
									<p>None</p>
								</c:when>
								<c:otherwise>
									<div class="scroll-list">
										<c:forEach items="${superproperties}" var="superproperty">
											<p><a href="#">${superproperty.getLabel()}</a></p>
										</c:forEach>
									</div>
								</c:otherwise>
							</c:choose>
							</td>
						</tr>
						<tr>
							<td><p><b>Sibling properties:</b></p>
							<c:choose>
								<c:when test="${empty siblings}">
									<p>None</p>
								</c:when>
								<c:otherwise>
									<div class="scroll-list">
										<c:forEach items="${siblings}" var="sibling">
											<p><a href="#">${sibling.getLabel()}</a></p>
										</c:forEach>
									</div>
								</c:otherwise>
							</c:choose>
							</td>
						</tr>
						<tr>
							<td><p><b>Subproperties:</b></p>
							<c:choose>
								<c:when test="${empty subproperties}">
									<p>None</p>
								</c:when>
								<c:otherwise>
									<div class="scroll-list">
										<c:forEach items="${subproperties}" var="subproperty">
											<p><a href="#">${subproperty.getLabel()}</a></p>
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

<div class="item-container">
    <div class="item">
        <table>
            <tr>
                <td valign="bottom" colspan="2">
                     <span class="vclass-label">${objectProperty.getLabel()} <b class="object-property">(OBJECT PROPERTY)</b> <i class="fa fa-pencil"></i> </span>
                </td>
                <td valign="bottom" colspan="2" id="edit-delete-vclass">
                    <p><input type="submit" class="delete action-delete-vclass" name="_delete" value="Delete"></input></p>
                </td>
            </tr>
        </table>
    </div>
</div>

<div class="info-container">
	<p>Placeholder</p>
</div>

</jsp:root>