package com.tw.b2g;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import com.tw.b2g.Event.*;


public class BrailleNotes extends Activity {

    protected EditText screen = null;
    public static String fileName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        screen = (EditText) findViewById(R.id.screenText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                return (new NewFileEvent(this)).handleRequest(screen);
            case R.id.action_open:
                return (new OpenFileEvent(this)).handleRequest(screen);
            case R.id.action_save:
                return (new SaveFileEvent(this)).handleRequest(screen);
            case R.id.action_saveas:
                return (new SaveAsFileEvent(this)).handleRequest(screen);
            case R.id.action_email:
                return (new SendAsEmailEvent(this)).handleRequest(screen);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case b2gEvent.REQUEST_FILE_OPEN: {
                    fileName = data.getAction();
                    new OpenFileEvent(this).handleResponse(screen);
                    break;
                }
                case b2gEvent.REQUEST_FILE_SAVE_AS: {
                    if(fileName.equals(data.getAction())) {
                        new SaveFileEvent(this).handleResponse(screen);
                    }
                    else{
                        fileName = data.getAction();
                        new SaveAsFileEvent(this).handleResponse(screen);
                    }
                    break;
                }
            }
        }
    }





    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

}
