import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Repositorio da tabela de efetividade (TypeChart).
 */
public class TipoEfetividadeRepository
{
    private static Database database;
    private static Dao<TipoEfetividade, Integer> dao;
    private List<TipoEfetividade> loadedRows;

    public TipoEfetividadeRepository(Database database) {
        TipoEfetividadeRepository.setDatabase(database);
        loadedRows = new ArrayList<TipoEfetividade>();
    }

    public static void setDatabase(Database database) {
        TipoEfetividadeRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), TipoEfetividade.class);
            TableUtils.createTableIfNotExists(database.getConnection(), TipoEfetividade.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public TipoEfetividade create(TipoEfetividade te) {
        try {
            dao.create(te);
            loadedRows.add(te);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return te;
    }

    public List<TipoEfetividade> loadAll() {
        try {
            this.loadedRows = dao.queryForAll();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedRows;
    }
}
