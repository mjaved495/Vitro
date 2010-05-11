<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#if message??>
    <p>${message}</p>
<#else>
    <#list classGroups as classGroup>
        <h2>${classGroup.publicName}</h2>
        <ul>
            <#list classGroup.classes as class> 
                <li><a href="${class.url}">${class.name}</a> (${class.entityCount})</li>
            </#list>
        </ul>
    </#list>
</#if>