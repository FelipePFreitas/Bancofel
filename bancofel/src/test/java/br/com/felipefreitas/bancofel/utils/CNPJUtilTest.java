package br.com.felipefreitas.bancofel.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CNPJs de teste validados algoritmicamente com o mesmo cálculo (módulo 11
 * adaptado à tabela alfanumérica da Receita Federal) implementado em
 * CNPJUtil:
 * - "11222333000181" é um CNPJ numérico de teste amplamente conhecido.
 * - "12ABC34DE56F86" é um CNPJ alfanumérico válido calculado especificamente
 *   para estes testes (novo formato de CNPJ alfanumérico).
 */
class CNPJUtilTest {

    // ---------------------------------------------------------------
    // isValid
    // ---------------------------------------------------------------

    @Test
    void isValid_deveRetornarFalse_quandoCnpjNulo() {
        assertFalse(CNPJUtil.isValid(null));
    }

    @Test
    void isValid_deveRetornarFalse_quandoFormatoInvalido() {
        assertFalse(CNPJUtil.isValid("123"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoTamanhoDiferenteDe14SemMascara() {
        assertFalse(CNPJUtil.isValid("1122233300018")); // 13 caracteres
    }

    @Test
    void isValid_deveRetornarFalse_quandoMascaraComPosicoesErradas() {
        // barra e hífen fora do lugar esperado pelo padrão
        assertFalse(CNPJUtil.isValid("112.223.330/0018-1"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoSequenciaNumericaRepetida() {
        assertFalse(CNPJUtil.isValid("11111111111111"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoDigitosVerificadoresInvalidos() {
        // CNPJ válido "11222333000181" com o último dígito alterado
        assertFalse(CNPJUtil.isValid("11222333000180"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoPrimeiroDigitoVerificadorInvalido() {
        // Altera o dígito na posição 12, mantendo o segundo dígito original
        assertFalse(CNPJUtil.isValid("11222333000191"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCnpjNumericoValidoSemMascara() {
        assertTrue(CNPJUtil.isValid("11222333000181"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCnpjNumericoValidoComMascara() {
        assertTrue(CNPJUtil.isValid("11.222.333/0001-81"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCnpjAlfanumericoValidoSemMascara() {
        assertTrue(CNPJUtil.isValid("12ABC34DE56F86"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCnpjAlfanumericoValidoComMascara() {
        assertTrue(CNPJUtil.isValid("12.ABC.34D/E56F-86"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCnpjAlfanumericoMinusculo() {
        assertTrue(CNPJUtil.isValid("12.abc.34d/e56f-86"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCnpjComEspacosNasExtremidades() {
        assertTrue(CNPJUtil.isValid("  11222333000181  "));
    }

    @Test
    void isValid_deveRetornarFalse_quandoCnpjAlfanumericoComDigitoVerificadorInvalido() {
        assertFalse(CNPJUtil.isValid("12ABC34DE56F87"));
    }

    // ---------------------------------------------------------------
    // clean
    // ---------------------------------------------------------------

    @Test
    void clean_deveRetornarStringVazia_quandoNulo() {
        assertEquals("", CNPJUtil.clean(null));
    }

    @Test
    void clean_deveRemoverMascara() {
        assertEquals("11222333000181", CNPJUtil.clean("11.222.333/0001-81"));
    }

    @Test
    void clean_deveConverterParaMaiusculo() {
        assertEquals("12ABC34DE56F86", CNPJUtil.clean("12.abc.34d/e56f-86"));
    }

    @Test
    void clean_deveManterStringInalterada_quandoJaLimpaEMaiuscula() {
        assertEquals("11222333000181", CNPJUtil.clean("11222333000181"));
    }
}