# Relatório de Produção Individual - Etapa 1

**Nome:** Lucca de Amorim Romacheli e Melo Rocha / 202503255 
**Papel Principal:** Líder de Projeto / Documentação / Ajuda em trabalho conjunto
**Projeto:** Pokédex & Team Builder (1ª Geração - Kanto)  

---

## 1. Atribuição de Cargo e Tarefas

### Atribuição A Priori e Responsabilidades
Como **Líder de Projeto e Responsável pela Documentação**, minhas responsabilidades iniciais planejadas para a Etapa 1 envolviam a governança do repositório, definição e atribuição de prazos e tarefas (*issues*) no GitHub, especificação inicial dos requisitos de software e a garantia de que o projeto estivesse alinhado com o escopo de entregar 20% da aplicação.

### Atuação na Prática
Na prática, além de exercer a liderança organizacional e metodológica, assumi um papel de **arquiteto de informações e dados** na largada do projeto. Atuei diretamente na idealização do ecossistema do software, estruturei toda a documentação global de especificação e trabalhei ativamente na concepção do banco de dados, criando e tratando o arquivo CSV inicial (contendo os dados brutos dos 151 Pokémon) que serviu de fundação indispensável para a modelagem do banco SQLite executada pelo backend.

---

## 2. Contribuição de Acordo com a Atribuição

### Entregas Cumpridas
* **Elaboração da Documentação Global (Seções 1 a 4):** Redigi e estruturei completamente o documento de especificação do projeto em Markdown, definindo a justificativa, descrição do problema, objetivos macros, escopo do *Team Builder* com regras de *Natures/IVs/EVs*, e o cronograma detalhado de issues.
* **Mapeamento de Tarefas e Cronograma:** Dividi o projeto em 4 fases lógicas e distribuí as frentes de trabalho entre os integrantes (Gabriel, Luis, Guilherme e eu), garantindo a organização do fluxo de trabalho.
* **Criação da Base de Dados Primária:** Desenvolvi e limpei o arquivo CSV inicial com as métricas de Kanto, garantindo que o grupo tivesse dados estruturados para iniciar a codificação das entidades Java e tabelas SQL.
* **Trabalho conjunto:** Por termos um grupo menor, dividimos algumas coisas entre todo mundo, como os testes (ainda não feitos), mas também a idealização do projeto, ideias, algo que todos no grupo ajudaram.

### Commits

> **Obs:** Apesar de ter feito vários commits durante o projeto, como o meu papel nesta etapa não exigiu tanto trabalho executivo de código puro, não acumulei um volume massivo de commits individuais de desenvolvimento. O meu trabalho no GitHub foi focado em governança: garantir que tudo estivesse correto, organizar a estrutura das ramificações (*branches*) e revisar as integrações da equipe.

Abaixo estão os 3 registros mais marcantes da minha atuação nesta etapa, ilustrando as frentes de documentação, base de dados e governança do repositório:

1. **`commit fea6098` - *Add files via upload (planejamento.md)*** 
   * **Detalhes:** Criação e inclusão do documento oficial de especificação global do projeto dentro do diretório `docs/`. Este arquivo mapeia toda a introdução, justificativa, plano de objetivos globais, divisão de papéis e a modelagem estrutural do sistema em Markdown.
2. **`commit 2a505e2` - *Add files via upload (cmd usado de base para fazer o banco de dados)*** 
   * **Detalhes:** Upload dos arquivos de dados brutos que serviram de alicerce para a criação do banco de dados. Este commit disponibilizou para a equipe a base necessária para estruturar e popular o SQLite de maneira automatizada.
3. **`commit 974c352` - *Delete BancoDeDados/TipoEfetividade.java*** 
   * **Detalhes:** Exemplo prático da minha atuação em governança e manutenção do repositório. Trata-se de um commit de correção e limpeza técnica, removendo um arquivo gerado de forma equivocada, assegurando que a branch `main` permanecesse limpa, organizada e funcional para o grupo.
---

## 3. Contribuição Além do Atribuído

Embora meu papel estivesse focado na gestão e escrita de documentos, atuei diretamente na **Engenharia de Dados e Concepção Técnica**. 

* **Parceria no Backend:** Não me limitei a delegar o banco de dados; trabalhei em conjunto com o desenvolvedor backend (Luis) para planejar como o CSV seria convertido para o SQLite, sugerindo e desenhando a inclusão de colunas estratégicas como *Move Assinatura* e a estrutura de mapeamento de *Fraquezas Elementares*, adiantando problemas de modelagem que só apareceriam na fase de código.
* **Facilitação de Alinhamento:** Atuei como a ponte de comunicação entre a modelagem conceitual (UML) e o design de telas (Mockups), garantindo que as interfaces pensadas pelo frontend estivessem em perfeita sinergia com os dados que eu havia estruturado no CSV.

---

## 4. Considerações Gerais

### Aprendizados
Esta etapa inicial consolidou meu aprendizado sobre a importância de um planejamento rigoroso e de uma documentação viva antes da escrita de qualquer linha de código. Utilizar o Markdown para alinhar as expectativas do grupo e mapear as entidades nos poupou retrabalho. Além disso, aprofundei minha experiência na manipulação e tratamento de datasets (CSV) voltados para a integração com bancos de dados relacionais.

### Trabalhos Futuros e Conclusões
Com a base de dados pronta e o escopo blindado, os próximos passos envolvem liderar o plano de testes (*QA*) à medida que os módulos Java forem integrados. Concluo que o grupo atingiu com sucesso os 20% esperados para a Etapa 1, demonstrando domínio das ferramentas de versionamento (Git/GitHub) e uma base sólida para o desenvolvimento ágil do software.