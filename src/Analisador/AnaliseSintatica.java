package Analisador;

import classdecl.DecClasse;
import variaveis.Variavel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnaliseSintatica {

    private List<String> listaErros;
    private Stack<Character> stack;
    private String linhaAnterior; // Para armazenar a linha anterior
    private Map<String, Variavel> variaveis;
    private TokenMapper tokens;
    private boolean classeDeclarada = false;
    private Map<String, DecClasse> classDeclaration = new HashMap<>();
    private AnalisadorSemantico anSem;


    public AnaliseSintatica(Map<String, Variavel> variaveis) {
        listaErros = new ArrayList<>();
        stack = new Stack<>();
        linhaAnterior = null; // Inicializa a linha anterior como null
        this.variaveis = variaveis;
        this.anSem = new AnalisadorSemantico(listaErros, variaveis);
    }

    public void AnalisarErroLInha(String linha, int numLinha, String proximaLinha) {
        // Regra 1: Permitir linhas vazias
        if (linha == null || linha.trim().isEmpty()) {
            return;
        }
        // Verificar importação de bibliotecas
        verificarUsingLibImport(linha, numLinha);

        // Verificar escopo da classe
        if (!classeDeclarada) {
            verificarEscopoClasse(linha, numLinha);
        }

        // Se a classe não foi declarada e a linha não é uma declaração de classe, gerar erro
        if (!classeDeclarada && !linha.startsWith("using ") && !linha.matches(".*class\\s+\\w+.*")) {
            listaErros.add("Erro na linha " + numLinha + ": Declaração de classe faltando.");
            return;
        }

        // Se a classe foi declarada, verificar declaração de variáveis e outras estruturas
        if (classeDeclarada) {
            // Verificar declaração de variáveis
            verificarDeclaracaoVariavel(linha, numLinha);

            // Verificar estruturas de controle
            verificarExpressoesBooleanas(linha, numLinha, proximaLinha);

            // Verificar parênteses, chavetas ou colchetes desbalanceados em todo o código
            verificarBalanceamento(linha, numLinha);

            // Verificar se a linha anterior tem uma chaveta de fechamento no início
            if (linhaAnterior != null) {
                verificarChavetaFechamentoNoInicio(linhaAnterior, linha, numLinha);
            }

            // Verificar chamadas de Console.WriteLine e Console.Write
            anSem.verificarFormatString(linha, numLinha);
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
    private void verificarExpressoesBooleanas(String linha, int numLinha, String proximaLinha) {
            String[] estruturas = {"if", "for", "else", "while", "do", "foreach"};
            for (String estrutura : estruturas) {
                // Verificar se a estrutura é uma palavra isolada com parênteses antes e depois
                String regex = "\\b" + estrutura + "\\b\\s*\\(?.*\\)?\\s*\\{?.*";
                if (linha.matches(regex)) {
                    switch (estrutura) {
                        case "if":
                            if (!linha.matches(".*\\bif\\s*\\(.*\\)\\s*\\{?.*")) {
                                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'if' incorreta.");
                            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                                listaErros.add("Erro na linha " + numLinha + ": Bloco 'if' sem abertura de chaveta.");
                            }
                            break;
                        case "for":
                            if (!linha.matches(".*\\bfor\\s*\\(.*;.*;.*\\)\\s*\\{?.*")) {
                                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'for' incorreta.");
                            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                                listaErros.add("Erro na linha " + numLinha + ": Bloco 'for' sem abertura de chaveta.");
                            }
                            break;
                        case "else":
                            if (!linha.matches(".*\\belse\\b\\s*\\{?.*")) {
                                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'else' incorreta.");
                            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                                listaErros.add("Erro na linha " + numLinha + ": Bloco 'else' sem abertura de chaveta.");
                            }
                            break;
                        case "while":
                            if (!linha.matches(".*\\bwhile\\s*\\(.*\\)\\s*\\{?.*")) {
                                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'while' incorreta.");
                            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                                listaErros.add("Erro na linha " + numLinha + ": Bloco 'while' sem abertura de chaveta.");
                            }
                            break;
                        case "do":
                            if (!linha.matches(".*\\bdo\\b\\s*\\{?.*")) {
                                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'do' incorreta.");
                            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                                listaErros.add("Erro na linha " + numLinha + ": Bloco 'do' sem abertura de chaveta.");
                            }
                            break;
                        case "foreach":
                            if (!linha.matches(".*\\bforeach\\s*\\(.*\\)\\s*\\{?.*")) {
                                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'foreach' incorreta.");
                            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                                listaErros.add("Erro na linha " + numLinha + ": Bloco 'foreach' sem abertura de chaveta.");
                            }
                            break;
                    }
                }
            }
    }

    public void verificarUsingLibImport(String linha, int numLinha) {
        if (linha.startsWith("using ")) {
            // Usar regex para verificar a importação correta
            if (!linha.matches("\\busing\\s+[a-zA-Z0-9\\.]+\\s*;")) {
                listaErros.add("Erro na linha " + numLinha + ": Importação de biblioteca incorreta.");
            } else {
                String biblioteca = linha.substring(6).replace(";", "").trim();
                if (biblioteca.isEmpty()) {
                    listaErros.add("Erro na linha " + numLinha + ": Importação de biblioteca vazia.");
                }
            }
        }
    }

    public void verificarEscopoClasse(String linha, int numLinha) {

        // Regex para combinações válidas de escopos e tipos de classe
        String regex = "(public|private|protected( internal)?|internal)?\\s*(static|abstract|sealed)?\\s*class\\s+\\w+\\s*\\{?";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(linha);

        if (!matcher.matches()) {
            listaErros.add("Erro na linha " + numLinha + ": Definição de classe incorreta ou escopo inválido.");
        } else {
            // Extrair o modificador de acesso, tipo de classe e nome da classe
            String[] palavras = linha.split("\\s+");
            String tipoModif = "";
            String tipoClasse = "";
            String nomeClasse = "";

            for (int i = 0; i < palavras.length; i++) {
                // Verificar se a palavra é um modificador de acesso
                if (palavras[i].matches("public|private|protected|internal")) {
                    tipoModif = palavras[i];
                    if (tipoModif.equals("protected") && i + 1 < palavras.length && palavras[i + 1].equals("internal")) {
                        tipoModif = "protected internal";
                        i++; // Pular a próxima palavra já que foi combinada
                    } else if (tipoModif.equals("private") && i + 1 < palavras.length && palavras[i + 1].equals("protected")) {
                        tipoModif = "private protected";
                        i++; // Pular a próxima palavra já que foi combinada
                    }
                }

                // Verificar se a palavra é uma combinação de classe
                if (palavras[i].matches("static|abstract|sealed")) {
                    tipoClasse = palavras[i] + " class";
                    i++; // Pular a próxima palavra já que foi combinada
                } else if (palavras[i].equals("class")) {
                    tipoClasse = "class";
                }

                // Capturar o nome da classe
                if (tipoClasse.contains("class") && i + 1 < palavras.length) {
                    nomeClasse = palavras[i + 1].replaceAll("[^a-zA-Z0-9_]", "");
                    break;
                }
            }

            // Se não encontrou um modificador de acesso, usar "internal" como padrão
            if (tipoModif.isEmpty()) {
                tipoModif = "internal";
            }

            // Verificar se o nome da classe não é um token reservado
            if (TokenMapper.existeToken(nomeClasse)) {
                listaErros.add("Erro na linha " + numLinha + ": Nome da classe '" + nomeClasse + "' é um token reservado.");
            } else {
                classDeclaration.put(nomeClasse, new DecClasse(tipoModif + " " + tipoClasse, nomeClasse));
                classeDeclarada = true;
                System.out.println("Escopo: " + tipoModif + ", Tipo: " + tipoClasse + ", Nome da Classe: " + nomeClasse);
            }
        }
    }





    public List<String> getListaErros() {
        return listaErros;
    }
    public Map<String, Variavel> getVariaveis() {
        return variaveis;
    }
    public Map<String, DecClasse> getClassDeclaration() {
        return classDeclaration;
    }

}
