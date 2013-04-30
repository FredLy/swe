package de.shop.kundenverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Posten;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class KundeService implements Serializable {

	private static final long serialVersionUID = 4360325837484294309L;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	public List<Kunde> findKundenByNachname(String name, Locale locale) {
		validateNachname(name, locale);
		List<Kunde> kunden = em.createNamedQuery(Kunde.KUNDE_BY_NACHNAME, Kunde.class) 
								.setParameter("name", name)
								.getResultList();
		return kunden;
	}
	
	private void validateNachname(String nachname, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "name",
				                                                                           nachname,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidNachnameException(nachname, violations);
	}
	
	public Collection<Kunde> findAllKunden() {
		return (Collection<Kunde>) em.createNamedQuery(Kunde.ALL_KUNDEN, Kunde.class)
								.getResultList();		
	}	
	
	public Kunde findKundeById(Long id, Locale locale) {
		validateKundeId(id, locale);
		return em.find(Kunde.class, id);
	}
	
	public void validateKundeId(Long kundeId, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "id",
				                                                                           kundeId,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidKundeIdException(kundeId, violations);
	}
	
	public Kunde findKundeByEmail(String email, Locale locale) {
		validateEmail(email, locale);
		Kunde kunde;
		try {
			kunde = em.createNamedQuery(Kunde.KUNDE_BY_EMAIL, Kunde.class)
					  .setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
					  .getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
		
		return kunde;
	}
	
	private void validateEmail(String email, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validateValue(Kunde.class,
				                                                                           "email",
				                                                                           email,
				                                                                           Default.class);
		if (!violations.isEmpty())
			throw new InvalidEmailException(email, violations);
	}

	
	public Kunde createKunde(Kunde kunde, Locale locale) {
		if (kunde == null)
			return null;
		kunde.setId(KEINE_ID);
		validateKunde(kunde, locale, Default.class);
		kunde.setErstelldatum(new Date());
		em.persist(kunde);
		return kunde;
	}
	
	private void validateKunde(Kunde kunde, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator.validate(kunde, groups);
		if (!violations.isEmpty()) {
			throw new KundeValidationException(kunde, violations);
		}
	}
	
	public Kunde updateKunde(Kunde kunde, Locale locale) {
		if (kunde == null)
			return null;
		
		validateKunde(kunde, locale, Default.class);
		
		em.detach(kunde);

		Kunde tmp = findKundeById(kunde.getId(), locale);
		if (tmp == null) {
			throw new ConcurrentDeletedException(kunde.getId());
		}
		em.detach(tmp);
		
		//Gibt es ein anderes Objekt mit gleicher Email-Adresse?
		tmp = findKundeByEmail(kunde.getEmail(), locale);
		if (tmp != null) {
			em.detach(tmp);
			if (tmp.getId().longValue() != kunde.getId().longValue()) {
				// anderes Objekt mit gleichem Attributwert fuer email
				throw new EmailExistsException(kunde.getEmail());
			}
		}
		
		kunde.setAktualisierungsdatum(new Date());
		em.merge(kunde);
		return kunde;
	}

	public void deleteKunde(Kunde kunde) { // vorher children löschen. (nicht über bs möglich, da Zirkel)
		if (kunde == null)
			return;
		for(Bestellung b : kunde.getBestellungen()) {
			if (b == null)
				break;
			List<Posten> posten = b.getPosten();
			for (Posten p : posten) {
				em.remove(p);
			}
			em.remove(b);
		}
		em.remove(kunde);
	}
}
