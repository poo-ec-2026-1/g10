import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface JavaFX da Pokedex. Usa o banco (ORMLite) atraves dos repositorios.
 * NAO escreve SQL: pede objetos Pokemon aos repositorios e desenha as telas.
 *
 * Telas: 1) Pokedex (busca + filtro + grade com scroll)
 *        2) Card ampliado (stats, golpe, fraquezas/vantagens, nature)
 *        3) Meu Time (ate 4 Pokemon, com trocar nature / ataque / retirar)
 */
public class AppPokedex extends Application
{
    private static final int MAX_TIME = 4;

    private Database db;
    private PokemonRepository pokemonRepo;
    private GolpeRepository golpeRepo;
    private NatureRepository natureRepo;
    private EquipeRepository equipeRepo;
    private MembroTimeRepository membroRepo;

    private final List<MembroTime> time = new ArrayList<MembroTime>();

    private BorderPane root;

    private static final String[] TIPOS = {
        "Todos", "Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting",
        "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon",
        "Dark", "Steel", "Fairy"
    };

    @Override
    public void start(Stage stage) {
        db = new Database("pokedex.db");
        pokemonRepo = new PokemonRepository(db);
        golpeRepo = new GolpeRepository(db);
        natureRepo = new NatureRepository(db);
        
        try {
            equipeRepo = new EquipeRepository(db.getConnection());
            membroRepo = new MembroTimeRepository(db.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            alerta("Erro ao iniciar o banco de dados dos times.");
        }

        root = new BorderPane();
        root.setStyle("-fx-background-color: #eef1f5;");
        mostrarPokedex();

        Scene scene = new Scene(root, 1024, 720);
        stage.setTitle("Pokédex");
        stage.setScene(scene);
        stage.show();
    }

    // ============================ TELA 1: POKEDEX ============================
    private void mostrarPokedex() {
        // ----- barra de cima: pesquisa + botao + filtro + inspecionar time -----
        TextField campoBusca = new TextField();
        campoBusca.setPromptText("Pesquisar por Pokémon...");
        campoBusca.setPrefWidth(360);
        
        Button btnPesquisar = new Button("Pesquisar");
        
        ComboBox<String> filtroTipo = new ComboBox<String>();
        filtroTipo.getItems().addAll(TIPOS);
        filtroTipo.setValue("Todos");
        
        Button btnTime = new Button("Inspecionar Time");
        
        // BOTÃO: Times Salvos
        Button btnTimesSalvos = new Button("Times Salvos");
        btnTimesSalvos.setOnAction(e -> mostrarTimesSalvos());
        
        ScrollPane areaCards = new ScrollPane();
        areaCards.setFitToWidth(true);
        areaCards.setStyle("-fx-background: #eef1f5; -fx-background-color: #eef1f5;");
        
        // acoes
        btnPesquisar.setOnAction(e -> {
            String termo = campoBusca.getText().trim();
            if (termo.isEmpty()) areaCards.setContent(gradeDeCards(pokemonRepo.loadAll()));
            else areaCards.setContent(gradeDeCards(pokemonRepo.loadByName(termo)));
        });
        campoBusca.setOnAction(e -> btnPesquisar.fire()); // Enter pesquisa
        
        filtroTipo.setOnAction(e -> {
            String tipo = filtroTipo.getValue();
            if (tipo == null || tipo.equals("Todos")) areaCards.setContent(gradeDeCards(pokemonRepo.loadAll()));
            else areaCards.setContent(gradeDeCards(pokemonRepo.loadByType(tipo)));
        });
        
        btnTime.setOnAction(e -> mostrarTime());
        
    
        HBox barra = new HBox(10, campoBusca, btnPesquisar, filtroTipo, espacador(), btnTimesSalvos, btnTime);
        barra.setPadding(new Insets(14));
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setStyle("-fx-background-color: white; -fx-border-color: #d8dee6; -fx-border-width: 0 0 1 0;");
        
        // carga inicial: todos
        areaCards.setContent(gradeDeCards(pokemonRepo.loadAll()));
        root.setTop(barra);
        root.setCenter(areaCards);
    }
    /** Grade adaptavel de cards (FlowPane quebra linha sozinho; o ScrollPane rola). */
    private FlowPane gradeDeCards(List<Pokemon> lista) {
        FlowPane grade = new FlowPane();
        grade.setHgap(16);
        grade.setVgap(16);
        grade.setPadding(new Insets(16));
        grade.setStyle("-fx-background-color: #eef1f5;");
        for (Pokemon p : lista) {
            grade.getChildren().add(criarCard(p));
        }
        return grade;
    }

    /** Um card: imagem + nome. Clicar abre o card ampliado. */
    private VBox criarCard(Pokemon p) {
        ImageView img = new ImageView(new Image(p.getSpriteUrl(), 96, 96, true, true, true));
        Label nome = new Label(p.getNome());
        nome.setFont(Font.font("System", FontWeight.BOLD, 13));

        VBox card = new VBox(8, img, nome);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setPrefSize(140, 150);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
                    + "-fx-border-color: #d8dee6; -fx-border-radius: 12; -fx-cursor: hand;");
        card.setOnMouseClicked(e -> mostrarCard(p));
        return card;
    }

