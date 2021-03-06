package de.shop.kundenverwaltung.service;

public class EmailExistsException extends AbstractKundeServiceException {
	private static final long serialVersionUID = 4867667611097919943L;
	private final String email;

	public EmailExistsException(String email) {
		super("Die Email-Adresse " + email + " existiert bereits");
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
}
