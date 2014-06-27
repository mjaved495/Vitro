/* $This file is distributed under the terms of the license in /doc/license.txt$ */
/**
 * 
 */
package edu.cornell.mannlib.vitro.webapp.search.documentBuilding;

import java.io.InputStream;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stubs.edu.cornell.mannlib.vitro.webapp.modules.ApplicationStub;
import stubs.edu.cornell.mannlib.vitro.webapp.modules.searchEngine.SearchEngineStub;
import stubs.javax.servlet.ServletContextStub;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.application.ApplicationUtils;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.modules.searchEngine.SearchInputDocument;
import edu.cornell.mannlib.vitro.webapp.modules.searchEngine.SearchInputField;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceFactorySingle;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.jena.model.RDFServiceModel;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.documentBuilding.SkipIndividualException;
import edu.cornell.mannlib.vitro.webapp.search.documentBuilding.ThumbnailImageURL;

public class ThumbnailImageURLTest extends AbstractTestClass{
    RDFServiceFactory testRDF;
    String personsURI = "http://vivo.cornell.edu/individual/individual8803";
        
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        setLoggerLevel(RDFDefaultErrorHandler.class, Level.OFF);
        ApplicationStub.setup(new ServletContextStub(), new SearchEngineStub());

        Model model = ModelFactory.createDefaultModel();        
        InputStream in = ThumbnailImageURLTest.class.getResourceAsStream("testPerson.n3");
        model.read(in,"","N3");        
        testRDF = new RDFServiceFactorySingle( new RDFServiceModel( model ) );            
    }

    /**
     * Test to see if ThumbnailImageURL gets the date it is suppose to gete
     * from a set of RDF.
     */
    @Test
    public void testThumbnailFieldCreatedInSearchDoc() {
        SearchInputDocument doc = ApplicationUtils.instance().getSearchEngine().createInputDocument();
        ThumbnailImageURL testMe = new ThumbnailImageURL( testRDF );
        Individual ind = new IndividualImpl();
        ind.setURI(personsURI);
        
        //make sure that the person is in the RDF
        try {
            testMe.modifyDocument(ind, doc, null);
        } catch (SkipIndividualException e) {
                Assert.fail("Test individual was skipped by classes that build the search document: " + e.getMessage());
        }

        //make sure that a search document field got created for the thumbnail image
        
        SearchInputField thumbnailField = doc.getField( VitroSearchTermNames.THUMBNAIL_URL );
        Assert.assertNotNull(thumbnailField);

        Assert.assertNotNull( thumbnailField.getValues() );
        Assert.assertEquals(1, thumbnailField.getValues().size());
        
        Assert.assertEquals("http://vivo.cornell.edu/individual/n54945", thumbnailField.getFirstValue());
    }

}
