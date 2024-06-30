package Analisador;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import variaveis.Variavel;

import java.util.List;

public class LeitorFicheiro {

    int numLinha = 0;
    private AnaliseSintatica anSint = new AnaliseSintatica();
    private static final String nomeFIle = "CodigoTeste.txt";
    private AnaLex analex = new AnaLex();

    private HashMap<String, Variavel> variaveis;

    public LeitorFicheiro() {
        variaveis = new HashMap<>();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("O ficheiro não existe.");
        }
    }

}
