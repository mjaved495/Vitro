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

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateProcessingHelper {
    
    private static final Log log = LogFactory.getLog(TemplateProcessingHelper.class);
    
    private Configuration config = null;
    private HttpServletRequest request = null;
    private ServletContext context = null;
    //private Map<String, Object> templateDataModel = null;
    
    TemplateProcessingHelper(Configuration config, HttpServletRequest request, ServletContext context) {
        this.config = config;
        this.request = request;
        this.context = context;
        //this.templateDataModel = new HashMap<String, Object>();
    }
    
    protected StringWriter processTemplate(String templateName, Map<String, Object> map) {
        Template template = getTemplate(templateName);
        StringWriter sw = new StringWriter();        
        processTemplate(template, map, sw);
        return sw;
    }
    
    protected StringWriter processTemplate(ResponseValues values) {
        if (values == null) {
            return null;
        }
        return processTemplate(values.getTemplateName(), values.getMap());
    }

    private void processTemplate(Template template, Map<String, Object> map, Writer writer) {
        
        try {
            Environment env = template.createProcessingEnvironment(map, writer);
            // Add request and servlet context as custom attributes of the environment, so they
            // can be used in directives.
            env.setCustomAttribute("request", request);
            env.setCustomAttribute("context", context);
            env.process();
        } catch (TemplateException e) {
            log.error("Template Exception creating processing environment", e);
        } catch (IOException e) {
            log.error("IOException creating processing environment", e);
        }        
    }

    // In fact, we can put StringWriter objects directly into the data model, so perhaps we should eliminate the processTemplateToString() methods.
    protected String processTemplateToString(String templateName, Map<String, Object> map) {
        return processTemplate(templateName, map).toString();
    }

    protected String processTemplateToString(ResponseValues values) {
        return processTemplate(values).toString();
    }
    
    private Template getTemplate(String templateName) {
        Template template = null;
        try {
            template = config.getTemplate(templateName);
        } catch (IOException e) {
            // RY Should probably throw this error instead.
            log.error("Cannot get template " + templateName, e);
        }  
        return template;
    }
    
}
