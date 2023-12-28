package com.github.rakhmedovrs.client;

import com.github.rakhmedovrs.Common;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ruslan Rakhmedov
 * @created 12/20/2023
 */
@Slf4j
public class ClientRunner {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(Common.MAXIMUM_NUMBER_OF_ACTIVE_CLIENTS);
		for (int i = 0; i < Common.MAXIMUM_NUMBER_OF_ACTIVE_CLIENTS; i++) {
			executorService.submit(new Client(i));
			log.info("Client {} has been started", i);
		}
	}
}
