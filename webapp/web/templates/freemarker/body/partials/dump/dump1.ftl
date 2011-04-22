<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for dump directives -->

<#-- Styles here are temporary; use stylesheets.add() once that's working (see below) -->
<style>
div.dump {
    margin-bottom: .5em;
    border-top: 1px solid #ccc;
    border-bottom: 1px solid #ccc; 
    padding-top: .75em;
    padding-bottom: .75em;
}

.dump ul ul {
    margin-left: 1.5em;
}

.dump ul li {
    list-style: none;
}

.dump ul li p {
    margin-bottom: .25em;  
}

.dump ul li.item {
    margin-bottom: 1.25em;
}

.dump ul li.item .value { 
    margin-left: 1.5em;
}

.dump ul.methods li {
    margin-bottom: .25em;
}
</style>

<div class="dump">
    <h3>${title}</h3>
    
    <@doDump dump />
</div>

<#macro doDump dump>
    <#if dump?keys?has_content>
        <ul>
            <#list dump?keys as key>
                <li>
                    <p><strong>Variable name:</strong> ${key}</p>  
                    
                    <#local type = dump[key].type>
                    <#if type == "Directive" || type == "Method"> 
                        <@doMethod dump[key] />
                    <#else>                
                        <@doTypeAndValue dump[key] />
                    </#if>
                </li>       
            </#list>
        </ul> 
    </#if>
</#macro>

<#macro doTypeAndValue map>
    <#local type = map.type!>
    <#if type?has_content>
        <p><strong>Type:</strong> ${type}</p>
        
        <#if map.dateType?has_content>
            <p><strong>Date type:</strong> ${map.dateType}</p>
        </#if>
    </#if>   

    <#local value = map.value!>    
    <#if value??>
        <div class="values">
            <#if type?contains(".")><@doObjectValue value />
            <#elseif value?is_sequence><@doSequenceValue value type />
            <#elseif value?is_hash_ex><@doMapValue value />            
            <#else><@doScalarValue value />
            </#if>
       </div>
   </#if>                    
</#macro>

<#macro doObjectValue obj>
    <#if obj.properties?has_content>
        <p><strong>Properties:</strong></p>
        <ul class="properties">
            <#list obj.properties?keys as property>
                <@liItem>
                    ${property} => <@divValue><@doTypeAndValue obj.properties[property] /></@divValue>
                </@liItem>
            </#list>
        </ul>
    </#if>
    
    <#if obj.methods?has_content>
        <p><strong>Methods:</strong</p>
        <ul class="methods">
            <#list obj.methods as method>
                <@liItem>${method}</@liItem>
            </#list>
        </ul>
    </#if>
</#macro>

<#macro doSequenceValue seq type>
    <strong>Values:</strong>
    <#if seq?has_content>
        <ul class="sequence">
            <#list seq as item>
                <@liItem>
                    <#if type == "Sequence">
                        Item ${item_index}: 
                        <@divValue><@doTypeAndValue item /></@divValue>
                    <#else><@doTypeAndValue item />
                    </#if>                    
                </@liItem>
            </#list>
        </ul>
     <#else>no values
     </#if>
</#macro>

<#macro doMapValue map>
    <strong>Values:</strong>
    <#if map?has_content>
        <ul class="map">
            <#list map?keys as key>
                <@liItem>
                    ${key} => <@divValue><@doTypeAndValue map[key] /></@divValue>
                </@liItem>
            </#list>
        </ul>
    <#else>no values
    </#if>
</#macro>

<#macro doScalarValue value>
    <strong>Value:</strong>
    
    <#if value?is_string>${value}
    <#elseif value?is_number>${value?c}
    <#elseif value?is_boolean>${value?string}
    <#elseif value?is_date>${value?string("EEEE, MMMM dd, yyyy hh:mm:ss a zzz")}
    </#if>    
</#macro>

<#macro doMethod method>
    <p><strong>Type:</strong> ${method.type}</p>
    <#local help = method.help>
    <#if help?has_content>
        <p><strong>Help:</strong><p>
        <ul class="help">
            <#list help?keys as key>
                <li>
                    <#local value = help[key]>
                    <@divValue>                        
                        <#if value?is_string><p><strong>${key?capitalize}:</strong> ${value}</p>
                        <#else>
                            <p><strong>${key?capitalize}:</strong></p>
                            <ul>
                                <#if value?is_sequence>
                                    <#list value as item>
                                        <li>${item}</li>
                                    </#list>
                                <#elseif value?is_hash_ex>
                                    <#list value?keys as key>
                                        <li><strong>${key}:</strong> ${value[key]}</li>
                                    </#list>
                                </#if>
                            </ul>
                        </#if>
                    </@divValue>
                </li>
            </#list>        
        </ul>
    </#if>
</#macro>

<#macro divValue>
    <div class="value"><#nested></div>
</#macro>

<#macro liItem>
    <li class="item"><#nested></li>
</#macro>


<#-- This will work after we move stylesheets to Configuration sharedVariables 
${stylesheets.add('<link rel="stylesheet" href="/css/fmdump.css">')}
-->
