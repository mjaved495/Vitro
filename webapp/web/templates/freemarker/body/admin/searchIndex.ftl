<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- 
    Template for the page that controls the updating or rebuilding of the Search Index. 
-->

<h2>Search Index Status</h2>

<#if worklevel == "IDLE">
    <#if hasPreviousBuild??>
        <p>Previous activity completed at ${since?string("hh:mm:ss a, MMMM dd, yyyy")}</p>
    </#if>
    
    <form action="${actionUrl}" method="POST">
    	<p>
            <input type="submit" name="update" value="Update">
            Add the latest changes to the index.
        </p>
        <p>
            <input type="submit" name="rebuild" value="Rebuild">
            Start with an empty index and build it completely.
        </p>
    </form>
<#else>
    <h3>The search index is currently being ${currentTask}.</h3>
    <p>since ${since?string("hh:mm:ss a, MMMM dd, yyyy")}, elapsed time ${elapsed}</p>
</#if>
