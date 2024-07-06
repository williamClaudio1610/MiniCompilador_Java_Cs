package classdecl;

public class DecClasse {
    private String tipoClasse;
    private String nomeClasse;

    public DecClasse(String tipoClasse, String nomeClasse) {
        this.tipoClasse = tipoClasse;
        this.nomeClasse = nomeClasse;
    }

    public String getTipoClasse() {
        return tipoClasse;
    }

    public void setTipoClasse(String tipoClasse) {
        this.tipoClasse = tipoClasse;
    }

    public String getNomeClasse() {
        return nomeClasse;
    }

    public void setNomeClasse(String nomeClasse) {
        this.nomeClasse = nomeClasse;
    }
}
