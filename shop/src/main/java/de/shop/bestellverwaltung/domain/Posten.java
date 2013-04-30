package de.shop.bestellverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.artikelverwaltung.domain.Artikel;

@Entity
@Table(name = "posten")
@NamedQueries({
	@NamedQuery(name = Posten.POSTEN_BY_ID, query = "SELECT p FROM Posten p WHERE p.id = :id"),
})
@XmlRootElement
public class Posten implements Serializable {
	
	private static final long serialVersionUID = -7239623830255216244L;
	private static final Long KEINE_ID = null;

	private static final String PREFIX = "PostenService.";
	public static final String POSTEN_BY_ID = PREFIX + "findPostenById";
	private static final int ERSTE_VERSION = 0;
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)
	@XmlAttribute
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Column(name = "menge", nullable = false, updatable = false)
	@XmlElement
	private int menge;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "bestellungs_id", nullable = true, updatable = false, insertable = false)
	@XmlTransient
	@JsonIgnore
	private Bestellung bestellung;
	
	@ManyToOne
	@JoinColumn(name = "artikel_id", nullable = true, updatable = false, insertable = true)
	@XmlTransient
	@JsonIgnore
	private Artikel artikel;
	
	@Transient
	@XmlElement
	private URI artikelUri;
	
	@Temporal(TemporalType.DATE)
	private Date erstelldatum;
	
	@Temporal(TemporalType.DATE)
	private Date aktualisierungsdatum;
	
	public URI getArtikelUri() {
		return artikelUri;
	}

	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}
	
	public Posten() {
		super();
	}
	
	public Posten(int menge, Bestellung bestellung, Artikel artikel) {
		
		this.id = KEINE_ID;
		this.menge = menge;
		this.bestellung = bestellung;
		this.artikel = artikel;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public int getMenge() {
		return menge;
	}
	public void setMenge(int menge) {
		this.menge = menge;
	}
	
	public Bestellung getBestellung() {
		return bestellung;
	}
	public void setBestellung(Bestellung bestellung) {
		this.bestellung = bestellung;
	}
	
	public Artikel getArtikel() {
		return artikel;
	}
	public void setArtikel(Artikel a) {
		this.artikel = a;
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
				+ ((artikel == null) ? 0 : artikel.hashCode());
		result = prime * result
				+ ((bestellung == null) ? 0 : bestellung.hashCode());
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
		Posten other = (Posten) obj;
		if (artikel.getId() == null) {
			if (other.artikel.getId() != null)
				return false;
		} 
		else if (!artikel.getId().equals(other.artikel.getId()))
			return false;
		if (bestellung == null) {
			if (other.bestellung != null)
				return false;
		} 
		else if (!bestellung.equals(other.bestellung))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Posten: [" + artikel + " , Anzahl: " + menge + "]";
	}
}
