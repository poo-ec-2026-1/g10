# Relatório Individual de Produção
## Projeto Pokédex — Arquitetura e Modelagem UML (g10)

**Aluno:** Gabriel | **Disciplina/Professor:** Akira | **Grupo:** G10
**Repositório:** https://github.com/poo-ec-2026-1/g10

---

## 1. Atribuição de cargo e tarefas

Fui responsável pela arquitetura e modelagem de classes do projeto Pokédex: decidir quantas classes o sistema precisaria, por que cada uma existe, e produzir os diagramas UML que documentam essa arquitetura. No primeiro documento entregue ao professor, também fiquei responsável pela codificação de todas as classes entidade, com exceção da AppPokedex.

### 1.1 Etapa 1 — Atribuição inicial

- Definir o modelo de domínio: quantas classes seriam necessárias e por que cada uma deveria existir separadamente, em vez de virar atributo de outra.
- Produzir o diagrama de classes protótipo, com as entidades centrais (Pokemon, Golpe, Nature, TipoEfetividade).
- Implementar essas classes entidade em Java, com as anotações ORMLite necessárias.

### 1.2 Etapa 2 — Ampliação da atribuição

- Modelar e implementar a funcionalidade de time de Pokémon (Equipe e MembroTime), sugerida pelo Guilherme.
- Produzir o diagrama de classes final, já com as novas entidades incorporadas.
- Produzir o diagrama de regras de negócio (atividades), sugerido pelo professor após reunião de acompanhamento.
- Preparar e gravar a apresentação em vídeo da minha parte do projeto.

---

## 2. Contribuição de acordo com a atribuição

### 2.1 Etapa 1 — O que foi entregue

O ponto de partida foi decidir a arquitetura: quantas classes o sistema precisaria e por que cada uma deveria existir separadamente. Minha ideia inicial era deixar Golpe e Nature como atributos simples dentro de Pokemon. Ao tentar aplicar essa lógica, ela não se sustentou: um golpe carrega vários atributos próprios (nome, tipo, poder, precisão, categoria), e o mesmo vale pra Nature.

Vindo de uma base forte em C, pensei nisso como um struct — só que Java não tem struct; o equivalente correto é criar uma classe. A regra que usei pro resto do projeto foi: **atributo simples fica dentro da classe; qualquer conceito com mais de um dado próprio vira uma classe separada.** Foi assim que cheguei às quatro entidades centrais: Pokemon, Golpe, Nature e TipoEfetividade.

Essa arquitetura foi registrada no diagrama de classes protótipo (Figura 1) — feito só pra ter uma noção geral do sistema, antes de a funcionalidade de time existir. Por isso ele ainda não contém Equipe nem MembroTime.

> **Figura 1** — Diagrama de classes protótipo (versão inicial, sem Equipe/MembroTime).
> Estrutura: `PopularBanco` → chama os quatro Repositories (`PokemonRepository`, `GolpeRepository`, `NatureRepository`, `TipoEfetividadeRepository`), que gerenciam/usam `Database` e as entidades `Pokemon`, `Golpe`, `Nature` e `TipoEfetividade`.

**Classes entidade implementadas**

Entidade central, mapeada direto pra tabela pokemon. Status numéricos como campos primitivos (dado atômico = atributo, não classe); tipo2 é opcional.

**Pokemon.java (trecho)**
```java
@DatabaseTable(tableName = "pokemon")
public class Pokemon
{
    @DatabaseField(id = true) // id = numero da Pokedex (vem do CSV)
    private int id;

    @DatabaseField
    private int hp;
    // ... demais stats (ataque, defesa, spAtk, spDef, velocidade)

    @DatabaseField(columnName = "tipo_2", canBeNull = true) // pode ser nulo
    private String tipo2;

    public String getTiposFormatados() {
        return (tipo2 == null || tipo2.isEmpty()) ? tipo1 : tipo1 + " / " + tipo2;
    }
}
```

Golpe foi o primeiro caso que me fez abandonar a ideia de atributo simples: poder e precisao usam `Integer` (wrapper), não int primitivo, porque golpes de status não têm valor numérico e precisam aceitar null.

**Golpe.java (trecho)**
```java
@DatabaseTable(tableName = "golpe")
public class Golpe
{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true) // golpes de status nao tem poder
    private Integer poder;
    @DatabaseField(canBeNull = true)
    private Integer precisao;

    @DatabaseField
    private String categoria; // Physical / Special / Status
}
```

Nature segue a mesma lógica — statAumentada/statReduzida, HP nunca afetado — e TipoEfetividade guarda o multiplicador entre tipos, funcionando só como cadastro de consulta (usado no método fraquezasDe, Seção 3).

