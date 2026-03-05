package zk_pages.zznsi_pages.jira_tasks.form.core;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;

import java.util.List;

@RequiredArgsConstructor
public class EAutoFF {

	public enum Type {
		PAGESTATE, SESSION, COOCKIE;

		public AutoFF.IAutoFF newAutoFF() {
			switch (this) {
				case PAGESTATE:
					return new AutoFF.AffPageState();
				case COOCKIE:
					return new AutoFF.AffCookie();
				case SESSION:
					return new AutoFF.AffSession();
				default:
					throw new WhatIsTypeException(this);
			}
		}
	}

	public final List<Type> allowed;

	public static EAutoFF of() {
		return new EAutoFF(ARR.as(Type.values()));
	}
}
