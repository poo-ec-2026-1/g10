import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.List;

public class EquipeRepository {

    private Dao<Equipe, Integer> dao;

    public EquipeRepository(ConnectionSource connectionSource) throws SQLException {
        this.dao = DaoManager.createDao(connectionSource, Equipe.class);
        // Cria a tabela automaticamente se ela não existir
        com.j256.ormlite.table.TableUtils.createTableIfNotExists(connectionSource, Equipe.class);
    }

    public Equipe salvar(Equipe equipe) throws SQLException {
        dao.createOrUpdate(equipe);
        return equipe;
    }

    public Equipe buscarPorId(Integer id) throws SQLException {
        return dao.queryForId(id);
    }

    public List<Equipe> buscarTodos() throws SQLException {
        return dao.queryForAll();
    }

    public int atualizar(Equipe equipe) throws SQLException {
        return dao.update(equipe);
    }

    public int excluir(Equipe equipe) throws SQLException {
        return dao.delete(equipe);
    }

    public int excluirPorId(Integer id) throws SQLException {
        return dao.deleteById(id);
    }
}