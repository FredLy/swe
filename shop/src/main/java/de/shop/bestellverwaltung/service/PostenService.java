package de.shop.bestellverwaltung.service;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.shop.bestellverwaltung.domain.Posten;
import de.shop.util.Log;

@Log
public class PostenService implements Serializable {
	private static final long serialVersionUID = 3967356803638760510L;

	@PersistenceContext
	private transient EntityManager em;
	
	public Posten findPostenById(Long id) {
		Posten posten = 
				em.createNamedQuery(Posten.POSTEN_BY_ID, Posten.class)
				.setParameter("id", id).getSingleResult();
		return posten;
	}
	
	public Posten createPosten(Posten posten) {
		if (posten == null)
			return null;
		posten.setErstelldatum(new Date());
		em.persist(posten);
		return posten;
	}
	
	public Posten updatePosten(Posten posten) {
		if (posten == null)
			return null;
		posten.setAktualisierungsdatum(new Date());
		em.merge(posten);
		return posten;
	}
}
