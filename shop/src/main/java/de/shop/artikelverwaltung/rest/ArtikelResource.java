package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/artikel")
@Produces({ APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
@Transactional
public class ArtikelResource {

	private static final Logger LOGGER = 
			Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private UriHelperArtikel uriHelper;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(Level.FINER, "Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(Level.FINER, "Bean {0} wurde geloescht", this);
	}
	
	/*
	 * Liefert alle vorhandenen Artikel
	 * Aufruf: /artikel
	 */
	@GET
	@Wrapped(element = "artikel")
	public Collection<Artikel> findAllArtikel() {
		return as.findAllArtikel();
	}
	
	/*
	 * Liefert Artikel anhand seiner Id
	 * Aufruf: /artikel/{ID}
	 */
	@GET
	@Path("{id:[0-9]*}")
	public Artikel findArtikel(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Artikel artikel = as.findArtikelById(id);
		if (artikel == null) {
			final String msg = ("Es existiert kein Artikel mit der ID " + id);
			throw new NotFoundException(msg);
		}
		return artikel;
	}
	
	/*
	 * Aktualisiert einen Artikel und speichert die neuen Werte in der Datenbank
	 * Aufruf: /artikel
	 */
	@PUT
	@Consumes({ APPLICATION_JSON })
	@Produces
	public void updateArtikel(Artikel artikel) {
		Artikel artikelOriginal = as.findArtikelById(artikel.getId());
		if (artikelOriginal == null)
			throw new NotFoundException("Kein Artikel gefunden mit der ID " + artikel.getId());
		
		artikelOriginal = artikel;
		as.updateArtikel(artikelOriginal);
	}
	
	/*
	 * Erzeugt einen neuen Artikel und speichert diesen in der Datenbank
	 * Aufruf: /artikel
	 */
	@POST
	@Consumes({ APPLICATION_JSON })
	@Produces
	public Response createArtikel(Artikel artikel, @Context UriInfo uriInfo) throws URISyntaxException {
		Artikel artikelNew = as.createArtikel(artikel);
		URI artikelUri = uriHelper.getUriArtikel(artikelNew, uriInfo);
		return Response.created(artikelUri).build();
	}
	
	/*
	 * L�scht einen Artikel anand dessen Id aus der Datenbank
	 * Aufruf: /artikel/{ID}
	 */
	@DELETE
	@Path("{id:[0-9]*}")
	@Produces
	public void deleteArtikel(@PathParam("id") Long id) {
		Artikel artikel = as.findArtikelById(id);
		if (artikel == null)
			throw new NotFoundException("Kein Artikel gefunden mit der ID " + id);
		as.deleteArtikel(artikel);
	}
}
