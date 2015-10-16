<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<script src="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.js"></script>
<link href="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.css" rel="stylesheet"/>
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

	$(".action-edit").click(function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		var itemDetailText = itemDetail.text();
		var input = $("<input type='text' value='" + itemDetailText + "'></input>");
		input.css({"width": "100% !important"});
		itemDetail.html('');
		itemDetail.append(input);
	});

});

function editClass() {
	window.location.href = "/vivo/vclass_retry";
}

function deleteClass() {
	window.location.href = "/vivo/vclass_retry"; // is there a more specific URL?
}

function editSuperclasses() {
	// replace the URI with the actual URI of the class
	window.location.href = "http://localhost:8080/vivo/editForm?SubclassURI=http%3A%2F%2Fvivoweb.org%2Fontology%2Fcore%23FacultyMember&controller=Classes2Classes"
}

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
</script>