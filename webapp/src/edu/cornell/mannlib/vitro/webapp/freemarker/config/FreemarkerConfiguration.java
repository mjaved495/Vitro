/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.freemarker.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.config.RevisionInfoBean;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.DelimitingTemplateLoader;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FlatteningTemplateLoader;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfigurationConstants;
import edu.cornell.mannlib.vitro.webapp.i18n.freemarker.I18nMethodModel;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;
import edu.cornell.mannlib.vitro.webapp.web.directives.IndividualShortViewDirective;
import edu.cornell.mannlib.vitro.webapp.web.directives.UrlDirective;
import edu.cornell.mannlib.vitro.webapp.web.directives.WidgetDirective;
import edu.cornell.mannlib.vitro.webapp.web.methods.IndividualLocalNameMethod;
import edu.cornell.mannlib.vitro.webapp.web.methods.IndividualPlaceholderImageUrlMethod;
import edu.cornell.mannlib.vitro.webapp.web.methods.IndividualProfileUrlMethod;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * Access point for a singleton Configuration instance.
 * 
 * The instance is created at system startup, so we can fail early if there are
 * any problems.
 * 
 * The Configuration is slightly extended to hold request-based information in a
 * ThreadLocal. The net result is although there is only one configuration (and
 * hence only one template cache), each request gets a customization with its
 * own locale, etc.
 * 
 * Each time a request asks for the configuration, check to see whether the
 * cache is still valid, and whether the theme has changed (needs a new
 * TemplateLoader). Store the request info to the ThreadLocal.
 */
public abstract class FreemarkerConfiguration {
	private static final Log log = LogFactory
			.getLog(FreemarkerConfiguration.class);

	private static final String PROPERTY_DEFEAT_CACHE = "developer.defeatFreemarkerCache";
	private static final String PROPERTY_INSERT_DELIMITERS = "developer.insertFreemarkerDelimiters";

	private static volatile FreemarkerConfigurationImpl instance;
	private static volatile String previousThemeDir;

	public static Configuration getConfig(HttpServletRequest req) {
		confirmInstanceIsSet();

		synchronized (instance) {
			clearTemplateCacheIfRequested(req);
			keepTemplateLoaderCurrentWithThemeDirectory(req);
			setThreadLocalsForRequest(req);
			return instance;
		}
	}

	private static void confirmInstanceIsSet() {
		if (instance == null) {
			throw new IllegalStateException(
					"VitroFreemarkerConfiguration has not been set.");
		}
	}

	private static void clearTemplateCacheIfRequested(HttpServletRequest req) {
		if (isTemplateCacheInvalid(req)) {
			instance.clearTemplateCache();
		}
	}

	private static boolean isTemplateCacheInvalid(HttpServletRequest req) {
		ConfigurationProperties props = ConfigurationProperties.getBean(req);

		// If the developer doesn't want the cache, it's invalid.
		if (Boolean.valueOf(props.getProperty(PROPERTY_DEFEAT_CACHE))) {
			return true;
		}

		return false;
	}

	/**
	 * Keep track of the theme directory. If it changes, create an appropriate
	 * new TemplateLoader.
	 * 
	 * Note that setting a new TemplateLoader on the context Configuration also
	 * creates a new, empty TemplateCache.
	 */
	private static void keepTemplateLoaderCurrentWithThemeDirectory(
			HttpServletRequest req) {
		String themeDir = getThemeDirectory(req);
		if (hasThemeDirectoryChanged(themeDir)) {
			TemplateLoader tl = createTemplateLoader(req, themeDir);
			instance.setTemplateLoader(tl);
		}
	}

	private static String getThemeDirectory(HttpServletRequest req) {
		return new VitroRequest(req).getAppBean().getThemeDir();
	}

	private static boolean hasThemeDirectoryChanged(String themeDir) {
		synchronized (instance) {
			if (StringUtils.equals(themeDir, previousThemeDir)) {
				return false;
			} else {
				previousThemeDir = themeDir;
				return true;
			}
		}
	}

