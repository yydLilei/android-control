package com.yongyida.robot.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.yongyida.robot.R;
import com.yongyida.robot.utils.Constants;
import com.yongyida.robot.utils.StartUtil;
import com.yongyida.robot.utils.ToastUtil;

public class PowerListActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout video_chat;
	private RelativeLayout video_monitor;
	private RelativeLayout power_task;
	private RelativeLayout power_photo;
	private RelativeLayout power_setting;
	private RelativeLayout more;
	private TextView power_title;
	private String mMode;
	private String mType; //机型 eg: Y50  Y20
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mType = getIntent().getStringExtra("type");
		setContentView(R.layout.activity_power_list);
		initBase();
	}


	private void initBase() {
		more = (RelativeLayout) findViewById(R.id.more);
		more.setOnClickListener(this);
		more.setOnTouchListener(ontouch);
		power_title = (TextView) findViewById(R.id.power_title);
		power_title.setOnClickListener(this);
		power_setting = (RelativeLayout) findViewById(R.id.power_setting);
		power_setting.setOnClickListener(this);
		power_setting.setOnTouchListener(ontouch);
		power_photo = (RelativeLayout) findViewById(R.id.power_photo);
		power_photo.setOnTouchListener(ontouch);
		power_photo.setOnClickListener(this);
		video_monitor = (RelativeLayout) findViewById(R.id.video_monitor);
		video_monitor.setOnClickListener(this);
		video_monitor.setOnTouchListener(ontouch);
		video_chat = (RelativeLayout) findViewById(R.id.video_chat);
		video_chat.setOnClickListener(this);
		video_chat.setOnTouchListener(ontouch);
		power_task = (RelativeLayout) findViewById(R.id.power_task);
		power_task.setOnClickListener(this);
		power_task.setOnTouchListener(ontouch);
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	public void initlayout(OnRefreshListener onRefreshListener) {
	}

	long now = 0;
	long secc = 0;
	boolean flag = true;

	public void downanimaction(View view) {
		view.setPivotX(0);
		view.setPivotY(0);
		view.invalidate();
		ObjectAnimator.ofFloat(view, "rotationX", 0.0f, -30.0f)
				.setDuration(500).start();
	}

	private OnTouchListener ontouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Drawable background = v.getBackground();
				v.setBackgroundColor(Color.DKGRAY);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundColor(Color.TRANSPARENT);
			}
			return false;
		}
	};
	int width;
	int height;

	public void sendmsg(String mode, String touser) {
		EMMessage msg = EMMessage.createSendMessage(Type.CMD);
		msg.setReceipt(touser);
		msg.setAttribute("mode", mode);
		CmdMessageBody cmd = new CmdMessageBody(Constants.Video_Mode);
		msg.addBody(cmd);
		if (EMChatManager.getInstance().isConnected()) {
			EMChatManager.getInstance().sendMessage(msg, mCallBack);
		} else {
			Log.e("PowerListActivity","连接失败");
			ToastUtil.showtomain(PowerListActivity.this, getString(R.string.initialize_fail));
			EMChatManager.getInstance().login(
					getSharedPreferences("huanxin", MODE_PRIVATE)
							.getString("username", null),
					getSharedPreferences("huanxin", MODE_PRIVATE)
							.getString("password", null),
					new EMCallBack() {

						@Override
						public void onSuccess() {
						}

						@Override
						public void onProgress(int arg0, String arg1) {
						}

						@Override
						public void onError(int arg0, String arg1) {
						}
					});
		}
	}

	private EMCallBack mCallBack = new EMCallBack() {
		
		@Override
		public void onSuccess() {

			Bundle params = new Bundle();
			params.putString("mode", mMode);
			StartUtil.startintent(PowerListActivity.this, ControlActivity.class, "no", params);
		}
		
		@Override
		public void onProgress(int arg0, String arg1) {
		}
		
		@Override
		public void onError(int arg0, String arg1) {
			Log.e("PowerListActivity","error:" + arg1);
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					ToastUtil.showtomain(PowerListActivity.this, getString(R.string.initialize_fail));
				}
			});
		}
	};
	
	@Override
	public void onClick(View v) {
		Bundle params = new Bundle();

		switch (v.getId()) {
		case R.id.more:
			ToastUtil.showtomain(this, getString(R.string.waitting));
			break;
		case R.id.power_title:
			onBackPressed();
			break;
		case R.id.video_chat:
			if ("Y20".equals(mType)) {

			} else {
				mMode = "chat";
				sendmsg(mMode,getSharedPreferences("Receipt", MODE_PRIVATE).getString(
						"username", null));
				if (!"Y50".equals(mType)){
					Log.e("PowerListActivity","video_chat");
				}
			}
			break;
		case R.id.video_monitor:
			if ("Y20".equals(mType)) {

			} else {
				mMode = "control";
				sendmsg(mMode, getSharedPreferences("Receipt", MODE_PRIVATE).getString(
						"username", null));
				if (!"Y50".equals(mType)){
					Log.e("PowerListActivity","video_monitor");
				}
			}
			break;
		case R.id.power_photo:
		 	StartUtil.startintent(this, PhotoActivity.class, "no");
		 	break;
		case R.id.power_setting:
			params.putString("flag", "main");
			StartUtil.startintent(this, SettingActivity.class, "no", params);
			break;
		case R.id.power_task:
			StartUtil.startintent(this, TaskRemindActivity.class, "no");
			break;
		// case R.id.power_chat:
		// params.putString(
		// "username",
		// getSharedPreferences("Receipt", MODE_PRIVATE).getString(
		// "username", null));
		// params.putInt("chatType", 1);
		// StartUtil.startintent(this, ChatActivity.class, "no", params);
		// break;
		default:
			break;
		}
	}
	

	@Override
	public void onBackPressed() {
		sendBroadcast(new Intent(Constants.Stop));
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
