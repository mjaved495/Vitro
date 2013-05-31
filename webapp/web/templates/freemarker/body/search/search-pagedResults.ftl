<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for displaying paged search results -->




<h2 style="float:left">
<#escape x as x?html>
    Search results for '${querytext}'
    <#if classGroupName?has_content>limited to type '${classGroupName}'</#if>
    <#if typeName?has_content>limited to type '${typeName}'</#if>
</#escape>
<script type="text/javascript">
	var url = window.location.toString();	
	if (url.indexOf("?") == -1){
		var queryText = 'querytext=${querytext}';
	} else {
		var urlArray = url.split("?");
		var queryText = urlArray[1];
	}
	
	var urlsBase = '${urls.base}';
</script>
</h2>

<span id="downloadResults" title="Download Results">
	<img id="downloadIcon" src="images/download-icon.png" alt="Download Results"  />
</span>

<span id="searchHelp"><a href="${urls.base}/searchHelp" title="search help">Not the results you expected?</a></span>

<div class="contentsBrowseGroup" style="clear:left">

    <#-- Refinement links -->
    <#if classGroupLinks?has_content>
        <div class="searchTOC">
            <h4>Display only</h4>           
            <ul>           
            <#list classGroupLinks as link>
                <li><a href="${link.url}" title="class group link">${link.text}</a><span>(${link.count})</span></li>
            </#list>
            </ul>           
        </div>
    </#if>

    <#if classLinks?has_content>
        <div class="searchTOC">
            <#if classGroupName?has_content>
                <h4>Limit ${classGroupName} to</h4>
            <#else>
                <h4>Limit to</h4>
            </#if>
            <ul>           
            <#list classLinks as link>
                <li><a href="${link.url}" title="class link">${link.text}</a><span>(${link.count})</span></li>
            </#list>
            </ul>
        </div>
    </#if>

    <#-- Search results -->
    <ul class="searchhits">
        <#list individuals as individual>
            <li>                        
            	<@shortView uri=individual.uri viewContext="search" />
            </li>
        </#list>
    </ul>
    

    <#-- Paging controls -->
    <#if (pagingLinks?size > 0)>
        <div class="searchpages">
            Pages: 
            <#if prevPage??><a class="prev" href="${prevPage}" title="previous">Previous</a></#if>
            <#list pagingLinks as link>
                <#if link.url??>
                    <a href="${link.url}" title="page link">${link.text}</a>
                <#else>
                    <span>${link.text}</span> <#-- no link if current page -->
                </#if>
            </#list>
            <#if nextPage??><a class="next" href="${nextPage}" title="next">Next</a></#if>
        </div>
    </#if>
    <br />

    <#-- VIVO OpenSocial Extension by UCSF -->
    <#if openSocial??>
        <#if openSocial.visible>
        <h3>OpenSocial</h3>
            <script type="text/javascript" language="javascript">
                // find the 'Search' gadget(s).
                var searchGadgets = my.findGadgetsAttachingTo("gadgets-search");
                var keyword = '${querytext}';
                // add params to these gadgets
                if (keyword) {
                    for (var i = 0; i < searchGadgets.length; i++) {
                        var searchGadget = searchGadgets[i];
                        searchGadget.additionalParams = searchGadget.additionalParams || {};
                        searchGadget.additionalParams["keyword"] = keyword;
                    }
                }
                else {  // remove these gadgets
                    my.removeGadgets(searchGadgets);
                }
            </script>

            <div id="gadgets-search" class="gadgets-gadget-parent" style="display:inline-block"></div>
        </#if>
    </#if>

</div> <!-- end contentsBrowseGroup -->

${stylesheets.add('<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />',
  				  '<link rel="stylesheet" href="${urls.base}/css/search.css" />')}

${headScripts.add('<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>',
				  '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>'
                  )}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/searchDownload.js"></script>')}
