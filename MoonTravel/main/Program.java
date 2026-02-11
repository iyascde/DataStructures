package MoonTravel.main;

import java.awt.event.KeyEvent;
import java.io.IOException;

import edu.ftdev.KeyInterceptor.KeyHook;
import edu.ftdev.MouseInterceptor.MouseHook;
import edu.ftdev.Map.MoonMap;

public class Program {
    // #region: [Private] fields
    // State enumerator for the various background images of the map
    private enum State {
        RAW,        // "raw" image background
        CONTRAST,   // "contrasted" image background
        PIXELATE    // "pixelated" image background
    };

    // MoonMap object, used for loading and manipulating the map image
    private static MoonMap _moonMap = null;
    // MapModel object, containing the route start and target areas and implementing the routing algorithm.
    private static MapModel _model = null;
    // The active state of the map("raw", "contrasted" or "pixelated")
    private static State _crtState = State.RAW;
    // #endregion: [Private] fields

    // #region: [Private] Mouse hooks
    /**
     * Hooked to mouse left button clicks. When the user clicks the mouse left button,
     * the route endpoints are being reset.
     * @param mouseEvent the location of the mouse event used to infer the x,y position of the click. 
     * @param args not used.
     * @see MapModel#resetRoute(int, int)
     */
    private static MouseHook _onMouse = (mouseEvent, args) -> {
        Integer x = _moonMap.getX(mouseEvent);
        Integer y = _moonMap.getY(mouseEvent);
        if (_model.resetRoute(x, y)) {
            // restore the background snapshot for the current state
            _moonMap.restore(_crtState.name());
            // update and overlay the start and target route endpoints
            _model.resetTerrain();
            // repaint the map model
            _model.repaint();
        }
    };
    // #endregion: [Private] Mouse hooks

    // #region: [Private] Keyboard hooks
    /**
     * Hooked to LEFT/RIGHT/UP/DOWN.
     * When pressing UP/DOWN or LEFT/RIGHT the background of the map is
     * cycled through "raw" > "contrasted" > "pixelated" images.
     * @param keyEvent the key event, used to infer the specific key being pressed. 
     * @param args not used.
     */
    private static KeyHook _onKey = (keyEvent, args) -> {
        int offset = 0;
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_RIGHT:
                offset = 1;
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_LEFT:
                offset = -1;
                break;
        }
        _crtState = State.values()[(_crtState.ordinal() + offset + State.values().length) % State.values().length];
        _moonMap.restore(_crtState.name());
        _model.repaint();
    };

    /**
     * Hooked to the 'B', 'D' and 'A' keys.
     * When any of these keys get pressed, the corresponding routing algorithm,
     * BFS, Dijkstra or AStart gets executed linking the start to target areas, if both are defined.
     * @param keyEvent the key event - not used.
     * @param args the RouteAlgo object indicating which routing algorithm to be executed.
     */
    private static KeyHook _onRoute = (keyEvent, args) -> {
        MapModel.RouteAlgo algo = (MapModel.RouteAlgo)args[0];
        // restore the background shapshot
        _moonMap.restore(_crtState.name());
        // reset the internal state of the map model
        _model.resetTerrain();
        // execute the specific routing algorithm
        _moonMap.setStatusMessage("Searching route via %s algorithm!", algo.name());
        boolean success = false;
        switch(algo) {
        case BFS:
            success = _model.routeBFS();
            break;
        case DIJKSTRA:
            success = _model.routeDijkstra();
            break;
        case ASTAR:
            success = _model.routeAStar();
            break;
        }
        if (success) {
            double distance = _model.repaintRoute();
            _moonMap.setStatusMessage("%s route length: %.2f", algo.name(), distance);
        } else {
            _moonMap.setStatusMessage("%s found NO route possible!", algo.name());
            _moonMap.repaint();
        }
    };
    // #endregion: [Private] Keyboard hooks

    /**
     * Main entry point in the program.
     * @param args - not used
     * @throws IOException if the specific map raw  cannot be loaded from the disk. 
     */
    public static void main(String[] args) throws IOException {
        // load and open the MoonMap image and take the "raw" snapshot of the map.
        _moonMap = new MoonMap("MoonTravel/data/moon2.jpg");
        _moonMap.open();
        _moonMap.snapshot(State.RAW.name());
        _moonMap.breakLeap("Raw Moon map loaded!");

        // apply the contrast filter and take the "contrast" snapshot.
        Filters.contrast(_moonMap);
        _moonMap.snapshot(State.CONTRAST.name());
        _moonMap.breakLeap("Contrast filter applied!");

        // pixelate the contrasted image and take the "pixelate" snapshot.
        boolean[][] terrain = Filters.pixelate(_moonMap);
        _moonMap.snapshot(State.PIXELATE.name());
        _moonMap.breakLeap("Pixelation complete!");

        // image processing done, restore  "raw" snapshot.
        _moonMap.restore(State.RAW.name());
        _moonMap.setStatusMessage("Raw image restored! Use arrow keys to loop through images.");
        _crtState = State.RAW;

        // create the map model passing in the map image and the terrain data
        // as extracted from the pixelated image.
        _model = new MapModel(_moonMap, terrain);

        // map is now loaded, hook mouse and keyboard to specific actions

        // ---- mouse hooks ----
        // clicking on the map will set or reset the _start and _target areas on the map. 
        _moonMap.setMouseHook(_onMouse);
        
        // ---- keyboard hooks for changing backgrounds ----
        // pressing UP/DOWN or LEFT/RIGHT will cycle the background through the "raw", "contrasted" and "pixelated" images.
        _moonMap.setKeyHook(KeyEvent.VK_DOWN, _onKey);
        _moonMap.setKeyHook(KeyEvent.VK_UP, _onKey);
        _moonMap.setKeyHook(KeyEvent.VK_LEFT, _onKey);
        _moonMap.setKeyHook(KeyEvent.VK_RIGHT, _onKey);

        // ---- keyboard hooks for route calculations ----
        // pressing the 'B', 'D' and 'A' keys will execute the BFS, Dijkstra and A* routing algorigthms 
        _moonMap.setKeyHook(KeyEvent.VK_B, _onRoute, MapModel.RouteAlgo.BFS);
        _moonMap.setKeyHook(KeyEvent.VK_D, _onRoute, MapModel.RouteAlgo.DIJKSTRA);
        _moonMap.setKeyHook(KeyEvent.VK_A, _onRoute, MapModel.RouteAlgo.ASTAR);

        // terminate the program when user closes the map window.
        _moonMap.close();
        System.out.println("Program terminated");
    }
}
