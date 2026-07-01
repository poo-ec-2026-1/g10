import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

/**
 * Entidade Pokemon (POJO + anotacoes ORMLite).
 * Plana, como a Student do tutorial. Mapeia direto as colunas do CSV.
 */
@DatabaseTable(tableName = "pokemon")
public class Pokemon
{
    @DatabaseField(id = true)                  // id = numero da Pokedex (vem do CSV)
    private int id;

    @DatabaseField
    private String nome;

    @DatabaseField
    private int hp;

    @DatabaseField
    private int ataque;

    @DatabaseField
    private int defesa;

    @DatabaseField(columnName = "sp_atk")
    private int spAtk;

    @DatabaseField(columnName = "sp_def")
    private int spDef;

    @DatabaseField
    private int velocidade;

    @DatabaseField(columnName = "sprite_url")
    private String spriteUrl;

    @DatabaseField(columnName = "tipo_1")
    private String tipo1;

    @DatabaseField(columnName = "tipo_2", canBeNull = true)   // pode ser nulo
    private String tipo2;

    @DatabaseField
    private String golpe;                       // golpe caracteristico (so visual)

    /** Construtor sem-argumento exigido pelo ORMLite. */
    public Pokemon() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }

    public int getAtaque() { return ataque; }
    public void setAtaque(int ataque) { this.ataque = ataque; }

    public int getDefesa() { return defesa; }
    public void setDefesa(int defesa) { this.defesa = defesa; }

    public int getSpAtk() { return spAtk; }
    public void setSpAtk(int spAtk) { this.spAtk = spAtk; }

    public int getSpDef() { return spDef; }
    public void setSpDef(int spDef) { this.spDef = spDef; }

    public int getVelocidade() { return velocidade; }
    public void setVelocidade(int velocidade) { this.velocidade = velocidade; }

    public String getSpriteUrl() { return spriteUrl; }
    public void setSpriteUrl(String spriteUrl) { this.spriteUrl = spriteUrl; }

    public String getTipo1() { return tipo1; }
    public void setTipo1(String tipo1) { this.tipo1 = tipo1; }

    public String getTipo2() { return tipo2; }
    public void setTipo2(String tipo2) { this.tipo2 = tipo2; }

    public String getGolpe() { return golpe; }
    public void setGolpe(String golpe) { this.golpe = golpe; }

    /** Atalho pra UI: "Fire" ou "Fire / Flying". */
    public String getTiposFormatados() {
        return (tipo2 == null || tipo2.isEmpty()) ? tipo1 : tipo1 + " / " + tipo2;
    }
}
