/* $This file is distributed under the terms of the license in /doc/license.txt$ */
  
    var classHierarchyUtils = {
    onLoad: function(urlBase,displayOption) {
        this.imagePath = urlBase + "/images/";
        this.initObjects();
        this.expandAll.hide();

        if ( displayOption == "all" ) {
            this.buildAllClassesHtml();
        }
        else if ( displayOption == "group" ) {
            this.buildClassGroupHtml();
        }
        else {
            this.buildClassHierarchyHtml();
            this.wireExpandLink();
        }
        
        if ( displayOption == "asserted" || displayOption == "inferred" ) {
            this.expandAll.show();
        }
        this.bindEventListeners();
    },

    initObjects: function() { 
        this.expandAll = $('span#expandAll').find('a');   
        this.classCounter = 1;
        this.expandCounter = 1;
        this.classHtml = "";
        this.clickableSpans = [] ;
        this.form = $('form#classHierarchyForm');
        this.select = $('select#displayOption');
        this.addClass = $('input#addClass');
        this.addGroup = $('input#addGroup');
    },

    bindEventListeners: function() {
        this.select.change(function() {
            if ( classHierarchyUtils.select.val() == "all") {
                classHierarchyUtils.form.attr("action", "listVClassWebapps");
            }
            else if ( classHierarchyUtils.select.val() == "group") {
                classHierarchyUtils.form.attr("action", "listGroups");
            }
            
            classHierarchyUtils.form.submit();
        });
        this.addClass.click(function() {
            classHierarchyUtils.form.attr("action", "vclass_retry");
            classHierarchyUtils.form.submit();
        });
        this.addGroup.click(function() {
            classHierarchyUtils.form.attr("action", "editForm?controller=Classgroup");
            classHierarchyUtils.form.submit();
        });
    },
     
    buildClassHierarchyHtml: function() {

        $.each(json, function() {
            $newClassSection = jQuery("<section></section>", {
                id: "classContainer" + classHierarchyUtils.classCounter
            });
            var descendants = "";
            var headerSpan = "";
            
            if ( this.children.length ) {
                descendants = classHierarchyUtils.getTheChildren(this);
                headerSpan = "<span class='headerSpanPlus' id='headerSpan" + classHierarchyUtils.classCounter 
                              + "' view='less'>&nbsp;</span>";
            }

            classHierarchyUtils.classHtml += "<div>" + this.name + headerSpan + "</div>" + "<table class='classHierarchy' id='classHierarchy" 
                                      + classHierarchyUtils.classCounter + "'>" ;

            if ( this.data.shortDef.length > 0 ) {
                classHierarchyUtils.classHtml += "<tr><td colspan='2'>" + this.data.shortDef + "</td></tr>";
            }

            if ( this.data.classGroup.length > 0 ) {
                classHierarchyUtils.classHtml += "<tr><td class='classDetail'>Class Group:</td><td>" + this.data.classGroup + "</td></tr>";
            }

            classHierarchyUtils.classHtml += "<tr><td class='classDetail'>Ontology:</td><td>" + this.data.ontology + "</td></tr>";

 
            classHierarchyUtils.classHtml += descendants;

            classHierarchyUtils.classHtml += "</table>";
       //     alert(classHierarchyUtils.classHtml);
            $newClassSection.html(classHierarchyUtils.classHtml);
            $newClassSection.appendTo($('section#container'));
            classHierarchyUtils.makeHeaderSpansClickable(classHierarchyUtils.classCounter);
            classHierarchyUtils.makeSubclassSpansClickable();
            classHierarchyUtils.clickableSpans = [] ;
            classHierarchyUtils.classHtml = "";
            classHierarchyUtils.classCounter += 1;
        });
    },

    getTheChildren: function(node) {
        var childDetails = "";
        var subclassString = " ";
        var ctr = 0
        $.each(node.children, function() {
            if ( ctr == 0 ) {
                childDetails += "<tr><td class='classDetail'>Subclasses:</td>";
                ctr = ctr + 1;
            }
            else {
                childDetails += "<tr><td></td>" ;
            }
            
            if ( this.children.length == 1 ) {
                subclassString += "<span style='font-size:0.8em'> (1 subclass)</span>"; 
            }
            else if ( this.children.length > 1 ) {
                subclassString += "<span style='font-size:0.8em'> (" + this.children.length + " subclasses)</span>";
            }
            childDetails += "<td class='subclassCell'><span class='subclassExpandPlus' id='subclassExpand" 
                            + classHierarchyUtils.expandCounter + "'>&nbsp;</span>" 
                            + this.name + subclassString + "</td></tr><tr><td></td><td><table id='subclassTable" 
                            + classHierarchyUtils.expandCounter + "' class='subclassTable'>";
            subclassString = " ";
            classHierarchyUtils.clickableSpans.push('subclassExpand' + classHierarchyUtils.expandCounter);
            
            classHierarchyUtils.expandCounter += 1;
            
            if ( this.data.shortDef.length > 0 ) {
                childDetails += "<tr><td colspan='2'>" + this.data.shortDef + "</td></tr>";
            }

            if ( this.data.classGroup.length > 0 ) {
                childDetails += "<tr><td class='classDetail'>Class Group:</td><td>" + this.data.classGroup + "</td></tr>";
            }

            childDetails += "<tr><td class='classDetail'>Ontology:</td><td>" + this.data.ontology + "</td></tr>";

            if ( this.children ) {
                var grandChildren = classHierarchyUtils.getTheChildren(this);
                childDetails += grandChildren;
            }
        });
        childDetails += "</table></td></tr>";
        return childDetails;
    },
    
    makeHeaderSpansClickable: function(ctr) {

        var $clickableHeader = $('section#classContainer' + ctr).find('span.headerSpanPlus');

        $clickableHeader.click(function() {
            if ( $clickableHeader.attr('view') == "less" ) {
                $clickableHeader.addClass("headerSpanMinus");
                $('table#classHierarchy' + ctr).find('span.subclassExpandPlus').addClass("subclassExpandMinus");
                $('table#classHierarchy' + ctr).find('table.subclassTable').show();
                $clickableHeader.attr('view', 'more' );
            }
            else {
                $clickableHeader.removeClass("headerSpanMinus");
                $('table#classHierarchy' + ctr).find('span.subclassExpandPlus').removeClass("subclassExpandMinus");
                $('table#classHierarchy' + ctr).find('table.subclassTable').hide();
                $clickableHeader.attr('view', 'less' );
            }
        });
    },//    $('myOjbect').css('background-image', 'url(' + imageUrl + ')');
    
    makeSubclassSpansClickable: function() {
        $.each(classHierarchyUtils.clickableSpans, function() {
            var currentSpan = this;
            var $clickableSpan = $('section#container').find('span#' + currentSpan);
            var $subclassTable = $('section#container').find('table#subclassTable' + currentSpan.replace("subclassExpand",""));

            $clickableSpan.click(function() {
                if ( $subclassTable.is(':visible') ) {
                    $subclassTable.hide();
                    $subclassTable.find('table.subclassTable').hide();
                    $subclassTable.find('span').removeClass("subclassExpandMinus");
                    $clickableSpan.removeClass("subclassExpandMinus");
                }
                else {
                    $subclassTable.show();
                    $clickableSpan.addClass("subclassExpandMinus");
                }
            });
        });
    },
    
    wireExpandLink: function() {
        this.expandAll.click(function() {
            if ( classHierarchyUtils.expandAll.text() == "expand all" ) {
                classHierarchyUtils.expandAll.text("collapse all");
                $('span.headerSpanPlus').addClass("headerSpanMinus");
                $('table.classHierarchy').find('span.subclassExpandPlus').addClass("subclassExpandMinus");
                $('table.classHierarchy').find('table.subclassTable').show();
                $('section#container').find('span.headerSpanPlus').attr('view','more');
            }
            else {
                classHierarchyUtils.expandAll.text("expand all");
                $('span.headerSpanPlus').removeClass("headerSpanMinus");
                $('table.classHierarchy').find('span.subclassExpandPlus').removeClass("subclassExpandMinus");
                $('table.classHierarchy').find('table.subclassTable').hide();
                $('section#container').find('span.headerSpanPlus').attr('view','less');
            }
        });
    },
     
    buildAllClassesHtml: function() {

        $.each(json, function() {
            $newClassSection = jQuery("<section></section>", {
                id: "classContainer" + classHierarchyUtils.classCounter
            });
            
            classHierarchyUtils.classHtml += "<div>" + this.name + "</div>" + "<table class='classHierarchy' id='classHierarchy" 
                                      + classHierarchyUtils.classCounter + "'>" ;

            if ( this.data.shortDef.length > 0 ) {
                classHierarchyUtils.classHtml += "<tr><td colspan='2'>" + this.data.shortDef + "</td></tr>";
            }

            if ( this.data.classGroup.length > 0 ) {
                classHierarchyUtils.classHtml += "<tr><td class='classDetail'>Class Group:</td><td>" + this.data.classGroup + "</td></tr>";
            }

            classHierarchyUtils.classHtml += "<tr><td class='classDetail'>Ontology:</td><td>" + this.data.ontology + "</td></tr>";

            classHierarchyUtils.classHtml += "</table>";

            $newClassSection.html(classHierarchyUtils.classHtml);
            $newClassSection.appendTo($('section#container'));
            classHierarchyUtils.classHtml = "";
            classHierarchyUtils.classCounter += 1;
        });
    },

    buildClassGroupHtml: function() {

        $.each(json, function() {
            $newClassSection = jQuery("<section></section>", {
                id: "classContainer" + classHierarchyUtils.classCounter
            });
            var descendants = "";
            
            if ( this.children.length ) {
                var ctr = 0;
                $.each(this.children, function() {
                    if ( ctr == 0 ) {
                        descendants += "<tr><td class='classDetail'>Classes:</td>";
                        ctr = ctr + 1;
                    }
                    else {
                        descendants += "<tr><td></td>" ;
                    }

                    descendants += "<td class='subclassCell'>" + this.name + "</td></tr>";
                    descendants += "<tr><td></td><td><table class='innerDefinition'><tr><td>" + this.data.shortDef + "</td></tr></table></td></tr>";
                    
                });
                descendants += "</table></td></tr>";
            }

            classHierarchyUtils.classHtml += "<div>" + this.name + "</div>" + "<table class='classHierarchy' id='classHierarchy" 
                                      + classHierarchyUtils.classCounter + "'>" ;

            if ( this.data.displayRank.length > 0 ) {
                classHierarchyUtils.classHtml += "<tr><td class='classDetail'>Display Rank:</td><td>" + this.data.displayRank + "</td></tr>"
            }
 
            classHierarchyUtils.classHtml += descendants;

            classHierarchyUtils.classHtml += "</table>";
       //     alert(classHierarchyUtils.classHtml);
            $newClassSection.html(classHierarchyUtils.classHtml);
            $newClassSection.appendTo($('section#container'));
            classHierarchyUtils.classHtml = "";
            classHierarchyUtils.classCounter += 1;
        });
    }
}
