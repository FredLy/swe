package de.shop.kundenverwaltung.domain;

import static javax.persistence.FetchType.EAGER;
import javax.persistence.UniqueConstraint;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;






import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import de.shop.auth.service.jboss.AuthService.RolleType;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.*;


@Entity
@Table(name = "kunde")
@NamedQueries({
	@NamedQuery(name = Kunde.KUNDE_BY_NACHNAME,	
			query = "SELECT k FROM Kunde k WHERE k.name = :name"),
	@NamedQuery(name = Kunde.KUNDE_BY_ID, 
			query = "SELECT k FROM Kunde k WHERE k.id = :id"),
	@NamedQuery(name = Kunde.ALL_KUNDEN,
			query = "SELECT k FROM Kunde k"),
	@NamedQuery(name = Kunde.KUNDE_BY_EMAIL,
			query = "SELECT k FROM Kunde k WHERE k.email = :email")
})

//TODO @ScriptAssert Passwort ueberpruefen

@XmlRootElement
public class Kunde implements Serializable {
		
	private static final long serialVersionUID = -2886563226290035252L;
	private static final Long KEINE_ID = null;

	private static final String PREFIX = "KundeService.";
	public static final String KUNDE_BY_NACHNAME = PREFIX + "findKundeByNachname";
	public static final String KUNDE_BY_ID = PREFIX + "findKundeById";
	public static final String ALL_KUNDEN = PREFIX + "findAllKunden";
	public static final String KUNDE_BY_EMAIL = PREFIX + "findKundeByEmail";
	public static final String PARAM_KUNDE_EMAIL = "email";
	private static final int ERSTE_VERSION = 0;
	

		
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)
	@Min(value = 1, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Email
	@Column(name = "email", length = 50, nullable = false, unique = true)
	@XmlAttribute
	private String email;
	
	@NotBlank
	@Size(min = 2, max = 50, message = "{Kundenname.msg}")
	@Pattern(regexp = "[A-Zƒ÷‹][a-z‰ˆ¸ﬂ]+(-[A-Zƒ÷‹][a-z‰ˆ¸ﬂ]+)?", message = "{Kunderegexp.msg}")
	@Column(name = "name", length = 50, nullable = false)
	@XmlElement
	private String name;
	
	@NotBlank
	@Size(min = 2, max = 50, message = "{Kundenvorname.msg}")
	@Pattern(regexp = "[A-Zƒ÷‹][a-z‰ˆ¸ﬂ]+(-[A-Zƒ÷‹][a-z‰ˆ¸ﬂ]+)?", message = "{Kunderegexp.msg}")
	@Column(name = "vorname", length = 50, nullable = false)
	@XmlElement
	private String vorname;
	
	@NotBlank
	@Size(min = 2, max = 50, message = "{Kundenstrasse.msg}")
	@Column(name = "strasse", length = 50, nullable = false)
	@XmlElement
	private String strasse;
	
	@NotBlank
	@Size(max = 50, message = "{KundenHausnummer.msg}")
	@NotEmpty
	@Column(name = "hausnummer", length = 50, nullable = false)
	@XmlElement
	private String hausnummer;
	
	@NotBlank
	@Size(min = 2, max = 10, message = "{KundenPLZ.msg}")
	@Column(name = "plz", length = 10, nullable = false)
	@XmlElement
	private String plz;
	
	@NotBlank
	@Size(min = 2, max = 50, message = "{KundenOrt.msg}")
	@Column(name = "ort", length = 50, nullable = false)
	@XmlElement
	private String ort;
	
	@Column(length = 40)
	@Size(max = 40, message = "{KundenPasswort.msg}")
	private String password;
	
	@Transient
	@JsonIgnore
	private String passwordWdh;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_rolle",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "rolle_fk" }))
	@Column(table = "kunde_rolle", name = "rolle_fk", nullable = false)
	private Set<RolleType> rollen;
	
	@OneToMany
	@JoinColumn(name = "kunden_id", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@XmlTransient
	@JsonIgnore
	private List<Bestellung> bestellungen;
	
	@Transient
	@XmlElement
	private URI bestellungUri;
	
	@XmlTransient
	@JsonIgnore
	@Temporal(TemporalType.DATE)
	private Date erstelldatum;
	
	@XmlTransient
	@JsonIgnore
	@Temporal(TemporalType.DATE)
	private Date aktualisierungsdatum;
	
	public Kunde() {
		super();
	}
	
	public Kunde(String email, String name, String vorname, String strasse, String hausnummer, String plz, String ort) {
		
		this.id = KEINE_ID;
		this.email = email;
		this.name = name;
		this.vorname = vorname;
		this.strasse = strasse;
		this.hausnummer = hausnummer;
		this.plz = plz;
		this.ort = ort;
	}
	
	public List<Bestellung> getBestellungen()  {
		return Collections.unmodifiableList(bestellungen);
	}
	
	public void setBestellungen(List<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}
	
		this.bestellungen.clear();
		if (bestellungen != null) {
			this.bestellungen.addAll(bestellungen);
		}
	}
	
	public Kunde addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new ArrayList<>();
		}
		bestellungen.add(bestellung);
		return this;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVorname() {
		return vorname;
	}
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	public String getStrasse() {
		return strasse;
	}
	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}
	public String getHausnummer() {
		return hausnummer;
	}
	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}
	public String getPlz() {
		return plz;
	}
	public void setPlz(String plz) {
		this.plz = plz;
	}
	public String getOrt() {
		return ort;
	}
	public void setOrt(String ort) {
		this.ort = ort;
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
	public URI getBestellungUri() {
		return bestellungUri;
	}
	public void setBestellungUri(URI bestellungUri) {
		this.bestellungUri = bestellungUri;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordWdh() {
		return passwordWdh;
	}
	public void setPasswordWdh(String passwordWdh) {
		this.passwordWdh = passwordWdh;
	}
	public Set<RolleType> getRollen() {
		return rollen;
	}
	public void setRollen(Set<RolleType> rollen) {
		this.rollen = rollen;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		Kunde other = (Kunde) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} 
		else if (!email.equals(other.email))
			return false;
		return true;
	}
	

	@Override
	public String toString() {
		return "Kunde [ID=" 
		+ id 
		+ ", Name=" 
		+ name 
		+ ", Vorname=" 
		+ vorname 
		+ ", Ort=" 
		+ ort 
		+ ", PLZ=" 
		+ plz 
		+ ", Straﬂe=" 
		+ strasse 
		+ ", Hausnummer=" 
		+ hausnummer 
		+ ", Email="
		+ email 
		+  ", Erstelldatum="
		+ erstelldatum 
		+ "]";
	}
}
