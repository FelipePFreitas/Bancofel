package br.com.felipefreitas.bancofel.interfaces;

import br.com.felipefreitas.bancofel.entity.Cliente;

public interface ClienteService<T extends Cliente> {

    T cadastrarCliente(T cliente);

    // ✨ Mudou para String: Você pesquisa usando o texto do CPF ou CNPJ
    T pesquisaClientePorDocumento(String documento);

    // ✨ Recebe o documento para achar o cliente e o objeto com os novos dados
    T atualizarDadosCliente(String documento, T cliente);

    // ✨ Recebe o documento (String) para desativar
    T softDeleteCliente(String documento);

    // ✨ Recebe o documento (String) para reativar
    T reativarCliente(String documento);

}
