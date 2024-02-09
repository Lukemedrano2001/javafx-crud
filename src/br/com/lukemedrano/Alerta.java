package br.com.lukemedrano;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerta {
	public static void exibirAlerta(String titulo, String cabecalho, String conteudo, AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(conteudo);
        alerta.showAndWait();
    }
	
	public static Optional<ButtonType> exibirAlertaConfirmacao(String titulo, String cabecalho, String conteudo) {
		Alert alerta = new Alert(AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(conteudo);

        Optional<ButtonType> botaoConfirmacao = alerta.showAndWait();

        return botaoConfirmacao;
	}
}