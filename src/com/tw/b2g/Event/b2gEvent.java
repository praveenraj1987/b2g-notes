package com.tw.b2g.Event;

import android.content.Context;
import android.widget.EditText;

public interface b2gEvent {
    public final static int REQUEST_FILE_OPEN = 1;
    public final static int REQUEST_FILE_SAVE_AS = 2;
    public boolean handleRequest(EditText screen);
    public boolean handleResponse(EditText screen);
}


class Event{
    protected Context ctxt;

    Event(Context ctxt) {
        this.ctxt = ctxt;
    }
}