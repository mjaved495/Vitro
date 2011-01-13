<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template to setup and call scripts for menupages -->

<#----------------------------------------------------------------------------------
requestedPage is currently provided by FreemarkerHttpServlet. Should this be moved
to PageController? Maybe we should have Java provide the domain name directly
instead of the full URL of the requested page? Chintan was also asking for a
template variable with the domain name for an AJAX request with visualizations.
------------------------------------------------------------------------------------>
<#assign domainName = requestedPage?substring(0, requestedPage?index_of("/", 7)) />

<#list vClassGroup as vClass>
    <#if (vClass.entityCount > 0)>
        <#assign firstNonEmptyVClass = vClass.URI />
        <#break>
    </#if>
    <#-- test if we're at the last class. If we've gotten this far, none of the classes have any individuals -->
    <#if !vClass_has_next>
        <#assign firstNonEmptyVClass = "false">
    </#if>
</#list>

<script type="text/javascript">
    var menupageData = {
        baseUrl: '${domainName + urls.base}',
        dataServiceUrl: '${domainName + urls.base}/dataservice?getLuceneIndividualsByVClass=1&vclassId=',
        defaultBrowseVClassUri: '${firstNonEmptyVClass}'
    };
</script>

${scripts.add("/js/menupage/browseByVClass.js")}