package br.com.felipefreitas.bancofel.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CEPUtilTest {

    // ---------------------------------------------------------------
    // isValid
    // ---------------------------------------------------------------

    @Test
    void isValid_deveRetornarFalse_quandoCepNulo() {
        assertFalse(CEPUtil.isValid(null));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCepComHifen() {
        assertTrue(CEPUtil.isValid("06460-120"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCepSemHifen() {
        assertTrue(CEPUtil.isValid("06460120"));
    }

    @Test
    void isValid_deveRetornarTrue_quandoCepComPontosEEspacos() {
        assertTrue(CEPUtil.isValid(" 06.460-120 "));
    }

    @Test
    void isValid_deveRetornarFalse_quandoMenosDe8Digitos() {
        assertFalse(CEPUtil.isValid("1234567"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoMaisDe8Digitos() {
        assertFalse(CEPUtil.isValid("123456789"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoTodosDigitosIguais() {
        assertFalse(CEPUtil.isValid("00000000"));
    }

    @Test
    void isValid_deveRetornarFalse_quandoStringVazia() {
        assertFalse(CEPUtil.isValid(""));
    }

    @Test
    void isValid_deveRetornarFalse_quandoApenasLetras() {
        // Letras são removidas por clean(), sobrando string vazia (tamanho != 8)
        assertFalse(CEPUtil.isValid("ABCDEFGH"));
    }

    // ---------------------------------------------------------------
    // clean
    // ---------------------------------------------------------------

    @Test
    void clean_deveRetornarStringVazia_quandoNulo() {
        assertEquals("", CEPUtil.clean(null));
    }

    @Test
    void clean_deveRemoverCaracteresNaoNumericos() {
        assertEquals("06460120", CEPUtil.clean("06.460-120"));
    }

    @Test
    void clean_deveManterStringInalterada_quandoJaSomenteDigitos() {
        assertEquals("06460120", CEPUtil.clean("06460120"));
    }

    @Test
    void clean_deveRemoverTodosCaracteres_quandoApenasLetras() {
        assertEquals("", CEPUtil.clean("ABCDEFGH"));
    }

    // ---------------------------------------------------------------
    // format
    // ---------------------------------------------------------------

    @Test
    void format_deveAplicarMascara_quandoCepValido() {
        assertEquals("06460-120", CEPUtil.format("06460120"));
    }

    @Test
    void format_deveAplicarMascara_quandoCepJaFormatado() {
        assertEquals("06460-120", CEPUtil.format("06460-120"));
    }

    @Test
    void format_deveRetornarOriginal_quandoTamanhoInvalido() {
        assertEquals("1234567", CEPUtil.format("1234567"));
    }

    @Test
    void format_deveRetornarOriginal_quandoNulo() {
        assertNull(CEPUtil.format(null));
    }
}