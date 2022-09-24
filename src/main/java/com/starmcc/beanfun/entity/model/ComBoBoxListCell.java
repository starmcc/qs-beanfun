package com.starmcc.beanfun.entity.model;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

/**
 * 单元列表
 *
 * @author starmcc
 * @date 2022/09/23
 */
public class ComBoBoxListCell extends ListCell<String> {

    private Consumer<String> consumer;

    public ComBoBoxListCell(Consumer<String> consumer) {
        this.consumer = consumer;
    }


    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            Label label = new Label();
            label.setText(item);
            Button button = new Button("X");
            Font font = Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 14);
            button.setFont(font);
            button.setStyle("-fx-background-color: transparent;");
            button.setOnMouseReleased(event -> consumer.accept(item));
            button.setPadding(new Insets(0, 10, 0, 0));
            hBox.getChildren().add(label);
            HBox hBox1 = new HBox();
            HBox.setHgrow(hBox1, Priority.ALWAYS);
            hBox.getChildren().add(hBox1);
            hBox.getChildren().add(button);
            this.setGraphic(hBox);
        }
    }

}