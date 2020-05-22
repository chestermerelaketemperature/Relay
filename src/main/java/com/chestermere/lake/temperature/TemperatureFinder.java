package com.chestermere.lake.temperature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TemperatureFinder extends CompletableFuture<Float> {

	private final InputStream input;

	public TemperatureFinder(InputStream input) {
		this.input = input;
	}

	public CompletableFuture<Optional<Float>> getTemperature() {
		return CompletableFuture.supplyAsync(() -> {
			while (true) {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));
					String string = reader.readLine();
					try {
						return Optional.of(Float.parseFloat(string));
					} catch (NumberFormatException e) {}
				} catch (IOException e) {
					e.printStackTrace();
					return Optional.empty();
				}
			}
		});
	}

}
