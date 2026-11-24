// Teste opt-in em um servidor já iniciado, com ao menos uma competência importada.
// FOLHA_UI_URL=http://127.0.0.1:8080 node --env-file=.env --test src/test/browser/dashboard.mjs
// FOLHA_UI_BROWSER pode indicar o executável de Chrome/Chromium/Edge.
// Para exercitar o perfil comum, use FOLHA_UI_ROLE=VISUALIZADOR e as credenciais dessa conta.
import test from 'node:test';
import assert from 'node:assert/strict';
import {spawn} from 'node:child_process';
import {access, mkdtemp, readFile, rm} from 'node:fs/promises';
import {tmpdir} from 'node:os';
import {join} from 'node:path';
import {setTimeout as delay} from 'node:timers/promises';

async function until(check, label, timeout = 120000) {
    const end = Date.now() + timeout;
    while (Date.now() < end) {
        const result = await check();
        if (result) return result;
        await delay(100);
    }
    throw Error('Tempo esgotado em ' + label);
}

async function executable() {
    for (const path of [process.env.FOLHA_UI_BROWSER,
        'C:/Program Files/Google/Chrome/Application/chrome.exe',
        'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe',
        '/usr/bin/google-chrome','/usr/bin/chromium']) {
        if (path && await access(path).then(() => true, () => false)) return path;
    }
    throw Error('Configure FOLHA_UI_BROWSER com o caminho do navegador.');
}

// Comandos CDP nativos evitam instalar um WebDriver da mesma versão do navegador.
async function connect(port, failures) {
    const tabs = await (await fetch('http://127.0.0.1:' + port + '/json/list')).json();
    const socket = new WebSocket(tabs.find(t => t.type === 'page').webSocketDebuggerUrl);
    await new Promise((resolve,reject) => {
        socket.onopen = resolve;
        socket.onerror = reject;
    });
    const pending = new Map();
    let id = 0;
    socket.onmessage = event => {
        const msg = JSON.parse(event.data);
        if (msg.method === 'Runtime.exceptionThrown') failures.push('Exceção JavaScript na página');
        if (msg.method === 'Network.responseReceived' && msg.params.response.status >= 400) {
            const path = new URL(msg.params.response.url).pathname;
            if (path !== '/favicon.ico') failures.push(path.replace(/\/\d+$/,'/{id}') + ' HTTP ' + msg.params.response.status);
        }
        if (pending.has(msg.id)) {
            const {resolve,reject} = pending.get(msg.id);
            pending.delete(msg.id);
            if (msg.error) reject(Error(msg.error.message));
            else resolve(msg.result);
        }
    };
    function send(method, params = {}) {
        return new Promise((resolve,reject) => {
            pending.set(++id,{resolve,reject});
            socket.send(JSON.stringify({id,method,params}));
        });
    }
    await Promise.all([send('Page.enable'),send('Runtime.enable'),send('Network.enable')]);
    async function evaluate(expression) {
        const answer = await send('Runtime.evaluate',{expression,returnByValue:true,awaitPromise:true});
        if (answer.exceptionDetails) throw Error('Falha ao interagir com elemento na página.');
        return answer.result.value;
    }
    return {send,evaluate,close:() => socket.close()};
}

