/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.search.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.BooleanQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseBasicAjaxControllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.VitroAjaxController;
import edu.cornell.mannlib.vitro.webapp.search.lucene.Entity2LuceneDoc.VitroLuceneTermNames;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;

/**
 * AutocompleteController generates autocomplete content
 * through a Solr search. 
 */

// RY Rename to AutocompleteController once the transition to Solr is complete.
public class SolrAutocompleteController extends VitroAjaxController {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(SolrAutocompleteController.class);
    
    //private static final String TEMPLATE_DEFAULT = "autocompleteResults.ftl";
    
    private static final String PARAM_QUERY = "term";
    private static final String PARAM_RDFTYPE = "type";
    
    String NORESULT_MSG = "";    
    private static final int DEFAULT_MAX_HIT_COUNT = 1000; 

    public static final int MAX_QUERY_LENGTH = 500;
    
    @Override
    protected Actions requiredActions(VitroRequest vreq) {
    	return new Actions(new UseBasicAjaxControllers());
    }
    
    @Override
    protected void doRequest(VitroRequest vreq, HttpServletResponse response)
        throws IOException, ServletException {
        
        try {
            
            String qtxt = vreq.getParameter(PARAM_QUERY);
            
            SolrQuery query = getQuery(qtxt, vreq);             
            if (query == null ) {
                log.debug("query for '" + qtxt +"' is null.");
                doNoQuery(response);
                return;
            }
            log.debug("query for '" + qtxt +"' is " + query.toString());
                        
            SolrServer solr = SolrSetup.getSolrServer(getServletContext());
            QueryResponse queryResponse = solr.query(query);

            if ( queryResponse == null) {
                log.error("Query response for a search was null");                
                doNoSearchResults(response);
                return;
            }
            
            SolrDocumentList docs = queryResponse.getResults();

            if ( docs == null) {
                log.error("Docs for a search was null");                
                doNoSearchResults(response);
                return;
            }
            
            long hitCount = docs.getNumFound();
            log.debug("Number of hits = " + hitCount);
            if ( hitCount < 1 ) {                
                doNoSearchResults(response);
                return;
            }            

            List<SearchResult> results = new ArrayList<SearchResult>();
            for (SolrDocument doc : docs) {
                try{                                      
                    String uri = doc.get(VitroLuceneTermNames.URI).toString();
                    String name = doc.get(VitroLuceneTermNames.NAME_RAW).toString();
                    SearchResult result = new SearchResult(name, uri);
                    results.add(result);
                } catch(Exception e){
                    log.error("problem getting usable Individuals from search " +
                            "hits" + e.getMessage());
                }
            }   

            Collections.sort(results);
            
            // map.put("results", results);
            // writeTemplate(TEMPLATE_DEFAULT, map, config, vreq, response);
            
            JSONArray jsonArray = new JSONArray();
            for (SearchResult result : results) {
                jsonArray.put(result.toMap());
            }
            response.getWriter().write(jsonArray.toString());
        
        } catch (Throwable e) {
            log.error(e, e);            
            doSearchError(response);
        }
    }

    private SolrQuery getQuery(String querystr, VitroRequest vreq) {
       
        if ( querystr == null) {
            log.error("There was no parameter '"+ PARAM_QUERY            
                +"' in the request.");                
            return null;
        } else if( querystr.length() > MAX_QUERY_LENGTH ) {
            log.debug("The search was too long. The maximum " +
                    "query length is " + MAX_QUERY_LENGTH );
            return null;
        }
                   
        SolrQuery query = new SolrQuery();
        query = query.setStart(0);
        query = query.setRows(DEFAULT_MAX_HIT_COUNT);  
        
        query = setNameQuery(query, querystr, vreq);
        
        // Filter by type
        String typeParam = (String) vreq.getParameter(PARAM_RDFTYPE);
        if (typeParam != null) {
            query = query.addFilterQuery(VitroLuceneTermNames.RDFTYPE + ":\"" + typeParam + "\"");
        }   
        
        // Set the fields to retrieve **** RY
        // query = query.setFields( ... );

        return query;
    }
    
