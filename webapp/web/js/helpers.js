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
	var scope = jQElement.parent().parent().parent().parent();
	console.log(scope);
	jQElement.fadeOut(500, function() {
		// remove scrollbar if necessary
		var scrollDiv = scope.find(".scroll-list");
		console.log(scrollDiv);
		if(scrollDiv.height() < 250) {
			console.log("should remove scrollbar")
			scrollDiv.css("overflow-y", "hidden");
			console.log(scrollDiv.css("overflow-y"));
		}
		onRemovalCallback(jQElementCopy);
	});
}

var createAutocompleteInput = function() {
	var input = $("<select style='width:100px !important;margin-top:5px;' placeholder='Name...'></select>");
	$.each(".option-data", function(i, optionInput) {
		input.append($('<option data-uri="' + optionInput.attr('data-uri') + '">' + optionInput.val() + '</option>'));
	});
	return input;
}

var addItem = function(jQElement, onAddCallback, type) {
	var tableRow = $("<tr class='class-item'></tr>");
	var tdItemDetail = $("<td class='item-detail' id='editable-item-detail'></td>");
	var input = createAutocompleteInput();
	tdItemDetail.append(input);
	var cancelSpan = $("<span>&nbsp;<a href='#' id='cancel'>cancel</a></span>");
	tdItemDetail.append(cancelSpan);
	tableRow.append(tdItemDetail);

	var scope = jQElement.parent().parent();
	scope.find("table").append(tableRow);

	var scrollDiv = scope.find(".scroll-list");
	console.log("scrollDiv is " + scrollDiv.height());
	if(scrollDiv.height() >= 250) {
		scrollDiv.css("overflow-y", "scroll");
	}

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
			tableRow.append($("<td class='item-action'><i class='fa fa-pencil action action-edit-" + type + "' title='Edit/replace'></i></td>"))
			tableRow.append($("<td class='item-action'><i class='fa fa-trash action action-delete-" + type + "' title='Remove this'></i></td>"))

			input.remove();
			tableRow.css({'background-color': '#FFFFAA'});
			tableRow.animate({'backgroundColor': '#FFFFFF'}, 1500, function() {
				onAddCallback(tdItemDetail);
			})
		}
	})
}

function getURI(className) {
	return "http://vivoweb.org/ontology/core#"+className; // todo: make smarter
}