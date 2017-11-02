package com.util.fxGUI.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

/**
 * Created by rajendv3 on 1/08/2017.
 */
public class MainUIController {

    public Label helloWorld;

    public void sayHelloWorld(ActionEvent event){
        helloWorld.setText("Hello World");
    }
}
