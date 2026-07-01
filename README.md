# BancoFel API 🏦

O dinheiro evoluiu. A forma de integrar também. Bancofel: a API que acelera o seu ecossistema financeiro.

O **BancoFel** é uma API RESTful de nível profissional que simula o ecossistema de um banco digital moderno. O sistema gerencia o cadastro de clientes, a abertura e controle de contas bancárias, e o processamento de movimentações financeiras com foco em segurança de dados, validações severas e integridade transacional.

---

## 🚀 Funcionalidades Principais (Escopo do Sistema)

*   **Gestão de Clientes (CRUD)**: Cadastro completo de usuários contendo dados pessoais (como CPF único e data de nascimento) e informações de endereço estruturadas.
*   **Controle de Contas**: Vinculo dinâmico onde um cliente pode possuir múltiplas contas (Relacionamento Um-para-Muitos), com controle rigoroso de saldos decimais precisos e identificadores textuais únicos por conta.
*   **Histórico de Transações**: Registro imutável de movimentações financeiras (Depósitos, Saques, Transferências e Pix) atrelado a carimbos de data/hora para auditoria e geração de extratos.
*   **Segurança de Credenciais**: Arquitetura que isola strings de conexão, usuários e senhas do banco de dados por meio de injeção via variáveis de ambiente, impossibilitando o vazamento de dados sensíveis no histórico do Git.

---

## 🛠️ Tecnologias e Dependências Utilizadas

O ecossistema do projeto foi construído utilizando a stack mais moderna e demandada pelo mercado Java corporativo:

### Core & Framework
*   **Java (Versão 17 ou superior)**: Linguagem robusta e fortemente tipada base para o desenvolvimento do ecossistema.
*   **Spring Boot 3.x / 4.x**: Framework base que acelera a configuração da aplicação através de convenções e autoconfiguração.
*   **Spring Web**: Módulo responsável por expor as rotas HTTP da API RESTful e gerenciar o ciclo de requisições e respostas.

### Persistência & Banco de Dados
*   **Spring Data JPA**: Abstração de persistência baseada no Hibernate que elimina a escrita manual de queries SQL repetitivas através do uso de interfaces `JpaRepository`.
*   **MySQL Driver**: Conector oficial para comunicação nativa, segura e de alta performance com o banco de dados relacional MySQL.
*   **Hibernate Dialect (MySQL)**: Motor de mapeamento objeto-relacional (ORM) configurado para traduzir entidades Java em tabelas otimizadas e gerenciar restrições físicas (`NOT NULL`, `UNIQUE`, `FOREIGN KEY`).

### Produtividade & Validação
*   **Lombok**: Biblioteca de produtividade que elimina códigos repetitivos (Boilerplate Code) gerando getters, setters, construtores e métodos auxiliares em tempo de compilação através de anotações como `@Data`.
*   **Spring Boot Starter Validation (Bean Validation - Jakarta)**: Motor de validação de dados que intercepta requisições inválidas antes de atingirem o banco de dados, aplicando regras estritas como `@NotBlank`, `@NotNull`, `@Size` e `@Positive`.
*   **Spring Boot DevTools**: Ferramenta de auxílio ao desenvolvimento que habilita o LiveReload e reinicia o servidor local automaticamente a cada alteração de código salva.

### Documentação
*   **Springdoc OpenAPI / Swagger**: Geração automatizada de documentação interativa e padronizada das rotas da API, permitindo testes de requisições direto pelo navegador.
