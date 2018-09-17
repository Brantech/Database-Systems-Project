package college.events.website.client;

import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("CEWService")
public interface CEWService extends RemoteService {
    GenericRPCResponse<String> login(String username, String password);
    GenericRPCResponse<String> createAccount(String username, String password, String firstName, String lastName, String email);
    GenericRPCResponse<String> isUsernameAvailable(String username);
}
