<?xml version="1.0" encoding="UTF-8"?>

<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<!-- TODO: replace /vivo with base URL, add onclicks to edit and delete within superclasses, equivalent, disjoint, etc. -->

<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:form="http://vitro.mannlib.cornell.edu/edit/tags" version="2.0">

<link rel="stylesheet" type="text/css" href="/vivo/css/ontology_editor.css"/> <!-- TODO replace /vivo with some base URL -->
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"/>

<input type="hidden" id="property-uri" data-property-uri="${dataProperty.getURI()}"/>

<c:choose>
    <c:when test="${empty allProps}">
        <input type="hidden" class="option-data" data-uri="" value="None"/>
    </c:when>
    <c:otherwise>
        <c:forEach items="${allProps}" var="prop">
            <input type="hidden" class="option-data" data-uri="${prop.getURI()}" value="${prop.getName()}"/>
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
                     <span class="vclass-label">${dataProperty.getLabel()} <b class="datatype-property">(DATATYPE PROPERTY)</b> <i class="fa fa-pencil"></i> </span>
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
                     <input type="text" readonly="true" value="${dataProperty.getURI()}" id="uri"></input>
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
                    <table>
                            <c:forEach items="${superproperties}" var="superproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${superproperty.getURI()}" data-superclass-uri="${superproperty.getURI()}"><p>${superproperty.getLabel()}</p></td> 
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
        <table>
            <tr>
                <td valign="bottom" colspan="4">
                   <p><b>Subproperties:</b> <span class="fa fa-plus action action-add-subproperty"></span></p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${subproperties}" var="subproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${subproperty.getURI()}" data-superclass-uri="${subproperty.getURI()}"><p>${subproperty.getLabel()}</p></td> 
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
                   <p><b>Equivalent properties:</b> <span class="fa fa-plus action action-add-eqproperty"></span></p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${eqproperties}" var="eqproperty">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${eqproperty.getURI()}" data-superclass-uri="${eqproperty.getURI()}"><p>${eqproperty.getLabel()}</p></td> 
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
        <table class="domain-table">
            <tr>
                <td valign="bottom" colspan="4">
                   <p id="add-domain-container"><b>Domain:</b> 
                    <c:choose>
                        <c:when test="${empty domains}">
                            <span class="fa fa-plus action action-add-domain"></span>
                        </c:when>
                        <c:otherwise></c:otherwise>
                    </c:choose>
                </p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${domains}" var="domain">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${domain.getURI()}" data-superclass-uri="${domain.getURI()}"><p>${domain.getName()}</p></td> 
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
        <table class="range-table">
            <tr><td colspan="4"><hr class="formDivider"/></td></tr>
            <tr>
                <td valign="bottom" colspan="4">
                   <p id="add-range-container"><b>Range:</b> 
                    <c:choose>
                        <c:when test="${empty ranges}">
                            <span class="fa fa-plus action action-add-range"></span>
                        </c:when>
                        <c:otherwise></c:otherwise>
                    </c:choose>
                </p>
                   <div class="scroll-list">
                    <table>
                            <c:forEach items="${ranges}" var="range">
                                <tr class="class-item">
                                   <td class="item-detail" id="editable-item-detail" title="${range.getURI()}" data-superclass-uri="${range.getURI()}"><p>${range.getName()}</p></td> 
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