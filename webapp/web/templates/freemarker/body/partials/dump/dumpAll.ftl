<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for dumpAll directive (dumping template data model) -->

<div class="dump datamodel">

    <h3>Data model dump for template <em>${containingTemplate}</em></h3>
    
    <h4>VARIABLES</h4>
    
    <ul>
        <#list models as model>
            <li>${model}</li> 
        </#list>
    </ul>
    
    <h4>DIRECTIVES</h4>
    
    <ul>
        <#list directives as directive>
            <li>${directive}</li>
        </#list> 
    </ul> 
      
</div>

${stylesheets.add("/css/dump.css")}