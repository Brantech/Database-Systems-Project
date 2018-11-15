package college.events.website.client;

import college.events.website.shared.Events;
import college.events.website.shared.messages.RSOMessage;
import college.events.website.shared.messages.SuperAdminMessage;
import college.events.website.shared.messages.UserInfo;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.events.website.shared.rpc.GetRSOResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RemoteServiceRelativePath("CEWService")
public interface CEWService extends RemoteService {

    GenericRPCResponse<UserInfo> login(String token);
    GenericRPCResponse<UserInfo> login(String username, String password);
    GenericRPCResponse<String> createAccount(String username, String password, String firstName, String lastName, String email, String uniId);
    GenericRPCResponse<String> createUniversity(String name, String location, String description, String authToken);
    GenericRPCResponse<String> createRSO(String rsoName, String description, String type, List<String> memberEmails, String authToken);
    GenericRPCResponse<ArrayList<SuperAdminMessage>> getSuperAdminMessages(String authToken);
    GenericRPCResponse<String> isUsernameAvailable(String username);
    GenericRPCResponse<ArrayList<Events>> getEvents(String authToken);
    GetRSOResponse getRSOs(UserInfo info);
    boolean approveRSOApplication(String messageID, String authToken);
    boolean declineRSOApplication(String messageID, String authToken);
    boolean joinRSO(RSOMessage rso, String authToken);
    boolean leaveRSO(RSOMessage rso, String authToken);
    boolean deleteRSO(RSOMessage rso, String authToken);
}
