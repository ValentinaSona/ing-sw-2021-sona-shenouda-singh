package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class Modify {

    public static void makeSelectable(ImageView image, Id id, Map<Id, Resource> map, boolean active) {
        if (active) {
            image.setOnMouseReleased(e -> {

                if (!map.containsKey(id)) map.put(id, GUIHelper.getInstance().getResFromImage((image.getImage())));
                else map.get(id).setQuantity(map.get(id).getQuantity()+1);

                image.setDisable(true);
                image.setOpacity(0.5);
            });
        }

        else image.setOnMouseReleased(e -> {});
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

}
