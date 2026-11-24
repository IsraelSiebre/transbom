import {action, bars, el, empty, grid, kpis, line, money, month, number, panel, pct, selectControl, table} from './visual.js';

const financial = [
    ['custo','Custo total'], ['custoMedio','Custo médio'], ['salario','Salários'],
    ['salarioMedio','Salário médio'], ['extras','Horas extras'], ['beneficios','Benefícios'],
    ['encargos','Encargos'], ['comissoes','Comissões'], ['provisoes','Provisões']
];
const orgColumns = [
    {key:'nome',label:'Operação / função'}, {key:'quantidade',label:'Pessoas distintas',format:number},
    ...financial.map(([key,label]) => ({key,label,format:money})),
    {key:'participacao',label:'% do custo', format:pct},
    {key:'diferenca',label:'Variação em R$',format:optionalMoney},
    {key:'variacao',label:'Variação em %',format:v => v == null ? 'Sem base' : pct(v)}
];
const centerColumns = [...orgColumns,
    {key:'variacaoMensal',label:'Último mês × anterior',format:pct},
    {key:'variacaoAnual',label:'Último mês × ano anterior',format:pct},
    {key:'mudancaParticipacao',label:'Mudança na participação',format:v=>v == null ? 'Sem base' : number(v)+' p.p.'}];
const scale = ['#898bff','#12a4b4','#5899da','#afafce','#92bbbc','#7d83af','#69769e'];
function optionalMoney(v) { return v == null ? 'Sem base' : money(v); }
function pickMonth(d) {
    return {competencia:d, ano:null, mes:null, inicio:null, fim:null, periodo:null};
}
function rank(rows,key,limit,select,fmt=money) {
    const sorted = [...rows].sort((a,b) => (b[key] ?? 0) - (a[key] ?? 0));
    return bars(sorted,{key,format:fmt,onSelect:select,limit});
}
function trends(ctx, grouped, selection, metric, format=money) {
    if (!selection.length) return empty('Nenhum grupo disponível.');
    const rows = (ctx.data.mensal || []).map(m => {
        const row = {...m, label:month(m.competencia)};
        for (let i=0;i<selection.length;i++) row['grupo'+i] = m.importado ? 0 : null;
        return row;
    });
    const byMonth = new Map(rows.map(r => [r.competencia,r]));
    const keys = new Map(selection.map((r,i) => [r.id,'grupo'+i]));
    for (const r of grouped) {
        const row = byMonth.get(r.competencia);
        if (row && keys.has(r.id)) row[keys.get(r.id)] = r[metric];
    }
    return line(rows,selection.map((r,i) => ({key:keys.get(r.id),label:r.nome,color:scale[i%7],format})),
        {label:r=>month(r.competencia),onSelect:r=>ctx.go('composicao',pickMonth(r.competencia))});
}

function heatmap(ctx, centers, monthly) {
    const months = (ctx.data.mensal || []).map(r=>r.competencia);
    const imported = new Set((ctx.data.mensal || []).filter(r=>r.importado).map(r=>r.competencia));
    if (!months.length || !centers.length) return empty('Sem competências para o mapa de calor.');
    const entries = new Map(monthly.map(r => [r.id+'|'+r.competencia, r]));
    const max = monthly.reduce((m,r) => Math.max(m,Math.abs(r.custo)),0);
    const wrap = el('div','org-scroll');
    const t = el('table','heat-table');
    const head = t.createTHead().insertRow();
    ['Centro', ...months.map(month)].forEach(name => head.append(el('th','',name)));
    const body = t.createTBody();
    for (const c of centers) {
        const row = body.insertRow();
        row.append(el('th','',c.nome));
        for (const m of months) {
            const point = entries.get(c.id+'|'+m) || {custo:0};
            const cell = row.insertCell();
            if (!imported.has(m)) { cell.textContent = 'Sem importação'; continue; }
            const btn = el('button','heat-cell',money(point.custo));
            btn.style.setProperty('--level', String(max ? Math.min(.9, .08 + .82*Math.abs(point.custo)/max) : .08));
            btn.title = c.nome+' · '+month(m)+' · '+money(point.custo);
            btn.setAttribute('aria-label',btn.title);
            btn.onclick = () => ctx.go('centros',{...pickMonth(m),centro:c.id});
            cell.append(btn);
        }
    }
    wrap.append(t);
    return wrap;
}

