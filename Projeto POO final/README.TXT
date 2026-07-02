### TÍTULO DO PROJETO:
Pokédex Interativa Desktop (JavaFX + ORMLite)

### OBJETIVO DO PROJETO:
Prover uma interface gráfica de usuário (GUI) intuitiva e responsiva que funcione como um catálogo e gerenciador tático de Pokémon. O projeto visa encapsular regras de negócio da franquia (como modificadores matemáticos de Naturezas e mapeamento de fraquezas elementares) e integrá-las de forma robusta a uma camada de persistência relacional local (SQLite) usando o padrão Repository via ORMLite, garantindo que o controle visual nunca execute instruções SQL cruas diretamente.

### VERSÃO ou DATA:
Versão 1.0 — Julho de 2026

### COMO INICIAR O PROJETO:
1. **Pré-requisitos**: Certifique-se de ter o Java JDK instalado (versão 17 ou superior recomendada) e o SDK do JavaFX devidamente configurado em sua IDE (como IntelliJ IDEA, Eclipse ou BlueJ) ou ambiente de compilação.
Aqui está a lista completa de bibliotecas que esse projeto precisa para rodar:
JavaFX (interface gráfica):

javafx-base-23.0.2-win
javafx-controls-23.0.2-win
javafx-graphics-23.0.2-win

Baixe em gluonhq.com/products/javafx (versão SDK, Windows, x64).

Banco de dados (ORMLite + SQLite):

sqlite-jdbc-3.46.0.0
ormlite-core-6.1
ormlite-jdbc-6.1
slf4j-api-2.0.13
slf4j-simple-2.0.13

2. **Base de Dados**: O arquivo de banco de dados SQLite contendo as tabelas populadas deve ser posicionado obrigatoriamente no diretório raiz do projeto com o nome exato de `pokedex.db`.
3. **Execução**: Configure os parâmetros de inicialização de módulos da VM (`--module-path` e `--add-modules javafx.controls`) e execute o método `main` contido na classe principal `AppPokedex.java` para renderizar a janela da aplicação. No BlueJ é so clicar com o botão direito em cima da classe appPokedex e clicar em "Executar a aplicação em JavaFX".

### AUTORES:
Alunos do Grupo 10 (g10) — Engenharia de Computação / Disciplina de Programação Orientada a Objetos (POO).
-Lucca de Amorim (Lider),
-Luis Leão,
-Guilherme Gomes,
-Gabriel Lemos.

### INSTRUÇÕES PARA O USUÁRIO:
* **Explorar e Filtrar**: Na tela inicial, utilize o campo de texto para buscar um Pokémon pelo nome ou utilize a caixa de seleção para filtrar a listagem completa por tipos elementares específicos (ex: *Fire*, *Water*, *Electric*).
* **Análise Detalhada**: Clique em qualquer card da grade para expandi-lo. Na tela de detalhes, as barras de atributos mudam de cor dinamicamente com base no valor base do status (Vermelho para valores menores que 50, Laranja para intermediários, Amarelo para bons e Verde para excelentes).
* **Montagem de Equipes**: No card expandido de um Pokémon, clique em "Adicionar ao time". O sistema permite um limite máximo de até 4 integrantes simultâneos na memória de rascunho.
* **Customização Tática**: Acesse a sua equipe clicando em "Inspecionar Time". Na linha de cada membro, você pode alterar interativamente o movimento principal ("Alt. Ataque") ou a personalidade ("Alt. Nature") através de caixas de escolha. 
* **Impacto das Natures**: Ao clicar em "Ver Detalhes" de um Pokémon que está no seu time, você verá o efeito matemático real da Natureza aplicada: um bônus de +10% é indicado com um sufixo `(↑)` em verde e uma penalidade de -10% é exibida com um sufixo `(↓)` em vermelho.
* **Persistência e Histórico**: Para salvar sua equipe permanentemente no banco de dados, clique em "Salvar Time no Banco" e dê um nome a ela. Na barra superior da tela inicial, utilize o botão "Times Salvos" para visualizar seu histórico, carregar equipes salvas de volta para uso, renomear os títulos ou excluí-las com segurança em cascata.
