package de.shop.kundenverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.auth.service.jboss.AuthService;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Posten;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.File;
import de.shop.util.FileHelper;
import de.shop.util.FileHelper.MimeType;
import de.shop.util.Log;
import de.shop.util.NoMimeTypeException;
import de.shop.util.ValidatorProvider;

@Log
public class KundeService implements Serializable {

	private static final long serialVersionUID = 4360325837484294309L;
	
	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private ValidatorProvider validatorProvider;
	
	@Inject
	private AuthService authService;
	
	@Inject
	private FileHelper fileHelper;
	
	@Inject
	@NeuerKunde
	private transient Event<Kunde> event;
	
	public List<Kunde> findKundenByNachname(String name, Locale locale) {
		validateNachname(name, locale);
		List<Kunde> kunden = em.createNamedQuery(Kunde.KUNDE_BY_NACHNAME, Kunde.class) 
								.setParameter("name", name)
								.getResultList();
		return kunden;
	}
	
	public List<Kunde> findKundenByNachname(String name) {
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
	
	public Kunde findKundeById(Long id) {
		Kunde k = em.find(Kunde.class, id); 
		return k;
	}
	
	public List<Kunde> findKundenByIdPrefix(Long id) {
		if (id == null) {
			return Collections.emptyList();
		}
		
		final List<Kunde> kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_BY_ID_PREFIX,
				                                               Kunde.class)
				                             .setParameter(Kunde.PARAM_KUNDE_ID_PREFIX, id.toString() + '%')
				                             .getResultList();
		return kunden;
	}
	
	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		final List<String> nachnamen = em.createNamedQuery(Kunde.FIND_NACHNAMEN_BY_PREFIX, String.class)
				                         .setParameter(Kunde.PARAM_KUNDE_NACHNAME_PREFIX, nachnamePrefix + '%')
				                         .getResultList();
		return nachnamen;
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
		passwordVerschluesseln(kunde);
		//event.fire(kunde); Impl Observer
		kunde.setBestellungen(new ArrayList<Bestellung>());
		em.persist(kunde);
		return kunde;
	}
	
	private void passwordVerschluesseln(Kunde kunde) {
		//logger.debugf("passwordVerschluesseln BEGINN: %s", kunde);

		final String unverschluesselt = kunde.getPassword();
		final String verschluesselt = authService.verschluesseln(unverschluesselt);
		kunde.setPassword(verschluesselt);
		kunde.setPasswordWdh(verschluesselt);

		//logger.debugf("passwordVerschluesseln ENDE: %s", verschluesselt);
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
		for (Bestellung b : kunde.getBestellungen()) {
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
		
	public void setFile(Long kundeId, byte[] bytes, Locale locale) {
		final Kunde kunde = findKundeById(kundeId, locale);
		if (kunde == null) {
			return;
		}
		final MimeType mimeType = fileHelper.getMimeType(bytes);
		setFile(kunde, bytes, mimeType);
	}
	
	/**
	 * Mit MIME-Type fuer Upload bei Webseiten
	 */
	public void setFile(Kunde kunde, byte[] bytes, String mimeTypeStr) {
		final MimeType mimeType = MimeType.get(mimeTypeStr);
		setFile(kunde, bytes, mimeType);
	}
	
	private void setFile(Kunde kunde, byte[] bytes, MimeType mimeType) {
		if (mimeType == null) {
			throw new NoMimeTypeException();
		}
		
		final String filename = fileHelper.getFilename(kunde.getClass(), kunde.getId(), mimeType);
		
		// Gibt es noch kein (Multimedia-) File
		File file = kunde.getFile();
		if (file == null) {
			file = new File(bytes, filename, mimeType);
			kunde.setFile(file);
			em.persist(file);
		}
		else {
			file.set(bytes, filename, mimeType);
			em.merge(file);
		}
	}
}
