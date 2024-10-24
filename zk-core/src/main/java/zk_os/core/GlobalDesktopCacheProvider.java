//package zk_core.comzk;
//
//import org.jetbrains.annotations.Nullable;
//import org.springframework.context.ApplicationContext;
//import org.zkoss.zk.ui.*;
//import org.zkoss.zk.ui.http.ExecutionImpl;
//import org.zkoss.zk.ui.sys.DesktopCache;
//import org.zkoss.zk.ui.sys.DesktopCtrl;
//import org.zkoss.zk.ui.sys.ExecutionsCtrl;
//import org.zkoss.zk.ui.sys.WebAppCtrl;
//import org.zkoss.zk.ui.util.Configuration;
//import org.zkoss.zk.ui.util.DesktopRecycle;
//import org.zkoss.zk.ui.util.Monitor;
//
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.lang.ref.WeakReference;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//public class GlobalDesktopCacheProvider extends org.zkoss.zk.ui.impl.GlobalDesktopCacheProvider {
//	public static final String BEAN_NAME = "zkDesktopCache";
//	private volatile MonitoredDesktopCache mdc;
//	private ApplicationContext applicationContext;
//	private static WeakReference<GlobalDesktopCacheProvider> singleton;
//
//	public GlobalDesktopCacheProvider() {
//	}
//
//	public DesktopCache getDesktopCache(Session sess) {
//		if (this.mdc == null) {
//			synchronized(this) {
//				if (this.mdc == null) {
//					this.mdc = new MonitoredDesktopCache(sess);
//				}
//			}
//		}
//
//		if (singleton == null) {
//			singleton = new WeakReference(this);
//		}
//
//		return this.mdc;
//	}
//
//	public void sessionDestroyed(Session session) {
//		if (this.mdc != null) {
//			this.mdc.invalidateSessionDesktops(session);
//		}
//
//		String userSystemName = this.getSessionOwnerName(session);
//		ApplicationContext appContext = this.getApplicationContext();
//		if (appContext != null && userSystemName != null) {
//			appContext.publishEvent(new DesktopSessionCleanupEvent(session, userSystemName));
//		}
//
//	}
//
//	public ApplicationContext getApplicationContext() {
//		if (this.applicationContext == null) {
//			this.applicationContext = UIUtils.getApplicationContext();
//		}
//
//		return this.applicationContext;
//	}
//
//	@Nullable
//	private String getSessionOwnerName(Session session) {
//		String userSystemName = null;
//		Object userInfo = session.getAttribute("currentUser");
//		if (userInfo instanceof UserInfo) {
//			userSystemName = ((UserInfo)userInfo).getSystemName();
//		}
//
//		return userSystemName;
//	}
//
//	public static Map<String, Desktop> getDesktops() {
//		return (Map)(singleton != null && singleton.get() != null ? ((GlobalDesktopCacheProvider)singleton.get()).mdc.cache.asMap() : Collections.emptyMap());
//	}
//
//	private static final class StubExecution extends ExecutionImpl {
//		private Map<String, Object> _attrs;
//
//		StubExecution(ServletContext ctx, Desktop desktop) {
//			super(ctx, (HttpServletRequest)null, (HttpServletResponse)null, desktop, (Page)null);
//			this.setDesktop(desktop);
//		}
//
//		public Object getAttribute(String name) {
//			return this._attrs != null ? this._attrs.get(name) : null;
//		}
//
//		public Object setAttribute(String name, Object value) {
//			if (this._attrs == null) {
//				this._attrs = new HashMap(2);
//			}
//
//			return this._attrs.put(name, value);
//		}
//
//		public Object removeAttribute(String name) {
//			return this._attrs != null ? this._attrs.remove(name) : null;
//		}
//
//		public Map<String, Object> getAttributes() {
//			return this._attrs != null ? this._attrs : Collections.emptyMap();
//		}
//	}
//
//	static class MonitoredDesktopCache implements DesktopCache {
//		private final int maxSize;
//		Cache<String, Desktop> cache;
//		private int _nextKey;
//
//		MonitoredDesktopCache(Session sess) {
//			Configuration _config = sess.getWebApp().getConfiguration();
//			if (!_config.isRepeatUuid()) {
//				this._nextKey = (int)System.currentTimeMillis() & '\uffff';
//			}
//
//			this.maxSize = UIUtils.getConfigurationManager().getCombinedConfiguration().getInt("sufd.web.desktoplimit");
//			MetricRegistry.getMarker("core", "zkDesktopCache", "maxSize").mark(String.valueOf(this.maxSize));
//			int expiration = UIUtils.getConfigurationManager().getCombinedConfiguration().getInt("sufd.web.timeout");
//			MetricRegistry.getMarker("core", "zkDesktopCache", "expiration").mark(String.valueOf(expiration));
//			this.cache = CacheBuilder.newBuilder().maximumSize((long)this.maxSize).expireAfterAccess(expiration > 0 ? (long)expiration : 3600L, TimeUnit.SECONDS).removalListener((key, value, cause) -> {
//				this.desktopDestroyed((Desktop)value);
//				switch (cause) {
//					case SIZE:
//						MetricRegistry.getMarker("core", "zkDesktopCache", "cacheState").mark("FULL");
//						break;
//					case EXPIRED:
//						MetricRegistry.getConcurrentCounter("core", "zkDesktopCache", "expired").inc();
//				}
//
//			}).build();
//		}
//
//		public int getNextKey() {
//			synchronized(this) {
//				return this._nextKey++;
//			}
//		}
//
//		public Desktop getDesktop(String desktopId) {
//			Desktop desktop = (Desktop)this.cache.getIfPresent(desktopId);
//			if (desktop == null) {
//				throw new ComponentNotFoundException("Desktop not found: " + desktopId);
//			} else {
//				return desktop;
//			}
//		}
//
//		public Desktop getDesktopIfAny(String desktopId) {
//			return (Desktop)this.cache.getIfPresent(desktopId);
//		}
//
//		public void addDesktop(Desktop desktop) {
//			long size = this.cache.estimatedSize();
//			if ((long)this.maxSize <= size) {
//				MetricRegistry.getMarker("core", "zkDesktopCache", "cacheState").mark("FULL");
//			} else {
//				MetricRegistry.getMarker("core", "zkDesktopCache", "cacheState").mark("OK");
//			}
//
//			this.cache.put(desktop.getId(), desktop);
//			MetricRegistry.getMarker("core", "zkDesktopCache", "count").mark(String.valueOf(size + 1L));
//		}
//
//		public void removeDesktop(Desktop desktop) {
//			this.cache.invalidate(desktop.getId());
//		}
//
//		public void sessionWillPassivate(Session sess) {
//			Iterator var2 = this.cache.asMap().values().iterator();
//
//			while(var2.hasNext()) {
//				Desktop desktop = (Desktop)var2.next();
//				((DesktopCtrl)desktop).sessionWillPassivate(sess);
//			}
//
//		}
//
//		public void sessionDidActivate(Session sess) {
//			Iterator var2 = this.cache.asMap().values().iterator();
//
//			while(var2.hasNext()) {
//				Desktop desktop = (Desktop)var2.next();
//				((DesktopCtrl)desktop).sessionDidActivate(sess);
//			}
//
//		}
//
//		public void stop() {
//			Iterator var1 = this.cache.asMap().values().iterator();
//
//			while(var1.hasNext()) {
//				Desktop desktop = (Desktop)var1.next();
//				this.desktopDestroyed(desktop);
//			}
//
//			this.cache.invalidateAll();
//		}
//
//		void invalidateSessionDesktops(Session session) {
//			Iterator var2 = this.cache.asMap().entrySet().iterator();
//
//			while(var2.hasNext()) {
//				Map.Entry<String, Desktop> cachedDesktop = (Map.Entry)var2.next();
//				Desktop desktop = (Desktop)cachedDesktop.getValue();
//				if (desktop.getSession() == session) {
//					this.desktopDestroyed(desktop);
//					this.cache.invalidate(cachedDesktop.getKey());
//				}
//			}
//
//		}
//
//		private void desktopDestroyed(Desktop desktop) {
//			Session sess = desktop.getSession();
//			Execution exec = new StubExecution(desktop.getWebApp().getServletContext(), desktop);
//
//			try {
//				ExecutionsCtrl.setCurrent(exec);
//				DesktopCtrl desktopCtrl = (DesktopCtrl)desktop;
//				desktopCtrl.setExecution(exec);
//				WebApp wapp = desktop.getWebApp();
//				((DesktopCtrl)desktop).invokeDesktopCleanups();
//				Configuration config = wapp.getConfiguration();
//				config.invokeDesktopCleanups(desktop);
//				((WebAppCtrl)wapp).getUiEngine().desktopDestroyed(desktop);
//				Monitor monitor = desktop.getWebApp().getConfiguration().getMonitor();
//				if (monitor != null) {
//					try {
//						monitor.desktopDestroyed(desktop);
//					} catch (Exception var15) {
//					}
//				}
//
//				DesktopRecycle dtrc = config.getDesktopRecycle();
//				if (dtrc != null) {
//					try {
//						dtrc.afterRemove(sess, desktop);
//					} catch (Exception var14) {
//					}
//				}
//			} finally {
//				ExecutionsCtrl.setCurrent((Execution)null);
//			}
//
//		}
//	}
//}