import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import java.sql.SQLException;
import java.util.List;

public class MembroTimeRepository {

    private Dao<MembroTime, Integer> dao;

    public MembroTimeRepository(ConnectionSource connectionSource) throws SQLException {
        this.dao = DaoManager.createDao(connectionSource, MembroTime.class);
        // Cria a tabela automaticamente se ela não existir
        com.j256.ormlite.table.TableUtils.createTableIfNotExists(connectionSource, MembroTime.class);
    }

    public MembroTime salvar(MembroTime membro) throws SQLException {
        dao.createOrUpdate(membro);
        return membro;
    }

    public MembroTime buscarPorId(Integer id) throws SQLException {
        return dao.queryForId(id);
    }

    public List<MembroTime> buscarTodos() throws SQLException {
        return dao.queryForAll();
    }

    public int atualizar(MembroTime membro) throws SQLException {
        return dao.update(membro);
    }

    public int excluir(MembroTime membro) throws SQLException {
        return dao.delete(membro);
    }

    public int excluirPorId(Integer id) throws SQLException {
        return dao.deleteById(id);
    }

    public List<MembroTime> buscarPorEquipe(Equipe equipe) throws SQLException {
        QueryBuilder<MembroTime, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.where().eq(MembroTime.COL_EQUIPE, equipe);
        return queryBuilder.query();
    }
}