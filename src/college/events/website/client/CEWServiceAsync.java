package college.events.website.client;

import college.events.website.shared.rpc.GenericRPCResponse;
import college.hibernate.QueryResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CEWServiceAsync {
    void createAccount(String username, String password, String firstName, String lastName, String email, AsyncCallback<GenericRPCResponse<String>> async);

    void isUsernameAvailable(String username, AsyncCallback<GenericRPCResponse<String>> async);

    void login(String username, String password, AsyncCallback<GenericRPCResponse<String>> async);
}
