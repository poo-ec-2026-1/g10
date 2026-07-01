import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import com.j256.ormlite.table.TableUtils;

/**
 * Cria o banco pokedex.db do zero e popula tudo via ORMLite:
 * Pokemon (CSV) + Golpe (CSV) + TypeChart + Natures.
 *
 * Como rodar no BlueJ: clique com o botao direito nesta classe e escolha
 * "void main(String[] args)". Os arquivos .csv precisam estar na pasta do projeto.
 */
public class PopularBanco
{
    public static void main(String[] args) throws Exception {
        Database db = new Database("pokedex.db");

        // Recria do zero (apaga tabelas antigas pra nao duplicar ao rodar de novo)
        TableUtils.dropTable(db.getConnection(), Pokemon.class, true);
        TableUtils.dropTable(db.getConnection(), Golpe.class, true);
        TableUtils.dropTable(db.getConnection(), Nature.class, true);
        TableUtils.dropTable(db.getConnection(), TipoEfetividade.class, true);

        PokemonRepository pokemonRepo = new PokemonRepository(db);
        GolpeRepository golpeRepo = new GolpeRepository(db);
        NatureRepository natureRepo = new NatureRepository(db);
        TipoEfetividadeRepository tipoEfetRepo = new TipoEfetividadeRepository(db);

        popularGolpes(golpeRepo);
        popularPokemons(pokemonRepo);
        popularTypeChart(tipoEfetRepo);
        popularNatures(natureRepo);

        System.out.println("\n=== Banco pokedex.db criado e populado com sucesso! ===");
        db.close();
    }

    // ---------- Pokemon (le pokemons.csv + pokemon_golpes.csv) ----------
    private static void popularPokemons(PokemonRepository repo) throws Exception {
        // mapa id -> nome do golpe caracteristico
        java.util.Map<Integer, String> golpeDoPokemon = new java.util.HashMap<Integer, String>();
        try (BufferedReader br = abrir("pokemon_golpes.csv")) {
            String linha; boolean primeira = true;
            while ((linha = br.readLine()) != null) {
                if (primeira) { primeira = false; continue; }
                if (linha.trim().isEmpty()) continue;
                String[] c = linha.split(",", -1);
                golpeDoPokemon.put(Integer.parseInt(c[0].trim()), c[1].trim());
            }
        }

        int total = 0;
        try (BufferedReader br = abrir("pokemons.csv")) {
            String linha; boolean primeira = true;
            while ((linha = br.readLine()) != null) {
                if (primeira) { primeira = false; continue; }
                if (linha.trim().isEmpty()) continue;
                String[] c = linha.split(",", -1);

                Pokemon p = new Pokemon();
                p.setId(Integer.parseInt(c[0].trim()));
                p.setNome(c[1].trim());
                p.setTipo1(c[2].trim());
                p.setTipo2(c[3].trim().isEmpty() ? null : c[3].trim());
                p.setHp(Integer.parseInt(c[4].trim()));
                p.setAtaque(Integer.parseInt(c[5].trim()));
                p.setDefesa(Integer.parseInt(c[6].trim()));
                p.setSpAtk(Integer.parseInt(c[7].trim()));
                p.setSpDef(Integer.parseInt(c[8].trim()));
                p.setVelocidade(Integer.parseInt(c[9].trim()));
                p.setSpriteUrl(c[10].trim());
                p.setGolpe(golpeDoPokemon.get(p.getId()));

                repo.create(p);
                total++;
            }
        }
        System.out.println("[OK] " + total + " Pokemon inseridos.");
    }

    // ---------- Golpe (le golpes.csv) ----------
    private static void popularGolpes(GolpeRepository repo) throws Exception {
        int total = 0;
        try (BufferedReader br = abrir("golpes.csv")) {
            String linha; boolean primeira = true;
            while ((linha = br.readLine()) != null) {
                if (primeira) { primeira = false; continue; }
                if (linha.trim().isEmpty()) continue;
                String[] c = linha.split(",", -1);

                Golpe g = new Golpe();
                g.setNome(c[0].trim());
                g.setTipo(c[1].trim());
                g.setPoder(c[2].trim().isEmpty() ? null : Integer.valueOf(c[2].trim()));
                g.setPrecisao(c[3].trim().isEmpty() ? null : Integer.valueOf(c[3].trim()));
                g.setCategoria(c[4].trim());

                repo.create(g);
                total++;
            }
        }
        System.out.println("[OK] " + total + " golpes inseridos.");
    }

