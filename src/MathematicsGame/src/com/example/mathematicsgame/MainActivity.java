package com.example.mathematicsgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button btnStartGame;
	ImageView ivSound;
	SQLiteDatabase db;
	String sql;
	MediaPlayer player;
	int counter = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initialDB();
        final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
        
        player = MediaPlayer.create(MainActivity.this, R.raw.song);
        player.setLooping(true);
        player.start();
        
        btnStartGame = (Button)findViewById(R.id.btnStartGame);
        ivSound = (ImageView)findViewById(R.id.ivSound);
        ivSound.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		if(counter == 0){
        			ivSound.setImageResource(R.drawable.off);
        			player.stop();
        	    }else if(counter == 1){
        	    	ivSound.setImageResource(R.drawable.on);
        	    	player = MediaPlayer.create(MainActivity.this, R.raw.song);
        	    	player.setLooping(true);
        	    	player.start();
        	    }
        		
        	    if(counter == 0) {
        	    	counter = 1;
        	    }else if (counter == 1) {
        	    	counter = 0;
        	    }
        	}
        });
        btnStartGame.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		v.startAnimation(animTranslate);
            	Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        		startActivity(intent);
        	}
        });
    }
    
    private Dialog createExitDialog() {
		return new AlertDialog.Builder(this).setTitle(getString(R.string.exit_dialog_header))
				.setMessage(getString(R.string.exit_dialog_text))
				.setPositiveButton(getString(R.string.positive_ans), createPositiveAnswerListener())
				.setNegativeButton(getString(R.string.negative_ans), createNegativeAnswerListener()).create();
	}
    
    private android.content.DialogInterface.OnClickListener createPositiveAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		};
	}
    
    private android.content.DialogInterface.OnClickListener createNegativeAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		};
	}
    
    @Override
	public void onBackPressed() {
    	ivSound.setImageResource(R.drawable.off);
    	counter = 1;
    	player.stop();
		createExitDialog().show();
	}
    
    public void initialDB() {
		try {
			db = SQLiteDatabase.openDatabase("/data/data/com.example.mathematicsgame/QuickMathDB", null, SQLiteDatabase.CREATE_IF_NECESSARY);
			
			sql = "DROP TABLE IF EXISTS QuestionsLog;";
			db.execSQL(sql);
			sql = "DROP TABLE IF EXISTS GamesLog;";
			db.execSQL(sql);

			sql = "CREATE TABLE QuestionsLog(questionNo int PRIMARY KEY, question text, answer int, userAnswer int, result text);";
			db.execSQL(sql);
			sql = "CREATE TABLE GamesLog(gameNo int PRIMARY KEY, playDate text, playTime int, correctCount int);";
			db.execSQL(sql);
			
			db.close();
		}catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
