package com.taxonic.carml.model;

import com.taxonic.carml.rdf_mapper.annotations.RdfProperty;
import com.taxonic.carml.vocab.Carml;

public interface FileSource {

	@RdfProperty(Carml.url)
	String getUrl();
}
