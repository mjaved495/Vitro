<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Confirmation that an password has been created. -->

<html>
    <head>
        <title>${subjectLine}</title>
    </head>
    <body>
        <p>
            ${userAccount.firstName} ${userAccount.lastName}
        </p>
        
        <p>
            <strong>Password successfully created.</strong>
        </p>
        
        <p>
            Your new password associated with ${userAccount.emailAddress} has been created.
        </p>
        
        <p>
            Thank you.
        </p>
    </body>
</html>