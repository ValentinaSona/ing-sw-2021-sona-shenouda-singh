package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class Modify {

    public static void makeSelectable(ImageView image, Id id, Map<Id, Resource> map, boolean active) {
        if (active) {
            image.setOpacity(0.5);
            image.setOnMouseReleased(e -> {

                if (!map.containsKey(id)) map.put(id, GUIHelper.getInstance().getResFromImage((image.getImage())));
                else map.get(id).setQuantity(map.get(id).getQuantity()+1);

                image.setDisable(true);
                image.setOpacity(1);
            });
        }

        else image.setOnMouseReleased(e -> {image.setOpacity(1);});
    }

    public static void makeDepotResourcesSelectable(VBox depot, Map<Id, Resource> map, boolean active) {
        if (active) {
            var depots = depot.getChildren();

            for (Node n : ((HBox)depots.get(0)).getChildren()) {
                makeSelectable((ImageView) n, Id.DEPOT_1, map, true);
            }
            for (Node n : ((HBox)depots.get(1)).getChildren()) {
                makeSelectable((ImageView) n, Id.DEPOT_2, map, true);
            }
            for (Node n : ((HBox)depots.get(2)).getChildren()) {
                makeSelectable((ImageView) n, Id.DEPOT_3, map, true);
            }
        }
    }

    public static void makeStrongboxSelectable(GridPane strongbox, Map<Id, Resource> map, boolean active) {
        if (active) {
            var res = strongbox.getChildren();

            for (Node n : res) {
                if (!(n instanceof Button)){
                    switch (GUIHelper.getInstance().getResFromImage(((ImageView)n).getImage()).getResourceType()) {
                        case COIN -> makeSelectable((ImageView) n, Id.STRONGBOX_COIN, map, true);
                        case SHIELD -> makeSelectable((ImageView) n, Id.STRONGBOX_SHIELD, map, true);
                        case SERVANT -> makeSelectable((ImageView) n, Id.STRONGBOX_SERVANT, map, true);
                        case STONE -> makeSelectable((ImageView) n, Id.STRONGBOX_STONE, map, true);
                    }
                }
            }
        }
    }

}
