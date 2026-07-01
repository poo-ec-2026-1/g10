import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Repositorio de Golpe (padrao Repository, igual ao do tutorial).
 */
public class GolpeRepository
{
    private static Database database;
    private static Dao<Golpe, Integer> dao;
    private List<Golpe> loadedGolpes;

    public GolpeRepository(Database database) {
        GolpeRepository.setDatabase(database);
        loadedGolpes = new ArrayList<Golpe>();
    }

    public static void setDatabase(Database database) {
        GolpeRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Golpe.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Golpe.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Golpe create(Golpe golpe) {
        try {
            dao.create(golpe);
            loadedGolpes.add(golpe);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return golpe;
    }

    public Golpe loadFromId(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /** Busca um golpe pelo nome (util pro card mostrar tipo/poder). */
    public Golpe loadByName(String nome) {
        try {
            List<Golpe> r = dao.queryForEq("nome", nome);
            return r.isEmpty() ? null : r.get(0);
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<Golpe> loadAll() {
        try {
            this.loadedGolpes = dao.queryForAll();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedGolpes;
    }
}
