/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration;


public abstract class BaseEditSubmissionPreprocessorVTwo implements
        EditSubmissionVTwoPreprocessor {

    protected EditConfigurationVTwo editConfiguration;
    
    public BaseEditSubmissionPreprocessorVTwo(EditConfigurationVTwo editConfig) {
        editConfiguration = editConfig;
    }
    

}
