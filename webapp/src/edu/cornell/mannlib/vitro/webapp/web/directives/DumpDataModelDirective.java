/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.directives;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHelper;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class DumpDataModelDirective implements TemplateDirectiveModel {

    private static final Log log = LogFactory.getLog(DumpDataModelDirective.class);
    
    @SuppressWarnings({ "unchecked" })
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {

        if (params.size() != 0) {
            throw new TemplateModelException(
                "The dumpDataModel directive doesn't allow parameters.");
        }       
        if (loopVars.length != 0) {
            throw new TemplateModelException(
                "The dumpDataModel directive doesn't allow loop variables.");
        }
        if (body != null) {
            throw new TemplateModelException(
                "The dumpDataModel directive doesn't allow nested content.");
        }

        TemplateHashModel dataModel = env.getDataModel();
        Map<String, Object> models = new HashMap<String, Object>();
        List<String> directives = new ArrayList<String>();
          
        Map<String, Object> dm = (Map<String, Object>) DeepUnwrap.permissiveUnwrap(dataModel);
        List<String> varNames = new ArrayList(dm.keySet()); 
        Collections.sort(varNames);
        for (String var : varNames) {
            Object value = dm.get(var);
            if (value instanceof TemplateDirectiveModel) {
                directives.add((String) var);
            } else {
                models.put(var, value);
            }
        }
        
        Configuration config = env.getConfiguration();
        String templateName = "dump-datamodel.ftl";
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("models", models);
        map.put("directives", directives);
        map.put("stylesheets", dataModel.get("stylesheets"));
        map.put("dump", dataModel.get("dump"));
        // Put the current datamodel into the new datamodel so its values can be dumped with the dump directive
        // RY Another way to do this would be to loop through the data model here, merging each variable with
        // the dump-var.ftl template and adding it to the output string.
        map.put("datamodel", dataModel);
        map.put("containingTemplate", env.getTemplate().getName());

        FreemarkerHelper helper = new FreemarkerHelper();
        String output = helper.mergeMapToTemplate(templateName, map, config);      
        Writer out = env.getOut();
        out.write(output);

    }

}
