/* $This file is distributed under the terms of the license in /doc/license.txt$ */

$(document).ready(function(){

    // login form is hidden by default; use JavaScript to reveal
    $("#loginFormAndLinks").show();
  
    // focus on email or newpassword field
    $('.focus').focus();
    
    // fade in error alerts
    $('section#error-alert').css('display', 'none').fadeIn(1500);
    
    // fade in fash-message when user log out
    $('section#flash-message').css('display', 'none').fadeIn(1500);   
    
});