<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#include "/components/doctype.html">

<#include "/components/head.ftl">

<body>
    <div id="wrap" class="container">
        <div id="header">
        
            <#include "/components/identity.ftl">
            
            <#-- Note to UI team: do not change this div without also making the corresponding change in menu.jsp -->
            <div id="navAndSearch" class="block">
                <#include "/components/menu.ftl">  
                <#include "/components/search.ftl">                
            </div> <!-- navAndSearch --> 
            
            <#include "/components/breadcrumbs.ftl">         
        </div> <!-- header --> 

        <hr class="hidden" />

        <div id="contentwrap">      
            <div id="content">
                <#-- We don't do title here because some pages don't get a title, or it may not be the same as the <title> text.
                <h2>${title}</h2> -->                            
                ${body} 
            </div> <!-- content -->
        </div> <!-- contentwrap -->
    
        <#include "/components/footer.ftl">
                                      
    </div> <!-- wrap --> 
    
    <#include "/components/scripts.ftl"> 
</body>
</html>

<#-- How to add a stylesheet:
${stylesheets.addFromTheme("/sample.css");
${stylesheets.add("/themes/vivo-basic/css/sample.css"}
NOT (gets contextPath added twice):
${stylesheets.add(stylesheetDir + "/sample.css")}
-->