package br.com.felipefreitas.bancofel.interfaces;

public interface ClienteImpl<E, S> {

    S cadastrarCliente(E cliente);

    // ✨ Mudou para String: Você pesquisa usando o texto do CPF ou CNPJ
    S pesquisaClientePorDocumento(String documento);

    // ✨ Recebe o documento para achar o cliente e o objeto com os novos dados
    S atualizarDadosCliente(String documento, E cliente);

    // ✨ Recebe o documento (String) para desativar
    S softDeleteCliente(String documento);

    // ✨ Recebe o documento (String) para reativar
    S reativarCliente(String documento);

}
