<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for individual profile page -->

<#import "lib-list.ftl" as l>
<#import "lib-properties.ftl" as p>
<#assign core = "http://vivoweb.org/ontology/core#">

<#assign editingClass>
    <#if editStatus.showEditLinks>editing<#else></#if>
</#assign>

<#if editStatus.showAdminPanel>
    <#include "individual-adminPanel.ftl">
</#if>

<#assign propertyGroups = individual.propertyList>

<section id="individual-intro" class="vcard" role="region">
    <section id="left-side" role="region"> 
        <#-- Thumbnail -->
        <#if individual.thumbUrl??>
            <a href="${individual.imageUrl}"><img class="individual-photo2" src="${individual.thumbUrl}" title="click to view larger image" alt="${individual.name}" width="115" /></a>
        <#--<#elseif individual.person>
            <img class="individual-photo2" src="${urls.images}/placeholders/person.thumbnail.jpg" title = "no image" alt="placeholder image" width="115" />-->                                                       
        </#if>
    </section>

    <section id="individual-info" role="region">
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}">&larr; return to ${relatedSubject.name}</a></p>                
            <#else>                
                <h1 class="fn">
                    <#-- Label -->
                    ${individual.name}
                        
                    <#-- Moniker -->
                    <#if individual.moniker?has_content>
                        <span class="preferred-title">${individual.moniker}</span>                  
                    </#if>
                </h1>
            </#if>
        </header>
         
        <#-- Overview -->
        <#assign overview = propertyGroups.getPropertyAndRemoveFromList("${core}overview")!> 
        <#if overview?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
            <#list overview.statements as statement>
                <p class="individual-overview">${statement.value}</p>
            </#list>
        </#if>
        
        <nav role="navigation">
            <ul id ="individual-tools" role="list">
                <li role="listitem"><a class="picto-font picto-uri" href="#">j</a></li>
                <li role="listitem"><a class="picto-font picto-pdf" href="#">F</a></li>
                <#--<li role="listitem"><a class="picto-font picto-share" href="#">R</a></li>-->
                <li role="listitem"><a class="icon-rdf" href="#">RDF</a></li>
            </ul>
        </nav>
                
        <#-- Links -->
        <nav role="navigation">
            <ul id ="individual-urls" role="list">
                <#list individual.links as link>                               
                    <li role="listitem"><a href="${link.url}">${link.anchor}</a></li>                                 
                </#list>         
            </ul>
        </nav>
    </section>
</section>

<section id="publications-visualization" role="region">
    <section id="sparklines-publications" role="region">
         <#include "individual-sparklineVisualization.ftl">

         <#-- RY Will we have an individual--foaf-organization.ftl template? If so, move this there and remove from here.
         Also remove the method IndividualTemplateModel.isOrganization(). -->
         <#if individual.organization >
            <div style="width: 100%;">
                <div style="width: 30%;float:left;margin-top: 5%;margin-right: 10px;"><img src="${urls.images}/visualization/temporal_vis_icon.jpg"/></div>
                <div><h3>Temporal Graph <br/><a class="view-all-style" href="${urls.base}/visualization?vis=entity_comparison&vis_mode=${individual.moniker}&render_mode=standalone&uri=${individual.uri}">View <span class= "pictos-arrow-10">4</span></a></h3></div>
            </div>      
            <#--<div>VISMODE: ${individual.moniker}</div>-->
        </#if>
    </section>
</section>

<#-- Property group menu -->
<#assign nameForOtherGroup = "other">
<#include "individual-propertyGroupMenu.ftl">

<#-- Ontology properties -->
<#include "individual-properties.ftl">

<#-- Keywords -->
<#if individual.keywords?has_content>
    <p id="keywords">Keywords: ${individual.keywordString}</p>
</#if>

${stylesheets.add("/css/individual/individual.css")}
                           
<#-- RY Figure out which of these scripts really need to go into the head, and which are needed at all (e.g., tinyMCE??) -->
${headScripts.add("/js/jquery_plugins/getUrlParam.js",                  
                  "/js/jquery_plugins/colorAnimations.js",
                  "/js/jquery_plugins/jquery.form.js",
                  "/js/tiny_mce/tiny_mce.js", 
                  "/js/controls.js",
                  "http://www.google.com/jsapi?autoload=%7B%22modules%22%3A%5B%7B%22name%22%3A%22visualization%22%2C%22version%22%3A%221%22%2C%22packages%22%3A%5B%22areachart%22%2C%22imagesparkline%22%5D%7D%5D%7D",
                  "/js/toggle.js")}
                  
${scripts.add("/js/imageUpload/imageUploadUtils.js")}