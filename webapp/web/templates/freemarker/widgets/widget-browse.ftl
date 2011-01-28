<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Browse widget -->

<#macro assets>
  <#-- 
   Are there stylesheets or scripts needed? 
   ${stylesheets.add("/css/browse.css")} 
   ${scripts.add("/js/browse.js")}
   -->        
</#macro>

<#macro allClassGroups>
    <section id="browse" role="region">
        <h4>Browse</h4>
        
        <ul id="browse-classgroups" role="list">
        <#list vclassGroupList as group>
            <#if (group.individualCount > 0)>
                <li role="listitem"><a href="${urls.base}/${currentPage}?classgroupUri=${group.uri?url}">${group.publicName?capitalize} <span class="count-classes">(${group.individualCount})</span></a></li>
            </#if>
        </#list>
        </ul>
    </section>
    
    <#-- <@classGroup /> -->
</#macro>

<#macro classGroup>
    <section id="browse" role="region">
        <h4>Browse</h4>
        
         <section id="browse-classes" role="navigation">
             <nav>
                 <ul id="classes-in-classgroup" role="list">
                     <#list classes as class>
                        <#if (class.individualCount > 0)>
                            <li role="listitem"><a href="${urls.base}/${currentPage}?classgroupUri=${classGroup.uri?url}&vclassUri=${class.uri?url}">${class.name} <span class="count-individuals"> (${class.individualCount})</span></a></li>
                        </#if>
                     </#list>
                 </ul>
             </nav>
        </section>
    </section>
</#macro>

<#macro vclass>
    <section id="browse" role="region">
    <h4>Browse</h4>    
        <div>
            vclass ${class.name} from ${classGroup.publicName}
            This has classGroup, classes, individualsInClass and class.
        </div> 
         
        <ul>
            <#list individualsInClass as ind>
                <li><a href="${urls.base}/individual?uri=${ind.uri?url}">${ind.name}</a></li>
            </#list>
        </section>
</#macro>

<#macro vclassAlpha>
    <section id="browse" role="region">
    <h4>Browse</h4>     
        <div>vclassAlpha is not yet implemented.</div> 
    </section>
</#macro>
