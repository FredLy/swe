package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;


@Path("/kunden")
@Produces({ APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
@Transactional
public class KundeResource {

	private static final Logger LOGGER = 
			Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private KundeService ks;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private UriHelperKunde uriHelper;
	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(Level.FINER, "Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(Level.FINER, "Bean {0} wurde geloescht", this);
	}
	
	/*
	 * Liefert einen Kunden anhand seiner ID
	 * Aufruf: /kunden/{ID}
	 */
	@GET
	@Path("{id:[0-9]*}")
	public Kunde findKundeById(@PathParam("id") Long id, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Kunde kunde = ks.findKundeById(id, locale);
		if (kunde == null) {
			final String msg = ("Es existiert kein Kunde mit der ID: " + id);
			throw new NotFoundException(msg);
		}
		
		uriHelper.updateUriKunde(kunde, uriInfo);
		return kunde;			
	}
	
	/*
	 * Liefert alle Bestellungen eines Kunden
	 * Aufruf: /kunden/{ID}/bestellungen
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findBestellungenByKundeId(@PathParam("id") Long kundeId,  
															@Context UriInfo uriInfo, 
															@Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		final Collection<Bestellung> bestellungen = bs.findBestellungenByKundeId(kundeId, locale);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Kunde gefunden mit der ID " + kundeId;
			throw new NotFoundException(msg);
		}
		for (Bestellung b : bestellungen) {
			uriHelperBestellung.updateUriBestellung(b, uriInfo);
			}
		return bestellungen;
	}
	
	/*
	 * Liefert alle Kunden zu einem Nachnamen
	 * Aufruf: /kunden?nachname={NACHNAME}
	 */
	@GET
	public Collection<Kunde> findKundenByNachname(@QueryParam("nachname") 
												  @DefaultValue("") String nachname, 
												  @Context UriInfo uriInfo, 
												  @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		Collection<Kunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = ks.findAllKunden();
			if (kunden.isEmpty()) {
				throw new NotFoundException("Keine Kunden vorhanden");
			}
		}
		else {
			kunden = ks.findKundenByNachname(nachname, locale);
			if (kunden.isEmpty()) {
				throw new NotFoundException("Kein Kunde gefunden mit Nachname " + nachname);
			}
		}
		for (Kunde k : kunden) {
			uriHelper.updateUriKunde(k, uriInfo);
			}
		return kunden;
	}
	
	/*
	 * Erzeugt einen neuen Kunden und speichert diesen in der Datenbank
	 * Aufruf: /kunden
	 */
	@POST
	public Response createKunde(Kunde kunde, @Context UriInfo uriInfo, 
								@Context HttpHeaders headers) throws URISyntaxException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		kunde.setId(null);
		Kunde kunde1 = ks.createKunde(kunde, locale);
		URI kundeUri = uriHelper.getUriKunde(kunde1, uriInfo);	
		return Response.created(kundeUri).build();
	}
	
	/*
	 * Aktualisiert einen Kunden und speichert die neuen Werte in der Datenbank
	 * Aufruf: /kunden
	 */
	@PUT
	@Consumes({ APPLICATION_JSON })
	@Produces
	public void updateKunde(Kunde kunde, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		 Kunde kundeoriginal = ks.findKundeById(kunde.getId(), locale);
		 if (kundeoriginal == null) {
			 final String msg = "Kein Kunde gefunden mit der ID " + kunde.getId();
			 throw new NotFoundException(msg);
		 }
		 kunde.setAktualisierungsdatum(kundeoriginal.getAktualisierungsdatum());
		 kunde.setErstelldatum(kundeoriginal.getErstelldatum());
		 kundeoriginal = kunde;
		 ks.updateKunde(kunde, locale);
	}
	
	/*
	 * Löscht einen Kunden anand dessen Id aus der Datenbank
	 * Aufruf: /kunden/{ID}
	 */
	@DELETE
	@Path("{id:[0-9]*}")
	@Produces
	public void deleteKunde(@PathParam("id") Long id, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		Kunde kunde = ks.findKundeById(id, locale);
		ks.deleteKunde(kunde);
	}
}