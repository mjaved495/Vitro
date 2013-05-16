/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import static edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary.DISPLAY_ONT_MODEL;
import static edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactorySDB.SDBDatasetMode.ASSERTIONS_ONLY;
import static edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactorySDB.SDBDatasetMode.INFERENCES_ONLY;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.ResourceUtils;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactoryConfig;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelSynchronizer;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelectorImpl;
import edu.cornell.mannlib.vitro.webapp.dao.jena.RDFServiceDataset;
import edu.cornell.mannlib.vitro.webapp.dao.jena.RDFServiceModelMaker;
import edu.cornell.mannlib.vitro.webapp.dao.jena.SpecialBulkUpdateHandlerGraph;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VitroJenaModelMaker;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VitroModelSource;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactorySDB;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;
import edu.cornell.mannlib.vitro.webapp.utils.jena.InitialJenaModelUtils;

/**
 * Primarily sets up webapp DAO factories.
 */
public class WebappDaoSetup extends JenaDataSourceSetupBase 
        implements javax.servlet.ServletContextListener {
    
    private static final Log log = LogFactory.getLog(WebappDaoSetup.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        StartupStatus ss = StartupStatus.getBean(ctx);
        
        long begin = System.currentTimeMillis();
        setUpJenaDataSource(ctx);
        ss.info(this, secondsSince(begin) + " seconds to set up models and DAO factories");  
    } 

    private void setUpJenaDataSource(ServletContext ctx) {
    	RDFServiceFactory rdfServiceFactory = RDFServiceUtils.getRDFServiceFactory(ctx);
    	RDFService rdfService = rdfServiceFactory.getRDFService();
    	Dataset dataset = new RDFServiceDataset(rdfService);
    	setStartupDataset(dataset, ctx);
    	
    	OntModel applicationMetadataModel = createdMemoryMappedModel(dataset, JENA_APPLICATION_METADATA_MODEL, "application metadata model");
		if (applicationMetadataModel.size()== 0) {
			JenaDataSourceSetupBase.thisIsFirstStartup();
		}

    	OntModel userAccountsModel = ontModelFromContextAttribute(ctx, "userAccountsOntModel");     
        OntModel displayModel = ontModelFromContextAttribute(ctx,DISPLAY_ONT_MODEL);
        OntModel baseABoxModel = createNamedModelFromDataset(dataset, JENA_DB_MODEL);
        OntModel inferenceABoxModel = createNamedModelFromDataset(dataset, JENA_INF_MODEL);
        OntModel baseTBoxModel = createdMemoryMappedModel(dataset, JENA_TBOX_ASSERTIONS_MODEL, "tbox assertions");
        OntModel inferenceTBoxModel = createdMemoryMappedModel(dataset, JENA_TBOX_INF_MODEL, "tbox inferences");
        OntModel unionABoxModel = createCombinedBulkUpdatingModel(baseABoxModel, inferenceABoxModel);
        OntModel unionTBoxModel = createCombinedBulkUpdatingModel(baseTBoxModel, inferenceTBoxModel);

        if (isFirstStartup()) {
        	loadInitialApplicationMetadataModel(applicationMetadataModel, ctx);
        	loadDataFromFilesystem(baseABoxModel, baseTBoxModel, applicationMetadataModel, ctx);
        }
        
        log.info("Setting up union models");
        OntModel baseFullModel = createCombinedBulkUpdatingModel(baseABoxModel, baseTBoxModel);
        OntModel inferenceFullModel = createCombinedModel(inferenceABoxModel, inferenceTBoxModel);
        OntModel unionFullModel = ModelFactory.createOntologyModel(DB_ONT_MODEL_SPEC, dataset.getDefaultModel());
        
        ModelContext.setBaseOntModel(baseFullModel, ctx);
        ModelContext.setInferenceOntModel(inferenceFullModel, ctx);
        
        checkForNamespaceMismatch( applicationMetadataModel, ctx );
        
        OntModelSelectorImpl baseOms = new OntModelSelectorImpl();     
        baseOms.setApplicationMetadataModel(applicationMetadataModel);
        baseOms.setUserAccountsModel(userAccountsModel);
        baseOms.setDisplayModel(displayModel);
		baseOms.setABoxModel(baseABoxModel);
		baseOms.setTBoxModel(baseTBoxModel);
		baseOms.setFullModel(baseFullModel);

		OntModelSelectorImpl inferenceOms = new OntModelSelectorImpl();       
		inferenceOms.setApplicationMetadataModel(applicationMetadataModel);
		inferenceOms.setUserAccountsModel(userAccountsModel);
		inferenceOms.setDisplayModel(displayModel);
		inferenceOms.setABoxModel(inferenceABoxModel);
		inferenceOms.setTBoxModel(inferenceTBoxModel);
		inferenceOms.setFullModel(inferenceFullModel);

		OntModelSelectorImpl unionOms = new OntModelSelectorImpl();
		unionOms.setApplicationMetadataModel(applicationMetadataModel);
		unionOms.setUserAccountsModel(userAccountsModel);       
		unionOms.setDisplayModel(displayModel);
		unionOms.setABoxModel(unionABoxModel);
		unionOms.setTBoxModel(unionTBoxModel);
		unionOms.setFullModel(unionFullModel);
                  
		ModelContext.setOntModelSelector(unionOms, ctx);
		ModelContext.setUnionOntModelSelector(unionOms, ctx); // assertions and inferences
		ModelContext.setBaseOntModelSelector(baseOms, ctx); // assertions
		ModelContext.setInferenceOntModelSelector(inferenceOms, ctx); // inferences       

		
        

		log.info("Setting up DAO factories");
        
        
        ctx.setAttribute("jenaOntModel", unionFullModel);  
        
        WebappDaoFactoryConfig config = new WebappDaoFactoryConfig();
        config.setDefaultNamespace(getDefaultNamespace(ctx));
        
        WebappDaoFactory baseWadf = new WebappDaoFactorySDB(rdfService, baseOms, config, ASSERTIONS_ONLY);
        ctx.setAttribute("assertionsWebappDaoFactory",baseWadf);
        
        WebappDaoFactory infWadf = new WebappDaoFactorySDB(rdfService, inferenceOms, config, INFERENCES_ONLY);
        ctx.setAttribute("deductionsWebappDaoFactory", infWadf);
        
        WebappDaoFactory wadf = new WebappDaoFactorySDB(rdfService, unionOms, config);
        ctx.setAttribute("webappDaoFactory",wadf);

        log.info("Model makers set up");
        
        ctx.setAttribute("defaultNamespace", getDefaultNamespace(ctx));
        
        makeModelMakerFromConnectionProperties(TripleStoreType.RDB, ctx);
        VitroJenaModelMaker vjmm = getVitroJenaModelMaker();
        setVitroJenaModelMaker(vjmm, ctx);
        makeModelMakerFromConnectionProperties(TripleStoreType.SDB, ctx);
        RDFServiceModelMaker vsmm = new RDFServiceModelMaker(rdfServiceFactory);
        setVitroJenaSDBModelMaker(vsmm, ctx);
                
        //bdc34: I have no reason for vsmm vs vjmm.  
        //I don't know what are the implications of this choice.        
        setVitroModelSource( new VitroModelSource(vsmm,ctx), ctx);
        
    }

	private OntModel createNamedModelFromDataset(Dataset dataset, String name) {
    	return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getNamedModel(name));
    }
    
	private OntModel createdMemoryMappedModel(Dataset dataset, String name, String label) {
		try {
			Model dbModel = dataset.getNamedModel(name);
			OntModel memoryModel = ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC);
			
			if (dbModel != null) {
			    long begin = System.currentTimeMillis();
				log.info("Copying cached " + label + " into memory");
			    memoryModel.add(dbModel);
			    log.info(secondsSince(begin) + " seconds to load " + label);
			    memoryModel.getBaseModel().register(new ModelSynchronizer(dbModel));
			}
			return memoryModel;
        } catch (Throwable e) {
            throw new RuntimeException("Unable to load " + label + " from DB", e);
        }
	}

	private OntModel createCombinedModel(OntModel oneModel, OntModel otherModel) {
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, 
        		ModelFactory.createUnion(oneModel, otherModel));
	}

	private OntModel createCombinedBulkUpdatingModel(OntModel baseModel,
			OntModel otherModel) {
		BulkUpdateHandler bulkUpdateHandler = baseModel.getGraph().getBulkUpdateHandler();
		Graph unionGraph = ModelFactory.createUnion(baseModel, otherModel).getGraph();
		Model unionModel = ModelFactory.createModelForGraph(
				new SpecialBulkUpdateHandlerGraph(unionGraph, bulkUpdateHandler));
		return ModelFactory.createOntologyModel(MEM_ONT_MODEL_SPEC, unionModel);
	}

	private void loadInitialApplicationMetadataModel(OntModel applicationMetadataModel,
			ServletContext ctx) {
		try {
			applicationMetadataModel.add(
					InitialJenaModelUtils.loadInitialModel(ctx, getDefaultNamespace(ctx)));
		} catch (Throwable e) {
			throw new RuntimeException("Unable to load application metadata model cache from DB", e);
		}
	}


    /**
     * If we find a "portal1" portal (and we should), its URI should use the
     * default namespace.
     */
    private void checkForNamespaceMismatch(OntModel model, ServletContext ctx) {
        String expectedNamespace = getDefaultNamespace(ctx);

        List<Resource> portals = getPortal1s(model);

        if(!portals.isEmpty() && noPortalForNamespace(
                portals, expectedNamespace)) {
            // There really should be only one portal 1, but if there happen to 
            // be multiple, just arbitrarily pick the first in the list.
            Resource portal = portals.get(0); 
            String oldNamespace = portal.getNameSpace();
            renamePortal(portal, expectedNamespace, model);
            StartupStatus ss = StartupStatus.getBean(ctx);
            ss.warning(this, "\nThe default namespace has been changed \n" +  
                             "from " + oldNamespace + 
                             "\nto " + expectedNamespace + ".\n" +
                             "The application will function normally, but " +
                             "any individuals in the \n" + oldNamespace + " " + 
                             "namespace will need to have their URIs \n" +
                             "changed in order to be served as linked data. " +
                             "You can use the Ingest Tools \nto change the " +
                             "URIs for a batch of resources.");
        }
    }
    
    private List<Resource> getPortal1s(Model model) {
        List<Resource> portals = new ArrayList<Resource>();
        try {
            model.enterCriticalSection(Lock.READ);
            ResIterator portalIt = model.listResourcesWithProperty(
                    RDF.type, PORTAL);
            while (portalIt.hasNext()) {
                Resource portal = portalIt.nextResource();
                if ("portal1".equals(portal.getLocalName())) {
                    portals.add(portal);
                }
            }
        } finally {
            model.leaveCriticalSection();
        }
        return portals;
    }
    
    private boolean noPortalForNamespace(List<Resource> portals, 
                                         String expectedNamespace) {
        for (Resource portal : portals) {
            if(expectedNamespace.equals(portal.getNameSpace())) {
                return false;
            }
        }
        return true;
    }

    private void renamePortal(Resource portal, String namespace, Model model) {
        model.enterCriticalSection(Lock.WRITE);
        try {
            ResourceUtils.renameResource(
                    portal, namespace + portal.getLocalName());
        } finally {
            model.leaveCriticalSection();
        }
    }
    
    
    /* ===================================================================== */

	private long secondsSince(long startTime) {
		return (System.currentTimeMillis() - startTime) / 1000;
	}

	private void loadDataFromFilesystem(OntModel baseABoxModel, OntModel baseTBoxModel, OntModel applicationMetadataModel, 
			ServletContext ctx) {
		Long startTime = System.currentTimeMillis();
		log.info("Initializing models from RDF files");    
		
		readOntologyFilesInPathSet(USER_ABOX_PATH, ctx, baseABoxModel);
		readOntologyFilesInPathSet(USER_TBOX_PATH, ctx, baseTBoxModel);
		readOntologyFilesInPathSet(USER_APPMETA_PATH, ctx, applicationMetadataModel);
		
		log.debug(((System.currentTimeMillis() - startTime) / 1000)
				+ " seconds to read RDF files ");
	}
	

    /* ===================================================================== */
    
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do.
    }    
    
  

 
 }

