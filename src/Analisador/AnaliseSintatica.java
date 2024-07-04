package Analisador;

import variaveis.Variavel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class AnaliseSintatica {

    private List<String> listaErros;
    private Stack<Character> stack;
    private String linhaAnterior; // Para armazenar a linha anterior
    private Map<String, Variavel> variaveis;

    private TokenMapper tokens;


    public AnaliseSintatica(Map<String, Variavel> variaveis) {
        listaErros = new ArrayList<>();
        stack = new Stack<>();
        linhaAnterior = null; // Inicializa a linha anterior como null
        this.variaveis = variaveis;
    }

    public void AnalisarErroLInha(String linha, int numLinha, String proximaLinha) {
        // Regra 1: Permitir linhas vazias
        if (linha == null || linha.trim().isEmpty()) {
            return;
        }

        // Verificar declaração de variáveis
        verificarDeclaracaoVariavel(linha, numLinha);

        // Verificar a estrutura do 'if'
        if (linha.contains("if")) {
            if (!linha.matches(".*\\bif\\s*\\(.*\\)\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'if' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'if' sem abertura de chaveta.");
            }
        }

        // Verificar a estrutura do 'for'
        if (linha.contains("for")) {
            if (!linha.matches(".*\\bfor\\s*\\(.*;.*;.*\\)\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'for' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'for' sem abertura de chaveta.");
            }
        }

        // Verificar a estrutura do 'else'
        if (linha.contains("else")) {
            if (!linha.matches(".*else\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'else' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'else' sem abertura de chaveta.");
            }
        }

        // Verificar a estrutura do 'while'
        if (linha.contains("while")) {
            if (!linha.matches(".*\\bwhile\\s*\\(.*\\)\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'while' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'while' sem abertura de chaveta.");
            }
        }

        // Verificar a estrutura do 'do-while'
        if (linha.matches(".*\\bdo\\b.*")) {
            if (!linha.matches(".*\\bdo\\b\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'do' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'do' sem abertura de chaveta.");
            }
        }

        // Verificar a estrutura do 'foreach'
        if (linha.contains("foreach")) {
            if (!linha.matches(".*\\bforeach\\s*\\(.*\\)\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'foreach' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'foreach' sem abertura de chaveta.");
            }
        }

        // Verificar uso de variáveis e compatibilidade de tipos
        //verificarCompatibilidadeDeTipos(linha, numLinha);

        // Verificar parênteses, chavetas ou colchetes desbalanceados em todo o código
        verificarBalanceamento(linha, numLinha);

        // Verificar se a linha anterior tem uma chaveta de fechamento no início
        if (linhaAnterior != null) {
            verificarChavetaFechamentoNoInicio(linhaAnterior, linha, numLinha);
        }

        // Atualizar a linha anterior
        linhaAnterior = linha;
    }

    private void verificarDeclaracaoVariavel(String linha, int numLinha) {
        String[] tipos = {
                "bool", "Bool", "byte", "Byte", "char", "Char", "decimal", "Decimal", "double", "Double", "float", "Float", "int", "Int", "long", "Long", "object", "Object",
                "sbyte", "Sbyte", "short", "Short", "string", "String", "uint", "Uint", "ulong", "Ulong", "ushort", "Ushort"
        };

        String[] modificadores = {
                "public", "private", "protected", "internal", "static", "readonly", "const"
        };

        String tipoEncontrado = null;
        String nomeVariavel = null;
        String valorVariavel = null;

        // Dividir a linha em palavras, preservando caracteres especiais para análise posterior
        String[] palavras = linha.split(" ");
        for (int i = 0; i < palavras.length; i++) {
            String palavra = palavras[i];

            // Verificar se é um modificador
            boolean isModificador = false;
            for (String modificador : modificadores) {
                if (palavra.equals(modificador)) {
                    isModificador = true;
                    break;
                }
            }

            // Verificar se é um tipo
            boolean isTipo = false;
            for (String tipo : tipos) {
                if (palavra.equals(tipo)) {
                    tipoEncontrado = tipo;
                    isTipo = true;
                    break;
                }
            }

            if (isModificador || isTipo) {
                continue;
            }

            // Se encontrou um tipo, a próxima palavra deve ser o nome da variável
            if (tipoEncontrado != null && nomeVariavel == null) {
                nomeVariavel = palavra.replaceAll("[^a-zA-Z0-9_]", "");
                if (tokens.existeToken(nomeVariavel)) {
                    listaErros.add("Erro na linha " + numLinha + ": Uma palavra reservada não pode ser uma variável.");
                    break;
                }
            } else if (nomeVariavel != null && linha.contains("=")) {
                // Encontrar o valor da variável
                int igualIndex = linha.indexOf("=");
                valorVariavel = linha.substring(igualIndex + 1).replace(";", "").trim();
                break;
            }
        }

        // Verificar se a declaração está correta
        if (tipoEncontrado != null && nomeVariavel != null) {
            if (variaveis.containsKey(nomeVariavel)) {
                listaErros.add("Erro na linha " + numLinha + ": Variável '" + nomeVariavel + "' já declarada.");
            } else {
                if (valorVariavel != null && !verificarCompatibilidadeDeTipos(tipoEncontrado, nomeVariavel, valorVariavel)) {
                    listaErros.add("Erro na linha " + numLinha + ": Incompatibilidade de tipo. Variável '" + nomeVariavel + "' deve ser do tipo " + tipoEncontrado + ".");
                } else {
                    variaveis.put(nomeVariavel, new Variavel(tipoEncontrado, nomeVariavel, valorVariavel));
                }
            }
        } else if (tipoEncontrado != null) {
            listaErros.add("Erro na linha " + numLinha + ": Declaração de variável incorreta.");
        }

        // Verificar atribuição de valor para variáveis já declaradas
        //verificarAtribuicaoValor(linha, numLinha);
    }




    private boolean isModificador(String token) {
        String[] modificadores = {
                "public", "private", "protected", "internal", "static", "readonly", "const"
        };
        for (String mod : modificadores) {
            if (mod.equals(token)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTipo(String token) {
        String[] tipos = {
                "bool", "byte", "char", "decimal", "double", "float", "int", "long", "object",
                "sbyte", "short", "string", "uint", "ulong", "ushort"
        };
        for (String tipo : tipos) {
            if (tipo.equals(token)) {
                return true;
            }
        }
        return false;
    }

    private void verificarAtribuicaoValor(String linha, int numLinha) {
        // Ignorar linhas que contém Console
        if (linha.contains("Console")) {
            return;
        }

        // Verificar atribuição de valor
        if (linha.contains("=")) {
            // Percorrer a linha para encontrar a variável no HashMap
            StringBuilder nomeBuilder = new StringBuilder();
            StringBuilder valorBuilder = new StringBuilder();
            boolean encontrouIgual = false;

            for (int i = 0; i < linha.length(); i++) {
                char ch = linha.charAt(i);

                if (ch == '=') {
                    encontrouIgual = true;
                    continue;
                }

                if (!encontrouIgual) {
                    // Ignorar espaços em branco antes do nome da variável
                    if (Character.isWhitespace(ch)) {
                        continue;
                    }
                    nomeBuilder.append(ch);
                } else {
                    valorBuilder.append(ch);
                }
            }

            String nome = nomeBuilder.toString().trim();
            String valor = valorBuilder.toString().trim().replace(";", "");

            if (variaveis.containsKey(nome)) {
                Variavel variavel = variaveis.get(nome);
                // Atualizar o valor da variável no mapa
                variavel.setValor(valor);
            } else {
                listaErros.add("Erro na linha " + numLinha + ": Variável '" + nome + "' não declarada.");
            }
        }
    }




    private boolean verificarCompatibilidadeDeTipos(String tipo, String nome, String valor) {
        // Remover espaços em branco desnecessários
        valor = valor.replaceAll("\\s+", "");

        // Se o valor é uma expressão aritmética
        String[] tokens = valor.split("[\\+\\-\\*/]");
        for (String token : tokens) {
            token = token.trim();
            if (token.matches("\\d+")) {
                // É um número inteiro, válido para tipos int, float, double, etc.
                if (!tipo.equals("int") && !tipo.equals("float") && !tipo.equals("double") && !tipo.equals("long") && !tipo.equals("short") && !tipo.equals("byte")) {
                    return false;
                }
            } else if (token.matches("\\d+\\.\\d+")) {
                // É um número decimal, válido para tipos float, double, etc.
                if (!tipo.equals("float") && !tipo.equals("double") && !tipo.equals("decimal")) {
                    return false;
                }
            } else if (token.matches("\".*\"")) {
                // É uma string, válida para tipos string
                if (!tipo.toLowerCase().equals("string")) {
                    return false;
                }
            } else if (token.matches("'.'")) {
                // É um caractere, válido para tipos char
                if (!tipo.toLowerCase().equals("char")) {
                    return false;
                }
            } else if (variaveis.containsKey(token)) {
                // É uma variável, verificar o tipo
                Variavel variavel = variaveis.get(token);
                String tipoVariavel = variavel.getTipo();
                if (!tipo.equals(tipoVariavel)) {
                    return false;
                }
            } else {
                // Token não é uma variável conhecida ou um valor válido, portanto, é inválido
                return false;
            }
        }

        // Se passou por todas as verificações, é compatível
        return true;
    }



    private void verificarBalanceamento(String linha, int numLinha) {
        for (char ch : linha.toCharArray()) {
            switch (ch) {
                case '(':
                case '{':
                case '[':
                    stack.push(ch);
                    break;
                case ')':
                    if (stack.isEmpty() || stack.pop() != '(') {
                        listaErros.add("Erro na linha " + numLinha + ": Parênteses desbalanceados.");
                    }
                    break;
                case '}':
                    if (stack.isEmpty() || stack.pop() != '{') {
                        listaErros.add("Erro na linha " + numLinha + ": Chavetas desbalanceadas.");
                    }
                    break;
                case ']':
                    if (stack.isEmpty() || stack.pop() != '[') {
                        listaErros.add("Erro na linha " + numLinha + ": Colchetes desbalanceados.");
                    }
                    break;
            }
        }
    }

    private void verificarChavetaFechamentoNoInicio(String linhaAnterior, String linhaAtual, int numLinha) {
        if (linhaAnterior.trim().endsWith("{") && linhaAtual.trim().startsWith("}")) {
            stack.pop(); // Remover a chaveta de abertura adicionada na linha anterior
        }
    }

    public void verificarBalanceamentoFinal(int ultimaLinha) {
        if (!stack.isEmpty()) {
            listaErros.add("Erro: Bloco desbalanceado até a linha " + ultimaLinha + ".");
        }
    }

    public List<String> getListaErros() {
        return listaErros;
    }
    public Map<String, Variavel> getVariaveis() {
        return variaveis;
    }
}
