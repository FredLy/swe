package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "kategorie")
public class Kategorie implements Serializable {


	private static final long serialVersionUID = 695676903234443083L;
	
	private static final Long KEINE_ID = null;
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false, updatable = false)
	private Long id = KEINE_ID;
	
	@NotBlank
	@Size(min = 2, max = 50, message = "{KategorieBezeichnung.msg}")
	@Column(name = "bezeichnung", length = 50, nullable = false)
	private String bezeichnung;
	
	@OneToMany
	@JoinColumn(name = "kategorie_id")
	@JsonIgnore
	private List<Artikel> artikel;
	
	public Kategorie() {
		super();
	}
	
	public Kategorie(String bz) {
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
		Kategorie other = (Kategorie) obj;
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
		return "Kategorie [Bezeichnung=" + bezeichnung + "]";
	}
}

