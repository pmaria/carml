package com.taxonic.carml.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.taxonic.carml.engine.function.FnoFunction;
import com.taxonic.carml.engine.function.FnoParam;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class TestBbiFunctions {
	
	private static final String FNPDOK = "http://data.pdok/nl/mappings/functions#";
	
	@FnoFunction(FNPDOK + "doIndicatieToBooleanLiteral")
	public boolean doIndicatieToBooleanLiteral(
		@FnoParam(FNPDOK + "valueParameter") String indicatie
  	) {
  		if (indicatie.equals("N")) {
  			return false;
  		} else if (indicatie.equals("J")) {
  			return true;
  		} else {
  			throw new RuntimeException(String.format("Invalid indicatie value: %s", indicatie));
  		}
  	}
	
	@FnoFunction(FNPDOK + "getKrimpgebiedUri")
	public String getKrimpgebiedUri(
		@FnoParam(FNPDOK + "baseUriParameter") String baseUri,
		@FnoParam(FNPDOK + "valueParameter") String regioNaam
  	) {
		System.out.println("baseUri: " + baseUri);
		System.out.println("regioNaam: " + regioNaam);
		return baseUri + caseIt(regioNaam);
  	}
	
	
	
	private static final List<String> ENERGIEVERB_KEYS = ImmutableList.of("GemiddeldAardgasverbruik_4", "GemiddeldElektriciteitsverbruik_5", "Stadsverwarming_6", "IndelingswijzigingWijkenEnBuurten_7");
	private static final List<String> ENERGIEVERB_VALUES =  ImmutableList.of("Gemiddeld aardgasverbruik (m3)", "Gemiddeld elektriciteitsverbruik (kWh)", "Stadsverwarming (%)", "Indelingswijziging wijken en buurten (code)");
	private static final Map<String, String> ENERGIEVERB_MAPPING = getEnergieverbrMapping();
	
	private static Map<String, String> getEnergieverbrMapping() {	
		Map<String, String> kernMapping = new HashMap<>();
		
		for (int i = 0; i < ENERGIEVERB_KEYS.size(); i++) {
			kernMapping.put(ENERGIEVERB_KEYS.get(i), lowerCaseIt((ENERGIEVERB_VALUES.get(i)))); 
		}
		
		return kernMapping;
	}
	
	private static final Map<String, String> PIJLER_VOORZ = 
		ImmutableMap
			.of(
				"ClientenMetVoorzieningen_5", lowerCaseIt("Sociaal domein clienten/huishoudens/Clienten met voorzieningen (aantal)"),
				"HuishoudensMetVoorzieningen_6", lowerCaseIt("Sociaal domein clienten/huishoudens/Huishoudens met voorzieningen (aantal)"),
				"ClientenMetVoorzieningen_7", lowerCaseIt("Sociaal domein clienten/huishoudens (%)/Clienten met voorzieningen (per 1 000 inwoners)"),
				"HuishoudensMetVoorzieningen_8", lowerCaseIt("Sociaal domein clienten/huishoudens (%)/Huishoudens met voorzieningen (per 1 000 huishoudens)")
			);
	
	@FnoFunction(FNPDOK + "getGemeenteUriFromWijkenEnBuurten")
	public String getGemeenteUriFromWijkenEnBuurten(
		@FnoParam(FNPDOK + "baseUriParameter") String baseUri,
		@FnoParam(FNPDOK + "typeParameter") String type ,
		@FnoParam(FNPDOK + "valueParameter") String wijkenEnBuurten
  	) {
//		System.out.println("value: " + type);
//		System.out.println("property: " + wijkenEnBuurten);
		
		if (/*type == null ||*/ wijkenEnBuurten == null) {
			return null;
		}
		
		//###############################TEMP#################
		if (type == null) {
			type = "Gemeente";
		}
		
//		String shortCode = "";
		type = type.trim();
		
		return baseUri + org.apache.commons.lang3.text.WordUtils.uncapitalize(type) + "/2016" + wijkenEnBuurten.trim();
  	}
	
	@FnoFunction(FNPDOK + "getWijkenEnBuurtenClass")
	public String getWijkenEnBuurtenClass(
		@FnoParam(FNPDOK + "baseUriParameter") String baseUri,
		@FnoParam(FNPDOK + "typeParameter") String type
  	) {	
		//###############################TEMP#################
		if (type == null) {
			type = "Gemeente";
		}
		return baseUri + type.trim();
  	}
	
	@FnoFunction(FNPDOK + "trimValue")
	public String trimValue(
		@FnoParam(FNPDOK + "valueParameter") String value
  	) {	
		return value.trim();
  	}
	
	@FnoFunction(FNPDOK + "trimValue")
	public String getPropertyIRI(
		@FnoParam(FNPDOK + "baseUriParameter") String baseUri,
		@FnoParam(FNPDOK + "valueParameter") String value
  	) {	
		return value.trim();
  	}
	
	@FnoFunction(FNPDOK + "getWoningKenmerkBasedPredicate")
	public String getWoningKenmerkBasedPredicate(
		@FnoParam(FNPDOK + "valueParameter") String value,
		@FnoParam(FNPDOK + "propertyParameter") String property
	) {
//		System.out.println("value: " + value);
//		System.out.println("property: " + property);
		
		if (value == null || property == null) {
			return null;
		}
		
		String predicate = getEnergieverbruikPredicate(property);
//		System.out.println("pred:" + predicate);
		switch (value) {
			case "a":
				return predicate + "_D_TotaalWoningen";
			case "b":
				return predicate + "_D_Appartement";
			case "c":
				return predicate + "_D_Tussenwoning";
			case "d":
				return predicate + "_D_Hoekwoning";
			case "e":
				return predicate + "_D_Twee_onder_een_kapWoning";
			case "f":
				return predicate + "_D_VrijstaandeWoning";
			case "g":
				return predicate + "_D_EigenWoning";
			case "h":
				return predicate + "_D_Huurwoning";
			default:
				return "";
		}
	}
	
	private String getEnergieverbruikPredicate(String property) {
		return "https://data.pdok.nl/cbs/vocab/" + ENERGIEVERB_MAPPING.get(property);
	}
	
	@FnoFunction(FNPDOK + "getPijlerVoorzieningenBasedPredicate")
	public String getPijlerVoorzieningenBasedPredicate(
		@FnoParam(FNPDOK + "valueParameter") String value,
		@FnoParam(FNPDOK + "propertyParameter") String property
	) {
//		System.out.println("value: " + value);
//		System.out.println("property: " + property);
		
		if (value == null || property == null) {
			return null;
		}
		
		String predicate = getPijlerVoorzieningenPredicate(property);
//		System.out.println("pred:" + predicate);
		switch (value) {
			case "T001287":
				return predicate + "_D_TotaalVoorzieningen";
			case "A041708":
				return predicate + "_D_AlleenJeugd";
			case "A041709":
				return predicate + "_D_AlleenP_wet";
			case "A041710":
				return predicate + "_D_AlleenWmo";
			case "A041711":
				return predicate + "_D_JeugdEnP_wet";
			case "A041712":
				return predicate + "_D_JeugdEnWmo";
			case "A041713":
				return predicate + "_D_P_wetEnWmo";
			case "A041714":
				return predicate + "_D_Jeugd_P_wetEnWmo";
			default:
				return "";
		}
	}
	
	private String getPijlerVoorzieningenPredicate(String property) {		
		return "https://data.pdok.nl/cbs/vocab/" + PIJLER_VOORZ.get(property);
	}
	
	@FnoFunction(FNPDOK + "getTypeJeugdzorgBasedPredicate")
	public String getTypeJeugdzorgBasedPredicate(
		@FnoParam(FNPDOK + "valueParameter") String value,
		@FnoParam(FNPDOK + "propertyParameter") String property
	) {
//		System.out.println("value: " + value);
//		System.out.println("property: " + property);
		
		if (value == null || property == null) {
			return null;
		}
		
		String predicate = getTypeJeugdzorgPredicate(property);
//		System.out.println("pred:" + predicate);
		switch (value) {
			case "T001203":
				return predicate + "_D_TotaalJeugdzorg";
			case "A042502":
				return predicate + "_D_TotaalJeugdhulp";
			case "A042503":
				return predicate + "_D_TotaalJeugdhulpZonderVerblijf";
			case "A027918":
				return predicate + "_D_JeugdhulpMetVerblijf";
			case "A027919":
				return predicate + "_D_Jeugdbescherming";
			case "A027920":
				return predicate + "_D_Jeugdreclassering";
			default:
				return "";
		}
	}
	
	public static final List<String> TYPE_JEUGDZORG_KEYS= ImmutableList.of("TotaalJongerenMetJeugdzorg_5", "k_0Tot4Jaar_6", "k_4Tot12Jaar_7", "k_12Tot18Jaar_8", "k_18Tot23Jaar_9", "ThuiswonendKindInEenouderGezin_10", "ThuiswonendKindInTweeouderGezin_11", "AndereHuishoudens_12", "TotaalJeugdzorgtrajecten_13", "StabilisatieVanEenCrisissituatie_14", "Diagnostiek_15", "Begeleiden_16", "Behandelen_17", "GemeentelijkeToegang_18", "Huisarts_19", "Jeugdarts_20", "GecertificeerdeInstelling_21", "MedischSpecialist_22", "RechterOfficierVanJustitie_23", "GeenVerwijzer_24", "VerwijzerOnbekend_25");
	private static final List<String> TYPE_JEUGDZORG_VALUES = ImmutableList.of("Jongeren met jeugdzorg/Totaal jongeren met jeugdzorg (aantal)", "Jongeren met jeugdzorg/Leeftijd/0 tot 4 jaar (aantal)", "Jongeren met jeugdzorg/Leeftijd/4 tot 12 jaar (aantal)", "Jongeren met jeugdzorg/Leeftijd/12 tot 18 jaar (aantal)", "Jongeren met jeugdzorg/Leeftijd/18 tot 23 jaar (aantal)", "Jongeren met jeugdzorg/Samenstelling huishouden bij hulpvraag/Thuiswonend kind in eenouder gezin (aantal)", "Jongeren met jeugdzorg/Samenstelling huishouden bij hulpvraag/Thuiswonend kind in tweeouder gezin (aantal)", "Jongeren met jeugdzorg/Samenstelling huishouden bij hulpvraag/Andere huishoudens (aantal)", "Jeugdzorgtrajecten/Totaal jeugdzorgtrajecten (aantal)", "Jeugdzorgtrajecten/Perspectief/Stabilisatie van een crisissituatie (aantal)", "Jeugdzorgtrajecten/Perspectief/Diagnostiek (aantal)", "Jeugdzorgtrajecten/Perspectief/Begeleiden (aantal)", "Jeugdzorgtrajecten/Perspectief/Behandelen (aantal)", "Jeugdzorgtrajecten/Verwijzer/Gemeentelijke toegang (aantal)", "Jeugdzorgtrajecten/Verwijzer/Huisarts (aantal)", "Jeugdzorgtrajecten/Verwijzer/Jeugdarts (aantal)", "Jeugdzorgtrajecten/Verwijzer/Gecertificeerde instelling (aantal)", "Jeugdzorgtrajecten/Verwijzer/Medisch specialist (aantal)", "Jeugdzorgtrajecten/Verwijzer/Rechter_ Officier van Justitie_ ... (aantal)", "Jeugdzorgtrajecten/Verwijzer/Geen verwijzer (aantal)", "Jeugdzorgtrajecten/Verwijzer/Verwijzer onbekend (aantal)");
	private static final Map<String, String> TYPE_JEUGDZORG_MAPPING = getTypeJeugdzorgMapping();
	
	private static Map<String, String> getTypeJeugdzorgMapping() {	
		Map<String, String> kernMapping = new HashMap<>();
		
		for (int i = 0; i < TYPE_JEUGDZORG_KEYS.size(); i++) {
			kernMapping.put(TYPE_JEUGDZORG_KEYS.get(i), lowerCaseIt(TYPE_JEUGDZORG_VALUES.get(i))); 
		}
		
		return kernMapping;
	}
	
	private String getTypeJeugdzorgPredicate(String property) {
		return "https://data.pdok.nl/cbs/vocab/" + TYPE_JEUGDZORG_MAPPING.get(property);
	}
	
	@FnoFunction(FNPDOK + "getFinancieringsvormBasedPredicate")
	public String getFinancieringsvormBasedPredicate(
		@FnoParam(FNPDOK + "valueParameter") String value,
		@FnoParam(FNPDOK + "propertyParameter") String property
	) {
//		System.out.println("value: " + value);
//		System.out.println("property: " + property);
		
		if (value == null || property == null) {
			return null;
		}
		
		String predicate = getFinancieringsvormPredicate(property);
//		System.out.println("pred:" + predicate);
		switch (value) {
			case "T001029":
				return predicate + "_D_Totaal";
			case "A019510":
				return predicate + "_D_UitsluitendPGB";
			case "A019511":
				return predicate + "_D_UitsluitendZIN";
			case "A019512":
				return predicate + "_D_ZowelPGBAlsZIN";
			default:
				return "";
		}
	}
	
	public static final List<String> FINANCIERINGSVORM_KEYS= ImmutableList.of("IndelingswijzeWijken_4", "WmoClienten_5", "WmoClientenPer1000Inwoners_6");
	private static final List<String> FINANCIERINGSVORM_VALUES = ImmutableList.of("Regioaanduiding/Indelingswijze wijken (code)", "Wmo-cliënten (aantal)", "Wmo-cliënten per 1 000 inwoners (per 1 000 inwoners)");
	private static final Map<String, String> FINANCIERINGSVORM_MAPPING = getFinancieringsvormMapping();
	
	private static Map<String, String> getFinancieringsvormMapping() {	
		Map<String, String> kernMapping = new HashMap<>();
		
		for (int i = 0; i < FINANCIERINGSVORM_KEYS.size(); i++) {
			kernMapping.put(FINANCIERINGSVORM_KEYS.get(i), lowerCaseIt(FINANCIERINGSVORM_VALUES.get(i))); 
		}
		
		return kernMapping;
	}
	
	private String getFinancieringsvormPredicate(String property) {
		return "https://data.pdok.nl/cbs/vocab/" + FINANCIERINGSVORM_MAPPING.get(property);
	}
	
	public static String lowerCaseIt(String s) {
		return org.apache.commons.lang3.StringUtils.deleteWhitespace(org.apache.commons.lang3.text.WordUtils.uncapitalize(caseIt(s)));
	}
	
	private static String caseIt(String s) {
	    if (Character.isDigit(s.charAt(0))) {
	    	s = "N" + s;
	    }
	    s = org.apache.commons.lang3.text.WordUtils.capitalize(s);
	    return s.replaceAll(" ", "").replaceAll("-", "_").replaceAll("\\(", "_").replaceAll("\\)", "").replaceAll(",", "")
	        .replaceAll(":", "_").replaceAll("/", "_").replaceAll(">", "_").replaceAll("<", "_").replaceAll("\\+", "")
	        .replaceAll("%", "p").replaceAll("'", "_").replaceAll(";", "_").replaceAll("\\.", "_").replaceAll(" ", "");
	}
	
	//@formatter:off
	  @FnoFunction(FNPDOK + "getWktHash")
	  public String getWktHash(
	      @FnoParam(FNPDOK + "baseIriParameter") String baseIri,
	      @FnoParam(FNPDOK + "valueParameter") String wkt
	  ) {
	  //@formatter:on
//	    System.out.println("Calling getGeoIri with baseIri " + baseIri + " wkt " + wkt);

	    try {
	      return baseIri + getRdGeometryHashId(wkt);
	    } catch (NoSuchAlgorithmException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    return null;
	  }

	  private String getRdGeometryHashId(String wktRd) throws NoSuchAlgorithmException {

	    String wktRdMd5Hash = "";
	    wktRdMd5Hash =
	        (new HexBinaryAdapter()).marshal(MessageDigest.getInstance("MD5").digest(wktRd.getBytes()));
	    return wktRdMd5Hash;
	  }

}
