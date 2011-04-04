/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.dao.filtering;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.HiddenFromDisplayBelowRoleLevelFilter;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelectorImpl;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;

/**
 * Test the filtering of IndividualFiltering.
 * 
 * There are 6 levels of data hiding - public, selfEditor, editor, curator,
 * dbAdmin and nobody. We add a 7th case for data which has no explicit hiding
 * level - it should be treated as public.
 * 
 * The data files for this test describe an Individual with 7 data properties,
 * each with a different hiding level, and 49 object properties, showing all
 * combinations of hiding levels for the property and for the class of the
 * object.
 * 
 * There is a flag in HiddenFromDisplayBelowRoleLevelFilter which
 * enables/disables filtering based on the class of the object. These tests
 * should work regardless of how that flag is set.
 */
@RunWith(value = Parameterized.class)
public class IndividualFilteringTest extends AbstractTestClass {
	private static final Log log = LogFactory
			.getLog(IndividualFilteringTest.class);

	/**
	 * Where the ontology statements are stored for this test.
	 */
	private static final String TBOX_DATA_FILENAME = "IndividualFilteringTest-TBox.n3";

	/**
	 * Where the model statements are stored for this test.
	 */
	private static final String ABOX_DATA_FILENAME = "IndividualFilteringTest-Abox.n3";

	/**
	 * The domain where all of the objects and properties are defined.
	 */
	private static final String NS = "http://vivo.mydomain.edu/individual/";

	// ----------------------------------------------------------------------
	// Data elements and creating the model.
	// ----------------------------------------------------------------------

	/**
	 * The individual we are reading.
	 */
	private static final String INDIVIDUAL_URI = mydomain("bozo");

	/**
	 * Data properties to look for.
	 */
	private static final String OPEN_DATA_PROPERTY = mydomain("openDataProperty");
	private static final String PUBLIC_DATA_PROPERTY = mydomain("publicDataProperty");
	private static final String SELF_DATA_PROPERTY = mydomain("selfDataProperty");
	private static final String EDITOR_DATA_PROPERTY = mydomain("editorDataProperty");
	private static final String CURATOR_DATA_PROPERTY = mydomain("curatorDataProperty");
	private static final String DBA_DATA_PROPERTY = mydomain("dbaDataProperty");
	private static final String HIDDEN_DATA_PROPERTY = mydomain("hiddenDataProperty");
	private static final String[] DATA_PROPERTIES = { OPEN_DATA_PROPERTY,
			PUBLIC_DATA_PROPERTY, SELF_DATA_PROPERTY, EDITOR_DATA_PROPERTY,
			CURATOR_DATA_PROPERTY, DBA_DATA_PROPERTY, HIDDEN_DATA_PROPERTY };

	/**
	 * Object properties to look for.
	 */
	private static final String OPEN_OBJECT_PROPERTY = mydomain("openObjectProperty");
	private static final String PUBLIC_OBJECT_PROPERTY = mydomain("publicObjectProperty");
	private static final String SELF_OBJECT_PROPERTY = mydomain("selfObjectProperty");
	private static final String EDITOR_OBJECT_PROPERTY = mydomain("editorObjectProperty");
	private static final String CURATOR_OBJECT_PROPERTY = mydomain("curatorObjectProperty");
	private static final String DBA_OBJECT_PROPERTY = mydomain("dbaObjectProperty");
	private static final String HIDDEN_OBJECT_PROPERTY = mydomain("hiddenObjectProperty");
	private static final String[] OBJECT_PROPERTIES = { OPEN_OBJECT_PROPERTY,
			PUBLIC_OBJECT_PROPERTY, SELF_OBJECT_PROPERTY,
			EDITOR_OBJECT_PROPERTY, CURATOR_OBJECT_PROPERTY,
			DBA_OBJECT_PROPERTY, HIDDEN_OBJECT_PROPERTY };

	/**
	 * Objects to look for.
	 */
	private static final String OPEN_OBJECT = mydomain("openObject");
	private static final String PUBLIC_OBJECT = mydomain("publicObject");
	private static final String SELF_OBJECT = mydomain("selfObject");
	private static final String EDITOR_OBJECT = mydomain("editorObject");
	private static final String CURATOR_OBJECT = mydomain("curatorObject");
	private static final String DBA_OBJECT = mydomain("dbaObject");
	private static final String HIDDEN_OBJECT = mydomain("hiddenObject");
	private static final String[] OBJECTS = { OPEN_OBJECT, PUBLIC_OBJECT,
			SELF_OBJECT, EDITOR_OBJECT, CURATOR_OBJECT, DBA_OBJECT,
			HIDDEN_OBJECT };

