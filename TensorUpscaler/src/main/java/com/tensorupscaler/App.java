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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    stage.setTitle("TensorUpscaler");
    javax.imageio.ImageIO.scanForPlugins();


    ImageView left = new ImageView();
    left.setPreserveRatio(true);
    left.setFitWidth(520);

    ImageView right = new ImageView();
    right.setPreserveRatio(true);
    right.setFitWidth(520);

    Button openBtn = new Button("Abrir");
    Button processBtn = new Button("Procesar");
    Button saveBtn = new Button("Guardar");

    ComboBox<Integer> scaleBox = new ComboBox<>();
    scaleBox.getItems().addAll(2, 3, 4);
    scaleBox.setValue(2);

    Slider sharpen = new Slider(0, 1, 0.6);
    sharpen.setPrefWidth(160);

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
            processBtn,
            saveBtn
    );
    controls.setPadding(new Insets(10));

    HBox images = new HBox(10, left, right);
    images.setPadding(new Insets(10));

    BorderPane root = new BorderPane();
    root.setTop(controls);
    root.setCenter(images);

    Scene scene = new Scene(root, 1100, 650);
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
