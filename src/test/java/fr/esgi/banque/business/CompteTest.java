package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Test d'exemple fourni pour vérifier que le projet est bien configuré.
 * À compléter (partie A).
 */
class CompteTest {

	@ParameterizedTest
	@CsvSource({
			", Alice",
			"'', Alice",
			"FR001,",
			"FR001, ''"
	})
	void doitRefuserUnNumeroOuUnTitulaireManquant(String numero, String titulaire) {
		// Arrange
		BigDecimal soldeInitial = new BigDecimal("100.00");
		BigDecimal decouvertAutorise = new BigDecimal("200");

		// Act
		Throwable exception = catchThrowable(
				() -> new Compte(numero, titulaire, soldeInitial, decouvertAutorise));

		// Assert
		assertThat(exception).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({
			", 0",
			"0,",
			"0, -0.01"
	})
	void doitRefuserUnSoldeOuUnDecouvertInvalide(BigDecimal soldeInitial, BigDecimal decouvertAutorise) {
		// Arrange
		String numero = "FR001";
		String titulaire = "Alice";

		// Act
		Throwable exception = catchThrowable(
				() -> new Compte(numero, titulaire, soldeInitial, decouvertAutorise));

		// Assert
		assertThat(exception).isInstanceOf(IllegalArgumentException.class);
	}

		@ParameterizedTest
	@CsvSource({
			"0, 0",
			"0, 0.01",
			"-0.01, 0"
	})
	void doitAccepterUnSoldeEtUnDecouvertValides(BigDecimal soldeInitial, BigDecimal decouvertAutorise) {
		// Arrange
		String numero = "FR001";
		String titulaire = "Alice";

		// Act
		Compte compte = new Compte(numero, titulaire, soldeInitial, decouvertAutorise);

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo(soldeInitial);
		assertThat(compte.getDecouvertAutorise()).isEqualByComparingTo(decouvertAutorise);
	}

	@ParameterizedTest
	@NullSource
	@CsvSource({"0", "-0.01"})
	void doitRefuserUnCreditNonStrictementPositif(BigDecimal montant) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act
		Throwable exception = catchThrowable(() -> compte.crediter(montant));

		// Assert
		assertThat(exception).isInstanceOf(IllegalArgumentException.class);
		assertThat(compte.getSolde()).isEqualByComparingTo("100.00");
	}

	@Test
	void doitAccepterUnDebitStrictementPositif() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));
		BigDecimal montant = new BigDecimal("0.01");

		// Act
		compte.debiter(montant);

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("99.99");
	}

	@Test
	void shouldIncreaseBalanceWhenCrediting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act
		compte.crediter(new BigDecimal("50.00"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("150.00");
	}

}
