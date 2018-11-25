package college.events.website.client;

import college.events.website.shared.messages.*;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.events.website.shared.rpc.GetRSOResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

public interface CEWServiceAsync {
    void createAccount(String username, String password, String firstName, String lastName, String email, String uniId, AsyncCallback<GenericRPCResponse<UserInfo>> async);

    void isUsernameAvailable(String username, AsyncCallback<GenericRPCResponse<String>> async);

    void login(String username, String password, AsyncCallback<GenericRPCResponse<UserInfo>> async);

    void login(String token, AsyncCallback<GenericRPCResponse<UserInfo>> async);

    void createUniversity(String name, String location, String description, String authToken, AsyncCallback<GenericRPCResponse<String>> async);

    void getSuperAdminMessages(String authToken, AsyncCallback<GenericRPCResponse<ArrayList<SuperAdminMessage>>> async);

    void approveRSOApplication(String messageID, String authToken, AsyncCallback<Boolean> async);

    void getEvents(String authToken, String uniID, String privacy, AsyncCallback<GenericRPCResponse<ArrayList<EventMessage>>> async);

    void createRSO(String rsoName, String description, String type, List<String> memberEmails, String authToken, AsyncCallback<GenericRPCResponse<String>> async);

    void declineMessage(String messageID, String authToken, AsyncCallback<Boolean> async);

    void getRSOs(UserInfo info, AsyncCallback<GetRSOResponse> async);

    void joinRSO(RSOMessage rso, String authToken, AsyncCallback<Boolean> async);

    void leaveRSO(RSOMessage rso, String authToken, AsyncCallback<Boolean> async);

    void deleteRSO(RSOMessage rso, String authToken, AsyncCallback<Boolean> async);

    void getEventsRSOs(AsyncCallback<GenericRPCResponse<ArrayList<RSOMessage>>> async);

    void createEvent(String authToken, String eventName, String description, String location, String date, String time, String rsoID, String category, String privacy, String contactName, String contactPhone, String contactEmail, AsyncCallback<GenericRPCResponse<String>> async);

    void approveEvent(String messageID, String authToken, AsyncCallback<Boolean> async);

    void writeComment(String authToken, String title, String message, String eventID, AsyncCallback<GenericRPCResponse<String>> async);

    void getComments(String eventID, AsyncCallback<GenericRPCResponse<ArrayList<CommentsMessage>>> async);

    void getUniversities(AsyncCallback<GenericRPCResponse<ArrayList<UniversityMessage>>> async);

    void getLocations(AsyncCallback<GenericRPCResponse<ArrayList<String>>> async);

    void editComment(String authToken, String title, String message, String mID, AsyncCallback<GenericRPCResponse<String>> async);
}
