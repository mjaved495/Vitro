/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class UncollatedObjectPropertyTemplateModel extends ObjectPropertyTemplateModel {

    private static final Log log = LogFactory.getLog(UncollatedObjectPropertyTemplateModel.class);  
    
    private List<ObjectPropertyStatementTemplateModel> statements;
    
    UncollatedObjectPropertyTemplateModel(ObjectProperty op, Individual subject, 
            VitroRequest vreq, EditingPolicyHelper policyHelper, 
            List<ObjectProperty> populatedObjectPropertyList)
        throws InvalidConfigurationException {
        
        super(op, subject, vreq, policyHelper);
        statements = new ArrayList<ObjectPropertyStatementTemplateModel>();
        
        if (populatedObjectPropertyList.contains(op)) {
            log.debug("Getting data for populated object property " + getUri());
            /* Get the data */
            WebappDaoFactory wdf = vreq.getWebappDaoFactory();
            ObjectPropertyStatementDao opDao = wdf.getObjectPropertyStatementDao();
            String subjectUri = subject.getURI();
            String propertyUri = op.getURI();
            List<Map<String, String>> statementData = 
                opDao.getObjectPropertyStatementsForIndividualByProperty(subjectUri, propertyUri, getSelectQuery(), getConstructQueries());
            
            /* Apply postprocessing */
            postprocess(statementData, wdf);
            
            /* Put into data structure to send to template */            
            String objectKey = getObjectKey();
            for (Map<String, String> map : statementData) {
                statements.add(new ObjectPropertyStatementTemplateModel(subjectUri, 
                        propertyUri, objectKey, map, policyHelper));
            }
            
            postprocessStatementList(statements);
        } else {
            log.debug("Object property " + getUri() + " is unpopulated.");
        }
    }
    
    /* Access methods for templates */

    public List<ObjectPropertyStatementTemplateModel> getStatements() {
        return statements;
    }
    
    @Override
    public boolean isCollatedBySubclass() {
        return false;
    }
}
