<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<script src="http://code.jquery.com/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/css/select2.min.css">
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/js/select2.min.js"></script>
<link href="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.css" rel="stylesheet"/>
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jstree/3.0.9/themes/default/style.min.css" />
<script src="//cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
<script src="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.js"></script>
<script src="/vivo/js/jquery.color.js"></script>
<script src="/vivo/js/helpers.js"></script>
<script language="JavaScript" type="text/javascript">
$(document).ready(function() {

	$.get("/vivo/edit_api/get_prop_hierarchy?uri="+encodeURIComponent($("#uri").val()), function(jsonData) {
		var data = JSON.parse(jsonData);
		console.log(data);
		$("#tree").jstree({
			"core": {
				"data": [ data ]
			} 
		}).on("ready.jstree", function(e, data) {
			$("#tree").on("click", "a", function(e) {
				// window.location.href = "/vivo/classpage?uri=" + encodeURIComponent($(this).attr("data-vclass-uri"));
				var uri = $(this).attr("data-vclass-uri");
				updateData(uri);
			});
			$("#tree").on("click", "i", function(e) {
				var link = $(this).parent().find("a").first();
				console.log(link.find(".jstree-icon").css("background-image"));
				if(link.find(".jstree-icon").css("background-image") != undefined && link.find(".jstree-icon").css("background-image").indexOf("bluedot-open.png") > -1) {
					link.find(".jstree-icon").css("background-image", "url('/vivo/images/bluedot.png')");
				}
				else {
					link.find(".jstree-icon").css("background-image", "url('/vivo/images/bluedot-open.png')");
				}
				
			});
		});
	});

	var updateData = function(uri) {
		$.get("/vivo/edit_api/propinfo", {"uri": uri}, function(jsonData) {

			/* jsonData will look like this:

			{"displayLevel": "display level",
			"updateLevel": "update level",
			"publishLevel": "publish level",
			"label": "the class label",
			"group": "group",
			"ontology": "ontology name",
			"superclasses": [{"uri": "some uri", "name": "the name"}, ... ],
			"subclasses": [{"uri": "some uri", "name": "the name"}, ... ],
			"eqclasses": [{"uri": "some uri", "name": "the name"}, ... ],
			"disjoints": [{"uri": "some uri", "name": "the name"}, ... ]} */

			var data = JSON.parse(jsonData);

			var ontology = data["ontology"];
			var displayLevel = data["displayLevel"];
			var updateLevel = data["updateLevel"];
			var publishLevel = data["publishLevel"];

			var propLabel = data["label"];
			var superproperties = data["superproperties"];
			var subproperties = data["subproperties"];
			var eqproperties = data["eqprops"];
			var domain = data["domain"];
			var range = data["range"];

			$("#vclass-uri").attr("data-vclass-uri", uri);
			$("#vclass-uri").val(uri);

			/*$("#update-level").text(updateLevel);
			$("#publish-level").text(publishLevel);
			$("#display-level").text(displayLevel);*/

			$("#ontology-name").text(ontology);
			//$("#class-group").text(group);

			$(".prop-label").html(propLabel + '<b class="object-property">(OBJECT PROPERTY)</b><i class="fa fa-pencil"></i>');
			$("#uri").val(uri);

			$("#superproperty-table").html('');

			for(var i = 0; i < superproperties.length; i++) {
				var superproperty = superproperties[i];
				var superpropertyDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + superproperty["uri"] + '" data-superproperty-uri="' + superproperty["uri"] + '"><p>' + superproperty["name"] + '</p></td><td class="item-spacer"></td><td class="item-action"> <i class="fa fa-pencil action action-edit-superproperty" title="Edit/replace"> </i></td><td class="item-action"> <i class="fa fa-trash action action-delete-superproperty" title="Remove this"></i> </td></tr>')
				$("#superproperty-table").append(superpropertyDiv);
			}

			$("#subproperty-table").html('');

			for(var i = 0; i < subproperties.length; i++) {
				var subproperty = subproperties[i];
				var subpropertyDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + subproperty["uri"] + '" data-subproperty-uri="' + subproperty["uri"] + '"><p>' + subproperty["name"] + '</p></td> <td class="item-spacer"></td><td class="item-action"> <i class="fa fa-pencil action action-edit-subproperty" title="Edit/replace"> </i></td><td class="item-action"> <i class="fa fa-trash action action-delete-subproperty" title="Remove this"></i> </td></tr>')
				$("#subproperty-table").append(subpropertyDiv);
			}

			$("#eqproperty-table").html('');

			for(var i = 0; i < eqproperties.length; i++) {
				var eqproperty = eqproperties[i];
				var eqpropertyDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + eqproperty["uri"] + '" data-eqproperty-uri="' + eqproperty["uri"] + '"><p>' + eqproperty["name"] + '</p></td> <td class="item-spacer"></td><td class="item-action"><i class="fa fa-pencil action action-edit-eqproperty" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-eqproperty" title="Remove this"></i></td></tr>')
				$("#eqproperty-table").append(eqpropertyDiv);
			}

			/*$("#disjoint-table").html('')

			for(var i = 0; i < disjoints.length; i++) {
				var disjoint = disjoints[i];
				var disjointDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + disjoint["uri"] + '" data-disjoint-uri="' + disjoint["uri"] + '"><p>' + disjoint["name"] + '</p></td> <td class="item-spacer"></td><td class="item-action"><i class="fa fa-pencil action action-edit-disjoint" title="Edit/replace"></i></td> <td class="item-action"> <i class="fa fa-trash action action-delete-disjoint" title="Remove this"></i></td></tr>')
				$("#disjoint-table").append(disjointDiv);
			}*/

			var domainDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + domain["uri"] + '"" data-vclass-uri="' + domain["uri"] + '"></p>' + domain["name"] + '</p></td> <td class="item-spacer"></td> <td class="item-action"><i class="fa fa-pencil action action-edit-domain" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-domain" title="Remove this"></i></td></tr>')
			$("#domain-table").append(domainDiv);

			var rangeDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + range["uri"] + '"" data-vclass-uri="' + range["uri"] + '"></p>' + range["name"] + '</p></td> <td class="item-spacer"></td> <td class="item-action"><i class="fa fa-pencil action action-edit-range" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-range" title="Remove this"></i></td></tr>')
			$("#range-table").append(rangeDiv);

			$.each($(".scroll-list"), function(i, div) {
				$(div).css("min-height", "60px");
				$(div).css("max-height", "61px");
				if($(div).height() <= 60) {
					$(div).css("overflow-y", "visible");
				}
				else {
					$(div).css("overflow-y", "scroll");
				}
			});

			window.history.pushState($("html").html(), document.title, "/vivo/propertypage?uri=" + encodeURIComponent(uri));
			
			//updateEventHandlers();
		});
	}

	$(".stretch-panel").css({'height': '50px', 'margin-top': 25});
	$(".stretch-panel-header").click(function() {
		if($(this).parent().height() > 50) {
			$(this).parent().animate({'height': 50})
		}
		else {
			$(this).parent().animate({'height': 250});
		}
	})

	$("#uri-check").click(function() {
		var uriInput = $("#uri-field").find("input");
		if(uriInput.attr("readonly")) {
			uriInput.removeAttr("readonly");
		}
		else {
			uriInput.attr("readonly", "true");
		}
	});

	function updateEventHandlers() {
		$(".action-edit-superproperty").click(actionEditSuperproperty);
		$(".action-delete-superproperty").click(actionDeleteSuperproperty);
		$(".action-edit-subproperty").click(actionEditSubproperty);
		$(".action-delete-subproperty").click(actionDeleteSubproperty);
		$(".action-edit-eqproperty").click(actionEditEqProperty);
		$(".action-delete-eqproperty").click(actionDeleteEqProperty);
		$(".action-edit-inverse").click(actionEditInverse);
		$(".action-delete-inverse").click(actionDeleteInverse);
		$(".action-edit-domain").click(actionEditDomain);
		$(".action-delete-domain").click(actionDeleteDomain);
		$(".action-edit-range").click(actionEditRange);
		$(".action-delete-range").click(actionDeleteRange);
	}

	var actionEditSuperproperty = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSuperpropertyCallback, "superproperty");
	}

	var actionDeleteSuperproperty = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSuperpropertyCallback);
	}

	var actionEditSubproperty = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSubpropertyCallback, "subproperty");
	}

	var actionDeleteSubproperty = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSubpropertyCallback);
	}

	var actionEditEqProperty = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditEqPropertyCallback, "eqproperty");
	}

	var actionDeleteEqProperty = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteEqpropertyCallback);
	}

	var actionEditInverse = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditInverseCallback, "inverseproperty");
	}

	var actionDeleteInverse = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteInverseCallback);
	}

	var actionEditDomain = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditDomainCallback, "domain-class");
	}

	var actionDeleteDomain = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteDomainCallback);
	}

	var actionEditRange = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditRangeCallback, "range-class");
	}

	var actionDeleteRange = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteRangeCallback);
	}

	var actionEditSuperpropertyCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldSuperpropertyURI = itemDetail.attr("data-superproperty-uri");
		$.get("/vivo/edit_api/uri", {"name": itemDetail.text(), "type": "class"}, function(data) {
			var newSuperpropertyURI = data;
			$.post("/vivo/edit_api/edit_superproperty", {"propertyURI": propertyURI, 
			"oldSuperpropertyURI": oldSuperpropertyURI, "newSuperpropertyURI": newSuperpropertyURI},
			function(res) {
				if(!(res === newSuperpropertyURI)) {
					console.log("error: " + res);
				}
			});
		});
	};

	var actionDeleteSuperpropertyCallback = function(row) {
		var superpropertyURI = row.find(".item-detail").attr("data-superproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_superproperty", {"superpropertyURI": superpropertyURI, "propertyURI": propertyURI}, function(res) {
			console.log(res);
		})
	}

	var actionEditSubpropertyCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldSubpropertyURI = itemDetail.attr("data-subproperty-uri");
		$.get("/vivo/edit_api/uri", {"name": itemDetail.text(), "type": "property"}, function(data) {
			var newSubpropertyURI = data;
			$.post("/vivo/edit_api/edit_subproperty", {"propertyURI": propertyURI, 
			"oldSubpropertyURI": oldSubpropertyURI, "newSubpropertyURI": newSubpropertyURI},
			function(res) {
				if(!(res === newSubpropertyURI)) {
					console.log("error: " + res);
				}
			});
		});
	}

	var actionDeleteSubpropertyCallback = function(row) {
		var subpropertyURI = row.find(".item-detail").attr("data-subproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_subproperty", {"subpropertyURI": subpropertyURI, "propertyURI": propertyURI}, function(res) {
			console.log(res);
		})
	}

	var actionEditEqPropertyCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldEqPropertyURI = itemDetail.attr("data-eqproperty-uri");
		$.get("/vivo/edit_api/uri", {"name": itemDetail.text(), "type": "property"}, function(data) {
			var newEqPropertyURI = data;
			$.post("/vivo/edit_api/edit_eqproperty", {"propertyURI": propertyURI, 
			"oldEqPropertyURI": oldEqPropertyURI, "newEqPropertyURI": newEqPropertyURI},
			function(res) {
				if(!(res === newEqPropertyURI)) {
					console.log("error: " + res);
				}
			});
		});
	}

	var actionDeleteEqpropertyCallback = function(row) {
		var eqpropertyURI = row.find(".item-detail").attr("data-eqproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_eqproperty", {"eqpropertyURI": eqpropertyURI, "propertyURI": propertyURI}, function(res) {
			console.log(res);
		})
	}

	var actionEditInverseCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldInverseURI = itemDetail.attr("data-inverse-uri");
		$.get("/vivo/edit_api/uri", {"name": itemDetail.text(), "type": "property"}, function(data) {
			var newInverseURI = data;
			$.post("/vivo/edit_api/edit_inverse", {"propertyURI": propertyURI, 
			"oldInverseURI": oldInverseURI, "newInverseURI": newInverseURI},
			function(res) {
				if(!(res === newInverseURI)) {
					console.log("error: " + res);
				}
			});
		});
	}

	var actionDeleteInverseCallback = function(row) {
		var subpropertyURI = row.find(".item-detail").attr("data-inverse-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_inverse", {"inverseURI": superpropertyURI, "propertyURI": propertyURI}, function(res) {
			console.log(res);
		})
	}

	var actionEditDomainCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldDomainURI = itemDetail.attr("data-domain-uri");
		$.get("/vivo/edit_api/uri", {"name": itemDetail.text(), "type": "class"}, function(data) {
			var newDomainURI = data;
			$.post("/vivo/edit_api/edit_domain", {"propertyURI": propertyURI, 
			"oldDomainURI": oldDomainURI, "newDomainURI": newDomainURI},
			function(res) {
				if(!(res === newDomainURI)) {
					console.log("error: " + res);
				}
			});
		});
	}

	var actionEditRangeCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldRangeURI = itemDetail.attr("data-range-uri");
		var newRangeURI = getURI(itemDetail.text());
		$.post("/vivo/edit_api/edit_range", {"propertyURI": propertyURI, 
		"oldRangeURI": oldRangeURI, "newRangeURI": newRangeURI},
		function(res) {
			if(!(res === newRangeURI)) {
				console.log("error: " + res);
			}
		});
	}

	var addSuperproperty = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var superpropertyURI = td.attr("data-superproperty-uri");
			$.post('/vivo/edit_api/add_superproperty', {'propertyURI': propertyURI, 'superpropertyURI': superpropertyURI}, function(res) {
				if(res != superpropertyURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-superproperty").click(actionEditSuperproperty);
					td.parent().find(".action-delete-superproperty").click(actionDeleteSuperproperty);
				}
			})
		}, "superproperty");
	}

	var addSubproperty = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var subpropertyURI = td.attr("data-subproperty-uri");
			$.post('/vivo/edit_api/add_subproperty', {'propertyURI': propertyURI, 'subpropertyURI': subpropertyURI}, function(res) {
				if(res != subpropertyURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-subproperty").click(actionEditSubproperty);
					td.parent().find(".action-delete-subproperty").click(actionDeleteSubproperty);
				}
			})
		}, "subproperty");
	}

	var addEqProperty = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var eqPropertyURI = td.attr("data-eqproperty-uri")
			$.ajax({
				"type": "POST",
				"url": '/vivo/edit_api/add_eqproperty', 
				"data": {'propertyURI': propertyURI, 'eqPropertyURI': eqPropertyURI}, 
				"success": function(res) {
					td.parent().find(".action-edit-eqproperty").click(actionEditEqProperty);
					td.parent().find(".action-delete-eqproperty").click(actionDeleteEqProperty);
				},
				"error": function(req, textStatus, err) {
					alert("The equivalent property could not be added.");
				}
			})
		}, "eqproperty");
	}

	var addInverse = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var inverseURI = getURI(td.text());
			$.post('/vivo/edit_api/add_inverse', {'propertyURI': propertyURI, 'inverseURI': inverseURI}, function(res) {
				if(res != inverseURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-inverse").click(actionEditInverse);
					td.parent().find(".action-delete-inverse").click(actionDeleteInverse);
				}
			})
		}, "inverseproperty");
	}

	var addDomain = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var domainURI = td.attr('data-domain-class-uri');
			$.post('/vivo/edit_api/add_domain', {'propertyURI': propertyURI, 'domainURI': domainURI}, function(res) {
				if(res != domainURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-domain").click(actionEditDomain);
					td.parent().find(".action-delete-domain").click(actionDeleteDomain);
				}
			})
		}, "domain-class");
	}

	var addRange = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var rangeURI = td.attr('data-range-class-uri');
			$.post('/vivo/edit_api/add_range', {'propertyURI': propertyURI, 'rangeURI': rangeURI}, function(res) {
				if(res != rangeURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-range").click(actionEditRange);
					td.parent().find(".action-delete-range").click(actionDeleteRange);
				}
			})
		}, "range-class");
	}

	var onTransitiveCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'transitive', 'value': 'true'}, function(data) {

		});
	}

	var onSymmetricCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'symmetric', 'value': 'true'}, function(data) {

		});
	}

	var onFunctionalCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'functional', 'value': 'true'}, function(data) {

		});
	}

	var onInverseFunctionalCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'inverse_functional', 'value': 'true'}, function(data) {

		});
	}

	var onReflexiveCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'reflexive', 'value': 'true'}, function(data) {

		});
	}

	$("#transitive-check").change(onTransitiveCheck);
	$("#symmetric-check").change(onSymmetricCheck);
	$("#functional-check").change(onFunctionalCheck);
	$("#inverse-functional-check").change(onInverseFunctionalCheck);
	$("#reflexive-check").change(onReflexiveCheck);

	$(".action-edit-superproperty").click(actionEditSuperproperty);
	$(".action-delete-superproperty").click(actionDeleteSuperproperty);
	$(".action-edit-subproperty").click(actionEditSubproperty);
	$(".action-delete-subproperty").click(actionDeleteSubproperty);
	$(".action-edit-eqproperty").click(actionEditEqProperty);
	$(".action-delete-eqproperty").click(actionDeleteEqProperty);
	$(".action-edit-inverse").click(actionEditInverse);
	$(".action-delete-inverse").click(actionDeleteInverse);
	$(".action-edit-domain").click(actionEditDomain);
	$(".action-delete-domain").click(actionDeleteDomain);
	$(".action-edit-range").click(actionEditRange);
	$(".action-delete-range").click(actionDeleteRange);

	$(".action-add-superproperty").click(addSuperproperty);
	$(".action-add-subproperty").click(addSubproperty);
	$(".action-add-eqproperty").click(addEqProperty);
	$(".action-add-inverse").click(addInverse);
	$(".action-add-domain").click(addDomain);
	$(".action-add-range").click(addRange);

});
</script>