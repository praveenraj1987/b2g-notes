package com.tw.b2g.Event;

import android.content.Context;
import android.widget.EditText;
import com.tw.b2g.BrailleNotes;

import java.io.*;

public class SaveFileEvent extends Event implements b2gEvent {
    public SaveFileEvent(Context ctxt) {
        super(ctxt);
    }

    @Override
    public boolean handleRequest(EditText screen) {
        if (BrailleNotes.fileName.equalsIgnoreCase("")) {
            (new SaveAsFileEvent(ctxt)).handleRequest(screen);
        } else {
            saveFile(screen);
        }
        return true;
    }

    @Override
    public boolean handleResponse(EditText screen) {
        saveFile(screen);
        return true;
    }

    private void saveFile(EditText screen) {
        try {
            File file = new File(BrailleNotes.fileName);
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            BufferedWriter buffer = new BufferedWriter(fw);
            buffer.write(screen.getText().toString());
            buffer.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }


    }
}