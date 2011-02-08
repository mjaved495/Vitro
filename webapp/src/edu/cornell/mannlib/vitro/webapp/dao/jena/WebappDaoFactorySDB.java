/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;

import org.apache.commons.dbcp.BasicDataSource;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;

import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;

public class WebappDaoFactorySDB extends WebappDaoFactoryJena {
	
    private SDBDatasetMode datasetMode = SDBDatasetMode.ASSERTIONS_AND_INFERENCES;
    
	/**
	 * For use when any database connection associated with the Dataset
	 * is managed externally
	 */
	public WebappDaoFactorySDB(OntModelSelector ontModelSelector, Dataset dataset) {
		super(ontModelSelector);
		this.dwf = new StaticDatasetFactory(dataset);
	}
	
    /**
     * For use when any database connection associated with the Dataset
     * is managed externally
     */
	public WebappDaoFactorySDB(OntModelSelector ontModelSelector, 
	                            Dataset dataset, 
	                            String defaultNamespace, 
	                            HashSet<String> nonuserNamespaces, 
	                            String[] preferredLanguages) {
		super(ontModelSelector, defaultNamespace, nonuserNamespaces, preferredLanguages);
        this.dwf = new StaticDatasetFactory(dataset);
	}
	
    /**
     * For use when any Dataset access should get a temporary DB connection
     * from a pool
     */
    public WebappDaoFactorySDB(OntModelSelector ontModelSelector, 
                                BasicDataSource bds,
                                StoreDesc storeDesc,
                                String defaultNamespace, 
                                HashSet<String> nonuserNamespaces, 
                                String[] preferredLanguages) {
        super(ontModelSelector, defaultNamespace, nonuserNamespaces, preferredLanguages);
        this.dwf = new ReconnectingDatasetFactory(bds, storeDesc);
    }
    
    /**
     * For use when any Dataset access should get a temporary DB connection
     * from a pool, and access to the inference graph needs to be specified.
     */
    public WebappDaoFactorySDB(OntModelSelector ontModelSelector, 
                                BasicDataSource bds,
                                StoreDesc storeDesc,
                                String defaultNamespace, 
                                HashSet<String> nonuserNamespaces, 
                                String[] preferredLanguages,
                                SDBDatasetMode datasetMode) {
        super(ontModelSelector, defaultNamespace, nonuserNamespaces, preferredLanguages);
        this.dwf = new ReconnectingDatasetFactory(bds, storeDesc);
        this.datasetMode = datasetMode;
    }
   
    
    public WebappDaoFactorySDB(WebappDaoFactorySDB base, String userURI) {
        super(base.ontModelSelector);
        this.ontModelSelector = base.ontModelSelector;
        this.defaultNamespace = base.defaultNamespace;
        this.nonuserNamespaces = base.nonuserNamespaces;
        this.preferredLanguages = base.preferredLanguages;
        this.userURI = userURI;
        this.flag2ValueMap = base.flag2ValueMap;
        this.flag2ClassLabelMap = base.flag2ClassLabelMap;
        this.dwf = base.dwf;
    }
	
	@Override
    public IndividualDao getIndividualDao() {
        if (entityWebappDao != null)
            return entityWebappDao;
        else
            return entityWebappDao = new IndividualDaoSDB(
                    dwf, datasetMode, this);
    }
	
	@Override
	public DataPropertyStatementDao getDataPropertyStatementDao() {
		if (dataPropertyStatementDao != null) 
			return dataPropertyStatementDao;
		else
			return dataPropertyStatementDao = new DataPropertyStatementDaoSDB(
			        dwf, datasetMode, this);
	}
	
	@Override
	public ObjectPropertyStatementDao getObjectPropertyStatementDao() {
		if (objectPropertyStatementDao != null) 
			return objectPropertyStatementDao;
		else
			return objectPropertyStatementDao = 
			    new ObjectPropertyStatementDaoSDB(dwf, datasetMode, this);
	}
	
	@Override
	public VClassDao getVClassDao() {
		if (vClassDao != null) 
			return vClassDao;
		else
			return vClassDao = new VClassDaoSDB(dwf, datasetMode, this);
	}
	
	public WebappDaoFactory getUserAwareDaoFactory(String userURI) {
        // TODO: put the user-aware factories in a hashmap so we don't keep re-creating them
        return new WebappDaoFactorySDB(this, userURI);
    }
	
	public enum SDBDatasetMode {
	    ASSERTIONS_ONLY, INFERENCES_ONLY, ASSERTIONS_AND_INFERENCES
	}
	
	public static String getFilterBlock(String[] graphVars, 
	                                    SDBDatasetMode datasetMode) {
	    StringBuffer filterBlock = new StringBuffer();
	    for (int i = 0; i < graphVars.length; i++) {
	        switch (datasetMode) {
	            case ASSERTIONS_ONLY :  
	                    filterBlock.append("FILTER (")
	                        .append("(!bound(").append(graphVars[i])
	                        .append(")) || (")
	                        .append(graphVars[i])
	                        .append(" != <")
	                        .append(JenaDataSourceSetupBase.JENA_INF_MODEL)
	                        .append("> && ").append(graphVars[i])
	                        .append(" != <")
	                        .append(JenaDataSourceSetupBase.JENA_TBOX_INF_MODEL)
	                        .append(">) ) \n");
	                    break;
	            case INFERENCES_ONLY :  
                    filterBlock.append("FILTER (")
                        .append("(!bound(").append(graphVars[i])
                        .append(")) || (")
                        .append(graphVars[i])
                        .append(" = <")
                        .append(JenaDataSourceSetupBase.JENA_INF_MODEL)
                        .append("> || ").append(graphVars[i])
                        .append(" = <")
                        .append(JenaDataSourceSetupBase.JENA_TBOX_INF_MODEL)
                        .append(">) )\n");
                    break;
	            default:
	                break;
	        }
	    }
	    return filterBlock.toString();
	}
	
	private class ReconnectingDatasetFactory implements DatasetWrapperFactory {
	    
	    private BasicDataSource _bds;
	    private StoreDesc _storeDesc;
	    private Dataset _dataset;
	    private Connection _conn;
	    
	    public ReconnectingDatasetFactory(BasicDataSource bds, StoreDesc storeDesc) {
	        _bds = bds;
	        _storeDesc = storeDesc;
	    }
	    
	    public DatasetWrapper getDatasetWrapper() {
	        try {
	            if ((_dataset != null) && (_conn != null) && (!_conn.isClosed())) {
	                return new DatasetWrapper(_dataset);
	            } else {
	                _conn = _bds.getConnection();
                    SDBConnection conn = new SDBConnection(_conn) ;
                    Store store = SDBFactory.connectStore(conn, _storeDesc);
                    _dataset = SDBFactory.connectDataset(store);
                    return new DatasetWrapper(_dataset);
	            }
            } catch (SQLException sqe) {
                throw new RuntimeException("Unable to connect to database", sqe);
            }
	    }
	    
	}    
	
}
