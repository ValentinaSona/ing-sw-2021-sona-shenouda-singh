package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Static helper class that can modify ImageViews at runtime and make them selectable
 */
public class Modify {

    /**
     * Makes a resource selectable when there is need to select a certain resource in a certain depot
     * @param image the image of the resource to select
     * @param id the Id of the correspondent depot
     * @param map the map that should be updated at every selection
     * @param active true if this feature must be activated, false if it must be deactivated
     */
    public static void makeSelectable(ImageView image, Id id, Map<Id, Resource> map, boolean active) {
        if (active) {

            image.setOpacity(0.5);
            image.setOnMouseReleased(e -> {

                if(image.getOpacity() == 0.5) {
                    if (!map.containsKey(id)) map.put(id, GUIHelper.getInstance().getResFromImage((image.getImage())));
                    else map.get(id).setQuantity(map.get(id).getQuantity()+1);

                    image.setOpacity(1);
                }
                else if (image.getOpacity() == 1){
                    if (map.get(id).getQuantity() > 1) {
                        map.get(id).setQuantity(map.get(id).getQuantity()-1);
                    }
                    else map.remove(id);

                    image.setOpacity(0.5);
                }
            });
        }

        else image.setOnMouseReleased(e -> image.setOpacity(1));
    }

    /**
     * Makes all the resources of a depot selectable
     * @param depot the depot
     * @param map the map that should be updated at every selection
     * @param active true to activate this feature, false to deactivate it
     */
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

    /**
     * Makes special depots selectable
     * @param depot1 the first special depot HBox
     * @param depot2 the second special depot HBox
     * @param map the map that should be updated at every selection
     * @param active true to activate this feature, false to deactivate it
     */
    public static void makeSpecialDepotSelectable(HBox depot1, HBox depot2,  Map<Id, Resource> map, boolean active) {
        for (Node n : depot1.getChildren()) {
            makeSelectable((ImageView) n, Id.S_DEPOT_1, map, active);
        }
        for (Node n : depot2.getChildren()) {
            makeSelectable((ImageView) n, Id.S_DEPOT_2, map, active);
        }
    }

    /**
     * Makes the strongbox's resources selectable
     * @param strongbox the strongbox's GridPane
     * @param map the map that should be updated at every selection
     * @param active true to activate this feature, false to deactivate it
     */
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
