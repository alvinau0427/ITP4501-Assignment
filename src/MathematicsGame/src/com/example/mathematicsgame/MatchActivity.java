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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MatchActivity extends Activity {
	TextView tvShowCorrect, tvShowWrong, tvShowTime, tvResult;
	EditText etInput;
	Button btnBegin, btnFinish;
	
	String [][] listQuestion;
	String[] ansArray = new String [4];
	String result = "", answer = "";
	
	int correct = 0, wrong = 0, i = 0, j = 0, ans = 100, questionCount = 0, gameCount = 1;
	int getStopTime = 0;
	int indexNum;
	int chkInputInt;
	String chkInputStr;
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
		setContentView(R.layout.activity_match);
		
		final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		
		tvShowCorrect = (TextView)findViewById(R.id.tvShowCorrect);
		tvShowWrong = (TextView)findViewById(R.id.tvShowWrong);
		tvShowTime = (TextView)findViewById(R.id.tvShowTime);
		tvResult = (TextView)findViewById(R.id.tvResult);
		etInput = (EditText)findViewById(R.id.etInput);
		btnBegin = (Button)findViewById(R.id.btnBegin);
		btnFinish = (Button)findViewById(R.id.btnFinish);
		
		etInput.setEnabled(false);
		btnFinish.setEnabled(false);
		tvResult.setText("Please get the question first ...");
		
		btnBegin.setOnClickListener(new Button.OnClickListener() {
        	@Override
			public void onClick(View v) {
        		countGame();
        		deleteTable();
        		getQuestion();
        		v.startAnimation(animAlpha);
        		etInput.setEnabled(true);
        		btnFinish.setEnabled(true);
        		btnBegin.setEnabled(false);
        		startTime = SystemClock.uptimeMillis();
        		customHandler.postDelayed(updateTimerThread, 0);
        		etInput.requestFocus();
        		showKeyboard(etInput);
			}
		});
		
		btnFinish.setOnClickListener(new Button.OnClickListener() {
        	@Override
			public void onClick(View v) {	
        		try {
        			chkInputStr = String.valueOf(etInput.getText());
        			chkInputInt = Integer.parseInt(chkInputStr);
        			if (i <= 9) {
        				questionCount++;
	    				if(Integer.parseInt(answer) == Integer.parseInt(chkInputStr)) {
	    					//Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
	    					showToast(MatchActivity.this, "Correct!", 500);
	            			correct++;
	            			userResult = "Correct";
	            			score();
	            			tvShowCorrect.setText(String.valueOf(correct));
	            			etInput.setText("");
	            			etInput.requestFocus();
	            		}else {
	            			//Toast.makeText(getApplicationContext(), "Wrong!" + " Correct answer = " + answer, Toast.LENGTH_SHORT).show();
	            			showToast(MatchActivity.this, "Wrong! Correct answer = " + answer, 500);
	            			wrong++;
	            			userResult = "Wrong";
	            			score();
	            			tvShowWrong.setText(String.valueOf(wrong));
	            			etInput.setText("");
	            			etInput.requestFocus();
	            		}
	        		
		    			if(i < 9) {
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
        		}catch(Exception e) {
        			//Toast.makeText(getApplicationContext(), "Please enter a number for answer.", Toast.LENGTH_SHORT).show();
        			showToast(MatchActivity.this, "Please enter a number for answer.", 500);
        			etInput.setText("");
        			etInput.requestFocus();
        		}
			}
		});
		
		etInput.setOnKeyListener(new EditText.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				 if (keyCode ==  KeyEvent.KEYCODE_DPAD_CENTER || keyCode ==  KeyEvent.KEYCODE_ENTER) {
					 if (event.getAction() == KeyEvent.ACTION_DOWN) {
		                  // do nothing yet
					 }else if(event.getAction() == KeyEvent.ACTION_UP) {
		            	  btnFinish.callOnClick();
					 }
					 return true;
				 }else {
					 return false;
				 }
			}
		});
	}
	
	public void showKeyboard(View v) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(etInput, InputMethodManager.SHOW_IMPLICIT);
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
		}catch (Exception e) {
			tvResult.setText("setQuestion() exception has been catched.");
		}
	}
	
	public int countGame() {
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
		return gameCount;
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
}
