package edu.cornell.mannlib.vitro.webapp.web.methods;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;

public abstract class BaseTemplateMethodModel implements TemplateMethodModel {

    private static final Log log = LogFactory.getLog(BaseTemplateMethodModel.class);
    
    public String help(String name, Environment env) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", name);
        
        return mergeToHelpTemplate(map, env);
    }
    
    protected String mergeToHelpTemplate(Map<String, Object> map, Environment env) {
        return processTemplateToString("help-method.ftl", map, env);        
    }
    
    public static String processTemplateToString(String templateName, Map<String, Object> map, Environment env) {
        Template template = getTemplate(templateName, env);
        StringWriter sw = new StringWriter();
        try {
            template.process(map, sw);
        } catch (TemplateException e) {
            log.error("Template Exception creating processing environment", e);
        } catch (IOException e) {
            log.error("IOException creating processing environment", e);
        }
        return sw.toString();        
    }
    
    private static Template getTemplate(String templateName, Environment env) {
        Template template = null;
        try {
            template = env.getConfiguration().getTemplate(templateName);
        } catch (IOException e) {
            // RY Should probably throw this error instead.
            log.error("Cannot get template " + templateName, e);
        }  
        return template;        
    }

}
