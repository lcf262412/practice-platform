package com.example.demo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
public class OutputToInput {
	private static OutputToPipe outputToPipe;
	
	@Autowired
	public void setOutputToPipe(OutputToPipe outputToPipeValue) {
		OutputToInput.outputToPipe = outputToPipeValue;
	}

	public static InputStream OutputToInput(final ByteArrayOutputStream out) throws IOException {
		PipedInputStream inputStream = new PipedInputStream();
		final PipedOutputStream outputStream = new PipedOutputStream(inputStream);
		outputToPipe.asyncOutputToPipe(out, outputStream);
		
		return inputStream;
	}
}

@Slf4j
@Component
class OutputToPipe {
	
	@Async("asyncExecutor")
	public void asyncOutputToPipe (ByteArrayOutputStream out, PipedOutputStream outputStream) {
		try {
			out.writeTo(outputStream);
			outputStream.close();
		} catch (IOException e) {
			log.error("Exception :", e);
		}
	}
}
