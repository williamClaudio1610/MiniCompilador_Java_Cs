package Analisador;

import variaveis.Variavel;

import java.util.Arrays;
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
    public void analisarExpressaoBooleana(String linha, int numLinha) {
        String[] tokens = linha.split("(?<=[-+*/=<>!])|(?=[-+*/=<>!])");

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

    public void ifStatement(String linha, int numLinha) {
        String[] operadoresArit = {"==", "<=", ">=", "!", "!=", ">", "<"};
        String[] operadoresBoolean = {"&&", "||"};

        boolean ifEncontrado = false;
        boolean abrirParenteseEncontrado = false;
        boolean fecharParentesesEncontrado = false;
        String nomeVar1 = "";
        String operador = "";
        String nomeVar2 = "";
        String tipoDado1 = "";
        String tipoDado2 = "";

        // Remover espaços extras e dividir a linha em tokens
        String[] tokens = linha.trim().split("\\s+");

        for (int i = 0; i < tokens.length; i++) {
            String currentToken = tokens[i];

            // Verificar se a estrutura é 'if'
            if (currentToken.equals("if")) {
                ifEncontrado = true;
                continue;
            }

            // Verificar se encontrou o parêntese de abertura
            if (ifEncontrado && currentToken.startsWith("(")) {
                abrirParenteseEncontrado = true;
                currentToken = currentToken.substring(1); // Remover o parêntese de abertura
            }

            // Coletar a primeira variável ou valor
            if (abrirParenteseEncontrado && nomeVar1.isEmpty()) {
                if (variaveis.containsKey(currentToken)) {
                    nomeVar1 = currentToken;
                    tipoDado1 = variaveis.get(currentToken).getTipo();
                } else {
                    nomeVar1 = currentToken;
                    tipoDado1 = inferirTipoDado(currentToken);
                }
                continue;
            }

            // Coletar o operador
            if (!nomeVar1.isEmpty() && operador.isEmpty()) {
                for (String op : operadoresArit) {
                    if (currentToken.equals(op)) {
                        operador = currentToken;
                        break;
                    }
                }
                for (String op : operadoresBoolean) {
                    if (currentToken.equals(op)) {
                        operador = currentToken;
                        break;
                    }
                }
                if (!operador.isEmpty()) continue;
            }

            // Coletar a segunda variável ou valor
            if (!operador.isEmpty() && nomeVar2.isEmpty()) {
                if (variaveis.containsKey(currentToken)) {
                    nomeVar2 = currentToken;
                    tipoDado2 = variaveis.get(currentToken).getTipo();
                } else {
                    nomeVar2 = currentToken;
                    tipoDado2 = inferirTipoDado(currentToken);
                }
                // Verificar se o próximo token contém o parêntese de fechamento
                if (i + 1 < tokens.length && tokens[i + 1].endsWith(")")) {
                    fecharParentesesEncontrado = true;
                    tokens[i + 1] = tokens[i + 1].substring(0, tokens[i + 1].length() - 1); // Remover o parêntese de fechamento
                }
                break;
            }
        }

        // Verificar se as variáveis e o operador foram identificados corretamente
        if (nomeVar1.isEmpty() || (!operador.isEmpty() && nomeVar2.isEmpty() && !fecharParentesesEncontrado)) {
            listaErros.add("Erro na linha " + numLinha + ": Estrutura 'if' incorreta ou variável/valor não declarada.");
        } else if (operador.isEmpty()) {
            // Verificação para uma única variável (e.g., if (trueFalseVar) ou if (!trueFalseVar))
            if (!tipoDado1.equals("boolean") && !tipoDado1.equals("bool")) {
                listaErros.add("Erro na linha " + numLinha + ": Tipo incompatível para expressão booleana. Esperado 'bool' mas encontrado '" + tipoDado1 + "'.");
            }
        } else {
            // Verificar compatibilidade dos tipos de dados para operadores aritméticos e booleanos
            boolean operadorAritmetico = false;
            for (String op : operadoresArit) {
                if (operador.equals(op)) {
                    operadorAritmetico = true;
                    break;
                }
            }

            boolean operadorBooleano = false;
            for (String op : operadoresBoolean) {
                if (operador.equals(op)) {
                    operadorBooleano = true;
                    break;
                }
            }

            if (operadorAritmetico && !tipoDado1.equals(tipoDado2)) {
                listaErros.add("Erro na linha " + numLinha + ": Incompatibilidade de tipos entre '" + nomeVar1 + "' e '" + nomeVar2 + "'.");
            } else if (operadorBooleano && (!tipoDado1.equals("boolean") || !tipoDado2.equals("boolean"))) {
                listaErros.add("Erro na linha " + numLinha + ": Operadores booleanos requerem variáveis do tipo 'bool'.");
            }
        }
    }

    public void whileStatement(String linha, int numLinha){

    }
    public void foreachStatement(String linha, int numLinha) {

        boolean foreachEncontrado = false;
        boolean abrirParentesesEncontrado = false;
        boolean fecharParentesesEncontrado = false;
        boolean inEncontrado = false;
        String tipoVariavel = "";
        String nomeVariavel = "";
        String colecao = "";

        // Remover espaços extras e dividir a linha em tokens
        String[] tokens = linha.trim().split("\\s+");

        for (int i = 0; i < tokens.length; i++) {
            String currentToken = tokens[i];

            // Verificar se a estrutura é 'foreach'
            if (currentToken.equals("foreach")) {
                foreachEncontrado = true;
                continue;
            }

            // Verificar se encontrou o parêntese de abertura
            if (foreachEncontrado && currentToken.startsWith("(")) {
                abrirParentesesEncontrado = true;
                currentToken = currentToken.substring(1); // Remover o parêntese de abertura
            }

            // Coletar o tipo da variável e a variável em si
            if (abrirParentesesEncontrado && tipoVariavel.isEmpty()) {
                tipoVariavel = currentToken;
                if (i + 1 < tokens.length) {
                    nomeVariavel = tokens[i + 1].replaceAll("[^a-zA-Z0-9_]", "");
                    i++; // Avançar um token adicional
                }
                continue;
            }

            // Verificar se encontrou a palavra-chave 'in'
            if (!tipoVariavel.isEmpty() && currentToken.equals("in")) {
                inEncontrado = true;
                continue;
            }

            // Coletar a coleção sobre a qual iterar
            if (inEncontrado && !fecharParentesesEncontrado) {
                colecao = currentToken.replaceAll("[^a-zA-Z0-9_]", "");
                // Verificar se o próximo token contém o parêntese de fechamento
                if (i + 1 < tokens.length && tokens[i + 1].endsWith(")")) {
                    fecharParentesesEncontrado = true;
                    tokens[i + 1] = tokens[i + 1].substring(0, tokens[i + 1].length() - 1); // Remover o parêntese de fechamento
                }
                continue;
            }
        }

        // Verificar se a estrutura do 'foreach' está correta
        if (!foreachEncontrado || !abrirParentesesEncontrado || !fecharParentesesEncontrado || nomeVariavel.isEmpty() || colecao.isEmpty()) {
            listaErros.add("Erro na linha " + numLinha + ": Estrutura 'foreach' incorreta.");
        } else {
            // Verificar se o tipo é válido
            if (!tipoVariavel.matches("bool|Bool|byte|Byte|char|Char|decimal|Decimal|double|Double|float|Float|int|Int|long|Long|object|Object|sbyte|Sbyte|short|Short|string|String|uint|Uint|ulong|Ulong|ushort|Ushort")) {
                listaErros.add("Erro na linha " + numLinha + ": Tipo de dado '" + tipoVariavel + "' inválido.");
                return;
            }

            // Verificar se a variável é válida
            if (!nomeVariavel.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                listaErros.add("Erro na linha " + numLinha + ": Nome da variável '" + nomeVariavel + "' inválido.");
            } else if (variaveis.containsKey(nomeVariavel)) {
                listaErros.add("Erro na linha " + numLinha + ": Variável '" + nomeVariavel + "' já declarada.");
            } else {
                variaveis.put(nomeVariavel, new Variavel(tipoVariavel, nomeVariavel, null)); // Adicionar a variável do loop ao mapa de variáveis
            }

            // Verificar se o nome da coleção é aceitável
            if (!colecao.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                listaErros.add("Erro na linha " + numLinha + ": Nome da coleção '" + colecao + "' é inválido.");
            } else if (variaveis.containsKey(colecao)) {
                listaErros.add("Erro na linha " + numLinha + ": Nome da coleção '" + colecao + "' já está sendo usado como variável.");
            }
        }
    }




    private String inferirTipoDado(String valor) {
        if (valor.matches("\\d+")) {
            return "int";
        } else if (valor.matches("\\d+\\.\\d+")) {
            return "double";
        } else if (valor.matches("\".*\"")) {
            return "string";
        } else if (valor.matches("true|false")) {
            return "bool";
        } else if (valor.matches("\\d+L")) {
            return "long";
        } else if (valor.matches("\\d+F")) {
            return "float";
        } else if (valor.matches("\\d+D")) {
            return "double";
        } else if (valor.matches("\\d+M")) {
            return "decimal";
        } else if (valor.matches("'[a-zA-Z]'")) {
            return "char";
        } else if (valor.matches("0|1")) {
            return "byte";
        }
        return "desconhecido";
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
