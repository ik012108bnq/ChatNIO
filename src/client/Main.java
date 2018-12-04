package client;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import views.ClientLayoutController;
import views.EnterLayoutController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	Stage chatStage;
	Stage enterStage;

	@Override
	public void start(Stage primaryStage) 
	{		
		try {
			this.enterStage = primaryStage;
			enterStage.setTitle("Enter Chat");
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("../views/EnterLayout.fxml"));
			AnchorPane enterOverview = (AnchorPane) loader.load();
		    enterStage.setScene(new Scene(enterOverview));
		    EnterLayoutController controller = loader.getController();
		    controller.setMainData(this);		
		    enterStage.show();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
		
	public void initChatLayout(String nombre)
	{		
        try {
        	enterStage.close();
        	chatStage = new Stage();        	
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("../views/ClientLayout.fxml"));
            BorderPane chatLayout = (BorderPane) loader.load();
            chatStage.setScene(new Scene(chatLayout));
            ClientLayoutController controller = loader.getController();
            controller.enter(nombre);
            chatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}
}
