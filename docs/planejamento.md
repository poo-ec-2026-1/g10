# Documento de Especificação e Planejamento Global: Pokédex & Team Builder

## Seção 1 - Introdução

### 1.1 Justificativa e Motivação
O tema foi escolhido porque Pokémon era algo que todos os integrantes do grupo gostam, e tivemos a ideia pois percebemos que há uma falta de bons sites/aplicações de Pokédex, para poder ajudar os jogadores, sendo sempre confuso achar informações. Então o objetivo do nosso projeto, é facilitar a vida de todos os jogadores de Pokémon.

### 1.2 Descrição do Problema
Atualmente, as bases de dados de Pokémon costumam ser densas e de difícil navegação para usuários que desejam apenas focar na nostalgia ou estratégia básica da **1ª Geração (Kanto)**. Além disso, as ferramentas existentes muitas vezes separam a consulta de dados (Pokédex) da simulação de montagem de times (Team Builder). Este projeto resolve essa fragmentação unindo dados simplificados de fraquezas e montagem de equipes em um único ecossistema desktop amigável, ágil e intuitivo.

---

## Seção 2 - Plano do Projeto (Objetivos Globais)

### 2.1 Objetivo Geral
Desenvolver uma aplicação desktop em Java funcional, estável e com interface gráfica amigável (GUI), que funcione como uma Pokédex completa dos 151 Pokémon da primeira geração, integrada a um sistema de gerenciamento (CRUD) de equipes.

### 2.2 Objetivos Específicos
* **Módulo Pokédex (Consulta):** Implementar uma interface de listagem eficiente com suporte a rolagem, filtros por tipo (Fogo, Água, Grama, etc.) e uma barra de pesquisa responsiva por nome ou número identificador.
* **Tela de Detalhes Expandida:** Desenvolver uma interface de transição que exiba o Pokémon selecionado de forma ampliada, contendo seus sprites em alta definição, seus stats base detalhados ($HP, Attack, Defense, Sp. Attack, Sp. Defense, Speed$) e sua lista de fraquezas elementares.
* **Módulo Team Builder (CRUD):** Criar um sistema completo de gerenciamento de dados que permita ao usuário Criar, Ler, Atualizar e Deletar equipes personalizadas de até 4 Pokémons (Um formato existente no pokémon competitivo), além de seleção de $Natures$, $IVs$ e $EVs$
* **Arquitetura de Dados:** Configurar um banco de dados relacional **SQLite** persistente e otimizado, alimentado por um script automatizado de conversão a partir de uma base de dados pública (CSV).

---

## Seção 3 - Divisão de Tarefas e Cronograma do Projeto

Para garantir um desenvolvimento equilibrado e o uso correto das ferramentas (Git, GitHub, VS Code/BlueJ), o projeto foi dividido em quatro fases lógicas de desenvolvimento, distribuídas conforme os papéis de cada integrante.

### 3.1 Atribuição de Papéis e Responsabilidades

| Integrante | Papel Principal | Atribuição no Ciclo Total do Projeto |
| :--- | :--- | :--- |
| **Lucca** | Líder de Projeto / Documentação | Gestão do repositório, governança de prazos, especificação de requisitos de software e liderança do plano de testes (QA). |
| **Gabriel** | Arquiteto de Software | Definição da arquitetura técnica em Java, modelagem UML e desenvolvimento das regras de validação de negócios (ex: limites de Pokémon e regras do CRUD). |
| **Luis** | Desenvolvedor Backend | Modelagem relacional do banco de dados SQLite, desenvolvimento dos scripts de importação (ETL) do CSV e construção das classes de persistência/queries (DAO). |
| **Guilherme** | Desenvolvedor Frontend | Prototipagem da experiência do usuário (UI/UX), codificação das interfaces gráficas em Java e integração dos componentes visuais com as consultas do banco. |
| **Trabalho Conjunto** | Engenheiros de QA / Testes | Execução de testes de caixa-preta, validação conjunta de bugs de interface e consistência de dados na branch **main**. |

### 3.2 Cronograma Macro de Desenvolvimento (Issues GitHub)

