package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_NICHT_VORHANDEN;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
import static de.shop.util.TestConstants.ARTIKEL_VORHANDEN;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.logging.Logger;

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
public class ArtikelResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long ID_KATEGORIE = Long.valueOf(9);
	private static final Long ID_ABTEILUNG = Long.valueOf(1);
	private static final Long ID_SAISON = Long.valueOf(1);
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(3);
	private static final Long ARTIKEL_ID_DELETE = Long.valueOf(2);
	
	@Test
	public void findArtikelByID() {
	
		LOGGER.finer("BEGINN");	
		
		//Given
		final Long artikelId = Long.valueOf(ARTIKEL_VORHANDEN);
		
		//When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										 .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId) 
										 .get(ARTIKEL_ID_PATH);
		
		//Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
	              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelId));
		}

		LOGGER.finer("ENDE");
	}
	
	@Test
	public void findArtikelByIdNichtVorhanden() {
		LOGGER.finer("BEGINN");
		
		//Given
		final Long artikelId = Long.valueOf(ARTIKEL_NICHT_VORHANDEN);
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                         .get(ARTIKEL_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void createArtikel() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = "NeuerArtikel100";
		final String groesse = "L";
		final double preis = 14.99;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
									  .add("version", 0)
		             		          .add("bezeichnung", bezeichnung)
		             		          .add("groesse", groesse)
		             		          .add("preis", preis)
		             		          .add("kategorie", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_KATEGORIE)
		                    		                  .add("bezeichnung", "Kleider")
		                    		                  .add("version", 0)
		                    		                  .build())
		             		          .add("abteilung", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_ABTEILUNG)
		                    		                  .add("bezeichnung", "Herrenmode")
		                    		                  .add("version", 0)
		                    		                  .build())
		                    		  .add("saison", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_SAISON)
		                    		                  .add("bezeichnung", "Winter")
		                    		                  .add("version", 0)
		                    		                  .build())
		             		          .build();

		// When
		final Response response = (given().contentType(APPLICATION_JSON)
										 .auth()
										 .basic(username,  password)
				                         .body(jsonObject.toString()))
                                         .post(ARTIKEL_PATH);
		
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
	public void createArtikelNoRights() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = "Neu";
		final String groesse = "M";
		final double preis = 14.99;
		final String username = USERNAME_CUSTOMERRIGHTS;
		final String password = PASSWORD_CUSTOMERRIGHTS;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
									  .add("version", 0)
		             		          .add("bezeichnung", bezeichnung)
		             		          .add("groesse", groesse)
		             		          .add("preis", preis)
		             		          .add("kategorie", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_KATEGORIE)
		                    		                  .add("bezeichnung", "Kleider")
		                    		                  .add("version", 0)
		                    		                  .build())
		             		          .add("abteilung", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_ABTEILUNG)
		                    		                  .add("bezeichnung", "Herrenmode")
		                    		                  .add("version", 0)
		                    		                  .build())
		                    		  .add("saison", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_SAISON)
		                    		                  .add("bezeichnung", "Winter")
		                    		                  .add("version", 0)
		                    		                  .build())
		             		          .build();

		// When
		final Response response = (given().contentType(APPLICATION_JSON)
										 .auth()
										 .basic(username,  password)
				                         .body(jsonObject.toString()))
                                         .post(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_FORBIDDEN));
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void WrongPassword() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = "Neu";
		final String groesse = "M";
		final double preis = 14.99;
		final String username = USERNAME_CUSTOMERRIGHTS;
		final String password = PASSWORD_FALSCH;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
									  .add("version", 0)
		             		          .add("bezeichnung", bezeichnung)
		             		          .add("groesse", groesse)
		             		          .add("preis", preis)
		             		          .add("kategorie", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_KATEGORIE)
		                    		                  .add("bezeichnung", "Kleider")
		                    		                  .add("version", 0)
		                    		                  .build())
		             		          .add("abteilung", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_ABTEILUNG)
		                    		                  .add("bezeichnung", "Herrenmode")
		                    		                  .add("version", 0)
		                    		                  .build())
		                    		  .add("saison", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("id", ID_SAISON)
		                    		                  .add("bezeichnung", "Winter")
		                    		                  .add("version", 0)
		                    		                  .build())
		             		          .build();

		// When
		final Response response = (given().contentType(APPLICATION_JSON)
										 .auth()
										 .basic(username,  password)
				                         .body(jsonObject.toString()))
                                         .post(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_UNAUTHORIZED));
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void updateArtikel() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		final String neueBezeichnung = "bezeichnungNeu";
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .get(ARTIKEL_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelId.longValue()));
    	
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("bezeichnung".equals(k)) {
    			job.add("bezeichnung", neueBezeichnung);
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
                          .put(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
	}
	
	@Test
	public void deleteArtikel() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long artikelId = ARTIKEL_ID_DELETE;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		final Response response = given().auth()
                                         .basic(username, password)
                                         .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                         .delete(ARTIKEL_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.finer("ENDE");
	}
}
