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

	$.get("/vivo/edit_api/get_hierarchy?uri=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23FacultyMember", function(jsonData) {
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
				if(link.find(".jstree-icon").css("background-image") != undefined && link.find("jstree-icon").css("background-image").indexOf("orangedot-open.png") > -1) {
					link.find(".jstree-icon").css("background-image", "url('/vivo/images/orangedot.png')");
				}
				else {
					link.find(".jstree-icon").css("background-image", "url('/vivo/images/orangedot-open.png')");
				}
				
			});
		});
	});

	$.each($(".scroll-list"), function(i, div) {
		$(div).css("min-height", "75px");
		if($(div).height() <= 75) {
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

	/* toggle URI readonly state */

	$("#uri-check").click(function() {
		var uriInput = $("#uri-field").find("input");
		if(uriInput.attr("readonly")) {
			uriInput.removeAttr("readonly");
		}
		else {
			uriInput.attr("readonly", "true");
		}
	})

	/* tooltips */

	var identifiers = ['.item-detail', '.action-delete', '.action-edit', '.action-add'];
	for(var i = 0; i < identifiers.length; i++) {
		$(identifiers[i]).qtip({
			position: {
				my: "top left",
				at: "bottom left"
			}
		})
	}

	/* click handlers */

	var actionEditSuperclass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSuperclassCallback);
	}

	var actionDeleteSuperclass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSuperclassCallback);
	}

	var actionEditSubclass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditSubclassCallback);
	}

	var actionDeleteSubclass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteSubclassCallback);
	}

	var actionEditEqClass = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditEqclassCallback);
	}

	var actionDeleteEqClass = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteEqclassCallback);
	}

	var actionEditDisjoint = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditDisjointCallback);
	}

	var actionDeleteDisjoint = function() {
		var row = $(this).parent().parent();
		deleteItem(row, actionDeleteDisjointCallback);
	}

	var addSuperclass = function() {
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
	}

	var addSubclass = function() {
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
	}

	var addEqClass = function() {
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
	}

	var addDisjoint = function() {
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
	}

	var deleteVClass = function() {
		var sure = confirm("Are you sure you want to delete this class?");
		if(sure) {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			$.post("/vivo/edit_api/delete_vclass", {"vclassURI": vclassURI}, function(res) {
				if(res === "done") {
					window.location.href = "/vivo/siteAdmin";
				}
			})
		}
	}

	/* edit and delete callbacks */

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

	var actionDeleteSuperclassCallback = function(row) {
		var superclassURI = row.find(".item-detail").attr("data-superclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_superclass", {"vclassURI": vclassURI, "superclassURI": superclassURI}, function(res) {
			console.log(res);
		});
	}

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

	var actionDeleteSubclassCallback = function(row) {
		var subclassURI = row.find(".item-detail").attr("data-subclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_subclass", {"vclassURI": vclassURI, "subclassURI": subclassURI}, function(res) {
			console.log(res);
		});
	}

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

	var actionDeleteEqclassCallback = function(row) {
		var eqClassURI = row.find(".item-detail").attr("data-eqclass-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_eqclass", {"vclassURI": vclassURI, "eqClassURI": eqClassURI}, function(res) {
			console.log(res);
		});
	}

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

	var actionDeleteDisjointCallback = function(row) {
		var disjointClassURI = row.find(".item-detail").attr("data-disjoint-uri");
		var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
		$.post("/vivo/edit_api/delete_disjoint", {"vclassURI": vclassURI, "disjointClassURI": disjointClassURI}, function(res) {
			console.log(res);
		});
	}

	var updateData = function(uri) {
		$.get("/vivo/classinfo", {"uri": uri}, function(jsonData) {

			/* jsonData will look like this:

			{"label": "the class label",
			"superclasses": [{"uri": "some uri", "name": "the name"}, ... ],
			"subclasses": [{"uri": "some uri", "name": "the name"}, ... ],
			"eqclasses": [{"uri": "some uri", "name": "the name"}, ... ],
			"disjoints": [{"uri": "some uri", "name": "the name"}, ... ]} */

			var data = JSON.parse(jsonData);
			var classLabel = data["label"];
			var superclasses = data["superclasses"];
			var subclasses = data["subclasses"];
			var eqclasses = data["eqclasses"];
			var disjoints = data["disjoints"];

			$("#vclass-uri").attr("data-vclass-uri", uri);
			$("#vclass-uri").val(uri);

			$(".vclass-label").text(classLabel);
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
				var disjointDiv = $('<tr class="class-item"><td class="item-detail" id="editable-item-detail" title="' + disjoint["uri"] + '" data-disjoint-uri="' + disjoint["uri"] + '"><p>' + disjoint["name"] + '</p></td> <td class="item-spacer"></td><td class="item-action"><i class="fa fa-pencil action action-edit-disjoint" title="Edit/replace"></i></td> <td class="item-action"> <i class="fa fa-trash action action-delete-disjoint" title="Remove this"></i></td></tr>')
				$("#disjoint-table").append(disjointDiv);
			}
			
		});
	}


	$(".action-edit-superclass").click(actionEditSuperclass);
	$(".action-delete-superclass").click(actionDeleteSuperclass);
	$(".action-edit-subclass").click(actionEditSubclass);
	$(".action-delete-subclass").click(actionDeleteSubclass);
	$(".action-edit-eqclass").click(actionEditEqClass);
	$(".action-delete-eqclass").click(actionDeleteEqClass);
	$(".action-edit-disjoint").click(actionEditDisjoint);
	$(".action-delete-disjoint").click(actionDeleteDisjoint);

	$(".action-add-superclass").click(addSuperclass);
	$(".action-add-subclass").click(addSubclass);
	$(".action-add-eqclass").click(addEqClass);
	$(".action-add-disjoint").click(addDisjoint);

	$(".action-delete-vclass").click(deleteVClass);
});
</script>