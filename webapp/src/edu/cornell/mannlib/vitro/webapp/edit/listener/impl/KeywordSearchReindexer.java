/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.listener.impl;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.listener.ChangeListener;
import edu.cornell.mannlib.vitro.webapp.search.indexing.IndexBuilder;

public class KeywordSearchReindexer implements ChangeListener {

    public void doInserted(Object newObj, EditProcessObject epo){
        IndexBuilder builder = (IndexBuilder)epo.getSession().getServletContext().getAttribute(IndexBuilder.class.getName());
        builder.doUpdateIndex();
    }

    public void doUpdated(Object oldObj, Object newObj, EditProcessObject epo){
        doInserted(newObj, epo);
    }

    public void doDeleted(Object oldObj, EditProcessObject epo){
        doInserted(null, epo);
    }

}