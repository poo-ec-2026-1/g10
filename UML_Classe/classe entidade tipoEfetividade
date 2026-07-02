import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

/**
 * Tabela de efetividade entre tipos (TypeChart).
 * multiplicador: 2.0 (forte), 0.5 (fraco), 0.0 (imune).
 * So guarda os confrontos != 1.0; o resto e neutro.
 */
@DatabaseTable(tableName = "tipo_efetividade")
public class TipoEfetividade
{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "tipo_atacante")
    private String tipoAtacante;

    @DatabaseField(columnName = "tipo_defensor")
    private String tipoDefensor;

    @DatabaseField
    private double multiplicador;

    public TipoEfetividade() {
    }

    public TipoEfetividade(String tipoAtacante, String tipoDefensor, double multiplicador) {
        this.tipoAtacante = tipoAtacante;
        this.tipoDefensor = tipoDefensor;
        this.multiplicador = multiplicador;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipoAtacante() { return tipoAtacante; }
    public void setTipoAtacante(String tipoAtacante) { this.tipoAtacante = tipoAtacante; }

    public String getTipoDefensor() { return tipoDefensor; }
    public void setTipoDefensor(String tipoDefensor) { this.tipoDefensor = tipoDefensor; }

    public double getMultiplicador() { return multiplicador; }
    public void setMultiplicador(double multiplicador) { this.multiplicador = multiplicador; }
}
