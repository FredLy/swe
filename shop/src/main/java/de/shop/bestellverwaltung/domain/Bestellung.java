package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

import de.shop.kundenverwaltung.domain.Kunde;


@Entity
@Table(name = "bestellung")
@NamedQueries({
	@NamedQuery(name = Bestellung.BESTELLUNG_BY_ID, query = "SELECT b FROM Bestellung b WHERE b.id = :id"),
	@NamedQuery(name = Bestellung.BESTELLUNG_BY_BEZEICHNUNG, query =
	"SELECT b FROM Bestellung b WHERE b.bezeichnung = :bezeichnung"),
	@NamedQuery(name = Bestellung.ALL_BESTELLUNGEN, query = "SELECT b FROM Bestellung b"),
	@NamedQuery(name = Bestellung.BESTELLUNGEN_BY_KUNDE_ID, query = 
	"SELECT b FROM Bestellung b WHERE b.kunde.id = :kundeId"),
	@NamedQuery(name = Bestellung.KUNDE_BY_ID, query = "SELECT b.kunde from Bestellung b WHERE b.id = :id"),
	@NamedQuery(name = Bestellung.POSTEN_BY_ID, query = "SELECT p from Posten p WHERE p.bestellung.id = :id")
})
public class Bestellung implements Serializable {

	private static final long serialVersionUID = -4842381972226268777L;
	private static final Long KEINE_ID = null;
	
	private static final String PREFIX = "BestellungService.";
	public static final String BESTELLUNG_BY_ID = PREFIX + "findBestellungById";
	public static final String BESTELLUNG_BY_BEZEICHNUNG = PREFIX + "findBestellungByBezeichnung";
	public static final String ALL_BESTELLUNGEN = PREFIX + "findAllBestellungen";
	public static final String BESTELLUNGEN_BY_KUNDE_ID = PREFIX + "findBestellungenByKundeId";
	public static final String KUNDE_BY_ID = PREFIX + "findKundeById";
	public static final String POSTEN_BY_ID = PREFIX + "findPostenById";
	
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)	
	private Long id = KEINE_ID;
	
	@NotBlank
	@Column(name = "bezeichnung", nullable = false)
	private String bezeichnung;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "kunden_id", nullable = false, insertable = false, updatable = false)
	@JsonIgnore
	private Kunde kunde;
	
	@Transient
	private URI kundeUri;
	
	@OneToMany
	@JoinColumn(name = "bestellungs_id", nullable = false)
	@OrderColumn(name = "idx")
	private List<Posten> posten;
	
	@Temporal(TemporalType.DATE)
	private Date datum;
	
	@Temporal(TemporalType.DATE)
	private Date erstelldatum;
	
	@Temporal(TemporalType.DATE)
	private Date aktualisierungsdatum;
	
	public Bestellung() {
		super();
	}
	
	public Bestellung(String bezeichnung, Kunde kunde, List<Posten> posten) {
		
		this.id = KEINE_ID;
		this.bezeichnung = bezeichnung;
		this.kunde = kunde;
		this.posten = posten;
	}

	public URI getKundeUri() {
		return kundeUri;
	}

	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBezeichnung() {
		return bezeichnung;
	}
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	public Kunde getKunde() {
		return kunde;
	}
	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}
	public List<Posten> getPosten() {
		return Collections.unmodifiableList(posten);
	}
	public void setPosten(List<Posten> posten) {
		if (this.posten == null) {
			this.posten = posten;
			return;
		}
		
		this.posten.clear();
		if (posten != null) {
			this.posten.addAll(posten);
		}
	}
	public Date getDatum() {
		return datum == null ? null : (Date) datum.clone();
	}
	public void setDatum(Date datum) {
		this.datum = datum == null ? null : (Date) datum.clone();
	}
	public Date getErstelldatum() {
		return erstelldatum == null ? null : (Date) erstelldatum.clone();
	}
	public void setErstelldatum(Date erstelldatum) {
		this.erstelldatum = erstelldatum == null ? null : (Date) erstelldatum.clone();
	}
	public Date getAktualisierungsdatum() {
		return aktualisierungsdatum == null ? null : (Date) aktualisierungsdatum.clone();
	}
	public void setAktualisierungsdatum(Date aktualisierungsdatum) {
		this.aktualisierungsdatum = aktualisierungsdatum == null ? null : (Date) aktualisierungsdatum.clone();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bestellung other = (Bestellung) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} 
		else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		return true;
	}
	

	@Override
	public String toString() {
		return "Bestellung [ID=" + id + ", Kunde=" + kunde + ", Datum=" + datum
				+ "]";
	}
	
	public Bestellung addPosten(Posten p) {
		if (this.posten == null) {
			this.posten = new ArrayList<>();
		}
		this.posten.add(p);
		return this;
	}
}
