package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Virement;
import fr.esgi.banque.service.CompteIntrouvableException;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Objects;

public class VirementServiceImpl {

	private final CompteRepository repository;

	public VirementServiceImpl(CompteRepository repository, NotificationService notifications, Clock horloge) {
		this.repository = repository;
	}

	public Virement virer(String numeroSource, String numeroDestination, BigDecimal montant) {
		if (montant == null || montant.signum() <= 0) {
			throw new IllegalArgumentException("Le montant doit être strictement positif");
		}
		if (Objects.equals(numeroSource, numeroDestination)) {
			throw new IllegalArgumentException("Les comptes source et destination doivent être différents");
		}
		repository.trouver(numeroSource).orElseThrow(() -> new CompteIntrouvableException(numeroSource));
		repository.trouver(numeroDestination).orElseThrow(() -> new CompteIntrouvableException(numeroDestination));
		throw new UnsupportedOperationException("Le virement n'est pas encore implémenté");
	}
}
