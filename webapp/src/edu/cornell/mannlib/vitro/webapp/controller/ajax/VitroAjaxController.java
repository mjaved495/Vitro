/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerConfigurationLoader;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.TemplateProcessingHelper;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.TemplateProcessingHelper.TemplateProcessingException;
import edu.cornell.mannlib.vitro.webapp.search.controller.AutocompleteController;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * A base class for servlets that handle AJAX requests.
 */
public abstract class VitroAjaxController extends HttpServlet {
    
    private static final Log log = LogFactory.getLog(VitroAjaxController.class);
    
	/**
	 * Sub-classes must implement this method to verify that the user is
	 * authorized to execute this request.
	 */
	protected abstract boolean testIsAuthorized(HttpServletRequest request);

	/**
	 * Sub-classes must implement this method to handle both GET and POST
	 * requests.
	 */
	protected abstract void doRequest(VitroRequest vreq,
			HttpServletResponse resp) throws ServletException, IOException;

	/**
	 * Sub-classes should not override this. Instead, implement doRequest().
	 */
	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		VitroRequest vreq = new VitroRequest(req);
		if (testIsAuthorized(vreq)) {
			doRequest(vreq, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized");
		}
	}

	/**
	 * Sub-classes should not override this. Instead, implement doRequest().
	 */
	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	/** 
	 * Returns the current Freemarker Configuration so the controller can process
	 * its data through a template.
	 */
	protected final Configuration getFreemarkerConfiguration(VitroRequest vreq) {
	    ServletContext context = getServletContext();
        FreemarkerConfigurationLoader loader = 
            FreemarkerConfigurationLoader.getFreemarkerConfigurationLoader(context);
        return loader.getConfig(vreq);	    
	}
	
	/**
	 * Process data through a Freemarker template and output the result.
	 */
	protected void writeTemplate(String templateName, Map<String, Object> map, 
	        Configuration config, HttpServletRequest request, HttpServletResponse response) {
        Template template = null;
        try {
            template = config.getTemplate(templateName);
            PrintWriter out = response.getWriter();
            template.process(map, out);
        } catch (Exception e) {
            log.error(e, e);
        } 
	}
    
    protected void doError(HttpServletResponse response, String errorMsg, int httpstatus){
        response.setStatus(httpstatus);
        try {
            response.getWriter().write(errorMsg);
        } catch (IOException e) {
            log.debug("IO exception during output",e );
        }
    }
}
