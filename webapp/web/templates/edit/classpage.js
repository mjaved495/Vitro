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
					console.log(onSubmitCallback);
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
			console.log('done');
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
		console.log(vclassURI);
		$.post("/vivo/edit_api/edit_eqclass", {"vclassURI": vclassURI, 
		"oldEqClassURI": oldEqClassURI, "newEqClassURI": newEqClassURI},
		function(res) {
			console.log('done');
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

	}

	$(".action-edit-disjoint").click(function() {

	});

	var actionDeleteDisjointCallback = function(row) {

	}

	$(".action-delete-disjoint").click(function() {

	});

	function toggleURIEditable() {
		if(document.getElementById("uri").hasAttribute("readonly")) {
			document.getElementById("uri").removeAttribute("readonly");
		}
		else {
			document.getElementById("uri").setAttribute("readonly", "true");
		}
	}

});
</script>