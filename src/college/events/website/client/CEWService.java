package college.events.website.client;

import college.events.website.shared.rpc.GenericRPCResponse;
import college.hibernate.QueryResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;

@RemoteServiceRelativePath("CEWService")
public interface CEWService extends RemoteService {
    GenericRPCResponse<String> createAccount(String username, String password, String firstName, String lastName, String email);
    GenericRPCResponse<String> isUsernameAvailable(String username);
}
