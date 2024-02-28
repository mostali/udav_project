package mpe.rt;

import mpu.Sys;
import mpc.time.EPOCH;
import mpu.core.QDate;

public abstract class SheduledThread extends Thread {
	public static void main(String[] args) {
		sayAtTimeEpoch(QDate.now().addSeconds(1).epoch(), "0");
		sayAtTimeEpoch(QDate.now().addSeconds(60).epoch(), "1");
		sayAtTimeEpoch(QDate.now().addSeconds(120).epoch(), "2");
		sayAtTimeEpoch(QDate.now().addSeconds(180).epoch(), "3");
		sayAtTimeEpoch(QDate.now().addSeconds(240).epoch(), "4");
		sayAtTimeEpoch(QDate.now().addSeconds(300).epoch(), "5");
	}

	private static void sayAtTimeEpoch(int time_epoc, String msg, Object... args) {
		new SheduledThread(time_epoc) {
			@Override
			protected void doWork() {
				Sys.say(msg, args);
			}
		};
	}

	final int at_time_epoch;

	public SheduledThread(int at_time_epoch) {
		this.at_time_epoch = at_time_epoch;
		start();
	}

	@Override
	public void run() {
		while (true) {
			if (EPOCH.epoch() < at_time_epoch) {
				SLEEP.sleep(1000);
				continue;
			}
			doWork();
			break;
		}
	}

	protected abstract void doWork();
}
