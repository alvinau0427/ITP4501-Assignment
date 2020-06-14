package com.example.mathematicsgame;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreActivity extends Activity {
	TextView tvData, tvShowNo, tvShowDate;
	Button btnBack;
	
	SQLiteDatabase db;
	String sql, dataStr, data, dataStrHeader;
	Cursor cursor = null;
	String[] columns = {"questionNo", "question", "answer", "userAnswer", "result"};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		
		final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		
		tvData = (TextView)findViewById(R.id.tvData);
		tvShowNo = (TextView)findViewById(R.id.tvShowNo);
		tvShowDate = (TextView)findViewById(R.id.tvShowDate);
		btnBack = (Button)findViewById(R.id.btnBack);
		dataStrHeader = String.format("%2s %-8s %6s %-6s %2s\n", "Number", "Question", "Answer", "Reply", "Result");
		
		btnBack.setOnClickListener(new Button.OnClickListener() {
			@Override
        	public void onClick(View v) {
        		v.startAnimation(animAlpha);
        		Intent scoreActivity = new Intent(ScoreActivity.this, ResultActivity.class);
        		startActivity(scoreActivity);
        	}
        });
		
		try {
			db = SQLiteDatabase.openDatabase("/data/data/com.example.mathematicsgame/QuickMathDB", null, SQLiteDatabase.OPEN_READWRITE);
			cursor = db.rawQuery("SELECT * FROM QuestionsLog ORDER BY questionNo ASC", null);
			
			dataStr = dataStrHeader;
			while(cursor.moveToNext()) {
				int questionNo = cursor.getInt(cursor.getColumnIndex("questionNo"));
				String question = cursor.getString(cursor.getColumnIndex("question"));
				int answer = cursor.getInt(cursor.getColumnIndex("answer"));
				int userAnswer = cursor.getInt(cursor.getColumnIndex("userAnswer"));
				String result = cursor.getString(cursor.getColumnIndex("result"));
				dataStr += String.format("%5s %9s %6s %5s %7s\n", questionNo, question, answer, userAnswer, result);
			}
			tvData.setText(dataStr);
			db.close();
		}catch(Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		try {
			db = SQLiteDatabase.openDatabase("/data/data/com.example.mathematicsgame/QuickMathDB", null, SQLiteDatabase.OPEN_READWRITE);
			cursor = db.rawQuery("SELECT * FROM GamesLog ORDER BY gameNo DESC", null);
			if(cursor.moveToNext()) {
				int gameNo = cursor.getInt(cursor.getColumnIndex("gameNo"));
				String playDate = cursor.getString(cursor.getColumnIndex("playDate"));
				tvShowNo.setText(String.valueOf(gameNo));
				tvShowDate.setText(playDate.toString());
			}
			db.close();
		}catch(Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}