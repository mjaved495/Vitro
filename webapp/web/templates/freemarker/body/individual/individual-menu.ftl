<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Menu management page (uses individual display mechanism) -->

<#include "individual-setup.ftl">

<#assign hasElement = propertyGroups.pullProperty("${namespaces.display}hasElement")!>

<#assign addNewMenuItemUrl = "${urls.base}/menuManagementController?cmd=add" >

<#if hasElement?has_content>
    <script type="text/javascript">
        var menuItemData = [];
    </script>
    
    <h3>${i18n().menu_ordering}</h3>
    
    <#-- List the menu items -->
    <ul class="menuItems">
        <#list hasElement.statements as statement>
            <li class="menuItem"><#include "${hasElement.template}"> <span class="controls"><!--p.editingLinks "hasElement" statement editable /--></span></li>
        </#list>
    </ul>
    
    <#-- Link to add a new menu item -->
    <#if editable>
        <#if addNewMenuItemUrl?has_content>
        <form id="pageListForm" action="${urls.base}/editRequestDispatch" method="get">
            <input type="hidden" name="typeOfNew" value="http://vitro.mannlib.cornell.edu/ontologies/display/1.1#Page">              
            <input type="hidden" name="switchToDisplayModel" value="1">
            <input type="hidden" name="editForm" value="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.ManagePageGenerator" role="input">
       		<input type="hidden" name="addMenuItem" value="true" />
       	<input id="submit" value="Add new menu page" role="button" type="submit" >
        
        </form>
            <br />
            <p class="note">${i18n().refresh_page_after_reordering}</p>
        </#if>
    </#if>
    
    ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />',
                      '<link rel="stylesheet" href="${urls.base}/css/individual/menuManagement-menuItems.css" />')}
                      
    ${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
    
    <#assign positionPredicate = "${namespaces.display}menuPosition" />
    
    <script type="text/javascript">
        // <#-- We need the controller to provide ${reorderUrl}. This is where ajax request will be sent on drag-n-drop events. -->
        var menuManagementData = {
            reorderUrl: '${reorderUrl}',
            positionPredicate: '${positionPredicate}'
        };
    </script>
    
    ${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/menuManagement.js"></script>')}
<#else>
    <p id="error-alert">${i18n().display_has_element_error}</p>
</#if>