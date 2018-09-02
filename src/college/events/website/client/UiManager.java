package college.events.website.client;

import college.events.website.client.md.HomeScreen;
import college.events.website.client.md.MainContainer;
import college.events.website.shared.ScreenEnum;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Singleton class in charge of managing the displayed screens
 */
public class UiManager {
    /**
     * Instance of the UiManager
     */
    private static UiManager instance;

    /**
     * Main container of the displayed screens
     */
    private MainContainer mainContainer;

    /**
     * Constructor preventing outside instantiation
     */
    private UiManager() {
        RootPanel doc = RootPanel.get();
        doc.clear();

        mainContainer = new MainContainer();

        doc.add(mainContainer);
    }

    /**
     * Gets an instance of the UiManager
     * @return
     */
    public static UiManager getInstance() {
        if(instance == null) {
            instance = new UiManager();
        }
        return instance;
    }

    /**
     * Displays a screen based on the passed in enum
     *
     * @param screen Screen enum
     */
    public void displayScreen(ScreenEnum screen) {
        if(ScreenEnum.HOME.equals(screen)) {
            displayHomeScreen();
        }
    }

    /**
     * Displays the home screen
     */
    private void displayHomeScreen() {
        mainContainer.setDisplayedScreen(new HomeScreen());
    }
}
