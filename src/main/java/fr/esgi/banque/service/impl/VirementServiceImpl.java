package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Compte;
import fr.esgi.banque.business.Virement;
import fr.esgi.banque.service.CompteIntrouvableException;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

public class VirementServiceImpl {

	private static final BigDecimal SEUIL_ALERTE = new BigDecimal("10000");

	private final CompteRepository repository;
	private final NotificationService notifications;
	private final Clock horloge;

	public VirementServiceImpl(CompteRepository repository, NotificationService notifications, Clock horloge) {
		this.repository = repository;
		this.notifications = notifications;
		this.horloge = horloge;
	}

	public Virement virer(String numeroSource, String numeroDestination, BigDecimal montant) {
		validerVirement(numeroSource, numeroDestination, montant);
		Compte source = trouverCompte(numeroSource);
		Compte destination = trouverCompte(numeroDestination);
		source.debiter(montant);
		destination.crediter(montant);
		repository.sauvegarder(source);
		repository.sauvegarder(destination);
		if (montant.compareTo(SEUIL_ALERTE) > 0) {
			notifications.alerter(source.getTitulaire(), "Virement supérieur à 10 000 € : " + montant);
		}
		return new Virement(numeroSource, numeroDestination, montant, LocalDate.now(horloge));
	}

	private void validerVirement(String numeroSource, String numeroDestination, BigDecimal montant) {
		if (montant == null || montant.signum() <= 0) {
			throw new IllegalArgumentException("Le montant doit être strictement positif");
		}
		if (Objects.equals(numeroSource, numeroDestination)) {
			throw new IllegalArgumentException("Les comptes source et destination doivent être différents");
		}
	}

	private Compte trouverCompte(String numero) {
		return repository.trouver(numero).orElseThrow(() -> new CompteIntrouvableException(numero));
	}
}
