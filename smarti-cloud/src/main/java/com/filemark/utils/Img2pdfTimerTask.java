package com.filemark.utils;

import java.util.TimerTask;

public class Img2pdfTimerTask extends TimerTask {

	private Process process;

	
	
	public Img2pdfTimerTask(Process process) {
		super();
		this.process = process;
	}
	
	public void run() {
		if (process != null) {
			process.destroy();
			process = null;
		}	
	}
	
	public void setProcess(Process p) {
		process = p;
	}
	

}
