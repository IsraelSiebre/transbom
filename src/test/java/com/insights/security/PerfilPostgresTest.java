package com.insights.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "FOLHA_TEST_URL", matches = "jdbc:postgresql:.*")
class PerfilPostgresTest {
    private SingleConnectionDataSource conexao;
    private JdbcTemplate jdbc;

    @BeforeEach void abrir() {
        conexao = new SingleConnectionDataSource(System.getenv("FOLHA_TEST_URL"),
                System.getenv("FOLHA_TEST_USER"), System.getenv("FOLHA_TEST_PASSWORD"), true);
        jdbc = new JdbcTemplate(conexao);
        jdbc.execute("set search_path to pg_temp, pg_catalog");
    }

    @AfterEach void fechar() {
        conexao.destroy();
    }

    private void migrar() {
        new ResourceDatabasePopulator(new ClassPathResource("db/perfil-usuarios.sql")).execute(conexao);
    }

    @Test void migraBaseAnteriorEMantemCompatibilidadeComInsercaoSemPerfil() {
        migrar();
        jdbc.execute("create temporary table usuarios (id int primary key, senha_hash text)");
        jdbc.update("insert into usuarios values (1, 'hash-existente')");
        migrar();
        migrar();
        jdbc.update("insert into usuarios (id) values (2)");
        assertThat(jdbc.queryForList("select role from usuarios order by id", String.class))
                .containsExactly("VISUALIZADOR", "VISUALIZADOR");
        assertThat(jdbc.queryForObject("select senha_hash from usuarios where id=1", String.class))
                .isEqualTo("hash-existente");
        assertThat(jdbc.queryForObject("select attnotnull from pg_attribute where attrelid='usuarios'::regclass and attname='role'", Boolean.class)).isTrue();
    }

    @Test void preservaPerfisExplicitosEPreencheApenasNulos() {
        jdbc.execute("create temporary table usuarios (id int primary key, role varchar)");
        jdbc.update("insert into usuarios values (1, 'ADMIN'), (2, null), (3, 'VISUALIZADOR')");
        migrar();
        assertThat(jdbc.queryForList("select role from usuarios order by id", String.class))
                .containsExactly("ADMIN", "VISUALIZADOR", "VISUALIZADOR");
    }
}
