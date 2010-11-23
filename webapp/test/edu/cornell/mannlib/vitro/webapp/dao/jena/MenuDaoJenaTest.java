/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.io.InputStream;
import java.lang.reflect.Field;

import junit.framework.Assert;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.menu.Menu;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.menu.MenuItem;


public class MenuDaoJenaTest extends AbstractTestClass {
    
    OntModel displayModel;
    
    @Before
    public void setUp() throws Exception {
        // Suppress error logging.
        setLoggerLevel(RDFDefaultErrorHandler.class, Level.OFF);

        Model model = ModelFactory.createDefaultModel();        
        InputStream in = MenuDaoJenaTest.class.getResourceAsStream("resources/menuForTest.n3");
        model.read(in,"","N3");        
        displayModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,model);
    }

    @Test
    public void getMenuItemTest(){
        MenuDaoJena menuDaoJena = new MenuDaoJena(new WebappDaoFactoryJena(displayModel));
        Menu menu = menuDaoJena.getMenu(VitroVocabulary.DISPLAY+"DefaultMenu", displayModel);       
        
        try{
            Class clz = UrlBuilder.class;
            Field f = clz.getDeclaredField( "contextPath" );
            f.setAccessible(true);
            f.set(null, "bogusUrlContextPath"); 
        }catch(Exception e){
            Assert.fail(e.toString());
        }
        
        Assert.assertNotNull(menu);
        Assert.assertNotNull( menu.getItems() );
        Assert.assertEquals(5, menu.getItems().size());
        
        //The nulls in getUrl() are from the UrlBuilder not being setup correctly.
        //it should be fine.
        
        MenuItem item = menu.getItems().get(0);
        Assert.assertNotNull(item);
        Assert.assertEquals("Home",item.getLinkText());
        Assert.assertEquals("bogusUrlContextPath/home",item.getUrl());
        
        item = menu.getItems().get(1);
        Assert.assertNotNull(item);
        Assert.assertEquals("People",item.getLinkText());
        Assert.assertEquals("bogusUrlContextPath/people",item.getUrl());
        
        item = menu.getItems().get(2);
        Assert.assertNotNull(item);
        Assert.assertEquals("Publications",item.getLinkText());
        Assert.assertEquals("bogusUrlContextPath/publications",item.getUrl());
        
        item = menu.getItems().get(3);
        Assert.assertNotNull(item);
        Assert.assertEquals("Events",item.getLinkText());
        Assert.assertEquals("bogusUrlContextPath/events",item.getUrl());
        
        item = menu.getItems().get(4);
        Assert.assertNotNull(item);
        Assert.assertEquals("Organizations",item.getLinkText());
        Assert.assertEquals("bogusUrlContextPath/organizations",item.getUrl());
    }
}
