# Relatório Individual — Modelagem UML do Projeto Pokédex (g10)

**Gabriel — Arquitetura, modelagem de classes e implementação das entidades**

---

## 1. Objetivo

Este relatório documenta o processo completo de modelagem e implementação da camada de domínio do projeto Pokédex, desenvolvido em Java com JavaFX (MVC) e persistência via ORMLite/SQLite. Cobre a minha atuação como responsável pela arquitetura do sistema: o raciocínio por trás da definição de quantas classes seriam necessárias e por que cada uma existe, a evolução do diagrama de classes desde um protótipo inicial até a versão final, e a criação do diagrama de regras de negócio sugerido pelo professor. Nesta versão, o relatório também traz trechos reais do código-fonte para embasar tecnicamente cada explicação.

## 2. Minha atuação no projeto: arquitetura

Minha responsabilidade no grupo foi pensar a arquitetura do sistema — decidir o "modelo de negócio" em termos de orientação a objetos: quantas classes o sistema precisaria, quais responsabilidades cada uma teria, e principalmente **por que** cada classe deveria existir como classe separada, e não como um simples atributo de outra.

O primeiro ponto de decisão foi sobre **Golpe** e **Nature**. Minha ideia inicial era que ambos ficassem como atributos simples dentro da classe **Pokemon**. Ao tentar aplicar essa lógica, ela não se sustentou: um golpe não é um dado atômico, carrega vários atributos próprios (nome, tipo, poder, precisão, categoria), e o mesmo vale para a Nature.

Como eu vinha de uma base forte em linguagem C, meu primeiro instinto foi pensar nisso como um *struct*. O problema é que Java não tem struct; a forma equivalente e correta dentro do paradigma orientado a objetos é criar uma **classe**. A regra que usei para o resto do projeto foi: **atributo simples fica dentro da classe; qualquer conceito com mais de um dado próprio vira uma classe separada.** Foi assim que cheguei às quatro entidades centrais: Pokemon, Golpe, Nature e TipoEfetividade.

## 3. A primeira versão do diagrama de classes (protótipo)

A primeira versão do diagrama de classes é um **protótipo**, feito na fase inicial do projeto para ter uma noção geral da arquitetura — não a versão final implementada. Nesse momento eu ainda não tinha pensado na funcionalidade de montar e editar um time de Pokémon, então esse diagrama **não contém Equipe nem MembroTime**.

> **Figura 1** — Diagrama de classes protótipo (versão inicial, sem Equipe/MembroTime).
> Estrutura: `PopularBanco` → `PokemonRepository`, `GolpeRepository`, `NatureRepository`, `TipoEfetividadeRepository` → `Database`; entidades `Pokemon`, `Golpe`, `Nature`, `TipoEfetividade`.

## 4. Implementação das classes entidade

No primeiro documento entregue ao professor, minha responsabilidade era a codificação de todas as classes, com exceção da AppPokedex. Na prática, o que fiz efetivamente foi a implementação das **classes entidade**. As classes Repository são mais ligadas ao banco de dados, tarefa do Luís; eu contribuí pouco nelas — mais explicando a lógica de domínio do que escrevendo persistência em si (ver Seção 7).

### 4.1 Pokemon

Entidade central do sistema, mapeada com ORMLite direto para a tabela `pokemon`. Os status numéricos ficam como campos primitivos (dado atômico = atributo, não classe). `tipo2` é opcional, e `getTiposFormatados()` concentra a lógica de exibição, evitando reimplementá-la na tela.

```java
// Pokemon.java (trecho)
@DatabaseTable(tableName = "pokemon")
public class Pokemon
{
    @DatabaseField(id = true)                  // id = numero da Pokedex (vem do CSV)
    private int id;

    @DatabaseField
    private int hp;
    @DatabaseField
    private int ataque;
    // ... demais stats (defesa, spAtk, spDef, velocidade)

    @DatabaseField(columnName = "tipo_2", canBeNull = true)   // pode ser nulo
    private String tipo2;

    /** Atalho pra UI: "Fire" ou "Fire / Flying". */
    public String getTiposFormatados() {
        return (tipo2 == null || tipo2.isEmpty()) ? tipo1 : tipo1 + " / " + tipo2;
    }
}
```

