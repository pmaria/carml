package com.taxonic.carml.engine;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;

class PredicateObjectMapper {
	
	private List<TermGenerator<IRI>> graphGenerators;
	private List<PredicateMapper> predicateMappers;

	PredicateObjectMapper(
		List<TermGenerator<IRI>> graphGenerators,
		List<PredicateMapper> predicateMappers
	) {
		this.graphGenerators = graphGenerators;
		this.predicateMappers = predicateMappers;
	}

	void map(Model model, EvaluateExpression evaluate, Resource subject, List<IRI> subjectGraphs) {

		Resource[] contexts = Stream
				.concat(
					subjectGraphs.stream(),
					graphGenerators.stream()
							.map(g -> g.apply(evaluate))
							.filter(Optional::isPresent)
							.map(Optional::get)
				)
				.distinct()
				.toArray(Resource[]::new);
		
		predicateMappers.forEach(p -> p.map(model, evaluate, subject, contexts));
		
	}
}
