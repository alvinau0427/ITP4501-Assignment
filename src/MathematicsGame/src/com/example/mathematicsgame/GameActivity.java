package com.example.mathematicsgame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	TextView tvShowCorrect, tvShowWrong, tvShowTime, tvResult;
	Button btnBegin, btnFinish, btnSelection1, btnSelection2, btnSelection3, btnSelection4;
	String [][] listQuestion;
	String[] ansArray = new String [4];
	String result = "", answer = "";
	
	int correct = 0, wrong = 0, i = 0, j = 0, ans = 100, questionCount = 1, gameCount = 1;
	int getStopTime = 0;
	int indexNum;
	int chkInputInt;
	String userResult;
	
	Handler customHandler = new Handler();
	long startTime = 0L;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	
	JSONArray question = null;
	DownloadTask task = null;
	Cursor cursor = null;
	private static String url = "http://itdmoodle.hung0530.com/ptms/questions_ws.php";
	
	SQLiteDatabase db;
	String sql;
	Calendar c = Calendar.getInstance(); 
	String currentDate = c.get(Calendar.DAY_OF_MONTH) + " - " + ((c.get(Calendar.MONTH))+1) + " - " + c.get(Calendar.YEAR);
	
	Runnable updateTimerThread = new Runnable() {
		 public void run() {
			 timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			 updatedTime = timeSwapBuff + timeInMilliseconds;
			 int secs = (int) (updatedTime / 1000);
			 int mins = secs / 60;
			 secs = secs % 60;
			 int milliseconds = (int) (updatedTime % 1000);
			 tvShowTime.setText("" + mins + ":" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds));
			 getStopTime = Integer.parseInt(String.format("%02d", secs));
			 customHandler.postDelayed(this, 0);
		 }
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		
		tvShowCorrect = (TextView)findViewById(R.id.tvShowCorrect);
		tvShowWrong = (TextView)findViewById(R.id.tvShowWrong);
		tvShowTime = (TextView)findViewById(R.id.tvShowTime);
		tvResult = (TextView)findViewById(R.id.tvResult);
		btnBegin = (Button)findViewById(R.id.btnBegin);
		btnFinish = (Button)findViewById(R.id.btnFinish);
		btnSelection1 = (Button)findViewById(R.id.btnSelection1);
		btnSelection2 = (Button)findViewById(R.id.btnSelection2);
		btnSelection3 = (Button)findViewById(R.id.btnSelection3);
		btnSelection4 = (Button)findViewById(R.id.btnSelection4);
		
		setStatus();resetColor();
		btnFinish.setEnabled(false);
		tvResult.setText("Please get the question first ...");
		
		btnBegin.setOnClickListener(new Button.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		countGame();
        		deleteTable();
        		getQuestion();
        		v.startAnimation(animAlpha);
        		resetStatus();
        		btnBegin.setEnabled(false);
        		startTime = SystemClock.uptimeMillis();
        		customHandler.postDelayed(updateTimerThread, 0);
			}
		});
		
		btnFinish.setOnClickListener(new Button.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		resetStatus();
        		resetColor();
        		btnFinish.setEnabled(false);
        		if (i < 9) {
        			questionCount++;
        			i++;
    				setQuestion(i, j);
        		}else {
        			record();
        			tvResult.setText("Game Over");
    				Intent moveActivity = new Intent(getApplicationContext(), ResultActivity.class);
    				moveActivity.putExtra("time", tvShowTime.getText().toString());
    				moveActivity.putExtra("correct", String.valueOf(correct));
    				moveActivity.putExtra("wrong", String.valueOf(wrong));
            		startActivity(moveActivity);
        		}
			}
		});
		
		btnSelection1.setOnClickListener(new Button.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		btnFinish.setEnabled(true);
        		chkInputInt = Integer.parseInt(btnSelection1.getText().toString());
        		if (answer == btnSelection1.getText().toString()) {
        			showToast(GameActivity.this, "Correct!", 500);
        			correct++;
        			userResult = "Correct";
        			score();
        			tvShowCorrect.setText(String.valueOf(correct));
        			btnSelection1.setBackgroundColor(Color.rgb(175, 253, 141));
        		}else {
        			showToast(GameActivity.this, "Wrong! Correct answer = " + answer, 500);
        			wrong++;
        			userResult = "Wrong";
        			score();
        			tvShowWrong.setText(String.valueOf(wrong));
        			btnSelection1.setBackgroundColor(Color.rgb(253, 141, 175));
        		}
        		setStatus();
			}
		});
		
		btnSelection2.setOnClickListener(new Button.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		btnFinish.setEnabled(true);
        		chkInputInt = Integer.parseInt(btnSelection2.getText().toString());
        		if (answer == btnSelection2.getText().toString()) {
        			showToast(GameActivity.this, "Correct!", 500);
        			correct++;
        			userResult = "Correct";
        			score();
        			tvShowCorrect.setText(String.valueOf(correct));
        			btnSelection2.setBackgroundColor(Color.rgb(175, 253, 141));
        		}else {
        			showToast(GameActivity.this, "Wrong! Correct answer = " + answer, 500);
        			wrong++;
        			userResult = "Wrong";
        			score();
        			tvShowWrong.setText(String.valueOf(wrong));
        			btnSelection2.setBackgroundColor(Color.rgb(253, 141, 175));
        		}
        		setStatus();
			}
		});
		
		btnSelection3.setOnClickListener(new Button.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		btnFinish.setEnabled(true);
        		chkInputInt = Integer.parseInt(btnSelection3.getText().toString());
        		if (answer == btnSelection3.getText().toString()) {
        			showToast(GameActivity.this, "Correct!", 500);
        			correct++;
        			userResult = "Correct";
        			score();
        			tvShowCorrect.setText(String.valueOf(correct));
        			btnSelection3.setBackgroundColor(Color.rgb(175, 253, 141));
        		}else {
        			showToast(GameActivity.this, "Wrong! Correct answer = " + answer, 500);
        			wrong++;
        			userResult = "Wrong";
        			score();
        			tvShowWrong.setText(String.valueOf(wrong));
        			btnSelection3.setBackgroundColor(Color.rgb(253, 141, 175));
        		}
        		setStatus();
			}
		});
		
		btnSelection4.setOnClickListener(new Button.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		btnFinish.setEnabled(true);
        		chkInputInt = Integer.parseInt(btnSelection4.getText().toString());
        		if (answer == btnSelection4.getText().toString()) {
        			showToast(GameActivity.this, "Correct!", 500);
        			correct++;
        			userResult = "Correct";
        			score();
        			tvShowCorrect.setText(String.valueOf(correct));
        			btnSelection4.setBackgroundColor(Color.rgb(175, 253, 141));
        		}else {
        			showToast(GameActivity.this, "Wrong! Correct answer = " + answer, 500);
        			wrong++;
        			userResult = "Wrong";
        			score();
        			tvShowWrong.setText(String.valueOf(wrong));
        			btnSelection4.setBackgroundColor(Color.rgb(253, 141, 175));
        		}
        		setStatus();
			}
		});
	}
	
	private class DownloadTask extends AsyncTask<String, Integer, String> {
		protected String doInBackground(String... values) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(values[0]);
				HttpResponse response = client.execute(request);

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
 
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result += line + "\n";
                }
                rd.close();
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
		}
		
		@Override
        protected void onPostExecute(String result) {
			try {
    			String reply = "";
    	        reply = result.toString();
    	        JSONObject json = new JSONObject(reply);
    			JSONArray codeArray = json.getJSONArray("Questions");
    			listQuestion = new String[codeArray.length()][2];
    			for (int i = 0; i < codeArray.length(); i++) {
    				j = 0;
    				listQuestion[i][j] = codeArray.getJSONObject(i).getString("question");
        			listQuestion[i][j + 1] = codeArray.getJSONObject(i).getString("answer");
    			}
    			setQuestion(0, 0);
    		}catch (Exception e) {
    			result = e.getMessage();
    		}
        }
	}
	
	public void getQuestion() {
		if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED)) {
            task = new DownloadTask();
            task.execute(url);
        }
	}
	
	public void setQuestion(int i, int j) {
		try {
			tvResult.setText(listQuestion[i][j]);
			answer = (listQuestion[i][j + 1].toString());
			randomButtonSite();
		}catch (Exception e) {
			tvResult.setText("setQuestion() exception has been catched.");
		}
	}
	
	public void randomButtonSite(){
		int buffer[] = {-1, -1, -1, -1};
		int arr[] = buffer;
		try {
			for(int i = 0; i < arr.length; i++) {
				indexNum = (int)(Math.floor(Math.random() * 4));
				if(i == 0) {
					arr[i] = indexNum;
				}
				if(i > 0) {
					while(checkDuplicate(indexNum, arr)) {
						indexNum = (int)(Math.floor(Math.random() * 4));
					}
					arr[i] = indexNum;
				}
			}
			
			ansArray[arr[0]] = (listQuestion[i][j + 1]);
			ansArray[arr[1]] = (String.valueOf((int)(Math.random() * 100)));
			ansArray[arr[2]] = (String.valueOf((int)(Math.random() * 100)));
			ansArray[arr[3]] = (String.valueOf((int)(Math.random() * 100)));
			
			btnSelection1.setText(ansArray[0]);
			btnSelection2.setText(ansArray[1]);
			btnSelection3.setText(ansArray[2]);
			btnSelection4.setText(ansArray[3]);
		}catch (Exception e) {
			tvResult.setText("randomButtonSite() exception has been catched.");
		}
	}
	
	public boolean checkDuplicate(int indexNum, int arr[]){
		int chkArr[] = arr;
		try {
	    	for(int i = 0; i < chkArr.length; i++) {
	    		if(chkArr[i] == indexNum) {
	    			return true;
	    		}
	    	}
		}catch (Exception e) {
			tvResult.setText("checkDuplicate() exception has been catched.");
		}
		return false;
	}
	
	public void countGame() {
		try {
			db = SQLiteDatabase.openDatabase("/data/data/com.example.mathematicsgame/QuickMathDB", null, SQLiteDatabase.OPEN_READWRITE);
			cursor = db.rawQuery("SELECT * FROM GamesLog ORDER BY gameNo DESC", null);
			
			while(cursor.moveToFirst()) {
				int gameNo = cursor.getInt(cursor.getColumnIndex("gameNo"));
				gameCount = (gameNo + 1);
				break;
			}
			db.close();
		}catch(Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void deleteTable() {
		try {
			db = SQLiteDatabase.openDatabase("/data/data/com.example.mathematicsgame/QuickMathDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
			
			sql = "DROP TABLE IF EXISTS QuestionsLog;";
			db.execSQL(sql);
			
			sql = "CREATE TABLE QuestionsLog(questionNo int PRIMARY KEY, question text, answer int, userAnswer int, result text);";
			db.execSQL(sql);
			
			db.close();
		}catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void score() {
		try {
			db = SQLiteDatabase.openDatabase("/data/data/com.example.mathematicsgame/QuickMathDB", null, SQLiteDatabase.OPEN_READWRITE);
			ContentValues pairValue = new ContentValues();
			
			pairValue.put("questionNo", questionCount);
			pairValue.put("question", (listQuestion[i][j]));
			pairValue.put("answer", (listQuestion[i][j+1]));
			pairValue.put("userAnswer", chkInputInt);
			pairValue.put("result", userResult.toString());
			db.insert("QuestionsLog", null, pairValue);
		}catch(Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void record() {
		try {
			db = SQLiteDatabase.openDatabase("/data/data/com.example.mathematicsgame/QuickMathDB", null, SQLiteDatabase.OPEN_READWRITE);
			ContentValues pairValue = new ContentValues();
			
			pairValue.put("gameNo", gameCount);
			pairValue.put("playDate", currentDate.toString());
			pairValue.put("playTime", getStopTime);
			pairValue.put("correctCount", Integer.parseInt(tvShowCorrect.getText().toString()));
			db.insert("GamesLog", null, pairValue);
		}catch(Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public static void showToast(final Activity activity, final String word, final long time){
        activity.runOnUiThread(new Runnable() { 
            public void run() {
                final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_LONG);
                toast.show();
                Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                       public void run() {
                           toast.cancel(); 
                       }
                }, time);
            }
        });
    }
	
	public void setStatus() {
		btnSelection1.setEnabled(false);
		btnSelection2.setEnabled(false);
		btnSelection3.setEnabled(false);
		btnSelection4.setEnabled(false);
	}
	
	public void resetStatus() {
		btnSelection1.setEnabled(true);
		btnSelection2.setEnabled(true);
		btnSelection3.setEnabled(true);
		btnSelection4.setEnabled(true);
	}
	
	public void resetColor() {
		btnSelection1.setBackgroundColor(Color.rgb(97, 175, 194));
		btnSelection2.setBackgroundColor(Color.rgb(97, 175, 194));
		btnSelection3.setBackgroundColor(Color.rgb(97, 175, 194));
		btnSelection4.setBackgroundColor(Color.rgb(97, 175, 194));
	}
}