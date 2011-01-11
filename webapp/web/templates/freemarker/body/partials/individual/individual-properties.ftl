<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for property listing on individual profile page -->

<#import "lib-properties.ftl" as p>

<#list propertyGroups.all as group>

    <#assign groupname = group.name(nameForOtherGroup)>
    
    <section class="property-group" role="region">
        <nav class="scroll-up" role="navigation">
            <a href="#property-group-menu">
                <img src="${urls.images}/individual/scroll-up.png" alt="scroll to property group menus" />
            </a>
        </nav>
   
        <#-- Display the group heading --> 
        <#if groupname?has_content>
            <h2 id="${groupname}">${groupname?capitalize}</h2>
        </#if>

        <#-- List the properties in the group -->        
        <#list group.properties as property>
            <article class="property" role="article">
                <#-- Property display name -->
                <h3>${property.name} <@p.addLink property showEditingLinks /></h3>
                <#-- List the statements for each property -->   
                <ul class="property-list" role="list"> 
                    <#-- data property -->  
                    <#if property.type == "data"> 
                        <@p.dataPropertyList property.statements showEditingLinks />

                    <#-- object property -->      
                    <#elseif property.collatedBySubclass> <#-- collated -->                            
                        <@p.collatedObjectPropertyList property showEditingLinks />
                    <#else> <#-- uncollated -->
                        <@p.objectPropertyList property.statements property.template showEditingLinks />
                    </#if>  
                </ul>                 
            </article> <!-- end property -->             
        </#list>                    
    </section> <!-- end property-group -->
</#list> 

