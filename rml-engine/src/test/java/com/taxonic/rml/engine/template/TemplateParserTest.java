package com.taxonic.rml.engine.template;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.taxonic.rml.engine.template.TemplateImpl.Text;
import com.taxonic.rml.engine.template.TemplateImpl.ExpressionSegment;
import com.taxonic.rml.engine.template.Template;
import com.taxonic.rml.engine.template.TemplateParser;

public class TemplateParserTest {

	private TemplateParser parser;

	@Before
	public void createParser() {
		parser = TemplateParser.build();
	}
	
	@Test
	public void testTrailingVariable() {
		testTemplate(
			"abc{xyz}",
			new Text("abc"),
			new ExpressionSegment(0, "xyz")
		);
	}
	
	@Test
	public void testTrailingText() {
		testTemplate(
			"abc{xyz}x",
			new Text("abc"),
			new ExpressionSegment(0, "xyz"),
			new Text("x")
		);
	}
	
	@Test
	public void testLeadingVariable() {
		testTemplate(
			"{xyz}x",
			new ExpressionSegment(0, "xyz"),
			new Text("x")
		);
	}
	
	@Test
	public void testVariableOnly() {
		testTemplate(
			"{xyz}",
			new ExpressionSegment(0, "xyz")
		);
	}
	
	@Test
	public void testTextOnly() {
		testTemplate(
			"xyz",
			new Text("xyz")
		);
	}
	
	@Test
	public void testEscaping() {
		testTemplate(
			"xyz\\{",
			new Text("xyz{")
		);
	}
	
	@Test
	public void testClosingBraceInText() {
		testTemplate(
			"xyz}",
			new Text("xyz}")
		);
	}
	
	@Test
	public void testMultipleVariables() {
		testTemplate(
			"{abc}{xyz}",
			new ExpressionSegment(0, "abc"),
			new ExpressionSegment(1, "xyz")
		);
	}
	
	@Test
	public void testEmpty() {
		testTemplate(
			""
		);
	}
	
	private void testTemplate(String templateStr, TemplateImpl.Segment... expectedSegments) {
		Template template = parser.parse(templateStr);
		Template expected = TemplateImpl.build(Arrays.asList(expectedSegments));
		assertEquals(
			expected,
			template
		);
	}

}
