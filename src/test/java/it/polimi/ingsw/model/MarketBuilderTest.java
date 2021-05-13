package it.polimi.ingsw.model;

import it.polimi.ingsw.server.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


class MarketBuilderTest {

    @Test
    void build() {
        Market tray = MarketBuilder.build();

        HashMap<MarketMarble, Integer> marbleSet = new HashMap<>();
        marbleSet.put(MarketMarble.WHITE, 4);
        marbleSet.put(MarketMarble.BLUE, 2);
        marbleSet.put(MarketMarble.GREY, 2);
        marbleSet.put(MarketMarble.YELLOW, 2);
        marbleSet.put(MarketMarble.PURPLE, 2);
        marbleSet.put(MarketMarble.RED, 1);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {

                if (marbleSet.get(tray.getTray()[i][j]) > 1)
                    marbleSet.replace(tray.getTray()[i][j], marbleSet.get(tray.getTray()[i][j]) - 1);
                else marbleSet.remove(tray.getTray()[i][j]);

            }
        }

        assertEquals(marbleSet.get(tray.getExtra()), 1);
        marbleSet.remove(tray.getExtra());
        assertTrue(marbleSet.isEmpty());
        Market tray2 = MarketBuilder.build();
        assertNotEquals(Arrays.deepToString(tray2.getTray()), Arrays.deepToString(tray.getTray()));

        Market imported1 = MarketBuilder.build(tray.getTray(), tray.getExtra(), new HashMap<Player, List<MarketMarble>>());

        assertTrue(imported1 instanceof MarketTray);

        HashMap<Player, List<MarketMarble>> abilities = new HashMap<Player, List<MarketMarble>>() {{
            put(new Player("Nickname"), Arrays.asList(MarketMarble.BLUE));
        }};
        Market imported2 = MarketBuilder.build(tray2.getTray(), tray2.getExtra(), abilities);

        assertTrue(imported2 instanceof MarketTrayAbility);

    }

}