package fr.esgi.banque.service.impl;

import fr.esgi.banque.business.Compte;
import fr.esgi.banque.business.SoldeInsuffisantException;
import fr.esgi.banque.business.Virement;
import fr.esgi.banque.service.CompteIntrouvableException;
import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VirementServiceImplTest {

	@ParameterizedTest
	@NullSource
	@CsvSource({"0", "-0.01"})
	void doitRefuserUnMontantNonStrictementPositif(BigDecimal montant) {
		// Arrange
		CompteRepository repository = mock(CompteRepository.class);
		NotificationService notifications = mock(NotificationService.class);
		Clock horloge = Clock.fixed(Instant.parse("2026-11-09T10:00:00Z"), ZoneOffset.UTC);
		VirementServiceImpl service = new VirementServiceImpl(repository, notifications, horloge);

		// Act
		Throwable exception = catchThrowable(() -> service.virer("FR001", "FR002", montant));

		// Assert
		assertThat(exception).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void doitRefuserUnVirementVersLeMemeCompte() {
		// Arrange
		CompteRepository repository = mock(CompteRepository.class);
		NotificationService notifications = mock(NotificationService.class);
		Clock horloge = Clock.fixed(Instant.parse("2026-11-09T10:00:00Z"), ZoneOffset.UTC);
		VirementServiceImpl service = new VirementServiceImpl(repository, notifications, horloge);
		String numeroSource = "FR001";
		String numeroDestination = new String("FR001");
		BigDecimal montant = new BigDecimal("10.00");

		// Act
		Throwable exception = catchThrowable(() -> service.virer(numeroSource, numeroDestination, montant));

		// Assert
		assertThat(exception).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({"false, true", "true, false"})
	void doitRefuserUnVirementSiUnCompteEstIntrouvable(boolean sourceExiste, boolean destinationExiste) {
		// Arrange
		CompteRepository repository = mock(CompteRepository.class);
		NotificationService notifications = mock(NotificationService.class);
		Clock horloge = Clock.fixed(Instant.parse("2026-11-09T10:00:00Z"), ZoneOffset.UTC);
		VirementServiceImpl service = new VirementServiceImpl(repository, notifications, horloge);
		Compte source = new Compte("FR001", "Alice", new BigDecimal("100.00"), BigDecimal.ZERO);
		Compte destination = new Compte("FR002", "Bob", BigDecimal.ZERO, BigDecimal.ZERO);
		when(repository.trouver("FR001")).thenReturn(sourceExiste ? Optional.of(source) : Optional.empty());
		when(repository.trouver("FR002")).thenReturn(destinationExiste ? Optional.of(destination) : Optional.empty());

		// Act
		Throwable exception = catchThrowable(() -> service.virer("FR001", "FR002", new BigDecimal("10.00")));

		// Assert
		assertThat(exception).isInstanceOf(CompteIntrouvableException.class);
	}

	@Test
	void doitEffectuerEtSauvegarderUnVirement() {
		// Arrange
		CompteRepository repository = mock(CompteRepository.class);
		NotificationService notifications = mock(NotificationService.class);
		Clock horloge = Clock.fixed(Instant.parse("2026-11-09T10:00:00Z"), ZoneOffset.UTC);
		VirementServiceImpl service = new VirementServiceImpl(repository, notifications, horloge);
		Compte source = new Compte("FR001", "Alice", new BigDecimal("100.00"), BigDecimal.ZERO);
		Compte destination = new Compte("FR002", "Bob", new BigDecimal("25.00"), BigDecimal.ZERO);
		BigDecimal montant = new BigDecimal("25.00");
		when(repository.trouver("FR001")).thenReturn(Optional.of(source));
		when(repository.trouver("FR002")).thenReturn(Optional.of(destination));

		// Act
		Virement virement = service.virer("FR001", "FR002", montant);

		// Assert
		assertThat(source.getSolde()).isEqualByComparingTo("75.00");
		assertThat(destination.getSolde()).isEqualByComparingTo("50.00");
		verify(repository).sauvegarder(source);
		verify(repository).sauvegarder(destination);
		assertThat(virement.numeroSource()).isEqualTo("FR001");
		assertThat(virement.numeroDestination()).isEqualTo("FR002");
		assertThat(virement.montant()).isEqualByComparingTo("25.00");
		assertThat(virement.date()).isEqualTo(LocalDate.of(2026, 11, 9));
	}

	@Test
	void doitRefuserUnVirementSansSauvegardeSiLeSoldeEstInsuffisant() {
		// Arrange
		CompteRepository repository = mock(CompteRepository.class);
		NotificationService notifications = mock(NotificationService.class);
		Clock horloge = Clock.fixed(Instant.parse("2026-11-09T10:00:00Z"), ZoneOffset.UTC);
		VirementServiceImpl service = new VirementServiceImpl(repository, notifications, horloge);
		Compte source = new Compte("FR001", "Alice", new BigDecimal("100.00"), BigDecimal.ZERO);
		Compte destination = new Compte("FR002", "Bob", new BigDecimal("25.00"), BigDecimal.ZERO);
		when(repository.trouver("FR001")).thenReturn(Optional.of(source));
		when(repository.trouver("FR002")).thenReturn(Optional.of(destination));

		// Act
		Throwable exception = catchThrowable(() -> service.virer("FR001", "FR002", new BigDecimal("100.01")));

		// Assert
		assertThat(exception).isInstanceOf(SoldeInsuffisantException.class);
		verify(repository, never()).sauvegarder(any(Compte.class));
		assertThat(source.getSolde()).isEqualByComparingTo("100.00");
		assertThat(destination.getSolde()).isEqualByComparingTo("25.00");
	}
}
