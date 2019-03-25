package Paint;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class paintController {

    @FXML
    private Canvas canvas;

    @FXML
    private ToggleButton draw;

    @FXML
    private ToggleButton lineButton;

    @FXML
    private ToggleButton rectangleButton;

    @FXML
    private ToggleButton ellipseButton;

    @FXML
    private ToggleButton textButton;

    @FXML
    private ToggleButton eraser;

    @FXML
    private ToggleButton reset;

    @FXML
    private TextField textField;

    @FXML
    private CheckBox filling;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Slider fontSizeSlider;

    @FXML
    private Label fontSizeLabel;

    private Line line;
    private Rectangle rectangle;
    private Ellipse ellipse;

    public void initialize() {
        ToggleButton[] arrayTB = {draw, lineButton, textButton, rectangleButton, ellipseButton, eraser, reset};
        ToggleGroup groupTG = new ToggleGroup();
        for (ToggleButton x : arrayTB){
            x.setMinWidth(75);
            x.setToggleGroup(groupTG);
            x.setCursor(Cursor.HAND);
        }
        draw.setSelected(true);
        colorPicker.setValue(Color.BLACK);
        fontSizeSlider.setValue(10);

        line = new Line();
        rectangle = new Rectangle();
        ellipse = new Ellipse();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(fontSizeSlider.getValue());

        fontSizeSlider.setMin(1);
        fontSizeSlider.setMax(50);
        fontSizeSlider.setShowTickLabels(true);
        fontSizeSlider.setShowTickMarks(true);
        fontSizeSlider.valueProperty().addListener(e->{
            double value = fontSizeSlider.getValue();
            String str = String.format("%.0f", value);
            fontSizeLabel.setText(str);
            gc.setLineWidth(value);
        });

        reset.setOnAction(e -> {
            gc.clearRect(1,1,gc.getCanvas().getWidth()-2,gc.getCanvas().getHeight()-2);
        });

        canvas.setOnMouseDragged(e -> {
            double size = fontSizeSlider.getValue();
            double x = e.getX() - size/2;
            double y = e.getY() - size/2;
            if(draw.isSelected()){
                gc.setFill(colorPicker.getValue());
                gc.fillRect(x, y, size, size);
        }
            else if (eraser.isSelected()) {
                gc.clearRect(x, y, size, size);
                }
        });

        canvas.setOnMousePressed(e -> {
            gc.setStroke(colorPicker.getValue());
            gc.setFill(colorPicker.getValue());
            if(textButton.isSelected()){
                gc.setLineWidth(1);
                gc.setFont(Font.font(fontSizeSlider.getValue()));
                gc.fillText(textField.getText(), e.getX(), e.getY());
                gc.strokeText(textField.getText(), e.getX(), e.getY());
            }
            else if(lineButton.isSelected()){
                line.setStartX(e.getX());
                line.setStartY(e.getY());
            }
            else if(rectangleButton.isSelected()){
                rectangle.setX(e.getX());
                rectangle.setY(e.getY());
            }
            else if(ellipseButton.isSelected()){
                ellipse.setCenterX(e.getX());
                ellipse.setCenterY(e.getY());
            }
        });

        canvas.setOnMouseReleased(e ->{
            if(lineButton.isSelected()){
                gc.strokeLine(line.getStartX(),line.getStartY(),e.getX(), e.getY());
            }
            else if(rectangleButton.isSelected()){
                rectangle.setWidth(Math.abs(rectangle.getX()-e.getX()));
                rectangle.setHeight(Math.abs(rectangle.getY()-e.getY()));
                if(rectangle.getX() > e.getX())
                    rectangle.setX(e.getX());
                if(rectangle.getY() > e.getY())
                    rectangle.setY(e.getY());

                if(filling.isSelected())
                    gc.fillRect(rectangle.getX(),rectangle.getY(),rectangle.getWidth(),rectangle.getHeight());
                else
                    gc.strokeRect(rectangle.getX(),rectangle.getY(),rectangle.getWidth(),rectangle.getHeight());
            }
            else if(ellipseButton.isSelected()){
                ellipse.setRadiusX(Math.abs(ellipse.getCenterX()-e.getX()));
                ellipse.setRadiusY(Math.abs(ellipse.getCenterY()-e.getY()));
                if(ellipse.getCenterX() > e.getX())
                    ellipse.setCenterX(e.getX());
                if(ellipse.getCenterY() > e.getY())
                    ellipse.setCenterY(e.getY());

                if(filling.isSelected())
                    gc.fillOval(ellipse.getCenterX(),ellipse.getCenterY(),ellipse.getRadiusX(),ellipse.getRadiusY());
                else
                    gc.strokeOval(ellipse.getCenterX(),ellipse.getCenterY(),ellipse.getRadiusX(),ellipse.getRadiusY());
            }
        });
    }

   public void onOpen() {
        FileChooser fileOpen = new FileChooser();
        fileOpen.setTitle("Open Image");
        File file = fileOpen.showOpenDialog(null);
        try{
            InputStream io = new FileInputStream(file);
            Image image = new Image(io);
            canvas.getGraphicsContext2D().drawImage(image, 0, 0);
        } catch(IOException ioe){
            System.out.println("Failed to open image: " + ioe);
        }
    }

    public void onSave() {
        FileChooser fileSave = new FileChooser();
        fileSave.setTitle("Save Image");
        Image snapshot = canvas.snapshot(null, null);
        File file = fileSave.showSaveDialog(null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (Exception e) {
        }
    }

    public void onExit() {
        Platform.exit();
    }

}