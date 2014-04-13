package com.tw.b2g.Event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import com.tw.b2g.BrailleNotes;
import com.tw.b2g.R;

public class NewFileEvent extends Event implements b2gEvent{
    public NewFileEvent(Context ctxt) {
        super(ctxt);
    }

    @Override
    public boolean handleRequest(final EditText screen){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
        builder.setMessage(ctxt.getString(R.string.new_file_msg))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        screen.setText("");
                        BrailleNotes.fileName = "";

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();

        alert.show();

        return true;
    }

    @Override
    public boolean handleResponse(EditText screen) {
        return false;
    }

}