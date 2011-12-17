/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.auth.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.Permission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.SimpleRequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;

/**
 * A class of simple permissions. Each instance holds a RequestedAction, and
 * will only authorize that RequestedAction (or one with the same URI).
 */
public class SimplePermission implements Permission {
	private static final Log log = LogFactory.getLog(SimplePermission.class);

	private static final String NAMESPACE = "java://"
			+ SimplePermission.class.getName() + "#";

	private static final Map<String, SimplePermission> allInstances = new HashMap<String, SimplePermission>();

	public static final SimplePermission MANAGE_MENUS = new SimplePermission(
			"ManageMenus");

	public static List<SimplePermission> getAllInstances() {
		return new ArrayList<SimplePermission>(allInstances.values());
	}

	private final String localName;
	private final String uri;
	public final RequestedAction ACTION;
	public final Actions ACTIONS;

	public SimplePermission(String localName) {
		if (localName == null) {
			throw new NullPointerException("name may not be null.");
		}

		this.localName = localName;
		this.uri = NAMESPACE + localName;

		this.ACTION = new SimpleRequestedAction(localName);
		this.ACTIONS = new Actions(this.ACTION);

		if (allInstances.containsKey(this.uri)) {
			throw new IllegalStateException("A SimplePermission named '"
					+ this.uri + "' already exists.");
		}
		allInstances.put(uri, this);
	}

	@Override
	public String getLocalName() {
		return this.localName;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

	@Override
	public String getUri() {
		return NAMESPACE + this.localName;
	}

	@Override
	public boolean isAuthorized(RequestedAction whatToAuth) {
		if (whatToAuth != null) {
			if (ACTION.getURI().equals(whatToAuth.getURI())) {
				log.debug(this + " authorizes " + whatToAuth);
				return true;
			}
		}
		log.debug(this + " does not authorize " + whatToAuth);
		return false;
	}

	@Override
	public String toString() {
		return "SimplePermission['" + localName + "']";
	}

}
