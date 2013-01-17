/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateProcessingHelper {
    
    private static final Log log = LogFactory.getLog(TemplateProcessingHelper.class);
    
    private Configuration config = null;
    private HttpServletRequest request = null;
    private ServletContext context = null;
    
    public TemplateProcessingHelper(HttpServletRequest request, ServletContext context) {
        this.config = FreemarkerConfigurationLoader.getConfig(new VitroRequest(request));
        this.request = request;
        this.context = context;
    }
    
    public StringWriter processTemplate(String templateName, Map<String, Object> map) 
        throws TemplateProcessingException {
        Template template = getTemplate(templateName);
        StringWriter sw = new StringWriter();        
        processTemplate(template, map, sw);
        return sw;
    }
    
    private void processTemplate(Template template, Map<String, Object> map, Writer writer)
        throws TemplateProcessingException {
        
        try {
            Environment env = template.createProcessingEnvironment(map, writer);
            // Add request and servlet context as custom attributes of the environment, so they
            // can be used in directives.
            env.setCustomAttribute("request", request);
            env.setCustomAttribute("context", context);
            
            // Set the Locale from the request into the environment, so date builtins will be
            // Locale-dependent
            env.setLocale(request.getLocale());
            
            // Define a setup template to be included by every page template
            String templateType = (String) map.get("templateType");
            if (FreemarkerHttpServlet.PAGE_TEMPLATE_TYPE.equals(templateType)) {
                env.include(getTemplate("pageSetup.ftl"));
            }
            
            // Apply any data-getters that are associated with this template.
            FreemarkerConfiguration.retrieveAndRunDataGetters(env, template.getName());
            
            // Now process it.
            env.process();
        } catch (TemplateException e) {
            throw new TemplateProcessingException("TemplateException creating processing environment", e);
        } catch (IOException e) {
            throw new TemplateProcessingException("IOException creating processing environment", e);            
        }        
    }

    private Template getTemplate(String templateName) throws TemplateProcessingException {
        Template template = null;
        try {
            template = config.getTemplate(templateName);
        } catch (IOException e) {
            String msg;
            if (e instanceof freemarker.core.ParseException) {
                msg = "Syntax error in template " + templateName;
            } else if (e instanceof java.io.FileNotFoundException) {
                msg = "Cannot find template " + templateName;                  
            } else {
                msg = "IOException getting template " + templateName;
            }
            throw new TemplateProcessingException(msg, e);
        }  
        return template;
    }

    @SuppressWarnings("serial")
    public class TemplateProcessingException extends Exception {

        public TemplateProcessingException(String message) {
            super(message);
        } 
        
        public TemplateProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
