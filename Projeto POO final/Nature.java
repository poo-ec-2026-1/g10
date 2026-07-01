import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

/**
 * Nature: sobe uma stat e baixa outra (HP nunca muda).
 * As neutras tem statAumentada/statReduzida nulos.
 */
@DatabaseTable(tableName = "nature")
public class Nature
{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String nome;

    @DatabaseField(columnName = "stat_aumentada", canBeNull = true)
    private String statAumentada;

    @DatabaseField(columnName = "stat_reduzida", canBeNull = true)
    private String statReduzida;

    public Nature() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getStatAumentada() { return statAumentada; }
    public void setStatAumentada(String statAumentada) { this.statAumentada = statAumentada; }

    public String getStatReduzida() { return statReduzida; }
    public void setStatReduzida(String statReduzida) { this.statReduzida = statReduzida; }
}
