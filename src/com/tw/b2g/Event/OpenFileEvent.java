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
import android.widget.TextView;
import com.tw.b2g.BrailleNotes;
import com.tw.b2g.FileBrowser;
import com.tw.b2g.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class OpenFileEvent extends Event implements b2gEvent {
    public OpenFileEvent(Context ctxt) {
        super(ctxt);
    }

    @Override
    public boolean handleRequest(final EditText screen) {
        showDialog(screen);
        return true;
    }

    @Override
    public boolean handleResponse(EditText screen) {
        showDialog(screen);
        return false;
    }

    private void openFile(final EditText screen, final CharSequence fileName) {
        final StringBuffer result = readFile(fileName);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
        builder.setMessage(ctxt.getString(R.string.discard_changes))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        readFile(fileName);
                        screen.setText(result);
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

    private StringBuffer readFile(CharSequence fileName) {
        final StringBuffer result = new StringBuffer();

        try {
            File file = new File(fileName.toString());
            FileReader fr = new FileReader(file);

            if (file.length() != 0 && !file.isDirectory()) {
                char[] buffer;
                buffer = new char[1024];    // made it bigger just in case
                int char_read;
                do {
                    char_read = fr.read(buffer, 0, 1024);
                    if (char_read >= 0) {
                        result.append(buffer, 0, char_read);
                    }
                } while (char_read >= 0);
            }
        } catch (FileNotFoundException e) {
            //Todo: Handle corner case scenario for opening already opened file.
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return result;
    }

    private void showDialog(final EditText screen) {

        int layoutid = R.layout.dialog_savefile_noauto;

        LayoutInflater factory = LayoutInflater.from(ctxt);
        View dialogView = factory.inflate(layoutid, null);
        final TextView textEntryView = (TextView) dialogView.findViewById(R.id.filename_edit);

        textEntryView.setText(Environment.getExternalStorageDirectory().getPath());

        String filePath = Environment.getExternalStorageDirectory().getPath();


        if (textEntryView != null) {
            if ((!BrailleNotes.fileName.equals(""))) {
                filePath = BrailleNotes.fileName;
                textEntryView.setText(BrailleNotes.fileName);
            } else {
                textEntryView.setText(Environment.getExternalStorageDirectory().getPath());
            }
        }

        final String finalFilePath = filePath;
        final AlertDialog d = new AlertDialog.Builder(ctxt)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(ctxt.getString(R.string.open_file_title))
                .setView(dialogView)
                .setPositiveButton("Open", null)
                .setNeutralButton("Browser", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(ctxt, FileBrowser.class);
                        intent.setAction(finalFilePath);
                        ((Activity) ctxt).startActivityForResult(intent, b2gEvent.REQUEST_FILE_OPEN);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                if (b != null) {
                    b.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            File file = new File(textEntryView.getText().toString());
                            if (file.exists() && file.isDirectory()) {
                                //do nothing
                            } else {
                                BrailleNotes.fileName = textEntryView.getText().toString();
                                openFile(screen, BrailleNotes.fileName);
                                d.dismiss();
                            }

                        }
                    });
                }
            }
            });
        d.show();
    }
}