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

public class SaveAsFileEvent extends Event implements b2gEvent {
    public SaveAsFileEvent(Context ctxt) {
        super(ctxt);
    }

    @Override
    public boolean handleRequest(EditText screen) {
        showDialog(screen);
        return true;
    }

    @Override
    public boolean handleResponse(EditText screen) {
        showDialog(screen);
        return false;
    }


    private void showDialog(final EditText screen) {

        int layoutid = R.layout.dialog_savefile_noauto;
        LayoutInflater factory = LayoutInflater.from(ctxt);
        View dialogView = factory.inflate(layoutid, null);
        EditText textEntryView = null;
        if (dialogView != null) {
            textEntryView = (EditText) dialogView.findViewById(R.id.filename_edit);
        }

        if (textEntryView != null) {
            if (!BrailleNotes.fileName.equals(""))
                textEntryView.setText(BrailleNotes.fileName);
            else
                textEntryView.setText(Environment.getExternalStorageDirectory().getPath());
        }

        final AlertDialog d = new AlertDialog.Builder(ctxt)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(ctxt.getString(R.string.saveas_file_title))
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNeutralButton("Browser", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(ctxt, FileBrowser.class);
                        intent.setAction(Environment.getExternalStorageDirectory().getPath());
                        ((Activity) ctxt).startActivityForResult(intent, b2gEvent.REQUEST_FILE_SAVE_AS);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        final EditText finalTextEntryView = textEntryView;
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                if (b != null) {
                    b.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {

                            if (finalTextEntryView != null) {
                                File file = new File(finalTextEntryView.getText().toString());
                                if (file.exists() && file.isDirectory()) {
                                    //do nothing
                                } else {
                                    BrailleNotes.fileName = finalTextEntryView.getText().toString();
                                    if (file.exists()) {
                                        overwriteFileAs(screen, BrailleNotes.fileName);

                                    } else {
                                        saveNewFile(screen);
                                    }
                                    d.dismiss();
                                }
                            }
                        }
                    });
                }
            }
        });
        d.show();
    }

    private void overwriteFileAs(final EditText screen, String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
        builder.setMessage(ctxt.getString(R.string.overwrite_file))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveNewFile(screen);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveNewFile(final EditText screen) {
        (new SaveFileEvent(ctxt)).handleRequest(screen);
    }


}
