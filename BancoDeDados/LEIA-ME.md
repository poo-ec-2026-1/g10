# Pokédex — Banco de Dados (ORMLite + SQLite, no BlueJ)

Esta é a parte do **banco de dados**, feita no padrão do tutorial do professor:
classe `Database`, classes **entidade** (com anotações ORMLite) e classes
**Repository** (uma por entidade). Tudo no **pacote padrão** (sem `package`),
igual ao tutorial.

## 1. Bibliotecas (adicionar no BlueJ)

Baixe os 5 `.jar` (os mesmos do tutorial) e adicione em
**BlueJ → Preferências → Bibliotecas → Adicionar**, depois reinicie o BlueJ:

- sqlite-jdbc 3.46.0.0
- ormlite-core 6.1
- ormlite-jdbc 6.1
- slf4j-api 2.0.13
- slf4j-simple 2.0.13

(São os links que estão no README do professor.)

## 2. Arquivos do projeto

Classe de conexão:
- `Database.java` — gerencia a conexão com o SQLite (igual ao tutorial).

Entidades (POJO + anotações `@DatabaseTable` / `@DatabaseField`):
- `Pokemon.java` — id, nome, stats, sprite_url, tipo_1, tipo_2, golpe.
- `Golpe.java` — catálogo de golpes (nome, tipo, poder, precisão, categoria).
- `Nature.java` — as 25 natures.
- `TipoEfetividade.java` — a tabela de fraquezas (TypeChart).

Repositórios (padrão Repository, igual ao `StudentRepository`):
- `PokemonRepository.java` — `create`, `loadFromId`, `loadAll`,
  `loadByName` (busca), `loadByType` (filtro) e `fraquezasDe` (dashboard).
- `GolpeRepository.java`, `NatureRepository.java`, `TipoEfetividadeRepository.java`.

Carga de dados:
- `PopularBanco.java` — cria o `pokedex.db` e popula tudo a partir dos CSVs.

Dados (devem ficar na pasta do projeto):
- `pokemons.csv`, `golpes.csv`, `pokemon_golpes.csv`.

## 3. Como gerar o banco

1. Confirme que os 3 `.csv` estão na **pasta do projeto** (mesma pasta dos `.java`).
2. No BlueJ, clique com o botão direito em **`PopularBanco`** e escolha
   **`void main(String[] args)`**.
3. Ele cria o arquivo `pokedex.db` na pasta do projeto, já com os 151 Pokémon,
   os golpes, as 25 natures e os 120 confrontos de tipo. (Pode levar alguns
   segundos.) Pode rodar de novo quando quiser — ele recria do zero, sem duplicar.

Para olhar os dados em tabela, use o **DB Browser for SQLite** e abra o `pokedex.db`.

## 4. Para o pessoal da interface

Não precisam escrever SQL. Criem o `Database` uma vez e usem os repositórios:

```java
Database db = new Database("pokedex.db");
PokemonRepository repo = new PokemonRepository(db);

for (Pokemon p : repo.loadAll()) {
    // p.getNome(), p.getSpriteUrl(), p.getTiposFormatados(), p.getGolpe()
}

repo.loadByName("pika");   // barra de pesquisa
repo.loadByType("Fire");   // filtro por tipo
Pokemon charizard = repo.loadFromId(6);          // card ampliado
repo.fraquezasDe(charizard);  // {Rock=4.0, Water=2.0, Ground=0.0, ...} -> dashboard
```

## 5. Observações

- A `Pokemon` usa `@DatabaseField(id = true)` (o id é o número fixo da Pokédex,
  vindo do CSV). As outras entidades usam `generatedId = true`, como a `Student`
  do tutorial.
- Os golpes são **só visuais** (1 por Pokémon, sem sistema de combate).
- Se o projeto integrado usar `package`, é só adicionar a linha `package ...;`
  no topo de cada arquivo.
