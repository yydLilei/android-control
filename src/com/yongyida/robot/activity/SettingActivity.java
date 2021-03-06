package com.yongyida.robot.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.yongyida.robot.R;
import com.yongyida.robot.huanxin.DemoHXSDKHelper;
import com.yongyida.robot.utils.BroadcastReceiverRegister;
import com.yongyida.robot.utils.Constants;
import com.yongyida.robot.utils.StartUtil;
import com.yongyida.robot.utils.ThreadPool;
import com.yongyida.robot.utils.ToastUtil;
import com.yongyida.robot.widget.SwitchButton;

public class SettingActivity<AndroidLearn> extends BaseActivity implements
		View.OnClickListener {

	private TextView exit;	
	private TextView userid;
	private SwitchButton wifi;
	private Button back;
	private TextView edit;
	private EditText robotname;
	private TextView versionname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onStart() {
		BroadcastReceiverRegister.reg(this, new String[] { "flush" }, flush);
		super.onStart();
	}

	private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton button, boolean flag) {
			getSharedPreferences("setting", MODE_PRIVATE).edit()
					.putBoolean("wificheck", flag).commit();
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_exit:
			sendBroadcast(new Intent(Constants.Stop));
			ThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					DemoHXSDKHelper.getInstance().logout(false,
							new EMCallBack() {

								@Override
								public void onSuccess() {
									SharedPreferences sharedPreferences = getSharedPreferences(
											"userinfo", Activity.MODE_PRIVATE);
									SharedPreferences.Editor editor = sharedPreferences
											.edit();
									editor.clear();
									editor.commit();
									startActivity(new Intent(
											SettingActivity.this,
											LoginActivity.class)
											.setFlags(
													Intent.FLAG_ACTIVITY_NEW_TASK)
											.addFlags(
													Intent.FLAG_ACTIVITY_CLEAR_TASK));

								}

								@Override
								public void onProgress(int arg0, String arg1) {

								}

								@Override
								public void onError(int arg0, String arg1) {

								}
							});
				}
			});
			break;
		case R.id.setting_back:
			this.onBackPressed();
//			Intent intent = new Intent( this,SettingActivity.class); //***为你想要转到的界面名
//			startActivity(intent);
			break;
		case R.id.editname:
			if (edit.getText().toString().equals(getString(R.string.edit))) {
				edit.setText(R.string.complete);
				robotname.setEnabled(true);
				robotname.requestFocus();
			} else if (edit.getText().toString().equals(getString(R.string.complete))) {
				String name = robotname.getText().toString().trim();
				if (name.equals("")) {
					ToastUtil.showtomain(this, getString(R.string.name_cant_null));
					return;
				}
				getSharedPreferences("robotname", MODE_PRIVATE).edit()
						.putString("name", name).commit();
				sendBroadcast(new Intent(Constants.Robot_Info_Update).putExtra(
						"name", name));
				edit.setText(getString(R.string.edit));
				robotname.setEnabled(false);
			}

			break;
		}
	}

	BroadcastReceiver flush = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getStringExtra("result").equals("success")) {
				ToastUtil.showtomain(SettingActivity.this, getString(R.string.modify_success));
			}
		}
	};

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	protected void onStop() {
		if (flush != null)
			unregisterReceiver(flush);
		super.onStop();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void initlayout(OnRefreshListener onRefreshListener) {
		setContentView(R.layout.activity_setting);
		exit = (TextView) findViewById(R.id.setting_exit);
		exit.setOnClickListener(this);
		versionname = (TextView) findViewById(R.id.versionname);
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(getPackageName(),
					0);
			versionname.setText(info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		wifi = (SwitchButton) findViewById(R.id.wifisetting);
		wifi.setOnCheckedChangeListener(changeListener);
		userid = (TextView) findViewById(R.id.userid);
		back = (Button) findViewById(R.id.setting_back);
		back.setOnClickListener(this);
		edit = (TextView) findViewById(R.id.editname);
		edit.setOnClickListener(this);
		robotname = (EditText) findViewById(R.id.robotname);
		String username = getSharedPreferences("userinfo", MODE_PRIVATE)
				.getString("phonenumber", null);
		if (!username.equals(null)) {
			userid.setText(username);
		}
		SharedPreferences sharedPreferences = getSharedPreferences("setting", 0);

		if (sharedPreferences != null) {
			wifi.setChecked(sharedPreferences.getBoolean("wificheck", true));
		} else {
			wifi.setChecked(true);
		}
		if (getIntent().getExtras().getString("flag").equals("main")) {
			(findViewById(R.id.robot_name)).setVisibility(View.VISIBLE);
			robotname.setText(getSharedPreferences("robotname", MODE_PRIVATE)
					.getString("name", null));
		} else {
			(findViewById(R.id.robot_name)).setVisibility(View.GONE);
		}
	}

}
