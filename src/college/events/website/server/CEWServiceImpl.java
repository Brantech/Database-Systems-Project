package college.events.website.server;

import college.events.hibernate.DbManager;
import college.events.hibernate.QueryResponse;
import college.events.hibernate.entities.*;
import college.events.website.client.CEWService;
import college.events.website.shared.messages.*;
import college.events.website.shared.rpc.GenericRPCResponse;
import college.events.website.shared.rpc.GetRSOResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.List;

public class CEWServiceImpl extends RemoteServiceServlet implements CEWService {
    private static final DbManager manager = DbManager.getInstance();

    @Override
    public GenericRPCResponse<ArrayList<UniversityMessage>> getUniversities() {
        QueryResponse response = manager.getUniversities();
        ArrayList<UniversityMessage> ret = new ArrayList<>();
        try {
            for(UniversitiesEntity u : (List<UniversitiesEntity>) response.getPayload()) {
                ret.add(new UniversityMessage(u.getUniId(), u.getName()));
            }
        } catch (Exception e) {

        }
        return new GenericRPCResponse<>(ret, response.getSuccess());
    }

    @Override
    public GenericRPCResponse<ArrayList<String>> getLocations() {
        QueryResponse response = manager.getEventLocations();
        ArrayList<String> ret = new ArrayList<>();
        ret.addAll((List<String>) response.getPayload());
        return new GenericRPCResponse<>(ret, response.getSuccess());
    }

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
    public GenericRPCResponse<UserInfo> createAccount(String username, String password, String firstName, String lastName, String email, String uniId) {
        QueryResponse response = manager.createStudent(username, password, firstName, lastName, email, uniId);
        UserInfo info = null;
        if(response.getSuccess()) {
            UsersEntity u = (UsersEntity) response.getPayload();
            info = new UserInfo(u.getUserId(), u.getEmail(), u.getType(), u.getUniId(), response.getMessage());
        }
        return new GenericRPCResponse<>(info, response.getSuccess());
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
    public GenericRPCResponse<ArrayList<EventMessage>> getEvents(String authToken, String uniID, String privacy) {
        QueryResponse response = manager.getEvents(authToken, uniID, privacy);
        ArrayList<EventMessage> events = new ArrayList<>();

        if(response.getSuccess()) {
            for(EventsEntity e : (List<EventsEntity>) response.getPayload()) {
                events.add(new EventMessage(e.getEventId(), e.getName(), e.getType(), e.getCategory(),
                        e.getDescription(), e.getTime(), e.getDate(), e.getLocation(), e.getContactName(),
                        e.getContactPhone(), e.getContactEmail(), manager.getUniversityName(e.getUniId()).getMessage(), manager.getRSOName(e.getRsoId()).getMessage()));
            }
        }
        return new GenericRPCResponse<>(events, response.getSuccess());
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
    public GenericRPCResponse<String> writeComment(String authToken, String title, String message, String eventID) {
        QueryResponse response = DbManager.getInstance().writeComment(authToken, title, message, eventID);
        return new GenericRPCResponse<>(response.getMessage(), response.getSuccess());
    }

    @Override
    public GenericRPCResponse<ArrayList<CommentsMessage>> getComments(String eventID) {
        QueryResponse response = DbManager.getInstance().getComments(eventID);
        ArrayList<CommentsMessage> ret = new ArrayList();
        try {
            ret = (ArrayList) response.getPayload();
        } catch (Exception e) {
            return new GenericRPCResponse<>(null, false);
        }
        return new GenericRPCResponse<>(ret, true);
    }

    @Override
    public boolean approveRSOApplication(String messageID, String authToken) {
        QueryResponse response = manager.approveRSOApplication(messageID, authToken);
        return response.getSuccess();
    }

    @Override
    public boolean declineMessage(String messageID, String authToken) {
        QueryResponse response = manager.decline(messageID, authToken);
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

    @Override
    public GenericRPCResponse<ArrayList<RSOMessage>> getEventsRSOs() {
        QueryResponse response = manager.getRSOs();

        ArrayList<RSOMessage> ret = new ArrayList<>();
        for(RsoEntity r : (List<RsoEntity>) response.getPayload()) {
            ret.add(new RSOMessage(r.getRsoId(), r.getAdminId(), r.getName(), r.getDescription(), r.getType(), r.getMembers(), r.getUniId()));
        }
        return new GenericRPCResponse<>(ret, response.getSuccess());
    }

    @Override
    public GenericRPCResponse<String> createEvent(String authToken, String eventName, String description, String location, String date, String time, String rsoID, String category, String privacy, String contactName, String contactPhone, String contactEmail) {
        QueryResponse response = manager.createEvent(authToken, eventName, description, location, date, time, rsoID, category, privacy, contactName, contactPhone, contactEmail);
        return new GenericRPCResponse<>(response.getMessage(), response.getSuccess());
    }

    @Override
    public boolean approveEvent(String messageID, String authToken) {
        return manager.approveEvent(messageID, authToken).getSuccess();
    }

    @Override
    public GenericRPCResponse<String> editComment(String authToken, String title, String message, String mID) {
        QueryResponse response = manager.editComment(authToken, title, message, mID);
        return new GenericRPCResponse<>(response.getMessage(), response.getSuccess());
    }
}