import {el,panel,table,kpis,money,month,number,action,selectControl,empty} from './visual.js';
import {groups} from './filtros.js';

const fixed={nome:'Nome',competencia:'Competência',cargo:'Cargo',centro:'Centro de custo',dataAdmissao:'Admissão',salario:'Salário',fgts:'FGTS',encargos:'Encargos',beneficios:'Benefícios',descontos:'Descontos',custo:'Custo total'};
const base=Object.keys(fixed);
const textFields=new Set(['nome','competencia','cargo','centro','dataAdmissao']);
function format(key,value){
    if(value==null)return '—';
    if(key==='competencia')return month(value);
    if(key==='dataAdmissao')return String(value).split('-').reverse().join('/');
    return textFields.has(key)?value:money(value);
}
function chooseColumns(ctx,names,visible) {
    const details=el('details','fold');details.append(el('summary','','Selecionar colunas'));
    const form=el('form');const list=el('div','column-list');
    for(const [key,name] of Object.entries(names)){const box=el('label','check'),cb=el('input');cb.type='checkbox';cb.value=key;cb.checked=visible.includes(key);cb.name='campo';box.append(cb,document.createTextNode(name));list.append(box);}
    const save=el('button','btn secondary','Aplicar colunas');form.append(list,save);details.append(form);
    form.onsubmit=e=>{e.preventDefault();const selected=new FormData(form).getAll('campo');ctx.go('detalhes',{colunas:selected.length?selected:base});};return details;
}
function expansion(ctx,row){
    const wrapper=el('div');wrapper.append(action('Abrir perfil de '+(row.nome||'funcionário')+' →',()=>ctx.go('pessoas',{funcionario:row.pessoa})));
    const byGroup=new Map();for(const r of ctx.filters.rubricas){if(!byGroup.has(r.grupo))byGroup.set(r.grupo,[]);byGroup.get(r.grupo).push(r);}
    for(const [id,items] of byGroup){const block=el('details');const values=el('div','record-details');block.append(el('summary','',groups[id]||id),values);
        items.forEach(r=>{const p=el('p');p.append(el('span','',r.nome),el('b','',money(row.rubricas[r.campo])));values.append(p);});wrapper.append(block);
    }return wrapper;
}
export async function detalhes(ctx) {
    const {content,params:p,go,filters:f}=ctx;const data=await ctx.json('detalhes');
    if(ctx.signal.aborted)return;
    const names=Object.fromEntries([...Object.entries(fixed),...f.rubricas.filter(r=>!fixed[r.campo]).map(r=>[r.campo,r.nome])]);
    const visible=p.has('colunas')?p.getAll('colunas').filter(key=>Object.hasOwn(names,key)):base;
    const columns=visible.length?visible:base;
    const form=el('form','table-tools');const label=el('label','','Buscar nome ou cargo');const search=el('input');search.type='search';search.value=p.get('busca')||'';search.maxLength=160;search.placeholder='Digite um nome ou cargo';label.append(search);
    form.append(label,el('button','btn secondary','Buscar'),selectControl('Agrupar',[['','Sem agrupamento'],['cargo','Por cargo'],['centro','Por centro de custo']],p.get('agrupar')||'',v=>go('detalhes',{agrupar:v||null,pagina:null})));
    form.onsubmit=e=>{e.preventDefault();go('detalhes',{busca:search.value.trim()||null,pagina:null});};
    content.append(kpis([{label:'Registros encontrados',value:number(data.totalRegistros)},{label:'Custo total do filtro',value:money(data.totais.custo)},{label:'Salários do filtro',value:money(data.totais.salario)},{label:'Descontos do filtro',value:money(data.totais.descontos)}]));
    const container=el('div','table-scroll');container.tabIndex=0;const t=el('table');const head=t.createTHead().insertRow();head.append(el('th','','Registro'));
    for(const key of columns){const th=el('th');th.scope='col';const sort=p.get('ordenar')||'competencia',descending=(p.get('direcao')||'desc')==='desc';
        if(sort===key)th.setAttribute('aria-sort',descending?'descending':'ascending');
        th.append(action(names[key]+(sort===key?(descending?' ↓':' ↑'):''),()=>go('detalhes',{ordenar:key,direcao:sort===key&&descending?'asc':'desc',pagina:null})));head.append(th);
    }
    const body=t.createTBody();
    for(const row of data.linhas){
        const tr=body.insertRow();const toggle=action('Expandir',()=>{const expanding=detail.hidden;detail.hidden=!expanding;toggle.setAttribute('aria-expanded',String(expanding));toggle.textContent=expanding?'Recolher':'Expandir';if(expanding&&!cell.hasChildNodes())cell.append(expansion(ctx,row));});
        toggle.setAttribute('aria-label','Expandir registro de '+(row.nome||'funcionário')+' em '+month(row.competencia));toggle.setAttribute('aria-expanded','false');tr.insertCell().append(toggle);
        for(const key of columns){const value=row[key] ?? row.rubricas?.[key];const cell=tr.insertCell();if(key==='nome')cell.append(action(value||'Sem nome',()=>go('pessoas',{funcionario:row.pessoa})));else cell.textContent=format(key,value);}
        const detail=body.insertRow();detail.hidden=true;const cell=detail.insertCell();cell.colSpan=columns.length+1;
    }
    if(data.linhas.length){const footer=t.createTFoot().insertRow();footer.insertCell().textContent='Total filtrado';
        columns.forEach(k=>footer.insertCell().textContent=data.totais[k]!=null ? money(data.totais[k]) : '');}
    container.append(t);const previous=action('← Anterior',()=>go('detalhes',{pagina:data.pagina-1}),'btn secondary'),next=action('Próxima →',()=>go('detalhes',{pagina:data.pagina+1}),'btn secondary');
    const count=Math.ceil(data.totalRegistros/data.tamanho);previous.disabled=data.pagina<=0;next.disabled=data.pagina+1>=count;
    const pager=el('div','pager');const controls=el('div');controls.append(previous,next);pager.append(selectControl('Linhas por página',[10,25,50,100].map(n=>[n,n]),data.tamanho,v=>go('detalhes',{tamanho:v,pagina:null})),el('span','',count?'Página '+number(data.pagina+1)+' de '+number(count):'Sem páginas'),controls);
    content.append(panel('Registros de mão de obra','Os totais abrangem todos os registros que atendem à busca e aos filtros.',form,chooseColumns(ctx,names,columns),data.linhas.length?container:empty(),pager));
    if(data.subtotais.length){content.append(panel('Subtotais por '+(p.get('agrupar')==='cargo'?'cargo':'centro de custo'),'Todos os registros do grupo no recorte.',
        table(data.subtotais,[{key:'grupo',label:'Grupo'},{key:'registros',label:'Registros',format:number},...columns.filter(k=>!textFields.has(k)).map(k=>({key:k,label:names[k],format:money}))])));}
}
