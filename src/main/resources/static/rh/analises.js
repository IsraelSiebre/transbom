import {el,panel,grid,table,bars,line,money,pct,number,month,kpis,action,empty,selectControl,icon,palette} from './visual.js';
import {groups,metrics} from './filtros.js';

const costDesc='Soma do custo de mão de obra persistido em todos os registros do recorte, incluindo os pendentes de classificação.';
const descriptions={custo:costDesc,quantidade:'Pessoas distintas no período selecionado. Uma pessoa que trabalhou em vários meses conta uma vez.',custoMedio:'Custo total dividido pela soma das quantidades mensais de pessoas distintas.',salario:'Valores do campo salário acumulados nas competências selecionadas.',fgts:'FGTS registrado na folha.',descontos:'Descontos totais registrados na folha, apresentados separadamente do custo.',extras:'Valores das rubricas de horas extras, em reais.',comissoes:'Comissões com e sem provisão.',encargos:'Parcelas de encargos que compõem o custo da folha.',beneficios:'Benefícios que compõem o custo da folha.',provisoes:'Provisões de férias e de 13º salário.'};
const columns=(spec)=>spec.map(([key,label,format])=>({key,label,format}));
const cloneSort=(rows,key)=>[...rows].sort((a,b)=>(b[key]??-Infinity)-(a[key]??-Infinity));
const top=ctx=>Number(ctx.params.get('top')||10);
const signed=(v,fmt)=>v==null?'—':(v>0?'+':'')+fmt(v);
const percentShare=v=>v==null?'—':pct(v);
const periodPatch=id=>({periodo:null,competencia:id,inicio:null,fim:null,ano:null,mes:null,granularidade:'mensal',pagina:null});
const onMonth=ctx=>row=>ctx.go(row.importado===false?'qualidade':'composicao',periodPatch(row.id));
const onYear=ctx=>row=>{
    const months=ctx.data.meses.filter(d=>d.startsWith(String(row.id)+'-'));
    ctx.go('evolucao',{periodo:null,competencia:null,ano:String(row.id),inicio:months[0],fim:months.at(-1),granularidade:'mensal'});
};
const toCC=ctx=>r=>ctx.go('centros',{centro:r.id});
const toRubric=ctx=>r=>ctx.go('composicao',{rubrica:r.id,grupo:null,metrica:'valor'});
const toGroup=ctx=>r=>r.id==='residual'?ctx.go('qualidade'):ctx.go('composicao',{grupo:r.id,rubrica:null,metrica:'valor'});
function delta(ctx,key) {
    const variation=ctx.data.variacoes?.[key],fmt=key==='quantidade'?number:money;
    if(variation?.diferenca==null)return 'Sem base de comparação';
    const change=variation.diferenca;
    return (change>0?'↗ ':change<0?'↘ ':'→ ')+signed(change,fmt)+' · '+(variation.variacao==null?'Sem base percentual':signed(variation.variacao,pct));
}
function tile(ctx,key,mark) {
    return {label:metrics[key],value:(key==='quantidade'?number:money)(ctx.data.atual[key]),note:delta(ctx,key),title:descriptions[key],icon:mark};
}
export function corrections(ctx) {
    const q=ctx.data.qualidade;
    if(!q.semCentro)return null;
    const box=el('div','notice');box.append(el('span','',number(q.semCentro)+' registro(s) sem centro de custo no período.'));
    const link=el('a','','Classificar '+month(q.competenciaPendente)+' →');link.href=ctx.base+'/funcionarios/sem-centro-custo?competencia='+q.competenciaPendente;box.append(link);return box;
}
function series(ctx,rows,conf,options={}){
    const annual=rows===ctx.data.anual;
    return line(rows,conf,{onSelect:annual?onYear(ctx):onMonth(ctx),label:r=>annual?String(r.id):month(r.id),...options});
}
function insights(ctx) {
    const list=el('div','insight-list');
    for(const i of ctx.data.insights){
        const item=el('article','insight');const symbol=el('span','glyph');symbol.append(icon('spark'));
        const body=el('div');body.append(el('small','',i.relevancia),el('h3','',i.titulo),el('p','',i.evidencia));
        item.append(symbol,body,action('Investigar →',()=>ctx.go(i.visao,i.centro?{centro:i.centro}:i.grupo?{grupo:i.grupo,rubrica:null,metrica:'valor'}:{})));list.append(item);
    }
    return panel('Insights e pontos de atenção','Evidências calculadas para o recorte atual.',list.childElementCount?list:empty('Sem evidências suficientes para gerar insights neste recorte.'));
}
function highlights(rows,key,fmt) {
    const valid=rows.filter(r=>r.importado!==false && r[key]!=null);
    if(!valid.length)return empty('Sem valores no período.');
    const sorted=cloneSort(valid,key),peak=sorted[0],low=sorted.at(-1);
    return el('p','summary-note','Máximo em '+month(peak.id)+' · '+fmt(peak[key])+'  |  Mínimo em '+month(low.id)+' · '+fmt(low[key]));
}
export function executiva(ctx){
    const {content,data:d,params:p,go}=ctx;const notice=corrections(ctx);if(notice)content.append(notice);
    content.append(kpis([tile(ctx,'custo','wallet'),tile(ctx,'quantidade','people'),tile(ctx,'custoMedio','average'),tile(ctx,'salario','layers')]));
    const annual=p.get('granularidade')==='anual';const rows=annual?d.anual:d.mensal;
    const graph=panel('Evolução do custo','Custo de mão de obra e salários por '+(annual?'ano.':'competência.'),
        series(ctx,rows,[{key:'custo',label:'Custo total'},{key:'salario',label:'Salários'}]),action('Analisar evolução →',()=>go('evolucao')));
    const cc=panel('Custo por centro','Participação no custo total do recorte.',
        bars(cloneSort(d.centros,'custo'),{key:'custo',onSelect:toCC(ctx),limit:Number(p.get('top')||5),secondary:r=>percentShare(r.participacao)+' do total'}),
        action('Comparar centros →',()=>go('centros')));
    const mainGrid=grid(graph,cc);mainGrid.classList.add('executive-grid');content.append(mainGrid);
    if(p.has('rubrica')||p.has('grupo'))content.prepend(panel('Valor da seleção',ctx.selectionName,kpis([{label:ctx.selectionName,value:money(d.atual.valor),note:delta(ctx,'valor')}]),action('Investigar composição →',()=>go('composicao'))));
    const fold=el('details','panel fold');fold.append(el('summary','','Indicadores financeiros e operacionais'),kpis(['fgts','descontos','encargos','provisoes','extras','comissoes','beneficios'].map(k=>tile(ctx,k)).concat([
        {label:'Centro de maior custo',value:cloneSort(d.centros,'custo')[0]?.nome||'—',note:money(cloneSort(d.centros,'custo')[0]?.custo)},
        {label:'Cargo de maior custo',value:cloneSort(d.cargos,'custo')[0]?.nome||'—',note:money(cloneSort(d.cargos,'custo')[0]?.custo)}
    ])));content.append(fold);
    content.append(grid(panel('Composição do custo','Parcelas conciliadas com o custo total.',bars(d.grupos,{onSelect:toGroup(ctx),limit:top(ctx),secondary:r=>percentShare(r.participacao)+' do custo'})),
        panel('Evolução do quadro',annual?'Média mensal de funcionários em cada ano.':'Pessoas distintas em cada competência.',
            series(ctx,rows,[{key:annual?'quadroMedio':'quantidade',label:'Funcionários',color:palette[1]}],{format:number}),action('Investigar funcionários →',()=>go('pessoas')))),insights(ctx));
}
export function evolucao(ctx) {
    const {content,data:d,params:p,go}=ctx;const annual=p.get('granularidade')==='anual';
    const rows=annual?d.anual:d.mensal, key=ctx.metric, name=key==='valor'?ctx.selectionName:metrics[key];
    const fmt=key==='quantidade'?number:money;
    const controls=el('div','panel-tools');controls.append(selectControl('Métrica',Object.entries(metrics),key,v=>go('evolucao',{metrica:v})),
        selectControl('Agrupar',[['mensal','Por mês'],['anual','Por ano']],annual?'anual':'mensal',v=>go('evolucao',{granularidade:v})));
    content.append(panel('Evolução de '+name.toLowerCase(),key==='quantidade'?'Pessoas distintas por período.':'Valores em reais, pelas competências de referência.',
        controls,series(ctx,rows,[{key,label:name}],{format:fmt}),highlights(rows,key,fmt)));
    if(!annual && new Set(rows.map(r=>String(r.id).slice(0,4))).size>1){
        const years=[...new Set(rows.map(r=>r.id.slice(0,4)))];
        const points=Array.from({length:12},(_,i)=>({id:String(i+1).padStart(2,'0')}));
        rows.forEach(r=>{if(r.importado)points[Number(r.id.slice(5,7))-1][r.id.slice(0,4)]=r[key];});
        content.append(panel('Comparação entre anos','Mesma métrica alinhada pelo mês do calendário.',line(points,years.map(y=>({key:y,label:y})),{label:r=>r.id,format:fmt,onSelect:r=>go('evolucao',{mes:Number(r.id),competencia:null})})));
    }
    content.append(grid(panel('Histórico por competência','Barras em ordem cronológica. Meses sem importação aparecem na tabela de cobertura.',
        bars(d.mensal.filter(r=>r.importado),{key:'custo',label:r=>month(r.id),onSelect:onMonth(ctx),color:palette[0]})),
        panel('Custo e quadro','Custo médio por pessoa-mês permite acompanhar mudanças no quadro e no gasto individual.',
        table(d.mensal,columns([['id','Competência',month],['custo','Custo',money],['quantidade','Pessoas',number],['mudancaQuadro','Δ pessoas',v=>signed(v,number)],['custoMedio','Custo médio',money],['variacao','Δ custo MoM',pct]]),onMonth(ctx)))));
    content.append(panel('Comparações de custo','MoM, YoY e acumulado do ano até o último mês solicitado. Cada referência exige cobertura equivalente.',
        table(d.comparacoes,columns([['nome','Comparação'],['inicio','De',month],['fim','Até',month],['atual','Atual',money],['baseInicio','Base inicial',month],['baseFim','Base final',month],['anterior','Base',money],['diferenca','Δ absoluto',money],['variacao','Δ relativo',pct]]))));
    const ponte=d.decomposicaoQuadro;
    if(ponte?.diferenca!=null)content.append(panel('Quanto da diferença acompanha o quadro e o custo médio?',
        'O quadro passou de '+number(ponte.pessoasMesBase)+' para '+number(ponte.pessoasMesAtual)+' pessoas-mês. A primeira parcela mantém o custo médio da base. A segunda aplica a mudança de custo médio ao quadro atual.',
        kpis([{label:'Mudança no quadro',value:signed(ponte.volume,money)},
            {label:'Mudança no custo médio',value:signed(ponte.media,money)},
            {label:'Diferença total',value:signed(ponte.diferenca,money)}])));
    const extremes=[
        ['Maior aumento percentual de custo','variacao',1,pct],['Maior queda percentual de custo','variacao',-1,pct],
        ['Maior aumento do quadro','mudancaQuadro',1,number],['Maior redução do quadro','mudancaQuadro',-1,number]
    ].flatMap(([nome,k,sign,fmt])=>{
        const values=d.mensal.filter(r=>r[k]!=null && r[k]*sign>0).sort((a,b)=>sign*(b[k]-a[k]));
        return values.length?[{...values[0],nome,resultado:signed(values[0][k],fmt)}]:[];
    });
    content.append(panel('Maiores mudanças mensais','Cada competência é comparada ao mês imediatamente anterior.',
        table(extremes,columns([['nome','Movimento'],['id','Competência',month],['resultado','Variação']]),onMonth(ctx))));
    content.append(panel('Visão anual','As médias usam os meses importados. Encerramento corresponde à última competência disponível no recorte de cada ano.',
        table(d.anual,columns([['id','Ano'],['custo','Custo',money],['mesesImportados','Meses importados'],['mediaMensal','Custo médio mensal',money],['quantidade','Pessoas únicas',number],['quadroMedio','Quadro médio',number],['ultimaCompetencia','Último mês',month],['encerramento','Quadro final',number],['custoMedio','Custo / pessoa-mês',money],['diferenca','Δ anual',money],['variacao','Δ anual (%)',pct]]),onYear(ctx))));
}
export function composicao(ctx) {
    const {content,data:d,params:p,go}=ctx;const rubric=p.get('rubrica'),group=p.get('grupo'),percent=p.get('unidade')==='percentual',limit=top(ctx);
    const catalogRows=d.rubricas.filter(r=>(!group||r.grupo===group)&&(!rubric||r.id===rubric));
    const selected=Boolean(rubric||group);
    const hdr=el('div','panel-tools');hdr.append(selectControl('Exibir',[['absoluto','Valores em R$'],['percentual','Participação no custo']],percent?'percentual':'absoluto',v=>go('composicao',{unidade:v})));
    const list=percent?catalogRows.filter(r=>r.aditiva):catalogRows.filter(r=>r.grupo!=='totais'||rubric);
    const options={key:percent?'participacao':'valor',format:percent?percentShare:money,onSelect:toRubric(ctx),limit};
    if(selected)content.append(panel(ctx.selectionName,rubric?'Valor do campo selecionado no período.':'Soma das parcelas do grupo segundo a regra da folha.',
        kpis([{label:'Valor acumulado',value:money(d.atual.valor),note:delta(ctx,'valor')},{label:'Participação no custo',value:percentShare(rubric?catalogRows[0]?.participacao:d.grupos.find(g=>g.id===group)?.participacao),note:'Aplicável às parcelas aditivas.'}]),
        el('p','summary-note',d.saldoComposicao!=null?'As demais parcelas e eventuais diferenças de conciliação representam '+money(d.saldoComposicao)+' do custo total.':(catalogRows[0]?.natureza||'Indicador de folha')+'. Consulte a composição por grupo para a soma conciliada do custo.'),
        series(ctx,d.mensal,[{key:'valor',label:ctx.selectionName}]),action('Funcionários e registros →',()=>go('detalhes'))));
    content.append(grid(panel('Principais rubricas',percent?'Denominador: custo total do recorte. Apenas parcelas diretas.':'Valores registrados. Rubricas de desconto e origem têm natureza própria.',hdr,bars(cloneSort(list,options.key),options)),
        panel('Composição por grupo','Parcelas mutuamente exclusivas e eventual diferença de conciliação.',bars(d.grupos,{...options,onSelect:toGroup(ctx)}))));
    content.append(panel('Variação por componente','A diferença de cada grupo corresponde ao valor atual menos sua base. A soma inclui a eventual diferença de conciliação.',
        d.comparavel?table(d.grupos,columns([['nome','Grupo'],['valor','Período',money],['anterior','Base',money],['diferenca','Variação',v=>signed(v,money)],['variacao','Variação (%)',pct]]),toGroup(ctx)):empty('Sem base de comparação')));
    const increases=cloneSort(catalogRows.filter(r=>r.aditiva && r.diferenca>0),'diferenca');
    const reductions=catalogRows.filter(r=>r.aditiva && r.diferenca<0).sort((a,b)=>a.diferenca-b.diferenca);
    content.append(grid(panel('Rubricas com maior aumento','Parcelas aditivas ordenadas pela diferença em reais.',
        d.comparavel?bars(increases,{key:'diferenca',limit,onSelect:toRubric(ctx)}):empty('Sem base de comparação')),
        panel('Rubricas com maior redução','Parcelas aditivas com maior queda em reais.',
        d.comparavel?bars(reductions,{key:'diferenca',limit,onSelect:toRubric(ctx),diverging:true}):empty('Sem base de comparação'))));
    if(selected){content.append(grid(panel(ctx.selectionName+' por centro','Valor acumulado no período.',bars(cloneSort(d.centros,'valor'),{limit,onSelect:r=>go('composicao',{centro:r.id})})),
        panel(ctx.selectionName+' por cargo','Valor acumulado no período.',bars(cloneSort(d.cargos,'valor'),{limit,onSelect:r=>go('composicao',{cargo:r.id})}))));}
    content.append(grid(panel('Despesas variáveis','Evolução mensal de horas extras, comissões e provisões.',series(ctx,d.mensal,[{key:'extras',label:'Horas extras'},{key:'comissoes',label:'Comissões'},{key:'provisoes',label:'Provisões'}])),
        panel('Horas extras por centro','Valores em reais.',bars(cloneSort(d.centros,'extras'),{key:'extras',limit,onSelect:toCC(ctx)}))));
    content.append(panel('Comissões por centro','Comissões com e sem provisão.',bars(cloneSort(d.centros,'comissoes'),{key:'comissoes',limit,onSelect:toCC(ctx)})),
        panel('Catálogo e valores das rubricas','Participação usa o custo total como denominador. O traço indica rubrica fora da decomposição ou base zero.',
        table(catalogRows,columns([['nome','Rubrica'],['codigo','Código'],['id','Campo técnico'],['grupo','Grupo',v=>groups[v]],['natureza','Natureza'],['derivada','Calculada',v=>v?'Sim':'Não'],['valor','Valor',money],['participacao','% do custo',percentShare],['diferenca','Δ absoluto',money],['variacao','Δ relativo',pct]]),toRubric(ctx))));
}
export function qualidade(ctx) {
    const {content,data:d}=ctx;const q=d.qualidade,notice=corrections(ctx);if(notice)content.append(notice);
    content.append(kpis([{label:'Última importação',value:month(q.ultima)},{label:'Competências importadas',value:number(q.competencias)},{label:'Competências ausentes no recorte',value:number(q.ausentes.length)},{label:'Possíveis duplicidades',value:number(q.duplicados),note:'Registros excedentes por pessoa e mês.'}]));
    content.append(panel('Conciliação financeira','Custo classificado e custo pendente somam o custo geral do recorte.',kpis([{label:'Total geral',value:money(d.atual.custo)},{label:'Classificado',value:money(q.classificado)},{label:'Pendente',value:money(q.pendente)},{label:'Diferença da composição',value:money(q.diferencaComposicao)}])));
    const qualityByMonth=new Map((q.porCompetencia||[]).map(row=>[row.competencia,row]));
    const coverage=d.mensal.map(row=>({...row,...qualityByMonth.get(row.id)}));
    content.append(grid(panel('Integridade cadastral','A identificação usa CPF normalizado ou, na sua ausência, código externo e nome. Registros sem chave confiável são isolados.',
        table([{nome:'Sem centro de custo',valor:q.semCentro},{nome:'Sem cargo',valor:q.semCargo},{nome:'Sem CPF',valor:q.semCpf},{nome:'Sem identificador confiável',valor:q.semIdentificador},{nome:'Identificação por chave legada',valor:q.identidadeLegada},{nome:'Chave associada a mais de um CPF',valor:q.identidadeAmbigua},{nome:'Admissão instável no histórico',valor:q.admissaoInstavel},{nome:'Competência anterior à admissão',valor:q.antesAdmissao}],columns([['nome','Ocorrência'],['valor','Registros',number]])),action('Investigar registros →',()=>ctx.go('detalhes'))),
        panel('Cobertura mensal','Meses não importados são identificados separadamente.',
        table(coverage,columns([['id','Competência',month],['importado','Situação',v=>v?'Importada':'Sem importação'],['registros','Registros',number],['quantidade','Funcionários',number],['semCentro','Sem centro',number],['semCargo','Sem cargo',number],['semCpf','Sem CPF',number],['semIdentificador','Sem identificador',number],['duplicados','Possíveis duplicidades',number],['antesAdmissao','Antes da admissão',number]]),r=>ctx.go('detalhes',periodPatch(r.id))))));
    content.append(panel('Rubricas sem valores','Campos sem nenhum lançamento preenchido no recorte.',
        q.rubricasNulas.length?el('p','muted',q.rubricasNulas.join(' · ')):empty('Todos os campos possuem ao menos um valor informado.')),
        panel('Maiores oscilações do custo','Maiores variações mensais em magnitude, entre competências importadas com base comparável.',
            table(d.mensal.filter(r=>r.variacao!=null).sort((a,b)=>Math.abs(b.variacao)-Math.abs(a.variacao)).slice(0,top(ctx)),columns([['id','Competência',month],['custo','Atual',money],['anterior','Anterior',money],['variacao','Variação',pct]]),onMonth(ctx))));
}
