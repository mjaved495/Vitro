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

	function replaceWithInput(jQElement, onSubmitCallback) {
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
				jQElement.parent().animate({'backgroundColor': '#FFFFFF'}, 1500);
				console.log(onSubmitCallback);
				onSubmitCallback();
			}
		});
	}

	$(".action-edit").click(function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, function() {});

	});

	$(".action-delete").click(function() {
		$(this).parent().parent().fadeOut(500);
	})

	function editClass() {
		replaceWithInput($(".vclass-label"), function() {});
	}

	function deleteClass() {
		window.location.href = "/vivo/vclass_retry"; // is there a more specific URL?
	}

	$(".edit-class").click(function() {
		var itemDetail = $(this).parent().find("item-detail");
		replaceWithInput(itemDetail, function() {
			var vclassURI = $("#vclass-uri").attr("data-vclass-uri");
			var oldSuperclassURI = $(itemDetail).attr("data-superclass-uri");
			var newSuperclassURI = getURI($(itemDetail).text());
			$.post("/vivo/edit_api/edit_superclass", {"vclassURI": vclassURI, 
				"oldSuperclassURI": oldSuperclassURI, "newSuperclassURI": newSuperclassURI},
				function(res) {
					console.log('done');
					if(!(res === newSuperclassURI)) {
						console.log("error: " + res);
					}
				});
		});
	});

	function editEquivalentClasses() {
		window.location.href = "http://localhost:8080/vivo/editForm?controller=Classes2Classes&SuperclassURI=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23FacultyMember&opMode=equivalentClass"
	}

	function editDisjointClasses() {
		window.location.href = "http://localhost:8080/vivo/editForm?controller=Classes2Classes&SuperclassURI=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23FacultyMember&opMode=disjointWith"
	}

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