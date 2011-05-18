/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.identifier.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ArrayIdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.Identifier;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.SelfEditingConfiguration;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/**
 * Create Identifiers that are recognized by the common policy family.
 */
public class CommonIdentifierBundleFactory implements IdentifierBundleFactory {
	private static final Log log = LogFactory
			.getLog(CommonIdentifierBundleFactory.class);

	private final ServletContext context;

	public CommonIdentifierBundleFactory(ServletContext context) {
		this.context = context;
	}

	@Override
	public IdentifierBundle getIdentifierBundle(ServletRequest request,
			HttpSession session, ServletContext unusedContext) {

		// If this is not an HttpServletRequest, we might as well fail now.
		HttpServletRequest req = (HttpServletRequest) request;

		ArrayIdentifierBundle bundle = new ArrayIdentifierBundle();

		bundle.addAll(createUserIdentifiers(req));
		bundle.addAll(createRoleLevelIdentifiers(req));
		bundle.addAll(createBlacklistOrAssociatedIndividualIdentifiers(req));

		return bundle;
	}

	/**
	 * If the user is logged in, create an identifier that shows his URI.
	 */
	private Collection<? extends Identifier> createUserIdentifiers(
			HttpServletRequest req) {
		LoginStatusBean bean = LoginStatusBean.getBean(req);
		if (bean.isLoggedIn()) {
			return Collections.singleton(new IsUser(bean.getUserURI()));
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Create an identifier that shows the role level of the current user, or
	 * PUBLIC if the user is not logged in.
	 */
	private Collection<? extends Identifier> createRoleLevelIdentifiers(
			HttpServletRequest req) {
		RoleLevel roleLevel = RoleLevel.getRoleFromLoginStatus(req);
		return Collections.singleton(new HasRoleLevel(roleLevel));
	}

	/**
	 * Find all of the individuals that are associated with the current user,
	 * and create either an IsBlacklisted or HasAssociatedIndividual for each
	 * one.
	 */
	private Collection<? extends Identifier> createBlacklistOrAssociatedIndividualIdentifiers(
			HttpServletRequest req) {
		Collection<Identifier> ids = new ArrayList<Identifier>();

		for (Individual ind : getAssociatedIndividuals(req)) {
			// If they are blacklisted, this factory will return an identifier
			Identifier id = IsBlacklisted.getInstance(ind, context);
			if (id != null) {
				ids.add(id);
			} else {
				ids.add(new HasAssociatedIndividual(ind.getURI()));
			}
		}

		return ids;
	}

	private Collection<Individual> getAssociatedIndividuals(
			HttpServletRequest req) {
		Collection<Individual> individuals = new ArrayList<Individual>();

		LoginStatusBean bean = LoginStatusBean.getBean(req);
		String username = bean.getUsername();

		if (!bean.isLoggedIn()) {
			log.debug("No Associated Individuals: not logged in.");
			return individuals;
		}

		if (StringUtils.isEmpty(username)) {
			log.debug("No Associated Individuals: username is empty.");
			return individuals;
		}

		WebappDaoFactory wdf = (WebappDaoFactory) context
				.getAttribute("webappDaoFactory");
		if (wdf == null) {
			log.error("Could not get a WebappDaoFactory from the ServletContext");
			return individuals;
		}

		IndividualDao indDao = wdf.getIndividualDao();

		SelfEditingConfiguration sec = SelfEditingConfiguration.getBean(req);
		String uri = sec.getIndividualUriFromUsername(indDao, username);
		if (uri == null) {
			log.debug("Could not find an Individual with a netId of "
					+ username);
			return individuals;
		}

		Individual ind = indDao.getIndividualByURI(uri);
		if (ind == null) {
			log.warn("Found a URI for the netId " + username
					+ " but could not build Individual");
			return individuals;
		}
		log.debug("Found an Individual for netId " + username + " URI: " + uri);

		individuals.add(ind);
		return individuals;
	}
}
