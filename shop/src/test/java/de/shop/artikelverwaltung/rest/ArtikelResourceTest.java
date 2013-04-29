package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_VORHANDEN;
import static de.shop.util.TestConstants.ARTIKEL_NICHT_VORHANDEN;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
public class ArtikelResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long ARTIKEL_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	
	@Test
	public void findArtikelByID(){
	
		LOGGER.finer("BEGINN");	
		
		//Given
		final Long artikelId = Long.valueOf(ARTIKEL_VORHANDEN);
		
		//when
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										 //.pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId) --> führt zu Fehlermeldung: specified too many path parameters
										 .get(ARTIKEL_ID_PATH + ARTIKEL_VORHANDEN);
		
		//then
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
		
		// Given
		final Long artikelId = Long.valueOf(ARTIKEL_NICHT_VORHANDEN);
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         //.pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                         .get(ARTIKEL_ID_PATH + ARTIKEL_NICHT_VORHANDEN);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void createArtikel() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String bezeichnung = "NeuerArtikel";
		final String groesse = "L";
		final double preis = 14.99;
		final short saison = 2;
		final short kategorie = 2;
		final short abteilung = 2;
		
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
		             		          .add("bezeichnung", bezeichnung)
		             		          .add("groesse", groesse)
		             		          .add("preis", preis)
		             		          .add("saison", saison)
		             		          .add("kategorie", kategorie)
		             		          .add("abteilung", abteilung)
		             		          .build();

		// When
		final Response response = (given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString()))
                                         .post(ARTIKEL_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.finer("ENDE");
	}
}