**Nature.java + TipoEfetividade.java (trechos)**
```java
@DatabaseTable(tableName = "nature")
public class Nature
{
    @DatabaseField(columnName = "stat_aumentada", canBeNull = true)
    private String statAumentada;
    @DatabaseField(columnName = "stat_reduzida", canBeNull = true)
    private String statReduzida;
}

@DatabaseTable(tableName = "tipo_efetividade")
public class TipoEfetividade
{
    @DatabaseField(columnName = "tipo_atacante")
    private String tipoAtacante;
    @DatabaseField(columnName = "tipo_defensor")
    private String tipoDefensor;
    @DatabaseField
    private double multiplicador; // 2.0 forte, 0.5 fraco, 0.0 imune
}
```

### 2.2 Etapa 2 — O que foi adicionado

Depois da primeira entrega, o Guilherme sugeriu montar e editar um time de Pokémon — fora do escopo original. Segui a mesma regra de arquitetura e criei duas classes novas: **Equipe** (o time em si) e **MembroTime** (a ligação entre uma Equipe e um Pokemon, carregando também a Nature e o Golpe escolhidos pra aquele Pokémon dentro daquele time). MembroTime não podia ser uma simples lista de Pokemon dentro de Equipe, porque cada posição carrega dados próprios que não pertencem nem a Equipe nem a Pokemon isoladamente.

**Equipe.java (trecho)**
```java
@DatabaseTable(tableName = "equipes")
public class Equipe implements Serializable {
    @DatabaseField(columnName = COL_ID, generatedId = true)
    private Integer id;
    @DatabaseField(columnName = COL_NOME, canBeNull = false)
    private String nome;
}
```

MembroTime é a classe mais rica em regra de negócio: equipe e pokemon são chaves estrangeiras com `foreignAutoRefresh = true` (o ORMLite carrega o objeto completo, não só o id). Os três construtores cobrem cada momento do ciclo de vida do objeto: montar na tela, salvar no banco, e carregar de volta.

**MembroTime.java (trecho)**
```java
@DatabaseField(columnName = COL_EQUIPE, foreign = true, foreignAutoRefresh = true, canBeNull = false)
private Equipe equipe;
@DatabaseField(columnName = COL_POKEMON, foreign = true, foreignAutoRefresh = true, canBeNull = false)
private Pokemon pokemon;

// Construtor pratico: montando o time na tela, ainda sem equipe salva
public MembroTime(Pokemon pokemon) {
    this.pokemon = pokemon;
    this.nature = "Nenhuma";
    this.golpe = pokemon.getGolpe();
}

// Construtor completo: usado ao persistir, ja vinculado a uma Equipe
public MembroTime(Equipe equipe, Pokemon pokemon, String nature, String golpe) {
    this.equipe = equipe; this.pokemon = pokemon; this.nature = nature; this.golpe = golpe;
}

// Construtor para carregar o time do banco de volta para a memoria RAM
public MembroTime(Pokemon pokemon, String nature, String golpe) {
    this.pokemon = pokemon; this.nature = nature; this.golpe = golpe;
}
```

Com Equipe e MembroTime incorporadas, refiz o **diagrama de classes final** (Figura 2), organizado em três camadas: entidades de domínio, persistência (um Repository por entidade) e aplicação (AppPokedex no topo, dependendo de todos os Repositories).

> **Figura 2** — Diagrama de classes final. Estrutura em três camadas: `AppPokedex` no topo, usando os cinco Repositories (`PokemonRepository`, `TipoEfetividadeRepository`, `GolpeRepository`, `MembroTimeRepository`, `NatureRepository`, `EquipeRepository`), que gerenciam `Database` e as entidades `Pokemon`, `Golpe`, `Nature`, `TipoEfetividade`, `MembroTime` e `Equipe`.

Em paralelo, após reunião com o professor, produzi também o **diagrama de regras de negócio** (Figura 3) — um diagrama de atividades, mostrando o fluxo de decisões em tempo de execução, e não mais só a estrutura estática das classes.

> **Figura 3** — Diagrama de regras de negócio (atividades).
>
> - **Montar time:** seleciona Pokémon → verifica se o time já tem 4 → se sim, bloqueia inserção (alerta de time cheio); se não, adiciona MembroTime (nature = Nenhuma) → verifica se a Nature tem stat alterada → se sim, aplica +10% na stat aumentada / -10% na reduzida (HP não muda); se não, stats permanecem na base → calcula fraquezas multiplicando o multiplicador de tipo_1 e tipo_2 → se resultado = 1.0, descartado (neutro); senão, exibido como fraqueza ou resistência.
> - **Excluir equipe:** excluir equipe → remove MembroTime vinculados → remove a Equipe.