    private SolrQuery setNameQuery(SolrQuery query, String querystr, HttpServletRequest request) {

        String tokenizeParam = (String) request.getParameter("tokenize"); 
        boolean tokenize = "true".equals(tokenizeParam);
        
        // Note: Stemming is only relevant if we are tokenizing: an untokenized name
        // query will not be stemmed. So we don't look at the stem parameter until we get to
        // setTokenizedNameQuery().
        if (tokenize) {
            return setTokenizedNameQuery(query, querystr, request);
        } else {
            return setUntokenizedNameQuery(query, querystr);
        }
    }
    
    private SolrQuery setTokenizedNameQuery(SolrQuery query, String querystr, HttpServletRequest request) {
 
        String stemParam = (String) request.getParameter("stem"); 
        boolean stem = "true".equals(stemParam);
        String termName = stem ? VitroLuceneTermNames.NAME_STEMMED : VitroLuceneTermNames.NAME_UNSTEMMED;

        BooleanQuery boolQuery = new BooleanQuery();
        
//        // Use the query parser to analyze the search term the same way the indexed text was analyzed.
//        // For example, text is lowercased, and function words are stripped out.
//        QueryParser parser = getQueryParser(termName);
//        
//        // The wildcard query doesn't play well with stemming. Query term name:tales* doesn't match
//        // "tales", which is indexed as "tale", while query term name:tales does. Obviously we need 
//        // the wildcard for name:tal*, so the only way to get them all to match is use a disjunction 
//        // of wildcard and non-wildcard queries. The query will look have only an implicit disjunction
//        // operator: e.g., +(name:tales name:tales*)
//        try {
//            log.debug("Adding non-wildcard query for " + querystr);
//            Query query = parser.parse(querystr);
//            boolQuery.add(query, BooleanClause.Occur.SHOULD);
//
//            // Prevent ParseException here when adding * after a space.
//            // If there's a space at the end, we don't need the wildcard query.
//            if (! querystr.endsWith(" ")) {
//                log.debug("Adding wildcard query for " + querystr);
//                Query wildcardQuery = parser.parse(querystr + "*");            
//                boolQuery.add(wildcardQuery, BooleanClause.Occur.SHOULD);
//            }
//            
//            log.debug("Name query is: " + boolQuery.toString());
//        } catch (ParseException e) {
//            log.warn(e, e);
//        }
       
        return query;
    }

    private SolrQuery setUntokenizedNameQuery(SolrQuery query, String querystr) {
        
        //querystr = querystr.toLowerCase();
        querystr += "*";
        query = query.setQuery(querystr);
        // *** It's the df parameter that sets the field to search
        //String field = VitroLuceneTermNames.LABEL_LOWERCASE; 
        
        return query;
    }
            
    private void doNoQuery(HttpServletResponse response) throws IOException  {
        // For now, we are not sending an error message back to the client because 
        // with the default autocomplete configuration it chokes.
        doNoSearchResults(response);
    }

    private void doSearchError(HttpServletResponse response) throws IOException {
        // For now, we are not sending an error message back to the client because 
        // with the default autocomplete configuration it chokes.
        doNoSearchResults(response);
    }

    private void doNoSearchResults(HttpServletResponse response) throws IOException {
        response.getWriter().write("[]");
    }
    
    public class SearchResult implements Comparable<Object> {
        private String label;
        private String uri;
        
        SearchResult(String label, String uri) {
            this.label = label;
            this.uri = uri;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getJsonLabel() {
            return JSONObject.quote(label);
        }
        
        public String getUri() {
            return uri;
        }
        
        public String getJsonUri() {
            return JSONObject.quote(uri);
        }
        
        Map<String, String> toMap() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("label", label);
            map.put("uri", uri);
            return map;
        }

        public int compareTo(Object o) throws ClassCastException {
            if ( !(o instanceof SearchResult) ) {
                throw new ClassCastException("Error in SearchResult.compareTo(): expected SearchResult object.");
            }
            SearchResult sr = (SearchResult) o;
            return label.compareToIgnoreCase(sr.getLabel());
        }
    }

}
