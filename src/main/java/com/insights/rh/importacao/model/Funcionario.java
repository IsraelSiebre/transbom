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
@Table(name = "funcionarios_mo")
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

    @Column(name = "codigo_externo")
    private Integer codigoExterno;

    @Column(name = "empresa")
    private String empresa;

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

    public void setCentroDeCusto(CentroDeCusto centroDeCusto) {
        this.centroDeCusto = centroDeCusto;
    }

    public void setCentroDeCusto(String cc) {
        switch (cc) {
            case "ADM" -> this.centroDeCusto = CentroDeCusto.ADM;
            case "COL / ENT", "COL/ENT", "COL_ENT" -> this.centroDeCusto = CentroDeCusto.COL_ENT;
            case "COM" -> this.centroDeCusto = CentroDeCusto.COM;
            case "DIRETOR" -> this.centroDeCusto = CentroDeCusto.DIRETOR;
            case "OFI" -> this.centroDeCusto = CentroDeCusto.OFI;
            case "TERM" -> this.centroDeCusto = CentroDeCusto.TERM;
            case "TRAF" -> this.centroDeCusto = CentroDeCusto.TRAF;
            case "TRANSF" -> this.centroDeCusto = CentroDeCusto.TRANSF;

        }
    }

}
