import {icon} from './visual.js';

const sidebar = document.getElementById('sidebar');
const botao = document.getElementById('menu');
const telaCompacta = matchMedia('(max-width: 900px)');

function definirMenu(aberto) {
    document.body.classList.toggle('nav-open', aberto);
    sidebar.inert = telaCompacta.matches && !aberto;
    botao.setAttribute('aria-expanded', String(aberto));
    botao.setAttribute('aria-label', aberto ? 'Fechar navegação' : 'Abrir navegação');
}

export function fecharMenu() {
    definirMenu(false);
}

for (const elemento of document.querySelectorAll('i[data-icon]')) {
    elemento.setAttribute('aria-hidden', 'true');
    elemento.append(icon(elemento.dataset.icon));
}

const retorno = document.querySelector('a[data-retorno-painel]');
if (retorno && document.referrer) {
    const origem = new URL(document.referrer);
    const painel = new URL(retorno.href);
    if (origem.origin === painel.origin && [painel.pathname, painel.pathname + '/detalhamento'].includes(origem.pathname)) {
        retorno.href = origem.href;
        retorno.textContent = 'Voltar à análise anterior';
    }
}

botao.addEventListener('click', () => {
    const abrir = botao.getAttribute('aria-expanded') === 'false';
    definirMenu(abrir);
    if (abrir) (sidebar.querySelector('[aria-current="page"]') || sidebar.querySelector('a')).focus();
});

document.addEventListener('keydown', evento => {
    if (evento.key === 'Escape' && botao.getAttribute('aria-expanded') === 'true') {
        fecharMenu();
        botao.focus();
    }
});

telaCompacta.addEventListener('change', fecharMenu);
fecharMenu();
