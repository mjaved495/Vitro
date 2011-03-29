/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package stubs.edu.cornell.mannlib.vitro.webapp.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Keyword;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;

/**
 * A minimal implementation of the IndividualDao.
 * 
 * I have only implemented the methods that I needed. Feel free to implement
 * others.
 */
public class IndividualDaoStub implements IndividualDao {
	// ----------------------------------------------------------------------
	// Stub infrastructure
	// ----------------------------------------------------------------------

	private final Map<String, Individual> indMap = new HashMap<String, Individual>();

	public void addIndividual(Individual individual) {
		if (individual == null) {
			throw new NullPointerException("individual may not be null.");
		}
		
		String uri = individual.getURI();
		if (uri == null) {
			throw new NullPointerException("uri may not be null.");
		}
		
		indMap.put(uri, individual);
	}

	// ----------------------------------------------------------------------
	// Stub methods
	// ----------------------------------------------------------------------

	@Override
	public Individual getIndividualByURI(String individualURI) {
		return indMap.get(individualURI);
	}

	// ----------------------------------------------------------------------
	// Un-implemented methods
	// ----------------------------------------------------------------------

	@Override
	public Collection<DataPropertyStatement> getExternalIds(String individualURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getExternalIds() not implemented.");
	}

	@Override
	public Collection<DataPropertyStatement> getExternalIds(
			String individualURI, String dataPropertyURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getExternalIds() not implemented.");
	}

	@Override
	public void addVClass(String individualURI, String vclassURI) {
		throw new RuntimeException(
				"IndividualDaoStub.addVClass() not implemented.");
	}

	@Override
	public void removeVClass(String individualURI, String vclassURI) {
		throw new RuntimeException(
				"IndividualDaoStub.removeVClass() not implemented.");
	}

	@Override
	public List<Individual> getIndividualsByVClass(VClass vclass) {
		throw new RuntimeException(
				"IndividualDaoStub.getIndividualsByVClass() not implemented.");
	}

	@Override
	public List<Individual> getIndividualsByVClassURI(String vclassURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getIndividualsByVClassURI() not implemented.");
	}

	@Override
	public List<Individual> getIndividualsByVClassURI(String vclassURI,
			int offset, int quantity) {
		throw new RuntimeException(
				"IndividualDaoStub.getIndividualsByVClassURI() not implemented.");
	}

	@Override
	public String insertNewIndividual(Individual individual)
			throws InsertException {
		throw new RuntimeException(
				"IndividualDaoStub.insertNewIndividual() not implemented.");
	}

	@Override
	public int updateIndividual(Individual individual) {
		throw new RuntimeException(
				"IndividualDaoStub.updateIndividual() not implemented.");
	}

	@Override
	public int deleteIndividual(String individualURI) {
		throw new RuntimeException(
				"IndividualDaoStub.deleteIndividual() not implemented.");
	}

	@Override
	public int deleteIndividual(Individual individual) {
		throw new RuntimeException(
				"IndividualDaoStub.deleteIndividual() not implemented.");
	}

	@Override
	public void markModified(Individual individual) {
		throw new RuntimeException(
				"IndividualDaoStub.markModified() not implemented.");
	}

	@Override
	public Iterator<Individual> getAllOfThisTypeIterator() {
		throw new RuntimeException(
				"IndividualDaoStub.getAllOfThisTypeIterator() not implemented.");
	}

	@Override
	public Iterator<Individual> getAllOfThisVClassIterator(String vClassURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getAllOfThisVClassIterator() not implemented.");
	}

	@Override
	public Iterator<Individual> getUpdatedSinceIterator(long updatedSince) {
		throw new RuntimeException(
				"IndividualDaoStub.getUpdatedSinceIterator() not implemented.");
	}

	@Override
	public int getCountOfIndividualsInVClass(String vclassURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getCountOfIndividualsInVClass() not implemented.");
	}

	@Override
	public boolean isIndividualOfClass(String vclassURI, String indURI) {
		throw new RuntimeException(
				"IndividualDaoStub.isIndividualOfClass() not implemented.");
	}

	@Override
	public List<Individual> getIndividualsByDataProperty(
			String dataPropertyUri, String value) {
		throw new RuntimeException(
				"IndividualDaoStub.getIndividualsByDataProperty() not implemented.");
	}

	@Override
	public List<Individual> getIndividualsByDataProperty(
			String dataPropertyUri, String value, String datatypeUri,
			String lang) {
		throw new RuntimeException(
				"IndividualDaoStub.getIndividualsByDataProperty() not implemented.");
	}

	@Override
	public void fillVClassForIndividual(Individual individual) {
		throw new RuntimeException(
				"IndividualDaoStub.fillVClassForIndividual() not implemented.");
	}

	@Override
	public List<String> monikers(String vclassURI) {
		throw new RuntimeException(
				"IndividualDaoStub.monikers() not implemented.");
	}

	@Override
	public List<String> getKeywordsForIndividual(String individualURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getKeywordsForIndividual() not implemented.");
	}

	@Override
	public List<String> getKeywordsForIndividualByMode(String individualURI,
			String modeStr) {
		throw new RuntimeException(
				"IndividualDaoStub.getKeywordsForIndividualByMode() not implemented.");
	}

	@Override
	public List<Keyword> getKeywordObjectsForIndividual(String individualURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getKeywordObjectsForIndividual() not implemented.");
	}

	@Override
	public String getIndividualURIFromNetId(String netIdStr,
			String netidMatchingPropertyUri) {
		throw new RuntimeException(
				"IndividualDaoStub.getIndividualURIFromNetId() not implemented.");
	}

	@Override
	public String getNetId(String entityURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getNetId() not implemented.");
	}

	@Override
	public String getStatus(String entityURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getStatus() not implemented.");
	}

	@Override
	public String getUnusedURI(Individual individual) throws InsertException {
		throw new RuntimeException(
				"IndividualDaoStub.getUnusedURI() not implemented.");
	}

	@Override
	public Individual getIndividualByExternalId(int externalIdType,
			String externalIdValue) {
		throw new RuntimeException(
				"IndividualDaoStub.getIndividualByExternalId() not implemented.");
	}

	@Override
	public Individual getIndividualByExternalId(int externalIdType,
			String externalIdValue, String vClassURI) {
		throw new RuntimeException(
				"IndividualDaoStub.getIndividualByExternalId() not implemented.");
	}

}
