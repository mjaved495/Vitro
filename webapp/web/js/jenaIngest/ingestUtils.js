

$(document).ready(function(){
	
  $('#takeuri').submit(function() {
    if ($('#uri1').val() == '') {
      alert('Please enter a value for Individual URI 1.');
      return false;
    }
    if ($('#uri2').val() == '') {
      alert('Please enter a value for Individual URI 2.');
      return false;
    }
  });
  
});