package college.events.client;

import college.events.shared.ScreenEnum;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

public class Website implements EntryPoint {
    public void onModuleLoad() {
        UiManager.getInstance().displayScreen(ScreenEnum.HOME);
    }
}
