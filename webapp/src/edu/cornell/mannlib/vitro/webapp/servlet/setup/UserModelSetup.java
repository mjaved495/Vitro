/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelSynchronizer;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;


/** 
 * Setup the user account model.  If it does not exist in the 
 * database, create and populate it. 
 */
public class UserModelSetup extends JenaDataSourceSetupBase 
                                           implements ServletContextListener {
    
    private static final Log log = LogFactory.getLog(
            UserModelSetup.class.getName());

    @Override   
    public void contextInitialized(ServletContextEvent sce) {       
        ServletContext ctx = sce.getServletContext();
        StartupStatus ss = StartupStatus.getBean(ctx);                
    
        BasicDataSource bds = getApplicationDataSource(ctx);
        if( bds == null ){
            ss.fatal(this, "A DataSource must be setup before ModelSetup "+
                    "is run. Make sure that JenaPersistentDataSourceSetup runs before "+
                    "ModelSetup.");
            return;
        }
        
        setupUserAccountModel(bds, ctx, ss);
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // Does nothing.        
    }

    private void setupUserAccountModel (BasicDataSource bds, ServletContext ctx ,StartupStatus ss){             
        try {
            Model userAccountsDbModel = makeDBModel(bds,
                    JENA_USER_ACCOUNTS_MODEL, DB_ONT_MODEL_SPEC, ctx);
            OntModel userAccountsModel =
                ModelFactory.createOntologyModel( MEM_ONT_MODEL_SPEC);
    
            userAccountsModel.add(userAccountsDbModel);
            userAccountsModel.getBaseModel().register(
                    new ModelSynchronizer(userAccountsDbModel));
            ctx.setAttribute("userAccountsOntModel", userAccountsModel);
                        
        } catch (Throwable t) {
            log.error("Unable to load user accounts model from DB", t);
            ss.fatal(this, "Unable to load user accounts model from DB", t);
        }        
    }

}
