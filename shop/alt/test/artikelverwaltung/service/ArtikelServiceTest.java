package de.shop.artikelverwaltung.service;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class ArtikelServiceTest extends AbstractTest {
	
	@Inject
	private ArtikelService as;
	
	@Test
	public void findArtikelByID() {
		// GIVEN
		Long id = Long.valueOf(1);
		
		// WHEN
		Artikel artikel = as.findArtikelById(id);
		
		// THEN
		assertThat(artikel, is(notNullValue()));
		assertThat(id, is(artikel.getId()));
	}
	
	@Test
	public void findArtikelByBezeichnung() {
		// GIVEN
		String bezeichnung = "Schicker Anzug";
		
		// WHEN
		List<Artikel> artikel = as.findArtikelByBezeichnung(bezeichnung);
		
		// THEN
		assertThat(artikel, is(notNullValue()));
		for (Artikel a : artikel) {
			assertThat(a.getBezeichnung(), is(bezeichnung));
		}
	}
	
	@Test
	public void createArtikel() {
		// GIVEN
		String bezeichnung = "TestBezeichnung";
		
		// WHEN
		Artikel artikel = new Artikel();
		artikel.setBezeichnung(bezeichnung);
		as.createArtikel(artikel);
		
		// THEN
		// New List<Artikel>(ARTIKEL_BY_BEZEICHNUNG) should contain artikel
		List<Artikel> artikelList = as.findArtikelByBezeichnung(bezeichnung);
		assertThat(artikelList, is(notNullValue()));
		assertThat(artikelList.contains(artikel), is(true));
	}
	
	@Test
	public void updateArtikel() {
		// GIVEN
		String bezeichnungOld = "TestBezeichnungOld";
		String bezeichnungNew = "TestBezeichnungNew";
		
		// WHEN
		Artikel artikel = new Artikel();
		artikel.setBezeichnung(bezeichnungOld);
		as.createArtikel(artikel);
		artikel.setBezeichnung(bezeichnungNew);
		as.updateArtikel(artikel);
		
		// THEN
		List<Artikel> artikelListOld = as.findArtikelByBezeichnung(bezeichnungOld);
		assertThat(artikelListOld.contains(artikel), is(false));
		List<Artikel> artikelListNew = as.findArtikelByBezeichnung(bezeichnungNew);
		assertThat(artikelListNew.contains(artikel), is(true));
	}
}
