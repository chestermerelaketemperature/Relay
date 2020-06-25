package com.chestermere.lake.temperature;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.bluetooth.BluetoothConnectionException;
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
			japson = new JapsonClient(/*"chestermerelaketemperature.com", TESTING*/1337);
			//japson.enableDebug();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		while (true) {
			StreamConnection connection = null;
			try {
				connection = (StreamConnection) Connector.open(URL);
				TemperatureFinder finder = new TemperatureFinder(connection.openInputStream());
				while (connection != null) {
					try {
						Optional<Float> temperature = finder.getTemperature().get(5, TimeUnit.SECONDS);
						if (temperature.isPresent()) {
							System.out.println(temperature.get() + " - " + new Date().toString());
							japson.sendPacket(new TemperaturePacket(temperature.get()));
							//Thread.sleep(TimeUnit.MINUTES.toMillis(10)); // Ok we sent a successful temperature. Let's wait 10 minutes now.
						}
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						// Bluetooth device is potentially offline or missed connection.
						connection.close();
						connection = null;
					}
				}
			} catch (IOException e) {
				if (!(e instanceof BluetoothConnectionException))
					e.printStackTrace();
			} finally {
				try {
					if (connection != null)
						connection.close();
					connection = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("No connection, trying again...");
			try {
				Thread.sleep(17000); // Minimum tested time for the device to forget it's connection.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
