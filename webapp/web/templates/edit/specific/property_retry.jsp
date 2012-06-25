<%-- $This file is distributed under the terms of the license in /doc/license.txt$ --%>

<%@ taglib prefix="form" uri="http://vitro.mannlib.cornell.edu/edit/tags" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%-- colspan set to 5 in PropertyRetryController.java --%>
<tr class="editformcell">
    <td valign="top" colspan="2">
        <b>Parent property</b><br/>
        <select name="ParentURI"><form:option name="ParentURI"/></select>
    </td>
    <td valign="top" colspan="2">
	    <b>Property group</b><br/>
	    <select name="GroupURI"><form:option name="GroupURI"/></select><br/>
        <i>for grouping properties on individual pages</i><br/>
	</td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
	<td style="vertical-align:top;" valign="top" colspan="1">
		<b>Ontology</b><br/>
        <c:choose>
        	<c:when test="${_action eq 'update'}">
				<select name="Namespace" disabled="disabled"><form:option name="Namespace"/></select><br/>
        	    <i>Edit via "change URI" on previous screen</i>
			</c:when>
			<c:otherwise>
				<br/><select name="Namespace"><form:option name="Namespace"/></select>
			</c:otherwise>
		</c:choose>
	</td>
	<td style="vertical-align:top;" valign="top" colspan="2">
		<b>Internal name*</b> (RDF local name)<br/><br/>
        <c:choose>
        	<c:when test="${_action eq 'update'}">
        	    <input type="text" name="LocalName" class="fullWidthInput" disabled="disabled" value="<form:value name='LocalName'/>" /><br/>
        		<i>Edit via "change URI"</i>
        	</c:when>
        	<c:otherwise>
        	    <input type="text" name="LocalName" class="fullWidthInput" value="<form:value name="LocalName"/>" /><br/>
        	        <i>must be valid XML without spaces; by</i><br/>
        	        <i>convention use camel case with no initial capital</i><br/>
			</c:otherwise>
		</c:choose>
        <c:set var="LocalNameError"><form:error name="LocalName"/></c:set>
        <c:if test="${!empty LocalNameError}">
            <span class="notice"><c:out value="${LocalNameError}"/></span>
        </c:if>
	</td>
    <td style="vertical-align:top;" valign="top" colspan="2">
        <b>Label for public display</b><br/><br/>
        <input type="text" name="DomainPublic" class="fullWidthInput" maxlength="80" value="<form:value name="DomainPublic"/>"  />
        <c:set var="DomainPublicError"><form:error name="DomainPublic"/></c:set>
        <c:if test="${!empty DomainPublicError}">
            <span class="notice"><c:out value="${DomainPublicError}"/></span>
        </c:if>
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
    <td style="vertical-align:top;" valign="top" colspan="1">
        <b>Inverse property ontology</b><br/>
        <c:choose>
        	<c:when test="${_action eq 'update'}">
				<select name="NamespaceInverse" disabled="disabled"><form:option name="NamespaceInverse"/></select><br/>
        	    <i>Edit via "change URI"</i><br/>
			</c:when>
			<c:otherwise>
				<select name="NamespaceInverse"><form:option name="NamespaceInverse"/></select>
			</c:otherwise>
		</c:choose>
	</td>
    <td style="vertical-align:top;" valign="top" colspan="2">
		<b>Inverse property internal name</b> (RDF local name)<br/>
        <c:choose>
        	<c:when test="${_action eq 'update'}">
        	    <input type="text" name="LocalNameInverse" class="fullWidthInput" disabled="disabled" value="<form:value name="LocalNameInverse"/>" /><br/>
        	    <i>Edit via "change URI"</i><br/>
			</c:when>
			<c:otherwise>
				<input type="text" name="LocalNameInverse" class="fullWidthInput" value="<form:value name="LocalNameInverse"/>" /><br/>
				<i>must be a valid XML name without spaces; by</i><br/>
				<i>convention use camel case with no initial capital</i><br/>
			</c:otherwise>
		</c:choose>
        <c:set var="LocalNameInverseError"><form:error name="LocalNameInverse"/></c:set>
        <c:if test="${!empty LocalNameInverseError}">
            <span class="notice"><c:out value="${LocalNameInverseError}"/></span>
        </c:if>
	</td>
    <td valign="top" style="vertical-align:top;" colspan="2">
        <b>Inverse property label</b><br/><br/>
        <input type="text" name="RangePublic" value="<form:value name="RangePublic"/>" class="fullWidthInput" maxlength="80" /><br/>
        <br/> 
        <c:set var="RangePublicError"><form:error name="RangePublic"/></c:set>
        <c:if test="${!empty RangePublicError}">
            <span class="notice"><c:out value="${RangePublicError}"/></span>
        </c:if>
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
    <td valign="top" colspan="2">
        <b>Domain class</b><br />
        <select name="DomainVClassURI"><form:option name="DomainVClassURI"/></select>
    </td>
    <td valign="top" colspan="2">
        <b>Range class</b><br />
        <select name="RangeVClassURI" ><form:option name="RangeVClassURI"/></select>
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
    <td valign="top" colspan="2">
        <b>Make this property be:</b><br/><br/>
        <c:choose>
            <c:when test="${transitive}">
                <input name="Transitive" type="checkbox" value="TRUE" checked="checked"/>transitive
            </c:when>
            <c:otherwise>
                <input name="Transitive" type="checkbox" value="TRUE"/>transitive
            </c:otherwise>
        </c:choose>
    </td>
    <td valign="top" colspan="1">
        <br/><br/>
        <c:choose>
            <c:when test="${symmetric}">
                <input name="Symmetric" type="checkbox" value="TRUE" checked="checked"/> symmetric
            </c:when>
            <c:otherwise>
                <input name="Symmetric" type="checkbox" value="TRUE"/> symmetric
            </c:otherwise>
        </c:choose>
    </td>
    <td valign="top" colspan="1">
        <br/><br/>
        <c:choose>
            <c:when test="${functional}">
                <input name="Functional" type="checkbox" value="TRUE" checked="checked"/> functional
            </c:when>
            <c:otherwise>
                <input name="Functional" type="checkbox" value="TRUE"/> functional
            </c:otherwise>
        </c:choose>
    </td>
    <td valign="top" colspan="1">
        <br/><br/>
        <c:choose>
            <c:when test="${inverseFunctional}">
                <input name="InverseFunctional" type="checkbox" value="TRUE" checked="checked"/> inverse functional
            </c:when>
            <c:otherwise>
                <input name="InverseFunctional" type="checkbox" value="TRUE"/> inverse functional
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
	<td valign="top" colspan="5">
		<b>Public Description</b> for front-end users, as it will appear on editing forms<br/>
		<textarea name="PublicDescription"><form:value name="PublicDescription"/></textarea>
        <c:set var="PublicDescriptionError"><form:error name="PublicDescription"/></c:set>
        <c:if test="${!empty PublicDescriptionError}">
            <span class="notice"><c:out value="${PublicDescriptionError}"/></span>
        </c:if>
	</td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
    <td valign="top" colspan="5">
        <b>Example</b> for ontology editors<br/>
        <input type="text" name="Example" style="width:90%;" value="<form:value name="Example"/>" />
            <c:set var="ExampleError"><form:error name="Example"/></c:set>
            <c:if test="${!empty ExampleError}">
                <span class="notice"><c:out value="${ExampleError}"/></span>
            </c:if>
    </td>
