/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.ontology.update;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.dao.jena.RDFServiceDataset;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeSet;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService.ModelSerializationFormat;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;
import edu.cornell.mannlib.vitro.webapp.utils.jena.JenaIngestUtils;

/**
 * Performs knowledge base updates necessary to align with a
 * new ontology version.
 */
public class KnowledgeBaseUpdater {

	private final Log log = LogFactory.getLog(KnowledgeBaseUpdater.class);
	
	private UpdateSettings settings;
	private ChangeLogger logger;
	private ChangeRecord record;
	
	public KnowledgeBaseUpdater(UpdateSettings settings) {
		this.settings = settings;
		this.logger = null;
		this.record = new SimpleChangeRecord(settings.getAddedDataFile(), settings.getRemovedDataFile());
	}
	
	public boolean update(ServletContext servletContext) throws IOException {	
					
		if (this.logger == null) {
			this.logger = new SimpleChangeLogger(settings.getLogFile(),	settings.getErrorLogFile());
		}
			
		long startTime = System.currentTimeMillis();
        log.info("Performing any necessary data migration");
        logger.log("Started knowledge base migration");
		
		try {
		     performUpdate(servletContext);
		} catch (Exception e) {
			 logger.logError(e.getMessage());
			 log.error(e,e);
		}

		if (!logger.errorsWritten()) {
			assertSuccess(servletContext);
	    	logger.logWithDate("Finished knowledge base migration");
		}
		
		record.writeChanges();
		logger.closeLogs();

		long elapsedSecs = (System.currentTimeMillis() - startTime)/1000;		
		log.info("Finished checking knowledge base in " + elapsedSecs + " second" + (elapsedSecs != 1 ? "s" : ""));
		
		return record.hasRecordedChanges();
	}
	
	private void performUpdate(ServletContext servletContext) throws Exception {
		
		List<AtomicOntologyChange> rawChanges = getAtomicOntologyChanges();
		
		AtomicOntologyChangeLists changes = new AtomicOntologyChangeLists(rawChanges,settings.getNewTBoxModel(),settings.getOldTBoxModel());
	        	
		// update ABox data any time
    	log.debug("performing SPARQL CONSTRUCT additions");
    	performSparqlConstructs(settings.getSparqlConstructAdditionsDir(), settings.getRDFService(), ADD);
    	
        log.debug("performing SPARQL CONSTRUCT retractions");
        performSparqlConstructs(settings.getSparqlConstructDeletionsDir(), settings.getRDFService(), RETRACT);
        
        log.info("\tchecking the abox");
        updateABox(changes);
        
        log.debug("performing post-processing SPARQL CONSTRUCT additions");
        performSparqlConstructs(settings.getSparqlConstructAdditionsDir() + "/post/", 
                settings.getRDFService(), ADD);
        
        log.debug("performing post-processing SPARQL CONSTRUCT retractions");
        performSparqlConstructs(settings.getSparqlConstructDeletionsDir() + "/post/", 
                settings.getRDFService(), RETRACT);
        
        
        // Only modify the TBox and migration metadata the first time
        if(updateRequired(servletContext)) {
            //process the TBox before the ABox
            try {
                log.debug("\tupdating tbox annotations");
                updateTBoxAnnotations();
            } catch (Exception e) {
                log.error(e,e);
            }
            
        }

	}
	
    private static final boolean ADD = true;
    private static final boolean RETRACT = !ADD;
	
