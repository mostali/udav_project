package zk_com.ext.video;


import org.zkoss.util.media.Media;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.DeferredValue;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.StateChangeEvent;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Audio;
import org.zkoss.zul.Track;
import org.zkoss.zul.ext.MediaElement;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//public class AdvVideo extends HtmlBasedComponent implements UiLifeCycle, AfterCompose, FileUploadSpecification {
public class AdvVideo extends XulElement implements MediaElement {
	public static final int STOP = 0;
	public static final int PLAY = 1;
	public static final int PAUSE = 2;
	public static final int END = 3;
	protected List<String> _src = new ArrayList();
	private org.zkoss.video.Video _audio;
	private byte _audver;
	private boolean _autoplay;
	private boolean _controls;
	private boolean _loop;
	private boolean _muted;
	private String _preload;
	private int _currentState;

	public AdvVideo() {
	}

	public AdvVideo(String src) {
		this.setSrc(src);
	}

	public void service(AuRequest request, boolean everError) {
		String cmd = request.getCommand();
		if ("onStateChange".equals(cmd)) {
			this._currentState = (Integer) request.getData().get("state");
			Events.postEvent(new StateChangeEvent(cmd, this, this._currentState));
		} else {
			super.service(request, everError);
		}

	}

	public void play() {
		this.response("ctrl", new AuInvoke(this, "play"));
	}

	public void stop() {
		this.response("ctrl", new AuInvoke(this, "stop"));
		this._currentState = 0;
		Events.postEvent(new StateChangeEvent("onStateChange", this, this._currentState));
	}

	public void pause() {
		this.response("ctrl", new AuInvoke(this, "pause"));
	}

	public List<String> getSrc() {
		return this._src;
	}

	public void setSrc(String src) {
		List<String> list = new ArrayList();
		if (src.contains(",")) {
			list = new ArrayList(Arrays.asList(src.split("\\s*,\\s*")));
		} else {
			list.add(src.trim());
		}

		if (this._audio != null || !this._src.equals(list)) {
			this._audio = null;
			this.setSrc((List) list);
		}

	}

	public void setSrc(List<String> src) {
		if (!src.equals(this._src)) {
			this._src = src;
			this.smartUpdate("src", new EncodedSrc());
		}

	}

	/**
	 * @deprecated
	 */
	public boolean isAutostart() {
		return this.isAutoplay();
	}

	/**
	 * @deprecated
	 */
	public void setAutostart(boolean autostart) {
		this.setAutoplay(autostart);
	}

	public boolean isAutoplay() {
		return this._autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		if (this._autoplay != autoplay) {
			this._autoplay = autoplay;
			this.smartUpdate("autoplay", this._autoplay);
		}

	}

	public String getPreload() {
		return this._preload;
	}

	public void setPreload(String preload) {
		if ("none".equalsIgnoreCase(preload)) {
			preload = "none";
		} else if ("metadata".equalsIgnoreCase(preload)) {
			preload = "metadata";
		} else {
			preload = "auto";
		}

		if (!preload.equals(this._preload)) {
			this._preload = preload;
			this.smartUpdate("preload", this._preload);
		}

	}

	public boolean isControls() {
		return this._controls;
	}

	public void setControls(boolean controls) {
		if (this._controls != controls) {
			this._controls = controls;
			this.smartUpdate("controls", this._controls);
		}

	}

	public boolean isLoop() {
		return this._loop;
	}

	public void setLoop(boolean loop) {
		if (this._loop != loop) {
			this._loop = loop;
			this.smartUpdate("loop", this._loop);
		}

	}

	public boolean isMuted() {
		return this._muted;
	}

	public void setMuted(boolean muted) {
		if (this._muted != muted) {
			this._muted = muted;
			this.smartUpdate("muted", this._muted);
		}

	}

	public boolean isPlaying() {
		return this._currentState == 1;
	}

	public boolean isPaused() {
		return this._currentState == 2;
	}

	public boolean isStopped() {
		return this._currentState == 0 || this._currentState == 3;
	}

	public boolean isEnded() {
		return this._currentState == 3;
	}

	public void setContent(org.zkoss.video.Video audio) {
		if (this._src != null || audio != this._audio) {
			this._audio = audio;
			this._src = null;
			if (this._audio != null) {
				++this._audver;
			}

			this.smartUpdate("src", new EncodedSrc());
		}

	}

	public org.zkoss.video.Video getContent() {
		return this._audio;
	}

	private List<String> getEncodedSrc() {
		Desktop dt = this.getDesktop();
		List<String> list = new ArrayList();
		if (this._audio != null) {
			list.add(this.getAudioSrc());
		} else if (dt != null) {
			Iterator var3 = this._src.iterator();

			while (var3.hasNext()) {
				String src = (String) var3.next();
				list.add(dt.getExecution().encodeURL(src));
			}
		}

		return list;
	}

	private String getAudioSrc() {
		return Utils.getDynamicMediaURI(this, this._audver, this._audio.getName(), this._audio.getFormat());
	}

	protected void renderProperties(ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);
		this.render(renderer, "src", this.getEncodedSrc());
		this.render(renderer, "autoplay", this._autoplay);
		this.render(renderer, "preload", this._preload);
		this.render(renderer, "controls", this._controls);
		this.render(renderer, "loop", this._loop);
		this.render(renderer, "muted", this._muted);
	}

	public void beforeChildAdded(Component child, Component insertBefore) {
		if (!(child instanceof Track)) {
			throw new UiException("Unsupported child for audio: " + child);
		} else {
			super.beforeChildAdded(child, insertBefore);
		}
	}

	public Object getExtraCtrl() {
		return new AdvVideo.ExtraCtrl();
	}

	static {
		addClientEvent(Audio.class, "onStateChange", 1);
	}

	private class EncodedSrc implements DeferredValue {
		private EncodedSrc() {
		}

		public Object getValue() {
			return AdvVideo.this.getEncodedSrc();
		}
	}

	protected class ExtraCtrl extends HtmlBasedComponent.ExtraCtrl implements DynamicMedia {
		protected ExtraCtrl() {
//			super(Video.this);
		}

		public Media getMedia(String pathInfo) {
			return AdvVideo.this._audio;
		}
	}
}