	private static TemplateLoader createTemplateLoader(HttpServletRequest req,
			String themeDir) {
		ServletContext ctx = req.getSession().getServletContext();
		ConfigurationProperties props = ConfigurationProperties.getBean(ctx);

		List<TemplateLoader> loaders = new ArrayList<TemplateLoader>();

		// Theme template loader
		String themeTemplatePath = ctx.getRealPath(themeDir) + "/templates";
		File themeTemplateDir = new File(themeTemplatePath);
		// A theme need not contain a template directory.
		if (themeTemplateDir.exists()) {
			try {
				FileTemplateLoader themeFtl = new FileTemplateLoader(
						themeTemplateDir);
				loaders.add(themeFtl);
			} catch (IOException e) {
				log.error("Error creating theme template loader", e);
			}
		}

		// Vitro template loader
		String vitroTemplatePath = ctx.getRealPath("/templates/freemarker");
		loaders.add(new FlatteningTemplateLoader(new File(vitroTemplatePath)));

		// TODO VIVO-243 Why is this here?
		loaders.add(new ClassTemplateLoader(FreemarkerConfiguration.class, ""));

		TemplateLoader[] loaderArray = loaders
				.toArray(new TemplateLoader[loaders.size()]);
		MultiTemplateLoader mtl = new MultiTemplateLoader(loaderArray);

		// If requested, add delimiters to the templates.
		if (Boolean.valueOf(props.getProperty(PROPERTY_INSERT_DELIMITERS))) {
			return new DelimitingTemplateLoader(mtl);
		} else {
			return mtl;
		}
	}

	private static void setThreadLocalsForRequest(HttpServletRequest req) {
		instance.setRequestInfo(req);
	}

	// ----------------------------------------------------------------------
	// Setup class
	// ----------------------------------------------------------------------

	public static class Setup implements ServletContextListener {
		@Override
		public void contextInitialized(ServletContextEvent sce) {
			ServletContext ctx = sce.getServletContext();
			StartupStatus ss = StartupStatus.getBean(ctx);
			try {
				instance = createConfiguration(ctx);
				ss.info(this, "Initialized the Freemarker configuration.");
			} catch (Exception e) {
				ss.fatal(this,
						"Failed to initialize the Freemarker configuration.", e);
			}
		}

		private FreemarkerConfigurationImpl createConfiguration(
				ServletContext ctx) throws TemplateModelException {
			FreemarkerConfigurationImpl c = new FreemarkerConfigurationImpl();

			setMiscellaneousProperties(c);
			setSharedVariables(c, ctx);
			addDirectives(c);
			addMethods(c);

			return c;
		}

		private void setMiscellaneousProperties(FreemarkerConfigurationImpl c) {
			/*
			 * Lengthen the cache time.
			 */
			c.setTemplateUpdateDelay(60); // increase from the 5-second default

			/*
			 * On most template models, hide the getters and setters that take
			 * arguments.
			 */
			BeansWrapper wrapper = new DefaultObjectWrapper();
			wrapper.setExposureLevel(BeansWrapper.EXPOSE_PROPERTIES_ONLY);
			c.setObjectWrapper(wrapper);

			/*
			 * Set a default Locale, but expect it to be overridden by the
			 * request.
			 */
			c.setLocale(java.util.Locale.US);

			/*
			 * This is how we like our date and time strings to look.
			 */
			String dateFormat = "M/d/yyyy";
			c.setDateFormat(dateFormat);
			String timeFormat = "h:mm a";
			c.setTimeFormat(timeFormat);
			c.setDateTimeFormat(dateFormat + " " + timeFormat);

			/*
			 * What character set is used when escaping special characters in a
			 * URL?
			 */
			try {
				c.setSetting("url_escaping_charset", "ISO-8859-1");
			} catch (TemplateException e) {
				log.error("Error setting value for url_escaping_charset.");
			}
		}

		private void setSharedVariables(FreemarkerConfigurationImpl c,
				ServletContext ctx) throws TemplateModelException {
			c.setSharedVariable("version", getRevisionInfo(ctx));

			/*
			 * Put in edit configuration constants - useful for freemarker
			 * templates/editing
			 */
			c.setSharedVariable("editConfigurationConstants",
					EditConfigurationConstants.exportConstants());
		}

		private void addDirectives(FreemarkerConfigurationImpl c) {
			c.setSharedVariable("dump", new freemarker.ext.dump.DumpDirective());
			c.setSharedVariable("dumpAll",
					new freemarker.ext.dump.DumpAllDirective());
			c.setSharedVariable("help", new freemarker.ext.dump.HelpDirective());
			c.setSharedVariable("shortView", new IndividualShortViewDirective());
			c.setSharedVariable("url", new UrlDirective());
			c.setSharedVariable("widget", new WidgetDirective());
		}

		private void addMethods(FreemarkerConfigurationImpl c) {
			c.setSharedVariable("profileUrl", new IndividualProfileUrlMethod());
			c.setSharedVariable("localName", new IndividualLocalNameMethod());
			c.setSharedVariable("placeholderImageUrl",
					new IndividualPlaceholderImageUrlMethod());
			c.setSharedVariable("i18n", new I18nMethodModel());
		}

		private Map<String, Object> getRevisionInfo(ServletContext ctx) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("label", RevisionInfoBean.getBean(ctx).getReleaseLabel());
			map.put("moreInfoUrl", UrlBuilder.getUrl("/revisionInfo"));
			return map;
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) {
			instance = null;
		}
	}
}
