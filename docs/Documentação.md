# 📖 Pokédex Interativa - Frontend POO

Este projeto foi desenvolvido com foco na aplicação de conceitos de **Programação Orientada a Objetos (POO)** no frontend, garantindo uma interface fluida, responsiva e com gerenciamento dinâmico de dados.

## 🎯 Objetivo do Projeto
Criar uma aplicação interativa que simula uma Pokédex, permitindo aos usuários visualizar, buscar, inspecionar e gerenciar uma equipe de Pokémon utilizando operações de CRUD.

## 🚀 Funcionalidades

### 🏠 1. Página Principal (Main Page)
O hub central de navegação e exibição de dados da aplicação.
- **Display de Catálogo:** Renderização dinâmica dos Pokémon em formato de *cards* com suporte a rolagem (scroll).
- **Sistema de Busca:** Barra de pesquisa para localizar Pokémon específicos pelo nome.
- **Filtro por Tipo:** Segmentação de buscas por tipagem (ex: Fogo, Água, Planta).
- **Inspecionar Time:** Botão de acesso rápido para a área de gerenciamento da equipe atual.

### 🔍 2. Visualização Detalhada
Ao clicar em um *card* na página principal, o usuário tem acesso a uma visão ampliada com informações aprofundadas:
- **Dados Básicos:** Nome e Tipagem.
- **Atributos:** Stats base de combate.
- **Combate:** Fraquezas, Vantagens e Golpes (ataque principal ou moveset).
- **Ação de Captura:** Botão dedicado para adicionar o Pokémon inspecionado diretamente ao time ativo.

### 🛡️ 3. Gerenciamento de Equipe (Área do Time)
Ambiente focado no gerenciamento de estado e operações **CRUD** (Create, Read, Update, Delete).
- **Regra de Limite:** O time ativo comporta no máximo **4 Pokémon** simultâneos.
- **Operações de Entidade (Pokémon):**
  - **Adicionar/Remover:** Inserir ou retirar membros do time atual.
  - **Edição de Atributos:** Alterar a *Nature* e modificar o *Ataque* dos Pokémon da equipe.
- **Operações de Conjunto (Equipes Salvas):**
  - **Salvar:** Armazenar a composição da equipe atual no banco de dados/local storage.
  - **Carregar/Trocar:** Substituir o time ativo por uma equipe salva anteriormente.
  - **Deletar:** Excluir permanentemente uma equipe salva.

## 🧠 Conceitos Aplicados
Este projeto coloca em prática os pilares da Programação Orientada a Objetos:
- **Abstração:** Modelagem das entidades do mundo real (`Pokemon`, `Team`, `Pokedex`) para o código.
- **Encapsulamento:** Proteção e gerenciamento seguro do estado da equipe e dos atributos dos Pokémon.
- **CRUD:** Criação, leitura, atualização e exclusão aplicadas ao gerenciamento de times salvos.
