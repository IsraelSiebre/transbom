package com.insights.rh.analytics;

import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

record PeriodoFolha(List<LocalDate> meses, List<LocalDate> base, List<LocalDate> importados) {
    static PeriodoFolha resolver(MultiValueMap<String,String> p, List<LocalDate> disponiveis) {
        List<Integer> anos = valores(p, "ano").stream().map(s -> numero(s, 1900, 9999)).toList();
        List<Integer> mesesAno = valores(p, "mes").stream().map(s -> numero(s, 1, 12)).toList();
        LocalDate ultima = disponiveis.isEmpty() ? LocalDate.now().withDayOfMonth(1) : disponiveis.getLast();
        LocalDate primeira = disponiveis.isEmpty() ? ultima : disponiveis.getFirst();
        String modo = p.getFirst("periodo");
        boolean selecaoTemporal = !anos.isEmpty() || !mesesAno.isEmpty() || p.getFirst("inicio")!=null || p.getFirst("fim")!=null;
        if (modo == null || modo.isBlank()) modo = selecaoTemporal ? "historico" : "ultimo";
        LocalDate inicio = switch (modo) {
            case "historico", "personalizado" -> primeira;
            case "ultimo" -> ultima;
            case "3", "6", "12" -> disponiveis.isEmpty() ? ultima : disponiveis.get(Math.max(0, disponiveis.size() - Integer.parseInt(modo)));
            case "ytd" -> LocalDate.now().withDayOfYear(1);
            default -> throw invalido("Período desconhecido.");
        };
        LocalDate fim = "ytd".equals(modo) && ultima.getYear() > inicio.getYear() ? inicio.withMonth(12) : ultima;
        if (!anos.isEmpty() && ("historico".equals(modo) || "personalizado".equals(modo))) {
            inicio = LocalDate.of(Collections.min(anos), 1, 1);
            fim = LocalDate.of(Collections.max(anos), 12, 1);
        }
        if (p.getFirst("inicio") != null) inicio = data(p.getFirst("inicio"));
        if (p.getFirst("fim") != null) fim = data(p.getFirst("fim"));
        if (p.getFirst("competencia") != null) inicio = fim = data(p.getFirst("competencia"));

        if (inicio.isAfter(fim) && !"ytd".equals(modo)) throw invalido("O início deve preceder o fim.");
        if (ChronoUnit.MONTHS.between(inicio, fim) > 1200) throw invalido("Intervalo máximo de 100 anos.");
        var meses = new ArrayList<LocalDate>();
        for (LocalDate d = inicio; !d.isAfter(fim); d = d.plusMonths(1)) {
            if ((anos.isEmpty() || anos.contains(d.getYear())) && (mesesAno.isEmpty() || mesesAno.contains(d.getMonthValue()))) meses.add(d);
        }
        long distancia = meses.isEmpty() ? 1 : ChronoUnit.MONTHS.between(meses.getFirst(), meses.getLast()) + 1;
        String comparacao = Objects.requireNonNullElse(p.getFirst("comparar"), "anterior");
        Function<LocalDate, LocalDate> deslocar = switch (comparacao) {
            case "anterior" -> d -> d.minusMonths(distancia);
            case "anual" -> d -> d.minusYears(1);
            case "personalizado" -> {
                LocalDate baseInicio = data(p.getFirst("baseInicio"));
                long deslocamento = ChronoUnit.MONTHS.between(inicio, baseInicio);
                yield d -> d.plusMonths(deslocamento);
            }
            default -> throw invalido("Comparação desconhecida.");
        };
        return new PeriodoFolha(List.copyOf(meses), meses.stream().map(deslocar).toList(), disponiveis);
    }

    boolean comparavel() {
        return !meses.isEmpty() && importados.containsAll(meses) && importados.containsAll(base);
    }

    static List<String> valores(MultiValueMap<String,String> p, String chave) {
        List<String> valores = p.getOrDefault(chave, List.of());
        if (valores.size() > 1500 || valores.stream().anyMatch(v -> v == null || v.length() > 255)) throw invalido("Filtro extenso demais.");
        return valores.stream().filter(v -> !v.isBlank()).distinct().toList();
    }

    static LocalDate data(String valor) {
        try {
            LocalDate mes = valor.length() == 7 ? YearMonth.parse(valor).atDay(1) : LocalDate.parse(valor).withDayOfMonth(1);
            if (mes.getYear() < 1900 || mes.getYear() > 9999) throw invalido("Ano inválido.");
            return mes;
        } catch (RuntimeException e) {
            throw invalido("Competência inválida.");
        }
    }

    static int numero(String s, int min, int max) {
        try {
            int valor = Integer.parseInt(s);
            if (valor >= min && valor <= max) return valor;
        } catch (NumberFormatException ignored) { }
        throw invalido("Número fora do intervalo permitido.");
    }

    static ResponseStatusException invalido(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}

