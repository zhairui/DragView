package com.way.app;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.animoto.android.R;
import com.way.view.DragGridView;
import com.way.view.DragTest;
import com.way.view.DragTest.OnRearrangeListener;
import com.way.view.DragTest.RowListener;

/**
 * MainActivity
 * 
 * @author way
 * 
 */
public class MainActivity extends Activity{
	static Random random = new Random();
	static String[] words = "我 是 一 只 大 空 间".split(" ");
	DragTest mDragGridView;
	Button mAddBtn, mViewBtn;
	ArrayList<String> poem = new ArrayList<String>();
	int count=0;
	LinearLayout linear;
	LayoutParams mWindowParams;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		count=4;
		mDragGridView = ((	DragTest) findViewById(R.id.vgv));
		mAddBtn = ((Button) findViewById(R.id.add_item_btn));
		mViewBtn = ((Button) findViewById(R.id.view_poem_item));
		linear=(LinearLayout) findViewById(R.id.linear);
		setListeners();
		
		
	}

	private void setListeners() {
		/*mDragGridView.setOnRearrangeListener(new OnRearrangeListener() {
			public void onRearrange(int oldIndex, int newIndex) {
				String word = poem.remove(oldIndex);
				if (oldIndex < newIndex)
					poem.add(newIndex, word);
				else
					poem.add(newIndex, word);
			}
		});*/
		/*mDragGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mDragGridView.removeViewAt(arg2);
				poem.remove(arg2);
			}
		});*/
		for(int i=0;i<count;i++){
			TextView text=new TextView(MainActivity.this);
			text.setText(i+"行");
			text.setTextSize(20);
			LinearLayout.LayoutParams layout=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,200);
			text.setBackgroundColor(Color.BLUE);
			
			layout.setMargins(0, 20, 0, 0);
			text.setLayoutParams(layout);
			linear.addView(text);
		}
		
		
		
		mDragGridView.setRowListener(new RowListener() {
			
			@Override
			public int getRow() {
				// TODO Auto-generated method stub
				return count;
			}
		});
		
		mAddBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				for(int i=0;i<count;i++){
					String word = words[random.nextInt(words.length)];
					ImageView view = new ImageView(MainActivity.this);
					view.setImageBitmap(getThumb(word));
					mDragGridView.addView(view);
					poem.add(word);
				}
			}
		});
		mViewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String finishedPoem = "";
				for (String s : poem)
					finishedPoem += s + " ";
				new AlertDialog.Builder(MainActivity.this).setTitle("这是你选择的")
						.setMessage(finishedPoem).show();
			}
		});
	}

	private Bitmap getThumb(String s) {
		Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();

		paint.setColor(Color.rgb(random.nextInt(128), random.nextInt(128),
				random.nextInt(128)));
		paint.setTextSize(24);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawRect(new Rect(0, 0, 150, 150), paint);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(s, 75, 75, paint);

		return bmp;
	}
	


}