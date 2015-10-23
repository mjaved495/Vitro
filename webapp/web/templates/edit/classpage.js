<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<script src="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.js"></script>
<link href="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.css" rel="stylesheet"/>
<script src="/vivo/js/jquery.color.js"></script> <!-- use base url -->
<script language="JavaScript" type="text/javascript"> 
$(document).ready(function() {
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
		input = $("<input type='text' value='" + text + "'></input>");
		input.css({"width": "100% !important"});
		jQElement.html('');
		jQElement.append(input);

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

	var addItem = function(jQElement, onAddCallback) {
		var tableRow = $("<tr></tr>");
		var td = $("<td id='editable-item-detail'></td>");
		var input = $("<input type='text'></input>");
		td.append(input);
		tableRow.append(td);
		jQElement.parent().parent().find("table").append(tableRow);

		$(input).keypress(function(e) {
			if(e.keyCode == 13) {
				e.preventDefault();
				var text = $(this).val();
				tableRow.text = $(this).val();
				tableRow.css({'background-color': '#FFFFAA'});
				tableRow.animate({'backgroundColor': '#FFFFFF'}, 1500, function() {
					onAddCallback(tableRow);
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

	$(".action-edit-superclass").click(function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSuperclassCallback);
	});

	var actionDeleteSuperclassCallback = function(row) {
		var superclassURI = row.find(".item-detail").attr("data-superclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_superclass", {"vclassURI": vclassURI, "superclassURI": superclassURI}, function(res) {
			console.log(res);
		});
	}

	$(".action-delete-superclass").click(function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSuperclassCallback);
	});

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

	$(".action-edit-eqclass").click(function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditEqclassCallback);
	});

	var actionDeleteEqclassCallback = function(row) {
		var eqClassURI = row.find(".item-detail").attr("data-eqclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_eqclass", {"vclassURI": vclassURI, "eqClassURI": eqClassURI}, function(res) {
			console.log(res);
		});
	}

	$(".action-delete-eqclass").click(function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteEqclassCallback);
	});

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

	$(".action-edit-disjoint").click(function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditDisjointCallback);
	});

	var actionDeleteDisjointCallback = function(row) {
		var disjointClassURI = row.find(".item-detail").attr("data-disjoint-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_disjoint", {"vclassURI": vclassURI, "disjointClassURI": disjointClassURI}, function(res) {
			console.log(res);
		});
	}

	$(".action-delete-disjoint").click(function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteDisjointCallback);
	});

	// adding elements

	$(".action-add-superclass").click(function() {
		addItem($(this), function(tableRow) {
			alert("ok");
		});
		/* var vclassURI = encodeURIComponent($("#vclass-uri").attr("data-vclass-uri"));
		window.location.href = "/vivo/editForm?SubclassURI=" + vclassURI + "&controller=Classes2Classes";
		*/
	});

	$(".action-add-eqclass").click(function() {
		var vclassURI = encodeURIComponent($("#vclass-uri").attr("data-vclass-uri"));
		window.location.href = "/vivo/editForm?SuperclassURI=" + vclassURI + "&controller=Classes2Classes&opMode=equivalentClass"
	});

	$(".action-add-disjoint").click(function() {
		var vclassURI = encodeURIComponent($("#vclass-uri").attr("data-vclass-uri"));
		window.location.href = "/vivo/editForm?SuperclassURI=" + vclassURI + "&controller=Classes2Classes&opMode=disjointWith"
	});

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

	$("#move-class").click(notYetImplemented)
	$("#merge-class").click(mergeClassCallback)
	$("#move-instances").click(notYetImplemented)
	$("#split-class").click(notYetImplemented)
	$("#specialize").click(notYetImplemented)
	$("#generalize").click(notYetImplemented)
	$("#sibling-disjoint").click(notYetImplemented)

});
</script>