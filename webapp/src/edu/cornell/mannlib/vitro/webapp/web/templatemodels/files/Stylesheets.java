/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.files;

public class Stylesheets extends Files {
 
    protected static final String THEME_SUBDIR = "/css";
    
    public Stylesheets() { }
    
    public Stylesheets(String themeDir) {
        super(themeDir);
    }
    
    protected String getTag(String url) {
        return "<link rel=\"stylesheet\" href=\"" + url + "\" />";
    }
    
    protected String getThemeSubDir() {
        return THEME_SUBDIR;
    }

}