    // ---------- TypeChart (dados fixos) ----------
    private static void popularTypeChart(TipoEfetividadeRepository repo) {
        // formato: "Atacante;Defensor=mult,Defensor=mult,..."  (so os != 1.0)
        String[] chart = {
            "Normal;Rock=0.5,Ghost=0,Steel=0.5",
            "Fire;Fire=0.5,Water=0.5,Grass=2,Ice=2,Bug=2,Rock=0.5,Dragon=0.5,Steel=2",
            "Water;Fire=2,Water=0.5,Grass=0.5,Ground=2,Rock=2,Dragon=0.5",
            "Electric;Water=2,Electric=0.5,Grass=0.5,Ground=0,Flying=2,Dragon=0.5",
            "Grass;Fire=0.5,Water=2,Grass=0.5,Poison=0.5,Ground=2,Flying=0.5,Bug=0.5,Rock=2,Dragon=0.5,Steel=0.5",
            "Ice;Fire=0.5,Water=0.5,Grass=2,Ice=0.5,Ground=2,Flying=2,Dragon=2,Steel=0.5",
            "Fighting;Normal=2,Ice=2,Poison=0.5,Flying=0.5,Psychic=0.5,Bug=0.5,Rock=2,Ghost=0,Dark=2,Steel=2,Fairy=0.5",
            "Poison;Grass=2,Poison=0.5,Ground=0.5,Rock=0.5,Ghost=0.5,Steel=0,Fairy=2",
            "Ground;Fire=2,Electric=2,Grass=0.5,Poison=2,Flying=0,Bug=0.5,Rock=2,Steel=2",
            "Flying;Electric=0.5,Grass=2,Fighting=2,Bug=2,Rock=0.5,Steel=0.5",
            "Psychic;Fighting=2,Poison=2,Psychic=0.5,Dark=0,Steel=0.5",
            "Bug;Fire=0.5,Grass=2,Fighting=0.5,Poison=0.5,Flying=0.5,Psychic=2,Ghost=0.5,Dark=2,Steel=0.5,Fairy=0.5",
            "Rock;Fire=2,Ice=2,Fighting=0.5,Ground=0.5,Flying=2,Bug=2,Steel=0.5",
            "Ghost;Normal=0,Psychic=2,Ghost=2,Dark=0.5",
            "Dragon;Dragon=2,Steel=0.5,Fairy=0",
            "Dark;Fighting=0.5,Psychic=2,Ghost=2,Dark=0.5,Fairy=0.5",
            "Steel;Fire=0.5,Water=0.5,Electric=0.5,Ice=2,Rock=2,Steel=0.5,Fairy=2",
            "Fairy;Fire=0.5,Fighting=2,Poison=0.5,Dragon=2,Dark=2,Steel=0.5"
        };
        int total = 0;
        for (String linha : chart) {
            String[] parte = linha.split(";");
            String atacante = parte[0];
            for (String par : parte[1].split(",")) {
                String[] kv = par.split("=");
                repo.create(new TipoEfetividade(atacante, kv[0], Double.parseDouble(kv[1])));
                total++;
            }
        }
        System.out.println("[OK] " + total + " confrontos de efetividade inseridos.");
    }

    // ---------- Natures (dados fixos) ----------
    private static void popularNatures(NatureRepository repo) {
        // {nome, stat_aumentada, stat_reduzida}  ("" = neutra)
        String[][] natures = {
            {"Hardy","",""}, {"Lonely","ataque","defesa"}, {"Brave","ataque","velocidade"},
            {"Adamant","ataque","sp_atk"}, {"Naughty","ataque","sp_def"}, {"Bold","defesa","ataque"},
            {"Docile","",""}, {"Relaxed","defesa","velocidade"}, {"Impish","defesa","sp_atk"},
            {"Lax","defesa","sp_def"}, {"Timid","velocidade","ataque"}, {"Hasty","velocidade","defesa"},
            {"Serious","",""}, {"Jolly","velocidade","sp_atk"}, {"Naive","velocidade","sp_def"},
            {"Modest","sp_atk","ataque"}, {"Mild","sp_atk","defesa"}, {"Quiet","sp_atk","velocidade"},
            {"Bashful","",""}, {"Rash","sp_atk","sp_def"}, {"Calm","sp_def","ataque"},
            {"Gentle","sp_def","defesa"}, {"Sassy","sp_def","velocidade"}, {"Careful","sp_def","sp_atk"},
            {"Quirky","",""}
        };
        for (String[] n : natures) {
            Nature nat = new Nature();
            nat.setNome(n[0]);
            nat.setStatAumentada(n[1].isEmpty() ? null : n[1]);
            nat.setStatReduzida(n[2].isEmpty() ? null : n[2]);
            repo.create(nat);
        }
        System.out.println("[OK] " + natures.length + " natures inseridas.");
    }

    /** Abre um CSV da pasta do projeto, em UTF-8 (pros caracteres do Nidoran). */
    private static BufferedReader abrir(String arquivo) throws Exception {
        return new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
    }
}
