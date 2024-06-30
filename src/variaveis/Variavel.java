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

    public boolean isCompatible(String valor) {
        switch (this.tipo) {
            case "int":
                return valor.matches("\\d+");
            case "float":
                return valor.matches("\\d+\\.\\d+");
            case "string":
                return valor.matches("\".*\"");
            case "bool":
                return valor.equals("true") || valor.equals("false");
            default:
                return false;
        }
    }
}
