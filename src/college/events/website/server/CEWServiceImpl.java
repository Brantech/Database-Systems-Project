package college.events.website.server;

import college.events.website.client.CEWService;
import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CEWServiceImpl extends RemoteServiceServlet implements CEWService {

    @Override
    public GenericRPCResponse<String> createAccount(String username, String password, String firstName, String lastName) {
        return new GenericRPCResponse<>(true);
    }
}