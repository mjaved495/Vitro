package edu.cornell.mannlib.vitro.webapp.controller.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.utils.StringUtils;
import freemarker.template.Configuration;

public class PrimitiveRdfDelete extends PrimitiveRdfEdit {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(PrimitiveRdfDelete.class);  

    protected void processRequest(VitroRequest vreq, HttpServletResponse response) {
     
        String uriToDelete = vreq.getParameter("deletion");
        if (StringUtils.isEmpty(uriToDelete)) {
            doError(response, "No individual specified for deletion", 500);
            return;
        }
        
        // Check permissions
        // The permission-checking code should be inherited from superclass
        boolean hasPermission = true;
        
        if( !hasPermission ){
            //if not okay, send error message
            doError(response,"Insufficent permissions.",HttpStatus.SC_UNAUTHORIZED);
            return;
        }

        WebappDaoFactory wdf = vreq.getWebappDaoFactory();
        IndividualDao idao = wdf.getIndividualDao();
        int result = idao.deleteIndividual(uriToDelete);
        if (result == 1) {
            doError(response, "Error deleting individual", 500);
        }
    }

}
