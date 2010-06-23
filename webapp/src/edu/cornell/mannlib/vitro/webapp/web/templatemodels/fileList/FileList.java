/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.fileList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.web.templatemodels.ViewObject;

public abstract class FileList extends ViewObject {
    
    protected List<String> list = null;
    private String themeDir = null;
    
    public FileList() {
        this.list = new ArrayList<String>();
    }
    
    public FileList(String themeDir) {
        this();
        this.themeDir = themeDir;
    }
    
    public FileList(List<String> list) {
        this.list = list;
    }
    
    public void add(String path) {
        list.add(getUrl(path));
    }
    
    public void addFromTheme(String path) {
        path = themeDir + getThemeSubDir() + path;
        add(path);
    }
    
    public String getTags() {
        String tags = "";
      
        Iterator<String> i = list.iterator();
        while (i.hasNext()) {
            tags += getTag(i.next());
        }
        return tags;
    }
    
    protected abstract String getThemeSubDir();
    protected abstract String getTag(String url);
    
}
