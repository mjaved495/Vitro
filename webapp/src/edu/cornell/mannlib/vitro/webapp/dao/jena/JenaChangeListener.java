package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeListener;

/**
 * A ChangeListener that forwards events to a Jena ModelChangedListener 
 * @author bjl23
 *
 */
public class JenaChangeListener implements ChangeListener {

    private static final Log log = LogFactory.getLog(JenaChangeListener.class);
    
    private ModelChangedListener listener;
    private Model m = ModelFactory.createDefaultModel();
    
    public JenaChangeListener(ModelChangedListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void addedStatement(String serializedTriple, String graphURI) {
        listener.addedStatement(parseTriple(serializedTriple));
    }

    @Override
    public void removedStatement(String serializedTriple, String graphURI) {
        listener.removedStatement(parseTriple(serializedTriple));
    }

    @Override
    public void notifyEvent(String graphURI, Object event) {
        log.debug("event: " + event.getClass());
        listener.notifyEvent(m, event);
    }
    
    // TODO avoid overhead of Model
    private Statement parseTriple(String serializedTriple) {
        try {
            Model m = ModelFactory.createDefaultModel();
            m.read(new ByteArrayInputStream(
                    serializedTriple.getBytes("UTF-8")), null, "N3");
            StmtIterator sit = m.listStatements();
            if (!sit.hasNext()) {
                throw new RuntimeException("no triple parsed from change event");
            } else {
                Statement s = sit.nextStatement();
                if (sit.hasNext()) {
                    log.warn("More than one triple parsed from change event");
                }
                return s;
            }         
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }

}
