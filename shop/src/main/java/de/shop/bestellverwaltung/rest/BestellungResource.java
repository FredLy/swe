package de.shop.bestellverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Posten;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.rest.UriHelperKunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.Log;
import de.shop.util.NotFoundException;

@Path("/bestellungen")
@Produces({ APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class BestellungResource {

	
	private static final Logger LOGGER = 
			Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private UriHelperBestellung uriHelper;
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService as;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(Level.FINER, "Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(Level.FINER, "Bean {0} wurde geloescht", this);
	}
	
	/*
	 * Liefert alle existierenden Bestellungen
	 * Aufruf: /bestellungen
	 */
	@GET
	@Wrapped(element = "bestellungen")
	public Collection<Bestellung> findAllBestellungen(@Context UriInfo uriInfo) {
		Collection<Bestellung> bestellungen = bs.findAllBestellungen();
		if (bestellungen == null) {
			final String msg = "Keine Bestellungen vorhanden";
			throw new NotFoundException(msg);
		}
		for (Bestellung b : bestellungen) {
			uriHelper.updateUriBestellung(b, uriInfo);
		}
		return bestellungen;
	}
	
	/*
	 * Liefert eine Bestellung anhand der ID
	 * Aufruf: /bestellungen/{ID}
	 */
	@GET
	@Path("{id:[0-9]*}")
	public Bestellung findBestellungById(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Bestellung bestellung = bs.findBestellungById(id);
		uriHelper.updateUriBestellung(bestellung, uriInfo);
		return bestellung;
	}
	
	/*
	 * Liefert den Kunden zu einer Bestellungs-ID
	 * Aufruf: /bestellungen/{ID}/Kunde
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/kunde")
	public Kunde findKundeByBestellungId(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Kunde kunde = bs.findKundeById(id);
		uriHelperKunde.updateUriKunde(kunde, uriInfo);
		return kunde;
	}
	
	/*
	 * Ändert eine Bestellung und speichert sie in der Datenbank
	 * Aufruf: /bestellungen
	 */
	@PUT
	@Consumes({ APPLICATION_JSON })
	@Produces
	public void updateBestellung(Bestellung bestellung) {
		Bestellung bestellungOriginal = bs.findBestellungById(bestellung.getId());
		bestellungOriginal = bestellung;
		bs.updateBestellung(bestellungOriginal);
	}
	
	/*
	 * Erstellt eine neue Bestellung und speichert sie in der Datenbank
	 * Aufruf: /bestellungen
	 */
	@POST
	@Consumes({ APPLICATION_JSON })
	@Produces
	public Response createBestellung(Bestellung bestellung, 
									@Context UriInfo uriInfo, 
									@Context HttpHeaders headers) throws URISyntaxException {
		//Kunde setzen --> URI aufsplitten, Kunde anhand der ID auslesen und setzen
		final String kundeUriString = bestellung.getKundeUri().toString();
		int startPos = kundeUriString.lastIndexOf('/') + 1;
		final String kundeIdString = kundeUriString.substring(startPos);
		Long kundeId = null;
		try {
			kundeId = Long.valueOf(kundeIdString);
		} catch (NumberFormatException e) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID: " + kundeIdString, e);
		}
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		Kunde kunde = ks.findKundeById(kundeId, locale);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde vorhanden mit der ID: " + kundeIdString);
		}
		// Artikel in Posten setzen --> ArtikelURI aufsplitten, Artikel anhand der IDs auslesen und setzen
		Collection<Posten> posten = bestellung.getPosten();
		List<Long> artikelIds = new ArrayList<>(posten.size());
		for (Posten p : posten) {
			final String artikelUriString = p.getArtikelUri().toString();
			startPos = artikelUriString.lastIndexOf('/') + 1;
			final String artikelIdString = artikelUriString.substring(startPos);
			Long artikelId = null;
			try {
				artikelId = Long.valueOf(artikelIdString);
			}
			catch (NumberFormatException e) {
				// Wenn nicht alle IDs ungültig sind, werden ungültige nicht berücksichtigt
				continue;
			}
			artikelIds.add(artikelId);
		}
		if (artikelIds.isEmpty()) {
			// alle Artikel IDs sind ungültig
			// IDs sammeln und in Exception ausgeben
			final StringBuilder sb = new StringBuilder("Keine Artikel vorhanden mit den IDs: ");
			for (Posten p : posten) {
				final String artikelUriStr = p.getArtikelUri().toString();
				startPos = artikelUriStr.lastIndexOf('/') + 1;
				sb.append(artikelUriStr.substring(startPos));
				sb.append(" ");
			}
			throw new NotFoundException(sb.toString());
		}
		Collection<Artikel> gefundeneArtikel = as.findArtikelByIds(artikelIds);
		if (gefundeneArtikel.isEmpty()) {
			throw new NotFoundException("Keine Artikel vorhanden mit den IDs: " + artikelIds);
		}
		int i = 0;
		final List<Posten> neuePosten = new ArrayList<>(posten.size());
		for (Posten p : posten) {
			final long artikelId = artikelIds.get(i++);
			// Test für jeden Artikel, ob er gefunden wurde
			for (Artikel artikel : gefundeneArtikel) {
				if (artikel != null && artikel.getId().longValue() == artikelId) {
					// --> wurde gefunden, also setzen
					p.setArtikel(artikel);
					neuePosten.add(p);
					break;					
				}
			}
		}
		bestellung.setPosten(neuePosten);
		Bestellung bestellungNew = bs.createBestellung(bestellung, kunde, locale);
		final URI bestellungUri = uriHelper.getUriBestellung(bestellungNew, uriInfo);
		return Response.created(bestellungUri).build();
	}
	
	/*
	 * Löscht eine Bestellung aus der Datenbank
	 * Aufruf: /bestellungen/{ID}
	 */
	@DELETE
	@Path("{id:[0-9]*}")
	@Produces
	public void deleteBestellung(@PathParam("id") Long id) {
		Bestellung bestellung = bs.findBestellungById(id);
		bs.deleteBestellung(bestellung);
	}
}
