<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- List individual members of a class. -->

<div class="contents">
    <div class="individualList">
        <h2>${title}</h2>
        <#if subtitle??>
            <h4>${subtitle}</h4>
        </#if>
        
        <#if message??>
            <p>${message}</p>
        <#else>
            <#-- RY NEED TO ACCOUNT FOR p:process stuff -->
            <ul>
                <#list individuals as individual>
                    <li>
                        <#include "partials/defaultIndividualListView.ftl">              
                    </li>
                </#list>
            </ul>
        </#if>
    </div>   
</div>
