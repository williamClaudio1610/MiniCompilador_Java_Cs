package variaveis;

public class Variavel {

    private String tipo;
    private String nome;
    private String valor;

    public Variavel(String tipo, String nome, String valor) {
        this.tipo = tipo;
        this.nome = nome;
        this.valor = valor;
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
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String retornarTipo(String nomeVar) {
        if (this.nome.equals(nomeVar)) {
            return this.tipo;
        }
        return null;
    }
}
