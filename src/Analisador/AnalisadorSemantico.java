package Analisador;

import variaveis.Variavel;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalisadorSemantico {
    private List<String> listaErros;
    private Map<String, Variavel> variaveis;

    public AnalisadorSemantico(List<String> listaErros, Map<String, Variavel> variaveis) {
        this.listaErros = listaErros;
        this.variaveis = variaveis;
    }

    // Método para analisar expressões booleanas
    public void analisarExpressaoBooleana(String expressao, int numLinha) {
        String[] tokens = expressao.split("(?<=[-+*/=<>!])|(?=[-+*/=<>!])");

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            if (isOperador(token)) {
                continue;
            } else if (isNumero(token)) {
                continue;
            } else if (token.matches("\".*\"")) {
                continue;
            } else if (variaveis.containsKey(token)) {
                Variavel variavel = variaveis.get(token);
                if (variavel == null) {
                    listaErros.add("Erro na linha " + numLinha + ": Variável '" + token + "' não declarada.");
                }
            } else {
                listaErros.add("Erro na linha " + numLinha + ": Token desconhecido '" + token + "'.");
            }
        }
    }

    // Método para verificar se o token é um operador
    private boolean isOperador(String token) {
        String[] operadores = {"+", "-", "*", "/", "=", "==", "!=", "<", ">", "<=", ">=", "&&", "||"};
        for (String operador : operadores) {
            if (token.equals(operador)) {
                return true;
            }
        }
        return false;
    }

    // Método para verificar se o token é um número
    private boolean isNumero(String token) {
        return token.matches("\\d+") || token.matches("\\d+\\.\\d+");
    }

    // Método para analisar estruturas booleanas como if, while, etc.
    public void analisarEstrutura(String linha, int numLinha) {
        Pattern pattern = Pattern.compile("(if|while)\\s*\\((.*)\\)");
        Matcher matcher = pattern.matcher(linha);

        if (matcher.find()) {
            String expressao = matcher.group(2).trim();
            analisarExpressaoBooleana(expressao, numLinha);
        }
    }

    // Método para verificar o formato das strings em Console.WriteLine e Console.Write
    public void verificarFormatString(String linha, int numLinha) {
        if (linha.contains("Console.WriteLine") || linha.contains("Console.Write")) {
            int countSpecifiers = contarEspecificadores(linha, "%d") + contarEspecificadores(linha, "%f");
            int countVariables = contarVariaveis(linha);

            if (countSpecifiers != countVariables) {
                listaErros.add("Erro na linha " + numLinha + ": Número de especificadores de formato (%d, %f) não corresponde ao número de variáveis.");
            }
        }
    }

    // Método para contar o número de especificadores de formato (%d, %f) na linha
    private int contarEspecificadores(String linha, String especificador) {
        int count = 0;
        int index = 0;
        while ((index = linha.indexOf(especificador, index)) != -1) {
            count++;
            index += especificador.length();
        }
        return count;
    }

    // Método para contar o número de variáveis na linha após a vírgula
    private int contarVariaveis(String linha) {
        int count = 0;
        int startIndex = linha.indexOf("Console.WriteLine") != -1 ? linha.indexOf("Console.WriteLine") : linha.indexOf("Console.Write");
        if (startIndex != -1) {
            startIndex = linha.indexOf(",", startIndex);
            if (startIndex != -1) {
                String variaveisParte = linha.substring(startIndex + 1);
                String[] variaveis = variaveisParte.split(",");
                count = variaveis.length;
            }
        }
        return count;
    }
}
