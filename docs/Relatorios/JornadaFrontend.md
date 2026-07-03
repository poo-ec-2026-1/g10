# Relatório de Jornada do Projeto — Pokédex & Team Builder

**Integrante:** Guilherme Filho
**Papel no projeto:** Desenvolvedor Frontend (JavaFX)
**Grupo:** G10 — poo-ec-2026-1

---

## 1. Introdução

Este relatório documenta minha jornada individual ao longo do desenvolvimento do projeto, cobrindo as principais dificuldades enfrentadas, a colaboração recebida dos demais membros da equipe durante a construção da minha parte (interface gráfica) e minhas contribuições em diferentes áreas do projeto como um todo.

---

## 2. Principais Dificuldades Enfrentadas

### 2.1 Escolha do Tema do Projeto

A primeira dificuldade surgiu ainda antes do início efetivo do desenvolvimento. Nossa ideia original era construir um sistema hospitalar, com o objetivo de facilitar o atendimento entre pacientes e médicos. A proposta seguia a exigência do professor de que o projeto resolvesse um problema de uma aplicação real, mas ele avaliou que o escopo seria complexo demais para o tempo e a estrutura disponíveis.

Como alternativa, Lucca, líder do grupo, sugeriu direcionarmos o projeto para o universo dos games — mais especificamente, Pokémon. A partir daí, definimos que não faríamos apenas mais uma Pokédex nos moldes das já existentes, mas uma aplicação que permitisse interação real do usuário: montar times, testar combinações de Nature e golpe e construir builds personalizadas. Essa decisão deu ao projeto um propósito diferente do de uma simples ferramenta de consulta.

### 2.2 Planejamento Inicial

O segundo obstáculo foi o planejamento da minha parte do projeto. Eu não tinha um conhecimento aprofundado sobre como Pokédex costumam ser estruturadas nem muita experiência prévia com o domínio de dados de Pokémon. Para resolver isso, pesquisei referências na internet, principalmente a PokeAPI, e a utilizei como fonte de inspiração para entender quais dados e telas fariam sentido. A partir dessa pesquisa, criei diagramas que representassem minhas ideias de interface e passei a desenvolver o projeto com base neles.

### 2.3 Desafios na Programação da Interface

O terceiro problema apareceu já durante a implementação. Trabalhar com FXML se mostrou bastante trabalhoso, principalmente pela necessidade de criar controllers separados para cada parte da interface, o que tornava o processo lento e propenso a erros de vinculação entre tela e código. Como solução, optei por abandonar o uso de FXML e concentrar toda a interface em uma única classe, `AppPokedex`, onde a parte gráfica é construída diretamente por código, junto de todos os métodos necessários para seu funcionamento. Essa mudança simplificou bastante o desenvolvimento e eliminou a fonte principal de erros que eu vinha enfrentando.

### 2.4 Configuração das Bibliotecas Necessárias

Outra dificuldade relevante foi conseguir configurar corretamente todas as bibliotecas externas necessárias para que o projeto rodasse na minha máquina, como o JavaFX SDK, o ORMLite e o driver JDBC do SQLite. Referenciar essas bibliotecas corretamente no BlueJ, garantindo que estivessem no classpath do projeto, exigiu diversas tentativas até que o ambiente ficasse configurado de forma estável — um processo que precisei repetir mais de uma vez, inclusive ao configurar o projeto em computadores diferentes.

### 2.5 Adaptações e Implementação de Novas Funcionalidades

Ao longo do desenvolvimento, também enfrentei dificuldade em implementar certas alterações e funcionalidades específicas na interface. Um exemplo foi a criação das barras coloridas de status dos Pokémon, que exigiu ajustar a lógica visual para que a cor da barra mudasse dinamicamente de acordo com o valor de cada status. De forma mais ampla, também foi desafiador adaptar a interface para atender ao CRUD completo de times: inicialmente, a aplicação não contava com a opção de criar e salvar times, apenas de montá-los temporariamente em memória. Foi necessário reestruturar parte da interface para incluir as telas e ações de salvar, carregar, renomear e excluir times, integrando essas novas funcionalidades aos repositórios responsáveis pela persistência no banco de dados.

---

## 3. Apoio e Colaboração da Equipe

Durante toda a construção da minha etapa, contei com apoio constante dos demais integrantes do grupo:

- **Gabriel e Luis:** mantive comunicação frequente com os dois para garantir que meu código estivesse alinhado com a base que eles desenvolviam — as classes principais de entidade e o banco de dados, que é o núcleo de funcionamento da Pokédex.
- **Todos os membros:** recebi feedbacks constantes sobre o progresso da minha parte, incluindo críticas, sugestões e auxílio direto em alguns métodos do código.
- **Lucca:** como líder, foi responsável por organizar e distribuir as tarefas individuais de cada integrante, o que manteve o trabalho estruturado e eficiente. Essa organização me permitiu focar exclusivamente nas tarefas atribuídas a mim, sem precisar me preocupar com o planejamento geral do que deveria ser feito.

---

## 4. Minha Contribuição no Projeto

Além do desenvolvimento da interface gráfica, contribuí em outras frentes do projeto:

- Criei diagramas mostrando como a interface ficaria visualmente e quais funcionalidades cada tela deveria ter, ajudando o grupo a visualizar o resultado final antes da implementação.
- Auxiliei na definição do fluxo geral de criação do projeto, ajudando a determinar quais classes deveriam existir e quais responsabilidades cada uma deveria assumir, de forma a garantir que conseguíssemos entregar o que havia sido prometido.
- Fiquei responsável pelos testes do código e pela implementação dos feedbacks recebidos do grupo ao longo do desenvolvimento.
- Auxiliei o líder do grupo na organização da documentação do projeto e do espaço de trabalho no GitHub.

---

## 5. Aprendizados

Ao longo do desenvolvimento da minha parte do projeto, destaco três aprendizados principais:

- **Planejamento de layout:** aprendi que é sempre melhor ter um esqueleto da parte gráfica definido antes de efetivamente implementá-la, evitando retrabalho e permitindo visualizar o fluxo entre telas antes de escrever o código.
- **Interface sem arquivo `.fxml`:** aprendi que é possível construir uma interface JavaFX completa e funcional sem depender de arquivos `.fxml` e de controllers separados, montando todos os componentes gráficos diretamente por código em uma única classe.
- **Conceito de CRUD:** aprendi de forma mais aprofundada o conceito fundamental de CRUD (Criar, Ler, Atualizar e Deletar) e como implementá-lo na prática dentro do projeto, integrando a interface gráfica às operações de persistência de times no banco de dados.

---

## 6. Considerações Finais

Ao longo do projeto, as principais dificuldades — escolha do tema, planejamento inicial sem referência clara e os desafios técnicos com FXML — foram superadas por meio de pesquisa, apoio da equipe e disposição para mudar de abordagem quando uma solução não estava funcionando bem (como a troca de FXML por uma interface construída inteiramente em código). O suporte constante dos colegas, somado à organização definida por Lucca, foi essencial para que eu pudesse concluir minha parte com foco e sem sobrecarga de decisões fora do meu escopo.
