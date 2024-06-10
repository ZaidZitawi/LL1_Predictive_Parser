package com.example.ll1_predictive_parser;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class HelloApplication extends Application {
    String ButtonStyle2 = "-fx-background-color:#0598ff; -fx-background-color:transparent; -fx-font-size: 13; -fx-font-weight: bold;";

    Image load = new Image("C:\\Users\\DELL\\Idea Projects Ultimate\\LL1_Predictive_Parser\\src\\main\\java\\images\\img_1.png");
    Image eraser = new Image("C:\\Users\\DELL\\Idea Projects Ultimate\\LL1_Predictive_Parser\\src\\main\\java\\images\\img.png");
    Image analyze = new Image("C:\\Users\\DELL\\Idea Projects Ultimate\\LL1_Predictive_Parser\\src\\main\\java\\images\\img_2.png");
    Image pars = new Image("C:\\Users\\DELL\\Idea Projects Ultimate\\LL1_Predictive_Parser\\src\\main\\java\\images\\img_3.png");

    ImageView loadView = new ImageView(load);
    ImageView eraserView = new ImageView(eraser);
    ImageView analyzeView = new ImageView(analyze);
    ImageView parsView = new ImageView(pars);

    private TextArea codeArea; // Declare codeArea to use it throughout the class
    private TableView<Token> tokenTable; // Declare tokenTable to use it throughout the class
    private TextArea parsingProcessArea; // Declare parsingProcessArea to display parsing steps
    private TextArea outputArea; // Declare outputArea to display the result of parsing
    private Parser parser; // Declare a parser object

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();

        // Code area initialization
        codeArea = new TextArea();
        codeArea.setPromptText("Enter your code here...");
        VBox.setVgrow(codeArea, Priority.ALWAYS);

        // Buttons initialization
        Button btnAnalyze = new Button("Analyze", analyzeView);
        Button btnClear = new Button("Clear", eraserView);
        Button btnLoadFile = new Button("Load File", loadView);
        Button btnParse = new Button("Parse", parsView);
        initializeImageView(analyzeView, 30, 30);
        initializeImageView(eraserView, 30, 30);
        initializeImageView(loadView, 30, 30);
        initializeImageView(parsView, 30, 30);
        btnAnalyze.setStyle(ButtonStyle2);
        btnClear.setStyle(ButtonStyle2);
        btnLoadFile.setStyle(ButtonStyle2);
        btnParse.setStyle(ButtonStyle2);
        dropShadow(btnAnalyze);
        dropShadow(btnClear);
        dropShadow(btnLoadFile);
        dropShadow(btnParse);

        // Setting up the button bar
        HBox buttonBar = new HBox(10, btnLoadFile, btnAnalyze, btnParse, btnClear);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setStyle("-fx-alignment: center;");
        VBox leftBox = new VBox(10, buttonBar, codeArea);
        leftBox.setPadding(new Insets(10));
        leftBox.setStyle("-fx-background-color: #f0f0f0;");
        borderPane.setLeft(leftBox);

        // Token table initialization
        tokenTable = new TableView<>();
        setupTokenTable(tokenTable);
        VBox.setVgrow(tokenTable, Priority.ALWAYS);

        // Parsing process area
        parsingProcessArea = new TextArea();
        parsingProcessArea.setPromptText("Parsing process steps will be displayed here...");
        parsingProcessArea.setEditable(false);
        parsingProcessArea.setPrefHeight(150);
        VBox.setVgrow(parsingProcessArea, Priority.ALWAYS);

        // Output area initialization
        outputArea = new TextArea();
        outputArea.setPromptText("Output of the parsing operation will be displayed here...");
        outputArea.setEditable(false);
        outputArea.setPrefHeight(150);
        VBox.setVgrow(outputArea, Priority.ALWAYS);  // Make outputArea taller

        VBox rightBox = new VBox(10, new Label("Tokens"), tokenTable, new Label("Parsing Process"), parsingProcessArea, new Label("Output"), outputArea);
        rightBox.setPadding(new Insets(10));
        rightBox.setStyle("-fx-background-color: #ffffff;");
        borderPane.setRight(rightBox);

        // Attach events
        btnLoadFile.setOnAction(e -> {
            loadFile(primaryStage);
            btnParse.setDisable(true); // Disable parse button when a new file is loaded
            parsingProcessArea.setText("");
            outputArea.setText("");
        });
        btnClear.setOnAction(e -> codeArea.clear());
        btnAnalyze.setOnAction(e -> {
            analyzeCode();
            btnParse.setDisable(false);
        });
        btnParse.setOnAction(e -> parseTokens()); // Attach event to parse tokens

        // Scene setup
        Scene scene = new Scene(borderPane, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Lexical and Syntax Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeImageView(ImageView imageView, int width, int height) {
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
    }

    private void setupTokenTable(TableView<Token> table) {
        TableColumn<Token, Integer> lineColumn = new TableColumn<>("Line");
        lineColumn.setCellValueFactory(new PropertyValueFactory<>("line"));

        TableColumn<Token, String> tokenColumn = new TableColumn<>("Type");
        tokenColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Token, String> attributeColumn = new TableColumn<>("Token");
        attributeColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        double width = 400;
        double attributeWidth = width / 2;
        lineColumn.setPrefWidth(attributeWidth / 2);
        tokenColumn.setPrefWidth(attributeWidth);
        attributeColumn.setPrefWidth(attributeWidth / 2);

        table.getColumns().addAll(lineColumn, tokenColumn, attributeColumn);
    }

    private void loadFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        var file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String content = Files.readString(Paths.get(file.toURI()));
                codeArea.setText(content);
            } catch (IOException ioException) {
                codeArea.setText("Error reading file: " + ioException.getMessage());
            }
        }
    }

    private void analyzeCode() {
        String content = codeArea.getText();
        Scanner scanner = new Scanner(content); // Create a new Scanner instance
        ArrayList<Token> tokens = scanner.getTokens();
        tokenTable.setItems(FXCollections.observableArrayList(tokens));
        parser = new Parser(scanner, parsingProcessArea, outputArea); // Initialize parser with the scanner and parsing process area

        // Display lexical errors, if any
        ArrayList<String> errors = scanner.getErrors();
        if (!errors.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            for (String error : errors) {
                if (error.contains("invalid-identifier")) {
                    errorMessages.append("Invalid Identifiers: ").append(error).append("\n");
                } else {
                    errorMessages.append(error).append("\n");
                }
            }
            outputArea.setText(errorMessages.toString());
        } else {
            outputArea.setText(""); // Clear the output area if there are no errors
        }
    }

    private void parseTokens() {
        if (parser == null) {
            outputArea.setText("Please analyze the code before parsing.");
            return;
        }
        try {
            boolean success = parser.parse();
            if (success) {
                outputArea.setText("Parsing completed successfully.");
            } else {
                outputArea.appendText("\nParsing failed with errors. See details above in the process TextArea.");
            }
        } catch (RuntimeException e) {
            outputArea.setText("Parsing error: " + e.getMessage());
        }
    }

    public void dropShadow(Button btn) {
        // Add an effect to the button
        DropShadow dropShadow = new DropShadow();
        dropShadow.setBlurType(BlurType.GAUSSIAN);
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        // Set the effect to the button
        btn.setEffect(dropShadow);
        // Increase the opacity of the shadow when the mouse is over the button
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> dropShadow.setColor(Color.rgb(0, 0, 0, 1)));
        // Decrease the opacity of the shadow when the mouse is not over the button
        btn.addEventHandler(MouseEvent.MOUSE_EXITED, event -> dropShadow.setColor(Color.rgb(0, 0, 0, 0.5)));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
