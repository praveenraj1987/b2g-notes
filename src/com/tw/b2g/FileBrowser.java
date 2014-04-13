package com.tw.b2g;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.io.File;
import java.util.Arrays;

public class FileBrowser extends Activity
{
	private final static int MENU_MKDIR = Menu.FIRST;

	private final int DIALOG_FILE_OPTIONS = 1;
	private final int DIALOG_RENAME = 2;
	private final int DIALOG_MKDIR = 3;
	private final int DIALOG_SHOULD_DELETE = 4;
	private final int DIALOG_COULDNT_DELETE = 5;
	private final int DIALOG_COULDNT_RENAME = 6;
	private final int DIALOG_COULDNT_MKDIR = 7;
	private final int DIALOG_COULDNT_DELETEDIR = 8;

	private File dialog_file;

	protected View textEntryView = null;
	protected View mkDIRView = null;

	protected FileViewArrayAdapter fileAdapter;
	protected ListView fileBrowserList;
	protected TextView fileBrowserPath;
	protected TextView fileBrowserNoFiles;

	protected CharSequence filePath = "";

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.filebrowser_all);
        Button upButton = (Button) findViewById(R.id.fb_upbutton);
        Button selectButton = (Button) findViewById(R.id.fb_thisbutton);

        Button cancelButton = (Button) findViewById(R.id.fb_cancel_button);


        selectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (filePath != null){
                    setResult(RESULT_OK, (new Intent()).setAction((String) filePath));
                    finish();
                }
            }
        });
		upButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				File f = new File((String) filePath);
				if (filePath != null && !filePath.equals(Environment.getExternalStorageDirectory().getPath()))
		            refreshFileBrowserList(f.getParent());
			}
		});

        cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		            finish();
				}
			});

		fileBrowserList = (ListView) findViewById(R.id.filelist);
		fileBrowserPath = (TextView) findViewById(R.id.filepath);
		fileBrowserNoFiles = (TextView) findViewById(R.id.nofilemessage);

		fileAdapter = new FileViewArrayAdapter(this);
		fileBrowserList.setAdapter(fileAdapter);

		fileBrowserList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				File f = new File(arg0.getItemAtPosition(arg2).toString());
				dialog_file = f;
				showDialog(DIALOG_FILE_OPTIONS);
				return true;
		}} );

		fileBrowserList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

				File f = new File(arg0.getItemAtPosition(arg2).toString());
				if (f.isDirectory())
					refreshFileBrowserList(f.toString());
				else {
					filePath = arg0.getItemAtPosition(arg2).toString();
					setResult(RESULT_OK, (new Intent()).setAction((String) filePath));

					finish();
				}
		}} );

		Intent intent = getIntent();
		filePath = intent.getAction();

		refreshFileBrowserList(filePath.toString());

	}

	protected void onPrepareDialog(int id, Dialog dialog)
	{
		switch (id) {
			case DIALOG_FILE_OPTIONS: {
				dialog.setTitle(dialog_file.getName());
				break;
			}
			case DIALOG_SHOULD_DELETE: {
				dialog.setTitle("Confirm deletion of " + dialog_file.getName());
				break;
			}
			case DIALOG_RENAME: {
				EditText v = (EditText) textEntryView.findViewById(R.id.filename_edit);
				v.setText(dialog_file.getName());
				v.setSelection(v.getText().length(), v.getText().length());
				break;
			}
			case DIALOG_MKDIR: {
				EditText v = (EditText) mkDIRView.findViewById(R.id.filename_edit);
				v.setText("");
			}
		}
	}

	protected Dialog onCreateDialog(int id)
	{
		switch (id) {
			default:
			case DIALOG_FILE_OPTIONS: {

				String[] options = {new String("Delete File"), new String("Rename File")};

				return new AlertDialog.Builder(this)
				.setTitle(dialog_file.getName())
				.setItems(options, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0)
						{
							showDialog(DIALOG_SHOULD_DELETE);
						} else if (which == 1) {
							showDialog(DIALOG_RENAME);
						}
					}
				})
				.create();
			}
			case DIALOG_RENAME: {
				LayoutInflater factory = LayoutInflater.from(this);
				textEntryView = factory.inflate(R.layout.dialog_rename, null);

				return new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle("Rename File")
					.setView(textEntryView)
					.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// what to do when positive clicked
							TextView v = (TextView) textEntryView.findViewById(R.id.filename_edit);

							File f = new File(dialog_file.getParent() + "/" + v.getText().toString());
							if (!dialog_file.renameTo(f))
							{
								showDialog(DIALOG_COULDNT_RENAME);
							}
							refreshFileBrowserList((String) filePath, false);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					})
				.create();
			}
			case DIALOG_MKDIR: {
				LayoutInflater factory = LayoutInflater.from(this);
				mkDIRView = factory.inflate(R.layout.dialog_mkdir, null);

				// the actual dialog being created
				return new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle("Create Directory")
					.setView(mkDIRView)
					.setPositiveButton("Create", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// what to do when positive clicked
							TextView v = (TextView) mkDIRView.findViewById(R.id.filename_edit);

							File f = new File(filePath + "/" + v.getText().toString());
							if (!f.mkdir())
							{
								showDialog(DIALOG_COULDNT_MKDIR);
							}
							refreshFileBrowserList((String) filePath, false);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					})
				.create();
			}
			case DIALOG_SHOULD_DELETE: {
				return new AlertDialog.Builder(this)
				.setTitle("Confirm deletion of " + dialog_file.getName())
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (!dialog_file.delete())
						{
							if (dialog_file.isDirectory())
								showDialog(DIALOG_COULDNT_DELETEDIR);
							else
								showDialog(DIALOG_COULDNT_DELETE);
						}
						refreshFileBrowserList((String) filePath, false);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				})
                .create();
			}
			case DIALOG_COULDNT_DELETE: {
				return new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage("Unable to delete file.")
				.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				})
                .create();
			}
			case DIALOG_COULDNT_DELETEDIR: {
				return new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage("Unable to delete directory.\n\n- A directory must be empty to be deleted")
				.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				})
                .create();
			}
			case DIALOG_COULDNT_RENAME: {
				return new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage("Unable to rename file.")
				.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				})
                .create();
			}
			case DIALOG_COULDNT_MKDIR: {
				return new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage("Unable to create directory.")
				.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				})
                .create();
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_MKDIR, 0, "Create Directory").setIcon(android.R.drawable.ic_menu_add);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case MENU_MKDIR:
				showDialog(DIALOG_MKDIR);
				return true;
		}

		return true;
	}

	public void onSaveInstanceState(Bundle savedInstanceState)
	{

		savedInstanceState.putInt("FB_mode", fileAdapter.mode);
		savedInstanceState.putString("FilePath", filePath.toString());
		if (dialog_file != null)
			savedInstanceState.putString("dialog_file", dialog_file.getAbsolutePath());
		else
			savedInstanceState.putString("dialog_file", "");

		super.onSaveInstanceState(savedInstanceState);
	}

	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);

		fileAdapter.mode = savedInstanceState.getInt("FB_mode");
		filePath = savedInstanceState.getString("FilePath");
		dialog_file = new File(savedInstanceState.getString("dialog_file"));

		refreshFileBrowserList(filePath.toString(), false);

	}

    private void refreshFileBrowserList(String location)
	{
		refreshFileBrowserList(location, true);
	}

	private void refreshFileBrowserList(String location, boolean backToTop)
	{
		if (location == null)
			location = "";

		File loc = new File(location);

		if (!loc.isDirectory())
			loc = loc.getParentFile();

		if (loc == null)
			loc = new File(Environment.getExternalStorageDirectory().getPath());

		File[] files;
		files = loc.listFiles();
		if (files != null)
			Arrays.sort(files);
		else
			files = new File[0];

		fileAdapter.clear();

		int i, nonHidden = 0, len = files.length;

		for(i = 0; i < len; i++)
		{
			if (files[i].isDirectory() && !files[i].isHidden())
			{
				fileAdapter.add(files[i].toString());
				nonHidden++;
			}
		}

		for(i = 0; i < len; i++)
		{
			if (!files[i].isDirectory() && !files[i].isHidden())
			{
				fileAdapter.add(files[i].toString());
				nonHidden++;
			}
		}

		if (nonHidden == 0)
			fileBrowserNoFiles.setVisibility(View.VISIBLE);
		else
			fileBrowserNoFiles.setVisibility(View.GONE);

		if (loc.getPath().equals("/"))
		{
			fileBrowserPath.setText("Location: /");
			filePath = "/";
		} else {
			fileBrowserPath.setText("Location: " + loc.getPath() + "/");
			filePath = loc.getPath() + "/";
		}

		if (backToTop)
			fileBrowserList.setSelection(0);

		setResult(1, (new Intent()).setAction((String) filePath));
	}
}