package com.taxonic.carml.rmltestcases;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;

import com.taxonic.carml.engine.RmlMapper;
import com.taxonic.carml.logical_source_resolver.CsvResolver;
import com.taxonic.carml.logical_source_resolver.JsonPathResolver;
import com.taxonic.carml.logical_source_resolver.XPathResolver;
import com.taxonic.carml.model.TriplesMap;
import com.taxonic.carml.util.IoUtils;
import com.taxonic.carml.util.RmlMappingLoader;
import com.taxonic.carml.vocab.Rdf;

public class RmlTestCaseRunner {

	private static final String MAPPING_FILE_EXTENSION = "ttl";
	private static final String OUTPUT_FILE_EXTENSION = "nq";
	private static final URL TEST_CASE_ROOT = RmlTestCaseRunner.class.getResource("test-cases");

	@Test
	public void runRmlTestCases() throws IOException, URISyntaxException {
		Files.list(Paths.get(TEST_CASE_ROOT.toURI()))
				.forEach(p -> {
					try {
						runTestCaseSkipping(p, "MySQL", "PostgreSQL", "SQLServer", "SPARQL");
					} catch (RuntimeException | IOException e) {
						throw new RuntimeException(e);
					}
				});
	}

	private void runTestCaseSkipping(Path testFolder, String... skipWithPostFix) throws RuntimeException, IOException {
		if (Files.isDirectory(testFolder) && !endsWith(testFolder.getFileName().toString(), skipWithPostFix)) {
			runTestCase(testFolder);
		}
	}

	private boolean endsWith(String toTest, String... ends) {
		return Arrays.stream(ends).anyMatch(e -> toTest.endsWith(e));
	}

	private void runTestCase(Path testFolder) throws RuntimeException, IOException {
		System.out.println("Running: " + testFolder);

		RmlMapper mapper = RmlMapper.newBuilder() //
				.setLogicalSourceResolver(Rdf.Ql.JsonPath, new JsonPathResolver()) //
				.setLogicalSourceResolver(Rdf.Ql.XPath, new XPathResolver()) //
				.setLogicalSourceResolver(Rdf.Ql.Csv, new CsvResolver()) //
				.fileResolver(testFolder) //
				.build();
		Path mappingFile = getFileWithExtension(testFolder, MAPPING_FILE_EXTENSION).orElseThrow(RuntimeException::new);
		System.out.println(mappingFile);
		Set<TriplesMap> mapping = RmlMappingLoader.build().load(RDFFormat.TURTLE, mappingFile);
		Path expectedOutputFile = getFileWithExtension(testFolder, OUTPUT_FILE_EXTENSION)
				.orElse(null);

		Model expectedOutput = expectedOutputFile != null
				? IoUtils.parse(Files.newInputStream(expectedOutputFile), RDFFormat.NQUADS).stream() //
						.collect(Collectors.toCollection(TreeModel::new))
				: new TreeModel();

		Model result = new TreeModel();
		try {
			result = mapper.map(mapping).stream() //
					.collect(Collectors.toCollection(TreeModel::new));
		} catch (RuntimeException e) {
			System.err.println(String.format("Error on mapping %s: %n%s", testFolder.getFileName(), e));
		}

		assertThat(result, is(expectedOutput));
	}

	private Optional<Path> getFileWithExtension(Path testFolder, String extension) throws IOException {
		return Files.walk(testFolder) //
				.filter(Files::isRegularFile) //
				.filter(f -> f.getFileName().toString().endsWith(extension)) //
				.findFirst();
	}

}
