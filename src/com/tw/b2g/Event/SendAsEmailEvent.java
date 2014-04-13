package com.tw.b2g.Event;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.tw.b2g.BrailleNotes;
import com.tw.b2g.FileBrowser;
import com.tw.b2g.R;

import java.io.File;

public class SendAsEmailEvent extends Event implements b2gEvent {
    Button buttonSend;
    EditText textTo;
    EditText textSubject;
    String message;


    public SendAsEmailEvent(Context ctxt) {
        super(ctxt);
    }


    @Override
    public boolean handleRequest(EditText screen) {
        showDialog(screen);
        return true;
    }

    @Override
    public boolean handleResponse(EditText screen) {
        return false;
    }

    private void showDialog(final EditText screen) {
        int layoutid = R.layout.dialog_email;
        LayoutInflater factory = LayoutInflater.from(ctxt);
        View dialogView = factory.inflate(layoutid, null);

        Button buttonSend = (Button) dialogView.findViewById(R.id.buttonSend);
        final EditText textTo = (EditText) dialogView.findViewById(R.id.editTextTo);
        final EditText textSubject = (EditText) dialogView.findViewById(R.id.editTextSubject);

        final AlertDialog d = new AlertDialog.Builder(ctxt)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(ctxt.getString(R.string.saveas_file_title))
                .setView(dialogView).create();
        d.show();

        buttonSend.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String to = textTo.getText().toString();
              String subject = textSubject.getText().toString();
              String message = screen.getText().toString();

              Intent email = new Intent(Intent.ACTION_SEND);
              email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
              //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
              //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
              email.putExtra(Intent.EXTRA_SUBJECT, subject);
              email.putExtra(Intent.EXTRA_TEXT, message);

              email.setType("message/rfc822");

              ctxt.startActivity(Intent.createChooser(email, "Choose an Email client :"));
              d.dismiss();
          }
      }
        );
    }
}