package com.chestermere.lake.temperature;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class Relay {

	private final static String URL = "btspp://000666C0BAE0:1;authenticate=false;encrypt=false;master=false";

	public static void main(String[] args) {
		StreamConnection connection = null;
		try {
			connection = (StreamConnection) Connector.open(URL);
			TemperatureFinder finder = new TemperatureFinder(connection.openInputStream());
			while (true) {
				Optional<Float> optional = finder.getTemperature().get(10, TimeUnit.SECONDS);
				if (optional.isPresent()) {
					float temperature = optional.get();
					System.out.println(temperature);
				}
			}
		} catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
