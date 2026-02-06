package zk_pages.zznsi_pages.jira_tasks.core;

import lombok.RequiredArgsConstructor;
import mpc.exception.WhatIsTypeException;
import mpu.core.ARR;

import java.util.List;

@RequiredArgsConstructor
public class PageCtx {

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

	public static PageCtx of() {
		return new PageCtx(ARR.as(Type.values()));
	}
}
