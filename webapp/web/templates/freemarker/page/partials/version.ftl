<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for version/revision information -->

<#-- Only show version info if user is logged in -->
<#if loginName??>
    <div id="version">
        Version ${version.number}
    </div>
</#if>