package Analisador;

import classdecl.DecClasse;
import variaveis.Variavel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeitorFicheiro {

    int numLinha = 0;
    private AnaliseSintatica anSint;
    private static final String nomeFIle = "CodigoTeste.txt";
    private AnaLex analex = new AnaLex();

    private Map<String, Variavel> variaveis;

    public LeitorFicheiro() {
        variaveis = new HashMap<>();
        anSint = new AnaliseSintatica(variaveis);
    }

    public Map<String, Variavel> getVariaveis() {
        return variaveis;
    }

    public void carregarFicheiroCodigo() {
        File ficheiro = new File(nomeFIle);

        if (ficheiro.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(nomeFIle))) {
                String linhaAtual;
                String linhaSeguinte = br.readLine();

                while ((linhaAtual = linhaSeguinte) != null) {
                    numLinha++;
                    linhaSeguinte = br.readLine();

                    // Verificar se a linha é nula ou vazia
                    if (linhaAtual == null || linhaAtual.trim().isEmpty()) {
                        continue; // Pula para a próxima linha
                    }

                    // Verificar se a linha contém tokens válidos
                    boolean tokenValido = analex.processarLinha(linhaAtual);
                    if (!tokenValido) {
                        anSint.getListaErros().add("Erro na linha " + numLinha + ": Token inválido.");
                        continue; // Pula para a próxima linha
                    }

                    // Analisar erros na linha
                    anSint.AnalisarErroLInha(linhaAtual, numLinha, linhaSeguinte);
                }

                // Imprimir os erros encontrados após ler todas as linhas
                List<String> erros = anSint.getListaErros();
                for (String erro : erros) {
                    System.out.println(erro);
                }
                System.out.printf("\n");
                // exibir a lista de variaveis que foram guardadas
                for (Map.Entry<String, Variavel> entry : anSint.getVariaveis().entrySet()) {
                    String nome = entry.getKey();
                    Variavel variavel = entry.getValue();
                    System.out.println("Nome: " + nome + ", Tipo: " + variavel.getTipo() + ", Valor: " + variavel.getValor());
                }

                System.out.printf("\nLista de classes");
                // exibir a lista de classes que foram guardadas
                for (Map.Entry<String, DecClasse> entry : anSint.getClassDeclaration().entrySet()) {
                    String nome = entry.getKey();
                    DecClasse classes = entry.getValue();
                    System.out.println("Nome: " + classes.getNomeClasse() + ", Tipo: " + classes.getTipoClasse());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("O ficheiro não existe.");
        }
    }
}
