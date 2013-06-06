package de.shop.artikelverwaltung.controller;

import static de.shop.util.Messages.MessagesType.ARTIKELVERWALTUNG;
import static javax.ejb.TransactionAttributeType.REQUIRED;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Log;
import de.shop.util.Messages;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die ArtikelService
 */
@Named("ac")
@SessionScoped
@Stateful
@Log
public class ArtikelController implements Serializable {
	private static final long serialVersionUID = 1564024850446471639L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String JSF_LIST_ARTIKEL = "/artikelverwaltung/listArtikel";
	private static final String FLASH_ARTIKEL = "artikel";
	
	private static final String JSF_ARTIKELVERWALTUNG = "/artikelverwaltung/";
	private static final String JSF_VIEW_ARTIKEL = JSF_ARTIKELVERWALTUNG + "viewArtikel";
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String JSF_UPDATE_ARTIKEL = JSF_ARTIKELVERWALTUNG + "updateArtikel";
	
	private static final String CLIENT_ID_ARTIKEL_BEZEICHNUNG = "form:bezeichnung";
	private static final String MSG_KEY_ARTIKEL_NOT_FOUND_BY_BEZEICHNUNG = "listArtikel.notFound";
	
	private static final int MAX_AUTOCOMPLETE = 10;
	
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";

	private String bezeichnung;
	
	private Artikel neuerArtikel;
	
	private Long artikelId;
	private Artikel artikel;
	
	private List<Artikel> artikels = Collections.emptyList();
	
	private List<Artikel> ladenhueter;

	@Inject
	private Messages messages;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpSession session;

	
	public List<Artikel> getArtikels() {
		return artikels;
	}

	public void setArtikels(List<Artikel> artikels) {
		this.artikels = artikels;
	}

	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}

	public void setNeuerArtikel(Artikel neuerArtikel) {
		this.neuerArtikel = neuerArtikel;
	}
	
	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}


	public List<Artikel> getLadenhueter() {
		return ladenhueter;
	}

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@Override
	public String toString() {
		return "ArtikelController [bezeichnung=" + bezeichnung + "]";
	}

	@Transactional
	public String findArtikelByBezeichnung() {
		artikels = as.findArtikelByBezeichnung(bezeichnung);
		flash.put(FLASH_ARTIKEL, artikels);

		return JSF_LIST_ARTIKEL;
	}
	
	/**
	 * F&uuml;r rich:autocomplete
	 * @return Liste der potenziellen Bezeinungen
	 */
	@TransactionAttribute(REQUIRED)
	public List<String> findBezeichnungenByPrefix(String bezeichnungPrefix) {
		// NICHT: Liste von Kunden. Sonst waeren gleiche Nachnamen mehrfach vorhanden.
		final List<String> bezeichnungen = as.findBezeichnungenByPrefix(bezeichnungPrefix);
		if (bezeichnungen.isEmpty()) {
			messages.error(ARTIKELVERWALTUNG, MSG_KEY_ARTIKEL_NOT_FOUND_BY_BEZEICHNUNG, CLIENT_ID_ARTIKEL_BEZEICHNUNG, artikelId);
			return bezeichnungen;
		}

		if (bezeichnungen.size() > MAX_AUTOCOMPLETE) {
			return bezeichnungen.subList(0, MAX_AUTOCOMPLETE);
		}

		return bezeichnungen;
	}
	
	@TransactionAttribute(REQUIRED)
	public String details(Artikel ausgewaehlterArtikel) {
		if (ausgewaehlterArtikel == null) {
			return null;
		}
		
		// Bestellungen nachladen
		this.artikel = as.findArtikelById(ausgewaehlterArtikel.getId());
		this.artikelId = this.artikel.getId();
		
		return JSF_VIEW_ARTIKEL;
	}
	
	@Transactional
	public String selectArtikel() {
//		if (session.getAttribute(SESSION_VERFUEGBARE_ARTIKEL) != null) {
//			return JSF_SELECT_ARTIKEL;
//		}
		
		final List<Artikel> alleArtikel = (List<Artikel>) as.findAllArtikel();
		session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		return JSF_SELECT_ARTIKEL;
	}
	
	public void createEmptyArtikel() {
		if (neuerArtikel != null) {
			return;
		}
		neuerArtikel = new Artikel();
	}
	
	@TransactionAttribute(REQUIRED)
	public String createArtikel() {
		// Liste von Strings als Set von Enums konvertieren
		try {
			neuerArtikel = as.createArtikel(neuerArtikel);
		}catch(Exception e){}
//		catch (InvalidKundeException | EmailExistsException e) {
//			final String outcome = createPrivatkundeErrorMsg(e);
//			return outcome;
//		}

		// Push-Event fuer Webbrowser
//		neuerArtikelEvent.fire(String.valueOf(neuerArtikel.getId()));
		
		// Aufbereitung fuer viewArtikel.xhtml
		artikelId = neuerArtikel.getId();
		artikel = neuerArtikel;
		neuerArtikel = null;  // zuruecksetzen
		
		final List<Artikel> alleArtikel = (List<Artikel>) as.findAllArtikel();
		session.setAttribute(SESSION_VERFUEGBARE_ARTIKEL, alleArtikel);
		return JSF_SELECT_ARTIKEL;// JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	public String selectForUpdate(Artikel ausgewaehlterArtikel) {
		if (ausgewaehlterArtikel == null) {
			return null;
		}
		artikel = ausgewaehlterArtikel;
		return JSF_UPDATE_ARTIKEL;
	}
}
