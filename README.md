# Acesso à aplicação

As operações de negócio exigem autenticação. Para criar o primeiro administrador em um banco vazio, defina `APP_BOOTSTRAP_USERNAME` e `APP_BOOTSTRAP_PASSWORD` antes de iniciar a aplicação. Com Docker Compose, essas variáveis podem ser definidas no arquivo `.env` local.

Na atualização de um banco existente, contas sem perfil recebem `VISUALIZADOR`. Se ainda não houver um `ADMIN`, as variáveis de bootstrap podem indicar uma conta existente. A aplicação confere sua senha atual antes de atribuir o perfil administrativo. Se as credenciais estiverem ausentes ou incorretas, as contas continuam com seus perfis e o log informa como habilitar o primeiro administrador. Após a inicialização administrativa, remova as variáveis de bootstrap.

Acesse `/login` com sua conta. Ambos os perfis podem usar o painel, o detalhamento, as exportações, a classificação de centros de custo e **Gestão → Alterar minha senha**. O administrador também pode importar a folha, conciliar planilhas de pagamentos e criar contas em **Gestão → Criar usuário**. O cadastro oferece os dois perfis e seleciona **Visualizador** por padrão.

O nome de usuário aceita de 3 a 50 caracteres, incluindo letras sem acento, números, ponto, sublinhado e hífen. A senha deve ter pelo menos 8 caracteres e no máximo 72 bytes em UTF-8.

## Análise de mão de obra

O painel em `/mao-de-obra` abre nos últimos 12 meses disponíveis. Os controles de período, organização e comparação são compartilhados pelas sete áreas. A URL conserva o recorte para retorno, favoritos e investigação. A opção **Imprimir análise** abre a impressão do navegador, que também permite salvar em PDF.

### Bases dos indicadores

- O custo é a soma dos valores persistidos de `custoMoTotal`. A entidade calcula o valor por lançamento e o serviço legado lê esse mesmo campo. O total geral inclui a classificação pendente. A aba Qualidade concilia as parcelas classificadas e pendentes.
- O catálogo registra as parcelas diretas da fórmula. Os grupos aditivos incluem cada parcela uma única vez. Descontos, totais e campos de origem têm identificação própria. Para **Comissões sem provisão**, o painel e as exportações exibem somente a rubrica calculada. A diferença entre o custo persistido e os componentes aparece separadamente quando existir.
- Um funcionário é contado uma vez em cada competência. Para múltiplos meses, o quadro único, a média mensal e o encerramento têm denominadores distintos. O custo médio por pessoa-mês divide o gasto pela soma das contagens mensais. Rankings de custo e salário somam os valores do período. O histograma e a mediana usam um salário acumulado por pessoa e competência com salário informado.
- A vinculação histórica prioriza o CPF normalizado. Na ausência dele, usa código externo com nome normalizado e considera o CPF associado quando inequívoco. Registros ambíguos sem identificação suficiente permanecem individuais. O frontend recebe um ID interno. Mudanças de código ou nome sem CPF podem fragmentar o histórico. A qualidade destaca admissões divergentes e duplicidades potenciais.
- Competências importadas são descobertas na tabela de folha. Um mês sem importação aparece como lacuna. Comparações que exigem cobertura equivalente dependem de todos os meses das duas janelas. Base monetária zero permite informar a diferença absoluta e deixa o percentual indefinido.

### Regras que dependem de validação da folha

A base de `outrosEncargosSociaisTrab` inclui o ano da competência antes da aplicação de 7,3%. O importador atribui o código 264 a dois campos de hora extra noturna, ambos presentes na soma oficial do custo. O painel preserva esses critérios e identifica os campos separadamente. A equipe responsável pela folha deve validar a intenção das duas regras. As fontes atuais registram lançamentos e admissões e não oferecem eventos confiáveis de desligamento, absenteísmo ou motivo de variação.

### Páginas e consultas autenticadas

| Caminho a partir de `/mao-de-obra` | Conteúdo |
|---|---|
| Caminho base e `/detalhamento` | Painel executivo e investigação dos registros |
| `/guia` | Guia de indicadores, fórmulas da folha, leitura dos gráficos e catálogo de rubricas |
| `/api/filtros` | Competências, opções dependentes e pesquisa de funcionários com `q` |
| `/api/resumo` | Indicadores, comparações, séries, composição, rankings, insights e auditoria |
| `/api/detalhes` | Registros paginados, totais do recorte e subtotais |
| `/api/perfil/{pessoa}` | Histórico por competência e comparação com o grupo de referência |
| `/api/faixas-salariais/{faixa}` | Pessoas e competências de uma faixa do histograma, 25 observações por página |
| `/api/rubricas/catalogo` | Metadados, origem, natureza e aditividade das rubricas |
| `/api/exportar` | CSV ou XLSX da mesma consulta filtrada |
| `/funcionarios/sem-centro-custo` | Correção existente de classificação por competência |

As consultas usam `GET`. As datas aceitam `AAAA-MM` ou `AAAA-MM-DD` na API e são normalizadas ao primeiro dia. O link legado da página aceita a data ISO completa. O intervalo é inclusivo.

| Parâmetro | Valores e efeito |
|---|---|
| `periodo` | `ultimo`, `3`, `6`, `12`, `historico`, `ytd`, `personalizado` |
| `competencia`, `inicio`, `fim` | Mês específico ou limites do intervalo |
| `ano`, `mes`, `centro`, `cargo`, `funcionario` | Repetir o parâmetro para selecionar vários valores. Usar os identificadores retornados por filtros |
| `comparar`, `baseInicio` | `anterior`, `anual` ou `personalizado` com início da janela comparativa |
| `rubrica`, `grupo` | Campo técnico ou grupo do catálogo. Selecionam a medida investigada. Os indicadores de quadro e de custo geral mantêm a população filtrada |
| `top`, `ordem` | De 1 a 100 itens. Ordenação por `custo`, `quantidade`, `participacao` ou `variacao` |
| `metrica`, `granularidade` | Escolha da série e apresentação mensal ou anual na interface |
| `busca`, `pagina`, `tamanho` | Busca por nome/cargo, página a partir de zero e tamanho entre 1 e 100 no detalhamento |
| `ordenar`, `direcao`, `agrupar` | Campo da tabela, `asc` ou `desc` e agrupamento por `cargo` ou `centro` |
| `tipo`, `formato`, `colunas` | Exportar `funcionarios`, `centros`, `cargos`, `rubricas`, `mensal` ou `anual` em `csv` ou `xlsx`. Repetir `colunas` para projetar campos no detalhe |

### Validação

Use Java 25 e o Maven Wrapper do repositório. O comando `./mvnw verify` compila, testa e empacota a aplicação. No Windows, use `.\mvnw.cmd verify`. Para incluir os testes de integração do PostgreSQL, informe uma URL JDBC local em `FOLHA_TEST_URL` e as credenciais em `FOLHA_TEST_USER` e `FOLHA_TEST_PASSWORD`. Os testes criam uma tabela temporária privada da conexão e a descartam ao fechá-la.

Para os testes de interface, inicie a aplicação com uma folha importada, configure `FOLHA_UI_URL` e as credenciais de teste indicadas no arquivo `src/test/browser/dashboard.mjs`. Execute `node --test src/test/browser/dashboard.mjs` com Node 22+ e um navegador Chromium local.
