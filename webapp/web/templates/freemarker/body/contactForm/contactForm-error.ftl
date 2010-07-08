<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Contact form processing errors -->

<h2>${title}</h2>

<img src="${urls.siteIcons}/bomb.gif" alt="email error"/>

<p class="normal">An error occurred during the processing of your request.<br />
    <#if errorMessage?has_content>       
        <strong>${errorMessage}</strong>
    </#if>
</p> 


<p>Return to the <a href="${urls.home}">home page</a>.</p> 