    // ========================= TELA 2: CARD AMPLIADO =========================
    private void mostrarCard(Pokemon p) {
        ImageView img = new ImageView(new Image(p.getSpriteUrl(), 180, 180, true, true, true));
        Label nome = new Label(p.getNome());
        nome.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label tipos = new Label("Tipo: " + p.getTiposFormatados());
        tipos.setFont(Font.font(14));

        // coluna esquerda: stats + golpe + nature
            VBox esquerda = new VBox(8,
        titulo("Estatísticas"),
        criarBarraStatus("HP", p.getHp()),
        criarBarraStatus("ATK", p.getAtaque()),
        criarBarraStatus("DEF", p.getDefesa()),
        criarBarraStatus("Sp.Atk", p.getSpAtk()),
        criarBarraStatus("Sp.Def", p.getSpDef()),
        criarBarraStatus("SPEED", p.getVelocidade()),
        titulo("Ataque"),
        new Label(p.getGolpe() != null ? p.getGolpe() : "-"),
        titulo("Nature"),
        new Label("Nenhuma (padrão)")
    );

        // coluna direita: fraquezas e vantagens (vem da tabela de efetividade)
        Map<String, Double> efeito = pokemonRepo.fraquezasDe(p);
        VBox fraquezas = new VBox(4, titulo("Fraquezas"));
        VBox vantagens = new VBox(4, titulo("Vantagens (resistências)"));
        for (Map.Entry<String, Double> en : efeito.entrySet()) {
            String txt = en.getKey() + "  (x" + en.getValue() + ")";
            if (en.getValue() > 1.0) fraquezas.getChildren().add(new Label(txt));
            else vantagens.getChildren().add(new Label(txt));
        }
        VBox direita = new VBox(14, fraquezas, vantagens);

        HBox colunas = new HBox(48, esquerda, direita);
        colunas.setPadding(new Insets(16, 0, 0, 0));

        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setOnAction(e -> mostrarPokedex());
        Button btnAddTime = new Button("Adicionar ao time");
        btnAddTime.setOnAction(e -> adicionarAoTime(p));
        HBox acoes = new HBox(10, btnVoltar, btnAddTime);

        VBox conteudo = new VBox(10, acoes, img, nome, tipos, colunas);
        conteudo.setAlignment(Pos.TOP_CENTER);
        conteudo.setPadding(new Insets(20));
        conteudo.setMaxWidth(640);

        ScrollPane sp = new ScrollPane(conteudo);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #eef1f5; -fx-background-color: #eef1f5;");

        root.setTop(null);
        root.setCenter(sp);
    }

    private void adicionarAoTime(Pokemon p) {
        if (time.size() >= MAX_TIME) {
            alerta("O time já está cheio (máximo de " + MAX_TIME + " Pokémon).");
            return;
        }
        time.add(new MembroTime(p));
        alerta(p.getNome() + " foi adicionado ao time!");
    }

