const form = document.getElementById('formCentros');

function prepararClassificacao() {
    const linhas = [...form.querySelectorAll('tr[data-linha]')];
    const busca = document.getElementById('busca');
    const global = document.getElementById('centroGlobal');
    const aplicar = document.getElementById('btnAplicarTodos');
    const salvar = document.getElementById('btnSalvar');

    function atualizarIndicadores() {
        let prontos = 0;
        for (const linha of linhas) {
            const preenchido = Boolean(linha.querySelector('.centro-input').value);
            const situacao = linha.querySelector('[data-status]');
            situacao.classList.toggle('pronto', preenchido);
            situacao.textContent = preenchido ? 'Pronto para salvar' : 'Pendente';
            if (preenchido) prontos++;
        }
        document.getElementById('qtdPreenchidos').textContent = prontos;
        document.getElementById('qtdRestantes').textContent = linhas.length - prontos;
        document.getElementById('resumoSalvar').textContent = prontos
            ? prontos + (prontos === 1 ? ' registro pronto para salvar.' : ' registros prontos para salvar.')
            : 'Selecione um centro de custo para salvar.';
        aplicar.disabled = !global.value || prontos === linhas.length;
        salvar.disabled = prontos === 0;
    }

    function normalizar(texto) {
        return texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR').trim();
    }

    busca.addEventListener('input', () => {
        const termo = normalizar(busca.value);
        for (const linha of linhas) linha.hidden = !normalizar(linha.dataset.busca).includes(termo);
        document.getElementById('semResultados').hidden = linhas.some(linha => !linha.hidden);
    });

    form.addEventListener('change', atualizarIndicadores);
    aplicar.addEventListener('click', () => {
        for (const linha of linhas) {
            const centro = linha.querySelector('.centro-input');
            if (!centro.value) centro.value = global.value;
        }
        atualizarIndicadores();
    });
    form.addEventListener('formdata', ({formData}) => {
        formData.delete('funcionarioIds');
        formData.delete('centrosDeCusto');
        for (const linha of linhas) {
            const centro = linha.querySelector('.centro-input').value;
            if (centro) {
                formData.append('funcionarioIds', linha.querySelector('[name="funcionarioIds"]').value);
                formData.append('centrosDeCusto', centro);
            }
        }
    });
    form.addEventListener('submit', () => {
        salvar.disabled = true;
        salvar.textContent = 'Salvando…';
    });
    window.addEventListener('pageshow', () => {
        salvar.textContent = 'Salvar alterações';
        atualizarIndicadores();
    });
    atualizarIndicadores();
}

if (form) prepararClassificacao();