### 4.2 Golpe

Primeiro caso que me fez abandonar a ideia de atributo simples. `poder` e `precisao` são declarados como **Integer** (wrapper), e não `int` primitivo, de propósito: golpes de status não têm poder nem precisão numérica, e um `int` primitivo não aceita `null`.

```java
// Golpe.java (trecho)
@DatabaseTable(tableName = "golpe")
public class Golpe
{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true)        // golpes de status nao tem poder
    private Integer poder;

    @DatabaseField(canBeNull = true)
    private Integer precisao;

    @DatabaseField
    private String categoria;                // Physical / Special / Status
}
```

### 4.3 Nature

Mesma lógica de Golpe: uma Nature altera uma stat para cima e outra para baixo, e HP nunca é afetado. Naturezas neutras existem com `statAumentada`/`statReduzida` nulos.

```java
// Nature.java (trecho)
@DatabaseTable(tableName = "nature")
public class Nature
{
    @DatabaseField(columnName = "stat_aumentada", canBeNull = true)
    private String statAumentada;

    @DatabaseField(columnName = "stat_reduzida", canBeNull = true)
    private String statReduzida;
}
```

### 4.4 TipoEfetividade

Representa a tabela de efetividade entre tipos (type chart). Como o projeto não implementa um sistema de batalha, funciona apenas como cadastro de consulta — quem efetivamente usa esses dados é o método `fraquezasDe()` do `PokemonRepository`, detalhado na Seção 7.1.

```java
// TipoEfetividade.java (trecho)
@DatabaseTable(tableName = "tipo_efetividade")
public class TipoEfetividade
{
    @DatabaseField(columnName = "tipo_atacante")
    private String tipoAtacante;
    @DatabaseField(columnName = "tipo_defensor")
    private String tipoDefensor;
    @DatabaseField
    private double multiplicador;   // 2.0 forte, 0.5 fraco, 0.0 imune
}
```

## 5. Evolução do escopo: edição de time

Depois da primeira entrega, o Guilherme sugeriu que o sistema também permitisse montar e editar um time de Pokémon — funcionalidade fora do escopo original. Isso exigiu repensar a arquitetura: como representar um time, e como representar a relação entre um time e os Pokémon que o compõem.

A resposta, seguindo a regra da Seção 2, foi criar **Equipe** (o time em si) e **MembroTime** (a ligação entre uma Equipe e um Pokemon, carregando também a Nature e o Golpe escolhidos para aquele Pokémon dentro daquele time). MembroTime não podia ser uma simples lista de Pokemon dentro de Equipe, porque cada posição no time carrega dados próprios que não pertencem nem a Equipe nem a Pokemon isoladamente.

Em paralelo, após reunião com o professor, foi sugerido produzir um **diagrama de regras de negócio** (diagrama de atividades), documentando o fluxo de decisões do sistema.

## 6. As novas classes: Equipe e MembroTime

### 6.1 Equipe

Entidade simples: `id` (gerado pelo ORMLite) e `nome` (obrigatório). Implementa `Serializable` porque instâncias circulam entre telas da UI antes de serem persistidas.

```java
// Equipe.java (trecho)
@DatabaseTable(tableName = "equipes")
public class Equipe implements Serializable {

    @DatabaseField(columnName = COL_ID, generatedId = true)
    private Integer id;

    @DatabaseField(columnName = COL_NOME, canBeNull = false)
    private String nome;

    public Equipe(String nome) {
        this.nome = nome;
    }
}
```

### 6.2 MembroTime

Classe mais rica em regra de negócio das entidades. `equipe` e `pokemon` são chaves estrangeiras (`foreign = true`) com `foreignAutoRefresh = true`, o que faz o ORMLite carregar automaticamente o objeto completo ao ler um MembroTime do banco — importante para a UI exibir nome/sprite sem uma consulta extra manual.

