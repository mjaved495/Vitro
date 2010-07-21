/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.JenaBaseDao;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.SimpleOntModelSelector;
import edu.cornell.mannlib.vitro.webapp.ontology.update.OntologyUpdateSettings;
import edu.cornell.mannlib.vitro.webapp.ontology.update.OntologyUpdater;

/**
 * Invokes process to test whether the knowledge base needs any updating
 * to align with ontology changes.
 * @author bjl23
 *
 */
public class UpdateKnowledgeBase implements ServletContextListener {
	
	private final static Log log = LogFactory.getLog(UpdateKnowledgeBase.class);
	
	private final String DATA_DIR = "/WEB-INF/ontologies/update/";
	private final String LOG_DIR = "logs/";
	private final String CHANGED_DATA_DIR = "changedData/";
	private final String ASK_QUERY_FILE = DATA_DIR + "ask.sparql";
	private final String SUCCESS_ASSERTIONS_FILE = DATA_DIR + "success.n3";
	private final String SUCCESS_RDF_FORMAT = "N3";
	private final String DIFF_FILE = DATA_DIR + "diff.tab.txt";
	private final String LOG_FILE = DATA_DIR + LOG_DIR + "knowledgeBaseUpdate.log";
	private final String ERROR_LOG_FILE = DATA_DIR + LOG_DIR + 	"knowledgeBaseUpdate.error.log";
	private final String REMOVED_DATA_FILE = DATA_DIR + CHANGED_DATA_DIR + 	"removedData.n3";
	private final String ADDED_DATA_FILE = DATA_DIR + CHANGED_DATA_DIR + "addedData.n3";
	private final String SPARQL_CONSTRUCT_ADDITIONS_DIR = DATA_DIR + "sparqlConstructs/additions/";
	private final String SPARQL_CONSTRUCT_DELETIONS_DIR = DATA_DIR + "sparqlConstructs/deletions/";
	private final String MISC_REPLACEMENTS_FILE = DATA_DIR + "miscReplacements.rdf";
	private final String OLD_TBOX_MODEL_DIR = DATA_DIR + "oldVersion/";
	private final String NEW_TBOX_MODEL_DIR = "/WEB-INF/submodels/";
	private final String OLD_TBOX_ANNOTATIONS_DIR = DATA_DIR + "oldAnnotations/";
	private final String NEW_TBOX_ANNOTATIONS_DIR = "/WEB-INF/ontologies/user";
	
