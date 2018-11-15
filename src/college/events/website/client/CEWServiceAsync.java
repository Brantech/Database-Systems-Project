package college.events.website.client;

import college.events.website.shared.Events;
import college.events.website.shared.messages.RSOMessage;
import college.events.website.shared.messages.SuperAdminMessage;
import college.events.website.shared.messages.UserInfo;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.events.website.shared.rpc.GetRSOResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface CEWServiceAsync {
    void createAccount(String username, String password, String firstName, String lastName, String email, String uniId, AsyncCallback<GenericRPCResponse<String>> async);

    void isUsernameAvailable(String username, AsyncCallback<GenericRPCResponse<String>> async);

    void login(String username, String password, AsyncCallback<GenericRPCResponse<UserInfo>> async);

    void login(String token, AsyncCallback<GenericRPCResponse<UserInfo>> async);

    void createUniversity(String name, String location, String description, String authToken, AsyncCallback<GenericRPCResponse<String>> async);

    void getSuperAdminMessages(String authToken, AsyncCallback<GenericRPCResponse<ArrayList<SuperAdminMessage>>> async);

    void approveRSOApplication(String messageID, String authToken, AsyncCallback<Boolean> async);

    void getEvents(String authToken, AsyncCallback<GenericRPCResponse<ArrayList<Events>>> async);

    void createRSO(String rsoName, String description, String type, List<String> memberEmails, String authToken, AsyncCallback<GenericRPCResponse<String>> async);

    void declineRSOApplication(String messageID, String authToken, AsyncCallback<Boolean> async);

    void getRSOs(UserInfo info, AsyncCallback<GetRSOResponse> async);

    void joinRSO(RSOMessage rso, String authToken, AsyncCallback<Boolean> async);

    void leaveRSO(RSOMessage rso, String authToken, AsyncCallback<Boolean> async);

    void deleteRSO(RSOMessage rso, String authToken, AsyncCallback<Boolean> async);
}
