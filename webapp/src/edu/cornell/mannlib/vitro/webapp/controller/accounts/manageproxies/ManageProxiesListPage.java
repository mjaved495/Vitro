/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.accounts.manageproxies;

import static edu.cornell.mannlib.vitro.webapp.controller.accounts.manageproxies.ProxyRelationshipSelectionCriteria.DEFAULT_RELATIONSHIPS_PER_PAGE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.AbstractPageHandler;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.manageproxies.ProxyRelationshipSelectionCriteria.ProxyRelationshipView;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.manageproxies.ProxyRelationshipSelector.Context;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.utils.ImageUtil;

/**
 * TODO
 */
public class ManageProxiesListPage extends AbstractPageHandler {
	private static final Log log = LogFactory
			.getLog(ManageProxiesListPage.class);

	public static final String PARAMETER_RELATIONSHIPS_PER_PAGE = "relationshipsPerPage";
	public static final String PARAMETER_PAGE_INDEX = "pageIndex";
	public static final String PARAMETER_VIEW_TYPE = "viewType";
	public static final String PARAMETER_SEARCH_TERM = "searchTerm";

	private static final String TEMPLATE_NAME = "manageProxies-list.ftl";

	private static final String PROPERTY_PROFILE_TYPES = "proxy.eligibleTypeList";

	private static final String DEFAULT_IMAGE_URL = UrlBuilder
			.getUrl("/images/placeholders/person.thumbnail.jpg");

	private final Context selectorContext;

	private ProxyRelationshipSelectionCriteria criteria = ProxyRelationshipSelectionCriteria.DEFAULT_CRITERIA;

	public ManageProxiesListPage(VitroRequest vreq) {
		super(vreq);

		selectorContext = new Context(userAccountsModel, unionModel,
				getMatchingProperty());
		parseParameters();
	}

	private String getMatchingProperty() {
		return ConfigurationProperties.getBean(vreq).getProperty(
				"selfEditing.idMatchingProperty", "");
	}

	/**
	 * Build the criteria from the request parameters.
	 */
	private void parseParameters() {
		int relationshipsPerPage = getIntegerParameter(
				PARAMETER_RELATIONSHIPS_PER_PAGE,
				DEFAULT_RELATIONSHIPS_PER_PAGE);
		int pageIndex = getIntegerParameter(PARAMETER_PAGE_INDEX, 1);
		ProxyRelationshipView viewType = ProxyRelationshipView.fromKeyword(vreq
				.getParameter(PARAMETER_VIEW_TYPE));
		String searchTerm = getStringParameter(PARAMETER_SEARCH_TERM, "");

		criteria = new ProxyRelationshipSelectionCriteria(relationshipsPerPage,
				pageIndex, viewType, searchTerm);

		log.debug("selection criteria is: " + criteria);
	}

	public ResponseValues showPage() {
		ProxyRelationshipSelection selection = ProxyRelationshipSelector
				.select(selectorContext, criteria);
		Map<String, Object> body = buildTemplateBodyMap(selection);
		return new TemplateResponseValues(TEMPLATE_NAME, body);
	}

	private Map<String, Object> buildTemplateBodyMap(
			ProxyRelationshipSelection selection) {
		Map<String, Object> body = new HashMap<String, Object>();

		body.put("relationshipsPerPage", criteria.getRelationshipsPerPage());
		body.put("pageIndex", criteria.getPageIndex());
		body.put("viewType", criteria.getViewBy());
		body.put("searchTerm", criteria.getSearchTerm());

		body.put("relationships", wrapProxyRelationships(selection));
		body.put("total", selection.getTotalResultCount());
		body.put("page", buildPageMap(selection));

		body.put("matchingProperty", getMatchingProperty());
		body.put("profileTypes", buildProfileTypesString());

		body.put("formUrls", buildUrlsMap());

		applyMessage(vreq, body);

		log.debug("body map is: " + body);
		return body;
	}

