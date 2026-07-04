# Relatório Técnico Complementar — Demonstração em Vídeo da Interface (AppPokedex)

**LINK DO VIDEO:** https://drive.google.com/file/d/1tv3LnF2sIsVy-wOPEvZphdgMT_pDgSqY/view?usp=sharing

**Responsável:** Guilherme Filho — Desenvolvedor Frontend
**Classe demonstrada:** `AppPokedex.java`
**Finalidade:** Este documento acompanha o vídeo de demonstração da interface, detalhando, para cada ação realizada durante a gravação, quais métodos da classe `AppPokedex` são acionados e quais informações técnicas relevantes estão por trás de cada comportamento.

---

## 1. Abertura da aplicação no BlueJ

**O que acontece no vídeo:** o programa é iniciado a partir do BlueJ, executando o método `main`.

**Métodos acionados:**
- `main(String[] args)` → chama `launch(args)`, o método padrão do JavaFX que inicializa o ciclo de vida da aplicação.
- `start(Stage stage)` → ponto de entrada real da interface. Aqui são feitas três coisas na ordem: (1) a conexão com o banco é aberta (`new Database("pokedex.db")`); (2) todos os repositórios são instanciados (`PokemonRepository`, `GolpeRepository`, `NatureRepository`, `EquipeRepository`, `MembroTimeRepository`); (3) a primeira tela é montada, chamando `mostrarPokedex()`.

**Detalhe técnico importante:** o `AppPokedex` nunca executa SQL diretamente — toda comunicação com o banco passa pelos repositórios. Isso mantém a interface desacoplada da lógica de persistência, e é por isso que, se o banco ainda não tiver sido populado via `PopularBanco.java`, a tela abre normalmente, mas aparece vazia (sem cards).

---

## 2. Explicação do FlowPane e do ScrollPane (grade de cards)

**O que acontece no vídeo:** explicação de como os cards se organizam na tela e se ajustam ao tamanho da janela.

**Métodos acionados:**
- `mostrarPokedex()` → monta a barra superior e chama `gradeDeCards(pokemonRepo.loadAll())` para carregar todos os Pokémon inicialmente.
- `gradeDeCards(List<Pokemon>)` → cria um `FlowPane`, que é o componente responsável por quebrar a lista de cards automaticamente em novas linhas conforme o espaço disponível.
- `criarCard(Pokemon p)` → gera cada card individualmente (imagem + nome).

**Detalhe técnico importante:** o `FlowPane` fica dentro de um `ScrollPane` configurado com `setFitToWidth(true)`. Isso explica o comportamento mostrado no vídeo: a **largura** da grade se adapta à largura da janela (o `FlowPane` decide quantos cards cabem por linha), enquanto a **altura** total é resolvida pela rolagem do `ScrollPane`, permitindo navegar por todos os 151 Pokémon sem que a tela precise crescer.

---

## 3. Barra de pesquisa e filtro por tipo

**O que acontece no vídeo:** busca de um Pokémon pelo nome e filtragem por tipo.

**Métodos acionados:**
- Ao digitar e clicar em "Pesquisar" (ou apertar Enter, já que `campoBusca.setOnAction(e -> btnPesquisar.fire())` está configurado): o listener de `btnPesquisar` é disparado, chamando `pokemonRepo.loadByName(termo)` caso o campo não esteja vazio, ou `pokemonRepo.loadAll()` caso esteja.
- Ao selecionar um tipo no `ComboBox filtroTipo`: o listener correspondente chama `pokemonRepo.loadByType(tipo)`, ou `loadAll()` se o valor selecionado for "Todos".
- Em ambos os casos, o resultado é passado novamente para `gradeDeCards(...)`, que reconstrói a grade de cards com a nova lista.

**Detalhe técnico importante:** busca e filtro não se combinam automaticamente — cada ação (pesquisa por nome ou filtro por tipo) refaz a consulta do zero a partir do que está selecionado/digitado naquele momento, sem manter o outro filtro aplicado simultaneamente.

---

