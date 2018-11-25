package college.events.website.client;

import college.events.website.shared.messages.*;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.events.website.shared.rpc.GetRSOResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;
import java.util.List;

@RemoteServiceRelativePath("CEWService")
public interface CEWService extends RemoteService {

    GenericRPCResponse<ArrayList<UniversityMessage>> getUniversities();
    GenericRPCResponse<ArrayList<String>> getLocations();
    GenericRPCResponse<UserInfo> login(String token);
    GenericRPCResponse<UserInfo> login(String username, String password);
    GenericRPCResponse<UserInfo> createAccount(String username, String password, String firstName, String lastName, String email, String uniId);
    GenericRPCResponse<String> createUniversity(String name, String location, String description, String authToken);
    GenericRPCResponse<String> createRSO(String rsoName, String description, String type, List<String> memberEmails, String authToken);
    GenericRPCResponse<ArrayList<SuperAdminMessage>> getSuperAdminMessages(String authToken);
    GenericRPCResponse<String> isUsernameAvailable(String username);
    GenericRPCResponse<ArrayList<EventMessage>> getEvents(String authToken, String uniID, String privacy);
    GetRSOResponse getRSOs(UserInfo info);
    GenericRPCResponse<String> writeComment(String authToken, String title, String message, String eventID);
    GenericRPCResponse<ArrayList<CommentsMessage>> getComments(String eventID);
    boolean approveRSOApplication(String messageID, String authToken);
    boolean declineMessage(String messageID, String authToken);
    boolean joinRSO(RSOMessage rso, String authToken);
    boolean leaveRSO(RSOMessage rso, String authToken);
    boolean deleteRSO(RSOMessage rso, String authToken);
    GenericRPCResponse<ArrayList<RSOMessage>> getEventsRSOs();
    GenericRPCResponse<String> createEvent(String authToken, String eventName, String description, String location, String date, String time, String rso, String category, String privacy, String contactName, String contactPhone, String contactEmail);
    boolean approveEvent(String messageID, String authToken);
    GenericRPCResponse<String> editComment(String authToken, String title, String message, String mID);
}
