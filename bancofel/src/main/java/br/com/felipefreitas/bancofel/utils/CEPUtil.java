package br.com.felipefreitas.bancofel.utils;

public class CEPUtil {

    /**
     * Valida se o CEP informado possui um formato válido (com ou sem hífen).
     * Aceita formatos como "06460-120" ou "06460120".
     */
    public static boolean isValid(String cep) {
        if (cep == null) {
            return false;
        }

        // Remove hifens, pontos ou espaços, mantendo apenas os números
        String cleanCep = clean(cep);

        // Um CEP válido deve conter exatamente 8 dígitos numéricos
        // Também bloqueia sequências repetidas óbvias (ex: 00000000)
        return cleanCep.length() == 8 && !cleanCep.matches("(\\d)\\1{7}");
    }

    /**
     * Remove qualquer caractere não numérico do CEP.
     * Útil para padronizar o salvamento no banco de dados.
     */
    public static String clean(String cep) {
        if (cep == null) {
            return "";
        }
        return cep.replaceAll("\\D", "");
    }

    /**
     * Aplica a máscara padrão de CEP (XXXXX-XXX) em uma string numérica.
     */
    public static String format(String cep) {
        String cleanCep = clean(cep);
        if (cleanCep.length() != 8) {
            return cep; // Retorna o original caso não tenha os 8 dígitos
        }
        return cleanCep.substring(0, 5) + "-" + cleanCep.substring(5);
    }
}