	private static String mydomain(String localname) {
		return NS + localname;
	}

	private static OntModelSelectorImpl ontModelSelector;

	@BeforeClass
	public static void createTheModels() throws IOException {
		ontModelSelector = new OntModelSelectorImpl();
		ontModelSelector.setABoxModel(createAboxModel());
		ontModelSelector.setTBoxModel(createTboxModel());
		ontModelSelector.setFullModel(mergeModels(ontModelSelector));
	}

	private static OntModel createAboxModel() throws IOException {
		OntModel ontModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		readFileIntoModel(ontModel, ABOX_DATA_FILENAME, "N3");
		dumpModel("ABOX", ontModel);
		return ontModel;
	}

	private static OntModel createTboxModel() throws IOException {
		OntModel ontModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		readFileIntoModel(ontModel, TBOX_DATA_FILENAME, "N3");
		dumpModel("TBOX", ontModel);
		return ontModel;
	}

	private static OntModel mergeModels(OntModelSelectorImpl selector) {
		OntModel ontModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		ontModel.add(selector.getABoxModel());
		ontModel.add(selector.getTBoxModel());
		return ontModel;
	}

	private static void readFileIntoModel(OntModel ontModel, String filename,
			String format) throws IOException {
		InputStream stream = IndividualFilteringTest.class
				.getResourceAsStream(filename);
		ontModel.read(stream, null, format);
		stream.close();
	}

	// ----------------------------------------------------------------------
	// Set up
	// ----------------------------------------------------------------------

	/**
	 * For each set of tests, specify the login role level and the expected
	 * visible URIs of each type.
	 */
	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testDataList = new ArrayList<Object[]>();

		testDataList.add(new Object[] { RoleLevel.PUBLIC,
				set(OPEN_DATA_PROPERTY, PUBLIC_DATA_PROPERTY),
				set(OPEN_OBJECT_PROPERTY, PUBLIC_OBJECT_PROPERTY),
				set(OPEN_OBJECT, PUBLIC_OBJECT) });

		testDataList.add(new Object[] {
				RoleLevel.SELF,
				set(OPEN_DATA_PROPERTY, PUBLIC_DATA_PROPERTY,
						SELF_DATA_PROPERTY),
				set(OPEN_OBJECT_PROPERTY, PUBLIC_OBJECT_PROPERTY,
						SELF_OBJECT_PROPERTY),
				set(OPEN_OBJECT, PUBLIC_OBJECT, SELF_OBJECT) });

		testDataList.add(new Object[] {
				RoleLevel.EDITOR,
				set(OPEN_DATA_PROPERTY, PUBLIC_DATA_PROPERTY,
						SELF_DATA_PROPERTY, EDITOR_DATA_PROPERTY),
				set(OPEN_OBJECT_PROPERTY, PUBLIC_OBJECT_PROPERTY,
						SELF_OBJECT_PROPERTY, EDITOR_OBJECT_PROPERTY),
				set(OPEN_OBJECT, PUBLIC_OBJECT, SELF_OBJECT, EDITOR_OBJECT) });

		testDataList.add(new Object[] {
				RoleLevel.CURATOR,
				set(OPEN_DATA_PROPERTY, PUBLIC_DATA_PROPERTY,
						SELF_DATA_PROPERTY, EDITOR_DATA_PROPERTY,
						CURATOR_DATA_PROPERTY),
				set(OPEN_OBJECT_PROPERTY, PUBLIC_OBJECT_PROPERTY,
						SELF_OBJECT_PROPERTY, EDITOR_OBJECT_PROPERTY,
						CURATOR_OBJECT_PROPERTY),
				set(OPEN_OBJECT, PUBLIC_OBJECT, SELF_OBJECT, EDITOR_OBJECT,
						CURATOR_OBJECT) });

		testDataList.add(new Object[] {
				RoleLevel.DB_ADMIN,
				set(OPEN_DATA_PROPERTY, PUBLIC_DATA_PROPERTY,
						SELF_DATA_PROPERTY, EDITOR_DATA_PROPERTY,
						CURATOR_DATA_PROPERTY, DBA_DATA_PROPERTY),
				set(OPEN_OBJECT_PROPERTY, PUBLIC_OBJECT_PROPERTY,
						SELF_OBJECT_PROPERTY, EDITOR_OBJECT_PROPERTY,
						CURATOR_OBJECT_PROPERTY, DBA_OBJECT_PROPERTY),
				set(OPEN_OBJECT, PUBLIC_OBJECT, SELF_OBJECT, EDITOR_OBJECT,
						CURATOR_OBJECT, DBA_OBJECT) });