    // ============================ TELA 3: MEU TIME ============================
    private void mostrarTime() {
        VBox lista = new VBox(12);
        lista.setPadding(new Insets(16));
        
        Button btnVoltar = new Button("← Voltar para a Pokédex");
        btnVoltar.setOnAction(e -> mostrarPokedex());
        
        
        Button btnSalvar = new Button("Salvar Time no Banco");
        btnSalvar.setOnAction(e -> salvarTimeAtual());
        
        
        HBox topoTime = new HBox(10, btnVoltar, espacador(), btnSalvar);
        topoTime.setAlignment(Pos.CENTER_LEFT);
        
        Label titulo = new Label("Meu Time (" + time.size() + "/" + MAX_TIME + ")");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        
        lista.getChildren().addAll(topoTime, titulo);
        
        if (time.isEmpty()) {
            lista.getChildren().add(new Label("Seu time está vazio. Adicione Pokémon pelo card."));
        } else {
            for (MembroTime membro : time) {
                lista.getChildren().add(linhaTime(membro));
            }
        }
        
        ScrollPane sp = new ScrollPane(lista);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #eef1f5; -fx-background-color: #eef1f5;");
        
        root.setTop(null);
        root.setCenter(sp);
    }

    /** Uma linha do time: imagem + nome + nature/golpe atuais + botoes. */
    private HBox linhaTime(MembroTime membro) {
        Pokemon p = membro.getPokemon();
        ImageView img = new ImageView(new Image(p.getSpriteUrl(), 72, 72, true, true, true));
        
        Label nome = new Label(p.getNome());
        nome.setFont(Font.font("System", FontWeight.BOLD, 15));
        Label info = new Label("Nature: " + membro.getNature() + " | Ataque: " + membro.getGolpe());
        
        VBox texto = new VBox(4, nome, info);
        texto.setAlignment(Pos.CENTER_LEFT);
        
        Button btnDetalhes = new Button("Ver Detalhes");
        btnDetalhes.setOnAction(e -> mostrarCardTime(membro));
        
        Button btnNature = new Button("Alt. Nature");
        btnNature.setOnAction(e -> alterarNature(membro));
        
        Button btnAtk = new Button("Alt. Ataque");
        btnAtk.setOnAction(e -> alterarAtaque(membro));
        
        Button btnRetirar = new Button("Retirar");
        btnRetirar.setOnAction(e -> { time.remove(membro); mostrarTime(); });
        
        HBox botoes = new HBox(8, btnDetalhes, btnNature, btnAtk, btnRetirar);
        botoes.setAlignment(Pos.CENTER_RIGHT);
        
        HBox linha = new HBox(14, img, texto, espacador(), botoes);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setPadding(new Insets(12));
        linha.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d8dee6; -fx-border-radius: 12;");
        
        return linha;
    }

    private void alterarNature(MembroTime membro) {
        List<String> opcoes = new ArrayList<String>();
        opcoes.add("Nenhuma");
        for (Nature n : natureRepo.loadAll()) opcoes.add(n.getNome());

        ChoiceDialog<String> dlg = new ChoiceDialog<String>(membro.getNature(), opcoes);
        dlg.setTitle("Alterar Nature");
        dlg.setHeaderText(membro.getPokemon().getNome());
        dlg.setContentText("Escolha a nature:");
        Optional<String> r = dlg.showAndWait();
        if (r.isPresent()) { membro.setNature(r.get()); mostrarTime(); }
    }

    private void alterarAtaque(MembroTime membro) {
        List<String> opcoes = new ArrayList<String>();
        for (Golpe g : golpeRepo.loadAll()) opcoes.add(g.getNome());

        ChoiceDialog<String> dlg = new ChoiceDialog<String>(membro.getGolpe(), opcoes);
        dlg.setTitle("Alterar Ataque");
        dlg.setHeaderText(membro.getPokemon().getNome());
        dlg.setContentText("Escolha o ataque:");
        Optional<String> r = dlg.showAndWait();
        if (r.isPresent()) { membro.setGolpe(r.get()); mostrarTime(); }
    }
    
        // ============================ LÓGICA DE TIMES SALVOS ============================

