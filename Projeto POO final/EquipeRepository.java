import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Repositorio de Equipe (padrao Repository, mesmo molde dos demais).
 * Guarda o "time" do usuario (nome + Pokemon selecionados via MembroTime).
 */
public class EquipeRepository
{
    private static Database database;
    private static Dao<Equipe, Integer> dao;
    private List<Equipe> loadedEquipes;

    public EquipeRepository(Database database) {
        EquipeRepository.setDatabase(database);
        loadedEquipes = new ArrayList<Equipe>();
    }

    public static void setDatabase(Database database) {
        EquipeRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Equipe.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Equipe.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Equipe create(Equipe equipe) {
        try {
            int nrows = dao.create(equipe);
            if (nrows == 0)
                throw new SQLException("Error: object not saved");
            loadedEquipes.add(equipe);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return equipe;
    }

    public Equipe loadFromId(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<Equipe> loadAll() {
        try {
            this.loadedEquipes = dao.queryForAll();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedEquipes;
    }

    public int update(Equipe equipe) {
        try {
            return dao.update(equipe);
        } catch (SQLException e) {
            System.out.println(e);
            return 0;
        }
    }

    public int delete(Equipe equipe) {
        try {
            return dao.delete(equipe);
        } catch (SQLException e) {
            System.out.println(e);
            return 0;
        }
    }

    public int deleteById(int id) {
        try {
            return dao.deleteById(id);
        } catch (SQLException e) {
            System.out.println(e);
            return 0;
        }
    }

    public List<Equipe> getLoadedEquipes() { return loadedEquipes; }
}