		return testDataList;
	}

	private static <T> Set<T> set(T... elements) {
		return new HashSet<T>(Arrays.<T> asList(elements));
	}

	private final RoleLevel loginRole;
	private final Set<String> expectedDataPropertyUris;
	private final Set<String> expectedObjectPropertyUris;
	private final Set<String> expectedObjectUris;

	private WebappDaoFactory wadf;
	private Individual ind;

	@Before
	public void createTheFilteredIndividual() {
		WebappDaoFactory rawWadf = new WebappDaoFactoryJena(ontModelSelector);
		wadf = new WebappDaoFactoryFiltering(rawWadf,
				new HiddenFromDisplayBelowRoleLevelFilter(loginRole, rawWadf));
		ind = wadf.getIndividualDao().getIndividualByURI(INDIVIDUAL_URI);
	}

	public IndividualFilteringTest(RoleLevel loginRole,
			Set<String> expectedDataPropertyUris,
			Set<String> expectedObjectPropertyUris,
			Set<String> expectedObjectUris) {
		this.loginRole = loginRole;
		this.expectedDataPropertyUris = expectedDataPropertyUris;
		this.expectedObjectPropertyUris = expectedObjectPropertyUris;
		this.expectedObjectUris = expectedObjectUris;
	}

	// ----------------------------------------------------------------------
	// The tests
	// ----------------------------------------------------------------------

	@Test
	public void testGetDataPropertyList() {
		assertEqualSets("data property list", expectedDataPropertyUris,
				extractDataPropUris(ind.getDataPropertyList()));
	}

	@Test
	public void testGetPopulatedDataPropertyList() {
		assertEqualSets("populated data property list",
				expectedDataPropertyUris,
				extractDataPropUris(ind.getPopulatedDataPropertyList()));
	}

	@Test
	public void testDataPropertyStatements() {
		assertEqualSets("data property statments", expectedDataPropertyUris,
				extractDataPropStmtUris(ind.getDataPropertyStatements()));
	}

	@Test
	public void testDataPropertyStatements2() {
		for (String propUri : DATA_PROPERTIES) {
			Set<String> uris = extractDataPropStmtUris(ind
					.getDataPropertyStatements(propUri));
			if (expectedDataPropertyUris.contains(propUri)) {
				assertEquals("selected data property: " + propUri,
						Collections.singleton(propUri), uris);
			} else {
				assertEquals("selected data property: " + propUri,
						Collections.emptySet(), uris);
			}
		}
	}

	@Test
	public void testDataPropertyMap() {
		assertEqualSets("data property map", expectedDataPropertyUris, ind
				.getDataPropertyMap().keySet());
	}

	@Test
	public void testObjectPropertyList() {
		assertEqualSets("object properties", expectedObjectPropertyUris,
				extractObjectPropUris(ind.getObjectPropertyList()));
	}

	@Test
	public void testPopulatedObjectPropertyList() {
		assertEqualSets("populated object properties",
				expectedObjectPropertyUris,
				extractObjectPropUris(ind.getPopulatedObjectPropertyList()));
	}

	/**
	 * We expect to see an object property statment for each permitted property
	 * and each permitted object. If class filtering is disabled, then all
	 * objects are permitted.
	 */
	@Test
	public void testObjectPropertyStatements() {
		Collection<String> expectedObjects = filteringOnClasses() ? expectedObjectUris
				: Arrays.asList(OBJECTS);
		assertExpectedObjectPropertyStatements("object property statements",
				expectedObjectPropertyUris, expectedObjects,
				ind.getObjectPropertyStatements());
	}

	/**
	 * We expect to see an object property statment for each permitted property
	 * and each permitted object. If class filtering is disabled, then all
	 * objects are permitted.
	 */
	@Test
	public void testObjectPropertyStatements2() {
		Collection<String> expectedObjects = filteringOnClasses() ? expectedObjectUris
				: Arrays.asList(OBJECTS);
		for (String propUri : OBJECT_PROPERTIES) {
			if (expectedObjectPropertyUris.contains(propUri)) {
				assertExpectedObjectPropertyStatements(
						"object property statements for " + propUri,
						Collections.singleton(propUri), expectedObjects,
						ind.getObjectPropertyStatements(propUri));
			} else {
				assertExpectedObjectPropertyStatements(
						"object property statements for " + propUri,
						Collections.<String> emptySet(), expectedObjects,
						ind.getObjectPropertyStatements(propUri));
			}
		}
	}

	@Test
	public void testObjectPropertyMap() {
		assertEqualSets("object property map", expectedObjectPropertyUris, ind
				.getObjectPropertyMap().keySet());
	}

	// ----------------------------------------------------------------------
	// helper methods
	// ----------------------------------------------------------------------

	/**
	 * Are we filtering on VClasses? Use reflection to read that protected
	 * constant.
	 */
	private boolean filteringOnClasses() {
		try {
			Class<?> clazz = HiddenFromDisplayBelowRoleLevelFilter.class;
			Field field = clazz
					.getDeclaredField("FILTER_ON_INDIVIDUAL_VCLASSES");
			field.setAccessible(true);
			return (Boolean) field.get(null);
		} catch (Exception e) {
			fail("Can't decide on class filtering: " + e);
			return false;
		}
	}

	/** Get the URIs from these DataProperties */
	private Set<String> extractDataPropUris(
			Collection<DataProperty> dataProperties) {
		Set<String> uris = new TreeSet<String>();
		if (dataProperties != null) {
			for (DataProperty dp : dataProperties) {
				uris.add(dp.getURI());
			}
		}
		return uris;
	}

	/** Get the URIs from these DataPropertyStatements */
	private Set<String> extractDataPropStmtUris(
			Collection<DataPropertyStatement> dataPropertyStatements) {
		Set<String> uris = new TreeSet<String>();
		if (dataPropertyStatements != null) {
			for (DataPropertyStatement dps : dataPropertyStatements) {
				uris.add(dps.getDatapropURI());
			}
		}
		return uris;
	}

	/** Get the URIs from these ObjectProperties */
	private Set<String> extractObjectPropUris(
			Collection<ObjectProperty> objectProperties) {
		Set<String> uris = new TreeSet<String>();
		if (objectProperties != null) {
			for (ObjectProperty op : objectProperties) {
				uris.add(op.getURI());
			}
		}
		return uris;
	}

	/**
	 * We expect one statement for each combination of expected object
	 * properties and expected object.
	 */
	private void assertExpectedObjectPropertyStatements(String label,
			Collection<String> expectedProperties,
			Collection<String> expectedObjects,
			List<ObjectPropertyStatement> actualStmts) {
		Set<ObjectPropertyStatementUris> actualStmtUris = new HashSet<ObjectPropertyStatementUris>();
		for (ObjectPropertyStatement actualStmt : actualStmts) {
			actualStmtUris.add(new ObjectPropertyStatementUris(actualStmt));
		}

		Set<ObjectPropertyStatementUris> expectedStmtUris = new HashSet<ObjectPropertyStatementUris>();
		for (String propertyUri : expectedProperties) {
			for (String objectUri : expectedObjects) {
				expectedStmtUris.add(new ObjectPropertyStatementUris(ind
						.getURI(), propertyUri, objectUri));
			}
		}

		assertEqualSets(label, expectedStmtUris, actualStmtUris);
	}

	private static void dumpModel(String label, OntModel ontModel) {
		if (log.isDebugEnabled()) {
			log.debug("Dumping the " + label + " model:");
			StmtIterator stmtIt = ontModel.listStatements();
			while (stmtIt.hasNext()) {
				Statement stmt = stmtIt.next();
				log.debug("stmt: " + stmt);
			}
		}
	}

	/**
	 * Capture the essence of an ObjectPropertyStatement for comparison and
	 * display.
	 */
	private static class ObjectPropertyStatementUris implements
			Comparable<ObjectPropertyStatementUris> {
		private final String subjectUri;
		private final String propertyUri;
		private final String objectUri;

		ObjectPropertyStatementUris(ObjectPropertyStatement stmt) {
			this.subjectUri = stmt.getSubjectURI();
			this.propertyUri = stmt.getPropertyURI();
			this.objectUri = stmt.getObjectURI();
		}

		public ObjectPropertyStatementUris(String subjectUri,
				String propertyUri, String objectUri) {
			this.subjectUri = subjectUri;
			this.propertyUri = propertyUri;
			this.objectUri = objectUri;
		}

		@Override
		public int compareTo(ObjectPropertyStatementUris that) {
			int first = this.subjectUri.compareTo(that.subjectUri);
			if (first != 0) {
				return first;
			}

			int second = this.propertyUri.compareTo(that.propertyUri);
			if (second != 0) {
				return second;
			}

			int third = this.objectUri.compareTo(that.objectUri);
			return third;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ObjectPropertyStatementUris)) {
				return false;
			}
			ObjectPropertyStatementUris that = (ObjectPropertyStatementUris) o;
			return this.compareTo(that) == 0;
		}

		@Override
		public int hashCode() {
			return subjectUri.hashCode() ^ propertyUri.hashCode()
					^ objectUri.hashCode();
		}

		@Override
		public String toString() {
			return "[" + subjectUri + " ==> " + propertyUri + " ==> "
					+ objectUri + "]";
		}

	}
}
