package college.events.website.shared.rpc;

import college.events.website.shared.messages.RSOMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GetRSOResponse implements Serializable {
    private boolean success;
    private ArrayList<String> follows;
    private ArrayList<RSOMessage> rsos;

    private GetRSOResponse() {

    }

    public GetRSOResponse(boolean success, ArrayList<String> follows, ArrayList<RSOMessage> rsos) {
        this.success = success;
        this.follows = follows;
        this.rsos = rsos;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<String> getFollows() {
        return follows;
    }

    public void setFollows(ArrayList<String> follows) {
        this.follows = follows;
    }

    public ArrayList<RSOMessage> getRsos() {
        return rsos;
    }

    public void setRsos(ArrayList<RSOMessage> rsos) {
        this.rsos = rsos;
    }
}
