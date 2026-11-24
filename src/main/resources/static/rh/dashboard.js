import {el,money,month,number,action,empty,destroyCharts} from './visual.js';
import {fecharMenu} from './navegacao.js';
import {customPeriod,temporal,fieldLabel,filterDialog,metrics,groups} from './filtros.js';
import {executiva,evolucao,composicao,qualidade} from './analises.js';
import {detalhes} from './detalhes.js';
import {organizacao} from './organizacao.js';

const pages={
    executiva:['Visão executiva','Custos de mão de obra e evolução do quadro de funcionários.',executiva],
    evolucao:['Evolução de custos','Compare competências, anos e períodos equivalentes.',evolucao],
    centros:['Centros de custo','Distribuição do custo, composição e evolução por operação.',organizacao],
    composicao:['Composição de custos','Investigue as rubricas e as parcelas que explicam a variação do custo.',composicao],
    pessoas:['Funcionários e cargos','Quadro de funcionários, salários e histórico de lotação.',organizacao],
    detalhes:['Detalhamento analítico','Consulte os valores e as rubricas de cada registro importado.',detalhes],
    qualidade:['Qualidade dos dados','Cobertura das importações, pendências de classificação e conciliação financeira.',qualidade]
};
const q=s=>document.querySelector(s),base=document.body.dataset.base,api=base+'/api',content=q('#analysis');
let active,controller;
function paramsFromPage(){
    const p=new URLSearchParams(location.search);p.delete('visao');
    if(!customPeriod(p)&&!p.has('periodo'))p.set('periodo',location.pathname.endsWith('/detalhamento')?'ultimo':'12');
    return p;
}
function viewFromPage(){const v=new URLSearchParams(location.search).get('visao');
    if(location.pathname.endsWith('/detalhamento'))return 'detalhes';
    return Object.hasOwn(pages,v)?v:'executiva';
}
function url(view,params){const u=new URL(base+(view==='detalhes'?'/detalhamento':''),location.origin);
    const p=new URLSearchParams(params);p.delete('visao');if(view!=='detalhes'&&view!=='executiva')p.set('visao',view);u.search=p;return u;
}
function go(view,patch={},clear=false){
    if(!Object.hasOwn(pages,view))view='executiva';
    const p=clear?new URLSearchParams():paramsFromPage();
    if(view!=='detalhes')for(const k of ['busca','agrupar','colunas','direcao','ordenar','pagina','tamanho'])p.delete(k);
    if(!Object.hasOwn(patch,'pagina') && (view!==viewFromPage()||Object.keys(patch).some(k=>[...temporal,'centro','cargo','funcionario','periodo'].includes(k))))p.delete('pagina');
    for(const [k,v] of Object.entries(patch)){p.delete(k);for(const value of Array.isArray(v)?v:[v])if(value!=null&&value!=='')p.append(k,value);}
    if(clear)p.set('periodo','historico');
    const target=url(view,p);if(target.href===location.href)return;
    const from=location.pathname+location.search;
    history.pushState({from},'',target);window.scrollTo(0,0);render();
}
async function json(path,params,signal){
    const res=await fetch(api+'/'+path+'?'+params.toString(),{headers:{Accept:'application/json'},signal});
    if(res.redirected && new URL(res.url).pathname.endsWith('/login')){location.assign(res.url);throw Error('Sessão expirada.');}
    if(!res.ok){const error=await res.json().catch(()=>null);throw Error(error?.detail||error?.message||error?.erro||'Falha na consulta ('+res.status+').');}
    return res.json();
}
const labels={ano:'Ano',mes:'Mês',centro:'Centro',cargo:'Cargo',funcionario:'Funcionário',rubrica:'Rubrica',grupo:'Grupo',inicio:'De',fim:'Até',competencia:'Competência',baseInicio:'Base',busca:'Busca',metrica:'Métrica',top:'Ranking',ordem:'Ordem',granularidade:'Granularidade'};
function chrome(ctx){
    const {params:p,view,filters:f}=ctx;
    const custom=customPeriod(p);q('#period').value=custom?'personalizado':p.get('periodo')||'ultimo';
    q('#comparison').value=p.get('comparar')||'anterior';
    const center=q('#quick-center'),ids=p.getAll('centro');center.replaceChildren(new Option('Todos os centros',''),...f.centros.map(x=>new Option(x.nome,x.id)));
    if(ids.length>1)center.add(new Option(ids.length+' selecionados','multiplos'));
    if(ids.length===1&&!f.centros.some(x=>x.id===ids[0]))center.add(new Option(ids[0],ids[0]));
    center.value=ids.length>1?'multiplos':ids[0]||'';
    const chips=q('#chips');chips.replaceChildren();for(const [key,value] of p){if(!labels[key])continue;
        const friendly=fieldLabel(key,value,f);const cb=action(labels[key]+' · '+friendly+' ×',()=>{
            const vals=p.getAll(key).filter(v=>v!==value);go(view,{[key]:vals,...(key==='baseInicio'?{comparar:'anterior'}:{})});
        },'chip');cb.setAttribute('aria-label','Remover '+labels[key]+' '+friendly);chips.append(cb);
    }
    if(chips.childElementCount || p.get('periodo')!=='historico')chips.append(action('Limpar filtros',()=>go(view,{},true)));
    const competencia = p.get('competencia') || ctx.data?.qualidade.competenciaPendente || ctx.data?.meses.at(-1)
        || (p.get('periodo') === 'ultimo' ? f.competencias.at(-1) : null);
    const classificacao = new URL(base+'/funcionarios/sem-centro-custo', location.origin);
    if (competencia) classificacao.searchParams.set('competencia', competencia.slice(0,7)+'-01');
    q('[data-classificacao]').href = classificacao;
    for(const a of document.querySelectorAll('#sections a[data-view]')){a.href=url(a.dataset.view,p);if(a.dataset.view===view)a.setAttribute('aria-current','page');else a.removeAttribute('aria-current');}
}
async function render(){
    controller?.abort();controller=new AbortController();const signal=controller.signal,view=viewFromPage(),params=paramsFromPage(),status=q('#status');
    fecharMenu();
    destroyCharts();content.replaceChildren();active=null;
    for(const id of ['filter-open','quick-center','period','comparison','export-open'])q('#'+id).disabled=true;
    const [title,sub,handler]=pages[view];q('#title').textContent=title;document.title=title+' · Mão de obra | Transbom';q('#subtitle').textContent=sub;
    q('#legacy-alert')?.remove();q('#period-stamp').textContent='';q('#chips').replaceChildren();
    status.textContent='Consultando a folha…';content.setAttribute('aria-busy','true');
    try{
        const [filters,data]=await Promise.all([json('filtros',params,signal),view==='detalhes'?Promise.resolve(null):json('resumo',params,signal)]);
        if(signal.aborted)return;
        const m=params.get('metrica'),selectionName=filters.rubricas.find(r=>r.campo===params.get('rubrica'))?.nome||groups[params.get('grupo')]||'Custo total';
        const ctx={base,api,signal,view,params,filters,data,content,go,metric:Object.hasOwn(metrics,m)?m:params.has('rubrica')||params.has('grupo')?'valor':'custo',selectionName,json:(path)=>json(path,params,signal)};
        active=ctx;chrome(ctx);
        if(data?.meses?.length){const stamp=q('#period-stamp'),range=data.meses;stamp.textContent=range.length===1?month(range[0]):month(range[0])+' – '+month(range.at(-1));
            stamp.title='Competências solicitadas: '+range.map(month).join(', ');
            if(data.comparavel&&data.base?.length){const reference=data.base;stamp.append(el('small','','Base: '+month(reference[0])+(reference.length>1?' – '+month(reference.at(-1)):'')));}}
        if(history.state?.from && (view==='detalhes'||view==='pessoas'||view==='composicao'||view==='centros'))content.append(action('← Voltar à análise anterior',()=>history.back(),'back-link'));
        if((params.has('rubrica')||params.has('grupo'))&&view!=='composicao')content.append(el('p','summary-note','Seleção de rubricas: '+selectionName+'. Os demais KPIs e o detalhamento mantêm a folha completa da população filtrada.'));
        if(data?.mensal.some(r=>r.importado===false))content.append(el('div','notice',number(data.mensal.filter(r=>r.importado===false).length)+' competência(s) sem importação no período. Os totais refletem os meses disponíveis.'));
        if(data && !Number(data.atual.registros))content.append(empty(filters.competencias.length?'Nenhum registro no recorte solicitado.':'Nenhuma competência importada.'));
        await handler(ctx);if(signal.aborted)return;status.textContent='';
        for(const id of ['filter-open','quick-center','period','comparison','export-open'])q('#'+id).disabled=false;
    }catch(error){
        if(signal.aborted)return;content.replaceChildren();status.replaceChildren(el('p','notice',error.message),action('Tentar novamente',render,'btn secondary'),action('Limpar filtros',()=>go(view,{},true),'btn secondary'));
    }finally{if(!signal.aborted)content.setAttribute('aria-busy','false');}
}
q('#sections').addEventListener('click',e=>{const a=e.target.closest('a[data-view]');if(a&&!e.ctrlKey&&!e.metaKey&&!e.shiftKey){e.preventDefault();go(a.dataset.view);}});
q('#filter-open').onclick=()=>active&&filterDialog(active);
q('#period').onchange=e=>{if(e.target.value==='personalizado'){filterDialog(active);const form=q('#filters-form');form.periodo.value='personalizado';form.periodo.dispatchEvent(new Event('change',{bubbles:true}));chrome(active);}
    else go(active.view,{...Object.fromEntries(temporal.map(k=>[k,null])),periodo:e.target.value});};
q('#quick-center').onchange=e=>{if(e.target.value==='multiplos')filterDialog(active);else go(active.view,{centro:e.target.value||null});};
q('#comparison').onchange=e=>{if(e.target.value==='personalizado'){filterDialog(active);const f=q('#filters-form');f.comparar.value='personalizado';f.comparar.dispatchEvent(new Event('change',{bubbles:true}));chrome(active);}
    else go(active.view,{comparar:e.target.value,baseInicio:null});};
q('#export-open').onclick=()=>{q('#export-form').tipo.value=({centros:'centros',pessoas:'cargos',composicao:'rubricas',detalhes:'funcionarios',evolucao:active.params.get('granularidade')==='anual'?'anual':'mensal'})[active.view]||'mensal';q('#export-dialog').showModal();};
q('#export-form').onsubmit=e=>{e.preventDefault();const form=e.target,p=new URLSearchParams(active.params);p.set('tipo',form.tipo.value);p.set('formato',form.formato.value);q('#export-dialog').close();location.assign(api+'/exportar?'+p);};
q('#print').onclick=()=>window.print();
document.querySelectorAll('dialog [data-close]').forEach(b=>b.onclick=()=>b.closest('dialog').close());
window.addEventListener('popstate',render);render();
