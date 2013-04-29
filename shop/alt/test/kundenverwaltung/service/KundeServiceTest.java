package de.shop.kundenverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class KundeServiceTest extends AbstractTest {
	
	@Inject
	private KundeService ks;
	
	@Test
	public void findKundeById() {
		
		// GIVEN
		final Locale locale = Locale.getDefault();
		Long id = Long.valueOf(1);
		
		// WHEN
		Kunde k1 = (Kunde) ks.findKundeById(id, locale);
		
		// THEN
		assertThat(k1, is(notNullValue()));
		assertThat(id, is(k1.getId()));
	}

	@Test
	public void findKundeByNachname() {
		
		// GIVEN
		final Locale locale = Locale.getDefault();
		String name = "Angstmann";
		
		// WHEN
		List<Kunde> kunden = ks.findKundenByNachname(name, locale);
		
		// THEN
		assertThat(kunden, is(notNullValue()));
		for (Kunde k : kunden) {
			assertThat(k.getName(), is(name));
		}
	}
	
	@Test
	public void createKunde() {
		// GIVEN
		final Locale locale = Locale.getDefault();
		String vorname = "Testvorname";
		String name = "Testnachname";
		String email = "Test@test.de";
		String strasse = "Teststrasse";
		String hausnummer = "Testhausnummer";
		String plz = "TestPLZ";
		String ort = "Testort";
		
		// WHEN
		Kunde kunde = new Kunde();
		kunde.setVorname(vorname);
		kunde.setName(name);
		kunde.setEmail(email);
		kunde.setStrasse(strasse);
		kunde.setHausnummer(hausnummer);
		kunde.setPlz(plz);
		kunde.setOrt(ort);
		ks.createKunde(kunde, locale);
		
		// THEN
		// New List<Kunde>(KUNDE_BY_NACHNAME) should contain kunde
		List<Kunde> kundeList = ks.findKundenByNachname(name, locale);
		assertThat(kundeList, is(notNullValue()));
		assertThat(kundeList.contains(kunde), is(true));
	}
	
	@Test
	public void updateKunde() {
		// GIVEN
		final Locale locale = Locale.getDefault();
		String vorname = "Testvorname";
		String name = "Testnachnameold";
		String email = "Test@sume.eu";
		String strasse = "Teststrasse";
		String hausnummer = "Testhausnummer";
		String plz = "TestPLZ";
		String ort = "Testort";
		String nameNew = "Testnachnamenew";
		
		// WHEN
		Kunde kunde = new Kunde();
		kunde.setVorname(vorname);
		kunde.setName(name);
		kunde.setEmail(email);
		kunde.setStrasse(strasse);
		kunde.setHausnummer(hausnummer);
		kunde.setPlz(plz);
		kunde.setOrt(ort);
		ks.createKunde(kunde, locale);
		kunde.setName(nameNew);
		ks.updateKunde(kunde, locale);
		
		// THEN
		List<Kunde> kunden = ks.findKundenByNachname(name, locale); 
		assertThat(kunden.contains(kunde), is(false));
		List<Kunde> kundenNew = ks.findKundenByNachname(nameNew, locale);
		assertThat(kundenNew.contains(kunde), is(true));
	}
}
