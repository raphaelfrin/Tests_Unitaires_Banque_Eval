package fr.esgi.banque.business;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CalculFraisTest {

	@ParameterizedTest
	@CsvSource({
			"17, 100.00",
			"0, 100.00",
			"18,"
	})
	void doitRefuserUnTitulaireMineurOuUnSoldeMoyenManquant(int age, BigDecimal soldeMoyen) {
		// Arrange
		CalculFrais calculFrais = new CalculFrais();

		// Act
		Throwable exception = catchThrowable(() -> calculFrais.fraisMensuels(age, soldeMoyen, false));

		// Assert
		assertThat(exception).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({
			"18, 0",
			"19, 100.00"
	})
	void doitAccepterUnTitulaireMajeurEtUnSoldeMoyenRenseigne(int age, BigDecimal soldeMoyen) {
		// Arrange
		CalculFrais calculFrais = new CalculFrais();

		// Act
		Throwable exception = catchThrowable(() -> calculFrais.fraisMensuels(age, soldeMoyen, false));

		// Assert
		assertThat(exception).isNull();
	}

	@ParameterizedTest
	@CsvSource({
			"27, 999.99",
			"30, 0"
	})
	void doitFacturerDeuxEurosDeFraisDeTenue(int age, BigDecimal soldeMoyen) {
		// Arrange
		CalculFrais calculFrais = new CalculFrais();

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, soldeMoyen, false);

		// Assert
		assertThat(frais).isEqualByComparingTo("2.00");
	}

	@ParameterizedTest
	@CsvSource({
			"18, 0.00",
			"25, 0.00",
			"26, 2.00",
			"27, 2.00"
	})
	void doitAppliquerLaGratuiteSelonLAge(int age, BigDecimal fraisAttendus) {
		// Arrange
		CalculFrais calculFrais = new CalculFrais();
		BigDecimal soldeMoyen = new BigDecimal("100.00");

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, soldeMoyen, false);

		// Assert
		assertThat(frais).isEqualByComparingTo(fraisAttendus);
	}
}
