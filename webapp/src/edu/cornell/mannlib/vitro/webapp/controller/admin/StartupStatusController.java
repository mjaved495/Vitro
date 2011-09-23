/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.admin;

import java.util.HashMap;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.SeeStartupStatus;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Display the startup status page.
 */
public class StartupStatusController extends FreemarkerHttpServlet {

	@Override
	protected Actions requiredActions(VitroRequest vreq) {
		return new Actions(new SeeStartupStatus());
	}

	@Override
	protected ResponseValues processRequest(VitroRequest vreq) {
		Map<String, Object> body = new HashMap<String, Object>();

		body.put("title", "Startup Status");
		body.put("status", StartupStatus.getBean(getServletContext()));

		return new TemplateResponseValues("startupStatus-display.ftl", body);
	}

}
