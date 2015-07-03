package com.willard.picturesplayer.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.willard.photosplayer.R;
import com.willard.picturesplayer.PicturesPlayer;

public class PicturesPlayerDemoActivity extends Activity {
	private Button btnNext,btnPause,btnGoOn;
	private PicturesPlayer myPicturesPlayer;
	private int[] imagesResIds;
	// 放轮播图片的ImageView 的list
	private List<ImageView> imageViewsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_picturesplayer);
		onInitView();
	}

	private void onInitView() {
		myPicturesPlayer = (PicturesPlayer) findViewById(R.id.myPicturesPlayer);
		btnNext = (Button) findViewById(R.id.btn1);
		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPicturesPlayer.nextPage();
			}
		});
		btnPause = (Button) findViewById(R.id.btn2);
		btnPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPicturesPlayer.pausePlay();
			}
		});
		btnGoOn = (Button) findViewById(R.id.btn3);
		btnGoOn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myPicturesPlayer.goOnPlay();
			}
		});
		imagesResIds = new int[] { R.drawable.a, R.drawable.b, R.drawable.c,
				R.drawable.d, R.drawable.e,

		};
		imageViewsList = new ArrayList<ImageView>();
		for (int imageID : imagesResIds) {
			ImageView view = new ImageView(this);
			view.setImageResource(imageID);
			view.setScaleType(ScaleType.FIT_XY);
			imageViewsList.add(view);
		}
		myPicturesPlayer.setNeedRepeat(true);// normal
		myPicturesPlayer.setCanScroll(true);// normal
		// image.setmDuration(2000);//normal
		myPicturesPlayer.startPlayer(imageViewsList);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		myPicturesPlayer.closePlayer();
		super.onDestroy();
	}
	
	
}
