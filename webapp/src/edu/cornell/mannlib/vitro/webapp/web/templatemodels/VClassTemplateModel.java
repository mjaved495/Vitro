/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;

public class VClassTemplateModel extends BaseTemplateModel {
    
    private static final Log log = LogFactory.getLog(VClassTemplateModel.class.getName());
    private static final String PATH = Route.INDIVIDUAL_LIST.path();
    
    private VClass vclass;
    
    public VClassTemplateModel(VClass vclass) {
        this.vclass = vclass;
    }

    public String getName() {
        return vclass.getName();
    }
    
    public String getUrl() {
        return getUrl(PATH, new ParamMap("vclassId", vclass.getURI()));
    }
    
    public int getIndividualCount() {
        return vclass.getEntityCount();
    }

}
