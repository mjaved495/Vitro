<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

</div> <!-- #wrapper-content -->

<footer role="contentinfo">
    <p class="copyright">
        <#if copyright??>
            <small>&copy;${copyright.year?c}
            <#if copyright.url??>
                <a href="${copyright.url}">${copyright.text}</a>
            <#else>
                ${copyright.text}
            </#if>
             | <a class="terms" href="${urls.termsOfUse}">Terms of Use</a></small> | 
        </#if>
        Powered by <a class="powered-by-vitro" href="http://vitro.sourceforge.net"><strong>Vitro</strong></a>
        <#if user.hasRevisionInfoAccess>
             | Version <a href="${version.moreInfoUrl}">${version.label}</a>
        </#if>
    </p>
    
    <nav role="navigation">
        <ul id="footer-nav" role="list">
            <li role="listitem"><a href="${urls.about}">About</a></li>
            <#if urls.contact??>
                <li role="listitem"><a href="${urls.contact}">Contact Us</a></li>
            </#if> 
        </ul>
    </nav>
</footer>

<#include "scripts.ftl">