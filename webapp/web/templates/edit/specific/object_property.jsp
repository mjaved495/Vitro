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
    <div class="item">
        <table>
            <tr>
                <td id="uri-field">
                     <input type="text" readonly="true" value="${objectProperty.getURI()}" id="uri"></input>
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
            	   <p><b>Inverse properties:</b> <span class="fa fa-plus action action-add-inverse"></span></p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${inverses}" var="inverse">
                                <tr class="class-item">
                        	       <td class="item-detail" id="editable-item-detail" title="${inverse.getURI()}" data-superclass-uri="${inverse.getURI()}"><p>${inverse.getLabel()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-inverse" title="Edit/replace with different property"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-inverse" title="Remove this"></i> </td>
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
                   <p><b>Domains:</b> <span class="fa fa-plus action action-add-domain"></span></p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${domains}" var="domain">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${domain.getURI()}" data-superclass-uri="${domain.getURI()}"><p>${domain.getName()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-domain" title="Edit/replace with different class"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-domain" title="Remove this"></i> </td>
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
                   <p><b>Ranges:</b> <span class="fa fa-plus action action-add-range"></span></p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${ranges}" var="range">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${range.getURI()}" data-superclass-uri="${range.getURI()}"><p>${range.getName()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-range" title="Edit/replace with different class"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-range" title="Remove this"></i> </td>
                                </tr>
                            </c:forEach>
                    </table>
                    </div>
                </td>
            </tr>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
        </table>
    </div>

</div>

<div class="info-container">
	 <div class="item">
        <p class="right-pane-item"><b>Ontology:</b> ${ontology.getName()}</p>
        <hr/>
        <p>Transitive? <input type="checkbox" id="transitive-check"/></p>
        <p>Symmetric? <input type="checkbox" id="symmetric-check"/></p>
        <p>Functional? <input type="checkbox" id="functional-check"/></p>
        <p>Inverse functional? <input type="checkbox" id="inverse-functional-check"/></p>
        <p>Reflexive? <input type="checkbox" id="reflexive-check"/></p>
        <input type="submit" class="submit" value="Save changes"/>
    </div>
</div>

<div class="stretch-panel">
    <div class="stretch-panel-header">
        <p><b>Composite operations</b></p>
    </div>
    <div class="stretch-panel-body">
        <table class="stretch-panel-table">

        </table>
    </div>
</div>

</jsp:root>