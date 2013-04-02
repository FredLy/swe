package de.shop.bestellverwaltung.rest;


import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.rest.UriHelperArtikel;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Posten;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.rest.UriHelperKunde;
import de.shop.util.Log;


@ApplicationScoped
@Log
public class UriHelperBestellung {

	@Inject 
	private UriHelperKunde uriHelperKunde;
	
	@Inject 
	private UriHelperArtikel uriHelperArtikel;
	
	public URI getUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(BestellungResource.class)
		                             .path(BestellungResource.class, "findBestellungById");
		final URI bestellungUri = ub.build(bestellung.getId());
		return bestellungUri;
	}
	
	public void updateUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		//KundeUri
		Kunde kunde = bestellung.getKunde();
		if (kunde != null) {
			URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
			bestellung.setKundeUri(kundeUri);
		}
		//ArtikelUri
		final List<Posten> posten = bestellung.getPosten();
		if (posten != null && !posten.isEmpty()) {
			for (Posten p : posten) {
				final URI artikelUri = uriHelperArtikel.getUriArtikel(p.getArtikel(), uriInfo);
				p.setArtikelUri(artikelUri);
			}
		}
	}
} 