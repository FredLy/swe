package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static de.shop.util.TestConstants.KUNDEN_NACHNAME_QUERY_PARAM;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static de.shop.util.TestConstants.LOCATION;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;





import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
public class KundeResourceTest extends AbstractResourceTest{
	
	
	// A = Available -- N_A = Not_Available
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long  KUNDE_BY_ID_A = Long.valueOf(1);
	private static final Long KUNDE_BY_ID_N_A = Long.valueOf(1000);
	private static final String NACHNAME_A= "Merz";
	private static final String NACHNAME_N_A = "Fillipo";
	private static final String VORNAME_A = "Jaqueline";
	private static final String NEUE_EMAIL = NACHNAME_A + "@test.de";
	private static final String STRASSE = "Moltkestraße";
	private static final String HAUSNUMMER = "30/A";
	private static final String PLZ = "76131";
	private static final String ORT = "Karlsruhe";

	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Ignore
	@Test
	public void notYetImplemented() {
		fail();
	}
	
	@Test
	public void findKundeById() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_BY_ID_A;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .get(KUNDEN_ID_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(kundeId.longValue()));
		}
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void findKundeByIdNichtVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_BY_ID_N_A;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .get(KUNDEN_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void findKundeByNachname() {
		LOGGER.finer("BEGIN");
		
		// Given
		
		final String nachname = NACHNAME_A;
		
		// When
		
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
					.queryParam(KUNDEN_NACHNAME_QUERY_PARAM, nachname)
		             .get(KUNDEN_PATH);
		
		// Then
		try (final JsonReader jsonReader =
				getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
			    	assertThat(jsonArray.size() > 0, is(true));
			    	
			    	final List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			    	for (JsonObject jsonObject : jsonObjectList) {
			    		assertThat(jsonObject.getString("nachname"), is(nachname));
			    	}
				}

				LOGGER.finer("ENDE");	
	}
	
	@Test
	public void findKundenByNachnameNichtVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String nachname = NACHNAME_N_A;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(KUNDEN_NACHNAME_QUERY_PARAM, nachname)
                                         .get(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));

		LOGGER.finer("ENDE");
	}
	
	@Test
	public void createKunde() {
		LOGGER.finer("BEGINN");
		
		final String email = NEUE_EMAIL;
		final String name = NACHNAME_A;
		final String vorname = VORNAME_A;
		final String strasse = STRASSE;
		final String hausnummer = HAUSNUMMER;
		final String plz = PLZ;
		final String ort = ORT;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
										.add("nachname", name)
										.add("vorname", vorname)
										.add("email", email)
										.add("adresse", getJsonBuilderFactory().createObjectBuilder()
												.add("plz", plz)
												.add("ort", ort)
												.add("strasse", strasse)
												.add("hausnummer", hausnummer)
												.build())
										.build();
		// When
		/**
		 * final Response response = given().contentType(APPLICATION_JSON)
		 
						               .body(jsonObject.toString())
		                               .auth()
		                               .basic(username, password)
		                               .post(KUNDEN_PATH);
		*/
				
		// Then 
		/**		assertThat(response.getStatusCode(), is(HTTP_CREATED));
				final String location = response.getHeader(LOCATION);
				final int startPos = location.lastIndexOf('/');
				final String idStr = location.substring(startPos + 1);
				final Long id = Long.valueOf(idStr);
				assertThat(id.longValue() > 0, is(true));
		*/
				LOGGER.finer("ENDE");
	}
}
