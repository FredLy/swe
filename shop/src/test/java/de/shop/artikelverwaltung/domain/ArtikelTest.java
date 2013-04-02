package de.shop.artikelverwaltung.domain;
import java.util.List;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class ArtikelTest extends AbstractDomainTest {

	@Test
	public void findArtikelByID() {
		// GIVEN
		Long id = Long.valueOf(1);
		
		// WHEN
		Artikel artikel = getEntityManager().find(Artikel.class, id);
		
		// THEN
		assertThat(artikel, is(notNullValue()));
		assertThat(id, is(artikel.getId()));
	}
	
	@Test
	public void findArtikelByBezeichnung() {
		// GIVEN
		String bezeichnung = "Schicker Anzug";
		
		// WHEN
		List<Artikel> artikel = getEntityManager().createNamedQuery(Artikel.ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
				  .setParameter("bezeichnung", bezeichnung)
				  .getResultList();
		
		// THEN
		for (Artikel a : artikel) {
			assertThat(a.getBezeichnung(), is(bezeichnung));
		}
		assertThat(artikel, is(notNullValue()));
	}
	
	@Test
	public void createArtikel() {
		// GIVEN
		String bezeichnung = "TestArtikel";
		String groesse = "TestGroese";
		final Double preis = 1.1;
		
		// WHEN
		Artikel artikel = new Artikel();
		artikel.setBezeichnung(bezeichnung);
		artikel.setGroesse(groesse);
		artikel.setPreis(preis);
		getEntityManager().persist(artikel);
		// New List<Artikel> (ARTIKEL_BY_BEZEICHNUNG) should contain artikel
		List<Artikel> artikelList = getEntityManager().createNamedQuery(Artikel.ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
				.setParameter("bezeichnung", bezeichnung)
				.getResultList();
		
		// THEN
		assertThat(artikelList.contains(artikel), is(true));
	}
}
