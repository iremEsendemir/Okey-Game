import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OkeyGame {

    Player[] players;
    Tile[] tiles;

    Tile lastDiscardedTile;

    int currentPlayerIndex = 0;

    public OkeyGame() {
        players = new Player[4];
    }

    public void createTiles() {
        tiles = new Tile[104];
        int currentTile = 0;

        // two copies of each color-value combination, no jokers
        for (int i = 1; i <= 13; i++) {
            for (int j = 0; j < 2; j++) {
                tiles[currentTile++] = new Tile(i,'Y');
                tiles[currentTile++] = new Tile(i,'B');
                tiles[currentTile++] = new Tile(i,'R');
                tiles[currentTile++] = new Tile(i,'K');
            }
        }
    }

    /*
     * Distributes the starting tiles to the players
     * player at index 0 gets 15 tiles and starts first
     * other players get 14 tiles
     * this method assumes the tiles are already sorted
     */
    public void distributeTilesToPlayers() {
        for(int i = 0; i < 15; i++){
            players[currentPlayerIndex].playerTiles[i] = tiles[i];
            players[currentPlayerIndex].numberOfTiles++;
            tiles[i] = null;
        }
        for(int i = 1; i < 4; i++) {
            for(int j = 0; j < 14; j++){
                players[i].playerTiles[j] = tiles[j + 1 + i*14];
                players[i].numberOfTiles++;
                tiles[j + 1 + i*14] = null;
            }
        }
    }

    /*
     * Get the last discarded tile for the current player
     * (this simulates picking up the tile discarded by the previous player)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getLastDiscardedTile() {
        players[currentPlayerIndex].addTile(lastDiscardedTile);
        return lastDiscardedTile.toString();
    }

    /*
     * Get the top tile from tiles array for the current player
     * that tile is no longer in the tiles array (this simulates picking up the top tile)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getTopTile() {
        int topTileInt = 57; // just to avoid 'may not be initialized' error

        for(int i=0; tiles[i]==null; i++){
            topTileInt = i+1; // i is the last null index
        }
        Tile topTile = tiles[topTileInt];
        players[currentPlayerIndex].addTile(topTile);

        tiles[topTileInt] = null;

        return topTile.toString();
    }

    /*
     * Should randomly shuffle the tiles array before game starts
     */
    public void shuffleTiles() {
        List<Tile> list = new ArrayList<>(Arrays.asList(tiles));
        Collections.shuffle(list);
        for(int i = 0; i < tiles.length; i++) {
            tiles[i] = list.get(i);
        } 
    }

    /*
     * Check if game still continues, should return true if current player
     * finished the game. Use calculateLongestChainPerTile method to get the
     * longest chains per tile.
     * To win, you need one of the following cases to be true:
     * - 8 tiles have length >= 4 and remaining six tiles have length >= 3 the last one can be of any length
     * - 5 tiles have length >= 5 and remaining nine tiles have length >= 3 the last one can be of any length
     * These are assuming we check for the win condition before discarding a tile
     * The given cases do not cover all the winning hands based on the original
     * game and for some rare cases it may be erroneous but it will be enough
     * for this simplified version
     */
    public boolean didGameFinish() {
        int[] tileLengths = players[currentPlayerIndex].calculateLongestChainPerTile();
        int countOfTilesLongerThan4 = 0;
        int countOfTilesLongerThan3 = 0;
        for(int i = 0; i < players[currentPlayerIndex].numberOfTiles; i++) {
            if(tileLengths[i] >= 4) {
                countOfTilesLongerThan4++;
            }
            else if(tileLengths[i] >= 3) {
                countOfTilesLongerThan3++;
            }
        }
        if((countOfTilesLongerThan4 == 8 && countOfTilesLongerThan3 == 6) || 
        (countOfTilesLongerThan4 == 5 && countOfTilesLongerThan3 == 9)) {
            return true;
        }
        return false;
    }

    /*
     * Pick a tile for the current computer player using one of the following:
     * - picking from the tiles array using getTopTile()
     * - picking from the lastDiscardedTile using getLastDiscardedTile()
     * You may choose randomly or consider if the discarded tile is useful for
     * the current status. Print whether computer picks from tiles or discarded ones.
     */
    public void pickTileForComputer() {
        int totalForColor = 0;
        int totalForValue = 0;
        int biggestBefore = 0;

        players[currentPlayerIndex].sortTilesValueFirst();
        for(int i = 0; i < players[currentPlayerIndex].numberOfTiles; i++) {
            if (players[currentPlayerIndex].getTiles()[i].getValue() >= 3) {
                totalForValue++;
            }
        }

        players[currentPlayerIndex].sortTilesColorFirst();
        for(int i = 0; i < players[currentPlayerIndex].numberOfTiles; i++) {
            if (players[currentPlayerIndex].getTiles()[i].getValue() >= 3) {
                totalForColor++;
            }
        }

        if (totalForColor > totalForValue) {
            totalForColor = biggestBefore;
        } else {
            totalForValue = biggestBefore;
        }

        String forDiscarded = getLastDiscardedTile();  

        int totalForColorDiscarded = 0;
        int totalForValueDiscarded = 0;
        int biggestAfter = 0;

        players[currentPlayerIndex].sortTilesValueFirst();
        for(int i = 0; i < players[currentPlayerIndex].numberOfTiles; i++) {
            if (players[currentPlayerIndex].getTiles()[i].getValue() >= 3) {
                totalForValueDiscarded++;
            }
        }

        players[currentPlayerIndex].sortTilesColorFirst();
        for(int i = 0; i < players[currentPlayerIndex].numberOfTiles; i++) {
            if (players[currentPlayerIndex].getTiles()[i].getValue() >= 3) {
                totalForColorDiscarded++;
            }
        }

        if (totalForColorDiscarded > totalForValueDiscarded) {
            totalForColor = biggestAfter;
        } else {
            totalForValue = biggestAfter;
        }

        if (biggestBefore > biggestAfter) {
            discardTile(14);
            getTopTile();
        } 

    }

    /*
     * TODO: Current computer player will discard the least useful tile.
     * For this use the findLongestChainOf method in Player class to calculate
     * the longest chain length per tile of this player,
     * then choose the tile with the lowest chain length and discard it
     * this method should print what tile is discarded since it should be
     * known by other players
     */
    public void discardTileForComputer() {
        Player player = players[currentPlayerIndex];
        Tile tileToDiscard = player.getTiles()[0]; 
        Tile currentTile;

        for(int i=1; i< player.getTiles().length; i++){
            currentTile = player.getTiles()[i];
            if(player.findLongestChainOf(currentTile)<=player.findLongestChainOf(tileToDiscard)){
                tileToDiscard = currentTile;
            }
        }
        discardTile(player.findPositionOfTile(tileToDiscard));

        System.out.println(tileToDiscard.toString() + " is discarded.");
    }

    /*
     * TODO: discards the current player's tile at given index
     * this should set lastDiscardedTile variable and remove that tile from
     * that player's tiles
     */
    public void discardTile(int tileIndex) {
        lastDiscardedTile = players[currentPlayerIndex].getAndRemoveTile(tileIndex);
    }

    public void currentPlayerSortTilesColorFirst() {
        players[currentPlayerIndex].sortTilesColorFirst();
    }

    public void currentPlayerSortTilesValueFirst() {
        players[currentPlayerIndex].sortTilesValueFirst();
    }

    public void displayDiscardInformation() {
        if(lastDiscardedTile != null) {
            System.out.println("Last Discarded: " + lastDiscardedTile.toString());
        }
    }

    public void displayCurrentPlayersTiles() {
        players[currentPlayerIndex].displayTiles();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

      public String getCurrentPlayerName() {
        return players[currentPlayerIndex].getName();
    }

    public void passTurnToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }

    public void setPlayerName(int index, String name) {
        if(index >= 0 && index <= 3) {
            players[index] = new Player(name);
        }
    }

}
