package com.taxonic.carml.engine;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;

class PredicateObjectMapper {
	
	private Set<TermGenerator<IRI>> graphGenerators;
	private Set<PredicateMapper> predicateMappers;

	PredicateObjectMapper(
		Set<TermGenerator<IRI>> graphGenerators,
		Set<PredicateMapper> predicateMappers
	) {
		this.graphGenerators = graphGenerators;
		this.predicateMappers = predicateMappers;
	}

	void map(Model model, EvaluateExpression evaluate, Resource subject, Set<IRI> subjectGraphs) {

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
