package com.example.ocrdemo.utils;

import android.os.Handler;
import android.os.Looper;

import com.example.ocrdemo.ScanerCodeActivity;

import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {

    private final CountDownLatch handlerInitLatch;
    private ScanerCodeActivity activity;
    private Handler handler;

	DecodeThread(ScanerCodeActivity activity) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(activity);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
