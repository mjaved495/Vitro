<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#assign populatedClasses = 0 />

<#if vClassGroup??> <#-- the controller may put a null -->
    <#list vClassGroup as vClass>
        <#-- Check to see if any of the classes in this class group have individuals -->
        <#if (vClass.entityCount > 0)>
            <#assign populatedClasses = populatedClasses + 1 />
        </#if>
    </#list>
</#if>

<#if (populatedClasses == 0)>
    <#assign noData = true />
<#else>
    <#assign noData = false />
</#if>

<#assign noDataNotification>
    <h3>There is currently no ${page.title} content in the system</h3>
    <#if !user.loggedIn>
        <p>Please <a href="${urls.login}" title="log in to manage this site">log in</a> to manage content.</p>
    </#if>
    
    <p>You can browse all of the public content currently in the system using the <a href="${urls.index}" title="browse all content">index page</a>.</p>
</#assign>