package de.shop.artikelverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Log;

@Log
public class ArtikelService implements Serializable {

	private static final long serialVersionUID = 2929027248430844352L;
	
	@PersistenceContext
	private transient EntityManager em;
	
	public Artikel findArtikelById(Long id) {
	    return em.find(Artikel.class, id);
	}
	
	public List<Artikel> findArtikelByIds(List<Long> ids) {
	    List<Artikel> artikel = new ArrayList<Artikel>();
		for (Long l : ids) {
	    	artikel.add(em.find(Artikel.class, l));
	    }
		return artikel;
	}
	
	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		List <Artikel> artikel = em.createNamedQuery(Artikel.ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
					  .setParameter("bezeichnung", bezeichnung)
					  .getResultList();
		return artikel;
	}
	
	public List<String> findBezeichnungenByPrefix(String bezeichnungPrefix) {
		final List<String> bezeichnungen = em.createNamedQuery(Artikel.FIND_BEZEICHNUNGEN_BY_PREFIX, String.class)
				                         .setParameter(Artikel.PARAM_ARTIKEL_BEZEICHNUNG_PREFIX, 
				                        		 	   bezeichnungPrefix + '%')
				                         .getResultList();
		return bezeichnungen;
	}
	
	public Collection<Artikel> findAllArtikel() {
		return (Collection<Artikel>) em.createNamedQuery(Artikel.ALL_ARTIKEL, Artikel.class).getResultList();
	}

	public Artikel createArtikel(Artikel artikel) {
		if (artikel == null)
			return null;
		artikel.setErstelldatum(new Date());
		artikel.setId(KEINE_ID);
		em.persist(artikel);
		return artikel;
	}
	
	public Artikel updateArtikel(Artikel artikel) {
		if (artikel == null)
			return null;
		artikel.setAktualisierungsdatum(new Date());
		em.merge(artikel);
		return artikel;
	}
	
	public void deleteArtikel(Artikel artikel) {
		if (artikel == null) {
			return;
		}
		em.remove(artikel);
	}
}
