package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.TETileWrapper;
import byow.TileEngine.Tileset;

import java.awt.*;

import static byow.Core.MyUtils.isNumber;
import static byow.Core.MyUtils.isValidChar;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    static Long seed;
    public static final int RoomNum = 16;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    private Menu menu = new Menu(40, 40);
    // the world and the generator of world
    private TETile[][] world = new TETile[WIDTH][HEIGHT];
    private WorldGenerator worldGenerator;


    public void interactWithKeyboard() {
        menu.drawMenu();
        String inputString = "";
        char typedKey;
        while (true){
            typedKey = MyUtils.getNextKey();
            if (isNumber(typedKey) || isValidChar(typedKey)) {
                inputString += typedKey;
            }
            if (typedKey == 'S'){
                int stepIndex = inputString.indexOf("S");
                inputString = inputString.substring(1, stepIndex);
                System.out.println(inputString);
                break;
            }
        }
        renderWorld(inputString);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

//        input = input.toUpperCase();
//        int stepIndex = input.indexOf("S");
//        String number = input.substring(1, stepIndex);
        seed = Long.parseLong(input);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        WorldGenerator worldGenerator = new WorldGenerator(seed, finalWorldFrame, false);
        return worldGenerator.generateWorld();
    }

    public void renderWorld(String inputString){
        world = interactWithInputString(inputString);
        ter.initialize(WIDTH,HEIGHT);
        ter.renderFrame(world);
    }
    public void renderWorld(TETile[][] world){
        ter.initialize(WIDTH,HEIGHT);
        ter.renderFrame(world);
    }


}