function stacks(ctx, centers) {
    const groups = [['salario','Salário'],['extras','Horas extras'],['comissoes','Comissões'],
        ['beneficios','Benefícios'],['encargos','Encargos'],['provisoes','Provisões']];
    const wrap = el('div','stack-list');
    for (const c of centers) {
        const button = el('button','stack-item');
        button.append(el('span','stack-label',c.nome));
        const strip = el('span','stack-track');
        const other = c.custo - groups.reduce((sum,[key])=>sum+Number(c[key] || 0),0);
        const values = [...groups.map(([key,label])=>[label,c[key]]), ['Demais parcelas',other]];
        // Valores negativos não têm representação fiel em uma barra de participação.
        if (c.custo > 0 && values.every(([,v])=>v >= -.005)) {
            for (const [i,[label,value]] of values.entries()) {
                const part = el('span','stack-part');
                part.style.width = (100*value/c.custo)+'%';
                part.style.backgroundColor = scale[i];
                part.title = label+' · '+money(value)+' · '+pct(100*value/c.custo);
                strip.append(part);
            }
        } else strip.textContent = 'Consulte os valores na matriz';
        button.append(strip,el('span','',money(c.custo)));
        button.title = values.map(([label,value])=>label+' '+money(value)).join(' · ');
        button.onclick = () => ctx.go('composicao',{centro:c.id});
        wrap.append(button);
    }
    const legend = el('div','stack-legend');
    [...groups.map(g=>g[1]),'Demais parcelas'].forEach((name,i)=>{
        const label = el('span','',name);
        label.style.setProperty('--swatch',scale[i]);
        legend.append(label);
    });
    wrap.append(legend);
    return wrap;
}

function centersView(ctx) {
    const d = ctx.data, centers = d.centros || [], series = d.centrosMensais || [];
    const limit = Number(ctx.params.get('top') || 10);
    const drill = r => ctx.go('pessoas',{centro:r.id});
    ctx.content.append(
        grid(panel('Custo por centro','Participação sobre o custo total filtrado.',
                rank(centers,'custo',limit,drill)),
            panel('Evolução por centro','Custo por competência e operação.',
                trends(ctx,series,centers,'custo'))),
        panel('Composição por operação','Percentual do custo total de cada centro, com as demais parcelas reunidas no saldo.',
            stacks(ctx,centers)),
        panel('Matriz de centros de custo','Selecione uma operação para investigar cargos e funcionários.',
            table(centers,centerColumns,drill)),
        grid(panel('Horas extras por operação','Valor acumulado no período.',
                rank(centers,'extras',limit,drill)),
            panel('Comissões por operação','Valor acumulado no período.',
                rank(centers,'comissoes',limit,drill))),
        panel('Mapa de calor mensal','A intensidade acompanha o custo em reais. Selecione uma célula para investigar.',
            heatmap(ctx,centers,series))
    );
}

function dateBR(v) {return v ? v.slice(0,10).split('-').reverse().join('/') : 'Sem registro';}
const bands = ['Salário negativo','Igual a zero','Até R$ 1.999,99','R$ 2 mil a 3.999,99',
    'R$ 4 mil a 5.999,99','R$ 6 mil a 9.999,99','R$ 10 mil ou mais'];

