/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VisConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.BiboDocument;

public class UtilityFunctions {
	
	public static Map<String, Integer> getYearToPublicationCount(
			Set<BiboDocument> authorDocuments) {

		//List<Integer> publishedYears = new ArrayList<Integer>();

    	/*
    	 * Create a map from the year to number of publications. Use the BiboDocument's
    	 * parsedPublicationYear to populate the data.
    	 * */
    	Map<String, Integer> yearToPublicationCount = new TreeMap<String, Integer>();

    	for (BiboDocument curr : authorDocuments) {

    		/*
    		 * Increment the count because there is an entry already available for
    		 * that particular year.
    		 * */
    		String publicationYear;
    		if (curr.getPublicationYear() != null 
    				&& curr.getPublicationYear().length() != 0 
    				&& curr.getPublicationYear().trim().length() != 0) {
    			publicationYear = curr.getPublicationYear();
    		} else {
    			publicationYear = curr.getParsedPublicationYear();
    		}
    		
			if (yearToPublicationCount.containsKey(publicationYear)) {
    			yearToPublicationCount.put(publicationYear,
    									   yearToPublicationCount
    									   		.get(publicationYear) + 1);

    		} else {
    			yearToPublicationCount.put(publicationYear, 1);
    		}

//    		if (!parsedPublicationYear.equalsIgnoreCase(BiboDocument.DEFAULT_PUBLICATION_YEAR)) {
//    			publishedYears.add(Integer.parseInt(parsedPublicationYear));
//    		}

    	}

		return yearToPublicationCount;
	}
	
	/**
	 * Currently the approach for slugifying filenames is naive. In future if there is need, 
	 * we can write more sophisticated method.
	 * @param textToBeSlugified
	 * @return
	 */
	public static String slugify(String textToBeSlugified) {
		return textToBeSlugified.toLowerCase().replaceAll("[^a-zA-Z0-9-]", "-")
								.substring(0, VisConstants.MAX_NAME_TEXT_LENGTH);
	}

}
