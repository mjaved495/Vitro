<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Default individual profile page template -->

<#include "individual-setup.ftl">

<#if individual.showAdminPanel>
    <#include "individual-adminPanel.ftl">
</#if>

<section id="individual-intro" class="vcard" role="region">
    <section id="share-contact" role="region"> 
        <#-- Image -->
        <@p.imageLinks individual propertyGroups editable />
    </section>

    <section id="individual-info" role="region">
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}">&larr; return to ${relatedSubject.name}</a></p>                
            <#else>                
                <h1 class="fn">
                    <#-- Label -->
                    <#assign label = individual.nameStatement>
                    ${label.value}
                    <@p.editingLinks label label editable />
                        
                    <#-- Moniker -->
                    <#if individual.moniker?has_content>
                        <span class="preferred-title">${individual.moniker}</span>                  
                    </#if>
                </h1>
            </#if>
        </header>
        
        <nav role="navigation">
            <ul id ="individual-tools" role="list">
                <#--<li role="listitem"><a class="picto-font picto-uri" href="#">j</a></li>
                <li role="listitem"><a class="picto-font picto-pdf" href="#">F</a></li>
                <li role="listitem"><a class="picto-font picto-share" href="#">R</a></li>-->
                <#assign rdfUrl = individual.rdfUrl>
                <#if rdfUrl??>
                    <li role="listitem"><a class="icon-rdf" href="${rdfUrl}">RDF</a></li>
                </#if>
            </ul>
        </nav>
                
        <#-- Links -->
        <@p.vitroLinks propertyGroups editable  />
    </section>
</section>

<#assign nameForOtherGroup = "other"> <#-- used by both individual-propertyGroupMenu.ftl and individual-properties.ftl -->

<#-- Property group menu -->
<#include "individual-propertyGroupMenu.ftl">

<#-- Ontology properties -->
<#include "individual-properties.ftl">

${stylesheets.add("/css/individual/individual.css")}
                           
<#-- RY Figure out which of these scripts really need to go into the head, and which are needed at all (e.g., tinyMCE??) -->
${headScripts.add("/js/jquery_plugins/getURLParam.js",                  
                  "/js/jquery_plugins/colorAnimations.js",
                  "/js/jquery_plugins/jquery.form.js",
                  "/js/tiny_mce/tiny_mce.js", 
                  "/js/controls.js",
                  "/js/toggle.js")}
                  
${scripts.add("/js/imageUpload/imageUploadUtils.js")}