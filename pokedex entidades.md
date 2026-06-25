# Documentação das Entidades — Pokédex (g10)

Documentação das classes de modelo (camada `model`) do projeto. Todas são **POJOs planos com anotações ORMLite**, mapeando diretamente as colunas do banco SQLite. Nenhuma delas possui relação de objeto com as outras — cada uma vira uma tabela independente.

| Classe | Tabela | Tipo de `id` | Papel |
|--------|--------|--------------|-------|
| `Pokemon` | `pokemon` | id do CSV (manual) | Catálogo principal |
| `Nature` | `nature` | autoincremento | Naturezas |
| `Golpe` | `golpe` | autoincremento | Golpes/moves |
| `TipoEfetividade` | `tipo_efetividade` | autoincremento | Tabela de efetividade (type chart) |

---

## 1. `Pokemon`

**Tabela:** `pokemon`
**Anotação de classe:** `@DatabaseTable(tableName = "pokemon")`

Entidade principal. POJO plano, espelha direto as colunas do CSV. Tipos e golpe característico são guardados como `String`, **não** como entidades separadas.

### Atributos

| Campo | Tipo | Coluna no banco | Observações |
|-------|------|-----------------|-------------|
| `id` | `int` | `id` | `@DatabaseField(id = true)` — **chave manual**, é o número da Pokédex (vem do CSV) |
| `nome` | `String` | `nome` | — |
| `hp` | `int` | `hp` | — |
| `ataque` | `int` | `ataque` | — |
| `defesa` | `int` | `defesa` | — |
| `spAtk` | `int` | `sp_atk` | `columnName = "sp_atk"` |
| `spDef` | `int` | `sp_def` | `columnName = "sp_def"` |
| `velocidade` | `int` | `velocidade` | — |
| `spriteUrl` | `String` | `sprite_url` | `columnName = "sprite_url"` |
| `tipo1` | `String` | `tipo_1` | Tipo primário |
| `tipo2` | `String` | `tipo_2` | `canBeNull = true` — Pokémon de tipo único deixa nulo |
| `golpe` | `String` | `golpe` | Golpe característico, **só visual** |

### Construtor

- `Pokemon()` — construtor sem argumentos, **exigido pelo ORMLite** para instanciar via reflexão.

### Métodos

- **Getters/setters** de todos os campos (padrão JavaBean).
- `getTiposFormatados() : String` — atalho para a UI. Retorna `"Fire"` se só tem `tipo1`, ou `"Fire / Flying"` quando há `tipo2`. Trata `tipo2` nulo ou vazio.

### Regras de negócio

- O `id` **não é gerado** pelo banco: é o número oficial da Pokédex, importado do CSV.
- Um Pokémon tem **1 ou 2 tipos** (`tipo2` opcional).
- O campo `golpe` é meramente decorativo e **não tem relação** com a entidade `Golpe`. São coisas diferentes apesar do nome parecido.

---

## 2. `Nature`

**Tabela:** `nature`
**Anotação de classe:** `@DatabaseTable(tableName = "nature")`

Representa as naturezas, que modificam os atributos de um Pokémon (sobem um stat, baixam outro).

### Atributos

| Campo | Tipo | Coluna no banco | Observações |
|-------|------|-----------------|-------------|
| `id` | `int` | `id` | `@DatabaseField(generatedId = true)` — autoincremento |
| `nome` | `String` | `nome` | — |
| `statAumentada` | `String` | `stat_aumentada` | `canBeNull = true` — nome do atributo que sobe |
| `statReduzida` | `String` | `stat_reduzida` | `canBeNull = true` — nome do atributo que desce |

### Construtor

- `Nature()` — construtor vazio exigido pelo ORMLite.

### Métodos

- Apenas getters/setters de todos os campos.

### Regras de negócio

- Cada natureza normalmente **aumenta um stat e reduz outro**.
- Naturezas **neutras** (que não alteram nada) ficam com `statAumentada` e `statReduzida` nulos.
- Atualmente é uma tabela **independente**: nenhum Pokémon referencia uma `Nature` no código.

---

## 3. `Golpe`

**Tabela:** `golpe`
**Anotação de classe:** `@DatabaseTable(tableName = "golpe")`

Representa os golpes/moves. Entidade própria, separada do campo `golpe` (String) que existe em `Pokemon`.

### Atributos

| Campo | Tipo | Coluna no banco | Observações |
|-------|------|-----------------|-------------|
| `id` | `int` | `id` | `generatedId = true` — autoincremento |
| `nome` | `String` | `nome` | — |
| `tipo` | `String` | `tipo` | Tipo do golpe (Fire, Water, etc.) |
| `poder` | `Integer` | `poder` | `canBeNull = true` — objeto, não `int`, justamente pra aceitar nulo |
| `precisao` | `Integer` | `precisao` | `canBeNull = true` — idem |
| `categoria` | `String` | `categoria` | Físico / Especial / Status |

### Construtor

- `Golpe()` — construtor vazio exigido pelo ORMLite.

### Métodos

- Apenas getters/setters de todos os campos.

### Regras de negócio

- `poder` e `precisao` são `Integer` (e não `int`) de propósito: **golpes de status não têm poder/precisão** e ficam nulos.
- `categoria` separa golpes Físicos, Especiais e de Status.
- Tabela **independente**: nenhum Pokémon referencia um `Golpe` no código atual.

---

## 4. `TipoEfetividade`

**Tabela:** `tipo_efetividade`
**Anotação de classe:** `@DatabaseTable(tableName = "tipo_efetividade")`

Tabela de efetividade entre tipos (a *type chart*). Guarda o multiplicador de dano de um tipo atacante contra um tipo defensor.

### Atributos

| Campo | Tipo | Coluna no banco | Observações |
|-------|------|-----------------|-------------|
| `id` | `int` | `id` | `generatedId = true` — autoincremento |
| `tipoAtacante` | `String` | `tipo_atacante` | `columnName = "tipo_atacante"` |
| `tipoDefensor` | `String` | `tipo_defensor` | `columnName = "tipo_defensor"` |
| `multiplicador` | `double` | `multiplicador` | 2.0 forte, 0.5 fraco, 0.0 imune |

### Construtores

- `TipoEfetividade()` — construtor vazio exigido pelo ORMLite.
- `TipoEfetividade(String tipoAtacante, String tipoDefensor, double multiplicador)` — construtor com argumentos, usado para **popular a tabela** (seed inicial da type chart).

### Métodos

- Apenas getters/setters de todos os campos.

### Regras de negócio

- A tabela guarda **apenas os confrontos diferentes de 1.0**. Os multiplicadores possíveis são:
  - `2.0` → super efetivo (forte)
  - `0.5` → pouco efetivo (fraco)
  - `0.0` → imune (não causa dano)
- Qualquer par `(atacante, defensor)` **ausente** na tabela é tratado como **neutro (1.0)** implicitamente. Isso evita guardar centenas de linhas redundantes.

---

## Observações gerais

- **Padrão arquitetural:** todas as entidades são planas, sem associações de objeto entre si. Tipos e golpe característico são `String`, não classes. O modelo é "tabela espelhando dado", não orientado a objeto com relações.
- **Chave primária:** só `Pokemon` usa id manual (do CSV); as demais usam autoincremento.
- **Nomenclatura:** o código usa `Golpe`/`Repository`, mas o documento do grupo usa `Move`/`DAO`. Vale alinhar um padrão único antes de fechar a entrega.
