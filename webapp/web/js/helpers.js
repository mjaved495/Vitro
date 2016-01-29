var replaceWithInput = function(jQElement, onSubmitCallback) {
	var text = jQElement.text();
	var input = createAutocompleteInput();
	jQElement.html('');
	jQElement.append(input);
	input.select2();
	var cancelSpan = $("<span>&nbsp; <a href='#' id='save-select'>save</a> <a href='#' id='cancel'>cancel</a></span>");
	jQElement.append(cancelSpan);

	$("#cancel").click(function(e) {
		e.preventDefault();
		jQElement.html('');
		jQElement.text(text);
	});

	$("#save-select").click(function() {
		var text = input.val();
		jQElement.html(text);
		jQElement.parent().css({'background-color': '#FFFFAA'});
		jQElement.parent().animate({'backgroundColor': '#FFFFFF'}, 1500, function() {
			onSubmitCallback(jQElement);
		});
	});
}

var deleteItem = function(jQElement, onRemovalCallback) {
	var jQElementCopy = jQElement.clone();
	var scope = jQElement.parent().parent().parent().parent();
	console.log(scope);
	jQElement.fadeOut(500, function() {
		// remove scrollbar if necessary
		var scrollDiv = scope.find(".scroll-list");
		if(scrollDiv.height() <= 75) {
			scrollDiv.css("overflow-y", "visible");
		}
		onRemovalCallback(jQElementCopy);
	});
}

var createAutocompleteInput = function() {
	var input = $("<select class='option-select' style='width:50%'></select>");
	$.each($(".option-data"), function(i, optionInput) {
		input.append($('<option data-uri="' + $(optionInput).attr('data-uri') + '">' + $(optionInput).val() + '</option>'));
	});
	return input;
}

var addItem = function(jQElement, onAddCallback, type) {
	var tableRow = $("<tr class='class-item'></tr>");
	var tdItemDetail = $("<td class='item-detail' id='editable-item-detail'></td>");
	var input = createAutocompleteInput();
	tdItemDetail.append(input);
	input.select2({
		placeholder: "Select an item"
	})
	var cancelSpan = $("<span>&nbsp;<a href='#' id='save-select'>save</a> <a href='#' id='cancel'>cancel</a></span>");
	tdItemDetail.append(cancelSpan);
	tableRow.append(tdItemDetail);

	var scope = jQElement.parent().parent();
	scope.find("table").append(tableRow);

	var scrollDiv = scope.find(".scroll-list");
	if(scrollDiv.height() >= 75) {
		scrollDiv.css("overflow-y", "scroll");
	}

	$("#cancel").click(function(e) {
		e.preventDefault();
		$(this).parent().parent().remove();
	})

	var getOptionURI = function(input, val) {
		var attr = "";
		opts = $(input).find("option")
		for(var i = 0; i < opts.length; i++) {
			el = opts[i];
			if($(el).text() == val) {
				attr = el.getAttribute("data-uri");
			}
		}
		return attr;
	}

	$("#save-select").click(function() {
		tdItemDetail.text(input.val());
		var uri = getOptionURI(input, input.val());
		tdItemDetail.attr('title', uri);
		tdItemDetail.attr('data-' + type + '-uri', uri);

		/* fill out rest of table row */

		tableRow.append($("<td class='item-spacer'></td>"));
		tableRow.append($("<td class='item-action'><i class='fa fa-pencil action action-edit-" + type + "' title='Edit/replace'></i></td>"))
		tableRow.append($("<td class='item-action'><i class='fa fa-trash action action-delete-" + type + "' title='Remove this'></i></td>"))

		input.remove();
		tableRow.css({'background-color': '#FFFFAA'});
		tableRow.animate({'backgroundColor': '#FFFFFF'}, 1500, function() {
			onAddCallback(tdItemDetail);
		})
		
	})
}