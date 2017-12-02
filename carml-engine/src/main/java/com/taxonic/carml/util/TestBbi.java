package com.taxonic.carml.util;

import com.taxonic.carml.engine.RmlMapper;
import com.taxonic.carml.model.TriplesMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.WriterConfig;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;

public class TestBbi {
	
	private static final String BASE = "D:\\MariaP\\Mijn Documenten\\Work\\Opdrachten\\Kadaster\\CBS\\";
	private static final ValueFactory VF = SimpleValueFactory.getInstance();
	
	public static void main(String...args) throws URISyntaxException {
		fileMapper();
	}
	
	public static void fileMapper() {
		
		// Create mapper with specific basepath for logical sources.
		Path basePath = Paths.get(BASE);
		RmlMapper mapper = 
			RmlMapper.newBuilder()
			.addFunctions(new TestBbiFunctions())
			.fileResolver(basePath)
			.build();
		
		// Get mapping file from same folder
		Set<TriplesMap> mapping =
			RmlMappingLoader
				.build()
				.load(Paths.get(BASE + "/BBI.rml.ttl"), RDFFormat.TURTLE);
		
		// Execute mapping
		Model result = mapper.map(mapping);
		Model stripped = 
				result.stream()
				// hacky fix for empty string value generation
				.filter(st -> !st.getObject().stringValue().equals(""))
				// hacky fix for removal of function generation triples
				.filter(st -> !(st.getSubject() instanceof BNode))
				// hacky fix for uri generation bug
				.map(st -> {
					String obj = st.getObject().stringValue();
					if (obj.startsWith("https://data.pdok.nl/cbs/vocab/")) {
						return VF.createStatement(st.getSubject(), st.getPredicate(), VF.createIRI(obj));
					}
					return st;
				})
				.collect(Collectors.toCollection(LinkedHashModel::new));
		

		
		stripped.setNamespace("bbi", "http://data.pdok.nl/def/bbi#");
		stripped.setNamespace(RDF.NS);
		stripped.setNamespace(RDFS.NS);
		stripped.setNamespace(DCTERMS.NS);
		stripped.setNamespace(XMLSchema.NS);
		
		// Print model
//		result.forEach(System.out::println);
		
		writeToFile(stripped, BASE, "BBI", "data");
	}
	
	public static void krimpMapper() {
		
		// Create mapper with specific basepath for logical sources.
		Path basePath = Paths.get(BASE);
		RmlMapper mapper = 
			RmlMapper.newBuilder()
			.addFunctions(new TestBbiFunctions())
			.fileResolver(basePath)
			.build();
		
		// Get mapping file from same folder
		Set<TriplesMap> mapping =
			RmlMappingLoader
				.build()
				.load(Paths.get(BASE + "/Krimp.rml.ttl"), RDFFormat.TURTLE);
		
		// Execute mapping
		Model result = mapper.map(mapping);
		Model stripped = 
				result.stream()
				// hacky fix for empty string value generation
				.filter(st -> !st.getObject().stringValue().equals(""))
				// hacky fix for removal of function generation triples
				.filter(st -> !(st.getSubject() instanceof BNode))
				// hacky fix for uri generation bug
				.map(st -> {
					String obj = st.getObject().stringValue();
					if (obj.startsWith("https://data.pdok.nl/cbs/vocab/")) {
						return VF.createStatement(st.getSubject(), st.getPredicate(), VF.createIRI(obj));
					}
					return st;
				})
				.collect(Collectors.toCollection(LinkedHashModel::new));
		
		
		// Print model
		stripped.forEach(System.out::println);
		
		writeToFile(stripped, BASE, "Krimpgebieden", "" + 0);
	}
	
	public static void gemeenteMapper() {
		
		// Create mapper with specific basepath for logical sources.
		Path basePath = Paths.get(BASE);
		RmlMapper mapper = 
			RmlMapper.newBuilder()
			.addFunctions(new TestBbiFunctions())
			.fileResolver(basePath)
			.build();
		
		// Get mapping file from same folder
		Set<TriplesMap> mapping =
			RmlMappingLoader
				.build()
				.load(Paths.get(BASE + "/wijkenBuurtenGeo.rml.ttl"), RDFFormat.TURTLE);
		
		// Execute mapping
		Model result = mapper.map(mapping);
		Model stripped = 
				result.stream()
				// hacky fix for empty string value generation
				.filter(st -> !st.getObject().stringValue().equals(""))
				// hacky fix for removal of function generation triples
				.filter(st -> !(st.getSubject() instanceof BNode))
				// hacky fix for uri generation bug
				.map(st -> {
					String obj = st.getObject().stringValue();
					if (obj.startsWith("https://data.pdok.nl/cbs/vocab/")) {
						return VF.createStatement(st.getSubject(), st.getPredicate(), VF.createIRI(obj));
					}
					return st;
				})
				.collect(Collectors.toCollection(LinkedHashModel::new));
		
		
		// Print model
		stripped.forEach(System.out::println);
		
		writeToFile(stripped, BASE, "cbs_gemeentes_geo_gegeneraliseerd_2016", "" + 0);
	}
	
	public static void fileMapper2() {
		// Determine base path url string for a classpath resource
		String basePathUrl = "D:\\MariaP\\Mijn Documenten\\Work\\Opdrachten\\Kadaster\\CBS\\wijkenBuurten\\";
		// Create mapper with specific basepath for logical sources.
		Path basePath = Paths.get(basePathUrl);
		RmlMapper mapper = 
			RmlMapper.newBuilder()
			.addFunctions(new TestBbiFunctions())
			.fileResolver(basePath)
			.build();
		
		// Get mapping file from same folder
		Set<TriplesMap> mapping =
			RmlMappingLoader
				.build()
				.load(Paths.get(basePathUrl + "/wijkenBuurtenSets.rml.ttl"), RDFFormat.TURTLE);
		
		// Execute mapping
		Model result = mapper.map(mapping);
		
		Model stripped = 
				result.stream()
				// hacky fix for empty string value generation
				.filter(st -> !st.getObject().stringValue().equals(""))
				// hacky fix for removal of function generation triples
				.filter(st -> !(st.getSubject() instanceof BNode))
				// hacky fix for uri generation bug
				.map(st -> {
					String obj = st.getObject().stringValue();
					if (obj.startsWith("https://data.pdok.nl/cbs/vocab/")) {
						return VF.createStatement(st.getSubject(), st.getPredicate(), VF.createIRI(obj));
					}
					return st;
				})
				.collect(Collectors.toCollection(LinkedHashModel::new));
		
		// Print model
			stripped.forEach(System.out::println);
			
			writeToFile(stripped, basePathUrl, "WijkenEnBuurten_Energiebesparingspotentie_2015_As_2016", "" + 0);
			
			
	}
	
	private static void writeToFile(Model model, String mappingBasePath, String outputFileBaseName, String filePart) {
		FileWriter fw;
		try {
			fw = new FileWriter(mappingBasePath + String.format("%s-%s.ttl", outputFileBaseName, filePart));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		BufferedWriter writer = new BufferedWriter(fw);
		WriterConfig config = new WriterConfig();
		config.set(BasicWriterSettings.PRETTY_PRINT, true);
		Rio.write(model, writer, RDFFormat.TURTLE, config);
	}


}