## 4. Card ampliado de um Pokémon (Tela 2)

**O que acontece no vídeo:** clique em um Pokémon aleatório, abrindo a ficha detalhada com fraquezas, stats, Nature e ataque.

**Métodos acionados:**
- O clique no card, configurado em `criarCard()` via `card.setOnMouseClicked(e -> mostrarCard(p))`, chama `mostrarCard(Pokemon p)`.
- Dentro de `mostrarCard`, os stats (HP, Ataque, Defesa, Sp.Atk, Sp.Def, Velocidade) são exibidos através de `criarBarraStatus(String nome, int valor)`, chamado uma vez para cada status.
- As fraquezas e resistências são calculadas por `pokemonRepo.fraquezasDe(p)`, que retorna um `Map<String, Double>` com o multiplicador de dano de cada tipo contra aquele Pokémon.

**Detalhe técnico importante (barras adaptativas):** `criarBarraStatus` não apenas desenha a barra — ele decide a cor dela dinamicamente com base no valor do status: vermelho abaixo de 50, laranja entre 50 e 89, amarelo entre 90 e 119, e verde a partir de 120. É essa lógica que dá o efeito visual mostrado no vídeo.

**Detalhe técnico importante (fraquezas/vantagens):** o `Map` retornado por `fraquezasDe(p)` é percorrido uma única vez; para cada entrada, se o multiplicador for maior que 1.0, o tipo é classificado como fraqueza, senão como vantagem (resistência).

**Detalhe técnico importante (Nature padrão):** nessa tela, o valor "Nenhuma (padrão)" exibido para a Nature é um texto fixo no código (`new Label("Nenhuma (padrão)")`) — o Pokémon "base" da Pokédex nunca tem Nature associada; a Nature só existe no contexto de um `MembroTime`, depois que o Pokémon é adicionado a um time.

---

## 5. Adicionar Pokémon ao time e testar os métodos de cada membro

**O que acontece no vídeo:** uso do botão "Adicionar ao time" para montar um time de 3 Pokémon, seguido da demonstração de cada ação disponível para um membro do time.

**Métodos acionados:**
- `adicionarAoTime(Pokemon p)` → verifica se o time já está no limite de `MAX_TIME = 4`; se não estiver, cria um novo `MembroTime(p)` (com Nature e golpe ainda não definidos) e o adiciona à lista `time`, que existe apenas em memória até ser salva.
- `mostrarTime()` → monta a Tela 3, exibindo o contador "Meu Time (3/4)" e chamando `linhaTime(MembroTime)` para cada integrante.
- Dentro de `linhaTime`, cada botão está ligado a um método específico:
  - **Ver Detalhes** → `mostrarCardTime(MembroTime membro)`
  - **Alt. Nature** → `alterarNature(MembroTime membro)`
  - **Alt. Ataque** → `alterarAtaque(MembroTime membro)`
  - **Retirar** → remove o membro da lista `time` e chama `mostrarTime()` novamente para atualizar a tela

**Detalhe técnico importante (troca de Nature/Ataque):** `alterarNature` e `alterarAtaque` abrem um `ChoiceDialog`, populado respectivamente com `natureRepo.loadAll()` e `golpeRepo.loadAll()`. A escolha feita só é aplicada se o usuário confirmar o diálogo (`Optional<String> r = dlg.showAndWait()`); se ele cancelar, nada muda.

**Detalhe técnico importante (recálculo visual da Nature):** ao clicar em "Ver Detalhes" depois de trocar a Nature, `mostrarCardTime` busca o objeto `Nature` completo correspondente ao nome salvo no `MembroTime` e chama `criarBarraStatusNature(...)` para cada status (exceto HP, que nunca é afetado por Nature). Esse método recalcula o valor final aplicando **+10%** no status favorecido e **-10%** no prejudicado, e sinaliza isso visualmente: seta e texto em verde (↑) para o status aumentado, vermelho (↓) para o reduzido — exatamente o comportamento indicativo mostrado no vídeo.

---

