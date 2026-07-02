# Pokédex — Banco de Dados (ORMLite + SQLite, no BlueJ)
Aluno: Luis Leão

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

---

# Relatório Técnico: Implementação e Manuseio da Classe `AppPokedex`
Aluno: Guilherme Filho 

Este relatório apresenta uma análise detalhada da arquitetura, funcionamento interno e guias de manuseio da classe `AppPokedex.java`.

---

## 1. Visão Geral e Arquitetura

A classe `AppPokedex` estende `javafx.application.Application` e serve como o ponto de entrada e o controlador principal da interface visual. Seguindo as boas práticas de desenvolvimento de software e os princípios de **Separação de Conceitos (Separation of Concerns)**, a interface gráfica **não manipula ou escreve instruções SQL diretamente**. 

### Padrão de Projeto: Repository Pattern
Toda a comunicação com a camada de persistência (banco de dados SQLite local `pokedex.db`) é intermediada por repositórios especializados baseados no framework **ORMLite**. A classe coordena os seguintes repositórios:
* **`PokemonRepository`**: Gerencia a busca, filtragem e cálculo de efetividades (fraquezas e vantagens) dos Pokémon.
* **`GolpeRepository`**: Carrega os movimentos/ataques disponíveis no banco de dados.
* **`NatureRepository`**: Recupera as Natures que modificam os atributos dos Pokémon.
* **`EquipeRepository`**: Responsável pela criação, atualização, listagem e exclusão das equipes salvas.
* **`MembroTimeRepository`**: Gerencia a relação de cardinalidade dos Pokémon pertencentes a uma equipe específica no banco de dados.

### Gerenciamento de Estado em Memória
Para proporcionar uma experiência fluida, o sistema utiliza uma lista dinâmica em memória RAM (`private final List<MembroTime> time`) para gerenciar a equipe atual selecionada pelo usuário antes de efetuar a persistência definitiva no banco de dados. O limite máximo de componentes da equipe foi definido em **4 Pokémon** (`MAX_TIME = 4`).

---

## 2. Funcionamento Interno e Telas

A interface utiliza um contêiner do tipo `BorderPane` como nó raiz (`root`). A navegação entre as telas ocorre de forma dinâmica através da substituição do conteúdo das regiões `Top` e `Center` do `BorderPane`, evitando a abertura de múltiplas janelas e mantendo a aplicação em um fluxo único e coeso.

### Tela 1: Catálogo Pokédex (Tela Principal)
* **Estrutura**: Composta por uma barra superior (`HBox`) que contém um campo de busca textual (`TextField`), filtros de tipo (`ComboBox`), e botões de navegação rápida para o Time Atual e os Times Salvos. O centro consiste em um `ScrollPane` que abriga um `FlowPane` (grade adaptável de cards).
* **Mecanismo de Busca**: Ao digitar o nome e clicar em "Pesquisar" (ou pressionar Enter), o repositório é acionado via `pokemonRepo.loadByName(termo)`. Se o campo estiver vazio, carrega todos via `loadAll()`.
* **Mecanismo de Filtro**: A seleção de um tipo no combo-box dispara uma consulta filtrada por meio do `pokemonRepo.loadByType(tipo)`.

### Tela 2: Card Ampliado (Detalhes do Pokémon)
* **Cálculo de Efetividade**: Esta tela exibe as fraquezas e resistências do Pokémon através do método `pokemonRepo.fraquezasDe(p)`. O sistema mapeia os modificadores elementares: multiplicadores maiores que `1.0` são renderizados sob a seção de "Fraquezas", enquanto valores menores ou iguais a `1.0` vão para "Vantagens (resistências)".
* **Visualização de Atributos**: Os valores de base (HP, ATK, DEF, Sp.Atk, Sp.Def, SPEED) são representados visualmente por componentes `ProgressBar`. A cor da barra é alterada dinamicamente via CSS inline (`-fx-accent`) dependendo do valor do atributo:
  * 🔴 **Vermelho** (`#ff4c4c`): Menor que 50 (Atributo Crítico/Baixo).
  * 🟠 **Laranja** (`#ffad33`): Entre 50 e 89 (Atributo Moderado).
  * 🟡 **Amarelo** (`#f2d94e`): Entre 90 e 119 (Atributo Bom).
  * 🟢 **Verde** (`#4caf50`): Igual ou maior que 120 (Atributo Excelente).

