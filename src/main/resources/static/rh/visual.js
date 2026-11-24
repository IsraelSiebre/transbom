const currency = new Intl.NumberFormat('pt-BR', {style:'currency',currency:'BRL'});
const decimal = new Intl.NumberFormat('pt-BR',{maximumFractionDigits:2});
const short = new Intl.NumberFormat('pt-BR',{notation:'compact',maximumFractionDigits:1});
export const money = v => v == null ? '—' : currency.format(v);
export const number = v => v == null ? '—' : decimal.format(v);
export const pct = v => v == null ? 'Sem base de comparação' : number(v) + '%';
export const month = v => /^\d{4}-\d{2}/.test(String(v)) ? v.slice(5,7)+'/'+v.slice(0,4) : String(v ?? '—');
export const palette = ['#878aff','#22b4be','#689fd7','#afafce','#ab8ae0','#689ca6','#a0aec7','#7d78b9','#5b86a8'];
export function el(tag, className, text) {
    const node=document.createElement(tag);
    if (className) node.className=className;
    if (text != null) node.textContent=text;
    return node;
}
export function action(text, handler, className='text-action') {
    const button=el('button',className,text);
    button.type='button'; button.addEventListener('click',handler);
    return button;
}
const icons={
    overview:'<rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/>',
    trend:'<path d="M3 16l6-6 4 4 8-9m-6 0h6v6"/>', centers:'<rect x="3" y="13" width="5" height="8" rx="1"/><rect x="10" y="8" width="5" height="13" rx="1"/><rect x="17" y="3" width="4" height="18" rx="1"/>',
    layers:'<path d="M2 8l10-5 10 5-10 5zm1 5 9 5 9-5m-18 5 9 5 9-5"/>',people:'<circle cx="9" cy="8" r="3.2"/><path d="M2 21v-2c0-6 14-6 14 0v2M16 5a3 3 0 010 6m2 4c3 0 4 2 4 5"/>',
    table:'<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 10h18M9 10v10"/>',quality:'<path d="M12 2l8 3v6c0 5-4 9-8 11-4-2-8-6-8-11V5z"/><path d="M8 12l3 3 5-6"/>',
    upload:'<path d="M12 16V3m-5 5 5-5 5 5M3 15v5h18v-5"/>',checks:'<path d="M2 6h13l-3-3m3 3-3 3M22 18H9l3 3m-3-3 3-3"/>',
    user:'<circle cx="9" cy="7" r="3"/><path d="M2 20c0-8 14-8 14 0m3-13v6m-3-3h6"/>',logout:'<path d="M9 4H3v16h6m4-4 5-4-5-4m5 4H7"/>',
    lock:'<rect x="5" y="10" width="14" height="11" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3m-4 4v3"/>',
    print:'<path d="M7 7V3h10v4M7 17H3V9h18v8h-4"/><path d="M7 14h10v7H7z"/>',download:'<path d="M12 3v13m-5-5 5 5 5-5M3 16v5h18v-5"/>',menu:'<path d="M3 5h18M3 12h18M3 19h18"/>',
    filters:'<path d="M3 7h7m4 0h7M3 17h11m4 0h3"/><circle cx="12" cy="7" r="2"/><circle cx="16" cy="17" r="2"/>',close:'<path d="M6 6l12 12M18 6L6 18"/>',
    wallet:'<rect x="3" y="6" width="18" height="14" rx="2"/><path d="M3 7V5l13-3v4m5 5h-6v5h6"/>',average:'<path d="M3 19h18M5 15l4-5 4 2 6-8"/><circle cx="5" cy="15" r="1"/><circle cx="13" cy="12" r="1"/>',
    calendar:'<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M7 2v6m10-6v6M3 11h18"/>',spark:'<path d="M12 2l1.8 6.2L20 10l-6.2 1.8L12 18l-1.8-6.2L4 10l6.2-1.8zM20 16l.8 2.2L23 19l-2.2.8L20 22l-.8-2.2L17 19l2.2-.8z"/>',
    arrow:'<path d="M4 12h15m-5-5 5 5-5 5"/>',flag:'<path d="M5 21V3m0 1c5-3 9 3 14 0v10c-5 3-9-3-14 0"/>'
};
export function icon(key) {
    const svg=document.createElementNS('http://www.w3.org/2000/svg','svg');
    for (const [name,value] of Object.entries({viewBox:'0 0 24 24',fill:'none',stroke:'currentColor','stroke-width':1.6,'stroke-linecap':'round','stroke-linejoin':'round','aria-hidden':'true'})) svg.setAttribute(name,value);
    svg.innerHTML=icons[key] || icons.overview;
    return svg;
}
export const empty=(text='Nenhum dado para os filtros selecionados.') => el('p','empty',text);
export function panel(title,description,...children) {
    const node=el('section','panel'); const head=el('header','panel-head');head.append(el('h2','',title));
    if(description) head.append(el('p','',description));
    node.append(head,...children.filter(Boolean));return node;
}
export function grid(...children) { const node=el('div','grid');node.append(...children.filter(Boolean));return node; }
export function kpis(items) {
    const grid=el('div','kpis');
    items.forEach(({label,value,note,title,icon:mark,trend})=>{
        const tile=el('article','kpi');const heading=el('div','kpi-heading');
        if(mark){const badge=el('span','glyph');badge.append(icon(mark));heading.append(badge);}
        heading.append(el('span','',label));tile.append(heading,el('strong','kpi-value',value));
        if(title){tile.title=title;tile.tabIndex=0;}
        if(note)tile.append(el('span','kpi-note',note));
        if(trend)tile.append(el('span','change',trend));
        grid.append(tile);
    });return grid;
}
export function bars(rows,{key='valor',format=money,onSelect,limit=Infinity,label=r=>r.nome,secondary,color,diverging=false}={}) {
    if (!rows?.length) return empty();
    const box=el('div','bars');const kept=rows.slice(0,limit);
    const max=Math.max(...kept.map(r=>Math.abs(r[key]||0)),1);
    kept.forEach((row,index)=>{
        const item=el(onSelect?'button':'div','bar-row');
        if(onSelect){item.type='button';item.onclick=()=>onSelect(row);}
        const caption=el('div','bar-caption');caption.append(el('span','',label(row)),el('strong','',format(row[key])));
        const track=el('span','bar-track');const fill=el('span','bar-fill');
        fill.style.width=Math.min(100,Math.abs(row[key]||0)/max*100)+'%';
        fill.style.background=color || (diverging && row[key]<0 ? palette[1]:palette[index%palette.length]);
        track.append(fill);item.append(caption,track);
        if(secondary)item.append(el('small','bar-secondary',secondary(row)));
        item.title=label(row)+' · '+format(row[key]);box.append(item);
    });return box;
}
export function table(rows,columns,onSelect) {
    if(!rows?.length) return empty();
    const box=el('div','table-scroll');box.tabIndex=0;const table=el('table');const tr=table.createTHead().insertRow();
    columns.forEach(c=>{const th=el('th','',c.label);th.scope='col';tr.append(th);});
    const body=table.createTBody();
    rows.forEach(row=>{const tr=body.insertRow();columns.forEach((c,i)=>{const td=tr.insertCell();const raw=row[c.key];const value=c.format?c.format(raw,row):raw ?? '—';
        if(onSelect && i===0)td.append(action(value,()=>onSelect(row)));else td.textContent=value;});
    });box.append(table);return box;
}
const charts=new Set();
export function destroyCharts() {for(const chart of charts)chart.destroy();charts.clear();}
export function line(rows,series,{onSelect,label=r=>month(r.id),format=money,description='Série temporal',type='line'}={}) {
    if(!rows?.length || rows.every(r=>r.importado===false))return empty();
    const wrap=el('div','line-chart');const frame=el('div','plot');const canvas=el('canvas');
    canvas.setAttribute('role','img');canvas.setAttribute('aria-label',description);frame.append(canvas);wrap.append(frame);
    const colors=series.map((s,i)=>s.color || palette[i%palette.length]);const legend=el('div','legend');
    const highlighted = series.map(s=>{
        const valid=rows.flatMap((r,i)=>r.importado!==false && r[s.key]!=null?[i]:[]);
        if(!valid.length)return new Set();
        let low=valid[0],high=valid[0];
        for(const i of valid){if(rows[i][s.key]<rows[low][s.key])low=i;if(rows[i][s.key]>rows[high][s.key])high=i;}
        return new Set([low,high]);
    });
    series.forEach((s,i)=>{
        const item=el(series.length>1?'button':'span');const dot=el('i');dot.style.background=colors[i];item.append(dot,document.createTextNode(s.label));legend.append(item);
        if(series.length>1){
            item.type='button';item.setAttribute('aria-pressed','true');item.title='Mostrar ou ocultar '+s.label;
            item.onclick=()=>{
                const chart=window.Chart?.getChart(canvas);if(!chart)return;
                const visible=chart.isDatasetVisible(i);chart.setDatasetVisibility(i,!visible);
                item.setAttribute('aria-pressed',String(!visible));chart.update('none');
            };
        }
    });
    wrap.prepend(legend);
    const details=el('details','chart-data');details.append(el('summary','','Ver valores por período'));
    details.addEventListener('toggle',()=>{if(details.open && !details.querySelector('.table-scroll')){
        details.append(table(rows,[{key:'id',label:'Período',format:(v,r)=>label(r)},...series.map(s=>({key:s.key,label:s.label,format:(v,r)=>r.importado===false?'Sem importação':(s.format||format)(v)}))],onSelect));
    }});wrap.append(details);
    requestAnimationFrame(()=>{
        if(!canvas.isConnected)return;
        if(!window.Chart){details.open=true;frame.remove();return;}
        const chart=new window.Chart(canvas,{type,data:{labels:rows.map(label),datasets:series.map((s,i)=>({
            label:s.label,data:rows.map(r=>r.importado===false?null:r[s.key] ?? null),borderColor:colors[i],backgroundColor:colors[i]+'20',
            fill:type==='line' && i===0,pointRadius:c=>highlighted[i].has(c.dataIndex)?4.5:rows.length>40?0:rows.length>18?2:3,pointHoverRadius:6,pointBackgroundColor:colors[i],
            pointBorderWidth:0,borderWidth:2.5,tension:.15,spanGaps:false
        }))},options:{responsive:true,maintainAspectRatio:false,animation:false,interaction:{mode:'index',intersect:false},
            onClick:(event,elements)=>{if(elements.length && onSelect)onSelect(rows[elements[0].index]);},
            scales:{x:{grid:{display:false},ticks:{color:'#afafce',maxTicksLimit:10,maxRotation:0}},y:{beginAtZero:true,grid:{color:'#ffffff10'},border:{display:false},ticks:{color:'#afafce',count:5,callback:v=>short.format(v)}}},
            plugins:{legend:{display:false},tooltip:{backgroundColor:'#02081b',titleColor:'#fff',bodyColor:'#dddff5',borderColor:'#4e5879',borderWidth:1,padding:12,callbacks:{label:c=>c.dataset.label+' · '+(series[c.datasetIndex].format||format)(c.raw)}}}
        }});
        charts.add(chart);
    });return wrap;
}
export function selectControl(label,options,value,onChange) {
    const wrap=el('label','inline-control',label);const select=el('select');options.forEach(([id,name])=>select.add(new Option(name,id)));select.value=value;
    select.onchange=()=>onChange(select.value);wrap.append(select);return wrap;
}
