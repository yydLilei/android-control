package com.yongyida.robot.biz;

import com.yongyida.robot.bean.Alarm;
import com.yongyida.robot.bean.Remind;
import com.yongyida.robot.bean.Robot;
import com.yongyida.robot.utils.XmlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Biz {

	public static void adapter_robot(JSONObject json, List<Robot> list_robots)
			throws JSONException {
		if(list_robots.size() != 0){
			return;
		}
		String jsonstr = json.getString("Robots");
		JSONArray jsonarray = new JSONArray(jsonstr);
		for (int i = 0; i < jsonarray.length(); i++) {
			Robot robot = new Robot();
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			robot.setId(jsonobject.getString("id"));
			robot.setAddress(jsonobject.getString("addr"));
			robot.setRid(jsonobject.getInt("rid"));
			robot.setRname(jsonobject.getString("rname"));
			robot.setOnline(jsonobject.getBoolean("online"));
			robot.setController(jsonobject.getInt("controller"));
			robot.setRobot_serial(jsonobject.getString("serial"));
			robot.setAir(Robot.air.bind);
			robot.setBattery(jsonobject.getInt("battery"));
			list_robots.add(robot);
		}
	}

	public static void adapter_task(String result, List<Remind> list_task,
			List<Alarm> alarms) throws JSONException {
		JSONArray tasks = new JSONArray(result);
		for (int i = 0; i < tasks.length(); i++) {
			if (tasks.getJSONObject(i).getString("seq").indexOf("datetime") != -1) {
				Remind task = new Remind();
				task.setId(tasks.getJSONObject(i).getInt("id"));
				String time = tasks.getJSONObject(i).getString("time");
				task.setSettime(time);
				String title = tasks.getJSONObject(i).getString("title");
				task.setTitle(title);
				task.setContent(tasks.getJSONObject(i).getString("content"));
				list_task.add(task);
			} else {
				Alarm alarm = new Alarm();
				alarm.setId(tasks.getJSONObject(i).getInt("id"));
				String seq = tasks.getJSONObject(i).getString("seq");
				String next = seq.substring(seq.indexOf("<"), seq.length());
				String week = XmlUtil.xmltext(next, "day");
				String repeat = XmlUtil.xmltext(next, "repeat");
				if (Boolean.parseBoolean(repeat)) {
					alarm.setIsaways(1);
				} else {
					alarm.setIsaways(0);
				}
				String time = seq.substring(0, 8);
				alarm.setSettime(time);
				alarm.setTitle(tasks.getJSONObject(i).getString("title"));
				alarm.setContent(tasks.getJSONObject(i).getString("content"));
				alarm.setWeek(week);
				alarms.add(alarm);
			}

		}

	}

	public void messageparsing(JSONObject json) {

	}
}
