package com.chestermere.lake.temperature;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.chestermere.lake.temperature.packets.TemperaturePacket;
import com.sitrica.japson.client.JapsonClient;

public class Relay {

	// Bluetooth
	private final static String URL = "btspp://000666C0BAE0:1;authenticate=false;encrypt=false;master=false";
	private static JapsonClient japson;

	public static void main(String[] args) {
		try {
			japson = new JapsonClient("chestermerelaketemperature.com", 1337);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		StreamConnection connection = null;
		try {
			connection = (StreamConnection) Connector.open(URL);
			TemperatureFinder finder = new TemperatureFinder(connection.openInputStream());
			while (true) {
				try {
					Optional<Float> temperature = finder.getTemperature().get(10, TimeUnit.SECONDS);
					if (temperature.isPresent())
						japson.sendPacket(new TemperaturePacket(temperature.get()));
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					//TODO Bluetooth device is potentially offline or missed connection.
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