## 6. Salvar o time no banco

**O que acontece no vídeo:** o time montado (com Natures e ataques já personalizados) é salvo.

**Métodos acionados:**
- `salvarTimeAtual()` → abre um `TextInputDialog` pedindo um nome para o time. Se confirmado e o nome não estiver vazio:
  1. Cria um objeto `Equipe(nome)` e o persiste com `equipeRepo.create(eq)`.
  2. Para cada `MembroTime` presente na lista `time` em memória, cria um **novo** `MembroTime(eq, pokemon, nature, golpe)` já vinculado a essa equipe, e o persiste com `membroRepo.create(novoDb)`.

**Detalhe técnico importante:** os objetos `MembroTime` salvos no banco são instâncias novas, não os mesmos objetos que estavam em memória — isso garante que cada membro salvo carregue a referência correta da `Equipe` recém-criada (com o `id` já gerado pelo banco).

---

## 7. Tela "Times Salvos" e testes de Carregar / Renomear / Excluir

**O que acontece no vídeo:** abertura da lista de times salvos (3 no total: dois criados previamente para demonstração e um salvo durante a gravação), seguida do teste de cada ação disponível.

**Métodos acionados:**
- `mostrarTimesSalvos()` → chama `equipeRepo.loadAll()` para buscar todas as equipes do banco e, para cada uma, chama `linhaEquipeSalva(Equipe eq)`.
- `linhaEquipeSalva(Equipe eq)` → para montar a lista de nomes de Pokémon exibida em cada linha, chama `membroRepo.loadByEquipe(eq)` e concatena os nomes com `String.join(", ", listaNomes)`.
- **"Carregar Time" (definir como time atual)** → `carregarEquipe(Equipe eq)`: busca os membros daquela equipe com `membroRepo.loadByEquipe(eq)`, limpa a lista `time` em memória (`time.clear()`) e insere os membros carregados, tornando aquele time salvo o **time atual** de trabalho. Em seguida, chama `mostrarTime()` para exibir o resultado.
- **"Renomear"** → `renomearEquipe(Equipe eq, Label lblNome)`: abre um `TextInputDialog` pré-preenchido com o nome atual; se um novo nome válido for informado, atualiza o objeto (`eq.setNome(novoNome)`), persiste a alteração com `equipeRepo.update(eq)` e atualiza o texto exibido na tela diretamente no `Label` (sem precisar redesenhar a lista inteira).
- **"Excluir"** → `excluirEquipe(Equipe eq)`: primeiro busca e apaga todos os `MembroTime` vinculados àquela equipe (`membroRepo.delete(mt)` para cada um), e só depois apaga a própria `Equipe` (`equipeRepo.delete(eq)`), evitando registros órfãos no banco. Ao final, chama `mostrarTimesSalvos()` novamente para atualizar a lista.

---

## 8. Cobertura do CRUD completo

Como comentado ao final do vídeo, a combinação dos métodos acima cobre as quatro operações fundamentais de um CRUD, todas atendidas pela dupla `EquipeRepository` / `MembroTimeRepository`:

| Operação | Onde acontece na interface | Métodos envolvidos |
| :--- | :--- | :--- |
| **Create** | Botão "Salvar Time no Banco" | `salvarTimeAtual()` → `equipeRepo.create()`, `membroRepo.create()` |
| **Read** | Tela "Times Salvos" e "Carregar Time" | `mostrarTimesSalvos()`, `linhaEquipeSalva()`, `carregarEquipe()` → `equipeRepo.loadAll()`, `membroRepo.loadByEquipe()` |
| **Update** | Botão "Renomear" | `renomearEquipe()` → `equipeRepo.update()` |
| **Delete** | Botão "Excluir" | `excluirEquipe()` → `membroRepo.delete()`, `equipeRepo.delete()` |

Isso demonstra que a interface não é apenas uma camada visual estática: ela integra, de ponta a ponta, todas as operações de persistência necessárias para o Team Builder funcionar como uma ferramenta real de criação e gerenciamento de times.
