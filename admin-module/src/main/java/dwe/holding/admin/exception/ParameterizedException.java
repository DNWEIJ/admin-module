package dwe.holding.admin.exception;

/**
 * Interface die parameterisering implementeert bij exceptions. Zo kunnen meldingen variabelen waarden bevatten, en
 * worden de foutmeldingen flexibel te gebruiken met meerdere doelen.
 *
 */
public interface ParameterizedException {
    String getErrorCode();

    Object[] getParameters();
}