    /**
     * Performs a set of arbitrary SPARQL CONSTRUCT queries on the 
     * data, for changes that cannot be expressed as simple property
     * or class additions, deletions, or renamings.
     * Blank nodes created by the queries are given random URIs.
     * @param sparqlConstructDir
     * @param readModel
     * @param writeModel
     * @param add (add = true; retract = false)
     */
    private void performSparqlConstructs(String sparqlConstructDir, 
            RDFService rdfService,
            boolean add)   throws IOException {
        Dataset dataset = new RDFServiceDataset(rdfService);
        File sparqlConstructDirectory = new File(sparqlConstructDir);
        log.debug("Using SPARQL CONSTRUCT directory " + sparqlConstructDirectory);
        if (!sparqlConstructDirectory.isDirectory()) {
            String logMsg = this.getClass().getName() + 
                    "performSparqlConstructs() expected to find a directory " +
                    " at " + sparqlConstructDir + ". Unable to execute " +
                    " SPARQL CONSTRUCTS.";
            logger.logError(logMsg);
            log.error(logMsg);
            return;
        }
        List<File> sparqlFiles = Arrays.asList(sparqlConstructDirectory.listFiles());
        Collections.sort(sparqlFiles); // queries may depend on being run in a certain order
        JenaIngestUtils jiu = new JenaIngestUtils();
        for (File sparqlFile : sparqlFiles) {	
            if(sparqlFile.isDirectory()) {
                continue;
            }
            StringBuffer fileContents = new StringBuffer();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(sparqlFile));
                String ln;
                while ( (ln = reader.readLine()) != null) {
                    fileContents.append(ln).append('\n');
                }
            } catch (FileNotFoundException fnfe) {
                String logMsg = "WARNING: performSparqlConstructs() could not find " +
                        " SPARQL CONSTRUCT file " + sparqlFile + ". Skipping.";
                logger.log(logMsg);
                log.info(logMsg);
                continue;
            }   
            Model anonModel = ModelFactory.createDefaultModel();
            try {
                log.debug("\t\tprocessing SPARQL construct query from file " + sparqlFile.getName());
                
                anonModel = RDFServiceUtils.parseModel(
                        rdfService.sparqlConstructQuery(fileContents.toString(), 
                                RDFService.ModelSerializationFormat.NTRIPLE), 
                                ModelSerializationFormat.NTRIPLE);

                long num = anonModel.size();
                if (num > 0) {
                    String logMsg = (add ? "Added " : "Removed ") + num + 
                            " statement"  + ((num > 1) ? "s" : "") + 
                            " using the SPARQL construct query from file " + 
                            sparqlFile.getParentFile().getName() +
                            "/" + sparqlFile.getName();
                    logger.log(logMsg);
                    log.info(logMsg);
                }
        
            } catch (Exception e) {
                logger.logError(this.getClass().getName() + 
                        ".performSparqlConstructs() unable to execute " +
                        "query at " + sparqlFile + ". Error message is: " + e.getMessage());
                log.error(e,e);
            }
        
