package br.com.felipefreitas.bancofel.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bancofel: a API que acelera o seu ecossistema financeiro.",
                version = "1.0.0",
                description = "API RESTful para gerenciamento de um ecossistema de Banco, " +
                              "permitindo o controle dos clientes,contas e transações",
                contact = @Contact(
                        name = "Felipe Freitas",
                        email = "felipefreitas210891@gmail.com.br",
                        url = "https://www.linkedin.com/in/felipe-freitas-aa8651316/"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor de Desenvolvimento"),
        }
)
public class OpenApiConfig {
}