</tr>
<tr class="editformcell">
    <td valign="top" colspan="5">
        <b>Description</b> for ontology editors<br/>
        <textarea name="Description" style="width:90%;"><form:value name="Description"/></textarea>
        <c:set var="DescriptionError"><form:error name="Description"/></c:set>
        <c:if test="${!empty DescriptionError}">
            <span class="notice"><c:out value="${DescriptionError}"/></span>
        </c:if>
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
    <td valign="top" colspan="2">
        <b>Display level</b><br />
        <select name="HiddenFromDisplayBelowRoleLevelUsingRoleUri"><form:option name="HiddenFromDisplayBelowRoleLevelUsingRoleUri"/></select><br/>
        <i>specify least restrictive level allowed</i>
    </td>
    <td valign="top" colspan="2">
        <b>Update level</b><br/>
        <select name="ProhibitedFromUpdateBelowRoleLevelUsingRoleUri"><form:option name="ProhibitedFromUpdateBelowRoleLevelUsingRoleUri"/></select><br/>
        <i>specify least restrictive level allowed</i>
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
    <td valign="top" colspan="1">
        <b>Display tier</b> for this property<br/>
        <input type="text" name="DomainDisplayTier" value="<form:value name="DomainDisplayTier"/>" /><br/>
            <i><b>lower</b> numbers display first</i><br/>
        <c:set var="DomainDisplayTierError"><form:error name="DomainDisplayTier"/></c:set>
        <c:if test="${!empty DomainDisplayTierError}">
            <span class="notice"><c:out value="${DomainDisplayTierError}"/></span>
        </c:if>
    </td>
    <td valign="top" colspan="2">
        <b>Display tier</b> for inverse property<br/>
        <input type="text" name="RangeDisplayTier" value="<form:value name="RangeDisplayTier"/>" /><br/>
        <i><b>lower</b> numbers display first</i><br/>
        <c:set var="RangeDisplayTierError"><form:error name="RangeDisplayTier"/></c:set>
        <c:if test="${!empty RangeDisplayTierError}">
            <span class="notice"><c:out value="${RangeDisplayTierError}"/></span>
        </c:if>
    </td>
    <td valign="top" colspan="2">
        When displaying related individuals from different classes,<br/><br/>
        <c:choose>
            <c:when test="${collateBySubclass}">
                <input name="CollateBySubclass" type="checkbox" value="TRUE" checked="checked"/>collate by subclass
            </c:when>
            <c:otherwise>
                <input name="CollateBySubclass" type="checkbox" value="TRUE"/>collate by subclass
            </c:otherwise>            
        </c:choose>            
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
    <td valign="top" colspan="2">
        <b>Number</b> of individuals to display<br/>
        <input type="text" name="DomainDisplayLimit" value="<form:value name="DomainDisplayLimit"/>" /><br/>
            <i>before showing a &quot;more ...&quot; button</i><br/>
            <c:set var="DomainDisplayLimitError"><form:error name="DomainDisplayLimit"/></c:set>
            <c:if test="${!empty DomainDisplayLimitError}">
                <span class="notice"><c:out value="${DomainDisplayLimitError}"/></span>
            </c:if>
    </td>    
    <td valign="top" colspan="3">
        <b>Sort direction</b> for related individuals, alphabetically by their name (rdfs:label)<br/>
        <input type="text" name="DomainEntitySortDirection" value="<form:value name="DomainEntitySortDirection"/>" /><br/>
        <i>blank for ascending, &quot;desc&quot; for descending</i><br/>
        <i><b>Note:</b> will be ignored if a custom list view has been configured for this property</i><br/> 
        <c:set var="DomainEntitySortDirectionError"><form:error name="DomainEntitySortDirection"/></c:set>
        <c:if test="${!empty DomainEntitySortDirectionError}">
            <span class="notice"><c:out value="${DomainEntitySortDirectionError}"/></span>
        </c:if>
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>
<tr class="editformcell">
    <td valign="top" colspan="2">
    	Select related individuals from existing choices?<br/><br/>
       	<c:choose>
            <c:when test="${selectFromExisting}">
    	       	<input name="SelectFromExisting" type="checkbox" value="TRUE" checked="checked"/>provide selection
            </c:when>
            <c:otherwise>
               	<input name="SelectFromExisting" type="checkbox" value="TRUE"/>provide selection
            </c:otherwise>
        </c:choose>
    </td>
    <td valign="top" colspan="2">
    	Allow creating new related individuals?<br/><br/><br/>
       	<c:choose>
            <c:when test="${offerCreateNewOption}">
    	       	<input name="OfferCreateNewOption" type="checkbox" value="TRUE" checked="checked"/>offer create option
            </c:when>
            <c:otherwise>
               	<input name="OfferCreateNewOption" type="checkbox" value="TRUE"/>offer create option
            </c:otherwise>
        </c:choose>
    </td>
    <td valign="top" colspan="1">
        <b>Custom entry form</b><br/><br/>
        <input type="text" name="CustomEntryForm" class="fullWidthInput" value="<form:value name="CustomEntryForm"/>" />
            <c:set var="CustomEntryFormError"><form:error name="CustomEntryForm"/></c:set>
            <c:if test="${!empty CustomEntryFormError}">
                <span class="notice"><c:out value="${CustomEntryFormError}"/></span>
            </c:if>
    </td>
</tr>
<tr><td colspan="5"><hr color="Gainsboro"/></td></tr>

