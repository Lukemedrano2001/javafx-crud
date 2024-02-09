package br.com.lukemedrano;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FXMLDocumentController implements Initializable {
	@FXML
	private Button fechar_botao;
	
	@FXML
    private Button login_botao;

    @FXML
    private BorderPane login_formulario;

    @FXML
    private Button login_registro_botao;

    @FXML
    private PasswordField login_senha;

    @FXML
    private TextField login_usuario;

    @FXML
    private Button registro_botao;

    @FXML
    private BorderPane registro_formulario;

    @FXML
    private Button registro_login_botao;

    @FXML
    private PasswordField registro_senha;

    @FXML
    private TextField registro_usuario;
    
    private Connection conexao;
    private PreparedStatement preparar;
    private ResultSet resultado;
    
    private double x = 0;
    private double y = 0;
    
    public void fecharAplicacao() {
    	System.exit(0);
    }
    
    public void loginConta() {
    	String sql = "SELECT nome, senha FROM usuarios WHERE nome = ? and senha = ?";
    	
    	conexao = Database.getConexao();
    	
    	try {
    		if(login_usuario.getText().isBlank() || login_senha.getText().isBlank()) {
    			Alerta.exibirAlerta(
    					"Mensagem de erro", 
    					null, 
    					"Por favor, preencha todos os campos em branco", 
    					AlertType.ERROR);
    		} else {
    			preparar = conexao.prepareStatement(sql);
    			preparar.setString(1, login_usuario.getText());
    			preparar.setString(2, login_senha.getText());
    			
    			resultado = preparar.executeQuery();
    			
    			if(resultado.next()) {
    				Alerta.exibirAlerta(
    						"Mensagem de informação", 
    						null, 
    						"Login feito com sucesso", 
    						AlertType.INFORMATION);
        			
        			login_botao.getScene().getWindow().hide();
        			Parent root = FXMLLoader.load(getClass().getResource("CrudProduto.fxml"));
        			Stage stage = new Stage();
        			Scene scene = new Scene(root);
        			
        			root.setOnMousePressed(evento -> {
        		        x = evento.getSceneX();
        		        y = evento.getSceneY();
        		    });
        			
        			root.setOnMouseDragged(evento -> {
        		        stage.setX(evento.getScreenX() - x);
        		        stage.setY(evento.getScreenY() - y);
        		    });
        			
        			stage.initStyle(StageStyle.TRANSPARENT);
        			stage.setScene(scene);
        			stage.show();
    			} else {
    				Alerta.exibirAlerta(
    						"Mensagem de erro", 
    						null, 
    						"Nome de usuário ou Senha incorretos", 
    						AlertType.ERROR);
    			}
    		}
    	} catch(Exception exception) {
    		exception.printStackTrace();
    	} finally {
    		Database.fecharConexao();
    	}
    }
    
    public void registrarConta() {
    	String sql = "INSERT INTO usuarios(nome, senha) VALUES(?, ?)";
    	
    	conexao = Database.getConexao();
    	
    	try {	
    		if(registro_usuario.getText().isBlank() || registro_senha.getText().isBlank()) {
    			Alerta.exibirAlerta(
    					"Mensagem de erro", 
    					null, 
    					"Por favor, preencha todos os campos em branco", 
    					AlertType.ERROR);
    		} else {
    			String checarNomeUsuario = "SELECT nome FROM usuarios WHERE nome = ?";
    			preparar = conexao.prepareStatement(checarNomeUsuario);
    			preparar.setString(1, registro_usuario.getText());
    			resultado = preparar.executeQuery();
    			
    			Pattern patternSenha = Pattern
    					.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%¨&*()_+]).{8,}$");
                Matcher matcherSenha = patternSenha
                		.matcher(registro_senha.getText());
    			
    			if(resultado.next()) {
    				Alerta.exibirAlerta(
    						"Mensagem de erro", 
    						null, 
    						"Usuário já existente: " + registro_usuario.getText(), 
    						AlertType.ERROR);
    			} else if(!matcherSenha.matches()) {
    				Alerta.exibirAlerta(
    						"Mensagem de erro", 
    						null, 
    						"A senha deve atender tais requisitos:"
    	        					+ "\n- Pelo menos 8 caracteres"
    	        					+ "\n- Uma letra maiúscula"
    	        					+ "\n- Uma letra minúscula"
    	        					+ "\n- Um número"
    	        					+ "\n- Um caractere especial.", 
    						AlertType.ERROR);
    			} else {    				
    				preparar = conexao.prepareStatement(sql);
    				preparar.setString(1, registro_usuario.getText());
    				preparar.setString(2, registro_senha.getText());
    				
    				preparar.executeUpdate();
    				
    				Alerta.exibirAlerta(
    						"Mensagem de informação", 
    						null, 
    						"Registro feito com sucesso", 
    						AlertType.INFORMATION);
        			
        			login_formulario.setVisible(true);
            		registro_formulario.setVisible(false);
            		
            		registro_usuario.setText("");
            		registro_senha.setText("");
    			}
    		}
    	} catch(Exception exception) {
    		exception.printStackTrace();
    	} finally {
    		Database.fecharConexao();
    	}
    }
    
    public void trocarStageFormulario(ActionEvent evento) {
    	if(evento.getSource() == registro_login_botao) {
    		login_formulario.setVisible(true);
    		registro_formulario.setVisible(false);
    	} else if(evento.getSource() == login_registro_botao) {
    		login_formulario.setVisible(false);
    		registro_formulario.setVisible(true);
    	}
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
}