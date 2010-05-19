/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.utils.StringUtils;

public class TermsOfUseController extends FreeMarkerHttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(TermsOfUseController.class.getName());
    
    protected String getTitle() {
        return appName + " Terms of Use";
    }
    
    protected String getBody() {

        Map<String, Object> body = new HashMap<String, Object>();
        
        String rootBreadCrumbAnchor = portal.getRootBreadCrumbAnchor();
        String websiteName = StringUtils.isEmpty(rootBreadCrumbAnchor) ? appName : rootBreadCrumbAnchor;
 
        body.put("websiteName", websiteName);
        body.put("copyrightAnchor", portal.getCopyrightAnchor());
        
        String bodyTemplate = "termsOfUse.ftl";             
        return mergeBodyToTemplate(bodyTemplate, body);
    }
}