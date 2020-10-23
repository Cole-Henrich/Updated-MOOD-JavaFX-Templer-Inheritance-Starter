package org.headroyce.bsea;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        GameGUI root = new GameGUI();
        primaryStage.setTitle("Templer");

        Scene scene = new Scene(root, 500,500);
        primaryStage.setScene(scene);

        // Forward ket events to the Game
        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                root.handleKeyPress(keyEvent);
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                root.handleKeyRelease(keyEvent);
            }
        });
        primaryStage.show();

        root.pause(false,false);
    }


    public static void main(String[] args) {
        launch(args);
    }
    private class KeyPressHandler implements EventHandler<KeyEvent> {
        public void handle(KeyEvent event){
            System.err.println("KEY PRESS");
        }
    }

    private class KeyReleaseHandler implements EventHandler<KeyEvent> {
        public void handle(KeyEvent event) {
            System.err.println("KEY RELEASE");
        }
    }
}
