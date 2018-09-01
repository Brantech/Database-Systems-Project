package college.events.client;

import college.events.client.md.MainContainer;
import com.google.gwt.user.client.ui.RootPanel;

public class UiManager {
    private static UiManager instance;

    private MainContainer mainContainer;

    private UiManager() {
        RootPanel doc = RootPanel.get();
        doc.clear();

        mainContainer = new MainContainer();

        doc.add(mainContainer);
    }

    public static UiManager getInstance() {
        if(instance == null) {
            instance = new UiManager();
        }
        return instance;
    }
}
