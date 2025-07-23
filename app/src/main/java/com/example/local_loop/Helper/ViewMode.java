package com.example.local_loop.Helper;

public enum ViewMode {
        VIEW, //for intent names
        //General view modes of Activity/page
        DEFAULT,
        DELETE,
        EDIT,

        //Participant views
        PARTICIPANT_BROWSING,
        PARTICIPANT_EVENTS,

        //Organizer views
        ORG_EVENTS,
        ORG_PARTICPANTSLIST, //AttendeeMode

        //Admin views
        ADMIN_CATEGORIES,
        ADMIN_USERS,
        ADMIN_EVENTS


}
