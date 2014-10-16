/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.search.documentBuilding;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.modules.searchEngine.SearchInputDocument;

/**
 * This interface represents an object that can add to a SearchInputDocument.
 */
public interface DocumentModifier {
	/**
	 * Use the rules contained within this class to modify this search document,
	 * according to the characteristics of this individual.
	 * 
	 * @param individual
	 *            The individual that is being indexed. Will not be null.
	 * @param doc
	 *            The document as it stands so far. Will not be null.
	 */
	public void modifyDocument(Individual individual, SearchInputDocument doc);

	// called to inform the DocumentModifier that the system is shutting down
	public void shutdown();

}
