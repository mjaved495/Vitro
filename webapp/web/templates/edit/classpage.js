<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<script language="JavaScript" type="text/javascript"> 
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
</script>