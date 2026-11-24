import {el, month} from './visual.js';
export const groups={remuneracao:'Salários e remunerações',extras:'Horas extras e adicionais',comissoes:'Comissões',beneficios:'Benefícios',encargos:'Encargos',provisoes:'Provisões',ferias:'Férias e rescisões',descontos:'Descontos',outros:'Outros',informativos:'Informativos',totais:'Totais'};
export const metrics={custo:'Custo total',salario:'Salários',quantidade:'Funcionários distintos',custoMedio:'Custo médio por funcionário-mês',extras:'Horas extras',encargos:'Encargos',beneficios:'Benefícios',comissoes:'Comissões',provisoes:'Provisões',fgts:'FGTS',descontos:'Descontos',valor:'Rubrica ou grupo selecionado'};
const presets=[['ultimo','Última competência'],['3','Últimos 3 meses'],['6','Últimos 6 meses'],['12','Últimos 12 meses'],['historico','Todo o histórico'],['ytd','Ano até a última competência'],['personalizado','Anos, meses ou intervalo']];
const compare=[['anterior','Período anterior'],['anual','Ano anterior'],['personalizado','Período personalizado']];
const monthNames=Array.from({length:12},(_,i)=>new Intl.DateTimeFormat('pt-BR',{month:'long'}).format(new Date(2000,i,1)));
export const temporal=['competencia','inicio','fim','ano','mes'];
export function customPeriod(params){return temporal.some(k=>params.has(k));}
export function fieldLabel(key,val,filters) {
    const dict={periodo:Object.fromEntries(presets),comparar:Object.fromEntries(compare),centro:Object.fromEntries(filters.centros.map(x=>[x.id,x.nome])),funcionario:Object.fromEntries(filters.funcionarios.map(x=>[x.id,x.nome])),rubrica:Object.fromEntries(filters.rubricas.map(x=>[x.campo,x.nome])),grupo:groups,metrica:metrics};
    if(['competencia','inicio','fim','baseInicio'].includes(key))return month(val);
    if(key==='mes')return monthNames[Number(val)-1];if(key==='cargo'&&val==='__pendente__')return 'Sem cargo';
    return dict[key]?.[val] ?? (key==='funcionario'?'Funcionário selecionado':val);
}
function selectField(name,label,options,selected,all=false) {
    const box=el('label','',label);const node=el('select');node.name=name;
    if(all)node.add(new Option('Todos',''));
    options.forEach(([id,label])=>node.add(new Option(label,id)));
    node.value=selected ?? (all?'':options[0][0]);if(node.selectedIndex<0)node.selectedIndex=0;box.append(node);return box;
}
function inputMonth(name,title,value) {
    const label=el('label','',title);const input=el('input');input.type='month';input.name=name;input.value=value?.slice(0,7)||'';label.append(input);return label;
}
function section(title,...fields){const node=el('fieldset');node.append(el('legend','',title),...fields);return node;}
function choices(name,title,items,selected,search=false) {
    const details=el('details','multi');const summary=el('summary');const list=el('div','list');
    const update=()=>{const count=list.querySelectorAll(':checked').length;summary.textContent=title+(count?' · '+count:'');};
    const add=(items)=>{
        const chosen=new Map([...list.querySelectorAll(':checked')].map(c=>[c.value,c.parentElement.textContent.trim()]));
        list.replaceChildren();for(const [id,label] of new Map([...chosen,...items.map(([k,v])=>[String(k),v])])){
            const wrap=el('label','check');const cb=el('input');cb.type='checkbox';cb.name=name;cb.value=id;
            cb.checked=selected.includes(id)||chosen.has(id);wrap.append(cb,document.createTextNode(label));list.append(wrap);
        }
        if(!list.childElementCount)list.append(el('small','muted','Nenhuma opção disponível.'));update();
    };
    list.addEventListener('change',()=>{selected=[...list.querySelectorAll(':checked')].map(c=>c.value);update();});
    details.append(summary);
    if(search){const input=el('input');input.type='search';input.placeholder='Pesquisar por nome';input.setAttribute('aria-label',input.placeholder);details.append(input);}
    details.append(list);add(items);return {node:details,add};
}
export function filterDialog(ctx) {
    const {filters:f,params:p,go,view,api}=ctx;const dialog=document.querySelector('#filters-dialog'),body=document.querySelector('#filter-fields'),form=document.querySelector('#filters-form');
    const current=p.get('periodo') || (customPeriod(p)?'personalizado':'ultimo');
    const dates=f.competencias.map(v=>String(v).slice(0,10));
    const availableFields=f.rubricas.filter(r=>!f.rubricasDisponiveis||f.rubricasDisponiveis.includes(r.campo)||r.campo===p.get('rubrica'));
    const years=[...new Set(dates.map(m=>m.slice(0,4)))].sort().reverse();
    const months=[...new Set(dates.map(m=>String(Number(m.slice(5,7)))))].sort((a,b)=>a-b);
    const employees=choices('funcionario','Funcionários',f.funcionarios.map(v=>[v.id,v.nome]),p.getAll('funcionario'),true);
    const centers=choices('centro','Centros de custo',f.centros.map(v=>[v.id,v.nome]),p.getAll('centro'));
    const roles=choices('cargo','Cargos',f.cargos.map(v=>[v,v==='__pendente__'?'Sem cargo':v]),p.getAll('cargo'));
    const period=selectField('periodo','Período',presets,current),comp=selectField('comparar','Comparação',compare,p.get('comparar'));
    const dateFields=[
        selectField('competencia','Competência específica',dates.map(m=>[m,month(m)]),p.get('competencia')?.slice(0,7)+'-01',true),
        choices('ano','Anos',years.map(v=>[v,v]),p.getAll('ano')).node,
        choices('mes','Meses',months.map(v=>[v,monthNames[Number(v)-1]]),p.getAll('mes')).node,
        inputMonth('inicio','De (inclusive)',p.get('inicio')),inputMonth('fim','Até (inclusive)',p.get('fim'))
    ];
    const base=inputMonth('baseInicio','Início do período de comparação',p.get('baseInicio'));
    body.replaceChildren(section('Período e comparação',period,comp,...dateFields,base),
        section('Organização',centers.node,roles.node,employees.node),
        section('Análise',selectField('grupo','Grupo de rubricas',Object.entries(groups).filter(([id])=>availableFields.some(r=>r.grupo===id && (r.aditiva || id==='descontos'))),p.get('grupo'),true),
        selectField('rubrica','Rubrica',availableFields.map(r=>[r.campo,r.nome]),p.get('rubrica'),true),
        selectField('metrica','Métrica da evolução',Object.entries(metrics),p.get('metrica')||(p.has('rubrica')||p.has('grupo')?'valor':'custo')),
        selectField('granularidade','Granularidade',[['mensal','Mensal'],['anual','Anual']],p.get('granularidade')),
        selectField('top','Itens nos rankings',[5,10,20,50,100].map(k=>[k,k]),p.get('top')||10),
        selectField('ordem','Ordenar rankings por',[['custo','Custo'],['quantidade','Quantidade'],['participacao','Participação'],['variacao','Variação']],p.get('ordem'))));
    function timeFields(){
        for(const n of dateFields){n.hidden=form.periodo.value!=='personalizado';n.querySelectorAll('input,select').forEach(i=>i.disabled=n.hidden);}
        base.hidden=form.comparar.value!=='personalizado';base.querySelector('input').disabled=base.hidden;base.querySelector('input').required=!base.hidden;
    }
    period.onchange=timeFields;comp.onchange=timeFields;timeFields();
    const updateRubrics=()=>{for(const opt of form.rubrica.options){const meta=f.rubricas.find(r=>r.campo===opt.value);opt.hidden=Boolean(form.grupo.value && meta && meta.grupo!==form.grupo.value);}
        if(form.rubrica.selectedOptions[0]?.hidden)form.rubrica.value='';};
    form.grupo.onchange=()=>{updateRubrics();form.metrica.value=form.grupo.value?'valor':'custo';};form.rubrica.onchange=()=>{form.metrica.value=form.rubrica.value || form.grupo.value?'valor':'custo';};updateRubrics();
    let timer,version=0;const search=employees.node.querySelector('[type=search]');
    const refresh=(employeeOnly=false)=>{
        const ticket=++version;clearTimeout(timer);
        timer=setTimeout(async()=>{const q=new URLSearchParams(new FormData(form));
            if(q.get('periodo')==='personalizado'){q.delete('periodo');if(!temporal.some(k=>q.get(k)))q.set('periodo','historico');}
            for(const [k,v] of [...q])if(!v)q.delete(k,v);
            q.set('q',search.value.trim());try{const res=await fetch(api+'/filtros?'+q,{signal:ctx.signal});if(!res.ok)throw Error();const next=await res.json();
                if(ticket!==version||!dialog.open)return;
                employees.add(next.funcionarios.map(v=>[v.id,v.nome]));
                if(!employeeOnly){centers.add(next.centros.map(v=>[v.id,v.nome]));roles.add(next.cargos.map(v=>[v,v==='__pendente__'?'Sem cargo':v]));}
            }catch{if(ticket===version && dialog.open)search.placeholder='Falha na pesquisa. Tente novamente.';}
        },250);
    };
    search.oninput=()=>refresh(true);form.onchange=e=>{if(temporal.includes(e.target.name)||['centro','cargo','periodo'].includes(e.target.name))refresh();};
    form.onsubmit=e=>{
        e.preventDefault();const values=new URLSearchParams(new FormData(form));
        if(values.get('inicio') && values.get('fim') && values.get('inicio')>values.get('fim')){form.fim.setCustomValidity('A competência final deve ser igual ou posterior à inicial.');form.fim.reportValidity();return;}
        const keys=['periodo',...temporal,'comparar','baseInicio','centro','cargo','funcionario','grupo','rubrica','metrica','granularidade','top','ordem'];
        const patch=Object.fromEntries(keys.map(k=>[k,values.getAll(k).filter(Boolean)]));
        if(values.get('periodo')==='personalizado')patch.periodo=temporal.some(k=>values.get(k))?null:'historico';
        for(const k of ['inicio','fim','baseInicio'])if(values.get(k))patch[k]=values.get(k)+'-01';
        patch.pagina=null;dialog.close();go(view,patch);
    };
    form.fim.oninput=()=>form.fim.setCustomValidity('');
    document.querySelector('#clear-all').onclick=()=>{dialog.close();go(view,{},true);};dialog.showModal();
}