### Tela 3: Gerenciamento do Time Atual
* **Customização do Membro**: Permite visualizar os Pokémon atualmente adicionados à memória de rascunho. Cada linha oferece controles individuais para alterar a *Nature* e o *Ataque* do Pokémon utilizando caixas de diálogo de escolha única (`ChoiceDialog`).
* **Matemática de Modificadores de Nature**: Ao abrir o card detalhado a partir do time (`mostrarCardTime`), a classe executa uma lógica matemática precisa refletindo as regras oficiais da franquia Pokémon. Se a *Nature* do Pokémon possuir um atributo aumentado ou reduzido cadastrado no banco, o sistema aplica:
  * **+10%** no atributo aumentado, adicionando visualmente um sufixo `(↑)` em verde escuro (`#2e7d32`).
  * **-10%** no atributo reduzido, adicionando visualmente um sufixo `(↓)` em vermelho escuro (`#d32f2f`).
  * O HP permanece inalterado por regras de design do jogo.

### Tela 4: Times Salvos (Histórico e Persistência)
* **Prevenção de Dados Órfãos (Integridade Referencial)**: Ao excluir uma equipe (`excluirEquipe`), a classe executa uma exclusão em cascata controlada via software. Primeiro, ela remove todos os registros vinculados em `membroRepo.delete(mt)` e, subsequentemente, remove a equipe principal em `equipeRepo.delete(eq)`, garantindo a higienização perfeita do banco SQLite.
* **Operação de Carregamento**: Limpa o time em memória (`time.clear()`) e popula-o com os dados históricos recuperados da equipe selecionada, redirecionando o usuário instantaneamente para a tela do time para visualização imediata.

---

## 3. Guia de Manuseio (Manual do Usuário)

Para interagir perfeitamente com a aplicação, siga o passo a passo operacional abaixo:

### Passo 1: Explorar e Pesquisar Pokémon
1. Na tela inicial, utilize a barra de pesquisa digitando o nome do Pokémon desejado e clique em **"Pesquisar"** ou aperte **"Enter"**.
2. Para listar Pokémon por afinidade elemental, selecione uma opção no menu suspenso (Ex: *Fire*, *Water*, *Electric*). Para retornar à exibição completa, selecione **"Todos"**.

### Passo 2: Montar a sua Equipe
1. Ao encontrar um Pokémon do seu interesse na grade, clique sobre o card dele.
2. A tela detalhada será aberta exibindo suas estatísticas e resistências. Clique no botão **"Adicionar ao time"**.
3. O sistema exibirá uma notificação confirmando a inserção. Lembre-se: o limite máximo por equipe é de **4 Pokémon**. Caso tente adicionar um quinto, um alerta de impedimento será exibido.

### Passo 3: Customizar Atributos e Golpes do Time
1. Na barra superior da Pokédex, clique em **"Inspecionar Time"**.
2. Na linha correspondente ao Pokémon que deseja modificar:
   * Clique em **"Alt. Nature"** para selecionar uma nova Natureza e alterar o balanço de atributos (+10% / -10%).
   * Clique em **"Alt. Ataque"** para definir o movimento principal deste membro.
   * Clique em **"Ver Detalhes"** para conferir o impacto matemático real que a Nature aplicada causou nos atributos finais (representados pelas setas de aumento `↑` ou redução `↓`).
   * Clique em **"Retirar"** se desejar remover o Pokémon da equipe atual.

### Passo 4: Salvar e Gerenciar Múltiplos Times
1. Na tela do seu time, clique no botão **"Salvar Time no Banco"**.
2. Uma caixa de diálogo solicitará um nome identificador para a equipe (Ex: `"Meu Time de Elite"`). Digite o nome e confirme.
3. Para consultar seus times salvos a qualquer momento, retorne à Pokédex e clique em **"Times Salvos"**.
4. Na tela de listagem de históricos, você poderá:
   * **Carregar Time**: Substitui a equipe ativa pela salva no banco de dados.
   * **Renomear**: Altera o nome da equipe diretamente no banco sem desestruturar seus membros.
   * **Excluir**: Remove permanentemente a equipe e seus vínculos do banco de dados de maneira segura.

---

## 4. Conclusão e Alinhamento com o Planejamento

A implementação contida em `AppPokedex.java` cumpre com excelência as metas estipuladas no `planejamento.md` do grupo. Ela demonstra a viabilidade prática da integração de interfaces ricas com bancos de dados relacionais por meio do encapsulamento de consultas cruas em estruturas puramente orientadas a objetos. A separação clara de responsabilidades confere ao software alta manutenibilidade, escalabilidade para novas funcionalidades (como expansão para mais membros ou novos filtros) e excelente estabilidade de execução.
