package com.smartlogistics.javafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

public class SmartLogisticsApp extends Application {
    private VBox contentArea;
    private Label statusLabel;
    private final String BACKEND_URL = "http://localhost:8080/api";
    
    // Form fields
    private TextField shipmentIdField;
    private TextField originField;
    private TextField destinationField;
    private TextField cargoField;
    private TextField weightField;
    private ComboBox<String> statusCombo;
    private TextField etaField;
    private TextField carrierField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Smart Logistics Management System");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);

        // Header
        VBox header = createHeader();

        // Action Buttons
        HBox actionButtons = createActionButtons();

        // Main Content Area
        contentArea = new VBox(15);
        contentArea.setPadding(new Insets(20));
        contentArea.setStyle("-fx-background-color: #f5f5f5;");
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);

        // Status Bar
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-padding: 10; -fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 12;");

        // Layout
        VBox mainLayout = new VBox(0);
        mainLayout.getChildren().addAll(header, actionButtons, scrollPane, statusLabel);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();

        loadShipments();
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setPadding(new Insets(25));
        
        // Create a proper gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 0, true,
            javafx.scene.paint.CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#667eea")),
            new Stop(1, Color.web("#764ba2"))
        );
        
        header.setStyle(
            "-fx-padding: 25; " +
            "-fx-border-color: transparent;"
        );
        header.setBackground(new javafx.scene.layout.Background(
            new javafx.scene.layout.BackgroundFill(gradient, null, null)
        ));

        Label titleLabel = new Label("Smart Logistics Management System");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;"
        );

        Label subtitleLabel = new Label("Real-time Shipment Tracking with AI-powered Insights");
        subtitleLabel.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-text-fill: rgba(255,255,255,0.85);"
        );

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private HBox createActionButtons() {
        HBox buttons = new HBox(15);
        buttons.setPadding(new Insets(20));
        buttons.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0 0 2 0;");

        Button btnLoadShipments = createStyledButton("📦 Load Shipments", "#667eea", "#5568d3");
        btnLoadShipments.setOnAction(e -> loadShipments());

        Button btnCreateShipment = createStyledButton("➕ Create Shipment", "#4caf50", "#45a049");
        btnCreateShipment.setOnAction(e -> showCreateShipmentForm());

        Button btnPredictDemand = createStyledButton("📊 Predict Demand", "#667eea", "#5568d3");
        btnPredictDemand.setOnAction(e -> predictDemand());

        Button btnDetectAnomalies = createStyledButton("⚠️ Detect Anomalies", "#667eea", "#5568d3");
        btnDetectAnomalies.setOnAction(e -> detectAnomalies());

        Button btnRefresh = createStyledButton("🔄 Refresh", "#666", "#555");
        btnRefresh.setOnAction(e -> loadShipments());

        buttons.getChildren().addAll(btnLoadShipments, btnCreateShipment, btnPredictDemand, btnDetectAnomalies, btnRefresh);
        return buttons;
    }

    private Button createStyledButton(String text, String bgColor, String hoverColor) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(45);
        button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: " + bgColor + "; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 20px; " +
            "-fx-border-radius: 5px; " +
            "-fx-cursor: hand; " +
            "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent;"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-border-radius: 5px; " +
                "-fx-cursor: hand; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-border-radius: 5px; " +
                "-fx-cursor: hand; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent;"
            )
        );
        
        return button;
    }

    private void loadShipments() {
        new Thread(() -> {
            try {
                updateStatus("Loading shipments...");
                String response = callAPI("/shipments");
                JSONArray shipments = new JSONArray(response);

                javafx.application.Platform.runLater(() -> {
                    contentArea.getChildren().clear();

                    // Statistics Panel
                    HBox statsPanel = createStatsPanel(shipments);
                    contentArea.getChildren().add(statsPanel);

                    // Shipments Title
                    Label shipmentsTitle = new Label("📦 All Shipments (" + shipments.length() + ")");
                    shipmentsTitle.setStyle(
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #222; " +
                        "-fx-padding: 10 0 10 0;"
                    );
                    contentArea.getChildren().add(shipmentsTitle);

                    // Shipment Cards
                    FlowPane shipmentCards = new FlowPane(10, 10);
                    shipmentCards.setPrefWrapLength(1300);

                    for (int i = 0; i < shipments.length(); i++) {
                        JSONObject ship = shipments.getJSONObject(i);
                        VBox card = createShipmentCard(ship);
                        shipmentCards.getChildren().add(card);
                    }

                    contentArea.getChildren().add(shipmentCards);
                    updateStatus("✓ Loaded " + shipments.length() + " shipments");
                });
            } catch (Exception e) {
                updateStatusWithError("✗ Error loading shipments: " + e.getMessage());
            }
        }).start();
    }

    private HBox createStatsPanel(JSONArray shipments) {
        HBox statsBox = new HBox(15);
        statsBox.setPadding(new Insets(20));
        statsBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        int total = shipments.length();
        int inTransit = 0, delivered = 0, delayed = 0, pending = 0;

        for (int i = 0; i < shipments.length(); i++) {
            String status = shipments.getJSONObject(i).getString("status");
            switch (status) {
                case "In Transit" -> inTransit++;
                case "Delivered" -> delivered++;
                case "Delayed" -> delayed++;
                case "Pending" -> pending++;
            }
        }

        statsBox.getChildren().addAll(
            createStatBox("Total Shipments", total, "#667eea"),
            createStatBox("In Transit", inTransit, "#2196f3"),
            createStatBox("Delivered", delivered, "#4caf50"),
            createStatBox("Delayed", delayed, "#ff9800")
        );

        return statsBox;
    }

    private VBox createStatBox(String label, int value, String color) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-border-radius: 8; " +
            "-fx-text-fill: white;"
        );
        box.setPrefWidth(250);
        box.setMinHeight(120);

        Label numberLabel = new Label(String.valueOf(value));
        numberLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;"
        );

        Label labelText = new Label(label);
        labelText.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: rgba(255,255,255,0.9);"
        );

        box.getChildren().addAll(numberLabel, labelText);
        return box;
    }

    private VBox createShipmentCard(JSONObject ship) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-width: 1 0 0 4; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1);"
        );
        card.setPrefWidth(380);
        card.setMaxWidth(380);

        // ID and Status Badge
        HBox header = new HBox(10);
        Label idLabel = new Label(ship.getString("id"));
        idLabel.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 15px; " +
            "-fx-text-fill: #222;"
        );

        Label statusBadge = new Label(ship.getString("status"));
        statusBadge.setPadding(new Insets(5, 12, 5, 12));
        statusBadge.setStyle("-fx-border-radius: 15; -fx-font-size: 11px; -fx-font-weight: bold; " + getStatusColor(ship.getString("status")));

        header.getChildren().addAll(idLabel, statusBadge);

        // Origin to Destination
        Label routeLabel = new Label(ship.getString("origin") + " → " + ship.getString("destination"));
        routeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        // Cargo and Weight
        Label cargoLabel = new Label("📦 " + ship.getString("cargo") + " (" + ship.getDouble("weight") + " kg)");
        cargoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // ETA
        Label etaLabel = new Label("📅 ETA: " + ship.getString("eta"));
        etaLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Carrier
        Label carrierLabel = new Label("🚚 " + ship.getString("carrier"));
        carrierLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        card.getChildren().addAll(header, routeLabel, cargoLabel, etaLabel, carrierLabel);
        return card;
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "Pending" -> "-fx-background-color: #fff3cd; -fx-text-fill: #856404;";
            case "In Transit" -> "-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460;";
            case "Delivered" -> "-fx-background-color: #d4edda; -fx-text-fill: #155724;";
            case "Delayed" -> "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;";
            default -> "-fx-background-color: #e2e3e5; -fx-text-fill: #383d41;";
        };
    }

    private void predictDemand() {
        new Thread(() -> {
            try {
                updateStatus("Running ML prediction...");
                String response = callAPI("/ml/predict");
                JSONObject prediction = new JSONObject(response);

                javafx.application.Platform.runLater(() -> {
                    contentArea.getChildren().clear();

                    Label titleLabel = new Label("📊 Demand Prediction (Machine Learning)");
                    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #222; -fx-padding: 10 0 15 0;");
                    contentArea.getChildren().add(titleLabel);

                    VBox predictionCard = new VBox(15);
                    predictionCard.setPadding(new Insets(25));
                    predictionCard.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: #667eea; -fx-border-width: 2;");

                    // Current shipments
                    HBox currentBox = createPredictionMetric(
                        "Current Shipments",
                        String.valueOf(prediction.getInt("current_count")),
                        "#2196f3"
                    );

                    // Predicted shipments
                    HBox predictedBox = createPredictionMetric(
                        "Predicted Next Week",
                        String.valueOf(prediction.getInt("predicted_demand_next_week")),
                        "#4caf50"
                    );

                    // Growth percentage
                    HBox growthBox = createPredictionMetric(
                        "Growth Percentage",
                        prediction.getInt("growth_percentage") + "%",
                        "#ff9800"
                    );

                    // Confidence
                    HBox confidenceBox = createPredictionMetric(
                        "Confidence Score",
                        String.format("%.1f%%", prediction.getDouble("confidence") * 100),
                        "#9c27b0"
                    );

                    predictionCard.getChildren().addAll(currentBox, predictedBox, growthBox, confidenceBox);
                    contentArea.getChildren().add(predictionCard);

                    // Add insights
                    VBox insightsCard = new VBox();
                    insightsCard.setPadding(new Insets(15));
                    insightsCard.setStyle("-fx-background-color: #f5f5f5; -fx-border-radius: 8;");

                    Label insightsTitle = new Label("📈 Insights");
                    insightsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #333;");

                    Label insightsText = new Label(
                        "Based on current trends, shipment volume is expected to grow by " +
                        prediction.getInt("growth_percentage") + "% in the next week. " +
                        "This prediction has a confidence level of " +
                        String.format("%.0f%%", prediction.getDouble("confidence") * 100) + "."
                    );
                    insightsText.setStyle("-fx-font-size: 13; -fx-text-fill: #666; -fx-wrap-text: true;");
                    insightsText.setPrefWidth(400);

                    insightsCard.getChildren().addAll(insightsTitle, insightsText);
                    contentArea.getChildren().add(insightsCard);

                    updateStatus("✓ ML prediction complete");
                });
            } catch (Exception e) {
                updateStatusWithError("✗ Prediction failed: " + e.getMessage());
            }
        }).start();
    }

    private HBox createPredictionMetric(String label, String value, String color) {
        HBox box = new HBox(20);
        box.setPadding(new Insets(18));
        box.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1);"
        );

        VBox labelBox = new VBox(3);
        Label labelText = new Label(label);
        labelText.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #888; " +
            "-fx-font-weight: bold;"
        );
        labelBox.getChildren().add(labelText);

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + color + ";"
        );
        box.getChildren().addAll(labelBox, valueLabel);

        return box;
    }

    private void detectAnomalies() {
        new Thread(() -> {
            try {
                updateStatus("Detecting anomalies...");
                String response = callAPI("/ml/detect-anomalies");
                JSONObject result = new JSONObject(response);

                javafx.application.Platform.runLater(() -> {
                    contentArea.getChildren().clear();

                    Label titleLabel = new Label("⚠️ Anomaly Detection");
                    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #222; -fx-padding: 10 0 15 0;");
                    contentArea.getChildren().add(titleLabel);

                    // Summary Card
                    HBox summaryBox = new HBox(15);
                    summaryBox.setPadding(new Insets(15));
                    summaryBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: #e0e0e0;");

                    String health = result.getString("system_health");
                    String healthColor = health.equals("GOOD") ? "#4caf50" : "#ff9800";

                    summaryBox.getChildren().addAll(
                        createStatBox("Total Anomalies", result.getInt("total_anomalies"), "#f44336"),
                        createStatBox("Anomaly Rate", (int) result.getDouble("anomaly_rate_percentage"), "#ff9800"),
                        createStatBox("System Health", health.equals("GOOD") ? 100 : 50, healthColor)
                    );

                    contentArea.getChildren().add(summaryBox);

                    // Anomalies Details
                    Label anomaliesTitle = new Label("🔍 Detected Issues");
                    anomaliesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #222; -fx-padding: 15 0 10 0;");
                    contentArea.getChildren().add(anomaliesTitle);

                    JSONArray anomalies = result.getJSONArray("anomalies");
                    if (anomalies.length() == 0) {
                        Label noAnomaliesLabel = new Label("✓ No anomalies detected");
                        noAnomaliesLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #4caf50; -fx-padding: 10;");
                        contentArea.getChildren().add(noAnomaliesLabel);
                    } else {
                        for (int i = 0; i < anomalies.length(); i++) {
                            JSONObject anom = anomalies.getJSONObject(i);
                            VBox anomalyCard = createAnomalyCard(anom);
                            contentArea.getChildren().add(anomalyCard);
                        }
                    }

                    updateStatus("✓ Anomaly detection complete");
                });
            } catch (Exception e) {
                updateStatusWithError("✗ Anomaly detection failed: " + e.getMessage());
            }
        }).start();
    }

    private VBox createAnomalyCard(JSONObject anom) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: #fffbea; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: #ffb74d; " +
            "-fx-border-width: 1 0 0 4; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 3, 0, 0, 1);"
        );

        Label idLabel = new Label(anom.getString("id"));
        idLabel.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #333;"
        );

        Label severityLabel = new Label("⚠️ Severity: " + anom.getString("severity"));
        severityLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #ff9800; " +
            "-fx-font-weight: bold;"
        );

        Label recommendationLabel = new Label("💡 " + anom.getString("recommendation"));
        recommendationLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #666; " +
            "-fx-wrap-text: true;"
        );
        recommendationLabel.setPrefWidth(900);

        card.getChildren().addAll(idLabel, severityLabel, recommendationLabel);
        return card;
    }

    private String callAPI(String endpoint) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BACKEND_URL + endpoint))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private void updateStatus(String message) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-padding: 10; -fx-background-color: #333; -fx-text-fill: white; -fx-font-size: 12;");
        });
    }

    private void updateStatusWithError(String message) {
        javafx.application.Platform.runLater(() -> {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-padding: 10; -fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12;");
            contentArea.getChildren().clear();
            Label errorLabel = new Label(message);
            errorLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #f44336; -fx-padding: 20;");
            contentArea.getChildren().add(errorLabel);
        });
    }

    private void showCreateShipmentForm() {
        contentArea.getChildren().clear();

        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(25));
        formContainer.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-radius: 8; " +
            "-fx-border-color: #667eea; " +
            "-fx-border-width: 3; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        // Form Title
        Label titleLabel = new Label("➕ Create New Shipment");
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #667eea;"
        );
        formContainer.getChildren().add(titleLabel);

        // Shipment ID
        VBox shipmentIdBox = createFormField("Shipment ID", "e.g., SHP-1006");
        shipmentIdField = (TextField) shipmentIdBox.getChildren().get(1);
        formContainer.getChildren().add(shipmentIdBox);

        // Origin and Destination (2 columns)
        HBox row1 = new HBox(15);
        VBox originBox = createFormField("Origin", "e.g., Mumbai");
        originField = (TextField) originBox.getChildren().get(1);
        originBox.setPrefWidth(250);
        
        VBox destinationBox = createFormField("Destination", "e.g., Delhi");
        destinationField = (TextField) destinationBox.getChildren().get(1);
        destinationBox.setPrefWidth(250);
        
        row1.getChildren().addAll(originBox, destinationBox);
        formContainer.getChildren().add(row1);

        // Cargo and Weight (2 columns)
        HBox row2 = new HBox(15);
        VBox cargoBox = createFormField("Cargo", "e.g., Electronics");
        cargoField = (TextField) cargoBox.getChildren().get(1);
        cargoBox.setPrefWidth(250);
        
        VBox weightBox = createFormField("Weight (kg)", "e.g., 100");
        weightField = (TextField) weightBox.getChildren().get(1);
        weightBox.setPrefWidth(250);
        
        row2.getChildren().addAll(cargoBox, weightBox);
        formContainer.getChildren().add(row2);

        // Status and ETA (2 columns)
        HBox row3 = new HBox(15);
        
        VBox statusBox = new VBox(5);
        Label statusLabel = new Label("Status");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #333;");
        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Pending", "In Transit", "Delivered", "Delayed");
        statusCombo.setValue("Pending");
        statusCombo.setPrefWidth(250);
        statusCombo.setStyle("-fx-font-size: 12px; -fx-padding: 8;");
        statusBox.getChildren().addAll(statusLabel, statusCombo);
        statusBox.setPrefWidth(250);
        
        VBox etaBox = createFormField("ETA", "e.g., 2025-06-20");
        etaField = (TextField) etaBox.getChildren().get(1);
        etaBox.setPrefWidth(250);
        
        row3.getChildren().addAll(statusBox, etaBox);
        formContainer.getChildren().add(row3);

        // Carrier
        VBox carrierBox = createFormField("Carrier", "e.g., BlueDart");
        carrierField = (TextField) carrierBox.getChildren().get(1);
        formContainer.getChildren().add(carrierBox);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button saveButton = new Button("💾 Save Shipment");
        saveButton.setPrefWidth(250);
        saveButton.setPrefHeight(40);
        saveButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #4caf50; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 20px; " +
            "-fx-border-radius: 5px;"
        );
        saveButton.setOnAction(e -> saveShipment());

        Button clearButton = new Button("🔄 Clear Form");
        clearButton.setPrefWidth(250);
        clearButton.setPrefHeight(40);
        clearButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #999; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10px 20px; " +
            "-fx-border-radius: 5px;"
        );
        clearButton.setOnAction(e -> clearForm());

        buttonBox.getChildren().addAll(saveButton, clearButton);
        formContainer.getChildren().add(buttonBox);

        contentArea.getChildren().add(formContainer);
        updateStatus("✓ Create Shipment Form Ready");
    }

    private VBox createFormField(String label, String placeholder) {
        VBox box = new VBox(5);
        Label labelText = new Label(label);
        labelText.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #333;"
        );
        
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-padding: 8px; " +
            "-fx-border-color: #ddd; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 4;"
        );
        textField.setPrefHeight(35);
        
        box.getChildren().addAll(labelText, textField);
        return box;
    }

    private void saveShipment() {
        new Thread(() -> {
            try {
                updateStatus("Saving shipment...");

                // Validate inputs
                if (shipmentIdField.getText().isEmpty() || originField.getText().isEmpty() || 
                    destinationField.getText().isEmpty() || cargoField.getText().isEmpty() ||
                    weightField.getText().isEmpty() || etaField.getText().isEmpty() || 
                    carrierField.getText().isEmpty()) {
                    updateStatusWithError("✗ Error: Please fill in all fields");
                    return;
                }

                // Create JSON payload
                JSONObject shipment = new JSONObject();
                shipment.put("id", shipmentIdField.getText());
                shipment.put("origin", originField.getText());
                shipment.put("destination", destinationField.getText());
                shipment.put("cargo", cargoField.getText());
                shipment.put("weight", Double.parseDouble(weightField.getText()));
                shipment.put("status", statusCombo.getValue());
                shipment.put("eta", etaField.getText());
                shipment.put("carrier", carrierField.getText());
                shipment.put("createdAt", java.time.LocalDate.now().toString());

                // Create HTTP request
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BACKEND_URL + "/shipments"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(shipment.toString()))
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    javafx.application.Platform.runLater(() -> {
                        updateStatus("✓ Shipment saved successfully!");
                        clearForm();
                        // Reload shipments after 1 second
                        new java.util.Timer().schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                loadShipments();
                            }
                        }, 1000);
                    });
                } else {
                    updateStatusWithError("✗ Error saving shipment: " + response.statusCode());
                }
            } catch (Exception e) {
                updateStatusWithError("✗ Error: " + e.getMessage());
            }
        }).start();
    }

    private void clearForm() {
        shipmentIdField.clear();
        originField.clear();
        destinationField.clear();
        cargoField.clear();
        weightField.clear();
        statusCombo.setValue("Pending");
        etaField.clear();
        carrierField.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}