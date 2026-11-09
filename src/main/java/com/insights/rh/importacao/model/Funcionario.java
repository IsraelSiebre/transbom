package com.insights.rh.importacao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
@Table(name = "funcionario")
@RequiredArgsConstructor
public class Funcionario {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    //funcionario
    @Column(name = "nome")
    private  String nome;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "empresa")
    private String Empresa;

    @Column(name = "cargo")
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(name = "centro_de_custo")
    private CentroDeCusto centroDeCusto;

    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;

    public void setDataAdmissao(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataAdmissao = LocalDate.parse(data, formatter);
    }

    public void setDataAdmissao(LocalDate data) {
        this.dataAdmissao = data;
    }

}
