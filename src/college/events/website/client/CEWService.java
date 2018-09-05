package college.events.website.client;

import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;

@RemoteServiceRelativePath("CEWService")
public interface CEWService extends RemoteService {
    public GenericRPCResponse<String> createAccount(String username, String password, String firstName, String lastName, String email);
}
