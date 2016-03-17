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
		$("#tree").jstree({
			"core": {
				"data": [ data ]
			},
			"plugins": [ "sort" ]
		}).on("ready.jstree", function(e, data) {
			$("#tree").on("click", "a", function(e) {
				// window.location.href = "/vivo/classpage?uri=" + encodeURIComponent($(this).attr("data-vclass-uri"));
				var uri = $(this).attr("data-property-uri");
				updateData(uri);
			});
			$("#tree").on("click", "i", function(e) {
				var link = $(this).parent().find("a").first();
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

			var data = JSON.parse(jsonData);

			var ontology = data["ontology"];
			var displayLevel = data["displayLevel"];
			var updateLevel = data["updateLevel"];
			var publishLevel = data["publishLevel"];

			var propLabel = data["label"];
			var superproperties = data["superproperties"];
			var subproperties = data["subproperties"];
			var inverse = data["inverse"];
			var eqproperties = data["eqprops"];
			var domain = data["domain"];
			var range = data["range"];

			var transitive = data["transitive"];
			var symmetric = data["symmetric"];
			var functional = data["functional"];
			var inverseFunctional = data["inverseFunctional"];

			$("#transitive-check").prop('checked', false);
			$("#symmetric-check").prop('checked', false);
			$("#functional-check").prop('checked', false);
			$("#inverse-functional-check").prop('checked', false);

			if(transitive) {
				$("#transitive-check").prop('checked', true);
			}
			if(symmetric) {
				$("#symmetric-check").prop('checked', true);
			}
			if(functional) {
				$("#functional-check").prop('checked', true);
			}
			if(inverseFunctional) {
				$("#inverse-functional-check").prop('checked', true);
			}

			$("#vclass-uri").attr("data-vclass-uri", uri);
			$("#vclass-uri").val(uri);

			$("#update-level").text(updateLevel);
			$("#publish-level").text(publishLevel);
			$("#display-level").text(displayLevel);

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

			$("#inverse-table").html('')
			if(inverse["uri"] != "") {
				var inverseDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + inverse["uri"] + '"" data-vclass-uri="' + inverse["uri"] + '"></p>' + inverse["name"] + '</p></td> <td class="item-spacer"></td> <td class="item-action"><i class="fa fa-pencil action action-edit-inverse-property" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-inverse-property" title="Remove this"></i></td></tr>')
				$("#inverse-table").append(inverseDiv);
				$("#add-inverse-container").html("<b>Inverse:</b>");
			}
			else {
				$("#add-inverse-container").html("<b>Inverse:</b> <span class='fa fa-plus action action-add-inverse'></span>");
			}
			

			$(".domain-table").html('');
			if(domain["uri"] != "") {
				var domainDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + domain["uri"] + '"" data-vclass-uri="' + domain["uri"] + '"></p>' + domain["name"] + '</p></td> <td class="item-spacer"></td> <td class="item-action"><i class="fa fa-pencil action action-edit-domain-class" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-domain-class" title="Remove this"></i></td></tr>')
				$(".domain-table").append(domainDiv);
				$("#add-domain-container").html("<b>Domain:</b>");
			}
			else {
				$("#add-domain-container").html("<b>Domain:</b> <span class='fa fa-plus action action-add-domain'></span>");
			}
			

			$(".range-table").html('');
			if(range["uri"] != "") {
				var rangeDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + range["uri"] + '"" data-vclass-uri="' + range["uri"] + '"></p>' + range["name"] + '</p></td> <td class="item-spacer"></td> <td class="item-action"><i class="fa fa-pencil action action-edit-range-class" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-range-class" title="Remove this"></i></td></tr>')
				$(".range-table").append(rangeDiv);
				$("#add-range-container").html("<b>Range:</b>");
			}
			else {
				$("#add-range-container").html("<b>Range:</b> <span class='fa fa-plus action action-add-range'></span>");
			}

			window.history.pushState($("html").html(), document.title, "/vivo/propertypage?uri=" + encodeURIComponent(uri));
			
			updateEventHandlers();
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

	var actionEditName = function() {
		if($("#name-input").length == 0) {
			$("#name").hide();
			var nameInput = $("<input type='text' id='name-input'/>");
			$("#name").parent().prepend(nameInput);
			$(nameInput).keypress(function(e) {
				if(e.keyCode == 13) {
					editPropName($(this).val());
				}
			});
		}
	}

	var addProperty = function() {
		if($("#new-property-name").length == 0) {
			var nameInput = $("<input type='text' id='new-property-name' placeholder='New property name...'/>");
			var superpropertyInput = createAutocompleteInput("object-property"); // this will have the ID object-property-select
			var confirmButton = $("<input type='submit' class='submit' value='Finish'/>");
			$("#new-property-container").append(nameInput);
			$("#new-property-container").append(superpropertyInput);
			$("#new-property-container").append(confirmButton);
			superpropertyInput.select2({
				placeholder: "Select a superproperty"
			});
			$(confirmButton).click(function(e) {
				$.post("/vivo/edit_api/add_entity", {"name": $("#new-property-name").val(), "supertype": $("#object-property-select").val(), "type": "objprop"});
			})
		}
		
	}

	var editPropName = function(name) {
		$.post("/vivo/edit_api/edit_name", {"uri": $("#uri").val(), "newName": name, "type": "objprop"}, function(data) {
			setTimeout(function() {
				$("#name-input").remove();
				$("#name").show();
				$("#name").text(data);
			}, 2000);
		})
	}

	function updateEventHandlers() {
		$(".action-edit-superproperty").click(actionEditSuperproperty);
		$(".action-delete-superproperty").click(actionDeleteSuperproperty);
		$(".action-edit-subproperty").click(actionEditSubproperty);
		$(".action-delete-subproperty").click(actionDeleteSubproperty);
		$(".action-edit-eqproperty").click(actionEditEqProperty);
		$(".action-delete-eqproperty").click(actionDeleteEqProperty);
		$(".action-edit-inverse-property").click(actionEditInverse);
		$(".action-delete-inverse-property").click(actionDeleteInverse);
		$(".action-edit-domain-class").click(actionEditDomain);
		$(".action-delete-domain-class").click(actionDeleteDomain);
		$(".action-edit-range-class").click(actionEditRange);
		$(".action-delete-range-class").click(actionDeleteRange);
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
		replaceWithInput(itemDetail, actionEditInverseCallback, "inverse-property");
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
		getURI(itemDetail.text(), "property", function(data) {
			var newSuperpropertyURI = data;
			editItem(propertyURI, oldSuperpropertyURI, newSuperpropertyURI, "super", "objprop");
		});
	};

	var actionDeleteSuperpropertyCallback = function(row) {
		var superpropertyURI = row.find(".item-detail").attr("data-superproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"uri": propertyURI, "itemURI": superpropertyURI, "relationship": "super", "type": "objprop"});
	}

	var actionEditSubpropertyCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldSubpropertyURI = itemDetail.attr("data-subproperty-uri");
		getURI(itemDetail.text(), "property", function(data) {
			var newSubpropertyURI = data;
			editItem(propertyURI, oldSubpropertyURI, newSubpropertyURI, "sub", "objprop");
		});
	}

	var actionDeleteSubpropertyCallback = function(row) {
		var subpropertyURI = row.find(".item-detail").attr("data-subproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"uri": propertyURI, "itemURI": subpropertyURI, "relationship": "sub", "type": "objprop"});
	}

	var actionEditEqPropertyCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldEqPropertyURI = itemDetail.attr("data-eqproperty-uri");
		getURI(itemDetail.text(), "property", function(data) {
			var newEqPropertyURI = data;
			editItem(propertyURI, oldEqPropertyURI, newEqPropertyURI, "eq", "objprop");
		});
	}

	var actionDeleteEqpropertyCallback = function(row) {
		var eqpropertyURI = row.find(".item-detail").attr("data-eqproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"itemURI": eqpropertyURI, "uri": propertyURI, "relationship": "eq", "type": "objprop"});
	}

	var actionEditInverseCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldInverseURI = itemDetail.attr("data-inverse-uri");
		getURI(itemDetail.text(), "property", function(data) {
			var newInverseURI = data;
			editItem(propertyURI, oldInverseURI, newInverseURI, "inverse", "objprop");
		});
	}

	var actionDeleteInverseCallback = function(row) {
		var inverseURI = row.find(".item-detail").attr("data-inverse-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"itemURI": inverseURI, "uri": propertyURI, "relationship": "inverse", "type": "objprop"});
	}

	var actionEditDomainCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldDomainURI = itemDetail.attr("data-domain-class-uri");
		getURI(itemDetail.text(), "class", function(data) {
			var newDomainURI = data;
			editItem(propertyURI, oldDomainURI, newDomainURI, "domain", "objprop");
		});
	}

	var actionDeleteDomainCallback = function(row) {
		var domainURI = row.find(".item-detail").attr("data-domain-class-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"itemURI": domainURI, "uri": propertyURI, "relationship": "domain", "type": "objprop"}, function(res) {
			$("#add-domain-container").append("<span class='fa fa-plus action action-add-domain'></span>");
			$(".action-add-domain").click(addDomain);
		})
	}

	var actionEditRangeCallback = function(itemDetail) { 
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldRangeURI = itemDetail.attr("data-range-uri");
		getURI(itemDetail.text(), "class", function(data) {
			var newRangeURI = data;
			editItem(propertyURI, oldRangeURI, newRangeURI, "range", "objprop");
		});
	}

	var actionDeleteRangeCallback = function(row) {
		var rangeURI = row.find(".item-detail").attr("data-range-class-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"itemURI": rangeURI, "uri": propertyURI, "relationship": "range", "type": "objprop"}, function(res) {
			$("#add-range-container").append("<span class='fa fa-plus action action-add-range'></span>");
			$(".action-add-range").click(addRange);
		})
	}

	var addSuperproperty = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var superpropertyURI = td.attr("data-superproperty-uri");
			$.post('/vivo/edit_api/add_superproperty', {'propertyURI': propertyURI, 'superpropertyURI': superpropertyURI}, function(res) {
				td.parent().find(".action-edit-superproperty").click(actionEditSuperproperty);
				td.parent().find(".action-delete-superproperty").click(actionDeleteSuperproperty);
			})
		}, "superproperty");
	}

	var addSubproperty = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var subpropertyURI = td.attr("data-subproperty-uri");
			$.post('/vivo/edit_api/add_subproperty', {'propertyURI': propertyURI, 'subpropertyURI': subpropertyURI}, function(res) {
				td.parent().find(".action-edit-subproperty").click(actionEditSubproperty);
				td.parent().find(".action-delete-subproperty").click(actionDeleteSubproperty);
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
				"data": {'propertyURI': propertyURI, 'eqpropertyURI': eqPropertyURI}, 
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
			var inverseURI = td.attr("data-inverse-property-uri");
			$.post('/vivo/edit_api/add_inverse', {'propertyURI': propertyURI, 'inverseURI': inverseURI}, function(res) {
				td.parent().find(".action-edit-inverse-property").click(actionEditInverse);
				td.parent().find(".action-delete-inverse-property").click(actionDeleteInverse);
				$("#add-inverse-container").html("<b>Inverse:</b>");
			})
		}, "inverse-property");
	}

	var addDomain = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var domainURI = td.attr('data-domain-class-uri');
			$.post('/vivo/edit_api/add_domain', {'propertyURI': propertyURI, 'domainURI': domainURI}, function(res) {
				td.parent().find(".action-edit-domain-class").click(actionEditDomain);
				td.parent().find(".action-delete-domain-class").click(actionDeleteDomain);
				$("#add-domain-container").html("<b>Domain:</b>");
			})
		}, "domain-class");
	}

	var addRange = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var rangeURI = td.attr('data-range-class-uri');
			$.post('/vivo/edit_api/add_range', {'propertyURI': propertyURI, 'rangeURI': rangeURI}, function(res) {
				td.parent().find(".action-edit-range").click(actionEditRange);
				td.parent().find(".action-delete-range").click(actionDeleteRange);
				$("#add-range-container").html("<b>Range:</b>");
			})
		}, "range-class");
	}

	var onTransitiveCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'objprop': true, 'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'transitive', 'value': $(this).prop('checked')});
	}

	var onSymmetricCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'objprop': true, 'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'symmetric', 'value': $(this).prop('checked')});
	}

	var onFunctionalCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'objprop': true, 'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'functional', 'value': $(this).prop('checked')});
	}

	var onInverseFunctionalCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'objprop': true, 'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'inverse_functional', 'value': $(this).prop('checked')});
	}

	var onReflexiveCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'objprop': true, 'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'reflexive', 'value': $(this).prop('checked')});
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
	$(".action-edit-inverse-property").click(actionEditInverse);
	$(".action-delete-inverse-property").click(actionDeleteInverse);
	$(".action-edit-domain-class").click(actionEditDomain);
	$(".action-delete-domain-class").click(actionDeleteDomain);
	$(".action-edit-range-class").click(actionEditRange);
	$(".action-delete-range-class").click(actionDeleteRange);

	$(".action-add-superproperty").click(addSuperproperty);
	$(".action-add-subproperty").click(addSubproperty);
	$(".action-add-eqproperty").click(addEqProperty);
	$(".action-add-inverse").click(addInverse);
	$(".action-add-domain").click(addDomain);
	$(".action-add-range").click(addRange);

	$(".action-edit-name").click(actionEditName);

	$(".add-object-property").click(addProperty);

});
</script>