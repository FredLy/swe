package de.shop.bestellverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Posten;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.ValidatorProvider;

@Log
public class BestellungService implements Serializable {
	private static final long serialVersionUID = -6216590274350463462L;
	private static final Logger LOGGER = 
			Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ValidatorProvider validatorProvider;
		
	public Bestellung findBestellungById(Long id) {
		Bestellung bestellung = em.find(Bestellung.class, id);
		if (bestellung == null) 
			throw new NotFoundException("Keine Bestellung gefunden mit der ID: " + id);
		return bestellung;
	}
	
	public Bestellung findBestellungByBezeichnung(String bezeichnung) {
		Bestellung bestellung = 
				em.createNamedQuery(Bestellung.BESTELLUNG_BY_BEZEICHNUNG, Bestellung.class)
				.setParameter("bezeichnung", bezeichnung).getSingleResult();
		if (bestellung == null) 
			throw new NotFoundException("Keine Bestellung gefunden mit der Bezeichnung: " + bezeichnung);
		return bestellung;
	}
	
	public Collection<Bestellung> findBestellungenByKundeId(Long kundeId, Locale locale) {
		ks.validateKundeId(kundeId, locale);
		Collection<Bestellung> bestellungen = em.createNamedQuery(Bestellung.BESTELLUNGEN_BY_KUNDE_ID, Bestellung.class)
												.setParameter("kundeId", kundeId).getResultList();
		if (bestellungen == null) 
			throw new NotFoundException("Keine Bestellungen gefunden zu der Kunden-ID: " + kundeId);
		return bestellungen;
	}
	
	public Collection<Bestellung> findAllBestellungen() {
		Collection<Bestellung> bestellungen = em.createNamedQuery(Bestellung.ALL_BESTELLUNGEN, Bestellung.class)
												.getResultList();
		if (bestellungen == null)
			throw new NotFoundException("Keine Bestellungen vorhanden");
		return bestellungen;
				
	}
	
	public Kunde findKundeById(Long id) {
		Kunde kunde = em.createNamedQuery(Bestellung.KUNDE_BY_ID, Kunde.class)
						.setParameter("id", id)
						.getSingleResult();
		if (kunde == null)
			throw new NotFoundException("Keine Bestellung gefunden mit der ID: " + id);
		return kunde;
	}
	
	public List<Posten> findPostenById(Long id) {
		return (List<Posten>) em.createNamedQuery(Bestellung.POSTEN_BY_ID, Posten.class)
								.setParameter("id", id)
								.getResultList();
	}

	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale) {
		if (bestellung == null)
			return null;
		bestellung.setErstelldatum(new Date());
		bestellung.setAktualisierungsdatum(null);
		kunde = ks.findKundeById(kunde.getId(), locale);
		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);
		bestellung.setBezeichnung("blablabla :)");
		
		bestellung.setId(KEINE_ID);
		for (Posten p : bestellung.getPosten()) {
			p.setId(KEINE_ID);
			p.setErstelldatum(new Date());
			p.setAktualisierungsdatum(null);
		}
		validateBestellung(bestellung, locale, Default.class);
		for (Posten p : bestellung.getPosten()) {
			em.persist(p);
		}
		em.persist(bestellung);
		//event.fire(bestellung);
		return bestellung;
	}
	
	private void validateBestellung(Bestellung bestellung, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		
		final Set<ConstraintViolation<Bestellung>> violations = validator.validate(bestellung);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.exiting("BestellungService", "createBestellung", violations);
			throw new BestellungValidationException(bestellung, violations);
		}
	}
	
	public Bestellung updateBestellung(Bestellung bestellung) {
		if (bestellung == null)
			return null;
		bestellung.setAktualisierungsdatum(new Date());
		em.merge(bestellung);
		return bestellung;
	}
	
	public void deleteBestellung(Bestellung bestellung) {
		if (bestellung == null)
			return;
		List<Posten> posten = findPostenById(bestellung.getId());
		for (Posten p : posten) {
			em.remove(p);
		}
		em.remove(bestellung);
	}
}
