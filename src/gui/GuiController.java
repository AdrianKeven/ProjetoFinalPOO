package gui;
import operacoes.BancoService;
import gui.telas.TelaPrincipal;
public class GuiController {
    private static BancoService bancoService;

    
    public static BancoService getBancoService() {
        if (bancoService == null) {
            bancoService = new BancoService("Banco POO");
        }
        return bancoService;
    }

    
    public static void iniciar() {
        new TelaPrincipal().setVisible(true);
    }   
}
