package br.com.lukemedrano;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CRUDProdutoController implements Initializable {
	@FXML
    private Button botao_clear;

    @FXML
    private Button botao_create;

    @FXML
    private Button botao_delete;

    @FXML
    private Button botao_update;
    
    @FXML
    private Button botao_pesquisa_produto;
    
    @FXML
    private TextField field_pesquisa;

    @FXML
    private Button fechar_botao;

    @FXML
    private TextField id_produto;

    @FXML
    private TextField nome_produto;
    
    @FXML
    private ChoiceBox<String> categoria_produto;
    
    @FXML
    private TextField preco_produto;

    @FXML
    private TextField quantidade_estoque_produto;
    
    @FXML
    private DatePicker validade_produto;

    @FXML
    private TableView<Produto> table_view;

    @FXML
    private TableColumn<Produto, UUID> table_view_id;
    
    @FXML
    private TableColumn<Produto, String> table_view_nome;
    
    @FXML
    private TableColumn<Produto, String> table_view_categoria;

    @FXML
    private TableColumn<Produto, Integer> table_view_preco;

    @FXML
    private TableColumn<Produto, Date> table_view_validade;    
    
    @FXML
    private TableColumn<Produto, Integer> table_view_qtd_estoque;
    
    @FXML
    private Label quantidade_registro;
    
	private Connection conexao;
	private PreparedStatement preparar;
	private ResultSet resultado;
	
	private ObservableList<Produto> produto;
	
	private String[] categorias = {
			"Frutas", 
			"Legumes", 
			"Verduras", 
			"Carnes",
			"Massa",
			"Laticínios", 
			"Bebidas", 
			"Padaria", 
			"Limpeza", 
			"Higiene Pessoal", 
			"Grãos e Cereais", 
			"Congelados", 
			"Doces e Sobremesas", 
			"Utensílios de Cozinha", 
			"Flores e Plantas",  
			"Artigos de Pet Shop",
			"Material Escolar",
			"Tecnologia",
			"Eletrônicos",
			"Beleza",
			"Roupas e Acessórios",
			"Livros e Revistas",
			"Equipamentos Esportivos",
			"Móveis para Casa",
			"Outros"
	};

	
	public void fecharAplicacao() {
    	System.exit(0);
    }
	
	public void botaoCreateProduto() {
	    String sql = "INSERT INTO produtos "
	            + "(nome, categoria, preco, data_validade, qtd_estoque)"
	            + "VALUES(?, ?, ?, ?, ?)";

	    conexao = Database.getConexao();

	    try {
	        boolean isNomeValido = !nome_produto.getText().isBlank();
	        boolean isPrecoValido = !preco_produto.getText().isBlank();
	        boolean isDataValida = validade_produto.getValue() != null;
	        boolean isQuantidadeEstoqueValido = !quantidade_estoque_produto.getText().isEmpty();

	        if (isNomeValido && isPrecoValido && isQuantidadeEstoqueValido && isDataValida) {
	        	
	        	String checarNomeUnico = "SELECT nome FROM produtos WHERE LOWER(nome) = LOWER(?)";
	        	preparar = conexao.prepareStatement(checarNomeUnico);
	        	
	        	preparar.setString(1, nome_produto.getText());
	        	resultado = preparar.executeQuery();
	        	
	        	if(resultado.next()) {
	        		Alerta.exibirAlerta(
	        				"Mensagem de erro", 
	        				null, 
	        				"Produto já existente: "
	        				+ "" + nome_produto.getText(), 
	        				AlertType.ERROR);
	        	} else {
	        		preparar = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        		
	        		preparar.setString(1, nome_produto.getText());
	        		preparar.setString(2, (String) categoria_produto.getSelectionModel().getSelectedItem());
	        		preparar.setInt(3, Integer.parseInt(preco_produto.getText()));
	        		preparar.setDate(4, java.sql.Date.valueOf(validade_produto.getValue()));
	        		preparar.setInt(5, Integer.parseInt(quantidade_estoque_produto.getText()));
	        		
	        		preparar.executeUpdate();
	        		
	        		Alerta.exibirAlerta(
	        				"Mensagem de informação", 
	        				null, 
	        				"Registro criado com sucesso.", 
	        				AlertType.INFORMATION);
	        		
	        		mostrarDados();
	        		
	        		botaoClearProduto();
	        		
	        		getNumeroRegistros();
	        	}
	        } else {
	        	Alerta.exibirAlerta(
	        			"Mensagem de erro", 
	        			null, 
	        			"Por favor, preencha todos os campos em branco.", 
	        			AlertType.ERROR);
	        }
	    } catch (Exception exception) {
	    	exception.printStackTrace();
	    } finally {
	        Database.fecharConexao();
	    }
	}

	public void botaoUpdateProduto() {
	    String sql = "UPDATE produtos SET "
	            + "nome = ?, categoria = ?, preco = ?, data_validade = ?, qtd_estoque = ? "
	            + "WHERE id = ?";

	    conexao = Database.getConexao();

	    try {
	        boolean isNomeValido = !nome_produto.getText().isBlank();
	        boolean isPrecoValido = !preco_produto.getText().isBlank();
	        boolean isDataValida = validade_produto.getValue() != null;
	        boolean isQuantidadeEstoqueValido = !quantidade_estoque_produto.getText().isEmpty();
	        boolean isIdValido = !id_produto.getText().isEmpty();

	        if (isNomeValido && isPrecoValido && isQuantidadeEstoqueValido && isDataValida && isIdValido) {
	            
	            int id = Integer.parseInt(id_produto.getText());
	            if (produtoExiste(id)) {
	            	Optional<ButtonType> opcao = Alerta.exibirAlertaConfirmacao(
	            			"Mensagem de confirmação", 
	            			null, 
	            			"Tem certeza que deseja atualizar o registro: " + id_produto.getText() + " ?");
	            	
	            	if(opcao.isPresent() && opcao.get().equals(ButtonType.OK)) {
	            		preparar = conexao.prepareStatement(sql);
	            		
	            		preparar.setString(1, nome_produto.getText());
	            		preparar.setString(2, (String) categoria_produto.getSelectionModel().getSelectedItem());
	            		preparar.setInt(3, Integer.parseInt(preco_produto.getText()));
	            		preparar.setDate(4, java.sql.Date.valueOf(validade_produto.getValue()));
	            		preparar.setInt(5, Integer.parseInt(quantidade_estoque_produto.getText()));
	            		preparar.setInt(6, id);
	            		
	            		preparar.executeUpdate();
	            		
	            		Alerta.exibirAlerta(
	            				"Mensagem de informação", 
	            				null, 
	            				"Registro atualizado com sucesso.", 
	            				AlertType.INFORMATION);
	            		
	            		mostrarDados();	
	            		
	            		botaoClearProduto();
	            	} else {
		                Alerta.exibirAlerta(
		                		"Mensagem de cancelamento", 
		                		null, 
		                		"Operação cancelada.", 
		                		AlertType.ERROR);
	            	}
	            } else {
	            	Alerta.exibirAlerta(
	            			"Mensagem de erro", 
	            			null, 
	            			"ID do produto não encontrado.", 
	            			AlertType.ERROR);
	            }
	        } else {
	        	Alerta.exibirAlerta(
	        			"Mensagem de erro", 
	        			null, 
	        			"Por favor, preencha todos os campos em branco.", 
	        			AlertType.ERROR);
	        }
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    } finally {
	        Database.fecharConexao();
	    }
	}

	public void botaoDeleteProduto() {
		String sql = "DELETE FROM produtos WHERE id = ?";
		
		conexao = Database.getConexao();
		
		try {
			if (!id_produto.getText().isEmpty()) {
				int id = Integer.parseInt(id_produto.getText());
				if(produtoExiste(id)) {
					Optional<ButtonType> opcao = Alerta.exibirAlertaConfirmacao(
							"Mensagem de confirmação", 
							null, 
							"Tem certeza que deseja deletar o registro: " + id_produto.getText() + " ?");
					
					if(opcao.isPresent() && opcao.get().equals(ButtonType.OK)) {
						preparar = conexao.prepareStatement(sql);
						
						preparar.setInt(1, id);
						
						preparar.executeUpdate();
						
						Alerta.exibirAlerta(
								"Mensagem de informação", 
								null, 
								"Registro deletado com sucesso.", 
								AlertType.INFORMATION);
						
						mostrarDados();
						
						botaoClearProduto();
						
						getNumeroRegistros();
					} else {
						Alerta.exibirAlerta(
								"Mensagem de cancelamento", 
								null, 
								"Operação cancelada.", 
								AlertType.ERROR);
					}
				} else {
					Alerta.exibirAlerta(
							"Mensagem de erro", 
							null, 
							"ID do produto não encontrado.", 
							AlertType.ERROR);
				}
			} else {
				Alerta.exibirAlerta(
						"Mensagem de erro", 
						null, 
						"Selecione um produto.", 
						AlertType.ERROR);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		} finally {
			Database.fecharConexao();
		}
	}
	
	public void botaoPesquisarProduto() {
		String termoPesquisa = field_pesquisa.getText().trim().toLowerCase();

	    ObservableList<Produto> produtosFiltrados = FXCollections.observableArrayList();

	    String sql = "SELECT * FROM produtos WHERE LOWER(nome) LIKE ? OR LOWER(categoria) LIKE ? OR id = ?";
	    
	    conexao = Database.getConexao();

	    try {
	    	if(termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
	    		preparar = conexao.prepareStatement(sql);
	    		preparar.setString(1, "%" + termoPesquisa + "%");
	    		preparar.setString(2, "%" + termoPesquisa + "%");
	    		
	    		try {
	    			int id = Integer.parseInt(termoPesquisa);
	    			preparar.setInt(3, id);
	    		} catch (NumberFormatException exception) {
	    			preparar.setInt(3, -1);
	    		}
	    		
	    		resultado = preparar.executeQuery();
	    		
	    		while (resultado.next()) {
	    			int id = resultado.getInt("id");
	    			String nome = resultado.getString("nome");
	    			String categoria = resultado.getString("categoria");
	    			int preco = resultado.getInt("preco");
	    			LocalDate dataValidade = resultado.getDate("data_validade").toLocalDate();
	    			int quantidadeEstoque = resultado.getInt("qtd_estoque");
	    			
	    			Produto produto = new Produto(id, nome, categoria, preco, dataValidade, quantidadeEstoque);
	    			produtosFiltrados.add(produto);
	    		}
	    		
	    		table_view.setItems(produtosFiltrados);
	    	} else {
	    		mostrarDados();
	    	}
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    } finally {
	        Database.fecharConexao();
	    }
	}
	
	public void botaoClearProduto() {
		id_produto.setText("");
		nome_produto.setText("");
		categoria_produto.getSelectionModel().clearSelection();
		preco_produto.setText("");
		quantidade_estoque_produto.setText("");
		validade_produto.setValue(null);
	}
	
	private boolean produtoExiste(int id) {
	    String verificarId = "SELECT id FROM produtos WHERE id = ?";
	    
	    try {
	        preparar = conexao.prepareStatement(verificarId);
	        preparar.setInt(1, id);
	        resultado = preparar.executeQuery();
	        
	        return resultado.next();
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    }

	    return false;
	}

	public void getNumeroRegistros() {
	    String sql = "SELECT COUNT(*) FROM produtos";
	    
	    conexao = Database.getConexao();

	    try {
	        preparar = conexao.prepareStatement(sql);
	        resultado = preparar.executeQuery();

	        if (resultado.next()) {
	            int count = resultado.getInt(1);
	            quantidade_registro.setText(Integer.toString(count));
	        }
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    } finally {
	        Database.fecharConexao();
	    }
	}
	
	public ObservableList<Produto> dadosListaProdutos(){
		ObservableList<Produto> lista = FXCollections.observableArrayList();
		
		String selecionar = "SELECT * FROM produtos";
		
		conexao = Database.getConexao();
		
		try {
			preparar = conexao.prepareStatement(selecionar);
			resultado = preparar.executeQuery();
			
			while(resultado.next()) { 
				int id = resultado.getInt("id");
	            String nome = resultado.getString("nome");
	            String categoria = resultado.getString("categoria");
	            int preco = resultado.getInt("preco");
	            LocalDate dataValidade = resultado.getDate("data_validade").toLocalDate();
	            int quantidadeEstoque = resultado.getInt("qtd_estoque");
	
	            Produto produto = new Produto(id, nome, categoria, preco, dataValidade, quantidadeEstoque);
	            lista.add(produto);
			}
			
		} catch(Exception exception) {
			exception.printStackTrace();
		} finally {
			Database.fecharConexao();
		}		
		
		return lista;
	}
	   
	public void mostrarDados() {
		produto = dadosListaProdutos();
		
		table_view_id.setCellValueFactory(new PropertyValueFactory<>("id"));
		table_view_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		table_view_categoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
		table_view_preco.setCellValueFactory(new PropertyValueFactory<>("preco"));
		table_view_validade.setCellValueFactory(new PropertyValueFactory<>("dataValidade"));
		table_view_qtd_estoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
		
		table_view.setItems(produto);
	}
	
	public void selecionarDadosProduto() {
		Produto produto = table_view.getSelectionModel().getSelectedItem();
		
		try {
			id_produto.setText(String.valueOf(produto.getId()));
			nome_produto.setText(String.valueOf(produto.getNome()));
			categoria_produto.setValue(String.valueOf(produto.getCategoria()));
			preco_produto.setText(Integer.toString(produto.getPreco()));
			if (produto.getDataValidade() != null) {
				validade_produto.setValue(produto.getDataValidade());
			}

			quantidade_estoque_produto.setText(Integer.toString(produto.getQuantidadeEstoque()));						
		} catch(Exception exception) {
			return;
		}
	}
	
	public void listaCategorias() {
		List<String> listaCategorias = new ArrayList<String>();
		
		for(String categoria: categorias) {
			listaCategorias.add(categoria);
		}
		
		ObservableList<String> lista = FXCollections.observableList(listaCategorias);
		categoria_produto.setItems(lista);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getNumeroRegistros();
		mostrarDados();
		listaCategorias();
	}
}