	private String buildProfileTypesString() {
		return ConfigurationProperties.getBean(vreq).getProperty(
				PROPERTY_PROFILE_TYPES, "http://www.w3.org/2002/07/owl#Thing");
	}

	private List<ProxyRelationship> wrapProxyRelationships(
			ProxyRelationshipSelection selection) {
		List<ProxyRelationship> wrapped = new ArrayList<ProxyRelationship>();
		for (ProxyRelationship r : selection.getProxyRelationships()) {
			wrapped.add(new ProxyRelationship(wrapProxyItemList(r
					.getProxyInfos()), wrapProfileItemList(r.getProfileInfos())));
		}
		return wrapped;
	}

	private List<ProxyItemInfo> wrapProxyItemList(List<ProxyItemInfo> items) {
		List<ProxyItemInfo> wrapped = new ArrayList<ProxyItemInfo>();
		for (ProxyItemInfo item : items) {
			wrapped.add(wrapProxyItem(item));
		}
		return wrapped;
	}

	private List<ProxyItemInfo> wrapProfileItemList(List<ProxyItemInfo> items) {
		List<ProxyItemInfo> wrapped = new ArrayList<ProxyItemInfo>();
		for (ProxyItemInfo item : items) {
			wrapped.add(wrapProfileItem(item));
		}
		return wrapped;
	}

	private ProxyItemInfo wrapProxyItem(ProxyItemInfo item) {
		String imagePath = item.getImageUrl();
		if (imagePath.isEmpty()) {
			imagePath = ImageUtil
					.getPlaceholderImagePathForType(VitroVocabulary.USERACCOUNT);
		}

		return new ProxyItemWrapper(item.getUri(), item.getLabel(),
				item.getClassLabel(), UrlBuilder.getUrl(imagePath));
	}

	private ProxyItemInfo wrapProfileItem(ProxyItemInfo item) {
		String imagePath = item.getImageUrl();
		if (imagePath.isEmpty()) {
			imagePath = ImageUtil
					.getPlaceholderImagePathForIndividual(vreq, item.getUri());
		}

		return new ProfileItemWrapper(item.getUri(), item.getLabel(),
				item.getClassLabel(), UrlBuilder.getUrl(imagePath));
	}

	private Map<String, Integer> buildPageMap(
			ProxyRelationshipSelection selection) {
		int currentPage = selection.getCriteria().getPageIndex();

		float pageCount = ((float) selection.getTotalResultCount())
				/ selection.getCriteria().getRelationshipsPerPage();
		int lastPage = (int) Math.ceil(pageCount);

		Map<String, Integer> map = new HashMap<String, Integer>();

		map.put("current", currentPage);
		map.put("first", 1);
		map.put("last", lastPage);

		if (currentPage < lastPage) {
			map.put("next", currentPage + 1);
		}
		if (currentPage > 1) {
			map.put("previous", currentPage - 1);
		}

		return map;
	}

	protected Map<String, String> buildUrlsMap() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("list", UrlBuilder.getUrl("/manageProxies/list"));
		map.put("edit", UrlBuilder.getUrl("/manageProxies/edit"));
		map.put("create", UrlBuilder.getUrl("/manageProxies/create"));
		map.put("sparqlQueryAjax", UrlBuilder.getUrl("/ajax/sparqlQuery"));
		map.put("defaultImageUrl", DEFAULT_IMAGE_URL);

		return map;
	}

	private static class ProxyItemWrapper extends ProxyItemInfo {
		public ProxyItemWrapper(String uri, String label, String classLabel,
				String imageUrl) {
			super(uri, label, classLabel, imageUrl);
		}
	}

	private static class ProfileItemWrapper extends ProxyItemInfo {
		public ProfileItemWrapper(String uri, String label, String classLabel,
				String imageUrl) {
			super(uri, label, classLabel, imageUrl);
		}
	}
}
