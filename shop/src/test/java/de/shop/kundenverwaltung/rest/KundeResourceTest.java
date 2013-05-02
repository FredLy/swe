package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_NACHNAME_QUERY_PARAM;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
//import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
public class KundeResourceTest extends AbstractResourceTest {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long  KUNDE_ID_VORHANDEN = Long.valueOf(3);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final String NACHNAME_VORHANDEN = "Sudar";
	private static final String NACHNAME_NICHT_VORHANDEN = "Fillipo";
	private static final String VORNAME_A = "Jaqueline";
	private static final String NEUE_EMAIL = NACHNAME_VORHANDEN + "@test.de";
	private static final String STRASSE = "Moltkestraße";
	private static final String HAUSNUMMER = "30/A";
	private static final String PLZ = "76131";
	private static final String ORT = "Karlsruhe";
	private static final Long KUNDE_ID_UPDATE = Long.valueOf(1);
	private static final String NEUER_NACHNAME = "Menges";

	
	@Test
	public void findKundeById() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		
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
		final Long kundeId = KUNDE_ID_NICHT_VORHANDEN;
		
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
		
		final String nachname = NACHNAME_VORHANDEN;
		
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
			    		assertThat(jsonObject.getString("name"), is(nachname));
			    	}
				}

				LOGGER.finer("ENDE");	
	}
	
	@Test
	public void findKundenByNachnameNichtVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String nachname = NACHNAME_NICHT_VORHANDEN;
		
		
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
		
		//Given
		final String email = NEUE_EMAIL;
		final String name = NACHNAME_VORHANDEN;
		final String vorname = VORNAME_A;
		final String strasse = STRASSE;
		final String hausnummer = HAUSNUMMER;
		final String plz = PLZ;
		final String ort = ORT;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
										.add("name", name)
										.add("vorname", vorname)
										.add("email", email)
										.add("ort", ort)
										.add("plz", plz)
										.add("strasse", strasse)
										.add("hausnummer", hausnummer)
										.build();
		// When
		final Response response = given().contentType(APPLICATION_JSON)
		 
						               .body(jsonObject.toString())
		                               .auth()
		                               .basic(username, password)
		                               .post(KUNDEN_PATH);

				
		// Then 
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));
				
				LOGGER.finer("ENDE");
	}
	
	@Test
	public void deleteKunde() {
		LOGGER.finer("Beginn");
		
		//Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		//When
		final Response response = given()
									.auth()
									.basic(username, password)
									.pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
									.delete(KUNDEN_ID_PATH);
		
		//Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.finer("Ende");
	}
	
	@Test
	public void updateKunde() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_UPDATE;
		final String neuerNachname = NEUER_NACHNAME;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                   .get(KUNDEN_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(kundeId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("name".equals(k)) {
    			job.add("name", neuerNachname);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
                          .auth()
                          .basic(username, password)
                          .put(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
   	}
}
