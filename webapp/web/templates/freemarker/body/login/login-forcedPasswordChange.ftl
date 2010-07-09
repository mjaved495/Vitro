<#-- $This file is distributed under the terms of the license in /doc/license.txt$ -->

<#-- Log in template for accessing site admin -->


${stylesheets.addFromTheme("/login.css")}

<div id="formLogin" class="pageBodyGroup">
       <h2>Create Your New Password</h2>
       
       <#if errorMessage??>
           <div id="errorAlert"><img src="${urls.siteIcons}/iconAlert.png" width="24" height="24" alert="Error alert icon"/>
                  <p>${errorMessage}</p>
           </div>
       </#if>
       
       <form action="${formAction}" method="post">
              <label for="newPassword">New Password</label>
              <input type="password" name="newPassword"  />
              <p class="passwordNote">Please enter a password with more than 5 characters</p>
              <label for="confirmPassword">Confirm Password</label>
              <input type="password" name="confirmPassword"  />
              <input name="passwordChangeForm" type="submit" class="submit" value="Save Changes"/>
       </form>
</div>

