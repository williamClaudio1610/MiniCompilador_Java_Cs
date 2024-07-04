package Analisador;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TokenMapper {

    public enum Tokens{
        TOKEN_PontoVirgula,
        TOKEN_Ponto,
        TOKEN_AbreParentesesCurvo,
        TOKEN_FechaParentesesCurvo,
        TOKEN_Soma,
        TOKEN_Subtracao,
        TOKEN_Multiplicacao,
        TOKEN_Divisao,
        TOKEN_Identificador,
        TOKEN_NumeroInteiro,
        TOKEN_NumeroDecimal,
        TOKEN_String,
        TOKEN_Numero,
        TOKEN_AbrirChaveta,
        TOKEN_FecharChaveta,
        TOKEN_AbreParentesesRetos,
        TOKEN_FechaParentesesRetos,
        TOKEN_PalavraChave,
        TOKEN_Desconhecido,
        TOKEN_SubtracaoIgual,
        TOKEN_SomaIgual,
        TOKEN_IgualDUplo,
        TOKEN_Igual,
        TOKEN_MaiorIgual,
        TOKEN_MenorIgual,
        TOKEN_Incremento,
        TOKEN_Decremento,
        TOKEN_MultiplicacaoIgual,
        TOKEN_DivisaoIgual,
        TOKEN_ModuloIgual,
        TOKEN_Diferente,
        TOKEN_TipoDado,
        TOKEN_As,
        TOKEN_Async,
        TOKEN_Await,
        TOKEN_Base,
        TOKEN_Bool,
        TOKEN_Break,
        TOKEN_Case,
        TOKEN_Catch,
        TOKEN_Char,
        TOKEN_Checked,
        TOKEN_Class,
        TOKEN_Const,
        TOKEN_Continue,
        TOKEN_Decimal,
        TOKEN_Default,
        TOKEN_Delegate,
        TOKEN_Do,
        TOKEN_Double,
        TOKEN_Else,
        TOKEN_Enum,
        TOKEN_Event,
        TOKEN_Explicit,
        TOKEN_Extern,
        TOKEN_False,
        TOKEN_Finally,
        TOKEN_Fixed,
        TOKEN_For,
        TOKEN_Foreach,
        TOKEN_Goto,
        TOKEN_If,
        TOKEN_Implicit,
        TOKEN_In,
        TOKEN_Interface,
        TOKEN_Internal,
        TOKEN_Is,
        TOKEN_Lock,
        TOKEN_Long,
        TOKEN_Namespace,
        TOKEN_New,
        TOKEN_Null,
        TOKEN_Object,
        TOKEN_Operator,
        TOKEN_Out,
        TOKEN_Override,
        TOKEN_Params,
        TOKEN_Private,
        TOKEN_Protected,
        TOKEN_Public,
        TOKEN_Readonly,
        TOKEN_Ref,
        TOKEN_Return,
        TOKEN_Sbyte,
        TOKEN_Sealed,
        TOKEN_Short,
        TOKEN_Sizeof,
        TOKEN_Main,
        TOKEN_Maior,
        TOKEN_Menor,
        TOKEN_Stackalloc,
        TOKEN_Static,
        TOKEN_StringKeyword,
        TOKEN_Struct,
        TOKEN_Switch,
        TOKEN_This,
        TOKEN_Throw,
        TOKEN_True,
        TOKEN_Try,
        TOKEN_Typeof,
        TOKEN_Uint,
        TOKEN_Ulong,
        TOKEN_Unchecked,
        TOKEN_Unsafe,
        TOKEN_Ushort,
        TOKEN_Using,
        TOKEN_Virtual,
        TOKEN_Void,
        TOKEN_Volatile,
        TOKEN_While,
        TOKEN_Abstract,
        TOKEN_Byte,
        TOKEN_Float,
        TOKEN_Int,
        TOKEN_System,
        TOKEN_ConsoleFunction;
    }

    private static final Map<String, Tokens> pegaToken = new HashMap<>();
    static{
        pegaToken.put("(", Tokens.TOKEN_AbreParentesesCurvo);
        pegaToken.put(")", Tokens.TOKEN_FechaParentesesCurvo);
        pegaToken.put("+", Tokens.TOKEN_Soma);
        pegaToken.put(";", Tokens.TOKEN_PontoVirgula);
        pegaToken.put(".", Tokens.TOKEN_Ponto);
        pegaToken.put("-", Tokens.TOKEN_Subtracao);
        pegaToken.put("*", Tokens.TOKEN_Multiplicacao);
        pegaToken.put("/", Tokens.TOKEN_Divisao);
        pegaToken.put("[a-zA-Z]+", Tokens.TOKEN_Identificador);
        pegaToken.put("\\d+", Tokens.TOKEN_NumeroInteiro);
        pegaToken.put("\\d+\\.\\d+", Tokens.TOKEN_NumeroDecimal);
        pegaToken.put("\".*?\"", Tokens.TOKEN_String);
        pegaToken.put("[0-9]+", Tokens.TOKEN_Numero);
        pegaToken.put("{", Tokens.TOKEN_AbrirChaveta);
        pegaToken.put("}", Tokens.TOKEN_FecharChaveta);
        pegaToken.put("\\[", Tokens.TOKEN_AbreParentesesRetos);
        pegaToken.put("\\]", Tokens.TOKEN_FechaParentesesRetos);
        pegaToken.put("==", Tokens.TOKEN_IgualDUplo);
        pegaToken.put("=", Tokens.TOKEN_Igual);
        pegaToken.put("!=", Tokens.TOKEN_Diferente);
        pegaToken.put(">=", Tokens.TOKEN_MaiorIgual);
        pegaToken.put(">", Tokens.TOKEN_Maior);
        pegaToken.put("<", Tokens.TOKEN_Menor);
        pegaToken.put("<=", Tokens.TOKEN_MenorIgual);
        pegaToken.put("++", Tokens.TOKEN_Incremento);
        pegaToken.put("--", Tokens.TOKEN_Decremento);
        pegaToken.put("+=", Tokens.TOKEN_SomaIgual);
        pegaToken.put("-=", Tokens.TOKEN_SubtracaoIgual);
        pegaToken.put("*=", Tokens.TOKEN_MultiplicacaoIgual);
        pegaToken.put("/=", Tokens.TOKEN_DivisaoIgual);
        pegaToken.put("%=", Tokens.TOKEN_ModuloIgual);
        //palavra chave
        pegaToken.put("abstract", Tokens.TOKEN_Abstract);
        pegaToken.put("Main", Tokens.TOKEN_Main);
        pegaToken.put("as", Tokens.TOKEN_As);
        pegaToken.put("base", Tokens.TOKEN_Base);
        pegaToken.put("break", Tokens.TOKEN_Break);
        pegaToken.put("case", Tokens.TOKEN_Case);
        pegaToken.put("catch", Tokens.TOKEN_Catch);
        pegaToken.put("checked", Tokens.TOKEN_Checked);
        pegaToken.put("class", Tokens.TOKEN_Class);
        pegaToken.put("const", Tokens.TOKEN_Const);
        pegaToken.put("continue", Tokens.TOKEN_Continue);
        pegaToken.put("default", Tokens.TOKEN_Default);
        pegaToken.put("delegate", Tokens.TOKEN_Delegate);
        pegaToken.put("do", Tokens.TOKEN_Do);
        pegaToken.put("else", Tokens.TOKEN_Else);
        pegaToken.put("enum", Tokens.TOKEN_Enum);
        pegaToken.put("event", Tokens.TOKEN_Event);
        pegaToken.put("explicit", Tokens.TOKEN_Explicit);
        pegaToken.put("extern", Tokens.TOKEN_Extern);
        pegaToken.put("false", Tokens.TOKEN_False);
        pegaToken.put("finally", Tokens.TOKEN_Finally);
        pegaToken.put("fixed", Tokens.TOKEN_Fixed);
        pegaToken.put("for", Tokens.TOKEN_For);
        pegaToken.put("foreach", Tokens.TOKEN_Foreach);
        pegaToken.put("goto", Tokens.TOKEN_Goto);
        pegaToken.put("if", Tokens.TOKEN_If);
        pegaToken.put("implicit", Tokens.TOKEN_Implicit);
        pegaToken.put("in", Tokens.TOKEN_In);
        pegaToken.put("interface", Tokens.TOKEN_Interface);
        pegaToken.put("internal", Tokens.TOKEN_Internal);
        pegaToken.put("is", Tokens.TOKEN_Is);
        pegaToken.put("lock", Tokens.TOKEN_Lock);
        pegaToken.put("namespace", Tokens.TOKEN_Namespace);
        pegaToken.put("new", Tokens.TOKEN_New);
        pegaToken.put("null", Tokens.TOKEN_Null);
        pegaToken.put("operator", Tokens.TOKEN_Operator);
        pegaToken.put("out", Tokens.TOKEN_Out);
        pegaToken.put("override", Tokens.TOKEN_Override);
        pegaToken.put("params", Tokens.TOKEN_Params);
        pegaToken.put("private", Tokens.TOKEN_Private);
        pegaToken.put("protected", Tokens.TOKEN_Protected);
        pegaToken.put("public", Tokens.TOKEN_Public);
        pegaToken.put("readonly", Tokens.TOKEN_Readonly);
        pegaToken.put("ref", Tokens.TOKEN_Ref);
        pegaToken.put("return", Tokens.TOKEN_Return);
        pegaToken.put("sealed", Tokens.TOKEN_Sealed);
        pegaToken.put("sizeof", Tokens.TOKEN_Sizeof);
        pegaToken.put("stackalloc", Tokens.TOKEN_Stackalloc);
        pegaToken.put("static", Tokens.TOKEN_Static);
        pegaToken.put("struct", Tokens.TOKEN_Struct);
        pegaToken.put("switch", Tokens.TOKEN_Switch);
        pegaToken.put("this", Tokens.TOKEN_This);
        pegaToken.put("throw", Tokens.TOKEN_Throw);
        pegaToken.put("true", Tokens.TOKEN_True);
        pegaToken.put("try", Tokens.TOKEN_Try);
        pegaToken.put("typeof", Tokens.TOKEN_Typeof);
        pegaToken.put("unchecked", Tokens.TOKEN_Unchecked);
        pegaToken.put("unsafe", Tokens.TOKEN_Unsafe);
        pegaToken.put("using", Tokens.TOKEN_Using);
        pegaToken.put("virtual", Tokens.TOKEN_Virtual);
        pegaToken.put("void", Tokens.TOKEN_Void);
        pegaToken.put("volatile", Tokens.TOKEN_Volatile);
        pegaToken.put("while", Tokens.TOKEN_While);
        pegaToken.put("System", Tokens.TOKEN_System);
        pegaToken.put("Console", Tokens.TOKEN_ConsoleFunction);
        pegaToken.put("Console.WriteLine", Tokens.TOKEN_ConsoleFunction);
        //Tipos de dados
        pegaToken.put("bool", Tokens.TOKEN_Bool);
        pegaToken.put("byte", Tokens.TOKEN_Byte);
        pegaToken.put("char", Tokens.TOKEN_Char);
        pegaToken.put("decimal", Tokens.TOKEN_Decimal);
        pegaToken.put("double", Tokens.TOKEN_Double);
        pegaToken.put("float", Tokens.TOKEN_Float);
        pegaToken.put("int", Tokens.TOKEN_Int);
        pegaToken.put("long", Tokens.TOKEN_Long);
        pegaToken.put("object", Tokens.TOKEN_Object);
        pegaToken.put("sbyte", Tokens.TOKEN_Sbyte);
        pegaToken.put("short", Tokens.TOKEN_Short);
        pegaToken.put("string", Tokens.TOKEN_String);
        pegaToken.put("uint", Tokens.TOKEN_Uint);
        pegaToken.put("ulong", Tokens.TOKEN_Ulong);
        pegaToken.put("ushort", Tokens.TOKEN_Ushort);
    }


    public static Tokens getToken(String symbol) {
        return pegaToken.get(symbol);
    }

    public static Map<String, Tokens> getPegaToken() {
        return Collections.unmodifiableMap(pegaToken);
    }

    public static boolean existeToken(String token) {
        if(pegaToken.containsKey(token)){
            return true;
        }
        return false;
    }

}