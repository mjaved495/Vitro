/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.dao;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;

/**
 * Created by IntelliJ IDEA.
 * User: bdc34
 * Date: Apr 18, 2007
 * Time: 7:08:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ObjectPropertyStatementDao {
	
    void deleteObjectPropertyStatement(ObjectPropertyStatement objPropertyStmt);
    
    List<ObjectPropertyStatement> getObjectPropertyStatements(ObjectProperty objectProperty);
    
    List<ObjectPropertyStatement> getObjectPropertyStatements(ObjectProperty objectProperty, int startIndex, int endIndex);

    List<ObjectPropertyStatement> getObjectPropertyStatements(ObjectPropertyStatement objPropertyStmt);
    
    Individual fillExistingObjectPropertyStatements( Individual entity );

    int insertNewObjectPropertyStatement(ObjectPropertyStatement objPropertyStmt );
    
    public List<ObjectPropertyStatement> getObjectPropertyStatementsForIndividualByProperty(Individual subject, ObjectProperty property, String query);
}