async function openBand(ctx, node, faixa, pagina=0) {
    const q = new URLSearchParams(ctx.params);
    q.set('paginaFaixa',pagina);
    node.replaceChildren(el('p','muted','Consultando salários…'));
    node.dataset.consulta = faixa+'-'+pagina;
    try {
        const res = await fetch(ctx.api+'/faixas-salariais/'+faixa+'?'+q, {signal:ctx.signal});
        if (!res.ok) throw Error();
        const rows = await res.json();
        if (ctx.signal.aborted || node.dataset.consulta !== faixa+'-'+pagina) return;
        node.replaceChildren(el('h3','',bands[faixa+1]),table(rows,[
            {key:'nome',label:'Funcionário'}, {key:'competencia',label:'Competência',format:month},
            {key:'salario',label:'Salário registrado',format:money}],
            r=>ctx.go('pessoas',{...pickMonth(r.competencia),funcionario:r.pessoa})));
        if (pagina>0) node.append(action('Anterior',()=>openBand(ctx,node,faixa,pagina-1)));
        if (rows.length && (pagina+1)*25 < rows[0].registros)
            node.append(action('Próxima',()=>openBand(ctx,node,faixa,pagina+1)));
    } catch(error) {
        if (!ctx.signal.aborted) node.replaceChildren(empty('Falha na consulta da faixa salarial.'),
            action('Tentar novamente',()=>openBand(ctx,node,faixa,pagina)));
    }
}
function vinculo(admissao, competencia) {
    if (!admissao || !competencia) return '';
    const [ay,am] = admissao.split('-').map(Number), [cy,cm] = competencia.split('-').map(Number);
    const meses = 12*(cy-ay)+cm-am;
    if (meses < 0) return ' · Admissão posterior à competência';
    return ' · '+number(meses)+' mês(es) desde o mês da admissão registrada';
}
async function profile(ctx, id) {
    const url = new URL(ctx.api+'/perfil/'+encodeURIComponent(id), location.origin);
    url.search = ctx.params.toString();
    const response = await fetch(url,{signal:ctx.signal,headers:{Accept:'application/json'}});
    if (!response.ok) throw new Error('Não foi possível consultar o perfil.');
    const rows = (await response.json()).historico || [];
    if (ctx.signal?.aborted || !rows.length) return;
    const r = rows[rows.length-1];
    const verbas = el('details','chart-data');
    const values = el('div');
    const showValues = index => {
        const row = rows[index];
        const catalog = ctx.filters.rubricas.map(v=>({...v, valor:row.rubricas[v.campo]}))
            .sort((a,b)=>Math.abs(b.valor)-Math.abs(a.valor));
        values.replaceChildren(table(catalog,[{key:'nome',label:'Rubrica'}, {key:'codigo',label:'Código'},
            {key:'natureza',label:'Natureza'}, {key:'valor',label:'Valor',format:money}],
            v=>ctx.go('composicao',{...pickMonth(row.competencia),rubrica:v.campo,grupo:null})));
    };
    verbas.append(el('summary','','Consultar rubricas por competência'),
        selectControl('Registro',rows.map((s,i)=>[String(i),month(s.competencia)+' · '+s.centro+' · '+(s.cargo||'Sem cargo')]),
            String(rows.length-1),showValues),values);
    verbas.addEventListener('toggle',()=>{ if(verbas.open && !values.childElementCount) showValues(rows.length-1); });
    const header = panel(r.nome,'Registro de '+month(r.competencia)+' · '+(r.cargo || 'Sem cargo')+' · '+r.centro+
        ' · Admissão '+dateBR(r.dataAdmissao)+vinculo(r.dataAdmissao,r.competencia),
        kpis([['Salário',r.salario],['Custo individual',r.custo],['Média do cargo',r.mediaCargo],
            ['Média do centro',r.mediaCentro]].map(([label,value])=>({label,value:optionalMoney(value),note:month(r.competencia)}))),
        table(rows,[{key:'competencia',label:'Competência',format:month},{key:'cargo',label:'Cargo'},
            {key:'centro',label:'Centro'},...['salario','custo','encargos','beneficios','provisoes','descontos'].map((key,i)=>(
                {key,label:['Salário','Custo','Encargos','Benefícios','Provisões','Descontos'][i],format:money})),
            {key:'mediaCargo',label:'Média do cargo',format:optionalMoney},{key:'mediaCentro',label:'Média do centro',format:optionalMoney}
        ],row=>ctx.go('detalhes',pickMonth(row.competencia))),
        verbas, action('Consultar todo o histórico disponível',()=>ctx.go('pessoas',{
            ...pickMonth(null),periodo:'historico',cargo:null,centro:null,comparar:'anterior',baseInicio:null})));
    ctx.content.prepend(header);
}

