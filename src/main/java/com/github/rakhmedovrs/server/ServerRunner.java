package com.github.rakhmedovrs.server;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Ruslan Rakhmedov
 * @created 12/19/2023
 */
@Slf4j
public class ServerRunner {
	public static void main(String[] args) throws InterruptedException {
		Thread thread = new Thread(new Server());
		thread.start();
		thread.join();
	}
}
