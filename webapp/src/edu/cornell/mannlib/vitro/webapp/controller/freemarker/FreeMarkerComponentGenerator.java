/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import freemarker.template.Configuration;

/**
 * TEMPORARY for transition from JSP to FreeMarker. Once transition
 * is complete and no more pages are generated in JSP, this can be removed.
 * 
 * @author rjy7
 *
 */
public class FreeMarkerComponentGenerator extends FreeMarkerHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(FreeMarkerHttpServlet.class.getName());
    
    private static ServletContext context = null;
    
    FreeMarkerComponentGenerator(HttpServletRequest request) {
        VitroRequest vreq = new VitroRequest(request);
        Configuration config = getConfig(vreq);

        // root is the map used to create the page shell - header, footer, menus, etc.
        Map<String, Object> root = getSharedVariables(vreq); 
        setUpRoot(vreq, root);  
        
        request.setAttribute("ftl_identity", get("identity", root, config));
        request.setAttribute("ftl_menu", get("menu", root, config));
        request.setAttribute("ftl_search", get("search", root, config));
        request.setAttribute("ftl_footer", get("footer", root, config));
        request.setAttribute("ftl_googleAnalytics", get("googleAnalytics", root, config));
    }

    private String get(String templateName, Map<String, Object> root, Configuration config) {
        templateName += ".ftl";
        return mergeToTemplate(templateName, root, config).toString();
    }
    
    // RY We need the servlet context in getConfig(). For some reason using the method inherited from
    // GenericServlet bombs.
    public ServletContext getServletContext() {
        return context;
    }
    
    protected static void setServletContext(ServletContext sc) {
        context = sc;
    }
  


}
