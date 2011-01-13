<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for browsing individuals in class groups for menupages -->

<section id="browse-by" role="region">
    <h2>Browse by</h2>
    
    <nav role="navigation">
        <ul id="browse-childClasses">
            <#list vClassGroup as vClass>
                <#------------------------------------------------------------
                Need to replace vClassCamel with full URL that allows function
                to degrade gracefully in absence of JavaScript. Something
                similar to what Brian had setup with widget-browse.ftl
                ------------------------------------------------------------->
                <#assign vClassCamel = vClass.name?capitalize?replace(" ", "")?uncap_first />
                <#-- Only display vClasses with individuals -->
                <#if (vClass.entityCount > 0)>
                    <li id="${vClassCamel}"><a href="#${vClassCamel}" title="Browse all people in this class" data-uri="${vClass.URI}">${vClass.name} <span class="count-classes">(${vClass.entityCount})</span></a></li>
                </#if>
            </#list>
        </ul>
        <nav role="navigation">
            <#assign alphabet = ["A", "B", "C", "D", "E", "F", "G" "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"] />
            <ul id="alpha-browse-childClass">
                <li><a href="#" class="selected" data-alpha="all">All<span class="count-classes"> (n)</span></a></li>
                <#list alphabet as letter>
                    <li><a href="#" data-alpha="${letter?lower_case}" title="Browse all individuals whose names start with ${letter}">${letter}</a></li>
                </#list>
            </ul>
        </nav>
    </nav>
    
    <section id="individuals-in-childClass" role="region">
        <ul role="list">
            
        </ul>
    </section>
</section>