/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.rdfservice.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.resultset.ResultSetFormat;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService.ModelSerializationFormat;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService.ResultFormat;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;

public class RDFServiceUtils {

	static Log log = LogFactory.getLog(RDFServiceUtils.class);
	
    private static final String RDFSERVICEFACTORY_ATTR = 
            RDFServiceUtils.class.getName() + ".RDFServiceFactory";
    private static final String RDFSERVICEFACTORY_FILTERING_ATTR = 
            RDFServiceUtils.class.getName() + ".RDFServiceFactory.Filtering";
    
       
    public static RDFServiceFactory getRDFServiceFactory(ServletContext context) {
        Object o = context.getAttribute(RDFSERVICEFACTORY_ATTR);
        return (o instanceof RDFServiceFactory) ? (RDFServiceFactory) o : null;
    }
    
    public static void setRDFServiceFactory(ServletContext context, 
            RDFServiceFactory factory) {
        context.setAttribute(RDFSERVICEFACTORY_ATTR, factory);
    }
    
    public static InputStream toInputStream(String serializedRDF) {
        try {
            return new ByteArrayInputStream(serializedRDF.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Model parseModel(InputStream in, ModelSerializationFormat format) {
        Model model = ModelFactory.createDefaultModel();
        model.read(in, null,
                getSerializationFormatString(format));
        return model;
    }
    
    public static ResultSetFormat getJenaResultSetFormat(ResultFormat resultFormat) {
        switch(resultFormat) {
            case JSON:
                return ResultSetFormat.syntaxJSON;
            case CSV:
                return ResultSetFormat.syntaxCSV;
            case XML:
                return ResultSetFormat.syntaxXML;
            case TEXT:
                return ResultSetFormat.syntaxText;
            default:
                throw new RuntimeException("unsupported ResultFormat");
        }
    }
    
    public static String getSerializationFormatString(RDFService.ModelSerializationFormat format) {
        switch (format) {
            case RDFXML: 
                return "RDF/XML";
            case N3: 
                return "N3";
            default: 
                throw new RuntimeException("unexpected format in getFormatString");
        }
    }
    
    public static RDFService getRDFService(VitroRequest vreq) {
        return getRDFServiceFactory(
                vreq.getSession().getServletContext()).getRDFService();
    }

    public static ResultSet sparqlSelectQuery(String query, RDFService rdfService) {
    	
    	ResultSet resultSet = null;
    	
        try {
            InputStream resultStream = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
            resultSet = ResultSetFactory.fromJSON(resultStream);
            return resultSet;
        } catch (RDFServiceException e) {        	
            log.error("error executing sparql select query: " + e.getMessage());
        }
        
        return resultSet;
    }    
}
