package com.yongyida.robot.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.yongyida.robot.R;
import com.yongyida.robot.bean.Alarm;
import com.yongyida.robot.bean.Remind;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class NetUtil {

	private static final String TAG = "NetUtil";

	public NetUtil() {
		super();
		httputil = this;
	}

	private static NetUtil httputil;

	// 工厂方法
	public static NetUtil getinstance() {

		return httputil;
	}



	// http请求方法
	public boolean http(String url, Map<String, String> params, callback call,
			Context context) throws SocketTimeoutException {
		if (Constants.address == null) {
			String serverState = context.getSharedPreferences("net_state", context.MODE_PRIVATE).getString("state",null);
			if (serverState != null && !serverState.equals("official")){
				Utils.switchServer(Utils.TEST);
			} else {
				Utils.switchServer(Utils.CN);
			}
		}
		String address = Constants.address;
		try {
			Http http = new Http(address + url);
			http.setRequestProperty("Content-Type", "text/plain;charset=utf-8;");
			http.setCharset("utf-8");
			http.setReadTimeout(Constants.timeout);
			JSONObject json = new JSONObject(params);
			String result = http.post(json.toString());
			if (result.equals("404")) {
				call.error(context.getString(R.string.server_maintain));
				return false;
			} else {
				call.success(new JSONObject(result));
				return true;
			}

		} catch (Exception e) {
			if (isConnect(context)) {
				Log.e(TAG, "Exception:" + e.getMessage());
				call.error(context.getString(R.string.net_connection_timeout));
			} else {
				call.error(context.getString(R.string.net_connection_invisible));
			}
			e.printStackTrace();
			return false;
		}

	}

	public InputStream downloadfile(Context context, String url, callback call) {
		URL uri = null;
		InputStream inputstream = null;
		try {
			uri = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) uri
					.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(8000);
			inputstream = connection.getInputStream();

		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				call.error(context.getString(R.string.net_connection_timeout));
			}
			if (isConnect(context)) {
				call.error(context.getString(R.string.connection_fail));
			} else {
				call.error(context.getString(R.string.net_connection_invisible));
			}
		}
		return inputstream;
	}

	public static boolean isConnect(Context context) {

		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {

			ConnectivityManager connectivity = (ConnectivityManager) context

			.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (connectivity != null) {

				// 获取网络连接管理的对象

				NetworkInfo info = connectivity.getActiveNetworkInfo();

				if (info != null && info.isConnected()) {

					// 判断当前网络是否已经连接

					if (info.getState() == NetworkInfo.State.CONNECTED) {

						return true;

					}

				}

			}

		} catch (Exception e) {

			Log.v("error", e.toString());

		}

		return false;

	}

	/**
	 * 机器人转发视频数据
	 * @param command   转发的数据
	 * @param handler
	 */
	public static void socketY20MediaPush(String command,
										  ChannelHandlerContext handler){
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("command", command);
			socketY20(jsonObject, Command.MEDIA_PUSH, handler);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * socket视频请求
	 * @param id     数字id
	 * @param role   角色
	 *                 Robot
	 *                 User
	 * @param nickname 昵称
	 * @param pic   图片url
	 * @param type      Robot 小勇号
	 *                  Room房间号
	 *                  Phone 手机号
	 * @param number  请求拨号
	 * @param handler
	 */

	/**
	 * socket视频请求
	 * @param id   数字id
	 * @param role    角色
	 *                 Robot
	 *                 User
	 * @param nickname  昵称
	 * @param pic       图片url
	 * @param type      Robot 小勇号
	 *                  Room房间号
	 *                  Phone 手机号
	 * @param number    请求拨号
	 * @param videowidth
	 * @param videoheight
	 * @param framerate
	 * @param bitrate
	 * @param samplerate
	 * @param channel
	 * @param audioformat
	 * @param handler
	 */
	public static void socketY20MediaInvite(long id, String role, String nickname,
											String pic, String type, long number,
											int videowidth, int videoheight, int framerate, int bitrate,
											int samplerate, int channel, int audioformat,
											ChannelHandlerContext handler){
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", id);
			jsonObject.put("role", role);
			jsonObject.put("nickname", nickname);
			jsonObject.put("pic", pic);
			jsonObject.put("type", type);
			jsonObject.put("number", number);
			jsonObject.put("videowidth", videowidth);
			jsonObject.put("videoheight", videoheight);
			jsonObject.put("framerate", framerate);
			jsonObject.put("bitrate", bitrate);
			jsonObject.put("samplerate", samplerate);
			jsonObject.put("channel", channel);
			jsonObject.put("audioformat", audioformat);
			socketY20(jsonObject, Command.MEDIA_REQUEST, handler);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * socket 视频请求取消,取消当前拨号
	 * @param id   数字id
	 * @param role  角色
	 * @param handler
	 */
	public static void socketY20MediaCancel(long id, String role,
											 ChannelHandlerContext handler){
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", id);
			jsonObject.put("role", role);
			socketY20(jsonObject, Command.MEDIA_CANCEL, handler);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * socket 视频应答, 接通或挂断视频请求
	 * @param id     数字id
	 * @param role   角色
	 * @param invite_type  邀请人的类型
								Robot  机器人
								User   手机用户
	 * @param invite_id   邀请人的id
	 * @param reply     0  挂断
						1  接受音&视频
						2  接受音频
						3  接受视频
	 *
	 * @param handler
	 */
	public static void socketY20MediaReply(long id, String role, String invite_type,
											String invite_id, int reply,
											ChannelHandlerContext handler){
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", id);
			jsonObject.put("role", role);
			jsonObject.put("invite_type", invite_type);
			jsonObject.put("invite_id", invite_id);
			jsonObject.put("reply", reply);
			socketY20(jsonObject, Command.MEDIA_REPLY, handler);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * socket 登入视频房间
	 * @param id  数字id
	 * @param role   用户标识
						手机    User
						机器人  Robot
	 * @param room_id  房间id
	 *                 * @param videowidth
	 * @param videoheight
	 * @param framerate
	 * @param bitrate
	 * @param samplerate
	 * @param channel
	 * @param audioformat
	 * @param handler
	 */
	public static void socketY20MediaLogin(String id, String role, int room_id,
										   int videowidth, int videoheight, int framerate, int bitrate,
										   int samplerate, int channel, int audioformat,
										   ChannelHandlerContext handler){
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", id);
			jsonObject.put("role", role);
			jsonObject.put("room_id", room_id);
			jsonObject.put("videowidth", videowidth);
			jsonObject.put("videoheight", videoheight);
			jsonObject.put("framerate", framerate);
			jsonObject.put("bitrate", bitrate);
			jsonObject.put("samplerate", samplerate);
			jsonObject.put("channel", channel);
			jsonObject.put("audioformat", audioformat);
			socketY20(jsonObject, Command.MEDIA_LOGIN, handler);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * socket 登出视频房间
	 * @param id  数字id
	 * @param role   用户标识
						手机    User
						机器人  Robot
	 * @param room_id  房间id
	 * @param handler
	 */
	public static void socketY20MediaLogout(String id, String role, int room_id,
										   ChannelHandlerContext handler){
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", id);
			jsonObject.put("role", role);
			jsonObject.put("room_id", room_id);
			socketY20(jsonObject, Command.MEDIA_LOGOUT, handler);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public enum Command{
		MEDIA_REQUEST,
		MEDIA_CANCEL,
		MEDIA_PUSH,
		MEDIA_REPLY,
		MEDIA_LOGIN,
		MEDIA_LOGOUT
	}

	/**
	 * y20 socket连接
	 * @param params
	 * @param cmd
	 * @param handler
	 */
	public static void socketY20(JSONObject params, Command cmd, ChannelHandlerContext handler){
		ChannelBuffer channelBuffer = null;
		String str = null;
		switch (cmd) {
			case MEDIA_REQUEST:
				try {
					str = "{\"id\":\""+ params.getLong("id")
							+ "\",\"role\":\"" + params.getString("role")
							+ "\",\"nickname\":\"" + params.getString("nickname")
							+ "\",\"pic\":\"" + params.getString("pic")
							+ "\",\"type\":\"" + params.getString("type")
							+ "\",\"number\":\"" + params.getLong("number")
							+ "\",\"videowidth\":\"" + params.getInt("videowidth")
							+ "\",\"videoheight\":\"" + params.getInt("videoheight")
							+ "\",\"framerate\":\"" + params.getInt("framerate")
							+ "\",\"bitrate\":\"" + params.getInt("bitrate")
							+ "\",\"samplerate\":\"" + params.getInt("samplerate")
							+ "\",\"channel\":\"" + params.getInt("channel")
							+ "\",\"audioformat\":\"" + params.getInt("audioformat")
							+ "\",\"cmd\":\"/media/invite\"}";
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			case MEDIA_CANCEL:
				try {
					str = "{\"id\":\""+ params.getLong("id")
							+ "\",\"role\":\"" + params.getString("role")
							+ "\",\"cmd\":\"/media/cancel\"}";
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case MEDIA_PUSH:
				try {
					str = "{\"command\":\""+ params.getString("command")
							+ "\",\"cmd\":\"/media/push\"}";
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case MEDIA_REPLY:
				try {
					str = "{\"id\":\""+ params.getLong("id")
							+ "\",\"role\":\"" + params.getString("role")
							+ "\",\"invite_type\":\"" + params.getString("invite_type")
							+ "\",\"invite_id\":\"" + params.getString("invite_id")
							+ "\",\"reply\":\"" + params.getInt("reply")
							+ "\",\"cmd\":\"/media/reply\"}";
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case MEDIA_LOGIN:
				try {
					str = "{\"id\":\""+ params.getString("id")
							+ "\",\"room_id\":\"" + params.getInt("room_id")
							+ "\",\"role\":\"" + params.getString("role")
							+ "\",\"videowidth\":\"" + params.getInt("videowidth")
							+ "\",\"videoheight\":\"" + params.getInt("videoheight")
							+ "\",\"framerate\":\"" + params.getInt("framerate")
							+ "\",\"bitrate\":\"" + params.getInt("bitrate")
							+ "\",\"samplerate\":\"" + params.getInt("samplerate")
							+ "\",\"channel\":\"" + params.getInt("channel")
							+ "\",\"audioformat\":\"" + params.getInt("audioformat")
							+ "\",\"cmd\":\"/media/room/login\"}";
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case MEDIA_LOGOUT:
				try {
					str = "{\"id\":\""+ params.getString("id")
							+ "\",\"room_id\":\"" + params.getInt("room_id")
							+ "\",\"role\":\"" + params.getString("role")
							+ "\",\"cmd\":\"/media/room/logout\"}";
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
		channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
				12 + str.getBytes().length);
		channelBuffer.writeByte((byte) 1);
		for (int i = 0; i < 7; i++) {
			channelBuffer.writeByte((byte) 0);
		}
		channelBuffer.writeBytes(int2Byte(str.length()));
		channelBuffer.writeBytes(str.getBytes());
		Channel channel = handler.getChannel();
		channel.write(channelBuffer);
	}

	/**
	 * 登出socket
	 * @param id
	 * @param session
	 * @param handler
	 */
	public static void logoutSocket(int id, String session, ChannelHandlerContext handler){
		String str = "{\"id\":\""     + id
				+ "\",\"session\":\"" + session
				+ "\",\"cmd\":\"/user/logout\"}";
		ChannelBuffer channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
				12 + str.getBytes().length);
		channelBuffer.writeByte((byte) 1);
		for (int i = 0; i < 7; i++) {
			channelBuffer.writeByte((byte) 0);
		}
		channelBuffer.writeBytes(int2Byte(str.length()));
		channelBuffer.writeBytes(str.getBytes());
		Channel channel = handler.getChannel();
		channel.write(channelBuffer);
	}

	/**
	 * 登录socket
	 * @param id
	 * @param session
	 */
	public static void loginSocket(int id, String session, ChannelHandlerContext handler){
		String str = "{\"id\":\""     + id
				+ "\",\"session\":\"" + session
				+ "\",\"cmd\":\"/user/login\"}";
		ChannelBuffer channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
				12 + str.getBytes().length);
		channelBuffer.writeByte((byte) 1);
		for (int i = 0; i < 7; i++) {
			channelBuffer.writeByte((byte) 0);
		}
		channelBuffer.writeBytes(int2Byte(str.length()));
		channelBuffer.writeBytes(str.getBytes());
		Channel channel = handler.getChannel();
		channel.write(channelBuffer);
	}

	/**
	 * FOTA升级
	 * @param robotVersion
	 * @param newVersion
	 * @param handler
	 */
	public static void fotaUpdate(String robotVersion, String newVersion, ChannelHandlerContext handler) {
		String str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"updateSystem\",\"robotVersion\":\""
				+ robotVersion
				+ "\",\"newVersion\":\""
				+ newVersion
				+ "\"}}";
		ChannelBuffer channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
				12 + str.getBytes().length);
		channelBuffer.writeByte((byte) 1);
		for (int i = 0; i < 7; i++) {
			channelBuffer.writeByte((byte) 0);
		}
		channelBuffer.writeBytes(int2Byte(str.length()));
		channelBuffer.writeBytes(str.getBytes());
		Channel channel = handler.getChannel();
		channel.write(channelBuffer);
	}

	// socket方法
	public static void Scoket(JSONObject params, int flag, ChannelHandlerContext handler) {
		if (params != null) {
			Log.i("NetUtil", params.toString());
		}
		ChannelBuffer channelBuffer = null;
		String str = null;
		switch (flag) {
		case 0:
			try {
				str = "{\"rid\":\"" + params.getString("rid") + "\",\"id\":\""
						+ params.getInt("id") + "\",\"session\":\""
						+ params.getString("session")
						+ "\",\"cmd\":\"/robot/controll\"}";
			} catch (JSONException e) {
				e.printStackTrace();
			}
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
					12 + str.getBytes().length);
			channelBuffer.writeByte((byte) 1);
			for (int i = 0; i < 7; i++) {
				channelBuffer.writeByte((byte) 0);
			}
			channelBuffer.writeBytes(int2Byte(str.length()));
			channelBuffer.writeBytes(str.getBytes());

			break;
		case 1:
			str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"move\",\"type\":\""
					+ Constants.execode + "\"}}";
//			if (Constants.execode.equals("forward")) {
//				str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"204\",\"content\":{\"ms\":\"10\",\"ma\":\"0\",\"ro\":\"0\"}}}";
//			} else if (Constants.execode.equals("turn_left")) {
//                str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"204\",\"content\":{\"ms\":\"10\",\"ma\":\"270\",\"ro\":\"270\"}}}";
//			} else if (Constants.execode.equals("back")) {
//                str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"204\",\"content\":{\"ms\":\"10\",\"ma\":\"180\",\"ro\":\"180\"}}}";
//			} else if (Constants.execode.equals("turn_right")) {
//                str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"204\",\"content\":{\"ms\":\"10\",\"ma\":\"90\",\"ro\":\"90\"}}}";
//			} else if (Constants.execode.equals("stop")) {
//                str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"204\",\"content\":{\"ms\":\"0\",\"ma\":\"0\",\"ro\":\"0\"}}}";
//			//	str = "{\"cmd\":\"204\",\"content\":{\"ms\":\"0\",\"ma\":\"0\",\"ro\":\"0\"}}";
//			} else {
//				str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"move\",\"type\":\""
//					+ Constants.execode + "\"}}";
//			}
			Log.i("JsonString", str);
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
					(12 + str.getBytes().length));
			channelBuffer.writeByte((byte) 1);
			for (int i = 0; i < 7; i++) {
				channelBuffer.writeByte((byte) 0);
			}
			try {
				channelBuffer
						.writeBytes(int2Byte(str.getBytes("utf-8").length));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			channelBuffer.writeBytes(str.getBytes());
			break;
		case 2:
			str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"text\",\"type\":\""
					+ Constants.text + "\"}}";
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
					(12 + str.getBytes().length));
			channelBuffer.writeByte((byte) 1);
			for (int i = 0; i < 7; i++) {
				channelBuffer.writeByte((byte) 0);
			}
			try {
				channelBuffer
						.writeBytes(int2Byte(str.getBytes("utf-8").length));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			channelBuffer.writeBytes(str.getBytes());

			break;
		case 3:
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 8);
			for (int i = 0; i < 8; i++) {
				channelBuffer.writeByte(0);
			}
			Log.i("Heart", System.currentTimeMillis() + "");
			break;
		case 4:
			try {
				str = "{\"rid\":\"" + params.getString("rid") + "\",\"id\":\""
						+ params.getInt("id") + "\",\"session\":\""
						+ params.getString("session")
						+ "\",\"cmd\":\"/robot/uncontroll\"}";
			} catch (JSONException e) {
				e.printStackTrace();
			}
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
					12 + str.getBytes().length);
			channelBuffer.writeByte((byte) 1);
			for (int i = 0; i < 7; i++) {
				channelBuffer.writeByte((byte) 0);
			}
			channelBuffer.writeBytes(int2Byte(str.length()));
			channelBuffer.writeBytes(str.getBytes());

			break;
		case 5:
			str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"talk\",\"type\":\""
					+ Constants.text + "\"}}";
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
					(12 + str.getBytes().length));
			channelBuffer.writeByte((byte) 1);
			for (int i = 0; i < 7; i++) {
				channelBuffer.writeByte((byte) 0);
			}
			try {
				channelBuffer
						.writeBytes(int2Byte(str.getBytes("utf-8").length));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			channelBuffer.writeBytes(str.getBytes());
			break;
		case 6:
			str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"question\"}}";
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
					(12 + str.getBytes().length));
			channelBuffer.writeByte((byte) 1);
			for (int i = 0; i < 7; i++) {
				channelBuffer.writeByte((byte) 0);
			}
			try {
				channelBuffer
						.writeBytes(int2Byte(str.getBytes("utf-8").length));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			channelBuffer.writeBytes(str.getBytes());
			break;
		case 7:
			try {
				String s = "您儿子有消息了，请回应";
				str = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"emergency\",\"type\":\""
						+ s + "\", \"angel\":\"0\",\"username\":\"" + params.getString("username") + "\"}}";
			} catch (JSONException e) {
				e.printStackTrace();
			}

			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN,
					(12 + str.getBytes().length));
			channelBuffer.writeByte((byte) 1);
			for (int i = 0; i < 7; i++) {
				channelBuffer.writeByte((byte) 0);
			}
			try {
				channelBuffer
						.writeBytes(int2Byte(str.getBytes("utf-8").length));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			channelBuffer.writeBytes(str.getBytes());
			break;
		default:
			break;
		}

		Channel channel = handler.getChannel();
		channel.write(channelBuffer);

	}

	// 任务提醒 socket
	public static void socket(ChannelHandlerContext handler, Intent intent) {
		ChannelBuffer channelBuffer = null;
		String request_content = null;
		if (intent.getAction().equals(Constants.Task_Remove)) {
			request_content = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"remind_delete\",\"id\":\""
					+ Constants.task.getId()
					+ "\",\"time\":\"\",\"title\":\"\",\"content\":\"\",\"seq\":\"\"}}";
		} else if (intent.getAction().equals(Constants.Task_Add)) {
			if (Constants.task instanceof Alarm) {
				Alarm alarm = (Alarm) Constants.task;
				boolean fl = true;
				if (alarm.getIsaways() == 1) {
					fl = true;
				} else {
					fl = false;
				}
				request_content = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"remind_insert\",\"id\":\""
						+ alarm.getId()
						+ "\",\"time\":\""
						+ System.currentTimeMillis()
						+ "\",\"title\":\""
						+ alarm.getTitle()
						+ "\",\"content\":\""
						+ alarm.getContent()
						+ "\",\"seq\":\""
						+ alarm.getSettime()
						+ ":00"
						+ "<day>"
						+ alarm.getWeek()
						+ "</day><repeat>"
						+ fl
						+ "</repeat>\"}}";
			} else if (Constants.task instanceof Remind) {
				Remind remind = (Remind) Constants.task;
				String[] time = remind.getSettime().split(" ");
				String[] ti = time[1].split(":");
				if (Integer.parseInt(ti[0]) < 10) {
					time[1] = "0" + time[1];
				}
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = null;
				try {
					date = simpleDateFormat.parse(remind.getSettime());
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				request_content = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"remind_insert\",\"id\":\""
						+ remind.getId()
						+ "\",\"time\":\""
						+ date.getTime()
						+ "\",\"title\":\""
						+ remind.getTitle()
						+ "\",\"content\":\""
						+ remind.getContent()
						+ "\",\"seq\":\"0\"}}";
			}
		} else if (intent.getAction().equals(Constants.Task_Query)) {
			request_content = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"remind_query\",\"id\":\"\",\"time\":\"\",\"title\":\"\",\"content\":\"\",\"seq\":\"\"}}";
		} else if (intent.getAction().equals(Constants.Task_Updata)) {
			if (Constants.task instanceof Alarm) {
				Alarm alarm = (Alarm) Constants.task;
				boolean fl = true;
				if (alarm.getIsaways() == 1) {
					fl = true;
				} else {
					fl = false;
				}
				request_content = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"remind_updata\",\"id\":\""
						+ alarm.getId()
						+ "\",\"time\":\""
						+ System.currentTimeMillis()
						+ "\",\"title\":\""
						+ alarm.getTitle()
						+ "\",\"content\":\""
						+ alarm.getContent()
						+ "\",\"seq\":\""
						+ alarm.getSettime()
						+ ":00"
						+ "<day>"
						+ alarm.getWeek()
						+ "</day><repeat>"
						+ fl
						+ "</repeat>\"}}";
			} else if (Constants.task instanceof Remind) {
				Remind remind = (Remind) Constants.task;
				String[] time = remind.getSettime().split(" ");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = null;
				try {
					date = simpleDateFormat.parse(remind.getSettime());
				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				request_content = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"remind_updata\",\"id\":\""
						+ remind.getId()
						+ "\",\"time\":\""
						+ date.getTime()
						+ "\",\"title\":\""
						+ remind.getTitle()
						+ "\",\"content\":\""
						+ remind.getContent()
						+ "\",\"seq\":\"0\"}}";
			}
		}

		Log.i("request", request_content);
		try {
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 1024);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		channelBuffer.writeByte((byte) 1);
		for (int i = 0; i < 7; i++) {
			channelBuffer.writeByte((byte) 0);
		}
		try {
			channelBuffer
					.writeBytes(int2Byte(request_content.getBytes("utf8").length));
			channelBuffer.writeBytes(request_content.getBytes("utf8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Channel channel = handler.getChannel();
		channel.write(channelBuffer);
	}

	// 照片socket
	public static void photo_socket(ChannelHandlerContext handler,
			Intent intent) {
		ChannelBuffer channelBuffer = null;
		String photo_operation = "";
		if (intent.getAction().equals(Constants.Photo_Query_Name)) {
			photo_operation = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"photo_query\",\"type\":\"photo_query_list\"}}";
		} else if (intent.getAction().equals(Constants.Photo_Delete)) {
			String[] names = intent.getStringArrayExtra("delete_names");
			String name = "[";
			for (int i = 0; i < names.length; i++) {
				name += "{\"name\":\"" + names[i] + "\"},";
			}
			name = name.substring(0, name.length() - 1);
			name += "]";
			photo_operation = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"photo_query\",\"type\":\"photo_delete\",\"names\":"
					+ name + "}}";
		} else if (intent.getAction().equals(Constants.Photo_Query)) {
			photo_operation = "{\"cmd\":\"/robot/push\",\"command\":{\"cmd\":\"photo_query\",\"type\":\"photo_query_original\",\"name\":\""
					+ intent.getStringExtra("name") + "\"}}";
		}
		Log.i("request", photo_operation);
		try {
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 1024);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		channelBuffer.writeByte((byte) 1);
		for (int i = 0; i < 7; i++) {
			channelBuffer.writeByte((byte) 0);
		}
		try {
			channelBuffer
					.writeBytes(int2Byte(photo_operation.getBytes("utf8").length));
			channelBuffer.writeBytes(photo_operation.getBytes("utf8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Channel channel = handler.getChannel();
		channel.write(channelBuffer);
	};

	public static void robotinfoupdate(ChannelHandlerContext handler, Intent intent) {
		ChannelBuffer channelBuffer = null;
		String robotinfo_operation = "{\"cmd\":\"/robot/flush\",\"rname\":\""
				+ intent.getStringExtra("name") + "\"}";

		Log.i("request", robotinfo_operation);
		try {
			channelBuffer = new DynamicChannelBuffer(ByteOrder.BIG_ENDIAN, 1024);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		channelBuffer.writeByte((byte) 1);
		for (int i = 0; i < 7; i++) {
			channelBuffer.writeByte((byte) 0);
		}
		try {
			channelBuffer.writeBytes(int2Byte(robotinfo_operation
					.getBytes("utf8").length));
			channelBuffer.writeBytes(robotinfo_operation.getBytes("utf8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Channel channel = handler.getChannel();
		channel.write(channelBuffer);
	}

	// 将字节数组转换为字符串
	public static String getHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase(Locale.getDefault()));
		}
		return sb.toString();
	}

	//可能是测试用的
	public static void Scoket(JSONObject params, ChannelHandlerContext handler) {
		Channel channel = handler.getChannel();
		try {
			params.put("1", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	//	channel.write(params.toString().getBytes());
		channel.write("control");
	}

	// 得到4位字节长度方法
	public static byte[] int2Byte(int intValue) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
			// System.out.print(Integer.toBinaryString(b[i])+"");
			// System.out.print((b[i]& 0xFF) + " ");
		}

		return b;
	}

	// http回掉接口
	public interface callback {
		public void success(JSONObject json);

		public void error(String errorresult);
	}

	//拨号的类型
	public interface NumberType{
		String Robot = "Robot";
		String Room = "Room";
		String Phone = "Phone";
	}
}
