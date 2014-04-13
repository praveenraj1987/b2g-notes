package com.tw.b2g;

import android.content.Context;
import android.text.Spannable;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;



public class FileViewArrayAdapter extends ArrayAdapter<String>
{

	private final Spannable.Factory sf = new Spannable.Factory();
	private final LayoutInflater factory = LayoutInflater.from(getContext());
	
	protected int mode = 0;
	
	public FileViewArrayAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);		
	}
	
	public FileViewArrayAdapter(Context context, List<String> objects) {
		super(context, 0, objects);
	}

	public FileViewArrayAdapter(Context context) {
		super(context, 0);
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{		
		File f = new File(getItem(position));
		View textEntryView;
		if (convertView != null)
			textEntryView = convertView;
		else
			textEntryView = factory.inflate(R.layout.filebrowser_item, null);
		
		TextView tv = (TextView) textEntryView.findViewById(R.id.itemtext);
		tv.setText(getItem(position));
			Spannable text = sf.newSpannable(f.getName() + "\n" + getFileSize(f));
			text.setSpan(new AbsoluteSizeSpan(30), 0, f.getName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			tv.setText(text);

		return textEntryView;

	}

	public static String getFileSize(File file)
	{
		long size = file.length();
		
		if (file.isDirectory())
			return "Directory";
		
		if (size < 0)
			return "0";
		else if (size == 1)
			return "1 Byte";
		else if (size < 2048)
			return size + " Bytes";
		else if (size < 1024*1024*2)
			return ((int) (size/1024)) + " KB";
		else 
			return "\n" + Math.round(100.0*size/(1024*1024))/100.0 + " MB";
	}
	
}