const base = process.env.FOLHA_UI_URL;
test('dashboard autenticado e investigação de folha', {skip: !base,timeout: 900000}, async t => {
    const user = process.env.FOLHA_UI_USERNAME || process.env.APP_BOOTSTRAP_USERNAME;
    const password = process.env.FOLHA_UI_PASSWORD || process.env.APP_BOOTSTRAP_PASSWORD;
    const perfil = process.env.FOLHA_UI_ROLE || 'ADMIN';
    assert.ok(user && password, 'Informe as credenciais da instância de teste.');
    assert.ok(['ADMIN', 'VISUALIZADOR'].includes(perfil));
    const binary = await executable();
    const temp = await mkdtemp(join(tmpdir(),'transbom-ui-'));
    const proc = spawn(binary, ['--headless=new','--disable-background-networking','--no-first-run',
        '--no-default-browser-check','--disable-dev-shm-usage','--remote-debugging-port=0','--user-data-dir=' + temp,'about:blank'],
        {windowsHide:true,stdio:'ignore'});
    let browser;
    t.after(async () => {
        if (browser) {
            await browser.send('Browser.close').catch(() => {});
            browser.close();
        }
        if (proc.exitCode === null && proc.signalCode === null) proc.kill();
        await rm(temp, {recursive:true,force:true,maxRetries:10,retryDelay:250});
    });
    const port = await until(() => readFile(join(temp,'DevToolsActivePort'),'utf8')
        .then(s => s.split(/\r?\n/)[0],() => null), 'início do Chromium',30000);
    const failures = [];
    browser = await connect(port, failures);
    const {send,evaluate} = browser;
    const dashboard = new URL('/mao-de-obra',base).href;
    const nav = url => send('Page.navigate',{url});
    const ready = (extra='true') => until(() => evaluate(
        "document.querySelector('#analysis')?.getAttribute('aria-busy') === 'false' && ("+extra+")"),'dados da análise');
    const click = selector => evaluate("document.querySelector("+JSON.stringify(selector)+").click()");
    const view = async id => {
        await click('#sections a[data-view="' + id + '"]');
        await ready("document.querySelector('#sections [data-view="+id+"]')?.getAttribute('aria-current')==='page'");
    };
    const viewport = (width,height) => send('Emulation.setDeviceMetricsOverride',{width,height,mobile:false,deviceScaleFactor:1});
    await viewport(1440,900);
    await nav(new URL('/',base).href);
    await until(() => evaluate("!!document.querySelector('#password')"),'login');
    await until(() => evaluate("document.querySelector('.login-marca img')?.naturalWidth > 0"), 'logo no login');
    await evaluate("document.querySelector('#username').value="+JSON.stringify(user)+";document.querySelector('#password').value="+JSON.stringify(password)+";document.querySelector('form').requestSubmit()");
    await ready("document.querySelectorAll('#analysis canvas').length > 0");
    assert.equal(await evaluate("location.href"), dashboard);

    await t.test('KPIs, navegação, filtros e drill-down ao perfil', async () => {
        assert.ok(await evaluate("document.querySelectorAll('#analysis .kpi').length>=12"));
        await click('#filter-open');
        assert.ok(await evaluate("document.querySelector('#filters-dialog').open"));
        await click('#filters-dialog [data-close]');
        const panelBar = title => "[...document.querySelectorAll('#analysis .panel')].find(p=>p.querySelector('h2')?.textContent==="+JSON.stringify(title)+").querySelector('button.bar-row').click()";
        await evaluate(panelBar('Custo por centro'));
        await ready("new URLSearchParams(location.search).has('centro') && document.querySelector('#analysis').textContent.includes('Mapa de calor mensal')");
        await view('pessoas');
        assert.ok(await evaluate("new URLSearchParams(location.search).has('centro')"));
        await evaluate(panelBar('Ranking de salários'));
        await ready("new URLSearchParams(location.search).has('funcionario') && document.querySelector('#analysis').textContent.includes('Média do cargo')");
        for (const id of ['evolucao','composicao','qualidade','centros']) {
            await view(id);
            assert.ok(await evaluate("document.querySelectorAll('#analysis .panel').length>0"));
        }
        assert.deepEqual(failures,[]);
    });

    await t.test('composição, filtros e colunas expõem a comissão calculada sem suas origens', async () => {
        await nav(dashboard+'?visao=composicao');
        await ready("document.querySelector('#analysis').textContent.includes('Catálogo e valores das rubricas')");
        const ids = await evaluate("[...document.querySelectorAll('#analysis table td')].map(td=>td.textContent).filter(id=>['comissoesSemProvisao','comissoesPorFora','valorBestPontos'].includes(id))");
        assert.deepEqual(ids,['comissoesSemProvisao']);
        await click('#filter-open');
        const origens = await evaluate("[...document.querySelector('#filters-form').rubrica.options].map(o=>o.value).filter(v=>['comissoesPorFora','valorBestPontos'].includes(v))");
        assert.deepEqual(origens,[]);
        await click('#filters-dialog [data-close]');
        await view('detalhes');
        const colunas = await evaluate("[...document.querySelectorAll('.column-list input')].map(i=>i.value)");
        assert.ok(colunas.includes('comissoesSemProvisao'));
        assert.ok(!colunas.includes('comissoesPorFora') && !colunas.includes('valorBestPontos'));
        assert.deepEqual(failures,[]);
    });

    await t.test('seleção de competências e rubrica atualiza KPI e série do gráfico', async () => {
        await nav(dashboard);
        await ready("!!document.querySelector('#analysis canvas')");
        await click('#filter-open');
        const rubrica = await evaluate(`(async () => {
            const f=document.querySelector('#filters-form');
            f.periodo.value='personalizado';
            f.periodo.dispatchEvent(new Event('change',{bubbles:true}));
            f.competencia.value=[...f.competencia.options].at(-1).value;
            const dados=await (await fetch('/mao-de-obra/api/filtros?competencia='+f.competencia.value)).json();
            const campo=dados.rubricas.find(r=>r.aditiva && dados.rubricasDisponiveis.includes(r.campo));
            f.grupo.value=campo.grupo;
            f.grupo.dispatchEvent(new Event('change',{bubbles:true}));
            f.rubrica.value=campo.campo;
            f.rubrica.dispatchEvent(new Event('change',{bubbles:true}));
            f.requestSubmit();
            return campo.campo;
        })()`);
        await ready("new URLSearchParams(location.search).has('competencia') && new URLSearchParams(location.search).get('rubrica')==="+JSON.stringify(rubrica)+" && document.querySelector('#analysis').textContent.includes('Valor da seleção')");
        assert.ok(await evaluate(`(async()=>{
            const data=await (await fetch('/mao-de-obra/api/resumo'+location.search)).json();
            const valor=new Intl.NumberFormat('pt-BR',{style:'currency',currency:'BRL'}).format(data.atual.valor);
            return document.querySelector('#analysis .kpi-value').textContent===valor;
        })()`));
        await view('evolucao');
        assert.ok(await evaluate(`(async()=>{
            const data=await (await fetch('/mao-de-obra/api/resumo'+location.search)).json();
            return [...document.querySelectorAll('#analysis canvas')].some(c=>{
                const ch=Chart.getChart(c);
                return ch && ch.data.datasets.some(ds=>JSON.stringify(ds.data)===JSON.stringify(data.mensal.map(r=>r.valor)));
            });
        })()`));
        await evaluate("[...document.querySelectorAll('#chips button')].find(b=>b.textContent==='Limpar filtros').click()");
        await ready("!new URLSearchParams(location.search).has('rubrica')");
        assert.deepEqual(failures,[]);
    });

    await t.test('ordenação, paginação, busca vazia, expansão e exportação', async () => {
        await nav(dashboard+'/detalhamento?tamanho=10');
        await ready("!!document.querySelector('#analysis tbody tr button')");
        await click('#analysis tbody tr button[aria-expanded]');
        assert.equal(await evaluate("document.querySelector('#analysis tbody tr button[aria-expanded]').getAttribute('aria-expanded')"),'true');
        await evaluate("[...document.querySelectorAll('#analysis th button')].find(b=>b.textContent.startsWith('Salário')).click()");
        await ready("new URLSearchParams(location.search).get('ordenar')==='salario'");
        const next = await evaluate("[...document.querySelectorAll('.pager button')].some(b=>b.textContent.includes('Próxima') && !b.disabled)");
        if(next) {
            await evaluate("[...document.querySelectorAll('.pager button')].find(b=>b.textContent.includes('Próxima')).click()");
            await ready("document.querySelector('.pager')?.textContent.includes('Página 2')");
        }
        await click('#export-open');
        assert.ok(await evaluate("document.querySelector('#export-dialog').open"));
        await send('Browser.setDownloadBehavior',{behavior:'allow',downloadPath:temp});
        await evaluate("const exportForm=document.querySelector('#export-form');exportForm.formato.value='csv';exportForm.requestSubmit()");
        const baixa=await until(()=>readFile(join(temp,'mao-de-obra-funcionarios.csv')).catch(()=>null),'download do diálogo');
        assert.ok(baixa.toString('utf8',0,200).includes('Competência'));
        for (const formato of ['csv','xlsx']) {
            const signature = await evaluate("(async()=>{const res=await fetch('/mao-de-obra/api/exportar?tipo=funcionarios&colunas=salario&formato="+formato+"');const bytes=new Uint8Array(await res.arrayBuffer());return [res.status,bytes[0],bytes[1]]})()");
            assert.deepEqual(signature,formato==='csv'?[200,239,187]:[200,80,75]);
        }
        await evaluate("const input=document.querySelector('.table-tools input[type=search]');input.value='sem-correspondencia-'+crypto.randomUUID();input.form.requestSubmit()");
        await ready("!!document.querySelector('#analysis .empty')");
        assert.deepEqual(failures,[]);
    });

    await t.test('navegação entre a análise filtrada, o guia e as telas de gestão', async () => {
        const origem = dashboard + '?periodo=3&visao=evolucao';
        await nav(origem);
        await ready("!!document.querySelector('#sections [data-view=evolucao][aria-current=page]')");
        await click('#sections a[href="/mao-de-obra/guia"]');
        await until(() => evaluate("document.readyState === 'complete' && !!document.querySelector('#sections a[aria-current=page][href=\"/mao-de-obra/guia\"]')"), 'guia de indicadores');
        assert.deepEqual(await evaluate("[...document.querySelectorAll('.indice-guia a')].filter(a => !document.getElementById(a.hash.slice(1))).map(a => a.hash)"), []);
        assert.ok(await evaluate(`(async () => {
            const rubricas = await (await fetch('/mao-de-obra/api/rubricas/catalogo')).json();
            const nomes = [...document.querySelectorAll('#rubricas tbody tr')].map(tr => tr.cells[0].textContent);
            return nomes.length === rubricas.length && rubricas.every(r => nomes.includes(r.nome));
        })()`));
        await viewport(390,844);
        await click('.indice-guia a[href="#rubricas"]');
        await click('#rubricas details:has(table) summary');
        assert.ok(await evaluate("location.hash === '#rubricas' && !!document.querySelector('#rubricas details[open] table')"));
        assert.ok(await evaluate('document.documentElement.scrollWidth <= innerWidth + 1'), 'Largura do guia no celular');
        await viewport(1440,900);
        await click('[data-retorno-painel]');
        await ready("!!document.querySelector('#analysis canvas')");
        assert.equal(await evaluate("location.href"), origem);
        const destino = new URL(await evaluate("document.querySelector('[data-classificacao]').href"));
        const mes = destino.searchParams.get('competencia').slice(0, 7);
        await click('[data-classificacao]');
        await until(() => evaluate("!!document.querySelector('#mes-consulta')"), 'classificação da competência');
        assert.equal(await evaluate("document.querySelector('#mes-consulta').value"), mes);
        assert.equal(await evaluate("document.querySelector('.nav-extra [aria-current=page]').textContent.trim()"), 'Classificar centros');
        await until(() => evaluate("document.querySelector('[data-retorno-painel]').href === " + JSON.stringify(origem)), 'retorno com filtros');
        await click('[data-retorno-painel]');
        await ready("!!document.querySelector('#analysis canvas')");
        assert.equal(await evaluate("location.href"), origem);
        for (const path of ['/mao-de-obra/folha', '/mao-de-obra/upload', '/usuarios/novo', '/minha-conta/senha']) {
            const link = '.nav-extra a[href="' + path + '"]';
            if (perfil === 'VISUALIZADOR' && path !== '/minha-conta/senha') {
                assert.equal(await evaluate("document.querySelector(" + JSON.stringify(link) + ") === null"), true);
                continue;
            }
            await click(link);
            await until(() => evaluate("document.querySelector('.nav-extra [aria-current=page]')?.pathname === " + JSON.stringify(path)), 'navegação em ' + path);
            assert.ok(await evaluate("!!document.querySelector('.pagina-gestao form[method=post]')"));
        }
        assert.deepEqual(failures, []);
    });

    await t.test('layout em desktop, notebook, tablet e celular', async () => {
        await nav(dashboard);
        await ready("!!document.querySelector('#analysis canvas')");
        for (const [w,h] of [[1920,1080],[1440,900],[1366,768],[820,1180],[390,844]]) {
            await viewport(w,h);
            await delay(250);
            assert.ok(await evaluate('document.documentElement.scrollWidth <= innerWidth + 1'),'Rolagem horizontal em '+w);
            assert.ok(await evaluate("[...document.querySelectorAll('canvas')].every(c=>c.offsetWidth<=c.parentElement.clientWidth+2)"),'Gráfico fora do contêiner em '+w);
        }
        await click('#menu');
        assert.equal(await evaluate("document.querySelector('#menu').getAttribute('aria-expanded')"),'true');
        await view('detalhes');
        assert.ok(await evaluate('document.documentElement.scrollWidth <= innerWidth + 1'));
        for (const id of ['pessoas','centros','evolucao','composicao','qualidade']) {
            await click('#menu');
            await view(id);
            assert.ok(await evaluate('document.documentElement.scrollWidth <= innerWidth + 1'),'Overflow em '+id);
        }
        assert.deepEqual(failures,[]);
    });
});
