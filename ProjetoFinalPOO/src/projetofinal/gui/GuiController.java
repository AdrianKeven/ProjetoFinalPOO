package projetofinal.gui;
import projetofinal.operacoes.BancoService;
import projetofinal.gui.telas.TelaPrincipal;
public class GuiController {
    private static BancoService bancoService;

    // Inicializa o banco apenas uma vez
    public static BancoService getBancoService() {
        if (bancoService == null) {
            bancoService = new BancoService("Banco POO");
        }
        return bancoService;
    }

    // Método para abrir a tela principal ao iniciar
    public static void iniciar() {
        new TelaPrincipal().setVisible(true);
    }   
}
