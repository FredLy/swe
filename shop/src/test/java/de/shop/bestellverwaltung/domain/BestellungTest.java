package de.shop.bestellverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class BestellungTest extends AbstractDomainTest {
	@Test
	public void findBestellungById() {
		//Given
		Long id = Long.valueOf(1);
		//When
		Bestellung b = getEntityManager().find(Bestellung.class, id);
		
		//Then
		//Bestellung ist vorhanden
		assertThat(b, is(notNullValue()));		
		//ID von Bestellungen stimmen ueberein
		assertThat(id, is(b.getId()));
	}
	
	@Test
	public void findBestellungByBezeichnung() {
		//Given
		String bez = "Bestellung_K0_0";
		
		//When
		Bestellung b = getEntityManager().createNamedQuery(Bestellung.BESTELLUNG_BY_BEZEICHNUNG, Bestellung.class)
						.setParameter("bezeichnung", bez)
						.getSingleResult();
		
		//Then
		//Bestellung ist vorhanden
		assertThat(b, is(notNullValue()));		
		//Bezeichnung von Bestellungen stimmen ueberein
		assertThat(bez, is(b.getBezeichnung()));
	}
	
	@Test
	public void createBestellung() {
		// GIVEN
		String bezeichnung = "TestBestellung";
		
		// WHEN
		Bestellung bestellung = new Bestellung();
		bestellung.setBezeichnung(bezeichnung);
		bestellung.setKunde(getEntityManager().createNamedQuery(Kunde.KUNDE_BY_ID, Kunde.class)
					.setParameter("id", Long.valueOf(1))
					.getSingleResult());
		getEntityManager().persist(bestellung);
		// New List<Bestellung>(BESTELLUNG_BY_BEZEICHNUNG) should contain bestellung
		List<Bestellung> bestellungList = 
				getEntityManager().createNamedQuery(Bestellung.BESTELLUNG_BY_BEZEICHNUNG, Bestellung.class)
				.setParameter("bezeichnung", bezeichnung)
				.getResultList();
		
		// THEN
		assertThat(bestellungList.contains(bestellung), is(true));
	}
}
