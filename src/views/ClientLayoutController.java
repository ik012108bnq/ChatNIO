package views;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import client.Cliente;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class ClientLayoutController {

	Cliente cliente;
	String nombre;
	@FXML
	VBox chatArea;
	@FXML
	TextField chatBox;
	@FXML
	ListView<String> lv;
	
	public void enter(String nombre)
	{
		this.nombre = nombre;
		cliente = new Cliente(nombre, this);		
		Thread c = new Thread(cliente);
		c.setDaemon(true);
		c.start();
	}		
	
	private String formatearTexto(String texto)
	{
		LocalDateTime ldt = LocalDateTime.now().plusDays(1);
		DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("HH:mm:ss", new Locale("es", "ES"));
		String formatter = formmat1.format(ldt);
	
		return "["+formatter+"] "+ nombre +": " +texto;		
	}
	
	@FXML
	public void enviar()
	{
		String msg = chatBox.getText();
		TextArea ta = new TextArea(formatearTexto(msg));
		
		Platform.runLater(() ->{
			chatArea.getChildren().add(ta);
		});

		cliente.enviarMsg(formatearTexto(msg));
	}
	
	public void escribirMsg(String msg)
	{
		TextArea ta = new TextArea(msg);
		Platform.runLater(() ->{
			chatArea.getChildren().add(ta);
		});	
	}	
	
	public void listaUsuarios(String msg)
	{		
		String[] lista = msg.split(",");

		for(String n : lista)
		{
			lv.getItems().add(n);
		}		
	}
		
	public void addNameToList(String name)
	{
		Platform.runLater(() ->{
			lv.getItems().add(name);
		});
	}
	
	public void removeNameToList(String name)
	{		
		Platform.runLater(() ->{
			lv.getItems().remove(name);
		});
	}
	
}