    private void salvarTimeAtual() {
        if (time.isEmpty()) {
            alerta("Não há Pokémon no time para salvar!");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog("Meu Time 1");
        dialog.setTitle("Salvar Time");
        dialog.setHeaderText("Salvando seu time atual");
        dialog.setContentText("Digite um nome para o seu time:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String nome = result.get().trim();
            if (nome.isEmpty()) return;
            
            try {
                // 1. Cria a Equipe e salva no banco
                Equipe eq = new Equipe(nome);
                equipeRepo.salvar(eq);
                
                // 2. Salva cada membro do time atrelado a essa equipe
                for (MembroTime mt : time) {
                    MembroTime novoDb = new MembroTime(eq, mt.getPokemon(), mt.getNature(), mt.getGolpe());
                    membroRepo.salvar(novoDb);
                }
                alerta("Time '" + nome + "' salvo com sucesso!");
            } catch (SQLException ex) {
                alerta("Erro ao salvar no banco: " + ex.getMessage());
            }
        }
    }

    private void mostrarTimesSalvos() {
        VBox lista = new VBox(12);
        lista.setPadding(new Insets(16));
        
        Button btnVoltar = new Button("← Voltar para a Pokédex");
        btnVoltar.setOnAction(e -> mostrarPokedex());
        
        Label titulo = new Label("Meus Times Salvos");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        lista.getChildren().addAll(btnVoltar, titulo);
        
        try {
            List<Equipe> equipes = equipeRepo.buscarTodos();
            if (equipes.isEmpty()) {
                lista.getChildren().add(new Label("Você ainda não tem nenhum time salvo."));
            } else {
                for (Equipe eq : equipes) {
                    lista.getChildren().add(linhaEquipeSalva(eq));
                }
            }
        } catch (SQLException ex) {
            lista.getChildren().add(new Label("Erro ao carregar os times do banco."));
        }
        
        ScrollPane sp = new ScrollPane(lista);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #eef1f5; -fx-background-color: #eef1f5;");
        
        root.setTop(null);
        root.setCenter(sp);
    }

    private HBox linhaEquipeSalva(Equipe eq) {
    Label lblNome = new Label(eq.getNome());
    lblNome.setFont(Font.font("System", FontWeight.BOLD, 16));
    
    String nomesPokemons = "Vazio";
    try {
        List<MembroTime> membros = membroRepo.buscarPorEquipe(eq);
        if (!membros.isEmpty()) {
            List<String> listaNomes = new ArrayList<>();
            for (MembroTime mt : membros) {
                listaNomes.add(mt.getPokemon().getNome());
            }
            nomesPokemons = String.join(", ", listaNomes);
        }
    } catch (SQLException ex) {
        nomesPokemons = "Erro ao carregar Pokémon";
    }
    
    Label lblMembros = new Label(nomesPokemons);
    lblMembros.setFont(Font.font("System", 13));
    lblMembros.setStyle("-fx-text-fill: #666666;");
    
    VBox infoTime = new VBox(4, lblNome, lblMembros);
    infoTime.setAlignment(Pos.CENTER_LEFT);
    
    Button btnCarregar = new Button("Carregar Time");
    btnCarregar.setOnAction(e -> carregarEquipe(eq));
    
    // BOTÃO: Renomear
    Button btnRenomear = new Button("Renomear");
    btnRenomear.setOnAction(e -> renomearEquipe(eq, lblNome));
    
    Button btnExcluir = new Button("Excluir");
    btnExcluir.setStyle("-fx-text-fill: red;");
    btnExcluir.setOnAction(e -> excluirEquipe(eq));
    
    // Adicionando o botão renomear na lista de botões
    HBox botoes = new HBox(8, btnCarregar, btnRenomear, btnExcluir);
    
    HBox linha = new HBox(14, infoTime, espacador(), botoes);
    linha.setAlignment(Pos.CENTER_LEFT);
    linha.setPadding(new Insets(12));
    linha.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d8dee6; -fx-border-radius: 12;");
    
    return linha;
    }

    private void carregarEquipe(Equipe eq) {
        try {
            List<MembroTime> membros = membroRepo.buscarPorEquipe(eq);
            time.clear(); // Limpa o time atual da memória
            
            for (MembroTime mt : membros) {
                // Adiciona os membros carregados do banco na memória RAM
                time.add(mt);
            }
            alerta("Time '" + eq.getNome() + "' carregado com sucesso!");
            mostrarTime(); // Vai direto para a tela do time para ver os pokemons carregados
        } catch (SQLException ex) {
            alerta("Erro ao carregar o time: " + ex.getMessage());
        }
        }

    private void excluirEquipe(Equipe eq) {
        try {
            // Primeiro exclui os membros para não deixar dados "órfãos" no banco
            List<MembroTime> membros = membroRepo.buscarPorEquipe(eq);
            for (MembroTime mt : membros) {
                membroRepo.excluir(mt);
            }
            // Depois exclui a equipe
            equipeRepo.excluir(eq);
            
            // Atualiza a tela para sumir a linha
            mostrarTimesSalvos();
        } catch (SQLException ex) {
            alerta("Erro ao excluir o time: " + ex.getMessage());
        }
    }

    // ============================== UTILITARIOS ==============================
    private Label titulo(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("System", FontWeight.BOLD, 14));
        return l;
    }

