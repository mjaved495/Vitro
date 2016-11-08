<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<script src="http://code.jquery.com/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/css/select2.min.css">
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/js/select2.min.js"></script>
<link href="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.css" rel="stylesheet"/>
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jstree/3.0.9/themes/default/style.min.css" />
<script src="//cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
<script src="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.js"></script>
<script src="./js/jquery.color.js"></script>
<script src="./js/helpers.js"></script>
<script language="JavaScript" type="text/javascript"> 
$(document).ready(function() {

	$.get("./edit_api/get_hierarchy?uri=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23FacultyMember", function(jsonData) {
		var data = JSON.parse(jsonData);
		$("#tree").jstree({
			"core": {
				"data": [ data ]
			},
			"plugins": [ "sort" ]
		}).on("ready.jstree", function(e, data) {
			$("#tree").on("click", "a", function(e) {
				// window.location.href = "./classpage?uri=" + encodeURIComponent($(this).attr("data-vclass-uri"));
				var uri = $(this).attr("data-vclass-uri");
				updateData(uri);
			});
			$("#tree").on("click", "i", function(e) {
				var link = $(this).parent().find("a").first();
				if(link.find(".jstree-icon").css("background-image") != undefined && link.find(".jstree-icon").css("background-image").indexOf("orangedot-open.png") > -1) {
					link.find(".jstree-icon").css("background-image", "url('./images/orangedot.png')");
				}
				else {
					link.find(".jstree-icon").css("background-image", "url('./images/orangedot-open.png')");
				}
				
			});
		});
	});

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

	/* stretch panel */

	$(".stretch-panel").css({'height': '50px', 'margin-top': 25});
	$(".stretch-panel-header").click(function() {
		if($(this).parent().height() > 50) {
			$(this).parent().animate({'height': 50})
		}
		else {
			$(this).parent().animate({'height': 250});
		}
	})

	/* click handlers */

	var actionEditSuperclass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSuperclassCallback, "superclass");
	}

	var actionDeleteSuperclass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSuperclassRequest);
	}

	var actionEditSubclass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSubclassCallback, "subclass");
	}

	var actionDeleteSubclass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSubclassRequest);
	}

	var actionEditEqClass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditEqclassCallback, "eqclass");
	}

	var actionDeleteEqClass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteEqclassCallback);
	}

	var actionEditDisjoint = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditDisjointCallback, "disjointclass");
	}

	var actionDeleteDisjoint = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteDisjointCallback);
	}

	var refreshTree = function() {
		$.get("./edit_api/get_hierarchy?uri=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23FacultyMember", function(jsonData) {
			var data = JSON.parse(jsonData);
			$("#tree").jstree("destroy");
			$("#tree").jstree({
				"core": {
					"data": [ data ]
				},
				"plugins": [ "sort" ]
			});
		});
	}

	var addSuperclass = function() {
		addItem($(this), function(td) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var superclassURI = td.attr("data-superclass-uri");
			$.post('./edit_api/add_item', {'uri': vclassURI, 'itemURI': superclassURI, 'relationship': 'super', 'type': 'vclass'}, function(res) {
				td.parent().find(".action-edit-superclass").click(actionEditSuperclass);
				td.parent().find(".action-delete-superclass").click(actionDeleteSuperclass);
				refreshTree();
			})
		}, "superclass");
	}

	var addSubclass = function() {
		addItem($(this), function(td) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var subclassURI = td.attr("data-subclass-uri");
			$.post('./edit_api/add_item', {'uri': vclassURI, 'itemURI': subclassURI, 'relationship': 'sub', 'type': 'vclass'}, function(res) {
				td.parent().find(".action-edit-subclass").click(actionEditSubclass);
				td.parent().find(".action-delete-subclass").click(actionDeleteSubclass);
				refreshTree();
			})
		}, "subclass");
	}

	var addEqClass = function() {
		addItem($(this), function(td) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var eqClassURI = td.attr("data-eqclass-uri");
			$.post('./edit_api/add_item', {'uri': vclassURI, 'itemURI': eqClassURI, 'relationship': 'eq', 'type': 'vclass'}, function(res) {
				td.parent().find(".action-edit-eqclass").click(actionEditEqClass);
				td.parent().find(".action-delete-eqclass").click(actionDeleteEqClass);
			});
		}, "eqclass")
	}

	var addDisjoint = function() {
		addItem($(this), function(td) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var disjointClassURI = td.attr("data-disjointclass-uri");
			$.post('./edit_api/add_item', {'uri': vclassURI, 'itemURI': disjointClassURI, 'relationship': 'disjoint', 'type': 'vclass'}, function(res) {
				td.parent().find(".action-edit-disjoint").click(actionEditDisjoint);
				td.parent().find(".action-delete-disjoint").click(actionDeleteDisjoint);
			});
		}, "disjointclass")
	}

	var deleteVClass = function() {
		var sure = confirm("Are you sure you want to delete this class?");
		if(sure) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			$.post("./edit_api/delete_vclass", {"vclassURI": vclassURI}, function(res) {
				updateData(res);
				refreshTree();
			})
		}
	}

	/* edit and delete callbacks */

	var actionEditSuperclassCallback = function(itemDetail) {
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		var oldSuperclassURI = itemDetail.attr("data-superclass-uri");
		getURI(itemDetail.text(), "class", function(data) {
			var newSuperclassURI = data;
			editItem(vclassURI, oldSuperclassURI, newSuperclassURI, "super", "vclass", function() {
				refreshTree();
			});
		});
	}

	var actionDeleteSuperclassRequest = function(row, callback) {
		var superclassURI = row.find(".item-detail").attr("data-superclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("./edit_api/delete_item", {"uri": vclassURI, "itemURI": superclassURI, "relationship": "super", "type": "vclass"}, function() {
			callback();
			refreshTree();
		});
	}

	var actionEditSubclassCallback = function(itemDetail) {
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		var oldSubclassURI = itemDetail.attr("data-subclass-uri");
		getURI(itemDetail.text(), "class", function(data) {
			var newSubclassURI = data;
			editItem(vclassURI, oldSubclassURI, newSubclassURI, "sub", "vclass", function() {
				refreshTree();
			})
		});
	}

	var actionDeleteSubclassRequest = function(row, callback) {
		var subclassURI = row.find(".item-detail").attr("data-subclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("./edit_api/delete_item", {"uri": vclassURI, "itemURI": subclassURI, "relationship": "sub", "type": "vclass"}, function() {
			callback();
			$.get("./edit_api/get_hierarchy?uri=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23FacultyMember", function(jsonData) {
				var data = JSON.parse(jsonData);
				refreshTree(data);
			});
		});
	}

	var actionEditEqclassCallback = function(itemDetail) {
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		var oldEqClassURI = itemDetail.attr("data-eqclass-uri");
		getURI(itemDetail.text(), "class", function(data) {
			var newEqClassURI = data;
			editItem(vclassURI, oldEqClassURI, newEqClassURI, "eq", "vclass");
		});
	}

	var actionDeleteEqclassCallback = function(row, callback) {
		var eqClassURI = row.find(".item-detail").attr("data-eqclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("./edit_api/delete_item", {"uri": vclassURI, "itemURI": eqClassURI, "relationship": "eq", "type": "vclass"}, callback);
	}

	var actionEditDisjointCallback = function(itemDetail) {
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		var oldDisjointClassURI = itemDetail.attr("data-disjointclass-uri");
		getURI(itemDetail.text(), "class", function(data) {
			var newDisjointClassURI = data;
			editItem(vclassURI, oldDisjointClassURI, newDisjointClassURI, "disjoint", "vclass")
		});
	}

	var actionDeleteDisjointCallback = function(row, callback) {
		var disjointClassURI = row.find(".item-detail").attr("data-disjointclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("./edit_api/delete_item", {"uri": vclassURI, "itemURI": disjointClassURI, "relationship": "disjoint", "type": "vclass"}, callback);
	}

	var updateData = function(uri) {
		$.get("./edit_api/classinfo", {"uri": uri}, function(jsonData) {

			/* jsonData will look like this:

			{"displayLevel": "display level",
			"updateLevel": "update level",
			"publishLevel": "publish level",
			"label": "the class label",
			"group": "group",
			"ontology": "ontology name",
			"superclasses": [{"uri": "some uri", "name": "the name"}, .. ],
			"subclasses": [{"uri": "some uri", "name": "the name"}, .. ],
			"eqclasses": [{"uri": "some uri", "name": "the name"}, .. ],
			"disjoints": [{"uri": "some uri", "name": "the name"}, .. ]} */

			var data = JSON.parse(jsonData);

			var ontology = data["ontology"];
			var group = data["group"];
			var displayLevel = data["displayLevel"];
			var updateLevel = data["updateLevel"];
			var publishLevel = data["publishLevel"];

			var classLabel = data["label"];
			var superclasses = data["superclasses"];
			var subclasses = data["subclasses"];
			var eqclasses = data["eqclasses"];
			var disjoints = data["disjoints"];

			$("#vclass-uri").attr("data-vclass-uri", uri);
			$("#vclass-uri").val(uri);

			$("#update-level").text(updateLevel);
			$("#publish-level").text(publishLevel);
			$("#display-level").text(displayLevel);

			$("#ontology-name").text(ontology);
			$("#class-group").text(group);

			$(".vclass-label").html(classLabel + '<b class="concept">(CLASS)</b><i class="fa fa-pencil action-edit-name"></i>');
			$("#action-edit-name").click(actionEditName);
			$("#uri").val(uri);

			$("#superclass-table").html('');

			for(var i = 0; i < superclasses.length; i++) {
				var superclass = superclasses[i];
				var superclassDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + superclass["uri"] + '" data-superclass-uri="' + superclass["uri"] + '"><p>' + superclass["name"] + '</p></td><td class="item-spacer"></td><td class="item-action"> <i class="fa fa-pencil action action-edit-superclass" title="Edit/replace"> </i></td><td class="item-action"> <i class="fa fa-trash action action-delete-superclass" title="Remove this"></i> </td></tr>')
				$("#superclass-table").append(superclassDiv);
			}

			$("#subclass-table").html('');

			for(var i = 0; i < subclasses.length; i++) {
				var subclass = subclasses[i];
				var subclassDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + subclass["uri"] + '" data-subclass-uri="' + subclass["uri"] + '"><p>' + subclass["name"] + '</p></td> <td class="item-spacer"></td><td class="item-action"> <i class="fa fa-pencil action action-edit-subclass" title="Edit/replace"> </i></td><td class="item-action"> <i class="fa fa-trash action action-delete-subclass" title="Remove this"></i> </td></tr>')
				$("#subclass-table").append(subclassDiv);
			}

			$("#eqclass-table").html('');

			for(var i = 0; i < eqclasses.length; i++) {
				var eqclass = eqclasses[i];
				var eqclassDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + eqclass["uri"] + '" data-eqclass-uri="' + eqclass["uri"] + '"><p>' + eqclass["name"] + '</p></td> <td class="item-spacer"></td><td class="item-action"><i class="fa fa-pencil action action-edit-eqclass" title="Edit/replace"> </i></td> <td class="item-action"> <i class="fa fa-trash action action-delete action-delete-eqclass" title="Remove this"></i></td></tr>')
				$("#eqclass-table").append(eqclassDiv);
			}

			$("#disjoint-table").html('')

			for(var i = 0; i < disjoints.length; i++) {
				var disjoint = disjoints[i];
				var disjointDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + disjoint["uri"] + '" data-disjointclass-uri="' + disjoint["uri"] + '"><p>' + disjoint["name"] + '</p></td> <td class="item-spacer"></td><td class="item-action"><i class="fa fa-pencil action action-edit-disjoint" title="Edit/replace"></i></td> <td class="item-action"> <i class="fa fa-trash action action-delete-disjoint" title="Remove this"></i></td></tr>')
				$("#disjoint-table").append(disjointDiv);
			}
			
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

			window.history.pushState($("html").html(), document.title, "./classpage?uri=" + encodeURIComponent(uri));
			
			updateEventHandlers();
		});
	}

	var actionEditName = function() {
		if($("#name-input").length == 0) {
			$("#name").hide();
			var nameInput = $("<input type='text' id='name-input'/>");
			$("#name").parent().prepend(nameInput);
			$(nameInput).keypress(function(e) {
				if(e.keyCode == 13) {
					editVClassName($(this).val());
				}
			});
		}
	}

	var addClass = function() {
		if($("#new-vclass-name").length == 0) {
			var nameInput = $("<p>Class URI: <input type='text' id='new-vclass-uri'/></p>");
			var superclassInput = createAutocompleteInput("class");
			nameInput.css("width", "200px");
			superclassInput.css("width", "200px");
			var confirmButton = $("<input type='submit' class='submit' value='Add class'/>");
			var cancelButton = $("<a href='#' class='cancel-add'>Cancel</a>");
			var itemsContainer = $("<div class='items-container'></div>");
			$(itemsContainer).append(nameInput);
			$(itemsContainer).append($("<p>Superclass:</p>"));
			$(itemsContainer).append(superclassInput);
			$(itemsContainer).append(confirmButton);
			$(itemsContainer).append(cancelButton);
			$("#new-class-container").append(itemsContainer);
			superclassInput.select2({
				placeholder: "Select a superclass"
			});
			superclassInput.select2({})
			$(cancelButton).click(function() {
				$(this).parent().remove();
				$("#add-vclass").click(addClass);
			})
			$(confirmButton).click(function(e) {
				var selectedLabel = $("#class-select").val();
				var selectedURI = null;
				$(".class-option-data").each(function(i, el) {
					if($(el).val() == selectedLabel) {
						selectedURI = $(el).attr("data-uri");
					}
				})
				$.post("./edit_api/add_entity", {"uri": $("#new-vclass-uri").val(), "supertype": selectedURI, "type": "vclass"}, function(label) {
					$.get("./edit_api/get_hierarchy?uri=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23FacultyMember", function(jsonData) {
						var data = JSON.parse(jsonData);
						refreshTree(data);
					});
					$("#new-class-container").html('<p style="text-align:center;"><a href="#" class="add-vclass">Add New Class</a></p>');
					$(".add-vclass").click(addClass);
					if($("#uri").val() == selectedURI) { // if the superproperty is the same as the current page
						// add item to subproperty list
						var tableRow = $("<tr class='class-item'></tr>");
						var tdItemDetail = $("<td class='item-detail' id='editable-item-detail'></td>");
						tdItemDetail.text(label);
						tdItemDetail.attr('title', uri);
						tdItemDetail.attr('data-subproperty-uri', $("#new-vclass-uri").val());

						tableRow.append(tdItemDetail);

						var scope = $(".action-add-subclass").parent().parent();
						scope.find("table").append(tableRow);

						tableRow.append($("<td class='item-spacer'></td>"));
						tableRow.append($("<td class='item-action'><i class='fa fa-pencil action action-edit-subclass' title='Edit/replace'></i></td>"))
						tableRow.append($("<td class='item-action'><i class='fa fa-trash action action-delete-subclass' title='Remove this'></i></td>"))

						tableRow.css({'background-color': '#FFFFAA'});
						tableRow.animate({'backgroundColor': '#FFFFFF'}, 1500);
						tdItemDetail.parent().find(".action-edit-subclass").click(actionEditSubclass);
						tdItemDetail.parent().find(".action-delete-subclass").click(actionDeleteSubclass);
					}
				});
			})
		}
	}

	var editVClassName = function(name) {
		$.post("./edit_api/edit_name", {"uri": $("#uri").val(), "newName": name, "type": "vclass"}, function(data) {
			setTimeout(function() {
				$("#name-input").remove();
				$("#name").show();
				$("#name").text(data);
			}, 2000);
		})
	}

	$("#onProperty").select2({
		placeholder: "Select an item"
	})

	$("#ValueClass").select2({
		"placeholder": "Select an item"
	})

	$("#onProperty").select2({
		"placeholder": "Select an item"
	})

	$("#restrictionType").change(function() {
		var val = $(this).val();
		var startHtml = '<td><p>';
		var htmls = {"allValuesFrom": '<b>All values from:</b> <select id="ValueClass" name="ValueClass"></select>',
					 "someValuesFrom": '<b>Some values from:</b> <select id="ValueClass" name="ValueClass"></select>',
					 "hasValue": '<b>Has value:</b> <input id="ValueIndividual" name="ValueIndividual" placeholder="Enter individual URI.."/>',
					 "minCardinality": '<b>Minimum cardinality:</b> <input id="cardinality" name="cardinality"/>',
					 "maxCardinality": '<b>Maximum cardinality:</b> <input id="cardinality" name="cardinality"/>',
					 "cardinality": '<b>Exact cardinality:</b> <input id="cardinality" name="cardinality"/>'}
		var endHtml = '</p></td>';
		$("#restriction-container").html(startHtml + htmls[val] + endHtml);
		$.each($(".class-option-data"), function(i, optionInput) {
			$("#ValueClass").append($('<option value="' + $(optionInput).attr('data-uri') + '">' + $(optionInput).val() + '</option>'));
		});
		$("#ValueClass").select2();
	})

	$("#add-restriction").click(function(e) {
		e.preventDefault();
		var formData = $("#restriction-form").serializeArray();
		var dynamicInputIds = ["ValueClass", "ValueIndividual", "cardinality"]
		for(var i = 0; i < dynamicInputIds.length; i++) {
			var id = dynamicInputIds[i];
			if($("#"+id).length > 0) {
				formData.push({"name": id, "value": $("#"+id).val()})
			}
		}
		
		$.post("./edit_api/add_restriction", formData, function() {
			alert("Restriction added.");
			updateData($("#vclass-uri").val());
		});
	})

	function updateInitialEventHandlers() {

		$(".action-add-superclass").click(addSuperclass);
		$(".action-add-subclass").click(addSubclass);
		$(".action-add-eqclass").click(addEqClass);
		$(".action-add-disjoint").click(addDisjoint);

		$(".action-delete-vclass").click(deleteVClass);

		$(".action-edit-name").click(actionEditName);

		$(".add-vclass").click(addClass);

		updateEventHandlers();
	}

	function updateEventHandlers() {

		if($("#vclass-uri").attr("data-vclass-uri").indexOf("#Thing") >= 0) {
			$(".action-delete-vclass").hide();
		}
		else {
			$(".action-delete-vclass").show();
		}

		$(".action-edit-superclass").click(actionEditSuperclass);
		$(".action-delete-superclass").click(actionDeleteSuperclass);
		$(".action-edit-subclass").click(actionEditSubclass);
		$(".action-delete-subclass").click(actionDeleteSubclass);
		$(".action-edit-eqclass").click(actionEditEqClass);
		$(".action-delete-eqclass").click(actionDeleteEqClass);
		$(".action-edit-disjoint").click(actionEditDisjoint);
		$(".action-delete-disjoint").click(actionDeleteDisjoint);
	}

	updateInitialEventHandlers();
	
});
</script>