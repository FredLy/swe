package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;



import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.ConcurrentDelete;
import de.shop.util.AbstractResourceTest;
import de.shop.util.ConcurrentUpdate;

@RunWith(Arquillian.class)
public class KundeResourceConcurrencyTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private static final Long KUNDE_ID_UPDATE = Long.valueOf(3);
	private static final String NEUER_NACHNAME = "Testname";
	private static final String NEUER_NACHNAME_2 = "Neuertestname";
	private static final Long KUNDE_ID_DELETE1 = Long.valueOf(3);
	private static final Long KUNDE_ID_DELETE2 = Long.valueOf(4);

	@Test
	public void updateUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_UPDATE;
    	final String neuerNachname = NEUER_NACHNAME;
    	final String neuerNachname2 = NEUER_NACHNAME_2;
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

    	// Concurrent Update
    	JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("name".equals(k)) {
    			job.add("name", neuerNachname2);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final JsonObject jsonObject2 = job.build();
    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, KUNDEN_PATH,
    			                                                       username, password);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Failed Update
    	job = getJsonBuilderFactory().createObjectBuilder();
    	keys = jsonObject.keySet();
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
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void updateDelete() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_DELETE1;
    	final String neuerNachname = NEUER_NACHNAME;
		final String username = USERNAME;
		final String password = PASSWORD;
		final String username2 = USERNAME_ADMIN;
		final String password2 = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                   .get(KUNDEN_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Concurrent Delete
    	final ConcurrentDelete concurrentDelete = new ConcurrentDelete(KUNDEN_PATH + '/' + kundeId,
    			                                                       username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentDelete);
		response = future.get();
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Failed Update
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
    	response = given().contentType(APPLICATION_JSON)
    			          .body(jsonObject.toString())
                          .auth()
                          .basic(username, password)
                          .put(KUNDEN_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void deleteUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_DELETE2;
    	final String neuerNachname = NEUER_NACHNAME;
    	final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		final String username2 = USERNAME;
		final String password2 = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                   .get(KUNDEN_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Concurrent Update
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
    	final ConcurrentUpdate concurrenUpdate = new ConcurrentUpdate(jsonObject, KUNDEN_PATH,
    			                                                      username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrenUpdate);
		response = future.get();
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		response = given().auth()
                          .basic(username, password)
                          .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                          .delete(KUNDEN_ID_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		LOGGER.finer("ENDE");
	}
}
