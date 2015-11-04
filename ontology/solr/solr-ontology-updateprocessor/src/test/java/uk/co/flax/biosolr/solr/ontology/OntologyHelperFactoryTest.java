/**
 * Copyright (c) 2015 Lemur Consulting Ltd.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.flax.biosolr.solr.ontology;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.junit.Test;

import uk.co.flax.biosolr.solr.ontology.ols.OLSOntologyHelper;
import uk.co.flax.biosolr.solr.ontology.owl.OWLOntologyHelper;

/**
 * Unit tests for the OntologyHelperFactory.
 *
 * Created by mlp on 20/10/15.
 * @author mlp
 */
public class OntologyHelperFactoryTest {

	public static final String TEST_ONTOLOGY = "ontologyUpdate/owl/test.owl";
	public static final String COMPLETE_PROPFILE_PATH = "ontologyUpdate/config/ontology_1.properties";

	public static String getFilePath(String file) throws URISyntaxException {
		URL fileUrl = OntologyHelperFactoryTest.class.getClassLoader().getResource(file);
		if (fileUrl == null) {
			throw new URISyntaxException(file, "Cannot build file URL");
		}
		return new File(fileUrl.toURI()).getAbsolutePath();
	}

	@Test(expected = org.apache.solr.common.SolrException.class)
	public void construct_noParameters() throws Exception {
		SolrParams params = new MapSolrParams(Collections.emptyMap());
		new OntologyHelperFactory(params);
	}

	@Test(expected = org.apache.solr.common.SolrException.class)
	public void construct_missingConfigFile() throws Exception {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(OntologyHelperFactory.CONFIG_FILE_PARAM, "blah");
		new OntologyHelperFactory(new MapSolrParams(paramMap));
	}

	@Test(expected = org.apache.solr.common.SolrException.class)
	public void construct_missingOLSOntology() throws Exception {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(OntologyHelperFactory.OLS_BASE_URL, "http://www.ebi.ac.uk/ols/beta/api");
		new OntologyHelperFactory(new MapSolrParams(paramMap));
	}

	@Test(expected = org.apache.solr.common.SolrException.class)
	public void construct_missingOLSBaseUrl() throws Exception {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(OntologyHelperFactory.OLS_ONTOLOGY_NAME, "efo");
		new OntologyHelperFactory(new MapSolrParams(paramMap));
	}

	@Test
	public void buildOntologyHelper_defaultOwlConfig() throws Exception {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(OntologyHelperFactory.ONTOLOGY_URI_PARAM, TEST_ONTOLOGY);
		OntologyHelperFactory factory = new OntologyHelperFactory(new MapSolrParams(paramMap));
		OntologyHelper helper = factory.buildOntologyHelper();
		assertTrue(helper instanceof OWLOntologyHelper);
	}

	@Test
	public void buildOntologyHelper_fileOwlConfig() throws Exception {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(OntologyHelperFactory.ONTOLOGY_URI_PARAM, TEST_ONTOLOGY);
		paramMap.put(OntologyHelperFactory.CONFIG_FILE_PARAM, getFilePath(COMPLETE_PROPFILE_PATH));
		OntologyHelperFactory factory = new OntologyHelperFactory(new MapSolrParams(paramMap));
		OntologyHelper helper = factory.buildOntologyHelper();
		assertTrue(helper instanceof OWLOntologyHelper);
	}

	@Test
	public void buildOntologyHelper_ols() throws Exception {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(OntologyHelperFactory.OLS_BASE_URL, "http://www.ebi.ac.uk/ols/beta/api");
		paramMap.put(OntologyHelperFactory.OLS_ONTOLOGY_NAME, "efo");
		OntologyHelperFactory factory = new OntologyHelperFactory(new MapSolrParams(paramMap));
		OntologyHelper helper = factory.buildOntologyHelper();
		assertTrue(helper instanceof OLSOntologyHelper);
	}

}
