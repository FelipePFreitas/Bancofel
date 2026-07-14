package br.com.felipefreitas.bancofel.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CPFs de teste ("11144477735", "52998224725") são números canônicos
 * usados publicamente para testes, validados algoritmicamente com o mesmo
 * cálculo de módulo 11 implementado em CPFUtil.
 */
class CPFUtilTest {

    // ---------------------------------------------------------------
    // isValid
    // ---------------------------------------------------------------

    @Test
    void isValid_deveRetornarFalse_quandoCpfNulo() {
        assertFalse(CPFUtil.isValid(null));
    }

    @Test
    void isValid_deveRetornarFalse_quandoMenosDe11Digitos() {
        assertFalse(CPFUtil.isValid("123456789"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoMaisDe11Digitos() {
        assertFalse(CPFUtil.isValid("123456789012"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoTodosDigitosIguais() {
        assertFalse(CPFUtil.isValid("11111111111"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoDigitosVerificadoresInvalidos() {
        // CPF válido "11144477735" com o último dígito alterado
        assertFalse(CPFUtil.isValid("11144477736"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoPrimeiroDigitoVerificadorInvalido() {
        // Altera o primeiro dígito verificador (posição 9), mantendo o segundo
        assertFalse(CPFUtil.isValid("11144477825"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCpfValidoSemFormatacao() {
        assertTrue(CPFUtil.isValid("11144477735"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoOutroCpfValidoSemFormatacao() {
        assertTrue(CPFUtil.isValid("52998224725"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCpfValidoComFormatacao() {
        assertTrue(CPFUtil.isValid("529.982.247-25"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoContemLetras() {
        // Letras são removidas pelo replaceAll("\\D",""), sobrando menos de 11 dígitos
        assertFalse(CPFUtil.isValid("111.444.777-3A"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoStringVazia() {
        assertFalse(CPFUtil.isValid(""));
    }

    @Test
    void isValid_deveRetornarFalse_quandoApenasEspacos() {
        assertFalse(CPFUtil.isValid("   "));
    }
}