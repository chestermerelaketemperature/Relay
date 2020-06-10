package com.chestermere.lake.temperature.packets;

import com.sitrica.japson.gson.Gson;
import com.sitrica.japson.shared.Packet;

public class TemperaturePacket extends Packet {

	private final float temperature;

	public TemperaturePacket(float temperature) {
		super((byte) 0x01);
		this.temperature = temperature;
	}

	@Override
	public String toJson(Gson gson) {
		object.addProperty("temperature", temperature);
		return gson.toJson(object);
	}

}
