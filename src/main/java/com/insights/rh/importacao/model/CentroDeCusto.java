package com.insights.rh.importacao.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CentroDeCusto {
    TERM("Terminal de Cargas"),
    TRANSF("Transferencia"),
    COL_ENT("Coleta e entrega"),
    TRAF("Tráfego"),
    OFI("Oficina"),
    COM("Comercial"),
    ADM("Administrativo"),
    DIRETOR("Diretoria");

    private final String descricao;

    public static CentroDeCusto fromString(String cc) {
        return switch (cc) {
            case "TERM" -> TERM;
            case "TRANSF" -> TRANSF;
            case "COL / ENT" -> COL_ENT;
            case "TRAF" -> TRAF;
            case "OFI" -> OFI;
            case "COM" -> COM;
            case "ADM" -> ADM;
            case "DIRETOR" -> DIRETOR;
            default -> throw new IllegalStateException("Centro de custo inválido: " + cc);
        };
    }

    }
