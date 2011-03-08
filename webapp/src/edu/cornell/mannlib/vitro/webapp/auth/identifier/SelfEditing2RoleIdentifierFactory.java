/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.identifier;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import edu.cornell.mannlib.vitro.webapp.auth.policy.RoleBasedPolicy.AuthRole;
import edu.cornell.mannlib.vitro.webapp.beans.User;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/**
 * Checks to see if the Individual associated with a SelfEditingIdentifier
 * has Admin, Curator or Editor rights.  This ignores black listing.
 * 
 * This should be added to the IdentifierFactory list after the
 * SelfEditingIdentiferFactory.
 * 
 * SelfEditing2RoleIdentifierSetup can be used in web.xml to add this class
 * to the IdentifierFactory list of a servlet context.
 * 
 * @author bdc34
 *
 */
public class SelfEditing2RoleIdentifierFactory implements
        IdentifierBundleFactory {

    @Override
	public IdentifierBundle getIdentifierBundle(ServletRequest request,
            HttpSession session, ServletContext context) {               
        IdentifierBundle whoToAuth = RequestIdentifiers.getIdBundleForRequest(request);
        WebappDaoFactory wdf = (WebappDaoFactory)context.getAttribute("webappDaoFactory");
            if( wdf == null ) 
                return whoToAuth;
            SelfEditingIdentifierFactory.SelfEditing selfEditing = 
            SelfEditingIdentifierFactory.getSelfEditingIdentifier(whoToAuth);
        if( selfEditing != null ){
            User user = wdf.getUserDao().getUserByURI(selfEditing.getIndividual().getURI());
            if( user != null){
                String role = user.getRoleURI();
                if("role/:50".equals(role)){
                    whoToAuth.add( AuthRole.DBA );
                }
                if("role/:4".equals(role)){
                    whoToAuth.add( AuthRole.CURATOR);
                }
                if("role/:3".equals(role)){
                    whoToAuth.add( AuthRole.EDITOR);
                }
                if("role/:2".equals(role)){
                    whoToAuth.add( AuthRole.USER );
                }                                                            
            }               
        }            
        return whoToAuth;
    }   
}