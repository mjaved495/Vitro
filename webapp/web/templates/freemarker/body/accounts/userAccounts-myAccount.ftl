<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Template for editing a user account -->

<h3>Edit account</h3>

    <#if errorEmailIsEmpty??>
        <#assign errorMessage = "You must supply an email address." />
    </#if>
    
    <#if errorEmailInUse??>
        <#assign errorMessage = "An account with that email address already exists." />
    </#if>
    
    <#if errorEmailInvalidFormat??>
        <#assign errorMessage = "'${emailAddress}' is not a valid email address." />
    </#if>
    
    <#if errorFirstNameIsEmpty??>
        <#assign errorMessage = "You must supply a first name." />
    </#if>
    
    <#if errorLastNameIsEmpty??>
        <#assign errorMessage = "You must supply a last name." />
    </#if>
    
    <#if errorPasswordIsEmpty??>
        <#assign errorMessage = "No password supplied." />
    </#if>
    
    <#if errorPasswordIsWrongLength??>
        <#assign errorMessage = "Password must be between ${minimumLength} and ${maximumLength} characters." />
    </#if>
    
    <#if errorPasswordsDontMatch??>
        <#assign errorMessage = "Passwords do not match." />
    </#if>
    
    <#if errorMessage?has_content>
        <section id="error-alert" role="alert">
            <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon"/>
            <p>${errorMessage}</p>
        </section>
    </#if>


    <#if confirmChange??>
        <#assign confirmMessage = "Your changes have been saved." />
    </#if>
    
    <#if confirmEmailSent??>
        <#assign confirmMessage = "Your changes have been saved. A confirmation email has been sent to ${emailAddress}." />
    </#if>
    
    <#if confirmMessage?has_content>
        <section id="error-alert" role="alert">
            <img src="${urls.images}/iconConfirmation.png" width="24" height="24" alert="Confirmation icon"/>
            <p>${confirmMessage}</p>
        </section>
    </#if>

<section id="my-account" role="region">
    <fieldset>
        <legend>My account</legend>

        <form method="POST" action="${formUrls.myAccount}" class="customForm" role="my account">
            <label for="email-address">Email address<span class="requiredHint"> *</span></label>
            <input type="text" name="emailAddress" value="${emailAddress}" id="email-address" role="input "/>

			<p>Note: if email changes, a confirmation email will be sent to the new email address entered above.</p>
			
            <label for="first-name">First name<span class="requiredHint"> *</span></label> 
            <input type="text" name="firstName" value="${firstName}" id="first-name" role="input "/>

            <label for="last-name">Last name<span class="requiredHint"> *</span></label> 
            <input type="text" name="lastName" value="${lastName}" id="last-name" role="input "/>

            <#if !externalAuth??>
                <label for="new-password">New password<span class="requiredHint"> *</span></label>
                <input type="password" name="newPassword" value="${newPassword}" id="new-password" role="input "/>

                <p>Minimum of ${minimumLength} characters in length.</p>
                <p>Leaving this blank means that the password will not be changed.</p>

                <label for="confirm-password">Confirm initial password<span class="requiredHint"> *</span></label> 
                <input type="text" name="confirmPassword" value="${confirmPassword}" id="confirm-password" role="input "/>
            </#if>

            <input type="submit" name="submitMyAccount" value="Save changes" class="submit"/>

            <p class="requiredHint">* required fields</p>
        </form>
    </fieldset>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}