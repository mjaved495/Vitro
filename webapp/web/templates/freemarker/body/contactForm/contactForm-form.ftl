<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Contact form -->

<div class="staticPageBackground feedbackForm">

    <h2>${title}</h2>
    
    <p>Thank you for your interest in ${siteName}. 
        Please submit this form with questions, comments, or feedback about the content of this site.
    </p>
        
    <form name="contact_form" id="contact_form" action="${formAction}" method="post" onsubmit="return ValidateForm('contact_form');">
        <input type="hidden" name="RequiredFields" value="webusername,webuseremail,s34gfd88p9x1"/>
        <input type="hidden" name="RequiredFieldsNames" value="Name,Email address,Comments"/>
        <input type="hidden" name="EmailFields" value="webuseremail"/>
        <input type="hidden" name="EmailFieldsNames" value="emailaddress"/>
        <input type="hidden" name="DeliveryType" value="contact"/>
    
        <label for="webusername">Full name</label>
        <p><input style="width:33%;" type="text" name="webusername" maxlength="255"/></p>
        <label for="webuseremail">Email address</label>
        <p><input style="width:25%;" type="text" name="webuseremail" maxlength="255"/></p>


        <label>Comments, questions, or suggestions</label>

        <textarea name="s34gfd88p9x1" rows="10" cols="90"></textarea>
        
        <div class="buttons">
            <input id="submit" type="submit" value="Send Mail"/>
        </div

        <p style="font-weight: bold; margin-top: 1em">Thank you!</p>
    </form>    
    
</div>

${scripts.add('<script type="text/javascript" src="${urls.base}/js/commentForm.js"></script>')}
