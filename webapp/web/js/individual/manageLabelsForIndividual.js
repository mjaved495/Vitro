/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var manageLabels = {

    /* *** Initial page setup *** */
   
    onLoad: function() {
    
            this.mixIn();               
            this.initPage();
            
            var selectedRadio;       
        },

    mixIn: function() {

        // Get the custom form data from the page
        $.extend(this, customFormData);
    },

    // Initial page setup. Called only at page load.
    initPage: function() {
        
        $('input#submit').attr('disabled', 'disabled');
        $('input#submit').addClass('disabledSubmit');
        this.bindEventListeners();
                       
    },
    
    bindEventListeners: function() {
               
        $('input:radio').click( function() {
            manageLabels.selectedRadio = $(this);
            $('input#submit').attr('disabled', '');
            $('input#submit').removeClass('disabledSubmit');            
        });

        $('input#submit').click( function() {
             manageLabels.processLabel(manageLabels.selectedRadio);
        });

    },
                      
    processLabel: function(selectedRadio) {
        
        // PrimitiveDelete only handles one statement, so we have to use PrimitiveRdfEdit to handle multiple
        // retractions if they exist. But PrimitiveRdfEdit also handles assertions, so pass an empty string
        // for "additions"
        var add = "";
        var retract = "";
        
        $('input:radio').each( function() {
            if ( !$(this).is(':checked') ) {
                retract += " <" + manageLabels.individualUri + "> <http://www.w3.org/2000/01/rdf-schema#label> "
                                + "\"" + $(this).attr('id') + "\"" + $(this).attr('tagOrType') + " ." ;
            }
        });

        retract = retract.substring(0,retract.length -1);

        $.ajax({
            url: manageLabels.processingUrl,
            type: 'POST', 
            data: {
                additions: add,
                retractions: retract
            },
            dataType: 'json',
            context: selectedRadio, // context for callback
            complete: function(request, status) {
                
                if (status == 'success') {
                    window.location = $('a.cancel').attr('href');
                }
                else {
                    alert('Error processing request: the unchecked labels could not be deleted.');
                    selectedRadio.removeAttr('checked');
                }
            }
        });        

    },

};

$(document).ready(function() {   
    manageLabels.onLoad();
}); 