O ponto mais interessante são os **três construtores**, cada um pensado para um momento diferente do ciclo de vida do objeto:

```java
// MembroTime.java (trecho)
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
    this.equipe = equipe;
    this.pokemon = pokemon;
    this.nature = nature;
    this.golpe = golpe;
}

// Construtor para carregar o time do banco de volta para a memoria RAM
public MembroTime(Pokemon pokemon, String nature, String golpe) {
    this.pokemon = pokemon;
    this.nature = nature;
    this.golpe = golpe;
}
```

Ter três construtores evita que a tela de montagem de time precise construir um objeto "incompleto" e preenchê-lo depois via setters em vários pontos — cada fase do fluxo (montar, salvar, carregar) usa o construtor que já corresponde aos dados disponíveis naquele momento.

## 7. Camada de persistência e aplicação (Repository / AppPokedex)

Embora a codificação dessa camada tenha sido majoritariamente do Luís (persistência) e a AppPokedex de outro colega, participei da definição da lógica de negócio que os métodos precisavam expor. Vale detalhar os pontos onde a arquitetura pensada por mim (Seções 2, 5 e 6) aparece de fato implementada no código.

### 7.1 PokemonRepository.fraquezasDe() — cruzando Pokemon com TipoEfetividade

Este método é o único ponto do sistema onde TipoEfetividade é efetivamente consultada. Ele percorre toda a tabela de efetividade, verifica se o tipo defensor bate com tipo1 ou tipo2 do Pokémon, e **multiplica** os multiplicadores quando o mesmo tipo atacante afeta os dois tipos do Pokémon (dual-type) — exatamente a regra descrita no diagrama de atividades (Seção 9.1).

```java
// PokemonRepository.java — fraquezasDe()
public Map<String, Double> fraquezasDe(Pokemon p) {
    Map<String, Double> mult = new LinkedHashMap<String, Double>();
    List<TipoEfetividade> chart = new TipoEfetividadeRepository(database).loadAll();

    for (TipoEfetividade te : chart) {
        boolean atingeTipo1 = te.getTipoDefensor().equals(p.getTipo1());
        boolean atingeTipo2 = p.getTipo2() != null
                              && !p.getTipo2().isEmpty()
                              && te.getTipoDefensor().equals(p.getTipo2());
        if (atingeTipo1 || atingeTipo2) {
            String atk = te.getTipoAtacante();
            double atual = mult.containsKey(atk) ? mult.get(atk) : 1.0;
            mult.put(atk, atual * te.getMultiplicador());   // multiplica tipo_1 x tipo_2
        }
    }
    // remove os neutros (1.0) e ordena do mais perigoso pro menos
    Map<String, Double> ordenado = new LinkedHashMap<String, Double>();
    mult.entrySet().stream()
        .filter(e -> e.getValue() != 1.0)
        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
        .forEach(e -> ordenado.put(e.getKey(), e.getValue()));
    return ordenado;
}
```

O filtro `e.getValue() != 1.0` é a implementação direta da decisão "Resultado = 1.0? → Descartado, neutro" do diagrama de regras de negócio: resultado neutro não é nem fraqueza nem resistência, então é removido do mapa antes de chegar na tela.

### 7.2 EquipeRepository e MembroTimeRepository

Seguem o mesmo molde dos demais Repositories (create/loadAll usando um Dao do ORMLite), mas com dois acréscimos que só fazem sentido pela relação Equipe ↔ MembroTime definida na Seção 6: **update** (para renomear uma equipe já salva) e, principalmente, **loadByEquipe**, que filtra os membros de uma equipe específica usando a chave estrangeira mapeada em MembroTime.

```java
// MembroTimeRepository.java — loadByEquipe()
public List<MembroTime> loadByEquipe(Equipe equipe) {
    try {
        QueryBuilder<MembroTime, Integer> qb = dao.queryBuilder();
        qb.where().eq(MembroTime.COL_EQUIPE, equipe);
        return qb.query();
    } catch (SQLException e) {
        System.out.println(e);
        return new ArrayList<MembroTime>();
    }
}
```

