package fr.esgi.banque.service.impl;

import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;

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
}
