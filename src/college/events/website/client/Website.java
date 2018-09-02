package college.events.website.client;

import college.events.website.shared.ScreenEnum;
import com.google.gwt.core.client.EntryPoint;

public class Website implements EntryPoint {
    public void onModuleLoad() {
        UiManager.getInstance().displayScreen(ScreenEnum.HOME);
    }
}
