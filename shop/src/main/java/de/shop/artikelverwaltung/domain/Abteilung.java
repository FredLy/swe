package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "abteilung")
public class Abteilung implements Serializable {

	private static final long serialVersionUID = 6621511845673794931L;
	
	private static final Long KEINE_ID = null;

	private static final int ERSTE_VERSION = 0;
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@NotBlank
	@Size(min = 2, max = 50, message = "{AbteilungsBezeichnung.msg}")
	@Column(name = "bezeichnung", length = 50, nullable = false)
	private String bezeichnung;
	
	@OneToMany
	@JoinColumn(name = "abteilung_id")
	@XmlTransient
	@JsonIgnore
	private List<Artikel> artikel;
	
	public Abteilung() {
		super();
	}
	
	public Abteilung(String bz) {
		this.id = KEINE_ID;
		this.bezeichnung = bz;
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
	
	public List<Artikel> getArtikel() {
		return Collections.unmodifiableList(artikel);
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
		Abteilung other = (Abteilung) obj;
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
		return "Abteilung [bezeichnung=" + bezeichnung + "]";
	}
	
}