	public void contextInitialized(ServletContextEvent sce) {
				
		try {

			ServletContext ctx = sce.getServletContext();
			
			OntModelSelector oms = new SimpleOntModelSelector(
					(OntModel) sce.getServletContext().getAttribute(
							JenaBaseDao.ASSERTIONS_ONT_MODEL_ATTRIBUTE_NAME));
			
			OntologyUpdateSettings settings = new OntologyUpdateSettings();
			settings.setAskQueryFile(ctx.getRealPath(ASK_QUERY_FILE));
			settings.setDataDir(ctx.getRealPath(DATA_DIR));
			settings.setSparqlConstructAdditionsDir(ctx.getRealPath(SPARQL_CONSTRUCT_ADDITIONS_DIR));
			settings.setSparqlConstructDeletionsDir(ctx.getRealPath(SPARQL_CONSTRUCT_DELETIONS_DIR));
			settings.setDiffFile(ctx.getRealPath(DIFF_FILE));
			settings.setSuccessAssertionsFile(ctx.getRealPath(SUCCESS_ASSERTIONS_FILE));
			settings.setSuccessRDFFormat(SUCCESS_RDF_FORMAT);
			settings.setLogFile(ctx.getRealPath(LOG_FILE));
			settings.setErrorLogFile(ctx.getRealPath(ERROR_LOG_FILE));
			settings.setAddedDataFile(ctx.getRealPath(ADDED_DATA_FILE));
			settings.setRemovedDataFile(ctx.getRealPath(REMOVED_DATA_FILE));
			WebappDaoFactory wadf = (WebappDaoFactory) ctx.getAttribute("webappDaoFactory");
			settings.setDefaultNamespace(wadf.getDefaultNamespace());
				
			settings.setOntModelSelector(oms);
			OntModel oldTBoxModel = loadModelFromDirectory(ctx.getRealPath(OLD_TBOX_MODEL_DIR));
			settings.setOldTBoxModel(oldTBoxModel);
			OntModel newTBoxModel = loadModelFromDirectory(ctx.getRealPath(NEW_TBOX_MODEL_DIR));
			settings.setNewTBoxModel(newTBoxModel);
			OntModel oldTBoxAnnotationsModel = loadModelFromDirectory(ctx.getRealPath(OLD_TBOX_ANNOTATIONS_DIR));
			settings.setOldTBoxAnnotationsModel(oldTBoxAnnotationsModel);
			OntModel newTBoxAnnotationsModel = loadModelFromDirectory(ctx.getRealPath(NEW_TBOX_ANNOTATIONS_DIR));
			settings.setNewTBoxAnnotationsModel(newTBoxAnnotationsModel);
			
			try {
				
			  OntologyUpdater ontologyUpdater = new OntologyUpdater(settings);
			  
			  try {
				  if (ontologyUpdater.updateRequired()) {
					  ctx.setAttribute("UpdateKnowledgeBase.updateRequired", Boolean.TRUE);
					  doMiscAppMetadataReplacements(ctx.getRealPath(MISC_REPLACEMENTS_FILE), oms);
				  }
			  } catch (Throwable t){
				  log.error("Unable to perform miscellaneous application metadata replacements", t);
			  }
			  
			  ontologyUpdater.update();
				
			} catch (IOException ioe) {
				String errMsg = "IOException updating knowledge base " +
					"for ontology changes: ";
				// Tomcat doesn't always seem to print exceptions thrown from
				// context listeners
				System.out.println(errMsg);
				ioe.printStackTrace();
				throw new RuntimeException(errMsg, ioe);
			}	
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	}	
	
	/**
	 * 
	 * Behavior changed from 1.0
	 * 
	 * Replace any triple X P S in the application metadata model
	 * with X P T where X, P, and T are specified in the input file
	 * @param filename containing replacement values
	 * @param OntModelSelector oms
	 */
	private void doMiscAppMetadataReplacements(String filename, OntModelSelector oms) {
		try {
		    Model replacementValues = ModelFactory.createDefaultModel();
		    OntModel applicationMetadataModel = oms.getApplicationMetadataModel();
		    FileInputStream fis = new FileInputStream(new File(filename));
		    replacementValues.read(fis, null);
		    Model retractions = ModelFactory.createDefaultModel();
		    Model additions = ModelFactory.createDefaultModel();
		    StmtIterator replaceIt = replacementValues.listStatements();
		    while (replaceIt.hasNext()) {
		    	Statement replacement = replaceIt.nextStatement();
		    	applicationMetadataModel.enterCriticalSection(Lock.WRITE);
		    	try {
		    		StmtIterator stmtIt = 
		    			    applicationMetadataModel.listStatements( 
		    			    		replacement.getSubject(), 
		    			    		replacement.getPredicate(),
		    			    		(RDFNode) null);
		    		while (stmtIt.hasNext()) {
		    			Statement stmt = stmtIt.nextStatement();
		    			retractions.add(stmt);
		    			additions.add(stmt.getSubject(),
		    					replacement.getPredicate(), 
		    					replacement.getObject());
		    		}
		    		applicationMetadataModel.remove(retractions);
		    		applicationMetadataModel.add(additions);
		    	} finally {
		    		applicationMetadataModel.leaveCriticalSection();
		    	}
		    }
		} catch (Exception e) {
			log.error("Error performing miscellaneous application metadata " +
					" replacements.", e);
		}
	}
	
	private OntModel loadModelFromDirectory(String directoryPath) {
		
		OntModel om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		File directory = new File(directoryPath);
		if (!directory.isDirectory()) {
			throw new RuntimeException(directoryPath + " must be a directory " +
					"containing RDF files.");
		}
		File[] rdfFiles = directory.listFiles();
		for (int i = 0; i < rdfFiles.length; i++) {
			try {
				File f = rdfFiles[i];
				FileInputStream fis = new FileInputStream(f);
				try {
					if (f.getName().endsWith(".n3")) {
						om.read(fis, null, "N3");
					} else {
						om.read(fis, null, "RDF/XML");
					}
				} catch (Exception e) {
					log.error("Unable to load RDF from " + f.getName(), e); 
				}
			} catch (FileNotFoundException fnfe) {
				log.error(rdfFiles[i].getName() + " not found. Unable to load" +
						" RDF from this location.");
			}
		}
		return om;
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {
		// nothing to do	
	}

}
