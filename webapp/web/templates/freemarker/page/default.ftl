<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#include "/components/doctype.html">

<#include "/components/head.ftl">

<body>
    <div id="wrap" class="container">
        <div id="header">
        
            <#include "/components/identity.ftl">
            
            <div id="navAndSearch" class="block">
                <#include "/components/menu.ftl">  
                <#include "/components/search.ftl">
            </div> <!-- navAndSearch --> 
                      
        </div> <!-- header --> 

        <hr class="hidden" />

        <div id="contentwrap">      
            <div id="content" <#if contentClass??> class="${contentClass}" </#if>>
                ${body} 
            </div> <!-- content -->
        </div> <!-- contentwrap -->
    
        <#include "/components/footer.ftl">
                                      
    </div> <!-- wrap -->  
</body>
</html>
