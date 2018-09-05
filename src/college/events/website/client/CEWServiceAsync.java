package college.events.website.client;

import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CEWServiceAsync {
    void createAccount(String username, String password, String firstName, String lastName, String email, AsyncCallback<GenericRPCResponse<String>> async);
}
