<!-- $This file is distributed under the terms of the license in /doc/license.txt$ -->
<script src="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.js"></script>
<link href="https://cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.css" rel="stylesheet"/>
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jstree/3.0.9/themes/default/style.min.css" />
<script src="/vivo/js/jstree.js"></script>
<script src="/vivo/js/jquery.color.js"></script> <!-- use base url -->
<script src="/vivo/js/helpers.js"></script>
<script language="JavaScript" type="text/javascript">
$(document).ready(function() {

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

	var actionEditSuperproperty = function() {

	}

	var actionDeleteSuperproperty = function() {

	}

	var actionEditSubproperty = function() {

	}

	var actionDeleteSuperproperty = function() {

	}

	var actionEditEqProperty = function() {

	}

	var actionDeleteEqProperty = function() {

	}

	var actionEditInverse = function() {
		var itemDetail = $(this).parent().parent().find(".item-detail");
		replaceWithInput(itemDetail, actionEditInverseCallback);
	}

	var actionDeleteInverse = function() {

	}

	var actionEditDomain = function() {

	}

	var actionDeleteDomain = function() {

	}

	var actionEditRange = function() {

	}

	var actionDeleteRange = function() {

	}

	var addInverse = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var inverseURI = getURI(td.text());
			$.post('/vivo/edit_api/add_inverse', {'propertyURI': propertyURI, 'inverseURI': inverseURI}, function(res) {
				if(res != inverseURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-superclass").click(actionEditInverse);
					td.parent().find(".action-delete-superclass").click(actionDeleteInverse);
				}
			})
		}, "inverse");
	}

	var addDomain = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var domainURI = getURI(td.text());
			$.post('/vivo/edit_api/add_domain', {'propertyURI': propertyURI, 'domainURI': domainURI}, function(res) {
				if(res != domainURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-domain").click(actionEditDomain);
					td.parent().find(".action-delete-domain").click(actionDeleteDomain);
				}
			})
		}, "domain");
	}

	var addRange = function() {
		addItem($(this), function(td) {
			var propertyURI = $("#property-uri").attr("data-property-uri");
			var rangeURI = getURI(td.text());
			$.post('/vivo/edit_api/add_domain', {'propertyURI': propertyURI, 'rangeURI': rangeURI}, function(res) {
				if(res != rangeURI) {
					console.log("error: " + res);
				}
				else {
					td.parent().find(".action-edit-range").click(actionEditRange);
					td.parent().find(".action-delete-range").click(actionDeleteRange);
				}
			})
		}, "range");
	}

	$(".action-edit-superproperty").click(actionEditSuperproperty);
	$(".action-delete-superproperty").click(actionDeleteSuperproperty);
	$(".action-edit-subproperty").click(actionEditSubproperty);
	$(".action-delete-subproperty").click(actionDeleteSubproperty);
	$(".action-edit-eqproperty").click(actionEditEqProperty);
	$(".action-delete-eqproperty").click(actionDeleteEqProperty);
	$(".action-edit-inverse").click(actionEditInverse);
	$(".action-delete-inverse").click(actionDeleteInverse);
	$(".action-edit-domain").click(actionEditDomain);
	$(".action-delete-domain").click(actionDeleteDomain);
	$(".action-edit-range").click(actionEditRange);
	$(".action-delete-range").click(actionDeleteRange);

	
	$(".action-add-inverse").click(addInverse);
	$(".action-add-domain").click(addDomain);
	$(".action-add-range").click(addRange);

	$(".action-delete-vclass").click(deleteVClass);

});
</script>