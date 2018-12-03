package views;

import cliente.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class EnterLayoutController {

	private Main mainData;
	
	@FXML
	private TextField tbUsuario;
	@FXML
	private Button btnEntrar;
	@FXML
	private Button btnSalir;	
	
	@FXML
	public void entrarHandler()
	{
		boolean usado = false;
    	if(usado)//TODO: Comprobar si ya esta el nombre en uso
    	{
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setTitle("Usuario usados");
        	alert.setHeaderText(null);
        	alert.setContentText("Usuario en uso");
        	alert.showAndWait();
        	return;
    	}
		String nombre = tbUsuario.getText();
		if(nombre.length() > 0)
			mainData.initChatLayout(nombre);
		else
		{
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setTitle("Usuario erroneo");
        	alert.setHeaderText(null);
        	alert.setContentText("Debes introducir un nombre de usuario");
        	alert.showAndWait();
        	return;
		}			
	}
	
	public void setMainData(Main main)
	{
		this.mainData = main;
	}	
}
