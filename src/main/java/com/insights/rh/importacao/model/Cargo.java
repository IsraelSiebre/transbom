package com.insights.rh.importacao.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Cargo {
    MOTORISTA_CARRETEIRO_3("Motorista Carreteiro", 3),
    ASSISTENTE_ADMINISTRATIVO_2("Assistente Administrativo", 2),
    MOTORISTA_DE_CAMINHAO_1("Motorista de Caminhão", 1),
    GERENTE_DE_TRANSPORTES_3("Gerente de Transportes", 3),
    MOTORISTA_CARRETEIRO_2("Motorista Carreteiro", 2),
    MOTORISTA_CARRETEIRO_1("Motorista Carreteiro", 1),
    OPERADOR_DE_EMPILHADEIRA_2("Operador de Empilhadeira", 2),
    GERENTE_DE_TRANSPORTES_1("Gerente de Transportes", 1),
    AUXILIAR_DE_FATURAMENTO_1("Auxiliar de Faturamento", 1),
    AJUDANTE_DE_MOTORISTA_3("Ajudante de Motorista", 3),
    AUXILIAR_DE_ESCRITORIO_1("Auxiliar de Escritório", 1),
    MOTORISTA_1("Motorista", 1),
    MOTORISTA_2("Motorista", 2),
    CONTROLADOR_DE_ACESSO_1("Controlador de Acesso", 1),
    GERENTE_COMERCIAL_1("Gerente Comercial", 1),
    AJUDANTE_DE_MOTORISTA_2("Ajudante de Motorista", 2),
    AUX_DE_SERVICOS_DE_IMPORTACAO_1("Aux. de Serviços de Importação", 1),
    TECNICO_DE_SEGURANCA_DO_TRABALHO_1("Técnico de Segurança do Trabalho", 1),
    AJUDANTE_DE_MOTORISTA_1("Ajudante de Motorista", 1),
    AUXILIAR_DE_LOGISTICA_2("Auxiliar de Logística", 2),
    MECANICO_1("Mecânico", 1),
    ADMINISTRADOR_1("Administrador", 1),
    ASSISTENTE_DE_DEPTO_PESSOAL_1("Assistente de Depto. Pessoal", 1),
    ANALISTA_DE_FATURAMENTO_1("Analista de Faturamento", 1),
    MOTORISTA_3("Motorista", 3),
    ANALISTA_DE_SERVICO_DE_IMPORTACAO_E_EXPORTACAO_1("Analista de Serviço de Importação e Exportação", 1),
    ASSISTENTE_ADMINISTRATIVO_1("Assistente Administrativo", 1),
    AUXILIAR_ADMINISTRATIVO_2("Auxiliar Administrativo", 2),
    MOTORISTA_DE_CAMINHAO_3("Motorista de Caminhão", 3),
    LAVADOR_DE_VEICULOS_1("Lavador de Veículos", 1),
    OPERADOR_DE_EMPILHADEIRA_1("Operador de Empilhadeira", 1),
    SUPERVISOR_DE_MANUTENCAO_E_REPAROS_3("Supervisor de Manutenção e Reparos de Veículos", 3),
    ASSISTENTE_DE_VENDAS_1("Assistente de Vendas", 1),
    JARDINEIRO_1("Jardineiro", 1),
    EXPEDIDOR_DE_MERCADORIAS_2("Expedidor de Mercadorias", 2),
    COPEIRA_3("Copeira", 3),
    ASSISTENTE_ADMINISTRATIVO_3("Assistente Administrativo", 3),
    AUXILIAR_DE_LOGISTICA_1("Auxiliar de Logística", 1),
    RECEPCIONISTA_1("Recepcionista", 1),
    MENOR_APRENDIZ_3("Menor Aprendiz", 3),
    MENOR_APRENDIZ_1("Menor Aprendiz", 1),
    ENCARREGADO_DE_TRANSPORTE_1("Encarregado de Transporte", 1),
    BORRACHEIRO_2("Borracheiro", 2),
    SUPERVISOR_DE_COBRANCA_1("Supervisor de Cobrança", 1),
    FAXINEIRA_1("Faxineira", 1),
    SUPERVISOR_DE_ALMOXARIFADO_2("Supervisor de Almoxarifado", 2),
    SOCIO_ADMINISTRADOR_1("Sócio Administrador", 1);

    private final String descricao;
    private final int codigoCentroCusto;

}

