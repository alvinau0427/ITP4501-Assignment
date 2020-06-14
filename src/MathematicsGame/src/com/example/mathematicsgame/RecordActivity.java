package com.example.mathematicsgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RecordActivity extends Activity {
	RelativeLayout rootLayout;
	Panel view;
	Button btnBack;
	
	SQLiteDatabase db;
	String sql, dataStr, data;
	Cursor cursor = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		btnBack = (Button) findViewById(R.id.btnBack);	
		final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		
		btnBack.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
        		v.startAnimation(animAlpha);
        		Intent recordActivity = new Intent(RecordActivity.this, MenuActivity.class);
        		startActivity(recordActivity);
        	}
		});	
		
		rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
		view = new Panel(this);
		rootLayout.addView(view);
	}
	
	class Panel extends View {
		private Paint[] arrPaintArc;
		private Paint PaintText = null;
		Paint paint = new Paint();
		
		public Panel(Context context) {
			super(context);
			this.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
			BlurMaskFilter PaintBGBlur = new BlurMaskFilter(1, BlurMaskFilter.Blur.INNER);
			
			arrPaintArc = new Paint[5];
			for(int i = 0; i < 5; i++)  
            {  
                arrPaintArc[i] = new Paint();             
                arrPaintArc[i].setColor(Color.BLACK);
                arrPaintArc[i].setStyle(Paint.Style.FILL);  
                arrPaintArc[i].setStrokeWidth(4);  
                arrPaintArc[i].setMaskFilter(PaintBGBlur);  
            }
			
			PaintText = new Paint();  
            PaintText.setColor(Color.BLACK);  
            PaintText.setTextSize(30);
            PaintText.setTypeface(Typeface.DEFAULT_BOLD);
		}	
		
		public void onDraw(Canvas c) {
			super.onDraw(c);
			
			try {
				db = SQLiteDatabase.openDatabase("/data/data/com.example.mathematicsgame/QuickMathDB", null, SQLiteDatabase.OPEN_READWRITE);
				cursor = db.rawQuery("SELECT * FROM GamesLog ORDER BY gameNo ASC", null);
				
				arrPaintArc[0].setTextSize(25); // x-axis font size
				arrPaintArc[3].setTextSize(25); // x-axis font size
				
				int i = 0;
				
				float lnWidth = getWidth() * 0.01f; // y-axis mark line width
				float lnSpace = getWidth() * 0.055f; // y-axis mark space
				
				float startX = getWidth() * 0.1f; // x-axis start width
				float endX = getWidth() * 0.2f; // x-axis mark space
				
				float startY = getHeight() * 0.75f; // point of y-axis * x-axis
				float endY = getHeight() * 0.5f;
				
				float initX = startX;
				float initY = startY;
				
				float rectHeight = 10; // bar chart height
				
				// Y axis
				for(i = 0; i <= 20; i++) {
					startY = initY - (i + 1) * lnSpace;
					endY = startY;
					if(i == 0) {
						continue;
					}
					c.drawLine(startX - lnWidth, startY + lnSpace, initX, endY + lnSpace, PaintText);
					c.drawText(Integer.toString(i) + "s", initX - 64, endY + lnSpace, PaintText); // mark space with y-axis
				}
				c.drawLine( startX ,startY ,initX ,initY, PaintText);
				
				// X axis
				int[] arrTime = new int [999];
				int tempCount = 0;
				while(cursor.moveToNext()) {
					int gameNo = cursor.getInt(cursor.getColumnIndex("gameNo"));
					int correctCount = cursor.getInt(cursor.getColumnIndex("correctCount"));
					int playTime = cursor.getInt(cursor.getColumnIndex("playTime"));
					arrTime[tempCount] = playTime;
					
					startX = initX + (tempCount + 1) * lnSpace;
					endX = startX;
					if (playTime <= 20) {
						c.drawRect(startX - rectHeight, initY, startX + rectHeight, initY - arrTime[tempCount] * lnSpace, arrPaintArc[0]);
						c.drawText("G" + Integer.toString(gameNo), startX - 10, initY + lnSpace, arrPaintArc[0]);
						float bufferX = startX-20;
						c.drawText(System.getProperty("line.separator") + Integer.toString(correctCount) + "C", bufferX, initY + lnSpace + 40, arrPaintArc[0]);
						tempCount++;
					}
				}
				c.drawLine( initX, initY, getWidth() - 10, initY, PaintText);
			
				db.close();
			}catch(Exception e) {
				Toast.makeText(RecordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}
}