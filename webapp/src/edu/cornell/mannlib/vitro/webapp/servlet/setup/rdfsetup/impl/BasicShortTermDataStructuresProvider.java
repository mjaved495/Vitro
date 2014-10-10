/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.servlet.setup.rdfsetup.impl;

import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.WhichService.CONFIGURATION;
import static edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.WhichService.CONTENT;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.atlas.lib.Pair;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactoryConfig;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess.WhichService;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ontmodels.JoinedOntModelCache;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ontmodels.OntModelCache;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.filter.LanguageFilteringUtils;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.rdfsetup.ShortTermDataStructuresProvider;
import edu.cornell.mannlib.vitro.webapp.utils.logging.ToString;

/**
 * The simple implementation of ShortTermDataStructuresProvider.
 * 
 * The short-term RDFServices are cached, lest we somehow create duplicates for
 * the same request. Similarly with the short-term OntModels.
 */
public class BasicShortTermDataStructuresProvider implements
		ShortTermDataStructuresProvider {
	private static final Log log = LogFactory
			.getLog(BasicShortTermDataStructuresProvider.class);

	private final HttpServletRequest req;
	private final ServletContext ctx;
	private final ConfigurationProperties props;
	private final BasicDataStructuresProvider parent;
	private final Map<WhichService, SingleSourceDataStructuresProvider> providers;
	private final Map<WhichService, RDFService> rdfServices;
	private final OntModelCache ontModelCache;

	public BasicShortTermDataStructuresProvider(
			HttpServletRequest req,
			BasicDataStructuresProvider parent,
			final Map<WhichService, SingleSourceDataStructuresProvider> providers) {
		this.req = req;
		this.ctx = req.getSession().getServletContext();
		this.props = ConfigurationProperties.getBean(ctx);
		this.parent = parent;
		this.providers = providers;
		this.rdfServices = populateRdfServicesMap();
		this.ontModelCache = createOntModelCache();
	}

	private Map<WhichService, RDFService> populateRdfServicesMap() {
		Map<WhichService, RDFService> map = new EnumMap<>(WhichService.class);
		for (WhichService which : WhichService.values()) {
			map.put(which, parent.getRDFServiceFactory(which)
					.getShortTermRDFService());
		}
		return Collections.unmodifiableMap(map);
	}

	private OntModelCache createOntModelCache() {
		return new JoinedOntModelCache(shortModels(CONTENT),
				shortModels(CONFIGURATION));
	}

	/**
	 * Ask each provider what short-term models should mask their long-term
	 * counterparts.
	 */
	private OntModelCache shortModels(WhichService which) {
		return providers.get(which).getShortTermOntModels(
				rdfServices.get(which), parent.getOntModels(which));
	}

	@Override
	public RDFService getRDFService(WhichService whichService) {
		return rdfServices.get(whichService);
	}

	@Override
	public OntModelCache getOntModelCache() {
		return ontModelCache;
	}

	@Override
	public WebappDaoFactoryConfig getWebappDaoFactoryConfig() {
		List<String> langs = getPreferredLanguages();
		WebappDaoFactoryConfig config = new WebappDaoFactoryConfig();
		config.setDefaultNamespace(props.getProperty("Vitro.defaultNamespace"));
		config.setPreferredLanguages(langs);
		config.setUnderlyingStoreReasoned(isStoreReasoned());
		config.setCustomListViewConfigFileMap(getCustomListViewConfigFileMap());
		return config;
	}

	private List<String> getPreferredLanguages() {
		log.debug("Accept-Language: " + req.getHeader("Accept-Language"));
		return LanguageFilteringUtils.localesToLanguages(getPreferredLocales());
	}

	@SuppressWarnings("unchecked")
	private Enumeration<Locale> getPreferredLocales() {
		return req.getLocales();
	}

	private boolean isStoreReasoned() {
		String isStoreReasoned = props.getProperty(
				"VitroConnection.DataSource.isStoreReasoned", "true");
		return ("true".equals(isStoreReasoned));
	}

	private Map<Pair<String, Pair<ObjectProperty, String>>, String> getCustomListViewConfigFileMap() {
		Map<Pair<String, Pair<ObjectProperty, String>>, String> map = (Map<Pair<String, Pair<ObjectProperty, String>>, String>) ctx
				.getAttribute("customListViewConfigFileMap");
		if (map == null) {
			map = new ConcurrentHashMap<Pair<String, Pair<ObjectProperty, String>>, String>();
			ctx.setAttribute("customListViewConfigFileMap", map);
		}
		return map;
	}

	@Override
	public void close() {
		for (WhichService which : WhichService.values()) {
			rdfServices.get(which).close();
		}
	}

	@Override
	public String toString() {
		return "BasicShortTermDataStructuresProvider[" + ToString.hashHex(this)
				+ ", req=" + ToString.hashHex(req) + ", providers=" + providers
				+ ", ontModels=" + ontModelCache + "]";
	}

}
