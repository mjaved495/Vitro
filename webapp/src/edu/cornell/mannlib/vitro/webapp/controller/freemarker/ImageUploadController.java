/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.filestorage.FileModelHelper;
import edu.cornell.mannlib.vitro.webapp.filestorage.FileServingHelper;
import edu.cornell.mannlib.vitro.webapp.filestorage.backend.FileStorage;
import edu.cornell.mannlib.vitro.webapp.filestorage.backend.FileStorageSetup;
import edu.cornell.mannlib.vitro.webapp.filestorage.uploadrequest.FileUploadServletRequest;
import freemarker.template.Configuration;

/**
 * Handle adding, replacing or deleting the main image on an Individual.
 */
public class ImageUploadController extends FreeMarkerHttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory
			.getLog(ImageUploadController.class);

	private static final String DEFAULT_NAMESPACE = ConfigurationProperties
			.getProperty("Vitro.defaultNamespace");

	public static final String DUMMY_THUMBNAIL_URL = "/images/dummyImages/person.thumbnail.jpg";

	/** Limit file size to 50 megabytes. */
	public static final int MAXIMUM_FILE_SIZE = 50 * 1024 * 1024;

	/** Generated thumbnails will be this big. */
	public static final int THUMBNAIL_HEIGHT = 115;
	public static final int THUMBNAIL_WIDTH = 115;

	public static final String PARAMETER_ACTION = "action";
	public static final String PARAMETER_ENTITY_URI = "entityUri";
	public static final String PARAMETER_UPLOADED_FILE = "datafile";

	public static final String ACTION_SAVE = "save";
	public static final String ACTION_UPLOAD = "upload";
	public static final String ACTION_DELETE = "delete";

	public static final String BODY_TITLE = "title";
	public static final String BODY_ENTITY_NAME = "entityName";
	public static final String BODY_MAIN_IMAGE_URL = "imageUrl";
	public static final String BODY_THUMBNAIL_URL = "thumbnailUrl";
	public static final String BODY_CANCEL_URL = "cancelUrl";
	public static final String BODY_DELETE_URL = "deleteUrl";
	public static final String BODY_FORM_ACTION = "formAction";
	public static final String BODY_ERROR_MESSAGE = "errorMessage";

	public static final String TEMPLATE_NEW = "imageUpload/newImage.ftl";
	public static final String TEMPLATE_REPLACE = "imageUpload/replaceImage.ftl";
	public static final String TEMPLATE_CROP = "imageUpload/cropImage.ftl";

	private static final String URL_HERE = UrlBuilder.getUrl("/uploadImages");

	private FileStorage fileStorage;

	/**
	 * When initialized, get a reference to the File Storage system. Without
	 * that, we can do nothing.
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		Object o = getServletContext().getAttribute(
				FileStorageSetup.ATTRIBUTE_NAME);
		if (o instanceof FileStorage) {
			fileStorage = (FileStorage) o;
		} else if (o == null) {
			throw new UnavailableException(this.getClass().getSimpleName()
					+ " could not initialize. Attribute '"
					+ FileStorageSetup.ATTRIBUTE_NAME
					+ "' was not set in the servlet context.");
		} else {
			throw new UnavailableException(this.getClass().getSimpleName()
					+ " could not initialize. Attribute '"
					+ FileStorageSetup.ATTRIBUTE_NAME
					+ "' in the servlet context contained an instance of '"
					+ o.getClass().getName() + "' instead of '"
					+ FileStorage.class.getName() + "'");
		}
	}

	/**
	 * <p>
	 * Parse the multi-part request, process the request, and produce the
	 * output.
	 * </p>
	 * <p>
	 * If the request was a multi-part file upload, it will parse to a
	 * normal-looking request with a "file_item_map" attribute.
	 * </p>
	 * <p>
	 * The processing will produce a {@link ResponseValues} object, which
	 * represents either a request for a FreeMarker template or a forwarding
	 * operation.
	 * <ul>
	 * <li>If a FreeMarker template, we emulate the actions that
	 * FreeMarkerHttpServlet would have taken to produce the output.</li>
	 * <li>If a forwarding operation, we create a {@link RequestDispatcher} to
	 * do the forwarding.</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		try {
			FileUploadServletRequest parsedRequest = FileUploadServletRequest
					.parseRequest(request, MAXIMUM_FILE_SIZE);
			if (log.isTraceEnabled()) {
				dumpRequestDetails(parsedRequest);
			}
		} catch (FileUploadException e) {
			// Swallow the exception here. Test for FILE_ITEM_MAP later.
			log.error("Failed to parse the multi-part HTTP request", e);
		}

		try {
			// execute super.super.doGet()
			vitroHttpServletDoGet(request, response);

			VitroRequest vreq = new VitroRequest(request);
			ResponseValues values = buildTheResponse(vreq);

			if (values.isForwardResponse()) {
				doForward(vreq, response, values);
			} else {
				doTemplate(vreq, response, values);
			}
		} catch (Exception e) {
			log.error("Could not produce response page", e);
		}
	}

	/**
	 * We processed a response, and want to show a template.
	 */
	private void doTemplate(VitroRequest vreq, HttpServletResponse response,
			ResponseValues values) {
		// Set it up like FreeMarkerHttpServlet.doGet() would do.
		Configuration config = getConfig(vreq);
		Map<String, Object> sharedVariables = getSharedVariables(vreq);
		Map<String, Object> root = new HashMap<String, Object>(sharedVariables);
		Map<String, Object> body = new HashMap<String, Object>(sharedVariables);
		setUpRoot(vreq, root);

		// Add the values that we got, and merge to the template.
		body.putAll(values.getBodyMap());
		root.put("body", mergeBodyToTemplate(values.getTemplateName(), body,
				config));

		// Continue to simulate FreeMarkerHttpServlet.doGet()
		root.put("title", body.get("title"));
		writePage(root, config, response);
	}

	/**
	 * We processsed a response, and want to forward to another page.
	 */
	private void doForward(HttpServletRequest req, HttpServletResponse resp,
			ResponseValues values) throws ServletException, IOException {
		req.getRequestDispatcher(values.getForwardUrl()).forward(req, resp);
	}

	/**
	 * Handle the different actions. If not specified, the default action is to
	 * show the intro screen.
	 */
	private ResponseValues buildTheResponse(VitroRequest vreq) {
		String action = vreq.getParameter(PARAMETER_ACTION);

		try {
			Individual entity = validateEntityUri(vreq);
			if (ACTION_UPLOAD.equals(action)) {
				return doUploadImage(vreq, entity);
			} else if (ACTION_SAVE.equals(action)) {
				return doCreateThumbnail(vreq, entity);
			} else if (ACTION_DELETE.equals(action)) {
				return doDeleteImage(entity);
			} else {
				return doIntroScreen(entity);
			}
		} catch (UserMistakeException e) {
			// Can't find the entity? Complain.
			return showAddImagePageWithError(null, e.getMessage());
		} catch (Exception e) {
			// We weren't expecting this - dump as much info as possible.
			log.error(e, e);
			return doError(e.toString());
		}
	}

	/**
	 * Show the first screen in the upload process: Add or Replace.
	 */
	private ResponseValues doIntroScreen(Individual entity) {
		String thumbUrl = getThumbnailUrl(entity);
		if (thumbUrl == null) {
			return showAddImagePage(entity);
		} else {
			return showReplaceImagePage(entity, thumbUrl);
		}
	}

	/**
	 * The user has selected their main image file. Remove any previous main
	 * image (and thumbnail), and attach the new main image.
	 */
	private ResponseValues doUploadImage(VitroRequest vreq, Individual entity) {
		ImageUploadHelper helper = new ImageUploadHelper(fileStorage,
				getWebappDaoFactory());

		// Did they provide a file to upload? If not, show an error.
		FileItem fileItem;
		try {
			fileItem = helper.validateImageFromRequest(vreq);
		} catch (UserMistakeException e) {
			String thumbUrl = getThumbnailUrl(entity);
			String message = e.getMessage();
			if (thumbUrl == null) {
				return showAddImagePageWithError(entity, message);
			} else {
				return showReplaceImagePageWithError(entity, thumbUrl, message);
			}
		}

		// Remove the old main image (if any) and store the new one.
		helper.removeExistingImage(entity);
		helper.storeMainImageFile(entity, fileItem);

		// The entity Individual is stale - get another one;
		String entityUri = entity.getURI();
		entity = getWebappDaoFactory().getIndividualDao().getIndividualByURI(
				entityUri);

		// Go to the cropping page.
		return showCropImagePage(entity, getMainImageUrl(entity));
	}

	/**
	 * The user has specified how to crop the thumbnail. Crop it and attach it
	 * to the main image.
	 */
	private ResponseValues doCreateThumbnail(VitroRequest vreq,
			Individual entity) {
		ImageUploadHelper helper = new ImageUploadHelper(fileStorage,
				getWebappDaoFactory());

		validateMainImage(entity);
		CropRectangle crop = validateCropCoordinates(vreq);

		helper.removeExistingThumbnail(entity);
		helper.generateThumbnailAndStore(entity, crop);

		return showIndividualDisplayPage(entity);
	}

	/**
	 * Delete the main image and the thumbnail from the individual.
	 */
	private ResponseValues doDeleteImage(Individual entity) {
		ImageUploadHelper helper = new ImageUploadHelper(fileStorage,
				getWebappDaoFactory());

		helper.removeExistingImage(entity);

		return showIndividualDisplayPage(entity);
	}

	/**
	 * Display a error message to the user.
	 * 
	 * @message The text of the error message.
	 */
	private TemplateResponseValues doError(String message) {
		TemplateResponseValues rv = new TemplateResponseValues(
				"errorMessage.ftl");
		rv.put("errorMessage", message);
		return rv;
	}

	/**
	 * We need to be talking about an actual Individual here.
	 */
	private Individual validateEntityUri(VitroRequest vreq)
			throws UserMistakeException {
		String entityUri = vreq.getParameter(PARAMETER_ENTITY_URI);
		if (entityUri == null) {
			throw new UserMistakeException("No entity URI was provided");
		}

		Individual entity = getWebappDaoFactory().getIndividualDao()
				.getIndividualByURI(entityUri);
		if (entity == null) {
			throw new UserMistakeException(
					"This URI is not recognized as belonging to anyone: '"
							+ entityUri + "'");
		}
		return entity;
	}

	/**
	 * We can't do a thumbnail if there is no main image.
	 */
	private void validateMainImage(Individual entity) {
		if (entity.getMainImageUri() == null) {
			throw new IllegalStateException("Can't store a thumbnail "
					+ "on an individual with no main image: '"
					+ showEntity(entity) + "'");
		}
	}

	/**
	 * Did we get the cropping coordinates?
	 */
	private CropRectangle validateCropCoordinates(VitroRequest vreq) {
		int x = getRequiredIntegerParameter(vreq, "x");
		int y = getRequiredIntegerParameter(vreq, "y");
		int h = getRequiredIntegerParameter(vreq, "h");
		int w = getRequiredIntegerParameter(vreq, "w");
		return new CropRectangle(x, y, h, w);
	}

	/**
	 * We need this parameter on the request, and it must be a valid integer.
	 */
	private int getRequiredIntegerParameter(HttpServletRequest req, String key) {
		String string = req.getParameter(key);
		if (string == null) {
			throw new IllegalStateException(
					"Request did not contain a value for '" + key + "'");
		}
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			throw new IllegalStateException("Value for '" + key
					+ "' was not a valid integer: '" + string + "'");
		}
	}

	/**
	 * Get the URL that will serve this entity's main image, or null.
	 */
	private String getMainImageUrl(Individual entity) {
		String imageUri = FileModelHelper.getMainImageBytestreamUri(entity);
		String imageFilename = FileModelHelper.getMainImageFilename(entity);
		return FileServingHelper.getBytestreamAliasUrl(imageUri, imageFilename);
	}

	/**
	 * Get the URL that will serve this entity's thumbnail image, or null.
	 */
	private String getThumbnailUrl(Individual entity) {
		String thumbUri = FileModelHelper.getThumbnailBytestreamUri(entity);
		String thumbFilename = FileModelHelper.getThumbnailFilename(entity);
		return FileServingHelper.getBytestreamAliasUrl(thumbUri, thumbFilename);
	}

	/**
	 * The individual has no image - go to the Add Image page.
	 * 
	 * @param entity
	 *            if this is null, then all URLs lead to the welcome page.
	 */
	private TemplateResponseValues showAddImagePage(Individual entity) {
		String formAction = (entity == null) ? "/" : formAction(
				entity.getURI(), ACTION_UPLOAD);
		String cancelUrl = (entity == null) ? "/" : displayPageUrl(entity
				.getURI());

		TemplateResponseValues rv = new TemplateResponseValues(TEMPLATE_NEW);
		rv.put(BODY_THUMBNAIL_URL, UrlBuilder.getUrl(DUMMY_THUMBNAIL_URL));
		rv.put(BODY_FORM_ACTION, formAction);
		rv.put(BODY_CANCEL_URL, cancelUrl);
		rv.put(BODY_TITLE, "Upload image" + forName(entity));
		return rv;
	}

	/**
	 * The individual has no image, but the user did something wrong.
	 */
	private TemplateResponseValues showAddImagePageWithError(Individual entity,
			String message) {
		TemplateResponseValues rv = showAddImagePage(entity);
		rv.put(BODY_ERROR_MESSAGE, message);
		return rv;
	}

	/**
	 * The individual has an image - go to the Replace Image page.
	 */
	private TemplateResponseValues showReplaceImagePage(Individual entity,
			String thumbUrl) {
		TemplateResponseValues rv = new TemplateResponseValues(TEMPLATE_REPLACE);
		rv.put(BODY_THUMBNAIL_URL, UrlBuilder.getUrl(thumbUrl));
		rv.put(BODY_DELETE_URL, formAction(entity.getURI(), ACTION_DELETE));
		rv.put(BODY_FORM_ACTION, formAction(entity.getURI(), ACTION_UPLOAD));
		rv.put(BODY_CANCEL_URL, displayPageUrl(entity.getURI()));
		rv.put(BODY_TITLE, "Replace image" + forName(entity));
		return rv;
	}

	/**
	 * The individual has an image, but the user did something wrong.
	 */
	private TemplateResponseValues showReplaceImagePageWithError(
			Individual entity, String thumbUrl, String message) {
		TemplateResponseValues rv = showReplaceImagePage(entity, thumbUrl);
		rv.put(BODY_ERROR_MESSAGE, message);
		return rv;
	}

	/**
	 * We got their main image - go to the Crop Image page.
	 */
	private TemplateResponseValues showCropImagePage(Individual entity,
			String imageUrl) {
		TemplateResponseValues rv = new TemplateResponseValues(TEMPLATE_CROP);
		rv.put(BODY_MAIN_IMAGE_URL, UrlBuilder.getUrl(imageUrl));
		rv.put(BODY_FORM_ACTION, formAction(entity.getURI(), ACTION_SAVE));
		rv.put(BODY_CANCEL_URL, displayPageUrl(entity.getURI()));
		rv.put(BODY_TITLE, "Crop Photo" + forName(entity));
		return rv;
	}

	/**
	 * All done - go to the individual display page.
	 */
	private ForwardResponseValues showIndividualDisplayPage(Individual entity) {
		return new ForwardResponseValues(displayPageUrl(entity.getURI()));
	}

	/**
	 * When we complete the process, by success or by cancellation, we go to the
	 * individual display page.
	 */
	private String displayPageUrl(String entityUri) {
		if (DEFAULT_NAMESPACE == null) {
			return "/";
		} else if (!entityUri.startsWith(DEFAULT_NAMESPACE)) {
			return "/";
		} else {
			String tail = entityUri.substring(DEFAULT_NAMESPACE.length());
			if (!tail.startsWith("/")) {
				tail = "/" + tail;
			}
			return "display" + tail;
		}
	}

	/**
	 * The "action" parameter on the HTML "form" tag should include the path
	 * back to this controller, along with the desired action and the Entity
	 * URI.
	 */
	private String formAction(String entityUri, String action) {
		UrlBuilder.Params params = new UrlBuilder.Params(PARAMETER_ENTITY_URI,
				entityUri, PARAMETER_ACTION, action);
		return UrlBuilder.getPath(URL_HERE, params);
	}

	/**
	 * Format an entity for display in a message.
	 */
	private String showEntity(Individual entity) {
		if (entity == null) {
			return String.valueOf(null);
		} else if (entity.getName() == null) {
			return "'no name' (" + entity.getURI() + ")";
		} else {
			return "'" + entity.getName() + "' (" + entity.getURI() + ")";
		}
	}

	/**
	 * Format the entity's name for display as part of the page title.
	 */
	private String forName(Individual entity) {
		if (entity != null) {
			String name = entity.getName();
			if (name != null) {
				return " for " + name;
			}
		}
		return "";
	}

	/**
	 * Holds an error message to use as a complaint to the user.
	 */
	static class UserMistakeException extends Exception {
		UserMistakeException(String message) {
			super(message);
		}
	}

	/**
	 * Holds the coordinates that we use to crop the main image.
	 */
	static class CropRectangle {
		final int x;
		final int y;
		final int height;
		final int width;

		private CropRectangle(int x, int y, int height, int width) {
			this.x = x;
			this.y = y;
			this.height = height;
			this.width = width;
		}

	}

	/**
	 * For debugging, dump all sorts of information about the request.
	 * 
	 * WARNING: if "req" represents a Multi-part request which has not
	 * yet been parsed, then reading these parameters will consume them.
	 */
	@SuppressWarnings("unchecked")
	private void dumpRequestDetails(HttpServletRequest req) {
		log.trace("Request is " + req.getClass().getName());

		Map<String, String[]> parms = req.getParameterMap();
		for (Entry<String, String[]> entry : parms.entrySet()) {
			log.trace("Parameter '" + entry.getKey() + "'="
					+ Arrays.deepToString(entry.getValue()));
		}

		Enumeration<String> attrs = req.getAttributeNames();
		while (attrs.hasMoreElements()) {
			String key = attrs.nextElement();
			String valueString = String.valueOf(req.getAttribute(key));
			String valueOneLine = valueString.replace("\n", " | ");
			log.trace("Attribute '" + key + "'=" + valueOneLine);
		}
	}

	private static interface ResponseValues {
		boolean isForwardResponse();

		String getTemplateName();

		Map<? extends String, ? extends Object> getBodyMap();

		String getForwardUrl();
	}

	private static class TemplateResponseValues implements ResponseValues {
		private final String templateName;
		private final Map<String, Object> bodyMap = new HashMap<String, Object>();

		public TemplateResponseValues(String templateName) {
			this.templateName = templateName;
		}

		public void put(String key, Object value) {
			this.bodyMap.put(key, value);
		}

		@Override
		public boolean isForwardResponse() {
			return false;
		}

		@Override
		public String getForwardUrl() {
			throw new IllegalStateException(
					"This is not a forwarding response.");
		}

		@Override
		public Map<? extends String, ? extends Object> getBodyMap() {
			return Collections.unmodifiableMap(this.bodyMap);
		}

		@Override
		public String getTemplateName() {
			return this.templateName;
		}

	}

	private static class ForwardResponseValues implements ResponseValues {
		private final String forwardUrl;

		public ForwardResponseValues(String forwardUrl) {
			this.forwardUrl = forwardUrl;
		}

		@Override
		public boolean isForwardResponse() {
			return true;
		}

		@Override
		public String getForwardUrl() {
			return this.forwardUrl;
		}

		@Override
		public Map<? extends String, ? extends Object> getBodyMap() {
			throw new IllegalStateException("This is not a template response.");
		}

		@Override
		public String getTemplateName() {
			throw new IllegalStateException("This is not a template response.");
		}

	}
}
