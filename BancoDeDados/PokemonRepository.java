import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Repositorio de Pokemon (padrao Repository, igual ao StudentRepository do tutorial).
 * Alem do CRUD basico, tem busca por nome, filtro por tipo e calculo de fraquezas.
 */
public class PokemonRepository
{
    private static Database database;
    private static Dao<Pokemon, Integer> dao;
    private List<Pokemon> loadedPokemons;

    public PokemonRepository(Database database) {
        PokemonRepository.setDatabase(database);
        loadedPokemons = new ArrayList<Pokemon>();
    }

    public static void setDatabase(Database database) {
        PokemonRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Pokemon.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Pokemon.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Persiste um novo Pokémon no banco de dados.
     *
     * @param pokemon objeto Pokémon a ser salvo (deve ter os campos preenchidos)
     * @return o próprio objeto após a persistência
     */
    public Pokemon create(Pokemon pokemon) {
        try {
            int nrows = dao.create(pokemon);
            if (nrows == 0)
                throw new SQLException("Error: object not saved");
            loadedPokemons.add(pokemon);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return pokemon;
    }
    
    /**
     * Busca um Pokémon pelo seu id (número da Pokédex).
     *
     * @param id número identificador do Pokémon (1 a 151)
     * @return o Pokémon correspondente, ou null se não for encontrado
     */
    public Pokemon loadFromId(int id) {
        Pokemon p = null;
        try {
            p = dao.queryForId(id);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return p;
    }
/**
     * Retorna todos os Pokémon cadastrados no banco.
     *
     * @return lista com os 151 Pokémon
     */
    public List<Pokemon> loadAll() {
        try {
            this.loadedPokemons = dao.queryForAll();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedPokemons;
    }
 /**
     * Busca Pokémon cujo nome contenha o termo informado.
     * Utiliza o operador LIKE do SQL para busca parcial e case-insensitive.
     * Usado pela barra de pesquisa da interface.
     *
     * @param termo trecho a ser procurado no nome (ex.: "pika" retorna "Pikachu")
     * @return lista de Pokémon que contêm o termo no nome
     */
    /** Busca por nome (parcial, sem diferenciar maiusculas). Barra de pesquisa. */
    public List<Pokemon> loadByName(String termo) {
        List<Pokemon> lista = new ArrayList<Pokemon>();
        try {
            lista = dao.queryBuilder().where()
                       .like("nome", "%" + termo + "%")
                       .query();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return lista;
    }
 /**
     * Filtra Pokémon por tipo, considerando tanto o tipo primário quanto o secundário.
     * Usado pelo filtro de tipo da interface.
     *
     * @param tipo nome do tipo (ex.: "Fire", "Water")
     * @return lista de Pokémon cujo tipo_1 ou tipo_2 corresponde ao valor
     */
    /** Filtra por tipo (procura no tipo 1 OU no tipo 2). Filtro da Pokedex. */
    public List<Pokemon> loadByType(String tipo) {
        List<Pokemon> lista = new ArrayList<Pokemon>();
        try {
            lista = dao.queryBuilder().where()
                       .eq("tipo_1", tipo)
                       .or()
                       .eq("tipo_2", tipo)
                       .query();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return lista;
    }
/**
     * Calcula as fraquezas e resistências de um Pokémon combinando seus tipos
     * com a tabela de efetividade (TypeChart). Para Pokémon de dois tipos, os
     * multiplicadores são multiplicados entre si (ex.: Rock 2x contra Fire e
     * 2x contra Flying resulta em 4x contra Charizard).
     *
     * @param p Pokémon defensor
     * @return mapa de tipo atacante para multiplicador de dano, ordenado do
     *         mais perigoso para o menos perigoso
     */
    /**
     * Efetividade dos tipos atacantes CONTRA este Pokemon (dashboard / card).
     * Combina os 1-2 tipos e multiplica os multiplicadores em Java.
     * Ex.: Charizard -> {Rock=4.0, Electric=2.0, Water=2.0, Ground=0.0, ...}
     */
    public Map<String, Double> fraquezasDe(Pokemon p) {
        Map<String, Double> mult = new LinkedHashMap<String, Double>();
        List<TipoEfetividade> chart = new TipoEfetividadeRepository(database).loadAll();

        for (TipoEfetividade te : chart) {
            boolean atingeTipo1 = te.getTipoDefensor().equals(p.getTipo1());
            boolean atingeTipo2 = p.getTipo2() != null
                                  && !p.getTipo2().isEmpty()
                                  && te.getTipoDefensor().equals(p.getTipo2());
            if (atingeTipo1 || atingeTipo2) {
                String atk = te.getTipoAtacante();
                double atual = mult.containsKey(atk) ? mult.get(atk) : 1.0;
                mult.put(atk, atual * te.getMultiplicador());
            }
        }

        // remove os neutros (1.0) e ordena do mais perigoso pro menos
        Map<String, Double> ordenado = new LinkedHashMap<String, Double>();
        mult.entrySet().stream()
            .filter(e -> e.getValue() != 1.0)
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .forEach(e -> ordenado.put(e.getKey(), e.getValue()));
        return ordenado;
    }

    public List<Pokemon> getLoadedPokemons() { return loadedPokemons; }
}
