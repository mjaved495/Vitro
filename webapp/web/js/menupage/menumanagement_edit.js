/* $This file is distributed under the terms of the license in /doc/license.txt$ */

var menuManagementEdit = {
    onLoad: function() {
        this.initObjects();
        this.bindEventListeners();
        this.toggleClassSelection();
        //this.validateMenuItemForm();
    },
    initObjects: function() {
        this.defaultTemplateRadio = $('input.default-template');
        this.customTemplateRadio = $('input.custom-template');
        this.customTemplate = $('#custom-template');
        this.changeContentType = $('#changeContentType');
        this.selectContentType = $('#selectContentType');
        this.existingContentType = $('#existingContentType');
        this.selectClassGroupDropdown = $('#selectClassGroup');
        this.classesForClassGroup = $('#classesInSelectedGroup');
        this.selectedGroupForPage = $('#selectedContentTypeValue');
        // this.selectClassesMessage = $('#selectClassesMessage');
        this.allClassesSelectedCheckbox = $('#allSelected');
        
    },
    bindEventListeners: function() {        
        // Listeners for vClass switching
        this.changeContentType.click(function() {
           menuManagementEdit.showClassGroups();
           return false;
        });
        this.selectClassGroupDropdown.change(function() {
            menuManagementEdit.chooseClassGroup();
        });
        // this.allClassesSelectedCheckbox.change(function() {
        //     menuManagementEdit.toggleClassSelection();
        // });
        
        // Listeners for template field
        this.defaultTemplateRadio.click(function(){
            menuManagementEdit.customTemplate.addClass('hidden');
        });
        this.customTemplateRadio.click(function(){
            // If checked, hide this input element
            menuManagementEdit.customTemplate.removeClass('hidden');
        });
        $("form").submit(function () { 
            var validationError = menuManagementEdit.validateMenuItemForm();
            if (validationError == "") {
                   $(this).submit();
               } else{
                   $('#error-alert').removeClass('hidden');
                   $('#error-alert p').html(validationError);
                   $.scrollTo({ top:0, left:0}, 500)
                   return false;
               } 
         });
    },
    showClassGroups: function() { //User has clicked change content type
    	//Show the section with the class group dropdown
    	this.selectContentType.removeClass("hidden");
    	//Hide the "change content type" section which shows the selected class group
    	this.existingContentType.addClass("hidden");
    	//Hide the checkboxes for classes within the class group
        this.classesForClassGroup.addClass("hidden");
    },
    hideClassGroups: function() { //User has selected class group/content type, page should show classes for class group and 'existing' type with change link
    	//Hide the class group dropdown
    	this.selectContentType.addClass("hidden");
    	//Show the "change content type" section which shows the selected class group
    	this.existingContentType.removeClass("hidden");
    	//Show the classes in the class group
    	this.classesForClassGroup.removeClass("hidden");
    	
    },
    toggleClassSelection: function() {
        /*To do: please fix so selecting all selects all classes and deselecting
         * any class will deselect all
         */
        /*
        if(this.allClassesSelectedCheckbox.is(':checked')) {
            $('#classInClassGroup').attr('checked', 'checked');
        } else {
            $('#classInClassGroup').removeAttr('checked');
        }*/
        // Check/unckeck all classes for selection
        $('input:checkbox[name=allSelected]').click(function(){
             // alert($('input:checkbox[name=classInClassGroup]'));
             if ( this.checked ) {
             // if checked, select all the checkboxes
             $('input:checkbox[name=classInClassGroup]').attr('checked','checked');

             } else {
             // if not checked, deselect all the checkboxes
               $('input:checkbox[name=classInClassGroup]').removeAttr('checked');
             }
        });

        $('input:checkbox[name=classInClassGroup]').click(function(){
            $('input:checkbox[name=allSelected]').removeAttr('checked');
        });
    },
    validateMenuItemForm: function() {
        var validationError = "";
        
        //Check menu name
        if ($("input[type=text][name=menuName]").val() == ""){
            validationError += "You must supply a Name<br />";
            }
        //Check pretty url     
        if ($("input[type=text][name=prettyUrl]").val() == ""){
            validationError += "You must supply a Pretty URL<br />";
        }
          
        if ($("input:radio[name=selectedTemplate]:checked").val() == "custom") {
        	if($("input[name=customTemplate]").val() == "") {
        		validationError += "You must supply a Template<br />"; 
        	}
        }
        
        //if no class group selected, this is an error
        if ($("#selectClassGroup").val() =='-1'){
            validationError += "You must supply a Content type<br />"; 
        } else {
        	//class group has been selected, make sure there is at least one class selected
        	var noClassesSelected = $("input[name='classInClassGroup']:checked").length;
        	if(noClassesSelected == 0) {
        		//at least one class should be selected
        		validationError += "You must supply some content for displaying <br />";
        	}
        }
      
       
        //check select class group
       
        return validationError;
    },
    chooseClassGroup: function() {        
        var url = "dataservice?getVClassesForVClassGroup=1&classgroupUri=";
        var vclassUri = this.selectClassGroupDropdown.val();
        url += encodeURIComponent(vclassUri);
        //Make ajax call to retrieve vclasses
        $.getJSON(url, function(results) {
  
          if ( results.classes.length == 0 ) {
     
          } else {
              //update existing content type with correct class group name and hide class group select again
              var _this = menuManagementEdit;
              menuManagementEdit.hideClassGroups();
      
              menuManagementEdit.selectedGroupForPage.html(results.classGroupName);
                //retrieve classes for class group and display with all selected
              var selectedClassesList = menuManagementEdit.classesForClassGroup.children('ul#selectedClasses');
              
              selectedClassesList.empty();
              selectedClassesList.append('<li class="ui-state-default"> <input type="checkbox" name="allSelected" id="allSelected" value="all" checked="checked" /> <label class="inline" for="All"> All</label> </li>');
              
              $.each(results.classes, function(i, item) {
                  var thisClass = results.classes[i];
                  var thisClassName = thisClass.name;
                  //When first selecting new content type, all classes should be selected
                  appendHtml = ' <li class="ui-state-default">' + 
                          '<input type="checkbox" checked="checked" name="classInClassGroup" value="' + thisClass.URI + '" />' +  
                         '<label class="inline" for="' + thisClassName + '"> ' + thisClassName + '</label>' + 
                          '</li>';
                  selectedClassesList.append(appendHtml);
              });
              menuManagementEdit.toggleClassSelection();
          }
 
        });
    }
};

$(document).ready(function() {   
    menuManagementEdit.onLoad();
});