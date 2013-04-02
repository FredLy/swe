package de.shop.bestellverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Posten;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class BestellungServiceTest extends AbstractTest {
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private ArtikelService as;

	@Inject
	private PostenService ps;
	
	@Test
	public void findBestellungById() {
		//Given
		Long id = Long.valueOf(1);
		
		//When
		Bestellung b = bs.findBestellungById(id);
		
		//Then
		assertThat(id, is(notNullValue()));		
		assertThat(id, is(b.getId()));
	}

	@Test
	public void findBestellungByBezeichnung() {
		//Given
		String bez = "Bestellung_K0_0";
		
		//When
		Bestellung b = bs.findBestellungByBezeichnung(bez);
		
		//Then
		assertThat(bez, is(notNullValue()));		
		assertThat(bez, is(b.getBezeichnung()));
	}
	
	@Test
	public void createBestellung() throws RollbackException, HeuristicMixedException, 
											HeuristicRollbackException, SystemException, NotSupportedException {
		// GIVEN
		final Locale locale = Locale.getDefault();
		final int menge = 5;
		String bezeichnung = "TestBezeichnung";
		
		// WHEN
		Artikel artikel = as.findArtikelById(Long.valueOf(1));
		UserTransaction trans = getUserTransaction();
		trans.commit();
		Bestellung bestellung = new Bestellung();
		Posten posten = new Posten();
		posten.setArtikel(artikel);
		posten.setMenge(menge);
		bestellung.addPosten(posten);
		trans.begin();
		Artikel artikel2 = as.findArtikelById(Long.valueOf(2));
		trans.commit();
		Posten posten2 = new Posten();
		posten2.setArtikel(artikel2);
		posten2.setMenge(menge);
		trans.begin();
		ps.createPosten(posten);
		ps.createPosten(posten2);
		trans.commit();
		bestellung.addPosten(posten2);
		trans.begin();
		Kunde kunde = ks.findKundeById(Long.valueOf(1), locale);
		trans.commit();
		
		bestellung.setBezeichnung(bezeichnung);
		trans.begin();
		bestellung = bs.createBestellung(bestellung, kunde, locale);
		trans.commit();
		
		// THEN
		assertThat(bestellung, is(notNullValue()));
		trans.begin();
		assertThat(bs.findBestellungByBezeichnung(bezeichnung).getBezeichnung(), is(bestellung.getBezeichnung()));
		assertThat(bestellung.getKunde(), is(kunde));
		assertThat(bestellung.getPosten().contains(posten), is(true));
		assertThat(bestellung.getPosten().contains(posten2), is(true));
		assertThat(bestellung.getPosten().get(0), is(posten));
		assertThat(bestellung.getPosten().get(1), is(posten2));
	}
}