    private Region espacador() {
        Region r = new Region();
        HBox.setHgrow(r, javafx.scene.layout.Priority.ALWAYS);
        return r;
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private HBox criarBarraStatus(String nome, int valor) {
    Label lblNome = new Label(nome);
    lblNome.setPrefWidth(55);
    lblNome.setFont(Font.font("System", FontWeight.BOLD, 12));

    ProgressBar barra = new ProgressBar(valor / 255.0);
    barra.setPrefWidth(120);
    barra.setPrefHeight(14);
    
    // Lógica para mudar a cor da barra dependendo do valor
    String cor = "#4caf50"; // Verde (padrão para alto)
    if (valor < 50) cor = "#ff4c4c"; // Vermelho (baixo)
    else if (valor < 90) cor = "#ffad33"; // Laranja (médio)
    else if (valor < 120) cor = "#f2d94e"; // Amarelo (bom)
    
    barra.setStyle("-fx-accent: " + cor + ";");

    Label lblValor = new Label(String.valueOf(valor));
    lblValor.setPrefWidth(30);
    lblValor.setAlignment(Pos.CENTER_RIGHT);

    HBox linha = new HBox(10, lblNome, barra, lblValor);
    linha.setAlignment(Pos.CENTER_LEFT);
    return linha;
    }
    
     // MÉTODO (Faz a matemática e muda a cor/texto)
    private HBox criarBarraStatusNature(String nomeLabel, int valorBase, String nomeStatBanco, Nature nature) {
        int valorFinal = valorBase;
        String sufixo = "";
        String corTexto = "-fx-text-fill: black;";

        if (nature != null) {
            if (nomeStatBanco.equals(nature.getStatAumentada())) {
                valorFinal = (int) (valorBase * 1.1); // +10%
                sufixo = " (↑)";
                corTexto = "-fx-text-fill: #2e7d32; -fx-font-weight: bold;"; // Verde escuro
            } else if (nomeStatBanco.equals(nature.getStatReduzida())) {
                valorFinal = (int) (valorBase * 0.9); // -10%
                sufixo = " (↓)";
                corTexto = "-fx-text-fill: #d32f2f; -fx-font-weight: bold;"; // Vermelho escuro
            }
        }

        Label lblNome = new Label(nomeLabel + sufixo);
        lblNome.setPrefWidth(75); 
        lblNome.setStyle(corTexto);
        lblNome.setFont(Font.font("System", FontWeight.BOLD, 12));

        ProgressBar barra = new ProgressBar(valorFinal / 255.0);
        barra.setPrefWidth(100);
        barra.setPrefHeight(14);
        
        String cor = "#4caf50";
        if (valorFinal < 50) cor = "#ff4c4c";
        else if (valorFinal < 90) cor = "#ffad33";
        else if (valorFinal < 120) cor = "#f2d94e";
        barra.setStyle("-fx-accent: " + cor + ";");

        Label lblValor = new Label(String.valueOf(valorFinal));
        lblValor.setPrefWidth(30);
        lblValor.setAlignment(Pos.CENTER_RIGHT);
        lblValor.setStyle(corTexto);

        HBox linha = new HBox(10, lblNome, barra, lblValor);
        linha.setAlignment(Pos.CENTER_LEFT);
        return linha;
    }
    
    // MÉTODO (Abre o card ampliado do membro do time)
    private void mostrarCardTime(MembroTime membro) {
        Pokemon p = membro.getPokemon();
        
        // Busca a Nature completa no banco para saber os status afetados
        Nature natureObj = null;
        for (Nature n : natureRepo.loadAll()) {
            if (n.getNome().equals(membro.getNature())) {
                natureObj = n;
                break;
            }
        }

        ImageView img = new ImageView(new Image(p.getSpriteUrl(), 180, 180, true, true, true));
        Label nome = new Label(p.getNome());
        nome.setFont(Font.font("System", FontWeight.BOLD, 22));
        Label tipos = new Label("Tipo: " + p.getTiposFormatados());
        tipos.setFont(Font.font(14));

        // Coluna esquerda com a matemática aplicada
        VBox esquerda = new VBox(8,
            titulo("Estatísticas (Efeito da Nature)"),
            criarBarraStatus("HP", p.getHp()), // HP não muda
            criarBarraStatusNature("ATK", p.getAtaque(), "ataque", natureObj),
            criarBarraStatusNature("DEF", p.getDefesa(), "defesa", natureObj),
            criarBarraStatusNature("Sp.Atk", p.getSpAtk(), "sp_atk", natureObj),
            criarBarraStatusNature("Sp.Def", p.getSpDef(), "sp_def", natureObj),
            criarBarraStatusNature("SPEED", p.getVelocidade(), "velocidade", natureObj),
            titulo("Ataque Escolhido"),
            new Label(membro.getGolpe()),
            titulo("Nature Escolhida"),
            new Label(membro.getNature())
        );

        // Coluna direita (Fraquezas e Vantagens - igual ao card normal)
        Map<String, Double> efeito = pokemonRepo.fraquezasDe(p);
        VBox fraquezas = new VBox(4, titulo("Fraquezas"));
        VBox vantagens = new VBox(4, titulo("Vantagens (resistências)"));
        for (Map.Entry<String, Double> en : efeito.entrySet()) {
            String txt = en.getKey() + " (x" + en.getValue() + ")";
            if (en.getValue() > 1.0) fraquezas.getChildren().add(new Label(txt));
            else vantagens.getChildren().add(new Label(txt));
        }
        VBox direita = new VBox(14, fraquezas, vantagens);
        
        HBox colunas = new HBox(48, esquerda, direita);
        colunas.setPadding(new Insets(16, 0, 0, 0));

        Button btnVoltar = new Button("← Voltar para o Time");
        btnVoltar.setOnAction(e -> mostrarTime());
        
        HBox acoes = new HBox(10, btnVoltar);
        
        VBox conteudo = new VBox(10, acoes, img, nome, tipos, colunas);
        conteudo.setAlignment(Pos.TOP_CENTER);
        conteudo.setPadding(new Insets(20));
        conteudo.setMaxWidth(640);
        
        ScrollPane sp = new ScrollPane(conteudo);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #eef1f5; -fx-background-color: #eef1f5;");
        
        root.setTop(null);
        root.setCenter(sp);
    }
    
    private void renomearEquipe(Equipe eq, Label lblNome) {
    TextInputDialog dialog = new TextInputDialog(eq.getNome());
    dialog.setTitle("Renomear Time");
    dialog.setHeaderText("Renomeando: " + eq.getNome());
    dialog.setContentText("Novo nome:");

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {
        String novoNome = result.get().trim();
        if (!novoNome.isEmpty() && !novoNome.equals(eq.getNome())) {
            try {
                eq.setNome(novoNome);
                equipeRepo.atualizar(eq); // Atualiza no banco de dados
                lblNome.setText(novoNome); // Atualiza visualmente na tela
            } catch (SQLException ex) {
                alerta("Erro ao renomear o time: " + ex.getMessage());
            }
        }
    }
    }
}