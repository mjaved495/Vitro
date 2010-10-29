/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.edit.Authenticate;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.login.LoginProcessBean.State;
import freemarker.template.Configuration;

/**
 * A temporary means of displaying the Login templates within the SiteAdmin
 * form.
 * 
 * This class contains stuff that I swiped from {@link Authenticate}. The base
 * class, {@link LoginTemplateHelperBase}, contains stuff that I swiped from
 * {@link FreemarkerHttpServlet}.
 */
public class LoginTemplateHelper extends LoginTemplateHelperBase {
	private static final Log log = LogFactory.getLog(LoginTemplateHelper.class);

	/** If they are logging in, show them this form. */
	public static final String TEMPLATE_LOGIN = "login-form.ftl";

	/** If they are changing their password on first login, show them this form. */
	public static final String TEMPLATE_FORCE_PASSWORD_CHANGE = "login-forcedPasswordChange.ftl";

	/** Show error message */
	public static final String TEMPLATE_SERVER_ERROR = Template.ERROR_MESSAGE.toString();

	public static final String BODY_LOGIN_NAME = "loginName";
	public static final String BODY_FORM_ACTION = "formAction";
	public static final String BODY_INFO_MESSAGE = "infoMessage";
	public static final String BODY_ERROR_MESSAGE = "errorMessage";
	public static final String BODY_CANCEL_URL = "cancelUrl";

	public LoginTemplateHelper(HttpServletRequest req) {
		super(req);
	}

	/** Version for JSP page */
	public String showLoginPage(HttpServletRequest request) {
		VitroRequest vreq = new VitroRequest(request);
		try {
			State state = getCurrentLoginState(vreq);
			log.debug("State on exit: " + state);

			switch (state) {
			case LOGGED_IN:
				return "";
			case FORCED_PASSWORD_CHANGE:
				return doTemplate(vreq, showPasswordChangeScreen(vreq));
			default:
				return doTemplate(vreq, showLoginScreen(vreq));
			}
		} catch (Exception e) {
			log.error(e);
			return doTemplate(vreq, showError(e));
		}
	}

	/** Version for Freemarker page */
	public TemplateResponseValues showLoginPanel(VitroRequest vreq) {
		try {

			State state = getCurrentLoginState(vreq);
			log.debug("State on exit: " + state);

			switch (state) {
			// RY Why does this case exist? We don't call this method if a user is logged in.
			case LOGGED_IN:
				return null;
			case FORCED_PASSWORD_CHANGE:
				// return doTemplate(vreq, showPasswordChangeScreen(vreq), body, config);
				return showPasswordChangeScreen(vreq);
			default:
				// return doTemplate(vreq, showLoginScreen(vreq), body, config);
				return showLoginScreen(vreq);
			}
		} catch (Exception e) {
			log.error(e);
			return showError(e);
		}
	}

	/**
	 * User is just starting the login process. Be sure that we have a
	 * {@link LoginProcessBean} with the correct status. Show them the login
	 * screen.
	 */
	private TemplateResponseValues showLoginScreen(VitroRequest vreq)
			throws IOException {
		LoginProcessBean bean = getLoginProcessBean(vreq);
		bean.setState(State.LOGGING_IN);
		log.trace("Going to login screen: " + bean);

		TemplateResponseValues trv = new TemplateResponseValues(TEMPLATE_LOGIN);
		trv.put(BODY_FORM_ACTION, getAuthenticateUrl(vreq));
		trv.put(BODY_LOGIN_NAME, bean.getUsername());

		String infoMessage = bean.getInfoMessage();
		if (!infoMessage.isEmpty()) {
			trv.put(BODY_INFO_MESSAGE, infoMessage);
		}
		String errorMessage = bean.getErrorMessage();
		if (!errorMessage.isEmpty()) {
			trv.put(BODY_ERROR_MESSAGE, errorMessage);
		}

		return trv;
	}

	/**
	 * The user has given the correct password, but now they are required to
	 * change it (unless they cancel out).
	 */
	private TemplateResponseValues showPasswordChangeScreen(VitroRequest vreq) {
		LoginProcessBean bean = getLoginProcessBean(vreq);
		bean.setState(State.FORCED_PASSWORD_CHANGE);
		log.trace("Going to password change screen: " + bean);

		TemplateResponseValues trv = new TemplateResponseValues(
				TEMPLATE_FORCE_PASSWORD_CHANGE);
		trv.put(BODY_FORM_ACTION, getAuthenticateUrl(vreq));
		trv.put(BODY_CANCEL_URL, getCancelUrl(vreq));

		String errorMessage = bean.getErrorMessage();
		if (!errorMessage.isEmpty()) {
			trv.put(BODY_ERROR_MESSAGE, errorMessage);
		}
		return trv;
	}

	private TemplateResponseValues showError(Exception e) {
		TemplateResponseValues trv = new TemplateResponseValues(
				TEMPLATE_SERVER_ERROR);
		trv.put(BODY_ERROR_MESSAGE, "Internal server error:<br /> " + e);
		return trv;
	}

	/**
	 * We processed a response, and want to show a template. Version for JSP
	 * page.
	 */
	private String doTemplate(VitroRequest vreq, TemplateResponseValues values) {
		// Set it up like FreeMarkerHttpServlet.doGet() would do.
		Configuration config = getConfig(vreq);
		Map<String, Object> sharedVariables = getSharedVariables(vreq, new HashMap<String, Object>());
		Map<String, Object> root = new HashMap<String, Object>(sharedVariables);
		Map<String, Object> body = new HashMap<String, Object>(sharedVariables);
		root.putAll(getRootValues(vreq));

		// Add the values that we got, and merge to the template.
		body.putAll(values.getMap());
		return mergeMapToTemplate(values.getTemplateName(), body, config);
	}

	/**
	 * Where are we in the process? Logged in? Not? Somewhere in between?
	 */
	private State getCurrentLoginState(HttpServletRequest request) {
		if (LoginStatusBean.getBean(request).isLoggedIn()) {
			return State.LOGGED_IN;
		} else {
			return getLoginProcessBean(request).getState();
		}
	}

	/**
	 * How is the login process coming along?
	 */
	private LoginProcessBean getLoginProcessBean(HttpServletRequest request) {
		HttpSession session = request.getSession();

		LoginProcessBean bean = (LoginProcessBean) session
				.getAttribute(LoginProcessBean.SESSION_ATTRIBUTE);

		if (bean == null) {
			bean = new LoginProcessBean();
			session.setAttribute(LoginProcessBean.SESSION_ATTRIBUTE, bean);
		}

		return bean;
	}

	/** What's the URL for this servlet? */
	private String getAuthenticateUrl(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String urlParams = "?login=block";
		return contextPath + "/authenticate" + urlParams;
	}

	/** What's the URL for this servlet, with the cancel parameter added? */
	private String getCancelUrl(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String urlParams = "?login=block&cancel=true";
		return contextPath + "/authenticate" + urlParams;
	}
}
