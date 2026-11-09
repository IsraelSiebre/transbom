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

    }
