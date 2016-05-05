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
            <input type="hidden" class="property-option-data" data-uri="${prop.getURI()}" value="${prop.getLabel()}" data-localname="${prop.getLocalName()}"/>
        </c:forEach>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${empty allClasses}">
        <input type="hidden" class="class-option-data" data-uri="" value="None"/>
    </c:when>
    <c:otherwise>
        <c:forEach items="${allClasses}" var="vclass">
            <input type="hidden" class="class-option-data" data-uri="${vclass.getURI()}" value="${vclass.getName()}" data-localname="${prop.getLocalName()}"/>
        </c:forEach>
    </c:otherwise>
</c:choose>

<div class="tree-container">
    <div class="item" id="new-property-container">
        <p style="text-align:center;"><a href="#" class="add-object-property">Add Object Property</a></p>
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
                     <span class="prop-label"><span id="name">${objectProperty.getLabel()}</span>   <b class="object-property">(OBJECT PROPERTY)</b> <i class="fa fa-pencil action-edit-name"></i> </span>
                </td>
                <td valign="bottom" colspan="2" id="edit-delete-vclass">
                    <p><input type="submit" class="delete action-delete-property" name="_delete" value="Delete"></input></p>
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
                   <p><b>Superproperties:</b> <span class="fa fa-plus action action-add-superproperty" id="action-add-superproperty"></span></p>
                   <div class="scroll-list">
                    <table id="superproperty-table">
                            <c:forEach items="${superproperties}" var="superproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${superproperty.getURI()}" data-superproperty-uri="${superproperty.getURI()}"><p>${superproperty.getLabel()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-superproperty" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-superproperty" title="Remove this"></i> </td>
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
                   <p><b>Subproperties:</b> <span class="fa fa-plus action action-add-subproperty" id="action-add-subproperty"></span></p>
                   <div class="scroll-list">
                    <table id="subproperty-table">
                            <c:forEach items="${subproperties}" var="subproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${subproperty.getURI()}" data-subproperty-uri="${subproperty.getURI()}"><p>${subproperty.getLabel()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-subproperty" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-subproperty" title="Remove this"></i> </td>
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
                   <p><b>Equivalent properties:</b> <span class="fa fa-plus action action-add-eqproperty" id="action-add-eqproperty"></span></p>
                   <div class="scroll-list">
                    <table id="eqproperty-table">
                            <c:forEach items="${eqproperties}" var="eqproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${eqproperty.getURI()}" data-eqproperty-uri="${eqproperty.getURI()}"><p>${eqproperty.getLabel()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-eqproperty" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-eqproperty" title="Remove this"></i> </td>
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
            	   <p id="add-inverse-container"><b>Inverse:</b>
                    <c:choose>
                        <c:when test="${empty inverses}">
                            <span class="fa fa-plus action action-add-inverse" id="action-add-inverse"></span>
                        </c:when>
                        <c:otherwise></c:otherwise>
                    </c:choose>
                   </p>
                   <div class="scroll-list">
                    <table id="inverse-table">
                            <c:forEach items="${inverses}" var="inverse">
                                <tr class="class-item">
                        	       <td class="item-detail" id="editable-item-detail" title="${inverse.getURI()}" data-inverse-property-uri="${inverse.getURI()}"><p>${inverse.getLabel()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-inverse-property" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-inverse-property" title="Remove this"></i> </td>
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
                   <p id="add-domain-container"><b>Domain:</b> 
                    <c:choose>
                        <c:when test="${empty domains}">
                            <span class="fa fa-plus action action-add-domain" id="action-add-domain"></span>
                        </c:when>
                        <c:otherwise></c:otherwise>
                    </c:choose>
                </p>
                   <div class="scroll-list">
                    <table class="domain-table">
                            <c:forEach items="${domains}" var="domain">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${domain.getURI()}" data-domain-class-uri="${domain.getURI()}"><p>${domain.getName()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-domain-class" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-domain-class" title="Remove this"></i> </td>
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
                   <p id="add-range-container"><b>Range:</b> 
                    <c:choose>
                        <c:when test="${empty ranges}">
                            <span class="fa fa-plus action action-add-range" id="action-add-range"></span>
                        </c:when>
                        <c:otherwise></c:otherwise>
                    </c:choose>
                </p>
                   <div class="scroll-list">
                    <table class="range-table">
                            <c:forEach items="${ranges}" var="range">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${range.getURI()}" data-range-class-uri="${range.getURI()}"><p>${range.getName()}</p></td> 
                                   <td class="item-spacer"></td>
                                   <td class="item-action"> <i class="fa fa-pencil action action-edit-range-class" title="Edit/replace"> </i></td>
                                   <td class="item-action"> <i class="fa fa-trash action action-delete-range-class" title="Remove this"></i> </td>
                                </tr>
                            </c:forEach>
                    </table>
                    </div>
                </td>
            </tr>
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
        </table>
  
        <c:choose>
            <c:when test="${objectProperty.getTransitive()}">
                <input type="checkbox" id="transitive-check" checked="true"/> Transitive
            </c:when>
            <c:otherwise>
                <input type="checkbox" id="transitive-check" /> Transitive
            </c:otherwise>
        </c:choose>

       <c:choose>
            <c:when test="${objectProperty.getSymmetric()}">
                <input type="checkbox" id="symmetric-check" checked="true"/> Symmetric
            </c:when>
            <c:otherwise>
                <input type="checkbox" id="symmetric-check" /> Symmetric
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${objectProperty.getFunctional()}">
                <input type="checkbox" id="functional-check" checked="true"/> Functional
            </c:when>
            <c:otherwise>
                <input type="checkbox" id="functional-check" /> Functional
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${objectProperty.getInverseFunctional()}">
                <input type="checkbox" id="inverse-functional-check" checked="true"/> Inverse Functional
            </c:when>
            <c:otherwise>
                <input type="checkbox" id="inverse-functional-check" /> Inverse Functional
            </c:otherwise>
        </c:choose>

    </div>

</div>

<div class="info-container">
	 <div class="item">
        <p class="right-pane-item"><b>Ontology:</b><br/> <span id="ontology-name">${ontology.getName()}</span></p>

        <hr/>
        <p class="right-pane-item"><b>Display level:</b> <br/> <span id="display-level">${displayLevel}</span></p>
        <p class="right-pane-item"><b>Update level:</b> <br/> <span id="update-level">${updateLevel}</span></p>
        <p class="right-pane-item"><b>Publish level:</b> <br/> <span id="publish-level">${publishLevel}</span></p>

        <!-- <c:choose>
            <c:when test="${objectProperty.getTransitive()}">
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