/* $This file is distributed under the terms of the license in /doc/license.txt$ */

// This file extends and proxies the default behavior defined in vitro/webapp/web/js/menupage/browseByVClass.js

// Saving the original getIndividuals function from browseByVClass
var getPersonIndividuals = browseByVClass.getIndividuals;

browseByVClass.getIndividuals = function(vclassUri, alpha, page, scroll) {
	url = encodeURIComponent(vclassUri);
	var restrictClasses = $('#restrictClasses').val();
	if(restrictClasses.length > 0) {
		if(restrictClasses.indexOf(",") != -1) {
			var restrictClassesArray = restrictClasses.split(",");
			var restrictUris = restrictClassesArray.join("&vclassId=");
			url += "&vclassId=" + restrictUris;
		} 
		else {
		//does this need to be uri encoded? - assuming already url encoded
			url += "&vclassId=" + restrictClasses;
		} 
	}
    url = this.dataServiceUrl + url;
    //Get restriction classes from url
   
    if ( alpha && alpha != "all") {
        url = url + '&alpha=' + alpha;
    }
    if ( page ) {
        url += '&page=' + page;
    } else {
        page = 1;
    }
    if ( typeof scroll === "undefined" ) {
        scroll = true;
    }
    
    // Scroll to #menupage-intro page unless told otherwise
    if ( scroll != false ) {
        // only scroll back up if we're past the top of the #browse-by section
        scrollPosition = browseByVClass.getPageScroll();
        browseByOffset = $('#browse-by').offset();
        if ( scrollPosition[1] > browseByOffset.top) {
            $.scrollTo('#menupage-intro', 500);
        }
    }
    
    $.getJSON(url, function(results) {
        var individualList = "";
        
        // Catch exceptions when empty individuals result set is returned
        // This is very likely to happen now since we don't have individual counts for each letter and always allow the result set to be filtered by any letter
        if ( results.individuals.length == 0 ) {
            browseByVClass.emptyResultSet(results.vclass, alpha)
        } else {
            $.each(results.individuals, function(i, item) {
                var individual, 
                    label, 
                    firstName, 
                    lastName, 
                    fullName, 
                    vclassName, 
                    preferredTitle, 
                    uri, 
                    profileUrl, 
                    image, 
                    listItem;
                    
                individual = results.individuals[i];
                label = individual.label;
                firstName = individual.firstName;
                lastName = individual.lastName;
                if ( firstName && lastName ) {
                    fullName = firstName + ' ' + lastName;
                } else {
                    fullName = label;
                }
                var vclassName = individual.vclassName;
                if ( individual.preferredTitle ) {
                    preferredTitle = individual.preferredTitle;
                }
                uri = individual.URI;
                profileUrl = individual.profileUrl;
                if ( !individual.thumbUrl ) {
                    image = browseByVClass.baseUrl + '/images/placeholders/person.thumbnail.jpg';
                } else {
                    image = browseByVClass.baseUrl + individual.thumbUrl;
                }
                // Build the content of each list item, piecing together each component
                listItem = '<li class="vcard individual foaf-person" role="listitem" role="navigation">';
                listItem += '<img src="'+ image +'" width="90" alt="'+ fullName +'" />';
                listItem += '<h1 class="fn thumb"><a href="'+ profileUrl +'" title="View the profile page for '+ fullName +'">'+ fullName +'</a></h1>';
                // Include the calculated preferred title (see above) only if it's not empty
                if ( preferredTitle ) {
                    listItem += '<span class="title">'+ preferredTitle +'</span>';
                } 
                listItem += '</li>';
                // browseByVClass.individualsInVClass.append(listItem);
                individualList += listItem;
            })
            
            // Remove existing content
            browseByVClass.wipeSlate();
            
            // And then add the new content
            browseByVClass.individualsInVClass.append(individualList);
            
            // Check to see if we're dealing with pagination
            if ( results.pages.length ) {
                pages = results.pages;
                browseByVClass.pagination(pages, page);
            }
            
            if(results.vclass) {
            	$('h3.selected-class').text(results.vclass.name);
            	// set selected class, alpha and page
            	browseByVClass.selectedVClass(results.vclass.URI);
            }
            browseByVClass.selectedAlpha(alpha);
        }
    });
};