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

public class MenuActivity extends Activity {
	TextView tvIntro;
	Button btnStandard, btnTraining, btnScore, btnExit;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
		
		Button btnStandard = (Button)findViewById(R.id.btnStandard);
		Button btnTraining = (Button)findViewById(R.id.btnTraining);
		Button btnRecord = (Button)findViewById(R.id.btnRecord);
		Button btnExit = (Button)findViewById(R.id.btnExit);
		
		btnStandard.setOnClickListener(new Button.OnClickListener() {
			@Override
        	public void onClick(View v) {
        		v.startAnimation(animAlpha);
        		Intent matchActivity = new Intent(MenuActivity.this, MatchActivity.class);
        		startActivity(matchActivity);
        	}
        });
		
		btnTraining.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		v.startAnimation(animAlpha);
        		Intent gameActivity = new Intent(MenuActivity.this, GameActivity.class);
        		startActivity(gameActivity);
        	}
        });
		
		btnRecord.setOnClickListener(new Button.OnClickListener() {
			@Override
        	public void onClick(View v) {
				v.startAnimation(animAlpha);
				Intent recordActivity = new Intent(MenuActivity.this, RecordActivity.class);
        		startActivity(recordActivity);
        	}
        });
		
		btnExit.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		v.startAnimation(animAlpha);
        		exitGameDialog().show();
        	}
        });
	}
	
	private Dialog exitGameDialog() {
		return new AlertDialog.Builder(this).setTitle(getString(R.string.exit_dialog_header))
				.setMessage(getString(R.string.exit_dialog_text))
				.setPositiveButton(getString(R.string.positive_ans), positiveAnswerListener())
				.setNegativeButton(getString(R.string.negative_ans), negativeAnswerListener()).create();
	}
	
	private android.content.DialogInterface.OnClickListener positiveAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(mainActivity);
				MenuActivity.this.finish();
				
			    Intent intent = new Intent(Intent.ACTION_MAIN);
			    intent.addCategory(Intent.CATEGORY_HOME);
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intent);
			}
		};
	}
	
	private android.content.DialogInterface.OnClickListener negativeAnswerListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		};
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}