package br.com.felipefreitas.bancofel.utils;

import java.util.regex.Pattern;

public class CNPJUtil {

    // Aceita formato com máscara: AA.AAA.AAA/AAAA-AA ou sem máscara: AAAAAAAAAAAAAA
    private static final Pattern CNPJ_PATTERN = Pattern.compile("^([A-Z0-9]{2}\\.[A-Z0-9]{3}\\.[A-Z0-9]{3}/[A-Z0-9]{4}-[A-Z0-9]{2}|[A-Z0-9]{14})$");

    private CNPJUtil() {
        // Construtor privado para evitar instanciação de classe utilitária
    }

    /**
     * Valida um CNPJ (aceita numérico tradicional e alfanumérico).
     */
    public static boolean isValid(String cnpj) {
        if (cnpj == null) {
            return false;
        }

        // 1. Valida o formato inicial (com ou sem máscara)
        String cnpjUpper = cnpj.toUpperCase().trim();
        if (!CNPJ_PATTERN.matcher(cnpjUpper).matches()) {
            return false;
        }

        // 2. Limpa a máscara para o cálculo
        String cnpjLimpo = clean(cnpjUpper);

        // 3. Evita sequências idênticas conhecidas se for puramente numérico
        if (cnpjLimpo.matches("(\\d)\\1{13}")) {
            return false;
        }

        // 4. Valida os dois dígitos verificadores usando a lógica alfanumérica (Módulo 11)
        return calcularDigito(cnpjLimpo, 12) == Character.getNumericValue(cnpjLimpo.charAt(12)) &&
               calcularDigito(cnpjLimpo, 13) == Character.getNumericValue(cnpjLimpo.charAt(13));
    }

    /**
     * Remove pontos, barra e hífen do CNPJ, retornando apenas os 14 caracteres alfanuméricos.
     */
    public static String clean(String cnpj) {
        if (cnpj == null) {
            return "";
        }
        return cnpj.toUpperCase().replaceAll("[.\\-/]", "");
    }

    /**
     * Aplica o algoritmo do Módulo 11 adaptado para a tabela alfanumérica da Receita Federal.
     */
    private static int calcularDigito(String cnpj, int posicaoDigito) {
        int soma = 0;
        int peso = 2;

        // Caminha da direita para a esquerda a partir da posição desejada
        for (int i = posicaoDigito - 1; i >= 0; i--) {
            char caractere = cnpj.charAt(i);
            int valorNumerico;

            if (Character.isDigit(caractere)) {
                // Se for número, pega o valor dele (0 a 9)
                valorNumerico = Character.getNumericValue(caractere);
            } else {
                // Se for letra (A-Z), subtrai 48 do valor ASCII (Regra oficial da Receita Federal)
                // Exemplo: 'A' é 65 na tabela ASCII -> 65 - 48 = 17
                valorNumerico = (int) caractere - 48;
            }

            soma += valorNumerico * peso;

            // Os pesos variam de 2 a 9, reiniciando ao passar de 9
            peso = (peso == 9) ? 2 : peso + 1;
        }

        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}
