package variaveis;

public class Variavel {

    private String tipo;
    private String nome;
    private String Valor;

    public Variavel(String tipo, String nome, String valor) {
        this.tipo = tipo;
        this.nome = nome;
        Valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor() {
        return Valor;
    }

    public void setValor(String valor) {
        Valor = valor;
    }
}