            if(!add) {
                StmtIterator sit = anonModel.listStatements();
                while (sit.hasNext()) {
                    Statement stmt = sit.nextStatement();
                    Iterator<String> graphIt = dataset.listNames();
                    while(graphIt.hasNext()) {
                        String graph = graphIt.next();
                        if(!isUpdatableABoxGraph(graph)) {
                            continue;
                        }
                        Model writeModel = dataset.getNamedModel(graph);
                        if (writeModel.contains(stmt)) {
                            writeModel.remove(stmt);
                        }
                    }
                }            
                record.recordRetractions(anonModel);
                //log.info("removed " + anonModel.size() + " statements from SPARQL CONSTRUCTs");
            } else {
                Model writeModel = dataset.getNamedModel(JenaDataSourceSetupBase.JENA_DB_MODEL);
                Model additions = jiu.renameBNodes(
                        anonModel, settings.getDefaultNamespace() + "n", writeModel);
                Model actualAdditions = ModelFactory.createDefaultModel();
                StmtIterator stmtIt = additions.listStatements();      
                while (stmtIt.hasNext()) {
                    Statement stmt = stmtIt.nextStatement();
                    if (!writeModel.contains(stmt)) {
                        actualAdditions.add(stmt);
                    }
                }      
                writeModel.add(actualAdditions);
                //log.info("added " + actualAdditions.size() + " statements from SPARQL CONSTRUCTs");
                record.recordAdditions(actualAdditions);
            }
            
        }
    }
	
	
	private List<AtomicOntologyChange> getAtomicOntologyChanges() 
			throws IOException {
		return (new OntologyChangeParser(logger)).parseFile(settings.getDiffFile());
	}
	

	
	private void updateABox(AtomicOntologyChangeLists changes) 
			throws IOException {
		
	
		ABoxUpdater aboxUpdater = new ABoxUpdater(settings, logger, record);
		aboxUpdater.processPropertyChanges(changes.getAtomicPropertyChanges());
		aboxUpdater.processClassChanges(changes.getAtomicClassChanges());
	}
	
	private void updateTBoxAnnotations() {
		TBoxUpdater tboxUpdater = new TBoxUpdater(settings, logger, record);         
		try {
		    tboxUpdater.modifyPropertyQualifications();
		} catch (Exception e) {
		    log.error("Unable to modify qualified property config file ", e);
		}
		try {
            tboxUpdater.updateDefaultAnnotationValues();
		} catch (Exception e) {
		    log.error("Unable to update default annotation values ", e);
		}
	}
	
	/**
	 * Executes a SPARQL ASK query to determine whether the knowledge base
	 * needs to be updated to conform to a new ontology version
	 */
	public boolean updateRequired(ServletContext servletContext) throws IOException {
		boolean required = true;
		
		String sparqlQueryStr = loadSparqlQuery(settings.getAskUpdatedQueryFile());
		if (sparqlQueryStr == null) {
			return required;
		}
		
		RDFService rdfService = RDFServiceUtils.getRDFServiceFactory(servletContext).getRDFService();

		// if the ASK query DOES have a solution (i.e. the assertions exist
		// showing that the update has already been performed), then the update
		// is NOT required.
		try {
			if (rdfService.sparqlAskQuery(sparqlQueryStr)) {
				required = false;
			} else {
				required = true;
				if (JenaDataSourceSetupBase.isFirstStartup()) {
					assertSuccess(servletContext);  
					log.info("The application is starting with an empty database. " +
					         "An indication will be added to the database that a " +
					         "knowledge base migration to the current version is " +
					         "not required.");
				    required = false;	
				}
			}		   
		} catch (RDFServiceException e) {
			log.error("error trying to execute query to find out if knowledge base update is required",e); 
		}
		
		return required; 
	}
	
	/**
	 * loads a SPARQL ASK query from a text file
	 * @param filePath
	 * @return the query string or null if file not found
	 */
	public static String loadSparqlQuery(String filePath) throws IOException {
		
		File file = new File(filePath);	
		if (!file.exists()) {
			throw new RuntimeException("SPARQL file not found at " + filePath);
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuffer fileContents = new StringBuffer();
		String ln;		
		while ((ln = reader.readLine()) != null) {
			fileContents.append(ln).append('\n');
		}
		return fileContents.toString();				
	}
	
	private void assertSuccess(ServletContext servletContext) throws FileNotFoundException, IOException {
		try {				
			RDFService rdfService = RDFServiceUtils.getRDFServiceFactory(servletContext).getRDFService();
			
			ChangeSet changeSet = rdfService.manufactureChangeSet();
		    File successAssertionsFile = new File(settings.getSuccessAssertionsFile()); 
		    InputStream inStream = new FileInputStream(successAssertionsFile);		    
		    changeSet.addAddition(inStream, RDFService.ModelSerializationFormat.N3, JenaDataSourceSetupBase.JENA_APPLICATION_METADATA_MODEL);
			rdfService.changeSetUpdate(changeSet);	
		} catch (Exception e) {
			log.error("unable to make RDF assertions about successful " +
					" update to new ontology version: ", e);
		}		
	}
	
	public static boolean isUpdatableABoxGraph(String graphName) {
	    return (!graphName.contains("tbox") && !graphName.contains("filegraph"));
	}

	/**
	 * A class that allows to access two different ontology change lists,
	 * one for class changes and the other for property changes.  The 
	 * constructor will split a list containing both types of changes.
	 * @author bjl23
	 *
	 */
	private class AtomicOntologyChangeLists {
		
		private List<AtomicOntologyChange> atomicClassChanges = 
				new ArrayList<AtomicOntologyChange>();

		private List<AtomicOntologyChange> atomicPropertyChanges =
				new ArrayList<AtomicOntologyChange>();
		
		public AtomicOntologyChangeLists (
				List<AtomicOntologyChange> changeList, OntModel newTboxModel,
				OntModel oldTboxModel) throws IOException {
			
			Iterator<AtomicOntologyChange> listItr = changeList.iterator();
			
			while(listItr.hasNext()) {
				AtomicOntologyChange changeObj = listItr.next();
				if (changeObj.getSourceURI() != null){
			        log.debug("triaging " + changeObj);
					if (oldTboxModel.getOntProperty(changeObj.getSourceURI()) != null){
						 atomicPropertyChanges.add(changeObj);
						 log.debug("added to property changes");
					} else if (oldTboxModel.getOntClass(changeObj.getSourceURI()) != null) {
						 atomicClassChanges.add(changeObj);
						 log.debug("added to class changes");
					} else if ("Prop".equals(changeObj.getNotes())) {
						 atomicPropertyChanges.add(changeObj);
					} else if ("Class".equals(changeObj.getNotes())) {
						 atomicClassChanges.add(changeObj);
					} else{
						 logger.log("WARNING: Source URI is neither a Property" +
						    		" nor a Class. " + "Change Object skipped for sourceURI: " + changeObj.getSourceURI());
					}
					
				} else if(changeObj.getDestinationURI() != null){
					
					if (newTboxModel.getOntProperty(changeObj.getDestinationURI()) != null) {
						atomicPropertyChanges.add(changeObj);
					} else if(newTboxModel.getOntClass(changeObj.
						getDestinationURI()) != null) {
						atomicClassChanges.add(changeObj);
					} else{
						logger.log("WARNING: Destination URI is neither a Property" +
								" nor a Class. " + "Change Object skipped for destinationURI: " + changeObj.getDestinationURI());
					}
				} else{
					logger.log("WARNING: Source and Destination URI can't be null. " + "Change Object skipped" );
				}
			}
			//logger.log("Property and Class change Object lists have been created");
		}
		
		public List<AtomicOntologyChange> getAtomicClassChanges() {
			return atomicClassChanges;
		}

		public List<AtomicOntologyChange> getAtomicPropertyChanges() {
			return atomicPropertyChanges;
		}		
		
	}	
}
