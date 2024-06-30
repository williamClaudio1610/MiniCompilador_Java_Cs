package Analisador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import variaveis.Variavel;


public class AnaLex {

    private static final String nomeFIle = "CodigoTeste.txt";
    private static final Pattern tokenPattern = Pattern.compile("Console\\.WriteLine|\\b\\w+\\b|[(){};.,+\\-*/\\[\\]=!><]|console\\.(\\w+)|Console\\.(WriteLine|ReadLine|Write|Read)|MessageBox\\.(Show|ShowDialog)|string\\.Format|Math\\.(Abs|Max|Min)|Array\\.Sort|outra_funcao");
    private boolean proximoIdentificador = false;
    private String tipoIdentificador = "";


    public boolean processarLinha(String linha) {
        Matcher matcher = tokenPattern.matcher(linha);

        while (matcher.find()) {
            String tokenStr = matcher.group().trim();
            if (!tokenStr.isEmpty()) {
                if (proximoIdentificador) {
                    //System.out.println("Token: " + tokenStr + ", Tipo: Identificador, Tipo de Identificador: " + tipoIdentificador);
                    proximoIdentificador = false; // Resetando para o próximo token não ser tratado como identificador
                    tipoIdentificador = ""; // Resetando o tipo de identificador
                    return true;
                } else {
                    if (TokenMapper.getPegaToken().containsKey(tokenStr)) {
                        TokenMapper.Tokens token = TokenMapper.getToken(tokenStr);
                        if (token != null) {
                            //System.out.println("Token: " + tokenStr + ", Tipo: " + token);
                            return true;
                        }
                    } else {
                        // Verifica se é um tipo de dado e marca o próximo token como identificador
                        switch (tokenStr) {
                            case "bool":
                            case "byte":
                            case "char":
                            case "decimal":
                            case "double":
                            case "float":
                            case "int":
                            case "long":
                            case "object":
                            case "sbyte":
                            case "short":
                            case "string":
                            case "uint":
                            case "ulong":
                            case "ushort":
                                proximoIdentificador = true;
                                tipoIdentificador = tokenStr;
                                //System.out.println("Token: " + tokenStr + ", Tipo: Tipo de Dados");
                                return true;
                            default:
                                //System.out.println("Token: " + tokenStr + ", Tipo: Desconhecido");
                                return false;
                        }
                    }
                }
            }
        }
        return false;
    }
}