package college.events.website.server;

import college.events.website.client.CEWService;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.hibernate.DbManager;
import college.hibernate.QueryResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CEWServiceImpl extends RemoteServiceServlet implements CEWService {
    private static final DbManager manager = DbManager.getInstance();

    @Override
    public GenericRPCResponse<String> login(String username, String password) {
        QueryResponse response = manager.login(username, password);
        return new GenericRPCResponse<>(response.getMessage(), response.getSuccess());
    }

    @Override
    public GenericRPCResponse<String> createAccount(String username, String password, String firstName, String lastName, String email) {
        QueryResponse response = manager.createAccount(username, password, firstName, lastName, email);
        return new GenericRPCResponse<>("", response.getSuccess());
    }

    @Override
    public GenericRPCResponse<String> isUsernameAvailable(String username) {
        QueryResponse response = manager.isUsernameAvailable(username);
        return new GenericRPCResponse<>("", response.getSuccess());
    }
}