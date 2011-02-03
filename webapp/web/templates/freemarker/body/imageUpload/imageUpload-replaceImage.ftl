<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Upload a replacement main image for an Individual. -->

${scripts.add("/js/jquery.js")}
${scripts.add("/js/imageUpload/imageUploadUtils.js")}

${stylesheets.add("/css/uploadImages.css")}

<section id="photoUpload" role="region">
    <h2>Photo Upload</h2>

    <#if errorMessage??>
        <section id="error-alert" role="alert"><img src="${urls.images}/iconAlert.png" alt="Error alert icon" />
            <p>${errorMessage}</p>
        </section>
    </#if>
    
    <section id="photoUploadDefaultImage" role="region">
        <h3>Current Photo</h3>
            <img src="${thumbnailUrl}" width="115" alt="Individual photo" />
            
            <a class="thumbnail" href="${deleteUrl}">Delete photo</a>
    </section>

    <form id="photoUploadForm" action="${formAction}" enctype="multipart/form-data" method="post" role="form">
        <label>Replace Photo <span> (JPEG, GIF or PNG)</span></label>

        <input type="file" name="datafile" size="30">
        <input class="submit" type="submit" value="Upload photo"> 

        <span class="or"> or <a class="cancel"  href="${cancelUrl}">Cancel</a></span>
     </form>
</section>