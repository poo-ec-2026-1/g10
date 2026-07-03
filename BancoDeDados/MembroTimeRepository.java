/**
 * Repositório da entidade MembroTime (Pokémon dentro de um time).
 *
 * Segue o padrão Repository do tutorial da disciplina, com CRUD completo
 * (create, loadFromId, loadAll, update, delete, deleteById) e um método
 * adicional loadByEquipe(equipe), que utiliza QueryBuilder do ORMLite
 * para retornar todos os membros associados a uma determinada Equipe.
 *
 * Adicionado na Etapa 2 para suportar a persistência do time montado.
 */

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Repositorio de MembroTime (padrao Repository, mesmo molde dos demais).
 * Cada MembroTime representa um Pokemon dentro de uma Equipe, podendo ter
 * nature e golpe personalizados sem afetar a Pokedex.
 */
public class MembroTimeRepository
{
    private static Database database;
    private static Dao<MembroTime, Integer> dao;
    private List<MembroTime> loadedMembros;

    public MembroTimeRepository(Database database) {
        MembroTimeRepository.setDatabase(database);
        loadedMembros = new ArrayList<MembroTime>();
    }

    public static void setDatabase(Database database) {
        MembroTimeRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), MembroTime.class);
            TableUtils.createTableIfNotExists(database.getConnection(), MembroTime.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public MembroTime create(MembroTime membro) {
        try {
            int nrows = dao.create(membro);
            if (nrows == 0)
                throw new SQLException("Error: object not saved");
            loadedMembros.add(membro);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return membro;
    }

    public MembroTime loadFromId(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public List<MembroTime> loadAll() {
        try {
            this.loadedMembros = dao.queryForAll();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedMembros;
    }

    public int update(MembroTime membro) {
        try {
            return dao.update(membro);
        } catch (SQLException e) {
            System.out.println(e);
            return 0;
        }
    }

    public int delete(MembroTime membro) {
        try {
            return dao.delete(membro);
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

    /** Retorna todos os membros de uma equipe (usado pela tela de time). */
    public List<MembroTime> loadByEquipe(Equipe equipe) {
        try {
            QueryBuilder<MembroTime, Integer> qb = dao.queryBuilder();
            qb.where().eq(MembroTime.COL_EQUIPE, equipe);
            return qb.query();
        } catch (SQLException e) {
            System.out.println(e);
            return new ArrayList<MembroTime>();
        }
    }

    public List<MembroTime> getLoadedMembros() { return loadedMembros; }
}
