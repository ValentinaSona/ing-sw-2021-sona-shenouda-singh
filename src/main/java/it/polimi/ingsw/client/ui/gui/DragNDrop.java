package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.ResourceType;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.List;


public class DragNDrop {

    private Id currentDragged;
    private Image currentDraggedImage = null;

    private static DragNDrop singleton;

    public static DragNDrop getInstance() {
        if (singleton == null) singleton = new DragNDrop();
        return singleton;
    }

    public void setDepotDraggable (HBox source, Id id, boolean active) {

        if(active) {
            Group root = new Group();

            source.setOnDragDetected(event -> {
                /* drag was detected, start drag-and-drop gesture */

                /* allow any transfer mode */
                Dragboard db = source.startDragAndDrop(TransferMode.ANY);

                Image depotIm = GUIHelper.getInstance().getImage(ResourceType.COIN, 160, 160);

                for (DepotView d : GUIHelper.getInstance().getClientView().getWarehouse()) {
                    if (d.getId() == id) depotIm = GUIHelper.getInstance().getImage(d.getResource().getResourceType(), 160, 160);
                }

                db.setDragView(depotIm);

                ClipboardContent content = new ClipboardContent();

                content.putImage(depotIm);
                db.setContent(content);

                currentDragged = id;

                event.consume();
            });

            /* the drag-and-drop gesture ended */
            source.setOnDragDone(Event::consume);
        }
        else {
            source.setOnDragDetected(event -> {});
            source.setOnDragDone(event -> {});
        }
    }

    public void setDraggableResource (ImageView source, boolean active) {

        if(active) {
            Group root = new Group();

            source.setOnDragDetected(event -> {
                /* drag was detected, start drag-and-drop gesture */

                /* allow any transfer mode */
                Dragboard db = source.startDragAndDrop(TransferMode.ANY);

                var tempIm = source.getImage();

                db.setDragView(tempIm);

                ClipboardContent content = new ClipboardContent();

                content.putImage(tempIm);
                db.setContent(content);

                currentDraggedImage = tempIm;

                event.consume();
            });

            /* the drag-and-drop gesture ended */
            source.setOnDragDone(event -> {
                currentDraggedImage = null;
                if (GUIHelper.getInstance().getClientView().getTempResources().size() == 0) {
                    Platform.runLater(() -> GUIHelper.getInstance().getCurrentGameController().update());
                }

                event.consume();
            });
        }
        else {
            source.setOnDragDetected(event -> {});
            source.setOnDragDone(event -> {});
        }
    }

    public void setDroppable(HBox target, Id id) {
        target.setOnDragOver(event -> {
            /* data is dragged over the target */

            /*
             * accept it only if it is not dragged from the same node and if it
             * has a string data
             */
            if (event.getGestureSource() != target) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        });

        target.setOnDragEntered(event -> {
            /* the drag-and-drop gesture entered the target */
            /* show to the user that it is an actual gesture target */
            if (event.getGestureSource() != target) {
                target.setStyle("-fx-background-color: rgba(245, 179, 66, 0.5)");
            }

            event.consume();
        });

        target.setOnDragExited(event -> {
            /* mouse moved away, remove the graphical cues */
            target.setStyle("-fx-background-color: transparent");

            event.consume();
        });

        target.setOnDragDropped(event -> {
            /* data dropped */
            if (currentDraggedImage != null) UIController.getInstance().depositIntoWarehouse(id, GUIHelper.getInstance().getResFromImage(currentDraggedImage));

            else {
                UIController.getInstance().tidyWarehouse(currentDragged, id);

                event.setDropCompleted(true);

                event.consume();
            }
        });
    }

    public void setDroppableResource(HBox target, Id id) {
        target.setOnDragDropped(event -> {

            UIController.getInstance().depositIntoWarehouse(id, GUIHelper.getInstance().getResFromImage(currentDraggedImage));

            event.setDropCompleted(true);

            event.consume();
        });
    }

}


