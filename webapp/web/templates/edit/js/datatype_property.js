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
$(function() {
		$.get("/vivo/edit_api/get_dataprop_hierarchy?uri="+encodeURIComponent($("#uri").val()), function(jsonData) {
			var data = JSON.parse(jsonData);
			$("#tree").jstree({
				"core": {
					"data": [ data ]
				},
				"plugins": [ "sort" ]
			});
			$("#tree").on("loaded.jstree", function(e, data) {
				console.log("ready");
				$("#tree").on("click", "a", function(e) {
					// window.location.href = "/vivo/classpage?uri=" + encodeURIComponent($(this).attr("data-vclass-uri"));
					var uri = $(this).attr("data-property-uri");
					updateData(uri);
				});
				$("#tree").on("click", "i", function(e) {
					var link = $(this).parent().find("a").first();
					if(link.find(".jstree-icon").css("background-image") != undefined && link.find(".jstree-icon").css("background-image").indexOf("greendot-open.png") > -1) {
						link.find(".jstree-icon").css("background-image", "url('/vivo/images/greendot.png')");
					}
					else {
						link.find(".jstree-icon").css("background-image", "url('/vivo/images/greendot-open.png')");
					}
					
				});
			});
		});
	
	var updateData = function(uri) {
		$("#property-uri").attr("data-property-uri", uri);
		$.get("/vivo/edit_api/datapropinfo", {"uri": uri}, function(jsonData) {
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

			var functional = data["functional"];

			$("#functional-check").prop('checked', false);
			
			if(functional) {
				$("#functional-check").prop('checked', true);
			}

			$("#data-property-uri").attr("data-property-uri", uri);
			$("#data-property-uri").val(uri);
			$("#ontology-name").text(ontology);

			$("#name").html(propLabel);
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

			$("#domain-table").html('');
			if(domain["uri"] != "") {
				var domainDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + domain["uri"] + '"" data-vclass-uri="' + domain["uri"] + '"></p>' + domain["name"] + '</p></td> <td class="item-spacer"></td> <td class="item-action"><i class="fa fa-pencil action action-edit-domain-class" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-domain-class" title="Remove this"></i></td></tr>')
				$("#domain-table").append(domainDiv);
				$("#add-domain-container").html("<b>Domain:</b>");
			}
			else {
				$("#add-domain-container").html("<b>Domain:</b> <span class='fa fa-plus action action-add-domain'></span>");
				$(".action-add-domain").click(addDomain);
			}
			

			$("#range-table").html('');
			if(range["uri"] != "") {
				var rangeDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + range["uri"] + '"" data-vclass-uri="' + range["uri"] + '"></p>' + range["name"] + '</p></td> <td class="item-spacer"></td> <td class="item-action"><i class="fa fa-pencil action action-edit-range-datatype" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-range-datatype" title="Remove this"></i></td></tr>')
				$("#range-table").append(rangeDiv);
				$("#add-range-container").html("<b>Range:</b>");
			}
			else {
				$("#add-range-container").html("<b>Range:</b> <span class='fa fa-plus action action-add-range'></span>");
				$(".action-add-range").click(addRange);
			}
			

			window.history.pushState($("html").html(), document.title, "/vivo/datapropertypage?uri=" + encodeURIComponent(uri));
			
			updateEventHandlers();
		});
	}

	var addProperty = function() {
		if($("#new-property-uri").length == 0) {
			var nameInput = $("<p>Property URI: <input type='text' id='new-property-uri'/></p>");
			var superpropertyInput = createAutocompleteInput("data-property"); // this will have the ID data-property-select
			nameInput.css("width", "200px");
			superpropertyInput.css("width", "200px");
			var confirmButton = $("<input type='submit' class='submit' value='Add property'/><");
			var cancelButton = $("<a href='#' class='cancel-add'>Cancel</a>");
			var itemsContainer = $("<div class='items-container'></div>");
			$(itemsContainer).append(nameInput);
			$(itemsContainer).append($("<p>Superproperty URI:</p>"));
			$(itemsContainer).append(superpropertyInput);
			$(itemsContainer).append(confirmButton);
			$(itemsContainer).append(cancelButton);
			$("#new-property-container").append(itemsContainer);
			superpropertyInput.select2({
				placeholder: "Select a superproperty"
			});
			$(cancelButton).click(function() {
				$(this).parent().remove();
				$("#add-data-property").click(addProperty);
			})
			$(confirmButton).click(function(e) {
				var selectedLabel = $("#data-property-select").val();
				var selectedURI = null;
				$(".property-option-data").each(function(i, el) {
					if($(el).val() == selectedLabel) {
						selectedURI = $(el).attr("data-uri");
					}
				})
				$.post("/vivo/edit_api/add_entity", {"uri": $("#new-property-uri").val(), "supertype": selectedURI, "type": "dataprop"}, function(label) {
					$.get("/vivo/edit_api/get_dataprop_hierarchy?uri="+encodeURIComponent($("#uri").val()), function(jsonData) {
						var data = JSON.parse(jsonData);
						$("#tree").jstree("destroy");
						$("#tree").jstree({
							"core": {
								"data": [ data ]
							},
							"plugins": [ "sort" ]
						});
					});
					$("#new-property-container").html('<p style="text-align:center;"><a href="#" class="add-data-property">Add Data Property</a></p>');
					$(".add-data-property").click(addProperty);
					if($("#uri").val() == selectedURI) { // if the superproperty is the same as the current page
						// add item to subproperty list
						var tableRow = $("<tr class='class-item'></tr>");
						var tdItemDetail = $("<td class='item-detail' id='editable-item-detail'></td>");
						tdItemDetail.text(label);
						tdItemDetail.attr('title', uri);
						tdItemDetail.attr('data-subproperty-uri', $("#new-property-uri").val());

						tableRow.append(tdItemDetail);

						var scope = $(".action-add-subproperty").parent().parent();
						scope.find("table").append(tableRow);

						tableRow.append($("<td class='item-spacer'></td>"));
						tableRow.append($("<td class='item-action'><i class='fa fa-pencil action action-edit-subproperty' title='Edit/replace'></i></td>"))
						tableRow.append($("<td class='item-action'><i class='fa fa-trash action action-delete-subproperty' title='Remove this'></i></td>"))

						tableRow.css({'background-color': '#FFFFAA'});
						tableRow.animate({'backgroundColor': '#FFFFFF'}, 1500);
						tdItemDetail.parent().find(".action-edit-subproperty").click(actionEditSubproperty);
						tdItemDetail.parent().find(".action-delete-subproperty").click(actionDeleteSubproperty);
					}
				});
			})
		}
		
	}

	var actionEditName = function() {
		if($("#name-input").length == 0) {
			$("#name").hide();
			var nameInput = $("<input type='text' id='name-input'/>");
			$("#name").parent().prepend(nameInput);
			$(nameInput).keypress(function(e) {
				if(e.keyCode == 13) {
					editDataPropName($(this).val());
				}
			});
		}
	}

	var editDataPropName = function(name) {
		$.post("/vivo/edit_api/edit_name", {"uri": $("#uri").val(), "newName": name, "type": "dataprop"}, function(data) {
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
		$(".action-edit-domain-class").click(actionEditDomain);
		$(".action-delete-domain-class").click(actionDeleteDomain);
		$(".action-edit-range-datatype").click(actionEditRange);
		$(".action-delete-range-datatype").click(actionDeleteRange);

		$("#functional-check").change(onFunctionalCheck);
		$(".action-edit-name").click(actionEditName);
	}

	var actionEditSuperproperty = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSuperpropertyCallback, "superproperty");
	}

	var actionDeleteSuperproperty = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSuperpropertyRequest);
	}

	var actionEditSubproperty = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSubpropertyCallback, "subproperty");
	}

	var actionDeleteSubproperty = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSubpropertyRequest);
	}

	var actionEditEqProperty = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditEqPropertyCallback, "eqproperty");
	}

	var actionDeleteEqProperty = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteEqpropertyRequest);
	}

	var actionEditDomain = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditDomainCallback, "domain-class");
	}

	var actionDeleteDomain = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteDomainRequest);
	}

	var actionEditRange = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditRangeCallback, "range-datatype");
	}

	var actionDeleteRange = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteRangeRequest);
	}

	var actionEditSuperpropertyCallback = function(itemDetail) {
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldSuperpropertyURI = itemDetail.attr("data-superproperty-uri");
		getURI(itemDetail.text(), "dataproperty", function(data) {
			var newSuperpropertyURI = data;
			editItem(propertyURI, oldSuperpropertyURI, newSuperpropertyURI, "super", "dataprop");
		});
	}

	var actionDeleteSuperpropertyRequest = function(row, callback) {
		var superpropertyURI = row.find(".item-detail").attr("data-superproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"uri": propertyURI, "itemURI": superpropertyURI, "relationship": "super", "type": "dataprop"}, function() {
			callback();
			$.get("/vivo/edit_api/get_dataprop_hierarchy?uri="+encodeURIComponent($("#uri").val()), function(jsonData) {
				var data = JSON.parse(jsonData);
				$("#tree").jstree("destroy");
				$("#tree").jstree({
					"core": {
						"data": [ data ]
					},
					"plugins": [ "sort" ]
				});
			});
		});
	}

	var actionEditSubpropertyCallback = function(itemDetail) {
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldSubpropertyURI = itemDetail.attr("data-subproperty-uri");
		getURI(itemDetail.text(), "dataproperty", function(data) {
			var newSubpropertyURI = data;
			editItem(propertyURI, oldSubpropertyURI, newSubpropertyURI, "sub", "dataprop");
		});
	}

	var actionDeleteSubpropertyRequest = function(row, callback) {
		var subpropertyURI = row.find(".item-detail").attr("data-subproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"uri": propertyURI, "itemURI": subpropertyURI, "relationship": "sub", "type": "dataprop"}, function() {
			callback();
			$.get("/vivo/edit_api/get_dataprop_hierarchy?uri="+encodeURIComponent($("#uri").val()), function(jsonData) {
				var data = JSON.parse(jsonData);
				$("#tree").jstree("destroy");
				$("#tree").jstree({
					"core": {
						"data": [ data ]
					},
					"plugins": [ "sort" ]
				});
			});
		});
	}

	var actionEditEqPropertyCallback = function(itemDetail) {
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldEqPropertyURI = itemDetail.attr("data-eqproperty-uri");
		getURI(itemDetail.text(), "dataproperty", function(data) {
			var newEqPropertyURI = data;
			editItem(propertyURI, oldEqPropertyURI, newEqPropertyURI, "eq", "dataprop");
		});
	}

	var actionDeleteEqPropertyRequest = function(row, callback) {
		var eqpropertyURI = row.find(".item-detail").attr("data-eqproperty-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"uri": propertyURI, "itemURI": eqPropertyURI, "relationship": "eq", "type": "dataprop"}, callback);
	}

	var actionEditDomainCallback = function(itemDetail) {
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldDomainURI = itemDetail.attr("data-domain-class-uri");
		getURI(itemDetail.text(), "class", function(data) {
			var newDomainURI = data;
			editItem(propertyURI, oldDomainURI, newDomainURI, "domain", "dataprop");
		});
	}

	var actionDeleteDomainRequest = function(row, callback) {
		var domainURI = row.find(".item-detail").attr("data-domain-class-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"uri": propertyURI, "itemURI": domainURI, "relationship": "domain", "type": "dataprop"}, function(res) {
			$("#add-domain-container").append("<span class='fa fa-plus action action-add-domain'></span>");
			$(".action-add-domain").click(addDomain);
			callback();
		})
	}

	var actionEditRangeCallback = function(itemDetail) {
		var propertyURI = $("#property-uri").attr("data-property-uri");
		var oldRangeURI = itemDetail.attr("data-range-uri");
		getURI(itemDetail.text(), "class", function(data) {
			var newRangeURI = data;
			editItem(propertyURI, oldRangeURI, newRangeURI, "range", "dataprop");
		});
	}

	var actionDeleteRangeRequest = function(row, callback) {
		var rangeURI = row.find(".item-detail").attr("data-range-datatype-uri");
		var propertyURI = $("#property-uri").attr("data-property-uri");
		$.post("/vivo/edit_api/delete_item", {"uri": rangeURI, "itemURI": propertyURI, "relationship": "range", "type": "dataprop"}, function(res) {
			$("#add-range-container").append("<span class='fa fa-plus action action-add-range'></span>");
			$(".action-add-range").click(addRange);
			callback();
		})
	}

	var addSuperproperty = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var superpropertyURI = td.attr("data-superproperty-uri");
			$.post('/vivo/edit_api/add_item', {'uri': propertyURI, 'itemURI': superpropertyURI, 'relationship': 'super', 'type': 'dataprop'}, function(res) {
				td.parent().find(".action-edit-superproperty").click(actionEditSuperproperty);
				td.parent().find(".action-delete-superproperty").click(actionDeleteSuperproperty);
				$.get("/vivo/edit_api/get_dataprop_hierarchy?uri="+encodeURIComponent($("#uri").val()), function(jsonData) {
					var data = JSON.parse(jsonData);
					$("#tree").jstree("destroy");
					$("#tree").jstree({
						"core": {
							"data": [ data ]
						},
						"plugins": [ "sort" ]
					});
				});
			})
		}, "superproperty");
	}

	var addSubproperty = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var subpropertyURI = td.attr("data-subproperty-uri");
			$.post('/vivo/edit_api/add_item', {'uri': propertyURI, 'itemURI': subpropertyURI, 'relationship': 'sub', 'type': 'dataprop'}, function(res) {
				td.parent().find(".action-edit-subproperty").click(actionEditSubproperty);
				td.parent().find(".action-delete-subproperty").click(actionDeleteSubproperty);
				$.get("/vivo/edit_api/get_dataprop_hierarchy?uri="+encodeURIComponent($("#uri").val()), function(jsonData) {
					var data = JSON.parse(jsonData);
					$("#tree").jstree("destroy");
					$("#tree").jstree({
						"core": {
							"data": [ data ]
						},
						"plugins": [ "sort" ]
					});
				});
			})
		}, "subproperty");
	}

	var addEqProperty = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var eqPropertyURI = td.attr("data-eqproperty-uri")
			$.ajax({
				"type": "POST",
				"url": '/vivo/edit_api/add_item', 
				"data": {'uri': propertyURI, 'itemURI': eqPropertyURI, 'relationship': 'eq', 'type': 'dataprop'}, 
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

	var addDomain = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var domainURI = td.attr('data-domain-class-uri');
			$.post('/vivo/edit_api/add_item', {'uri': propertyURI, 'itemURI': domainURI, 'relationship': 'domain', 'type': 'dataprop'}, function(res) {
				td.parent().find(".action-edit-domain-class").click(actionEditDomain);
				td.parent().find(".action-delete-domain-class").click(actionDeleteDomain);
				$("#add-domain-container").html("<b>Domain:</b>");
			})
		}, "domain-class");
	}

	var addRange = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var rangeURI = td.attr('data-range-datatype-uri');
			$.post('/vivo/edit_api/add_item', {'uri': propertyURI, 'itemURI': rangeURI, 'relationship': 'range', 'type': 'dataprop'}, function(res) {
				td.parent().find(".action-edit-range").click(actionEditRange);
				td.parent().find(".action-delete-range").click(actionDeleteRange);
				$("#add-range-container").html("<b>Range:</b>");
			})
		}, "range-datatype");
	}

	var onFunctionalCheck = function() {
		$.post('/vivo/edit_api/checkbox', {'objprop': false, 'propertyURI': $("#property-uri").attr("data-property-uri"), 'attribute': 'functional', 'value': $(this).prop('checked')});
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

	$("#functional-check").change(onFunctionalCheck);

	$(".action-edit-superproperty").click(actionEditSuperproperty);
	$(".action-delete-superproperty").click(actionDeleteSuperproperty);
	$(".action-edit-subproperty").click(actionEditSubproperty);
	$(".action-delete-subproperty").click(actionDeleteSubproperty);
	$(".action-edit-eqproperty").click(actionEditEqProperty);
	$(".action-delete-eqproperty").click(actionDeleteEqProperty);
	$(".action-edit-domain-class").click(actionEditDomain);
	$(".action-delete-domain-class").click(actionDeleteDomain);
	$(".action-edit-range-datatype").click(actionEditRange);
	$(".action-delete-range-datatype").click(actionDeleteRange);

	$(".action-add-superproperty").click(addSuperproperty);
	$(".action-add-subproperty").click(addSubproperty);
	$(".action-add-eqproperty").click(addEqProperty);
	$(".action-add-domain").click(addDomain);
	$(".action-add-range").click(addRange);

	$(".action-edit-name").click(actionEditName);

	$(".add-data-property").click(addProperty);
	$(".action-delete-property").click(deleteProperty);

})
</script>