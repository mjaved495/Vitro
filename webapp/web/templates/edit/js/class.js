<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<script src="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.js"></script>
<link href="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.css" rel="stylesheet"/>
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jstree/3.0.9/themes/default/style.min.css" />
<script src="/vivo/js/jstree.js"></script>
<script src="/vivo/js/jquery.color.js"></script> <!-- use base url -->
<script language="JavaScript" type="text/javascript"> 
$(document).ready(function() {
	$("#uri-check").click(function() {
		var uriInput = $("#uri-field").find("input");
		if(uriInput.attr("readonly")) {
			uriInput.removeAttr("readonly");
		}
		else {
			uriInput.attr("readonly", "true");
		}
	})

	var identifiers = ['.item-detail', '.action-delete', '.action-edit', '.action-add'];
	for(var i = 0; i < identifiers.length; i++) {
		$(identifiers[i]).qtip({
			position: {
				my: "top left",
				at: "bottom left"
			}
		})
	}

	var replaceWithInput = function(jQElement, onSubmitCallback) {
		var text = jQElement.text();
		var input = $("<input type='text' value='" + text + "' style='width:100px !important;'></input>");
		jQElement.html('');
		jQElement.append(input);
		var cancelSpan = $("<span>&nbsp;<a href='#' id='cancel'>cancel</a></span>");
		jQElement.append(cancelSpan);

		$("#cancel").click(function(e) {
			e.preventDefault();
			jQElement.html('');
			jQElement.text(text);
		});

		$(input).keypress(function(e) {
			if(e.keyCode == 13) {
				var text = $(this).val();
				jQElement.html(text);
				jQElement.parent().css({'background-color': '#FFFFAA'});
				jQElement.parent().animate({'backgroundColor': '#FFFFFF'}, 1500, function() {
					onSubmitCallback(jQElement);
				});
			}
		});
	}

	var deleteItem = function(jQElement, onRemovalCallback) {
		var jQElementCopy = jQElement.clone();
		jQElement.fadeOut(500, function() {
			onRemovalCallback(jQElementCopy);
		});
	}

	var addItem = function(jQElement, onAddCallback, type) {
		/*
		<tr class="class-item">
        <td class="item-detail" id="editable-item-detail" title="${eqClass.getURI()}" data-eqclass-uri="${eqClass.getURI()}"><p>${eqClass.getName()}</p></td> 
        <td class="item-spacer"></td>
        <td class="item-action"><img src="/vivo/images/edit.png" class="action action-edit action-edit-eqclass" title="Edit/replace with different class"> </img></td> 
        <td class="item-action"> <img src="/vivo/images/delete.png" class="action action-delete action-delete-eqclass" title="Remove this equivalent class"></img></td></tr>
        */
		var tableRow = $("<tr class='class-item'></tr>");
		var tdItemDetail = $("<td class='item-detail' id='editable-item-detail'></td>");
		var input = $("<input type='text' style='width:100px !important;margin-top:5px;' placeholder='Class name...'></input>");
		tdItemDetail.append(input);
		var cancelSpan = $("<span>&nbsp;<a href='#' id='cancel'>cancel</a></span>");
		tdItemDetail.append(cancelSpan);
		tableRow.append(tdItemDetail);
		jQElement.parent().parent().find("table").append(tableRow);

		$("#cancel").click(function(e) {
			e.preventDefault();
			$(this).parent().parent().remove();
		})

		input.keypress(function(e) {
			if(e.keyCode == 13) {
				e.preventDefault();
				tdItemDetail.text(input.val());
				tdItemDetail.attr('title', getURI(input.val()));
				tdItemDetail.attr('data-' + type + '-uri', getURI(input.val()));

				/* fill out rest of table row */

				tableRow.append($("<td class='item-spacer'></td>"));
				tableRow.append($("<td class='item-action'><i class='fa fa-pencil action action-edit-" + type + "' title='Edit/replace with different class'></i></td>"))
				tableRow.append($("<td class='item-action'><i class='fa fa-trash action action-delete-" + type + "' title='Remove this'></i></td>"))

				input.remove();
				tableRow.css({'background-color': '#FFFFAA'});
				tableRow.animate({'backgroundColor': '#FFFFFF'}, 1500, function() {
					onAddCallback(tdItemDetail);
				})
			}
		})
	}

	/* $(".action-edit").click(function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");

	}); */

	function editClass() {
		// replaceWithInput($(".vclass-label"), function() {});
	}

	function deleteClass() {
		window.location.href = "/vivo/vclass_retry"; // is there a more specific URL?
	}

	function getURI(className) {
		return "http://vivoweb.org/ontology/core#"+className; // todo: make smarter
	}

	var actionEditSuperclassCallback = function(itemDetail) {
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		var oldSuperclassURI = itemDetail.attr("data-superclass-uri");
		var newSuperclassURI = getURI(itemDetail.text());
		$.post("/vivo/edit_api/edit_superclass", {"vclassURI": vclassURI, 
		"oldSuperclassURI": oldSuperclassURI, "newSuperclassURI": newSuperclassURI},
		function(res) {
			if(!(res === newSuperclassURI)) {
				console.log("error: " + res);
			}
		});
	}

	var actionEditSuperclass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSuperclassCallback);
	}

	$(".action-edit-superclass").click(actionEditSuperclass);

	var actionDeleteSuperclassCallback = function(row) {
		var superclassURI = row.find(".item-detail").attr("data-superclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_superclass", {"vclassURI": vclassURI, "superclassURI": superclassURI}, function(res) {
			console.log(res);
		});
	}

	var actionDeleteSuperclass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSuperclassCallback);
	}

	$(".action-delete-superclass").click(actionDeleteSuperclass);

	var actionEditSubclassCallback = function(itemDetail) {
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		var oldSubclassURI = itemDetail.attr("data-subclass-uri");
		var newSubclassURI = getURI(itemDetail.text());
		$.post("/vivo/edit_api/edit_subclass", {"vclassURI": vclassURI, 
		"oldSubclassURI": oldSubclassURI, "newSubclassURI": newSubclassURI},
		function(res) {
			if(!(res === newSubclassURI)) {
				console.log("error: " + res);
			}
		});
	}

	var actionEditSubclass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSubclassCallback);
	}

	$(".action-edit-subclass").click(actionEditSubclass);

	var actionDeleteSubclassCallback = function(row) {
		var subclassURI = row.find(".item-detail").attr("data-subclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_subclass", {"vclassURI": vclassURI, "subclassURI": subclassURI}, function(res) {
			console.log(res);
		});
	}

	var actionDeleteSubclass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSubclassCallback);
	}

	$(".action-delete-subclass").click(actionDeleteSubclass);


	var actionEditEqclassCallback = function(itemDetail) {
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		var oldEqClassURI = itemDetail.attr("data-eqclass-uri");
		var newEqClassURI = getURI(itemDetail.text());
		$.post("/vivo/edit_api/edit_eqclass", {"vclassURI": vclassURI, 
		"oldEqClassURI": oldEqClassURI, "newEqClassURI": newEqClassURI},
		function(res) {
			if(!(res === newEqClassURI)) {
				console.log("error: " + res);
			}
		});
	}

	var actionEditEqClass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditEqclassCallback);
	}

	$(".action-edit-eqclass").click(actionEditEqClass);

	var actionDeleteEqclassCallback = function(row) {
		var eqClassURI = row.find(".item-detail").attr("data-eqclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_eqclass", {"vclassURI": vclassURI, "eqClassURI": eqClassURI}, function(res) {
			console.log(res);
		});
	}

	var actionDeleteEqClass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteEqclassCallback);
	}

	$(".action-delete-eqclass").click(actionDeleteEqClass);

	var actionEditDisjointCallback = function(itemDetail) {
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		var oldDisjointClassURI = itemDetail.attr("data-disjoint-uri");
		var newDisjointClassURI = getURI(itemDetail.text());
		$.post("/vivo/edit_api/edit_disjoint", {"vclassURI": vclassURI, 
		"oldDisjointClassURI": oldDisjointClassURI, "newDisjointClassURI": newDisjointClassURI},
		function(res) {
			if(!(res === newDisjointClassURI)) {
				console.log("error: " + res);
			}
		});
	}

	var actionEditDisjoint = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditDisjointCallback);
	}

	$(".action-edit-disjoint").click(actionEditDisjoint);

	var actionDeleteDisjointCallback = function(row) {
		var disjointClassURI = row.find(".item-detail").attr("data-disjoint-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_disjoint", {"vclassURI": vclassURI, "disjointClassURI": disjointClassURI}, function(res) {
			console.log(res);
		});
	}

	var actionDeleteDisjoint = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteDisjointCallback);
	}

	$(".action-delete-disjoint").click(actionDeleteDisjoint);

	// adding elements

	$(".action-add-superclass").click(function() {
		addItem($(this), function(td) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var superclassURI = getURI(td.text());
			$.post('/vivo/edit_api/add_superclass', {'vclassURI': vclassURI, 'superclassURI': superclassURI}, function(res) {
				if(res != superclassURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-superclass").click(actionEditSuperclass);
					td.parent().find(".action-delete-superclass").click(actionDeleteSuperclass);
				}
			})
		}, "superclass");
	});

	$(".action-add-subclass").click(function() {
		addItem($(this), function(td) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var subclassURI = getURI(td.text());
			$.post('/vivo/edit_api/add_subclass', {'vclassURI': vclassURI, 'subclassURI': subclassURI}, function(res) {
				if(res != subclassURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-subclass").click(actionEditSubclass);
					td.parent().find(".action-delete-subclass").click(actionDeleteSubclass);
				}
			})
		}, "subclass");
	});

	$(".action-add-eqclass").click(function() {
		addItem($(this), function(td) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var eqClassURI = getURI(td.text());
			$.post('/vivo/edit_api/add_eqclass', {'vclassURI': vclassURI, 'eqClassURI': eqClassURI}, function(res) {
				if(res != eqClassURI) {
					console.log("error: " + res);

				}
				else {
					td.parent().find(".action-edit-eqclass").click(actionEditEqClass);
					td.parent().find(".action-delete-eqclass").click(actionDeleteEqClass);
				}
			})
		}, "eqclass")
		/* var vclassURI = encodeURIComponent($("#vclass-uri").attr("data-vclass-uri"));
		window.location.href = "/vivo/editForm?SuperclassURI=" + vclassURI + "&controller=Classes2Classes&opMode=equivalentClass" */
	});

	$(".action-add-disjoint").click(function() {
		addItem($(this), function(td) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var disjointClassURI = getURI(td.text());
			$.post('/vivo/edit_api/add_disjoint', {'vclassURI': vclassURI, 'disjointClassURI': disjointClassURI}, function(res) {
				if(res != disjointClassURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-disjoint").click(actionEditDisjoint);
					td.parent().find(".action-delete-disjoint").click(actionDeleteDisjoint);
				}
			});
		}, "disjoint")
		/* var vclassURI = encodeURIComponent($("#vclass-uri").attr("data-vclass-uri"));
		window.location.href = "/vivo/editForm?SuperclassURI=" + vclassURI + "&controller=Classes2Classes&opMode=disjointWith" */
	});

	$(".action-delete-vclass").click(function() {
		var sure = confirm("Are you sure you want to delete this class?");
		if(sure) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			$.post("/vivo/edit_api/delete_vclass", {"vclassURI": vclassURI}, function(res) {
				if(res === "done") {
					window.location.href = "/vivo/siteAdmin";
				}
			})
		}
		
	})

	function toggleURIEditable() {
		if(document.getElementById("uri").hasAttribute("readonly")) {
			document.getElementById("uri").removeAttribute("readonly");
		}
		else {
			document.getElementById("uri").setAttribute("readonly", "true");
		}
	}

	var notYetImplemented = function() {
		alert("not implemented yet");
	}

	function moveClassCallback() {
		// display hierarchy
	}

	function mergeClassCallback(e) {
		e.preventDefault();

		var tabbedDiv = $("<div id='tabbed'></div>");
		var classChoice = $("<input type='text' name='classChoice' placeholder='Class name...'/>");
		var label = $("<p>Merge into class:</p>");
		tabbedDiv.append(label);
		tabbedDiv.append(classChoice);
		$("#merge-class-container").append(tabbedDiv);
	}

	function moveInstancesCallback(e) {
		// 
	}

	function splitClassCallback(e) {

	}

	function makeSiblingsDisjoint() {
		$.post('/vivo/edit_api/siblings_disjoint', function(res) {
			if(res == "ok") {
				alert("Made sibling classes disjoint.");
			}
			else {
				alert("Error: " + res);
			}
		})
	}

	$("#move-class").click(notYetImplemented)
	$("#merge-class").click(mergeClassCallback)
	$("#move-instances").click(notYetImplemented)
	$("#split-class").click(notYetImplemented)
	$("#specialize").click(notYetImplemented)
	$("#generalize").click(notYetImplemented)
	$("#siblings-disjoint").click(makeSiblingsDisjoint)

	$(".stretch-panel").css({'height': '50px', 'margin-top': 25});
	$(".stretch-panel-header").click(function() {
		if($(this).parent().height() > 50) {
			$(this).parent().animate({'height': 50})
		}
		else {
			$(this).parent().animate({'height': 250});
		}
	})

});
</script>