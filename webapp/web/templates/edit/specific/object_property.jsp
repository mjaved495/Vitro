<?xml version="1.0" encoding="UTF-8"?>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<!-- TODO: replace /vivo with base URL, add onclicks to edit and delete within superclasses, equivalent, disjoint, etc. -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

<link rel="stylesheet" type="text/css" href="/vivo/css/ontology_editor.css"/> <!-- TODO replace /vivo with some base URL -->
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"/>

<input type="hidden" id="property-uri" data-property-uri="${objectProperty.getURI()}"/>

<!-- <div class="tree-container">
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
-->

<c:choose>
    <c:when test="${empty allProperties}">
        <input type="hidden" class="property-option-data" data-uri="" value="None"/>
    </c:when>
    <c:otherwise>
        <c:forEach items="${allProperties}" var="prop">
            <input type="hidden" class="property-option-data" data-uri="${prop.getURI()}" value="${prop.getLabel()}"/>
        </c:forEach>
    </c:otherwise>
</c:choose>

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

<div class="tree-container">
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
                     <span class="prop-label">${objectProperty.getLabel()} <b class="object-property">(OBJECT PROPERTY)</b> <i class="fa fa-pencil"></i> </span>
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
                   <p><b>Superproperties:</b> <span class="fa fa-plus action action-add-superproperty"></span></p>
                   <div class="scroll-list">
                    <table id="superproperty-table">
                            <c:forEach items="${superproperties}" var="superproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${superproperty.getURI()}" data-superclass-uri="${superproperty.getURI()}"><p>${superproperty.getLabel()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-inverse" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-inverse" title="Remove this"></i> </td>
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
        <table class="subproperty">
            <tr>
                <td valign="bottom" colspan="4">
                   <p><b>Subproperties:</b> <span class="fa fa-plus action action-add-subproperty"></span></p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${subproperties}" var="subproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${subproperty.getURI()}" data-superclass-uri="${subproperty.getURI()}"><p>${subproperty.getLabel()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-inverse" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-inverse" title="Remove this"></i> </td>
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
        <table class="eqproperty-table">
            <tr>
                <td valign="bottom" colspan="4">
                   <p><b>Equivalent properties:</b> <span class="fa fa-plus action action-add-eqproperty"></span></p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${eqproperties}" var="eqproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${eqproperty.getURI()}" data-superclass-uri="${eqproperty.getURI()}"><p>${eqproperty.getLabel()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-inverse" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-inverse" title="Remove this"></i> </td>
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
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-inverse" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-inverse" title="Remove this"></i> </td>
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
        <table class="domain-table">
            <tr>
                <td valign="bottom" colspan="4">
                   <p><b>Domains:</b> <span class="fa fa-plus action action-add-domain"></span></p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${domains}" var="domain">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${domain.getURI()}" data-superclass-uri="${domain.getURI()}"><p>${domain.getName()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-domain" title="Edit/replace"> </i></td>
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
        <table class="range-table">
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
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-range" title="Edit/replace"> </i></td>
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

        <c:choose>
            <c:when test="${prop.getTransitive()}">
                <p><input type="checkbox" id="transitive-check" checked="true"/> Transitive</p>
            </c:when>
            <c:otherwise>
                <p><input type="checkbox" id="transitive-check" /> Transitive</p>
            </c:otherwise>
        </c:choose>

       <c:choose>
            <c:when test="${prop.getSymmetric()}">
                <p><input type="checkbox" id="symmetric-check" checked="true"/> Symmetric</p>
            </c:when>
            <c:otherwise>
                <p><input type="checkbox" id="symmetric-check" /> Symmetric</p>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${prop.getFunctional()}">
                <p><input type="checkbox" id="functional-check" checked="true"/> Functional</p>
            </c:when>
            <c:otherwise>
                <p><input type="checkbox" id="functional-check" /> Functional</p>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${prop.getInverseFunctional()}">
                <p><input type="checkbox" id="inverse-functional-check" checked="true"/> Inverse Functional</p>
            </c:when>
            <c:otherwise>
                <p><input type="checkbox" id="inverse-functional-check" /> Inverse Functional</p>
            </c:otherwise>
        </c:choose>

        <!-- <c:choose>
            <c:when test="${prop.getTransitive()}">
                <p><input type="checkbox" id="inverse-functional-check" checked="true"/> Functional</p>
            </c:when>
            <c:otherwise>
                <p><input type="checkbox" id="inverse-functional-check" checked="false"/> Functional</p>
            </c:otherwise>
        </c:choose> -->
    </div>
</div>

<div class="stretch-panel">
    <div class="stretch-panel-header">
        <p><b>Composite operations</b></p>
    </div>
    <div class="stretch-panel-body">
        <p>Nothing here yet</p>
    </div>
</div>

</jsp:root>