- **Montar time** — verifica limite de 4 Pokémon; aplica +10%/-10% conforme a Nature (HP nunca muda); calcula fraquezas multiplicando tipo_1 e tipo_2, descartando resultado neutro (1.0).
- **Excluir equipe** — remove primeiro os MembroTime vinculados, só depois a Equipe, porque MembroTime tem uma chave estrangeira obrigatória pra Equipe.

### 2.3 Três commits mais relevantes (pasta UML_Classe)

Separando diagramas (documentação visual) de arquivos de classe (a decisão de modelagem virando código), estes três marcam os pontos de virada mais relevantes:

| # | Commit | Descrição |
|---|--------|-----------|
| 1 | `8982a66` — 10/06 <br> "Add files via upload" (UML.png) | Sobe o diagrama de classes protótipo — a primeira versão da arquitetura, ainda sem Equipe nem MembroTime. |
| 2 | `b1e8aed` — 30/06 <br> "Add MembroTime class for team member representation" | Cria o arquivo de classe MembroTime — a materialização em código da regra "struct → classe" pra funcionalidade de time. Os três construtores e as chaves estrangeiras nascem aqui. |
| 3 | `56ee8fb` <br> "Add UML code for business rules in Pokémon team management" | Adiciona o código-fonte PlantUML do diagrama de regras de negócio, sugerido pelo professor após a reunião de acompanhamento. |

### 2.4 Resposta ao feedback

O ponto de atenção que recebi ao longo do projeto foi ampliar a documentação visual da arquitetura além do diagrama de classes. Respondi a isso produzindo o diagrama de regras de negócio sugerido pelo professor (Seção 2.2, Figura 3), que documenta o fluxo de decisões do sistema — algo que o diagrama de classes, por ser estático, não cobre.

### 2.5 Principais dificuldades

- Perceber que a modelagem inicial (Golpe e Nature como atributos de Pokemon) não se sustentava, e reaprender em Java o que em C eu resolveria com struct.
- Decidir como representar a relação entre Equipe e Pokemon quando o escopo cresceu: MembroTime precisou carregar dados próprios (Nature, Golpe) que não cabiam nem em Equipe nem em Pokemon.
- Desenhar os três construtores de MembroTime de forma que cada fase do fluxo (montar, salvar, carregar) usasse só os dados que já tinha disponíveis, sem objetos incompletos preenchidos depois via setters.
- Traduzir em diagrama de atividades regras que já existiam no código, sem simplificar demais nem perder o comportamento real (limite de 4, ajuste de Nature, exclusão em cascata).

---

## 3. Contribuição além do atribuído

Além da minha atribuição estrita de arquitetura e modelagem de entidades, contribuí com a documentação do projeto: ajudei a documentar a lógica das classes e dos diagramas pra facilitar o entendimento de quem fosse ler o relatório ou revisar o código depois, reforçando a coerência entre o que foi desenhado nos diagramas UML e o que foi de fato implementado.

---

## 4. Considerações gerais

### 4.1 O que aprendi

- A diferença entre struct (C) e classe (Java), e como isso muda a forma de decidir o que vira atributo e o que vira entidade separada.
- ORMLite na prática: generatedId vs id vindo de fonte externa, canBeNull pra valores opcionais, e foreignAutoRefresh pra evitar consultas manuais extras.
- Diagrama de classes documenta estrutura estática; diagrama de atividades documenta comportamento em tempo de execução — os dois se complementam.
- Como manter uma arquitetura em camadas flexível o bastante pra incorporar novas entidades sem retrabalho nas já existentes.

### 4.2 Resultado alcançado

A arquitetura final ficou organizada em três camadas (entidades, persistência, aplicação), com seis entidades de domínio (Pokemon, Golpe, Nature, TipoEfetividade, Equipe, MembroTime) documentadas em dois diagramas de classes (protótipo e final) e um diagrama de regras de negócio. Cada decisão de modelagem tem uma contrapartida rastreável no código, verificada nos testes de integração da equipe com a interface JavaFX.

### 4.3 Conclusão

O processo de arquitetura do projeto Pokédex partiu de uma modelagem inicial simplificada, evoluiu aplicando consistentemente uma regra clara — dado com mais de um campo vira classe — e se consolidou em um diagrama final organizado em três camadas bem definidas. Considero a parte de arquitetura e modelagem concluída, documentada e coerente com o que foi efetivamente implementado.