### 7.3 Database — conexão única compartilhada

Todos os Repositories recebem a mesma instância de Database e a usam só para obter a conexão JDBC (`getConnection()`); a conexão real é aberta uma única vez (lazy init) e reaproveitada — é a implementação da dependência "usa" descrita no diagrama de classes.

```java
// Database.java — getConnection() (trecho)
public JdbcConnectionSource getConnection() throws SQLException {
   if (connection == null) {
       connection = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);
   }
   return connection;
}
```

### 7.4 AppPokedex.excluirEquipe() — a regra de negócio na prática

Este método é a implementação exata do fluxo "Excluir equipe" do diagrama de atividades (Seção 9.2): primeiro remove todos os MembroTime vinculados, só depois remove a Equipe.

```java
// AppPokedex.java — excluirEquipe()
private void excluirEquipe(Equipe eq) {
    // Primeiro exclui os membros para nao deixar dados "orfaos" no banco
    List<MembroTime> membros = membroRepo.loadByEquipe(eq);
    for (MembroTime mt : membros) {
        membroRepo.delete(mt);
    }
    // Depois exclui a equipe
    equipeRepo.delete(eq);
    mostrarTimesSalvos();
}
```

### 7.5 AppPokedex — aplicação da Nature nas stats (+10% / -10%)

O trecho abaixo é a implementação exata da regra "Nature tem stat alterada? → +10% na aumentada / -10% na reduzida (HP não muda)" do diagrama de atividades. Repare que o HP simplesmente não passa por essa função — é desenhado com `criarBarraStatus()` normal, nunca com a versão que aplica Nature.

```java
// AppPokedex.java — criarBarraStatusNature() (trecho)
if (nomeStatBanco.equals(nature.getStatAumentada())) {
    valorFinal = (int) (valorBase * 1.1); // +10%
} else if (nomeStatBanco.equals(nature.getStatReduzida())) {
    valorFinal = (int) (valorBase * 0.9); // -10%
}
```

### 7.6 AppPokedex.adicionarAoTime() — o limite de 4 Pokémon

Implementação direta do primeiro desvio do diagrama de atividades ("Time já tem 4?"). `MAX_TIME` é uma constante da própria AppPokedex, e o construtor de um argumento de MembroTime (Seção 6.2) é usado exatamente aqui, no momento de montagem em memória.

```java
// AppPokedex.java — adicionarAoTime()
private void adicionarAoTime(Pokemon p) {
    if (time.size() >= MAX_TIME) {
        alerta("O time ja esta cheio (maximo de " + MAX_TIME + " Pokemon).");
        return;
    }
    time.add(new MembroTime(p));
    alerta(p.getNome() + " foi adicionado ao time!");
}
```

## 8. Diagrama de classes final

Com Equipe e MembroTime incorporadas, o diagrama de classes foi refeito para refletir a arquitetura completa e final do sistema.

> **Figura 2** — Diagrama de classes final.

A leitura do diagrama segue três camadas, na seguinte sequência lógica:

- **Entidades de domínio** — Pokemon, Golpe, Nature, TipoEfetividade, Equipe e MembroTime. MembroTime é o ponto de junção, com associação "0..\* → 1" tanto para Equipe quanto para Pokemon.
- **Persistência (Repository)** — um Repository por entidade, cada um usando Database e gerenciando sua entidade correspondente, conforme detalhado na Seção 7.
- **Aplicação** — AppPokedex, no topo, depende de todos os Repositories para orquestrar a interface (Seção 7.4 a 7.6 mostram esse uso na prática).

Essa organização em camadas é o que permitiu adicionar Equipe e MembroTime sem alterar nada nas entidades já existentes: a nova funcionalidade se conecta ao restante do sistema apenas por referências e por um novo par de Repository.

## 9. Diagrama de regras de negócio

