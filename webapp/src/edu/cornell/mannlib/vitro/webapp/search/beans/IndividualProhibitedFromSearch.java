package edu.cornell.mannlib.vitro.webapp.search.beans;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;

public class IndividualProhibitedFromSearch {
    
    protected OntModel fullModel;
    
    protected static Log log = LogFactory.getLog(IndividualProhibitedFromSearch.class);
    
    
    public IndividualProhibitedFromSearch( ServletContext context ){
        this.fullModel = ModelContext.getUnionOntModelSelector(context).getFullModel(); 
    }    
    
    public boolean isIndividualProhibited(String uri){
        if( uri == null || uri.isEmpty() )
            return true;
        
        boolean prohibited = false;
        try {
            fullModel.getLock().enterCriticalSection(Lock.READ);                               
            Query query = makeAskQueryForUri( uri );
            prohibited = QueryExecutionFactory.create( query, fullModel).execAsk();            
        } finally {
            fullModel.getLock().leaveCriticalSection();
        }
        if( prohibited )
            log.debug("prohibited " + uri);
        
        return prohibited;
    }
    
    private Query makeAskQueryForUri( String uri ){
        String queryString =
            "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> \n" +            
            "ASK { \n" +
            "    <"+uri+"> <" + RDF.type.getURI() + "> ?type . \n" +             
            "  FILTER ( \n" + 
            "     (  fn:starts-with( str(?type), \"" + VitroVocabulary.vitroURI + "\" ) \n" +
            "        && \n"+
            "        ! fn:starts-with( str(?type), \"" + VitroVocabulary.vitroURI + "Flag\" ) ) || \n" +            
            "     fn:starts-with( str(?type), \"" + VitroVocabulary.PUBLIC + "\" ) || \n" +
            "     str(?type) = \"" + OWL.ObjectProperty.getURI() + "\"  || \n" +
            "     str(?type) = \"" + OWL.DatatypeProperty.getURI() + "\"  || \n" +
            "     str(?type) = \"" + OWL.AnnotationProperty.getURI() + "\"  \n" +
            "   )\n" +
            "}" ;                
        return QueryFactory.create( queryString );
    }
}
