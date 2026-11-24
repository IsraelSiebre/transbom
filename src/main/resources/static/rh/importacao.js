import {money, month, number} from './visual.js';

const form = document.querySelector('form[data-envio]');
const conciliacao = form.dataset.envio === 'conciliacao';
const entradas = [...form.querySelectorAll('input[type="file"]')];
const botao = form.querySelector('button[type="submit"]');
const rotulo = botao.querySelector('[data-rotulo]');
const rotuloInicial = rotulo.textContent;
const feedback = form.querySelector('[data-feedback]');
const painelResultado = document.querySelector('.painel-resultado');
let registros = [];

function atualizarArquivo(input) {
    const seletor = input.closest('.seletor-arquivo');
    const arquivo = input.files[0];
    seletor.toggleAttribute('data-selecionado', Boolean(arquivo));
    seletor.querySelector('[data-nome-arquivo]').textContent = arquivo?.name || 'Selecione ou arraste a planilha';
    seletor.querySelector('[data-escolher]').textContent = arquivo ? 'Trocar' : 'Escolher';
}

function informar(estado, mensagem) {
    feedback.dataset.state = estado;
    feedback.textContent = mensagem;
}

function exibirRegistros(lista) {
    registros = lista;
    const corpo = document.getElementById('tabelaResultado');
    const previa = registros.slice(0, 20);
    corpo.replaceChildren();
    for (const item of previa) {
        const linha = corpo.insertRow();
        for (const valor of [item.nome, month(item.competencia), money(item.salario), money(item.custoMoTotal)]) {
            linha.insertCell().textContent = valor ?? '—';
        }
    }
    document.getElementById('totalRegistros').textContent = registros.length > previa.length
        ? 'Exibindo ' + number(previa.length) + ' de ' + number(registros.length) + ' registros. O JSON inclui todos.'
        : number(registros.length) + (registros.length === 1 ? ' registro conciliado.' : ' registros conciliados.');
    painelResultado.hidden = false;
}

async function validarResposta(resposta) {
    if (resposta.redirected && new URL(resposta.url).pathname.endsWith('/login')) {
        throw new Error('Sua sessão terminou. Entre novamente para enviar a planilha.');
    }
    if (resposta.ok) return;
    if (resposta.status === 413) throw new Error('O arquivo excede o tamanho permitido para envio.');
    const erro = await resposta.json().catch(() => null);
    throw new Error(erro?.detail || erro?.message || 'Não foi possível processar a planilha (HTTP ' + resposta.status + ').');
}

for (const input of entradas) {
    atualizarArquivo(input);
    input.addEventListener('change', () => {
        atualizarArquivo(input);
        informar('', '');
    });
}

form.addEventListener('submit', async evento => {
    evento.preventDefault();
    if (botao.disabled) return;
    const dados = new FormData(form);
    const arquivo = entradas[0].files[0].name;
    botao.disabled = true;
    entradas.forEach(input => { input.disabled = true; });
    form.setAttribute('aria-busy', 'true');
    rotulo.textContent = conciliacao ? 'Conciliando…' : 'Importando…';
    informar('loading', conciliacao ? 'Processando os dois arquivos…' : 'Processando a planilha…');
    if (painelResultado) painelResultado.hidden = true;
    try {
        const resposta = await fetch(form.action, {
            method: 'POST',
            headers: {Accept: 'application/json', [form.dataset.csrfHeader]: form.dataset.csrfToken},
            body: dados
        });
        await validarResposta(resposta);
        if (conciliacao) {
            const lista = await resposta.json();
            if (!Array.isArray(lista)) throw new Error('A resposta da conciliação não contém a lista de registros.');
            exibirRegistros(lista);
            informar('success', 'Conciliação concluída. Os registros estão disponíveis abaixo.');
        } else {
            // A importação da folha consolidada retorna HTTP 204, sem corpo.
            informar('success', 'Importação concluída para ' + arquivo + '.');
        }
        form.reset();
        entradas.forEach(atualizarArquivo);
    } catch (erro) {
        informar('error', erro instanceof TypeError ? 'Falha de conexão durante o envio. Confira o resultado no painel antes de reenviar.' : erro.message);
    } finally {
        form.setAttribute('aria-busy', 'false');
        entradas.forEach(input => { input.disabled = false; });
        botao.disabled = false;
        rotulo.textContent = rotuloInicial;
    }
});

if (painelResultado) {
    document.getElementById('btnDownload').addEventListener('click', () => {
        const blob = new Blob([JSON.stringify(registros, null, 2)], {type: 'application/json'});
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'maodeobra_final.json';
        document.body.append(link);
        link.click();
        link.remove();
        setTimeout(() => URL.revokeObjectURL(url), 1000);
    });
}
