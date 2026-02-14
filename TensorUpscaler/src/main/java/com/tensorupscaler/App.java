package com.tensorupscaler;

import com.tensorupscaler.core.DefaultImageProcessor;
import com.tensorupscaler.core.ImageProcessor;
import com.tensorupscaler.core.Kernels;
import com.tensorupscaler.core.SimpleTensorCodec;
import com.tensorupscaler.core.SimpleTensorOps;
import java.awt.image.BufferedImage;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.File;

public class App extends Application {

  private BufferedImage original;
  private BufferedImage result;

  private final ImageProcessor processor =
          new DefaultImageProcessor(new SimpleTensorCodec(), new SimpleTensorOps());

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    Label title = new Label("TensorUpscaler");
    title.setFont(Font.font("System", FontWeight.BOLD, 28));

    Label subtitle = new Label("Escalado de imágenes mediante operaciones matriciales y tensores.");
    subtitle.setStyle("-fx-text-fill: #0E0F19;");

    VBox header = new VBox(5, title, subtitle);
    header.setAlignment(Pos.CENTER);
    header.setPadding(new Insets(20));

    stage.setTitle("TensorUpscaler");
    javax.imageio.ImageIO.scanForPlugins();


    ImageView left = new ImageView();
    left.setPreserveRatio(true);
    left.setFitWidth(520);

    ImageView right = new ImageView();
    right.setPreserveRatio(true);
    right.setFitWidth(520);

    MFXButton openBtn = new MFXButton("Abrir");
    MFXButton processBtn = new MFXButton("Procesar");
    MFXButton saveBtn = new MFXButton("Guardar");
    openBtn.getStyleClass().add("round-button");
    processBtn.getStyleClass().add("round-button");
    saveBtn.getStyleClass().add("round-button");
    openBtn.setCursor(javafx.scene.Cursor.HAND);
    processBtn.setCursor(javafx.scene.Cursor.HAND);
    saveBtn.setCursor(javafx.scene.Cursor.HAND);



    MFXComboBox<Integer> scaleBox = new MFXComboBox<>();
    scaleBox.getItems().addAll(2, 3, 4);
    scaleBox.selectItem(2);
    scaleBox.setCursor(Cursor.HAND);

    Slider sharpen = new Slider(0, 1, 0.6);
    sharpen.setPrefWidth(160);
    sharpen.setCursor(Cursor.HAND);

    sharpen.setStyle(
            "-fx-control-inner-background: #ccd5ae;" +
                    "-fx-accent: #2d3d3d;"
    );

    Label sharpenValue = new Label(String.format("%.2f", sharpen.getValue()));
    sharpenValue.setStyle("-fx-font-weight: bold; -fx-min-width: 40; -fx-alignment: center;");

    sharpen.valueProperty().addListener((obs, oldVal, newVal) -> {
      sharpenValue.setText(String.format("%.2f", newVal.doubleValue()));
    });

    VBox sharpenContainer = new VBox(2, sharpenValue, sharpen);
    sharpenContainer.setAlignment(Pos.CENTER);

    Label sharpenLabel = new Label("Nitidez");

    openBtn.setOnAction(e -> {
      try {
        FileChooser fc = new FileChooser();
        fc.setTitle("Abrir imagen");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.tif", "*.tiff")
        );

        File f = fc.showOpenDialog(stage);
        if (f == null) return;

        original = ImageIO.read(f);

        if (original == null) {
          showError("Formato no soportado",
                  "No se pudo leer la imagen.",
                  "El archivo seleccionado no es una imagen válida o no está soportado por ImageIO.");
          return;
        }

        left.setImage(SwingFXUtils.toFXImage(original, null));
        right.setImage(null);
        result = null;

      } catch (Exception ex) {
        ex.printStackTrace();
        showError("Error al abrir imagen",
                "Ocurrió un error al intentar abrir el archivo.",
                ex.getMessage());
      }
    });

    processBtn.setOnAction(e -> {
      try {
        if (original == null) return;
        int scale = scaleBox.getValue();
        float amount = (float) sharpen.getValue();

        result = processor.process(original, scale, amount, Kernels.sharpen());
        right.setImage(SwingFXUtils.toFXImage(result, null));
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    saveBtn.setOnAction(e -> {
      try {
        if (result == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar imagen");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg")
        );
        File f = fc.showSaveDialog(stage);
        if (f == null) return;

        String name = f.getName().toLowerCase();
        String format = (name.endsWith(".jpg") || name.endsWith(".jpeg")) ? "jpg" : "png";
        ImageIO.write(result, format, f);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    HBox controls = new HBox(10,
            openBtn,
            new Label("Escala"),
            scaleBox,
            sharpenLabel,
            sharpen,
            sharpenContainer,
            processBtn,
            saveBtn
    );
    controls.getStyleClass().add("controls-box");
    controls.setPadding(new Insets(10));
    controls.setAlignment(Pos.CENTER);


    HBox images = new HBox(10, left, right);
    images.setPadding(new Insets(10));
    images.getStyleClass().add("images-box");
    images.setAlignment(Pos.CENTER);

    BorderPane root = new BorderPane();
    root.getStyleClass().add("root");
    VBox topSection = new VBox(header, controls);
    topSection.setAlignment(Pos.CENTER);

    root.setTop(topSection);
    root.setCenter(images);

    Scene scene = new Scene(root, 1100, 650);
    scene.getStylesheets().add(
            MaterialFXStylesheets.DEFAULT.get().toExternalForm()
    );
    scene.getStylesheets().add(
            getClass().getResource("/styles.css").toExternalForm()
    );
    stage.setScene(scene);
    stage.show();
  }

  private void showError(String title, String header, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }

}
