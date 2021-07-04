package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.gui.JFXControllers.MarketGUIController;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class contains method to enable drag and drop features in certain parts of the GUI
 */

public class DragNDrop {

    private Id currentDragged;
    private Image currentDraggedImage = null;

    private static DragNDrop singleton;

    public static DragNDrop getInstance() {
        if (singleton == null) singleton = new DragNDrop();
        return singleton;
    }

    /**
     * Makes one entire depot draggable
     * @param source the depot
     * @param id the corresponding id of the depot
     * @param active true for activating the draggable feature, false to disable it
     */
    public void setDepotDraggable (HBox source, Id id, boolean active) {

        if(active) {
            source.setOnDragDetected(event -> {
                // Allow any transfer mode
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

            // the drag-and-drop gesture ended
            source.setOnDragDone(Event::consume);
        }
        else {
            source.setOnDragDetected(event -> {});
            source.setOnDragDone(event -> {});
        }
    }

    /**
     * Makes a single resource draggable
     * @param source the ImageView of the resource
     * @param active true for activating the draggable feature, false to disable it
     */
    public void setDraggableResource (ImageView source, boolean active) {

        if(active) {
            draggableImage(source);

            // the drag-and-drop gesture ended
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

    /**
     * Makes possible to drop draggable items on a certain depot
     * @param target the depot
     * @param id the Id associated with the depot
     */
    public void setDroppable(HBox target, Id id) {
        // data is dragged over the target
        target.setOnDragOver(event -> {
            // Accept it only if it is not dragged from the same node
            if (event.getGestureSource() != target)
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            event.consume();
        });

        target.setOnDragEntered(event -> {
            if (event.getGestureSource() != target)
                target.setStyle("-fx-background-color: rgba(245, 179, 66, 0.5)");

            event.consume();
        });

        target.setOnDragExited(event -> {
            target.setStyle("-fx-background-color: transparent");

            event.consume();
        });

        target.setOnDragDropped(event -> {
            if (currentDraggedImage != null) UIController.getInstance().depositIntoWarehouse(id, GUIHelper.getInstance().getResFromImage(currentDraggedImage));

            else {
                UIController.getInstance().tidyWarehouse(currentDragged, id);
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    /**
     * Makes possible to drop a resource on top of a jolly resource
     * @param target the ImageView of the resource to make droppable
     * @param box the VBox containing the ImageViews of the resources
     * @param res an array with the resources themselves
     */
    public void setMarketWhiteDroppable(ImageView target, VBox box, Resource[] res) {
        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        target.setOnDragEntered(Event::consume);

        target.setOnDragExited(Event::consume);

        target.setOnDragDropped(event -> {
            /*
            int index = 0;

            for (int i = 0; i < res.length; i ++) {
                if (box.getChildren().get(i).equals(target)) break;
                if (GUIHelper.getInstance().getResFromImage( ( (ImageView) box.getChildren().get(i) ).getImage() ).getResourceType().equals(ResourceType.JOLLY)) index++;
            }
            */
            if (currentDraggedImage != null) {
                for(int i = 0; i < res.length; i++) {
                    if (res[i] == null) {
                        res[i] = GUIHelper.getInstance().getResFromImage(currentDraggedImage);
                        target.setImage(currentDraggedImage);
                        target.setFitWidth(GUISizes.get().chosenMarbles());
                        target.setPreserveRatio(true);
                        break;
                    }
                }
            }
        });
    }

    /**
     * Makes a resource from a WhiteMarbleAbility draggable
     * @param source the ImageView of the resource
     * @param active true for activating the draggable feature, false to disable it
     */
    public void setMarketWhiteDraggable(ImageView source, boolean active) {
        if(active) {
            draggableImage(source);

            source.setOnDragDone(e -> {
                currentDraggedImage = null;
                Platform.runLater(() -> ((MarketGUIController)GUIHelper.getInstance().getCurrentGameController()).acceptChoice());
            });
        }
        else {
            source.setOnDragDetected(event -> {});
            source.setOnDragDone(event -> {});
        }
    }

    /**
     * Makes a generic ImageView draggable
     * @param source the ImageView
     */
    private void draggableImage(ImageView source) {
        source.setOnDragDetected(event -> {
            Dragboard db = source.startDragAndDrop(TransferMode.ANY);

            var tempIm = source.getImage();

            db.setDragView(tempIm);

            ClipboardContent content = new ClipboardContent();

            content.putImage(tempIm);
            db.setContent(content);

            currentDraggedImage = tempIm;

            event.consume();
        });
    }

}


