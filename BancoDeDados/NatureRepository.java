import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Repositorio de Nature (padrao Repository, igual ao do tutorial).
 */
public class NatureRepository
{
    private static Database database;
    private static Dao<Nature, Integer> dao;
    private List<Nature> loadedNatures;

    public NatureRepository(Database database) {
        NatureRepository.setDatabase(database);
        loadedNatures = new ArrayList<Nature>();
    }

    public static void setDatabase(Database database) {
        NatureRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Nature.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Nature.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Nature create(Nature nature) {
        try {
            dao.create(nature);
            loadedNatures.add(nature);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return nature;
    }

    public Nature loadFromId(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<Nature> loadAll() {
        try {
            this.loadedNatures = dao.queryForAll();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedNatures;
    }
}
