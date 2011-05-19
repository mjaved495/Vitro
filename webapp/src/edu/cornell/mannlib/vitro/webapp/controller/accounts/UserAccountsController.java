/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.accounts;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManageUserAccounts;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;

/**
 * Parcel out the different actions required of the UserAccounts GUI.
 */
public class UserAccountsController extends FreemarkerHttpServlet {
	private static final Log log = LogFactory
			.getLog(UserAccountsController.class);

	private static final String ACTION_ADD = "/add";
	private static final String ACTION_DELETE = "/delete";
	private static final String ACTION_EDIT = "/edit";

	@Override
	protected Actions requiredActions(VitroRequest vreq) {
		return new Actions(new ManageUserAccounts());
	}

	@Override
	protected ResponseValues processRequest(VitroRequest vreq) {
		if (log.isDebugEnabled()) {
			dumpRequestParameters(vreq);
		}

		String action = vreq.getPathInfo();
		log.debug("action = '" + action + "'");

		if (ACTION_ADD.equals(action)) {
			UserAccountsAddPage page = new UserAccountsAddPage(vreq);
			page.parseParametersAndValidate();
			if (page.isSubmit() && page.isValid()) {
				return addAccountAndShowList(vreq, page);
			} else {
				return page.showPage();
			}

		} else if (ACTION_EDIT.equals(action)) {
			return new UserAccountsEditPage(vreq).showPage();

		} else if (ACTION_DELETE.equals(action)) {
			UserAccountsDeleter deleter = new UserAccountsDeleter(vreq);
			Collection<String> deletedUris = deleter.delete();

			return new UserAccountsListPage(vreq)
					.showPageWithDeletions(deletedUris);

		} else {
			UserAccountsListPage page = new UserAccountsListPage(vreq);
			return page.showPage();
		}
	}

	private ResponseValues addAccountAndShowList(VitroRequest vreq,
			UserAccountsAddPage addPage) {
		UserAccount userAccount = addPage.createNewAccount();

		UserAccountsListPage listPage = new UserAccountsListPage(vreq);
		return listPage.showPageWithNewAccount(userAccount);
	}

}
