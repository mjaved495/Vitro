<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for Site Administration site configuration panel -->

<#if siteConfig?has_content>
    <section class="pageBodyGroup">
        <h3>Site Configuration</h3>
        
        <ul>
            <#if siteConfig.siteInfo?has_content>
                <li><a href="${siteConfig.siteInfo}">Site information</a></li>
            </#if>
            
            <#if siteConfig.menuManagement?has_content>
                <li><a href="${siteConfig.menuManagement}">Menu management</a></li>
            </#if>
            
            <#if siteConfig.internalClass?has_content>
                <li><a href="${siteConfig.internalClass}">Institutional internal class</a></li>
            </#if>
            
            <#if siteConfig.userAccounts?has_content>
                <li><a href="${siteConfig.userAccounts}">User accounts</a></li>
            </#if>           
            
            <#if siteConfig.startupStatus?has_content>
                <li>
                    <a href="${siteConfig.startupStatus}">Startup Status</a>
                    <#if siteConfig.startupStatusAlert>
                        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
                    </#if>
                </li>
            </#if>           
        </ul>
    </section>
</#if>