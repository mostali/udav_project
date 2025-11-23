package mpe.rt;

import mpu.Sys;
import mpc.time.EPOCH;
import mpu.core.QDate;

import java.util.concurrent.TimeUnit;

public abstract class SheduledThread extends Thread {
	public static void main(String[] args) {

		sayEverySec(TimeUnit.MINUTES.toSeconds(7));
//		sayAtTimeEpoch(QDate.now().addSeconds(1).epoch(), "0");
//		sayAtTimeEpoch(QDate.now().addSeconds(60).epoch(), "1");
//		sayAtTimeEpoch(QDate.now().addSeconds(120).epoch(), "2");
//		sayAtTimeEpoch(QDate.now().addSeconds(180).epoch(), "3");
//		sayAtTimeEpoch(QDate.now().addSeconds(240).epoch(), "4");
//		sayAtTimeEpoch(QDate.now().addSeconds(300).epoch(), "5");
	}

	private static void sayEverySec(long evevrySec) {
		int sleep = 1000;
		Sys.p(QDate.now().mono6_h2s2());
		Sys.say("0");
		int i = 0;
		int ctr = 0;
		while (++i >= 0) {
			if (i % evevrySec == 0) {
				ctr++;
				Sys.say("" + ctr);
				Sys.p("Ctr:" + ctr + ":" + QDate.now().mono6_h2s2());
			}
			SLEEP.ms(sleep);
			if (false) {
				break;
			}
		}
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
				SLEEP.ms(1000);
				continue;
			}
			doWork();
			break;
		}
	}

	protected abstract void doWork();
}
