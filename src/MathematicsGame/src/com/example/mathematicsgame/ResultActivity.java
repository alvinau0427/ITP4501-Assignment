package com.example.mathematicsgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity {
	TextView tvCountTime, tvShowCorrect, tvShowWrong;
	Button btnScore, btnRecord, btnMenu, btnExit;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		final Animation animTranslate = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
		
		tvCountTime = (TextView)findViewById(R.id.tvCountTime);
		tvShowCorrect = (TextView)findViewById(R.id.tvShowCorrect);
		tvShowWrong = (TextView)findViewById(R.id.tvShowWrong);
		btnScore  = (Button)findViewById(R.id.btnScore);
		btnRecord = (Button)findViewById(R.id.btnRecord);
		btnMenu  = (Button)findViewById(R.id.btnMenu);
		btnExit = (Button)findViewById(R.id.btnExit);
		
		Intent intent = getIntent();
		tvCountTime.setText(intent.getStringExtra("time"));
		tvShowCorrect.setText(intent.getStringExtra("correct"));
		tvShowWrong.setText(intent.getStringExtra("wrong"));
		
		btnScore.setOnClickListener(new Button.OnClickListener() {
			@Override
        	public void onClick(View v) {
        		v.startAnimation(animTranslate);
        		scoreDialog().show();
        	}
        });
		
		btnRecord.setOnClickListener(new Button.OnClickListener() {
			@Override
        	public void onClick(View v) {
        		v.startAnimation(animTranslate);
        		recordDialog().show();
        	}
        });
		
		btnMenu.setOnClickListener(new Button.OnClickListener() {
			@Override
        	public void onClick(View v) {
        		v.startAnimation(animTranslate);
        		menuDialog().show();
        	}
        });
		
		btnExit.setOnClickListener(new Button.OnClickListener() {
			@Override
        	public void onClick(View v) {
        		v.startAnimation(animTranslate);
        		exitGameDialog().show();
        	}
        });
	}
	
	// Score Dialog
	private Dialog scoreDialog() {
		return new AlertDialog.Builder(this).setTitle(getString(R.string.exit_dialog_header))
				.setMessage(getString(R.string.score_dialog_text))
				.setPositiveButton(getString(R.string.positive_ans), scorePositiveAnswerListener())
				.setNegativeButton(getString(R.string.negative_ans), scoreNegativeAnswerListener()).create();
	}
	
	private android.content.DialogInterface.OnClickListener scorePositiveAnswerListener() {
			return new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent scoreActivity = new Intent(getApplicationContext(), ScoreActivity.class);
	        		startActivity(scoreActivity);
				}
			};
	}
	
	private android.content.DialogInterface.OnClickListener scoreNegativeAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		};
	}
	
	// Restart Dialog
	private Dialog recordDialog() {
		return new AlertDialog.Builder(this).setTitle(getString(R.string.exit_dialog_header))
				.setMessage(getString(R.string.record_dialog_text))
				.setPositiveButton(getString(R.string.positive_ans), recordPositiveAnswerListener())
				.setNegativeButton(getString(R.string.negative_ans), recordNegativeAnswerListener()).create();
	}
	
	private android.content.DialogInterface.OnClickListener recordPositiveAnswerListener() {
			return new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent recordActivity = new Intent(getApplicationContext(), RecordActivity.class);
	        		startActivity(recordActivity);
				}
			};
	}
	
	private android.content.DialogInterface.OnClickListener recordNegativeAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		};
	}
	
	// Menu Dialog
	private Dialog menuDialog() {
		return new AlertDialog.Builder(this).setTitle(getString(R.string.exit_dialog_header))
				.setMessage(getString(R.string.menu_dialog_text))
				.setPositiveButton(getString(R.string.positive_ans), menuPositiveAnswerListener())
				.setNegativeButton(getString(R.string.negative_ans), menuNegativeAnswerListener()).create();
	}
	
	private android.content.DialogInterface.OnClickListener menuPositiveAnswerListener() {
			return new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent menuActivity = new Intent(getApplicationContext(), MenuActivity.class);
	        		startActivity(menuActivity);
			}
		};
	}
	    
	private android.content.DialogInterface.OnClickListener menuNegativeAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		};
	}
	
	// Exit Dialog
	private Dialog exitGameDialog() {
		return new AlertDialog.Builder(this).setTitle(getString(R.string.exit_dialog_header))
				.setMessage(getString(R.string.exit_dialog_text))
				.setPositiveButton(getString(R.string.positive_ans), exitPositiveAnswerListener())
				.setNegativeButton(getString(R.string.negative_ans), exitNegativeAnswerListener()).create();
	}
	
	private android.content.DialogInterface.OnClickListener exitPositiveAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(mainActivity);
				ResultActivity.this.finish();
				
			    Intent intent = new Intent(Intent.ACTION_MAIN);
			    intent.addCategory(Intent.CATEGORY_HOME);
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intent);
			}
		};
	}
	
	// Create Exit Dialog
	private android.content.DialogInterface.OnClickListener exitNegativeAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		};
	}
	
	@Override
	public void onBackPressed() {
		menuDialog().show();
	}
}
