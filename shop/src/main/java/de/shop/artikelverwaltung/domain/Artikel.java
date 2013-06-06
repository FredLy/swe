package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.bestellverwaltung.domain.Posten;

@Entity
@Table(name = "artikel")
@NamedQueries({
	@NamedQuery(name = Artikel.ARTIKEL_BY_ID,
			query = "SELECT a FROM Artikel a WHERE a.id = :id"),
	@NamedQuery(name = Artikel.ARTIKEL_BY_BEZEICHNUNG,
			query = "SELECT a FROM Artikel a WHERE a.bezeichnung = :bezeichnung"),
	@NamedQuery(name  = Artikel.FIND_BEZEICHNUNGEN_BY_PREFIX,
    		query = "SELECT   DISTINCT a.bezeichnung"
	        + " FROM  Artikel a "
	        + " WHERE UPPER(a.bezeichnung) LIKE UPPER(:"
        	+ Artikel.PARAM_ARTIKEL_BEZEICHNUNG_PREFIX + ")"),
	@NamedQuery(name = Artikel.ALL_ARTIKEL,
			query = "SELECT a FROM Artikel a")
})
public class Artikel implements Serializable {

	private static final long serialVersionUID = 4636681345698247017L;
	private static final Long KEINE_ID = null;
	private static final String PREFIX = "ArtikelService.";
	public static final String ARTIKEL_BY_ID = PREFIX + "findArtikelByID";
	public static final String ARTIKEL_BY_BEZEICHNUNG = PREFIX + "findArtikelByBezeichnung";
	public static final String ALL_ARTIKEL = PREFIX + "findAllArtikel";
	public static final String FIND_BEZEICHNUNGEN_BY_PREFIX = PREFIX + "findBezeichnungenByPrefix";
	public static final String PARAM_ARTIKEL_BEZEICHNUNG_PREFIX = "bezeichnungPrefix";
	private static final int ERSTE_VERSION = 0;
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Size(min = 2, max = 50, message = "{ArtikelBezeichnung.msg}")
	@Column(name = "bezeichnung", length = 50, nullable = false)
	private String bezeichnung;
	
	@Column(name = "groesse", length = 10, nullable = true)
	private String groesse;
	
	@DecimalMin("0.0")
	@Column(name = "preis", length = 12, nullable = false)
	private Double preis;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "saison_id", nullable = true, insertable = true, updatable = true)
	private Saison saison;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "kategorie_id", nullable = true, insertable = true, updatable = true)
	private Kategorie kategorie;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "abteilung_id", nullable = true, insertable = true, updatable = true)
	private Abteilung abteilung;
	
	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date erstelldatum;
	
	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private Date aktualisierungsdatum;
	
	@OneToMany
	@JoinColumn(name = "artikel_id")
	@JsonIgnore
	private List<Posten> posten;
	
	public Artikel() {
		super();
	}
	
	public Artikel(String bz, Double p) {
		this.id = KEINE_ID;
		this.bezeichnung = bz;
		this.preis = p;
	}
	
	public List<Posten> getPosten() {
		return Collections.unmodifiableList(posten);
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
	public String getBezeichnung() {
		return bezeichnung;
	}
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	public String getGroesse() {
		return groesse;
	}
	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}
	public Double getPreis() {
		return preis;
	}
	public void setPreis(Double preis) {
		this.preis = preis;
	}
	public Saison getSaison() {
		return saison;
	}
	public void setSaison(Saison saison) {
		this.saison = saison;
	}
	public Kategorie getKategorie() {
		return kategorie;
	}
	public void setKategorie(Kategorie kategorie) {
		this.kategorie = kategorie;
	}
	public Abteilung getAbteilung() {
		return abteilung;
	}
	public void setAbteilung(Abteilung abteilung) {
		this.abteilung = abteilung;
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
		result = prime * result + ((groesse == null) ? 0 : groesse.hashCode());
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
		Artikel other = (Artikel) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} 
		else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		if (groesse == null) {
			if (other.groesse != null)
				return false;
		} 
		else if (!groesse.equals(other.groesse))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artikel: [id: " + id + ", Bezeichnung: " + bezeichnung
				+ ", Groesse: " + groesse + ", Preis: " + preis + "]";
	}
	
	
}
