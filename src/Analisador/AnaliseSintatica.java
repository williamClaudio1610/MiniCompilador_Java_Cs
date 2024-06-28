package Analisador;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AnaliseSintatica {

    private List<String> listaErros;
    private Stack<Character> stack;
    private String linhaAnterior; // Para armazenar a linha anterior

    public AnaliseSintatica() {
        listaErros = new ArrayList<>();
        stack = new Stack<>();
        linhaAnterior = null; // Inicializa a linha anterior como null
    }

    public void AnalisarErroLInha(String linha, int numLinha, String proximaLinha) {
        // Regra 1: Permitir linhas vazias
        if (linha == null || linha.trim().isEmpty()) {
            return;
        }

        // Regra 2: Verificar a estrutura do 'if'
        if (linha.contains("if")) {
            if (!linha.matches(".*if\\s*\\(.*\\)\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'if' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'if' sem abertura de chaveta.");
            }
        }

        // Regra 3: Verificar a estrutura do 'case'
        if (linha.contains("case")) {
            if (!linha.matches(".*case\\s+[^:]+:\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'case' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'case' sem abertura de chaveta.");
            }
        }

        // Regra 4: Verificar a estrutura do 'for'
        if (linha.contains("for")) {
            if (!linha.matches(".*for\\s*\\(.*;.*;.*\\)\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'for' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'for' sem abertura de chaveta.");
            }
        }

        // Regra 5: Verificar a estrutura do 'else'
        if (linha.contains("else")) {
            if (!linha.matches(".*else\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'else' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'else' sem abertura de chaveta.");
            }
        }

        // Regra 6: Verificar a estrutura do 'while'
        if (linha.contains("while")) {
            if (!linha.matches(".*while\\s*\\(.*\\)\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'while' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'while' sem abertura de chaveta.");
            }
        }

        // Regra 7: Verificar a estrutura do 'do-while'
       /* if (linha.contains("do")) {
            if (!linha.matches(".*do\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'do-while' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'do-while' sem abertura de chaveta.");
            }
        }*/

        // Regra 8: Verificar a estrutura do 'foreach'
        if (linha.contains("foreach")) {
            if (!linha.matches(".*foreach\\s*\\(.*\\)\\s*\\{?.*")) {
                listaErros.add("Erro na linha " + numLinha + ": Estrutura 'foreach' incorreta.");
            } else if (!linha.contains("{") && (proximaLinha == null || !proximaLinha.trim().startsWith("{"))) {
                listaErros.add("Erro na linha " + numLinha + ": Bloco 'foreach' sem abertura de chaveta.");
            }
        }

        // Regra 9: Verificar declaração de variáveis
        verificarDeclaracaoVariavel(linha, numLinha);

        // Regra 10: Verificar parênteses, chavetas ou colchetes desbalanceados em todo o código
        verificarBalanceamento(linha, numLinha);

        // Verifica se a linha anterior tem uma chaveta de fechamento no início
        if (linhaAnterior != null) {
            verificarChavetaFechamentoNoInicio(linhaAnterior, linha, numLinha);
        }

        // Atualiza a linha anterior
        linhaAnterior = linha;
    }

    private void verificarDeclaracaoVariavel(String linha, int numLinha) {
        String[] tipos = {
                "bool", "byte", "char", "decimal", "double", "float", "int", "long", "object",
                "sbyte", "short", "string", "uint", "ulong", "ushort"
        };

        for (String tipo : tipos) {
            if (linha.contains(tipo)) {
                if (!linha.matches("\\s*" + tipo + "\\s+\\w+\\s*(=\\s*[^;]+)?\\s*;\\s*")) {
                    listaErros.add("Erro na linha " + numLinha + ": Declaração de variável '" + tipo + "' incorreta.");
                }
                break; // Para evitar múltiplas verificações para a mesma linha
            }
        }
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
}