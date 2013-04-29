package de.shop.kundenverwaltung.domain;
import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class KundeTest extends AbstractDomainTest {
	
	@Test
	public void findKundeById() {
		// GIVEN
		Long id = Long.valueOf(1);
		
		// WHEN
		Kunde k1 = getEntityManager().find(Kunde.class, id);
		
		// THEN
		assertThat(k1, is(notNullValue()));
		assertThat(id, is(k1.getId()));
	}

	@Test
	public void findKundeByNachname() {
		// GIVEN
		String name = "Angstmann";
		
		// WHEN
		List<Kunde> kunden = getEntityManager().createNamedQuery(Kunde.KUNDE_BY_NACHNAME, Kunde.class) 
				.setParameter("name", name)
				.getResultList();
		
		// THEN
		assertThat(kunden, is(notNullValue()));
		for (Kunde k : kunden) {
			assertThat(k.getName(), is(name));
		}
	}
	
	@Test
	public void createKunde() {
		// GIVEN
		String vorname = "Testvorname";
		String name = "Testnachname";
		String email = "Test@test.de";
		String strasse = "Teststrasse";
		String hausnummer = "Testhausnummer";
		String plz = "TestPLZ";
		String ort = "TestOrt";
		
		// WHEN
		Kunde kunde = new Kunde();
		kunde.setVorname(vorname);
		kunde.setName(name);
		kunde.setEmail(email);
		kunde.setStrasse(strasse);
		kunde.setHausnummer(hausnummer);
		kunde.setPlz(plz);
		kunde.setOrt(ort);
		getEntityManager().persist(kunde);
		// New List<Kunde>(KUNDE_BY_NACHNAME) should contain kunde
		List<Kunde> kundeList = getEntityManager().createNamedQuery(Kunde.KUNDE_BY_NACHNAME, Kunde.class)
						.setParameter("name", name)
						.getResultList();
		
		// THEN
		assertThat(kundeList.contains(kunde), is(true));
	}
}
