package college.events.website.server;

import college.events.hibernate.entities.MessagesEntity;
import college.events.hibernate.entities.RsoEntity;
import college.events.hibernate.entities.UsersEntity;
import college.events.website.client.CEWService;
import college.events.website.shared.Events;
import college.events.website.shared.messages.RSOMessage;
import college.events.website.shared.messages.SuperAdminMessage;
import college.events.website.shared.messages.UserInfo;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.events.hibernate.DbManager;
import college.events.hibernate.QueryResponse;
import college.events.website.shared.rpc.GetRSOResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CEWServiceImpl extends RemoteServiceServlet implements CEWService {
    private static final DbManager manager = DbManager.getInstance();

    @Override
    public GenericRPCResponse<UserInfo> login(String token) {
        QueryResponse response = manager.login(token);
        UserInfo info = null;
        if(response.getSuccess()) {
            UsersEntity u = (UsersEntity) response.getPayload();
            info = new UserInfo(u.getUserId(), u.getEmail(), u.getType(), u.getUniId(), response.getMessage());
        }
        return new GenericRPCResponse<>(info, response.getSuccess());
    }

    @Override
    public GenericRPCResponse<UserInfo> login(String username, String password) {
        QueryResponse response = manager.login(username, password);
        UserInfo info = null;
        if(response.getSuccess()) {
            UsersEntity u = (UsersEntity) response.getPayload();
            info = new UserInfo(u.getUserId(), u.getEmail(), u.getType(), u.getUniId(), response.getMessage());
        }
        return new GenericRPCResponse<>(info, response.getSuccess());
    }

    @Override
    public GenericRPCResponse<String> createAccount(String username, String password, String firstName, String lastName, String email, String uniId) {
        QueryResponse response = manager.createStudent(username, password, firstName, lastName, email, uniId);
        return new GenericRPCResponse<>(response.getSuccess());
    }

    @Override
    public GenericRPCResponse<String> createUniversity(String name, String location, String description, String authToken) {
        QueryResponse response = manager.createUniversity(name, location, description, authToken);
        return new GenericRPCResponse<>(response.getMessage(), response.getSuccess());
    }

    @Override
    public GenericRPCResponse<String> createRSO(String rsoName, String description, String type, List<String> memberEmail, String authToken) {
        QueryResponse response = manager.createRSO(rsoName, description, type, memberEmail, authToken);
        return new GenericRPCResponse<>(response.getMessage(), response.getSuccess());
    }

    @Override
    public GenericRPCResponse<ArrayList<SuperAdminMessage>> getSuperAdminMessages(String authToken) {
        QueryResponse response = manager.getSuperAdminMessages(authToken);

        ArrayList<SuperAdminMessage> ret = new ArrayList<>();
        for(MessagesEntity m : (List<MessagesEntity>) response.getPayload()) {
            ret.add(new SuperAdminMessage(m.getId(), m.getSubject(), m.getMessageType(), m.getMessage(), m.getSenderId(), m.getUniId(), Long.parseLong(m.getSendDate())));
        }
        return new GenericRPCResponse<>(ret, response.getSuccess());
    }

    @Override
    public GenericRPCResponse<String> isUsernameAvailable(String username) {
        QueryResponse response = manager.isUsernameAvailable(username);
        return new GenericRPCResponse<>("", response.getSuccess());
    }

    @Override
    public GenericRPCResponse<ArrayList<Events>> getEvents(String authToken) {
        QueryResponse response = manager.getEvents(authToken);
        return new GenericRPCResponse<>((ArrayList<Events>) response.getPayload(), response.getSuccess());
    }

    @Override
    public GetRSOResponse getRSOs(UserInfo info) {
        QueryResponse response = manager.getUniRSOs(info);

        ArrayList<String> follows = new ArrayList<>();
        ArrayList<RSOMessage> rsoList = new ArrayList<>();
        if(response.getSuccess()) {
            List<Object> ret = (List<Object>) response.getPayload();

            follows = (ArrayList<String>) ret.get(1);
            for(RsoEntity r : (List<RsoEntity>) ret.get(0)) {
                rsoList.add(new RSOMessage(r.getRsoId(), r.getAdminId(), r.getName(), r.getDescription(), r.getType(), r.getMembers(), r.getUniId()));
            }
        }

        return new GetRSOResponse(response.getSuccess(), follows, rsoList);
    }

    @Override
    public boolean approveRSOApplication(String messageID, String authToken) {
        QueryResponse response = manager.approveRSOApplication(messageID, authToken);
        return response.getSuccess();
    }

    @Override
    public boolean declineRSOApplication(String messageID, String authToken) {
        QueryResponse response = manager.declineRSOApplication(messageID, authToken);
        return response.getSuccess();
    }

    @Override
    public boolean joinRSO(RSOMessage rso, String authToken) {
        return manager.joinRSO(rso, authToken).getSuccess();
    }

    @Override
    public boolean leaveRSO(RSOMessage rso, String authToken) {
        return manager.leaveRSO(rso, authToken).getSuccess();
    }

    @Override
    public boolean deleteRSO(RSOMessage rso, String authToken) {
        return manager.deleteRSO(rso, authToken).getSuccess();
    }
}