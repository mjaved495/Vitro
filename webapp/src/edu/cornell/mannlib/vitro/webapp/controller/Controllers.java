/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class to store urls for different controllers.
 * This is just a place to stick the constants that are urls
 * to servlets and jsps.
 *
 * Maybe it should be pulled out of a properties file?
 * @author bdc34
 *
 */

public class Controllers {
    
    // Servlet urls
    
    public static final String ENTITY = "/entity";
    public static final String ENTITY_PROP_LIST = "/entityPropList";
    public static final String ENTITY_LIST = "/EntityList";

    public static final String BROWSE_CONTROLLER = "browsecontroller";
    public static final String RETRY_URL = "editForm";
    public static final String TAB_ENTITIES = "/TabEntitiesController";  
    
    public static final String ABOUT = "/about";
    
    public static final String SITE_ADMIN = "/siteAdmin";
    public static final String LOGIN = "/siteAdmin";
    public static final String LOGOUT = "/siteAdmin";
    
    public static final String EXPORT_RDF = "/export";
    
    public static final String CONTACT_URL = "/comments";
    public static final String TERMS_OF_USE_URL = "/termsOfUse";
    public static final String BROWSE = "/browse";
    public static final String INDIVIDUAL_LIST_URL = "/entitylist"; // will change to individuallist

    // jsps go here:
    
    public static final String EMPTY = "/empty.jsp";
    public static final String TAB = "/index.jsp";

    public static final String LOGIN_JSP = "/login_process.jsp";
    public static final String LOGOUT_JSP = "/login_process.jsp";
    
    public static final String BASIC_JSP = "/templates/page/basicPage.jsp";
    public static final String DEBUG_JSP = "/templates/page/debug.jsp";
    public static final Object BODY_MSG = "/templates/page/bodyMsg.jsp";
    
    public static final String ENTITY_JSP = "/templates/entity/entityBasic.jsp";
    public static final String ENTITY_PROP_LIST_JSP = "templates/entity/entityPropsList.jsp";
    public static final String ENTITY_DATAPROP_LIST_JSP = "templates/entity/entityDatapropsList.jsp";
    public static final String ENTITY_MERGED_PROP_LIST_GROUPED_JSP = "templates/entity/entityMergedPropsList.jsp";
    public static final String DASHBOARD_PROP_LIST_JSP = "edit/dashboardPropsList.jsp";
    public static final String ENTITY_MERGED_PROP_LIST_UNGROUPED_JSP = "templates/entity/entityMergedPropsListUngrouped.jsp";
    
    public static final String ENTITY_KEYWORDS_LIST_JSP = "templates/entity/entityKeywordsList.jsp";

    public static final String ENTITY_EDITABLE_JSP = "templates/entity/entityEditable.jsp";
    public static final String ENTITY_EDITABLE_PROP_LIST_JSP = "templates/entity/entityEditablePropsList.jsp";

    public static final String ENTITY_LIST_JSP = "templates/entity/entityList.jsp";
    public static final String ENTITY_LIST_FOR_TABS_JSP = "templates/entity/entityListForTabs.jsp";
    public static final String TAB_ENTITIES_LIST_GALLERY_JSP = "templates/entity/entityListForGalleryTab.jsp";
    public static final String ENTITY_NOT_FOUND_JSP = "templates/error/entityNotFound.jsp";

    public static final String TAB_BASIC_JSP = "/templates/tabs/tabBasic.jsp";
    public static final String TAB_PRIMARY_JSP = "/templates/tabs/tabprimary.jsp";

    public static final String ALPHA_INDEX_JSP = "/templates/alpha/alphaIndex.jsp";

    public static final String SEARCH_URL = "/search";
    public static final String SEARCH_BASIC_JSP = "/templates/search/searchBasic.jsp";
    public static final String SEARCH_PAGED_JSP = "/templates/search/searchPaged.jsp";
    public static final String SEARCH_FAILED_JSP = "/templates/search/searchFailed.jsp";
    public static final String SEARCH_GROUP_JSP = "/templates/search/searchGroup.jsp";
    public static final Object SEARCH_FORM_JSP = "/templates/search/searchForm.jsp";

    public static final String BROWSE_GROUP_JSP = "/templates/browse/browseGroup.jsp";

    public static final String HORIZONTAL_JSP = "/horizontal.jsp";
    public static final String VERTICAL_JSP = "/templates/edit/fetch/vertical.jsp";
    
    public static final String CHECK_DATATYPE_PROPERTIES = "/checkDatatypeProperties.jsp";
    public static final String EXPORT_SELECTION_JSP = "/jenaIngest/exportSelection.jsp";

    public static final String VCLASS_RETRY_URL = "vclass_retry";

    public static final String TOGGLE_SCRIPT_ELEMENT = "<script language='JavaScript' type='text/javascript' src='js/toggle.js'></script>";

    public static final Object SEARCH_ERROR_JSP = "/search_error.jsp";

    
    //public static final String TAB_ENTITIES_LIST_JSP = "templates/tab/tabEntities.jsp";

    private static List letters = null;
    public static List getLetters() {
        //there must be a better place to put this.
        if (Controllers.letters == null) {
            char c[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            Controllers.letters = new ArrayList(c.length);
            for (int i = 0; i < c.length; i++) {
                letters.add("" + c[i]);
            }
        }
        return Controllers.letters;
    }
}
