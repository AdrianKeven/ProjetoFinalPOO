package utilitarios;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<String> stringToHistorico(String texto) {
        List<String> lista = new ArrayList<>();
        if (texto == null || texto.isEmpty()) return lista;

        String[] partes = texto.split("###");
        for (String s : partes) {
            lista.add(s);
        }
        return lista;
    }

    public static String historicoToString(List<String> historico) {
        if (historico == null || historico.isEmpty()) {
            return "";
        }
        return String.join("###", historico);
    }
}
