package br.com.felipefreitas.bancofel.utils;

public class CPFUtil {

    public static boolean isValid(String cpf) {
        if (cpf == null) return false;

        // Remove pontos, hifens ou espaços mantendo apenas os números
        String cleanCpf = cpf.replaceAll("\\D", "");

        // CPF deve ter exatamente 11 dígitos numéricos
        if (cleanCpf.length() != 11) return false;

        // Bloqueia CPFs com todos os dígitos iguais (ex: 11111111111)
        if (cleanCpf.matches("(\\d)\\1{10}")) return false;

        try {
            // Cálculo do Primeiro Dígito Verificador
            int peso = 10;
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                int num = Character.getNumericValue(cleanCpf.charAt(i));
                soma += (num * peso--);
            }
            int r = 11 - (soma % 11);
            int digito1 = (r == 10 || r == 11) ? 0 : r;

            // Cálculo do Segundo Dígito Verificador
            peso = 11;
            soma = 0;
            for (int i = 0; i < 10; i++) {
                int num = Character.getNumericValue(cleanCpf.charAt(i));
                soma += (num * peso--);
            }
            r = 11 - (soma % 11);
            int digito2 = (r == 10 || r == 11) ? 0 : r;

            // Verifica se os dígitos calculados batem com os informados
            int d1Informado = Character.getNumericValue(cleanCpf.charAt(9));
            int d2Informado = Character.getNumericValue(cleanCpf.charAt(10));

            return d1Informado == digito1 && d2Informado == digito2;

        } catch (Exception e) {
            return false;
        }
    }
}