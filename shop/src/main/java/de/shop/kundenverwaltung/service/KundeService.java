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
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.kundenverwaltung.domain.Kunde;
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
		kunde.setAktualisierungsdatum(new Date());
		em.merge(kunde);
		return kunde;
	}

	public void deleteKunde(Kunde kunde) {
		if (kunde == null)
			return;
		em.remove(kunde);
	}
}
