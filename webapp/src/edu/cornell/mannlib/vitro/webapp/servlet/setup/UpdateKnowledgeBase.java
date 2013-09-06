/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.ontology.update.KnowledgeBaseUpdater;
import edu.cornell.mannlib.vitro.webapp.ontology.update.UpdateSettings;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Invokes process to test whether the knowledge base needs any updating
 * to align with ontology changes.
 * @author bjl23
 *
 */
public class UpdateKnowledgeBase implements ServletContextListener {
    public static final String KBM_REQURIED_AT_STARTUP = "KNOWLEDGE_BASE_MIGRATION_REQUIRED_AT_STARTUP";
	private final static Log log = LogFactory.getLog(UpdateKnowledgeBase.class);
	
	private static final String DATA_DIR = "/WEB-INF/ontologies/update/";
	private static final String DIFF_FILE = DATA_DIR + "diff.tab.txt";
	private static final String ASK_QUERY_FILE = DATA_DIR + "askUpdated.sparql";
	private static final String SUCCESS_ASSERTIONS_FILE = DATA_DIR + "success.n3";
	private static final String OLD_TBOX_MODEL_DIR = DATA_DIR + "oldVersion/";
	private static final String NEW_TBOX_MODEL_DIR = "/WEB-INF/filegraph/tbox/";
	private static final String OLD_TBOX_ANNOTATIONS_DIR = DATA_DIR + "oldAnnotations/";
	private static final String NEW_TBOX_ANNOTATIONS_DIR = "/WEB-INF/ontologies/user/tbox/";
	//For display model migration
	private static final String OLD_DISPLAYMODEL_TBOX_PATH = DATA_DIR + "oldDisplayModel/displayTBOX.n3";
	private static final String NEW_DISPLAYMODEL_TBOX_PATH = "/WEB-INF/ontologies/app/menuload/displayTBOX.n3";
	private static final String OLD_DISPLAYMODEL_DISPLAYMETADATA_PATH = DATA_DIR + "oldDisplayModel/displayDisplay.n3";
	private static final String NEW_DISPLAYMODEL_DISPLAYMETADATA_PATH = "/WEB-INF/ontologies/app/menuload/displayDisplay.n3";
	private static final String NEW_DISPLAYMODEL_PATH = "/WEB-INF/ontologies/app/menu.n3";
	private static final String LOADED_STARTUPT_DISPLAYMODEL_DIR = "/WEB-INF/ontologies/app/loadedAtStartup/";
	private static final String OLD_DISPLAYMODEL_VIVOLISTVIEW_PATH = DATA_DIR + "oldDisplayModel/vivoListViewConfig.rdf";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		StartupStatus ss = StartupStatus.getBean(ctx);

