public class Teste
{
    public static void main(String[] args) {
        System.out.println("funcionou!");
        
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite OK");
        } catch (Exception e) {
            System.out.println("SQLite NAO encontrado: " + e.getMessage());
        }
        
        try {
            Class.forName("com.j256.ormlite.jdbc.JdbcConnectionSource");
            System.out.println("ORMLite OK");
        } catch (Exception e) {
            System.out.println("ORMLite NAO encontrado: " + e.getMessage());
        }
    }
}