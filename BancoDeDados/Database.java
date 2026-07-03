import java.sql.*;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

/**
 * Gerencia a conexao com o banco SQLite via ORMLite.
 * (Mesmo padrao do tutorial do professor.)
 */
/**
     * Cria a classe de conexão apontando para o arquivo SQLite informado.
     * A conexão em si só é aberta quando getConnection() é chamado.
     *
     * @param databaseName caminho do arquivo do banco (ex.: "pokedex.db")
     */
public class Database
{
   private String databaseName = null;
   private JdbcConnectionSource connection = null;

   public Database(String databaseName) {
       this.databaseName = databaseName;
   }
/**
     * Retorna a conexão com o banco. Na primeira chamada, cria a conexão
     * com o SQLite; nas chamadas seguintes, reutiliza a mesma. Isso evita
     * abrir múltiplas conexões concorrentes.
     *
     * @return a conexão ativa do ORMLite com o banco
     * @throws SQLException se o nome do banco for nulo ou houver falha ao abrir
     */
   public JdbcConnectionSource getConnection() throws SQLException {
      if ( databaseName == null ) {
          throw new SQLException("database name is null");
      }
      if ( connection == null ) {
          try {
              connection = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);
          } catch ( Exception e ) {
              System.err.println( e.getClass().getName() + ": " + e.getMessage() );
              System.exit(0);
          }
          System.out.println("Opened database successfully");
      }
      return connection;
   }
 /**
     * Fecha a conexão com o banco, se estiver aberta.
     * Após o fechamento, uma chamada futura a getConnection() abrirá
     * uma conexão nova.
     */
   public void close() {
       if ( connection != null ) {
           try {
               connection.close();
               this.connection = null;
           } catch (java.lang.Exception e) {
               System.err.println(e);
           }
       }
   }
}