		try {
			UpdateSettings settings = new UpdateSettings();
			putReportingPathsIntoSettings(ctx, settings);
			putNonReportingPathsIntoSettings(ctx, settings);

			WebappDaoFactory wadf = ModelAccess.on(ctx).getWebappDaoFactory();
			settings.setDefaultNamespace(wadf.getDefaultNamespace());
			settings.setAssertionOntModelSelector(ModelAccess.on(ctx).getBaseOntModelSelector());
			settings.setInferenceOntModelSelector(ModelAccess.on(ctx).getInferenceOntModelSelector());
			settings.setUnionOntModelSelector(ModelAccess.on(ctx).getUnionOntModelSelector());
			boolean tryMigrateDisplay = true;
			try {
				settings.setDisplayModel(ModelAccess.on(ctx).getDisplayModel());
				OntModel oldTBoxModel = loadModelFromDirectory(ctx.getRealPath(OLD_TBOX_MODEL_DIR));
				settings.setOldTBoxModel(oldTBoxModel);
				OntModel newTBoxModel = loadModelFromDirectory(ctx.getRealPath(NEW_TBOX_MODEL_DIR));
				settings.setNewTBoxModel(newTBoxModel);
				OntModel oldTBoxAnnotationsModel = loadModelFromDirectory(ctx.getRealPath(OLD_TBOX_ANNOTATIONS_DIR));
				settings.setOldTBoxAnnotationsModel(oldTBoxAnnotationsModel);
				OntModel newTBoxAnnotationsModel = loadModelFromDirectory(ctx.getRealPath(NEW_TBOX_ANNOTATIONS_DIR));
				settings.setNewTBoxAnnotationsModel(newTBoxAnnotationsModel);
				//Display model tbox and display metadata 
				//old display model tbox model
				OntModel oldDisplayModelTboxModel = loadModelFromFile(ctx.getRealPath(OLD_DISPLAYMODEL_TBOX_PATH));
				settings.setOldDisplayModelTboxModel(oldDisplayModelTboxModel);
				//new display model tbox model
				OntModel newDisplayModelTboxModel = loadModelFromFile(ctx.getRealPath(NEW_DISPLAYMODEL_TBOX_PATH));
				settings.setNewDisplayModelTboxModel(newDisplayModelTboxModel);
				//old display model display model metadata
				OntModel oldDisplayModelDisplayMetadataModel = loadModelFromFile(ctx.getRealPath(OLD_DISPLAYMODEL_DISPLAYMETADATA_PATH));
				settings.setOldDisplayModelDisplayMetadataModel(oldDisplayModelDisplayMetadataModel);
				//new display model display model metadata
				OntModel newDisplayModelDisplayMetadataModel = loadModelFromFile(ctx.getRealPath(NEW_DISPLAYMODEL_DISPLAYMETADATA_PATH));
				settings.setNewDisplayModelDisplayMetadataModel(newDisplayModelDisplayMetadataModel);
				//Get new display model
				OntModel newDisplayModelFromFile = loadModelFromFile(ctx.getRealPath(NEW_DISPLAYMODEL_PATH));
				settings.setNewDisplayModelFromFile(newDisplayModelFromFile);
				OntModel loadedAtStartupFiles = loadModelFromDirectory(ctx.getRealPath(LOADED_STARTUPT_DISPLAYMODEL_DIR));
				settings.setLoadedAtStartupDisplayModel(loadedAtStartupFiles);
				OntModel oldDisplayModelVivoListView = loadModelFromFile(ctx.getRealPath(OLD_DISPLAYMODEL_VIVOLISTVIEW_PATH));
				settings.setVivoListViewConfigDisplayModel(oldDisplayModelVivoListView);
			} catch (Exception e) {
				log.info("Unable to read display model migration files. " + e.getMessage());
				tryMigrateDisplay = false;
			}
				
			try {		
			   KnowledgeBaseUpdater ontologyUpdater = new KnowledgeBaseUpdater(settings);
			  
			   try {
				  if (!ontologyUpdater.updateRequired(ctx)) {
				      log.info("No data migration required.");
				  } else {
					  ctx.setAttribute(KBM_REQURIED_AT_STARTUP, Boolean.TRUE);
					  log.info("Data migration required");
					  ontologyUpdater.update(ctx);
					  if (tryMigrateDisplay) {
						  try {
						       migrateDisplayModel(settings);
						       log.info("Migrated display model");
						  } catch (Exception e) {
							   log.warn("unable to successfully update display model: " + e.getMessage());
						  }
					  }
				  }
			   } catch (Exception ioe) {
					ss.fatal(this, "Exception updating knowledge base for ontology changes: ", ioe);
			   }	
			} catch (Throwable t){
				ss.fatal(this, "Exception updating knowledge base for ontology changes: ", t);
			}
		} catch (Throwable t) {
			ss.fatal(this, "Exception updating knowledge base for ontology changes: ", t);
		}
	}	

	
	/**
	 * Set the paths for the files that specify how to perform the update
	 */
	private void putNonReportingPathsIntoSettings(ServletContext ctx, UpdateSettings settings) {
        settings.setAskUpdatedQueryFile(ctx.getRealPath(ASK_QUERY_FILE));
        settings.setDiffFile(ctx.getRealPath(DIFF_FILE));
        settings.setSparqlConstructAdditionsDir(ctx.getRealPath(DATA_DIR + "sparqlConstructs/additions"));
        settings.setSparqlConstructDeletionsDir(ctx.getRealPath(DATA_DIR + "sparqlConstructs/deletions"));
        settings.setSuccessAssertionsFile(ctx.getRealPath(SUCCESS_ASSERTIONS_FILE));
        settings.setSuccessRDFFormat("N3");
	}
	
	/**
	 * Create the directories where we will report on the update. 
	 * Put the paths for the directories and files into the settings object.
	 */
	private void putReportingPathsIntoSettings(ServletContext ctx, UpdateSettings settings) throws IOException {
		ConfigurationProperties props = ConfigurationProperties.getBean(ctx);
		Path homeDir = Paths.get(props.getProperty("vitro.home"));
		
		Path dataDir = createDirectory(homeDir, "upgrade", "knowledgeBase");
		settings.setDataDir(dataDir.toString());
		StartupStatus.getBean(ctx).info(this, "Updating knowledge base: reports are in '" + dataDir + "'");

		Path changedDir = createDirectory(dataDir, "changedData");
		settings.setAddedDataFile(changedDir.resolve("addedData.n3").toString());
		settings.setRemovedDataFile(changedDir.resolve("removedData.n3").toString());
		
		Path logDir = createDirectory(dataDir, "logs");
		settings.setLogFile(logDir.resolve(timestampedFileName("knowledgeBaseUpdate", "log")).toString());
		settings.setErrorLogFile(logDir.resolve(timestampedFileName("knowledgeBaseUpdate.error", "log")).toString());
	}

	private Path createDirectory(Path parent, String... children) throws IOException {
		Path dir = parent;
		for (String child : children) {
			dir = dir.resolve(child);
		}
		Files.createDirectories(dir);
		return dir;
	}

	
	//Multiple changes from 1.4 to 1.5 will occur
	//update migration model
	public void migrateDisplayModel(UpdateSettings settings) throws Exception {
		log.debug("Beginning migration of display model");
		OntModel displayModel = settings.getDisplayModel();
		Model addStatements = ModelFactory.createDefaultModel();
		Model removeStatements = ModelFactory.createDefaultModel();
		//remove old tbox and display metadata statements and add statements from new versions
		replaceTboxAndDisplayMetadata(displayModel, addStatements, removeStatements,settings);
		//Update statements for data getter class types that have changed in 1.5 
		updateDataGetterClassNames(displayModel, addStatements, removeStatements);
		//add cannot delete flags to pages that shouldn't allow deletion on page list
		addCannotDeleteFlagDisplayModel(displayModel, addStatements, removeStatements);
		//removes requiresTemplate statement for people page
		updatePeoplePageDisplayModel(displayModel, addStatements, removeStatements);
		//add page list
		addPageListDisplayModel(displayModel, addStatements, removeStatements,settings);
		//update data getter labels
		updateDataGetterLabels(displayModel, addStatements, removeStatements,settings);
		
		displayModel.enterCriticalSection(Lock.WRITE);
		try {
			if(log.isDebugEnabled()) {
				StringWriter sw = new StringWriter();
				addStatements.write(sw,"N3");
				log.debug("Statements to be added are: ");
				log.debug(sw.toString());
				sw.close();
				sw = new StringWriter();
				removeStatements.write(sw, "N3");
				log.debug("Statements to be removed are: ");
				log.debug(sw.toString());
				sw.close();
			}
			displayModel.remove(removeStatements);
			displayModel.add(addStatements);
		} finally {
			displayModel.leaveCriticalSection();
		}
	}
	
	//replace 
	private void replaceTboxAndDisplayMetadata(OntModel displayModel, Model addStatements, Model removeStatements, UpdateSettings settings) {
		
		OntModel oldDisplayModelTboxModel = settings.getOldDisplayModelTboxModel();
		OntModel oldDisplayModelDisplayMetadataModel = settings.getOldDisplayModelDisplayMetadataModel();
		OntModel newDisplayModelTboxModel = settings.getNewDisplayModelTboxModel();
		OntModel newDisplayModelDisplayMetadataModel = settings.getNewDisplayModelDisplayMetadataModel();	
		OntModel loadedAtStartup = settings.getLoadedAtStartupDisplayModel();
		OntModel oldVivoListView = settings.getVivoListViewConfigDisplayModel();
		//Remove old display model tbox and display metadata statements from display model
		removeStatements.add(oldDisplayModelTboxModel);
		removeStatements.add(oldDisplayModelDisplayMetadataModel);
		//the old startup folder only contained by oldVivoListView
		removeStatements.add(oldVivoListView);
		StringWriter sw = new StringWriter();

		try {
			log.debug("Adding old display tbox model, display metadata model, and oldVivoListView to remove statements.  Remove statements now include:");
			removeStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close(); 
		}
		catch(Exception ex) {
			log.error("Exception occurred", ex);
		}
		//Add statements from new tbox and display metadata 
		addStatements.add(newDisplayModelTboxModel);
		addStatements.add(newDisplayModelDisplayMetadataModel);
		//this should include the list view in addition to other files
		addStatements.add(loadedAtStartup);
		try {
			sw = new StringWriter();
			log.debug("Adding new display tbox model, display metadata model, and loaded at startup to add statements.  Add statements now include:");
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close(); 	
		} catch(Exception ex) {
			log.error("Exception occurred in adding new display model tbox/metadata info to add statements ", ex);
		}
		log.debug("Adding new display tbox model, display metadata model, and all models loaded at startup");
	}
	
	//update statements for data getter classes
	private void updateDataGetterClassNames(OntModel displayModel, Model addStatements, Model removeStatements) {
		Resource classGroupOldType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.ClassGroupPageData");
		Resource browseOldType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.BrowseDataGetter");
		Resource individualsForClassesOldType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.IndividualsForClassesDataGetter");
		Resource internalClassesOldType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.InternalClassesDataGetter");
		Resource classGroupNewType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData");
		Resource browseNewType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.BrowseDataGetter");
		Resource individualsForClassesNewType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.IndividualsForClassesDataGetter");
		Resource internalClassesNewType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter");
		
		//Find statements where type is ClassGroupData
		updateAddRemoveDataGetterStatements(displayModel, removeStatements, addStatements, classGroupOldType, classGroupNewType);
		//Find statements where type is BrowseDataGetter
		updateAddRemoveDataGetterStatements(displayModel, removeStatements, addStatements, browseOldType, browseNewType);
		//Find statements where type is individuals for classes
		updateAddRemoveDataGetterStatements(displayModel, removeStatements, addStatements, individualsForClassesOldType, individualsForClassesNewType);
		//Find statements where type is internal class
		updateAddRemoveDataGetterStatements(displayModel, removeStatements, addStatements, internalClassesOldType, internalClassesNewType);
	}
	
	private void updateAddRemoveDataGetterStatements(OntModel displayModel, 
			Model removeStatements, Model addStatements,
			Resource oldType, Resource newType) {
		log.debug("Old type: " + oldType.getURI() + " - newType: " + newType.getURI());
		removeStatements.add(displayModel.listStatements(null, RDF.type, oldType));
		StmtIterator oldStatements = displayModel.listStatements(null, RDF.type, oldType);
		while(oldStatements.hasNext()) {
			Statement stmt = oldStatements.nextStatement();
			addStatements.add(stmt.getSubject(), RDF.type, newType);
		}
		//Print out remove and add statements here
		StringWriter sw = new StringWriter();
		try {
			log.debug("Adding statements with old data getter types to remove statements, remove statements is now");
			removeStatements.write(sw,"N3");
			log.debug(sw.toString());
			sw.close();
			sw = new StringWriter();
			log.debug("Adding statements with new data getter types to add statements, add statements is now");
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
		} catch(Exception ex) {
			log.error("Error occurred in writing out remove and statements for data getter types", ex);
		}
	}
	
	//add cannotDeleteFlag to display model
	private void addCannotDeleteFlagDisplayModel(OntModel displayModel, Model addStatements, Model removeStatements) {
		Resource homePage = displayModel.getResource(DisplayVocabulary.HOME_PAGE_URI);
		addStatements.add(homePage, 
				ResourceFactory.createProperty(DisplayVocabulary.DISPLAY_NS + "cannotDeletePage"),
				ResourceFactory.createPlainLiteral("true"));
	}
		
	//remove requires template
	private void updatePeoplePageDisplayModel(OntModel displayModel, Model addStatements, Model removeStatements) {
		Resource peoplePage = displayModel.getResource(DisplayVocabulary.DISPLAY_NS + "People");
		if(peoplePage != null) {
			removeStatements.add(peoplePage, DisplayVocabulary.REQUIRES_BODY_TEMPLATE,
					ResourceFactory.createPlainLiteral("menupage--classgroup-people.ftl"));
		}
		log.debug("Will remove body template from people page so added that to remove statements ");
	}
	
	//add page list sparql query
	private void addPageListDisplayModel(OntModel displayModel, Model addStatements, Model removeStatements, UpdateSettings settings) {
		OntModel newDisplayModel = settings.getNewDisplayModelFromFile();
		//Get all statements about pageListPage and pageListData
		Resource pageList = newDisplayModel.getResource(DisplayVocabulary.DISPLAY_NS + "pageListPage");
		Resource pageListData = newDisplayModel.getResource(DisplayVocabulary.DISPLAY_NS + "pageListData");

		addStatements.add(newDisplayModel.listStatements(pageList, null, (RDFNode) null));
		addStatements.add(newDisplayModel.listStatements(pageListData, null, (RDFNode) null));
		StringWriter sw = new StringWriter();
		try {
			if(pageList != null) {
				log.debug("Page list uri is " + pageList.getURI());
			} else {
				log.debug("Page list uri is null for some reason");
			}
			log.debug("New Display model from file is ");
			newDisplayModel.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
			sw = new StringWriter();
			log.debug("Added statements now include ");
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
		}catch(Exception ex) {
			log.error("Exception occurred in writing out new display model", ex);
		}
		
		log.debug("Checking: AFTER adding pageList resource, what do we have for pageList page");
		Resource testResource = ResourceFactory.createResource(DisplayVocabulary.DISPLAY_NS + "pageListPage");
		StmtIterator testIt = addStatements.listStatements(testResource, null, (RDFNode) null);
		if(!testIt.hasNext()) {
			log.debug("Add statements does not have the page list page resource " + testResource.getURI());
		}
		
		while(testIt.hasNext()) {
			log.debug("Statement for page list resource: " + testIt.nextStatement().toString());
		}
	}
	
	//update any new labels
	private void updateDataGetterLabels(OntModel displayModel, Model addStatements, Model removeStatements, UpdateSettings settings) {
		log.debug("Checking: BEFORE adding any statements, what do we have for pageList page");
		Resource testResource = ResourceFactory.createResource(DisplayVocabulary.DISPLAY_NS + "pageListPage");
		StmtIterator testIt = addStatements.listStatements(testResource, null, (RDFNode) null);
		if(!testIt.hasNext()) {
			log.debug("Add statements does not have the page list page resource " + testResource.getURI());
		}
		
		while(testIt.hasNext()) {
			log.debug("Statement for page list resource: " + testIt.nextStatement().toString());
		}
		
		log.debug("Triple checking -- before this method, the add statements model contains");
		StringWriter sw = new StringWriter();
		try {
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
		}catch(Exception ex) 
		{
			log.error("Error occurred in adding resource labels ", ex);
		}
		
		
		OntModel newDisplayModel = settings.getNewDisplayModelFromFile();
		List<Resource> resourcesForLabels = new ArrayList<Resource>();
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData"));
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.BrowseDataGetter"));
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.IndividualsForClassesDataGetter"));
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter"));
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SparqlQueryDataGetter"));
		for(Resource r: resourcesForLabels) {
			log.debug("Adding the following for " + r.getURI());
			log.debug(newDisplayModel.listStatements(r, RDFS.label, (RDFNode)null).toList().toString());
			addStatements.add(newDisplayModel.listStatements(r, RDFS.label, (RDFNode)null));
			log.debug("After adding statements, we now have the following in addStatements:::");
			sw = new StringWriter();
			try {
				addStatements.write(sw, "N3");
				log.debug(sw.toString());
				sw.close();
			}catch(Exception ex) 
			{
				log.error("Error occurred in adding resource labels ", ex);
			}
			
		}
		//Add statements now includes
		log.debug("AFTER all resources added, Add statements now includes ");
		sw = new StringWriter();
		try {
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
		}catch(Exception ex) 
		{
			log.error("Error occurred in adding resource labels ", ex);
		}
		
	}
	
  					
	private OntModel loadModelFromDirectory(String directoryPath) {
		log.debug("Loading model from directory " + directoryPath);
		OntModel om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		File directory = new File(directoryPath);
		if (!directory.isDirectory()) {
			throw new ModelDirectoryNotFoundException(directoryPath + " must be a directory " +
					"containing RDF files.");
		}
		File[] rdfFiles = directory.listFiles();
		for (int i = 0; i < rdfFiles.length; i++) {
			readFile(rdfFiles[i], om, directoryPath);
		}
		return om;
	}
	
	//load file from file path
	private OntModel loadModelFromFile(String filePath) {
		log.debug("Load model from file " + filePath);
		OntModel om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		File file = new File(filePath);
		if (!file.isFile()) {
			throw new ModelFileNotFoundException(filePath + " must be a file " +
					"containing RDF files.");
		}
		readFile(file, om, filePath);
		return om;
	}
	
	
	private void readFile(File f, OntModel om, String path) {
		try {
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
			log.error(f.getName() + " not found. Unable to load" +
					" RDF from this location: " + path);
		}	
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// nothing to do	
	}
	
	private static String timestampedFileName(String prefix, String suffix) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-sss");
		return prefix + "." + sdf.format(new Date()) + "." + suffix;
	}
	
	private class ModelDirectoryNotFoundException extends RuntimeException {
		public ModelDirectoryNotFoundException(String msg) {
			super(msg);
		}
	}
	
	private class ModelFileNotFoundException extends RuntimeException {
		public ModelFileNotFoundException(String msg) {
			super(msg);
		}
	}
}
