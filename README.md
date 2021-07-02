# Progetto di ingegneria del Software 2020/2021: Masters of Renaissance

**Professor:** Gianpaolo Cugola

**Group code:** GC05

**Group composition:** Antony Shenouda, Raul Singh, Valentina Sona.


| Feature                             | Description                                                  |
| ----------------------------------- | ------------------------------------------------------------ |
| Full rules                          | Can play both multiplayer and solo games                     |
| CLI + GUI + Socket                  | Both interfaces work over the network as required            |
| FA1: Partita locale                 | A client can play a singleplayer game without establishing a connection to the server. |
| FA2: Resilienza alle disconnessioni | If the disconnection happens during the setup (leader and starting resource selection) the game is canceled and must be recreated. If a player disconnects from the game, it goes on without them. If all players disconnect, the game waits for at least one player to reconnect and gives them the turn. If the player disconnects from a remote solo game, Lorenzo makes a single move and waits for the reconnection. Local solo games do not access the server and therefore do not benefit from this feature.<br />If they disconnected after buying marbles, the player is prompted to deposit the acquired resources after reconnection. If two white marble abilities are active, the server automatically picks one of them.<br />If they disconnected while activating productions, the already selected productions are activated.<br />If they disconnected while buying a card, the card is not bought. |
| FA3: Persistenza                    | At any time during their turn, the player can activate the save game function. This ends the game for all players, saving the game on the server. This feature is implemented for multiplayer and remote solo games - local games do not have access to the server and cannot save the game. **Important**: for this feature to work, a folder called `saved_game` has to be present in the folder from which the program is run. Saving again overwrites the previous save. |


## Coverage: // TO UPDATE
![](/Coverage.png?raw=true)

## Running the program

The program requires one command line argument amongst `cli`, `gui`, or `server` to run.

The CLI uses UTF-8 characters and ANSI color escapes, therefore requiring the terminal emulator to have these features enabled, and a font that contains UTF-8 characters selected.
For windows, this has been tested with Git-for-Windows' Git Bash terminal emulator.

The clients can be run from anywhere, while the server requires that it is run in a folder that has `./saved_games` as a sub-folder to support persistence.


`\path\to\java -jar Masters.jar cli`

`\path\to\java -jar Masters.jar gui`

`\path\to\java -jar Masters.jar server`

## Testing information
From a cli client and while in the main game menu, selecting `1492` instead of one of the displayed options causes the game to end for all players (gui clients included) as if a victory condition had been met.  

The precompiled jar file supplied in the `\deliverables` folder uses localhost as the server url and a preselected port. To change this behaviour either:
+ Change the values in `it/polimi/ingsw/utils/Constant.java` and recompile the program with `mvn package`
+ Supply two additional command line arguments when running the program, `<hostname>` and `<port>` (needs to be done for the server and for every client) e.g: 

`\path\to\java -jar Masters.jar cli 127.0.0.0 9017`

`\path\to\java -jar Masters.jar gui 127.0.0.0 9017`

`\path\to\java -jar Masters.jar server 127.0.0.0 9017`

No checks are implemented to verify whether the supplied arguments are correct.


To test specific game situations, one may save a game and modify the save files on the server - they are easily readable since they are written in json. 
Given that the use-case of this feature is that of a remote server to which the player has no access, no checks are implemented to assure that the files are valid and have not been tampered with. 
So, while useful for testing, no guarantees of any kind are given that this endeavor will not result in unplayable and/or illegal game states.

One notable example: setting one's leader cards to active, while not editing the ability maps in the markets or adding the depot/production to the respective lists (or vice versa) renders the abilities unattainable and/or malfunctioning. 

