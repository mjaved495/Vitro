/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import javax.servlet.ServletException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import stubs.javax.servlet.http.HttpServletRequestStub;
import stubs.javax.servlet.http.HttpServletResponseStub;
import edu.cornell.mannlib.vitro.testing.AbstractTestClass;

/**
 * Tests on various methods in the class.
 * @author Eliza Chan (elc2013@med.cornell.edu)
 *
 */
public class SolrJsonReconcileServletTest extends AbstractTestClass {

	private HttpServletRequestStub request;
	private HttpServletResponseStub response;
	private SolrJsonReconcileServlet reconcile;

	@Before
	public void setup() throws Exception {
		request = new HttpServletRequestStub();
		request.setRequestUrl(new URL("http://vivo.this.that/reconcile"));
		request.setMethod("POST");
		response = new HttpServletResponseStub();
		reconcile = new SolrJsonReconcileServlet();
	}
	
	@Test
	public void getMetadata() {
		int serverPort = 8080;
		String defaultNamespace = "http://vivo.this.that/individual/";
		String defaultTypeList = null;
		String serverName = null;
		String schemaSpaceOutput = null;
		JSONObject jsonResult = null;
		try {
			jsonResult = reconcile.getMetadata(request, response, defaultNamespace, defaultTypeList, serverName, serverPort);
			schemaSpaceOutput = jsonResult.getString("schemaSpace");
		} catch (ServletException e) {
			System.err.println("SolrJsonReconcileServletTest ServletException: " + e);
		} catch (JSONException e) {
			System.err.println("SolrJsonReconcileServletTest JSONException: " + e);
		}
		assertNotNull("output should not be null", jsonResult);
		assertEquals("schemaSpaceOutput", defaultNamespace, schemaSpaceOutput);
	}
}