export async function organizacao(ctx) {
    if (ctx.view === 'centros') { centersView(ctx); return; }
    const d = ctx.data, jobs = d.cargos || [], centers = d.centros || [];
    const limit = Number(ctx.params.get('top') || 10);
    const selectPerson = r => ctx.go('pessoas',{funcionario:String(r.id)});
    const selectJob = r => ctx.go('pessoas',{cargo:r.id});
    const stat = d.estatisticas || {};
    const histogram = (d.histograma || []).map(r=>({...r,nome:bands[Number(r.faixa)+1]}));
    const detailsBand = el('div');
    detailsBand.setAttribute('aria-live','polite');
    ctx.content.append(
        kpis([['Salário médio',stat.media],['Mediana salarial',stat.mediana],['Menor salário',stat.minimo],['Maior salário',stat.maximo]]
            .map(([label,value])=>({label,value:optionalMoney(value),note:'Por pessoa e competência'}))),
        grid(panel('Ranking de salários','Salários acumulados nas competências selecionadas.',
                rank(d.salarios || [],'salario',limit,selectPerson)),
            panel('Ranking de custo individual','Custo de mão de obra acumulado por pessoa.',
                rank(d.custosFuncionarios || d.funcionarios || [],'custo',limit,selectPerson))),
        panel('Funcionários na seleção','Pessoas com registros no período selecionado, na ordem dos filtros. Valores acumulados.',
            table(d.funcionarios || [],[{key:'nome',label:'Funcionário'},
                {key:'contratos',label:'Competências com registro',format:number},
                {key:'valor',label:'Rubrica, grupo ou custo',format:money},
                {key:'participacao',label:'% do custo total',format:pct},
                {key:'diferenca',label:'Mudança no valor selecionado',format:optionalMoney}],selectPerson)),
        grid(panel('Distribuição salarial','Uma observação por pessoa e competência com salário informado.',
                bars(histogram,{key:'quantidade',format:number,onSelect:r=>openBand(ctx,detailsBand,r.faixa)}),detailsBand),
            panel('Custo médio por funcionário','Custo dividido pelo quadro de cada competência.',
                line((d.mensal || []).map(r=>({...r,label:month(r.competencia)})),
                    [{key:'custoMedio',label:'Custo médio',format:money}],{label:r=>month(r.competencia),
                        onSelect:r=>ctx.go('detalhes',pickMonth(r.competencia))}))),
        grid(panel('Funcionários por centro','Pessoas distintas no período em cada centro.',
                rank(centers,'quantidade',limit,r=>ctx.go('pessoas',{centro:r.id}),number)),
            panel('Funcionários por cargo','Pessoas distintas no período em cada função.',
                rank(jobs,'quantidade',limit,selectJob,number))),
        grid(panel('Cargos por custo','Custo total no período.',rank(jobs,'custo',limit,selectJob)),
            panel('Cargos por salário médio','Base em pessoas e competências.',
                rank(jobs,'salarioMedio',limit,selectJob))),
        panel('Evolução dos cargos','Cargos de maior custo. Os filtros permitem isolar uma ou mais funções.',
            trends(ctx,d.cargosMensais || [],[...jobs].sort((a,b)=>b.custo-a.custo).slice(0,Math.min(limit,8)),'custo')),
        panel('Indicadores por cargo','Selecione um cargo para ver a distribuição e o quadro correspondente.',
            table(jobs,orgColumns,selectJob)),
        panel('Matriz de cargos e centros','Custo e quadro nas combinações registradas na folha.',
            table(d.matriz || [],[{key:'cargo',label:'Cargo'},{key:'id',label:'Centro'},
                {key:'custo',label:'Custo',format:money},{key:'quantidade',label:'Pessoas distintas',format:number}],
                row=>ctx.go('detalhes',{centro:row.id,cargo:row.cargo})))
    );
    const ids = ctx.params.getAll('funcionario');
    if (ids.length===1) await profile(ctx,ids[0]);
}