Produzido a partir da sugestão do professor. Diferente do diagrama de classes (estrutura estática), este é um **diagrama de atividades**, mostrando o fluxo de decisões em tempo de execução — e cada decisão tem um trecho de código correspondente na Seção 7.

> **Figura 3** — Diagrama de regras de negócio (atividades).

### 9.1 Montar time

- Seleciona um Pokémon → verifica limite de 4 (código: Seção 7.6, `adicionarAoTime`).
- Cria MembroTime com `nature = "Nenhuma"` por padrão (código: Seção 6.2, construtor de 1 argumento).
- Aplica +10%/-10% conforme a Nature, HP nunca muda (código: Seção 7.5, `criarBarraStatusNature`).
- Calcula fraquezas multiplicando tipo_1 e tipo_2, descarta resultado neutro = 1.0 (código: Seção 7.1, `fraquezasDe`).

### 9.2 Excluir equipe

- Remove primeiro todos os MembroTime vinculados, depois remove a Equipe (código: Seção 7.4, `excluirEquipe`). Essa ordem existe porque MembroTime tem uma chave estrangeira obrigatória (`canBeNull = false`) para Equipe — invertê-la deixaria membros órfãos ou quebraria a integridade referencial do banco.

## 10. Os 3 commits mais importantes (pasta `UML_Classe`)

Olhando o histórico específico da pasta `UML_Classe`, separei diagramas (documentação visual) de arquivos de classe (a própria decisão de modelagem virando código). Os três abaixo marcam os pontos de virada mais relevantes:

| # | Commit | Descrição |
|---|---|---|
| 1 | [`8982a66`](https://github.com/poo-ec-2026-1/g10/commit/8982a66dc780d07b151227309619a61b6d621a57) — 10/06<br>"Add files via upload" (`UML.png`) | Sobe o **diagrama de classes protótipo** (Figura 1, Seção 3) — a primeira versão da arquitetura, ainda sem Equipe nem MembroTime, feita só pra ter uma noção geral do sistema antes da funcionalidade de time existir. |
| 2 | [`b1e8aed`](https://github.com/poo-ec-2026-1/g10/commit/b1e8aed8871a124abbd6ed8a678075e0d55a93c6) — 30/06<br>"Add MembroTime class for team member representation" | Cria o arquivo de classe **MembroTime** — a materialização em código da regra "struct → classe" (Seção 2) pra funcionalidade de time. Mais que um diagrama, é a própria decisão de modelagem virando classe: os três construtores e as chaves estrangeiras (Seção 6.2) nascem aqui. |
| 3 | [`56ee8fb`](https://github.com/poo-ec-2026-1/g10/commit/56ee8fb339d0c56bd0f6cc85ef0ef77a5ae84a63)<br>"Add UML code for business rules in Pokémon team management" | Adiciona o código-fonte PlantUML do **diagrama de regras de negócio** (Figura 3, Seção 9), sugerido pelo professor após a reunião de acompanhamento — formaliza em diagrama de atividades as regras que já existiam no código (limite de 4 no time, ajuste de Nature, exclusão em cascata de MembroTime antes da Equipe). |

*Observação: o commit [`f59fa4b`](https://github.com/poo-ec-2026-1/g10/commit/f59fa4b46b6fa3d9909b8d2a5247628e5065a582) ("Create Equipe class for ORM mapping"), do mesmo dia, é o equivalente pra Equipe e teria peso parecido — MembroTime entrou na lista acima por concentrar mais regra de negócio (Seção 6.2).*

## 11. Conclusão

O processo de arquitetura do projeto Pokédex partiu de uma modelagem inicial simplificada, evoluiu aplicando consistentemente uma regra clara — dado com mais de um campo vira classe — e se consolidou em um diagrama final organizado em três camadas bem definidas. Os trechos de código apresentados neste relatório mostram que cada decisão de modelagem (Seções 2, 5 e 6) tem uma contrapartida direta e rastreável na implementação (Seção 7) e no diagrama de regras de negócio (Seção 9), demonstrando coerência entre o que foi desenhado e o que foi efetivamente codificado.
