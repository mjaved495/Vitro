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

public class WebappDaoFactorySDB extends WebappDaoFactoryJena {

	private DatasetWrapperFactory dwf;
	
	/**
	 * For use when any database connection associated with the Dataset
	 * is managed externally
	 * @param ontModelSelector
	 * @param dataset
	 */
	public WebappDaoFactorySDB(OntModelSelector ontModelSelector, Dataset dataset) {
		super(ontModelSelector);
		this.dwf = new StaticDatasetFactory(dataset);
	}
	
    /**
     * For use when any database connection associated with the Dataset
     * is managed externally
     * @param ontModelSelector
     * @param dataset
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
     * @param ontModelSelector
     * @param dataset
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
	
	@Override
    public IndividualDao getIndividualDao() {
        if (entityWebappDao != null)
            return entityWebappDao;
        else
            return entityWebappDao = new IndividualDaoSDB(dwf, this);
    }
	
	@Override
	public DataPropertyStatementDao getDataPropertyStatementDao() {
		if (dataPropertyStatementDao != null) 
			return dataPropertyStatementDao;
		else
			return dataPropertyStatementDao = new DataPropertyStatementDaoSDB(dwf, this);
	}
	
	@Override
	public ObjectPropertyStatementDao getObjectPropertyStatementDao() {
		if (objectPropertyStatementDao != null) 
			return objectPropertyStatementDao;
		else
			return objectPropertyStatementDao = new ObjectPropertyStatementDaoSDB(dwf, this);
	}
	
	@Override
	public VClassDao getVClassDao() {
		if (vClassDao != null) 
			return vClassDao;
		else
			return vClassDao = new VClassDaoSDB(dwf, this);
	}
	
	private class StaticDatasetFactory implements DatasetWrapperFactory {
	 
	    private Dataset _dataset;
	    
	    public StaticDatasetFactory (Dataset dataset) {
	        _dataset = dataset;
	    }
	    
	    public DatasetWrapper getDatasetWrapper() {
	        return new DatasetWrapper(_dataset);
	    }
	    
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