#### Fase 1: Fundações, Planejamento e Banco de Dados (Status Atual)
* **[Issue #01 - Lucca]** Elaboração e refinamento do documento Markdown de especificação global do projeto.
* **[Issue #02 - Gabriel]** Construção da modelagem UML inicial (Diagramas de Classes e Casos de Uso).
* **[Issue #03 - Luis]** Criação do banco SQLite usando o CSV já feito como referência, e adicionar as colunas de Move Assinatura e de Fraquezas.
* **[Issue #04 - Guilherme]** Prototipagem/Mockup das 3 telas (Lista, Detalhes do pokémon e Team Builder).

#### Fase 2: Desenvolvimento do Código-Fonte Base (Codificação Isolada)
* **[Issue #05 - Gabriel]** Codificação das classes de entidade em Java (`Pokemon`, `Move`, `Team`) e regras de limitação do time.
* **[Issue #06 - Luis]** Implementação da lógica de persistência de dados (métodos de salvar, editar, listar e deletar times do banco).
* **[Issue #07 - Guilherme]** Implementação da interface gráfica em Java, gerando os cards da Pokédex, a tela de detalhes e os slots do Team Builder.
* **[Issue #08 - Lucca]** Criação do roteiro formal de testes integrados e cenários de aceite do usuário.

#### Fase 3: Integração e Conectividade entre Camadas
* **[Issue #09 - Luis & Guilherme]** Vinculação dos componentes visuais (filtros e barra de busca) às consultas diretas do SQLite.
* **[Issue #10 - Gabriel & Guilherme]** Implementação da lógica de navegação (evento de clique para abrir a tela de detalhes enviando o ID correto do Pokémon).
* **[Issue #11 - Guilherme, Luis & Gabriel]** Integração do formulário do Team Builder com os métodos de salvamento e edição do banco de dados.

#### Fase 4: Engenharia de QA, Refinamento Visual e Entrega
* **[Issue #12 - Todos]** Execução conjunta do plano de testes buscando inconsistências de dados e tratamento de exceções (ex: evitar travamentos ao deletar times).
* **[Issue #13 - Guilherme]** Polimento fino da interface ("interface bonita e amigável ao usuário"), ajustando fontes, espaçamentos e transições de tela.
* **[Issue #14 - Lucca]** Consolidação do repositório, verificação dos commits individuais e preparação do pacote final `.zip` da branch *main*.

---

## Seção 4 - Modelagem Estrutural do Sistema

### 4.1 Diagrama de Classes UML (Mapeamento de Entidades)
A arquitetura do sistema foi desenhada sob o paradigma de Orientação a Objetos, contendo as seguintes estruturas centrais:

* **`Pokemon`:** Entidade que representa a criatura. Atributos: `id` (int), `name` (String), `hp` (int), `attack` (int), `defense` (int), `spAttack` (int), `spDefense` (int), `speed` (int), `type1` (String), `type2` (String), `weaknesses` (List<String>) e `imagePath` (String).
* **`Move`:** Representa os golpes que podem ser equipados. Atributos: `name` (String), `type` (String), `power` (int), `accuracy` (int).
* **`Team`:** Entidade responsável por encapsular os times customizados do usuário. Atributos: `id` (int), `teamName` (String), `pokemonSlots` (List<Pokemon>) — limitada rigidamente a no máximo 6 instâncias de Pokémon, onde cada uma pode conter até 4 instâncias de `Move`.
* **`DatabaseConnection` / `PokemonDAO`:** Classes utilitárias responsáveis pelo tratamento de strings de conexão e execução de instruções SQL (`SELECT`, `INSERT`, `UPDATE`, `DELETE`) no arquivo local do SQLite.

### 4.2 Casos de Uso Globais (Use Cases)
* **UC01 - Consultar Pokédex:** O usuário interage com a barra de busca ou com os botões de tipo; a interface gráfica dispara uma consulta filtrada ao banco de dados e atualiza a exibição de cards na tela.
* **UC02 - Visualizar Ficha Técnica Individual:** O usuário clica em um card específico; o sistema intercepta o ID da criatura, realiza uma nova busca detalhada no SQLite e renderiza a tela com o sprite ampliado e as fraquezas mapeadas.
* **UC03 - Gerenciar Equipes (CRUD):** O usuário inicia a montagem de um time, seleciona os integrantes, atribui até 4 movimentos mapeados para cada um e salva. O sistema permite listar as equipes salvas, editar seus componentes ou excluir permanentemente o registro do banco de dados.