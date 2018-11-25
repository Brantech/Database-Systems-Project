package college.events.website.client.md;

import college.events.website.client.CEWService;
import college.events.website.client.CEWServiceAsync;
import college.events.website.client.UiManager;
import college.events.website.client.md.subwidgets.Comment;
import college.events.website.client.md.subwidgets.EventItem;
import college.events.website.shared.CookyKeys;
import college.events.website.shared.ScreenEnum;
import college.events.website.shared.messages.CommentsMessage;
import college.events.website.shared.messages.EventMessage;
import college.events.website.shared.messages.RSOMessage;
import college.events.website.shared.messages.UniversityMessage;
import college.events.website.shared.rpc.GenericRPCResponse;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.timepicker.MaterialTimePicker;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.WavesType;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class EventsScreen extends Composite {
    private static Logger logger = Logger.getLogger(EventsScreen.class.getName());
    interface EventsScreenUiBinder extends UiBinder<Widget, EventsScreen> {
    }
    private static EventsScreenUiBinder uiBinder = GWT.create(EventsScreenUiBinder.class);
    private static CEWServiceAsync cewServiceAsync = GWT.create(CEWService.class);

    @UiField
    MaterialButton locationFilter, universityFilter;

    @UiField
    MaterialDropDown universities, locations;

    @UiField
    MaterialDialog eventCreationDialog, viewEventDialog;

    @UiField
    MaterialButton cancel, createEvent, submit, eventRSO, eventTypeButton, eventPrivacy;

    @UiField
    MaterialTextBox eventName, eventLocation, contactName, contactPhone, contactEmail;

    @UiField
    MaterialTextArea eventDescription;

    @UiField
    MaterialDatePicker eventStartDate, eventEndDate;

    @UiField
    MaterialTimePicker eventStartTime, eventEndTime;

    @UiField
    MaterialCard rsoDropDown, typesDropDown, privacyDropDown;

    @UiField
    MaterialCard eventsContainer;

    @UiField
    MaterialCard emptyState;

    @UiField
    MaterialTextBox nameView, locationView, contactNameView, contactPhoneView, contactEmailView, startDate, endDate, startTime, endTime, rsoView, categoryView, privacyView;

    @UiField
    MaterialLabel commentCount;

    @UiField
    MaterialTextArea descriptionView;

    @UiField
    MaterialCard commentsContainer;

    @UiField
    MaterialTextBox commentTitle;

    @UiField
    MaterialTextArea commentMessage;

    private static String[] types = {"Career", "Film", "Music", "Social", "Sports", "Tech Talk"};

    private List<EventMessage> events;

    private EventsScreen self;

    private RSOMessage selectedRSO;

    private EventMessage selectedEvent;

    private String uni = "Any", loc = "Any";

    public EventsScreen() {
        initWidget(uiBinder.createAndBindUi(this));

        cewServiceAsync.getEvents(UiManager.getInstance().getUserInfo().getAuthToken(), "Any", "Any", new AsyncCallback<GenericRPCResponse<ArrayList<EventMessage>>>() {
            @Override
            public void onFailure(Throwable caught) {
                Cookies.removeCookie(CookyKeys.AUTH_TOKEN);
                UiManager.getInstance().displayScreen(ScreenEnum.HOME);
            }

            @Override
            public void onSuccess(GenericRPCResponse<ArrayList<EventMessage>> result) {
                if(result.isSuccess()) {
                    events = result.getPayload();
                    init();
                } else {
                    Cookies.removeCookie(CookyKeys.AUTH_TOKEN);
                    UiManager.getInstance().displayScreen(ScreenEnum.HOME);
                }

            }
        });

    }

    private void init() {
        rsoDropDown.add(createDropDownItem("(none)", new Callback<Void, Void>() {
            @Override
            public void onFailure(Void reason) {

            }

            @Override
            public void onSuccess(Void result) {
                eventRSO.setText("(none)");
                selectedRSO = null;
                rsoDropDown.setDisplay(Display.NONE);
            }
        }));

        cewServiceAsync.getEventsRSOs(new AsyncCallback<GenericRPCResponse<ArrayList<RSOMessage>>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(GenericRPCResponse<ArrayList<RSOMessage>> result) {
                if(result.isSuccess()) {
                    for(RSOMessage m : result.getPayload()) {
                        rsoDropDown.add(createDropDownItem(m.getNAME(), new Callback<Void, Void>() {
                            @Override
                            public void onFailure(Void reason) {

                            }

                            @Override
                            public void onSuccess(Void result) {
                                eventRSO.setText(m.getNAME());
                                selectedRSO = m;
                                rsoDropDown.setDisplay(Display.NONE);
                            }
                        }));
                    }
                }
            }
        });

        for(String s : types) {
            typesDropDown.add(createDropDownItem(s, new Callback<Void, Void>() {
                @Override
                public void onFailure(Void reason) {

                }

                @Override
                public void onSuccess(Void result) {
                    typesDropDown.setDisplay(Display.NONE);
                    eventTypeButton.setText(s);
                }
            }));
        }

        cewServiceAsync.getLocations(new AsyncCallback<GenericRPCResponse<ArrayList<String>>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(GenericRPCResponse<ArrayList<String>> result) {
                if(result.isSuccess()) {
                    for(String s : result.getPayload()) {
                        MaterialLink link = new MaterialLink(s, s);
                        link.setTextColor(Color.BLACK);
                        locations.add(link);
                    }
                }
            }
        });
        cewServiceAsync.getUniversities(new AsyncCallback<GenericRPCResponse<ArrayList<UniversityMessage>>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(GenericRPCResponse<ArrayList<UniversityMessage>> result) {
                if(result.isSuccess()) {
                    for(UniversityMessage m : result.getPayload()) {
                        MaterialLink link = new MaterialLink(m.getName(), m.getUniID());
                        link.setTextColor(Color.BLACK);
                        universities.add(link);
                    }
                }
            }
        });

        privacyDropDown.add(createDropDownItem("Public", new Callback<Void, Void>() {
            @Override
            public void onFailure(Void reason) {

            }

            @Override
            public void onSuccess(Void result) {
                eventPrivacy.setText("Public");
                privacyDropDown.setDisplay(Display.NONE);
            }
        }));

        privacyDropDown.add(createDropDownItem("Private", new Callback<Void, Void>() {
            @Override
            public void onFailure(Void reason) {

            }

            @Override
            public void onSuccess(Void result) {
                eventPrivacy.setText("Private");
                privacyDropDown.setDisplay(Display.NONE);
            }
        }));

        if(events != null && !events.isEmpty()) {
            emptyState.setDisplay(Display.NONE);
            eventsContainer.setDisplay(Display.BLOCK);
            for(EventMessage m : events) {
                eventsContainer.add(new EventItem(m, new Callback<Void, Void>() {
                    @Override
                    public void onFailure(Void reason) {

                    }

                    @Override
                    public void onSuccess(Void result) {
                        resetEventViewDialog();
                        selectedEvent = m;
                        viewEventDialog.open();

                        nameView.setText(m.getName());
                        descriptionView.setText(m.getDescription());
                        locationView.setText(m.getLocation());
                        contactEmailView.setText(m.getContactEmail());
                        contactNameView.setText(m.getContactName());
                        contactPhoneView.setText(m.getContactPhone());

                        String[] times = m.getTime().split(" ");
                        String[] dates = m.getDate().split(" ");

                        startDate.setText(DateTimeFormat.getFormat("MM/dd/yyyy").format(new Date(Long.parseLong(dates[0]))));
                        endDate.setText(DateTimeFormat.getFormat("MM/dd/yyyy").format(new Date(Long.parseLong(dates[1]))));
                        startTime.setText(DateTimeFormat.getFormat("H:mm").format(new Date(Long.parseLong(times[0]))));
                        endTime.setText(DateTimeFormat.getFormat("H:mm").format(new Date(Long.parseLong(times[1]))));
                        categoryView.setText(m.getCategory());
                        privacyView.setText(m.getType());
                        rsoView.setText(m.getRso());

                        cewServiceAsync.getComments(m.getId(), new AsyncCallback<GenericRPCResponse<ArrayList<CommentsMessage>>>() {
                            @Override
                            public void onFailure(Throwable caught) {

                            }

                            @Override
                            public void onSuccess(GenericRPCResponse<ArrayList<CommentsMessage>> result) {
                                if(result.isSuccess()) {
                                    commentCount.setText(result.getPayload().size() + "");
                                    commentsContainer.clear();
                                    for(CommentsMessage m : result.getPayload()) {
                                        Comment temp = new Comment(m.getTitle(), m.getMessage(), m.getName(), m.getId());
                                        temp.setEditable(m.getUserID().equals(UiManager.getInstance().getUserInfo().getUSER_ID()));
                                        commentsContainer.add(temp);
                                    }
                                }
                            }
                        });
                    }
                }));
            }
        }
    }

    private MaterialLink createDropDownItem(String name, Callback<Void, Void> callback) {
        MaterialLink link = new MaterialLink(name);
        link.setStyle("display: block; width: 100%; font-size: 1.5em; cursor: pointer; padding: 10px;");
        link.setHoverable(true);
        link.setWaves(WavesType.DEFAULT);
        link.setTextColor(Color.BLACK);
        link.addClickHandler(event -> {
            if(callback != null) {
                callback.onSuccess(null);
            }
        });

        return link;
    }

    private void resetEventCreator() {
        eventName.setText(null);
        eventDescription.setText(null);
        eventLocation.setText(null);
        eventStartDate.reset();
        eventStartTime.reset();
        eventRSO.setText("(none)");
        eventPrivacy.setText("Public");
        contactName.setText(null);
        contactPhone.setText(null);
        contactEmail.setText(null);
    }

    private void resetEventViewDialog() {
        nameView.setText("");
        descriptionView.setText("");
        locationView.setText("");
        startDate.setText("");
        startTime.setText("");
        rsoView.setText("");
        categoryView.setText("");
        privacyView.setText("");
        contactEmailView.setText("");
        contactNameView.setText("");
        contactPhoneView.setText("");
    }

    @UiHandler("locations")
    void onLocationSelect(SelectionEvent<Widget> event) {
        MaterialLink link = (MaterialLink) event.getSelectedItem();
        locationFilter.setText(link.getText());

        cewServiceAsync.getEvents(UiManager.getInstance().getUserInfo().getAuthToken(), uni, link.getText(), new AsyncCallback<GenericRPCResponse<ArrayList<EventMessage>>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(GenericRPCResponse<ArrayList<EventMessage>> result) {
                if(result.isSuccess()) {
                    events = result.getPayload();
                    loc = link.getText();
                    if(events != null && !events.isEmpty()) {
                        emptyState.setDisplay(Display.NONE);
                        eventsContainer.setDisplay(Display.BLOCK);
                        eventsContainer.clear();
                        for(EventMessage m : events) {
                            eventsContainer.add(new EventItem(m, new Callback<Void, Void>() {
                                @Override
                                public void onFailure(Void reason) {

                                }

                                @Override
                                public void onSuccess(Void result) {
                                    resetEventViewDialog();
                                    selectedEvent = m;
                                    viewEventDialog.open();

                                    nameView.setText(m.getName());
                                    descriptionView.setText(m.getDescription());
                                    locationView.setText(m.getLocation());
                                    contactEmailView.setText(m.getContactEmail());
                                    contactNameView.setText(m.getContactName());
                                    contactPhoneView.setText(m.getContactPhone());
                                    startDate.setText(DateTimeFormat.getFormat("MM/dd/yyyy").format(new Date(Long.parseLong(m.getDate()))));
                                    startTime.setText(DateTimeFormat.getFormat("H:mm").format(new Date(Long.parseLong(m.getTime()))));
                                    categoryView.setText(m.getCategory());
                                    privacyView.setText(m.getType());
                                    rsoView.setText(m.getRso());

                                    cewServiceAsync.getComments(m.getId(), new AsyncCallback<GenericRPCResponse<ArrayList<CommentsMessage>>>() {
                                        @Override
                                        public void onFailure(Throwable caught) {

                                        }

                                        @Override
                                        public void onSuccess(GenericRPCResponse<ArrayList<CommentsMessage>> result) {
                                            if(result.isSuccess()) {
                                                commentCount.setText(result.getPayload().size() + "");
                                                commentsContainer.clear();
                                                for(CommentsMessage m : result.getPayload()) {
                                                    Comment temp = new Comment(m.getTitle(), m.getMessage(), m.getName(), m.getId());
                                                    temp.setEditable(m.getUserID().equals(UiManager.getInstance().getUserInfo().getUSER_ID()));
                                                    commentsContainer.add(temp);
                                                }
                                            }
                                        }
                                    });
                                }
                            }));
                        }
                    } else {
                        emptyState.setDisplay(Display.FLEX);
                        eventsContainer.setDisplay(Display.NONE);
                    }
                }
            }
        });
    }

    @UiHandler("universities")
    void onUniversitySelect(SelectionEvent<Widget> event) {
        MaterialLink link = (MaterialLink) event.getSelectedItem();
        universityFilter.setText(link.getText());

        cewServiceAsync.getEvents(UiManager.getInstance().getUserInfo().getAuthToken(), link.getHref().substring(1), loc, new AsyncCallback<GenericRPCResponse<ArrayList<EventMessage>>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(GenericRPCResponse<ArrayList<EventMessage>> result) {
                if(result.isSuccess()) {
                    events = result.getPayload();
                    uni = link.getHref().substring(1);
                    if(events != null && !events.isEmpty()) {
                        emptyState.setDisplay(Display.NONE);
                        eventsContainer.setDisplay(Display.BLOCK);
                        eventsContainer.clear();
                        for(EventMessage m : events) {
                            eventsContainer.add(new EventItem(m, new Callback<Void, Void>() {
                                @Override
                                public void onFailure(Void reason) {

                                }

                                @Override
                                public void onSuccess(Void result) {
                                    resetEventViewDialog();
                                    selectedEvent = m;
                                    viewEventDialog.open();

                                    nameView.setText(m.getName());
                                    descriptionView.setText(m.getDescription());
                                    locationView.setText(m.getLocation());
                                    contactEmailView.setText(m.getContactEmail());
                                    contactNameView.setText(m.getContactName());
                                    contactPhoneView.setText(m.getContactPhone());
                                    startDate.setText(DateTimeFormat.getFormat("MM/dd/yyyy").format(new Date(Long.parseLong(m.getDate()))));
                                    startTime.setText(DateTimeFormat.getFormat("H:mm").format(new Date(Long.parseLong(m.getTime()))));
                                    categoryView.setText(m.getCategory());
                                    privacyView.setText(m.getType());
                                    rsoView.setText(m.getRso());

                                    cewServiceAsync.getComments(m.getId(), new AsyncCallback<GenericRPCResponse<ArrayList<CommentsMessage>>>() {
                                        @Override
                                        public void onFailure(Throwable caught) {

                                        }

                                        @Override
                                        public void onSuccess(GenericRPCResponse<ArrayList<CommentsMessage>> result) {
                                            if(result.isSuccess()) {
                                                commentCount.setText(result.getPayload().size() + "");
                                                commentsContainer.clear();
                                                for(CommentsMessage m : result.getPayload()) {
                                                    Comment temp = new Comment(m.getTitle(), m.getMessage(), m.getName(), m.getId());
                                                    temp.setEditable(m.getUserID().equals(UiManager.getInstance().getUserInfo().getUSER_ID()));
                                                    commentsContainer.add(temp);
                                                }
                                            }
                                        }
                                    });
                                }
                            }));
                        }
                    } else {
                        emptyState.setDisplay(Display.FLEX);
                        eventsContainer.setDisplay(Display.NONE);
                    }
                }
            }
        });
    }

    @UiHandler("eventRSO")
    void onEventRSOClick(ClickEvent event) {
        logger.severe("zsadf");
        selectedRSO = null;
        rsoDropDown.setDisplay(Display.BLOCK);
    }

    @UiHandler("eventTypeButton")
    void onEventTypeButtonClick(ClickEvent event) {
        typesDropDown.setDisplay(Display.BLOCK);
    }

    @UiHandler("eventPrivacy")
    void onEventPrivacyClick(ClickEvent event) {
        privacyDropDown.setDisplay(Display.BLOCK);
    }

    @UiHandler("createEvent")
    void onCreateEventClick(ClickEvent event) {
        eventCreationDialog.open();
    }

    @UiHandler("cancel")
    void onCancelClick(ClickEvent event) {
        eventCreationDialog.close();
    }

    @UiHandler("submit")
    void onSubmitClick(ClickEvent event) {
        if(eventName.getText().isEmpty()) {
            MaterialToast.fireToast("Event name cannot be empty");
            return;
        }

        if(eventStartDate.getDate() == null) {
            MaterialToast.fireToast("Event start date cannot be empty");
            return;
        }

        if(eventEndDate.getDate() == null) {
            MaterialToast.fireToast("Event end date cannot be empty");
            return;
        }

        if(eventStartTime.getValue() == null) {
            MaterialToast.fireToast("Event start time cannot be empty");
            return;
        }

        if(eventEndTime.getValue() == null) {
            MaterialToast.fireToast("Event end time cannot be empty.");
            return;
        }

        if(contactName.getText().isEmpty()) {
            MaterialToast.fireToast("Contact name cannot be empty");
            return;
        }

        if(contactPhone.getText().isEmpty()) {
            MaterialToast.fireToast("Contact phone cannot be empty");
            return;
        }

        if(contactEmail.getText().isEmpty()) {
            MaterialToast.fireToast("Contact email cannot be empty");
            return;
        }



        String rsoID = selectedRSO == null ? null : selectedRSO.getRSO_ID();

        String date = Long.toString(eventStartDate.getDate().getTime()) + " " + eventEndDate.getDate().getTime();
        String time = Long.toString(eventStartTime.getValue().getTime()) + " " + eventEndTime.getValue().getTime();

        cewServiceAsync.createEvent(UiManager.getInstance().getUserInfo().getAuthToken(), eventName.getText(), eventDescription.getText(), eventLocation.getText(),
                date, time, rsoID, eventTypeButton.getText(), eventPrivacy.getText(), contactName.getText(), contactPhone.getText(), contactEmail.getText(),
                new AsyncCallback<GenericRPCResponse<String>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        logger.severe(caught.toString());
                    }

                    @Override
                    public void onSuccess(GenericRPCResponse<String> result) {
                        if(result.isSuccess()) {
                            eventCreationDialog.close();
                            MaterialToast.fireToast(result.getPayload(), 3000);
                            resetEventCreator();
                        } else {
                            MaterialToast.fireToast(result.getPayload(), 4000);
                        }
                    }
                });
    }

    @UiHandler("close")
    void onCloseClick(ClickEvent e) {
        viewEventDialog.close();
        resetEventViewDialog();
    }

    @UiHandler("addComment")
    void onAddCommentClick(ClickEvent e) {
        cewServiceAsync.writeComment(UiManager.getInstance().getUserInfo().getAuthToken(), commentTitle.getText(), commentMessage.getText(), selectedEvent.getId(), new AsyncCallback<GenericRPCResponse<String>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(GenericRPCResponse<String> result) {
                if(result.isSuccess()) {
                    commentMessage.setText(null);
                    commentTitle.setText(null);
                }

                MaterialToast.fireToast(result.getPayload());
            }
        });
    }
}
