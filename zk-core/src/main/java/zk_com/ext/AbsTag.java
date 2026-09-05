//package zk_com.ext;
//
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.io.Writer;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import org.zkoss.html.HTMLs;
//import org.zkoss.lang.Objects;
//import org.zkoss.xml.XMLs;
////import org.zkoss.zhtml.impl.PageRenderer;
////import org.zkoss.zhtml.impl.TagRenderContext;
//import org.zkoss.zk.au.DeferredValue;
//import org.zkoss.zk.ui.AbstractComponent;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Desktop;
//import org.zkoss.zk.ui.Execution;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.Page;
//import org.zkoss.zk.ui.UiException;
//import org.zkoss.zk.ui.WrongValueException;
//import org.zkoss.zk.ui.ext.DynamicPropertied;
//import org.zkoss.zk.ui.ext.RawId;
//import org.zkoss.zk.ui.ext.render.DirectContent;
//import org.zkoss.zk.ui.sys.BooleanPropertyAccess;
//import org.zkoss.zk.ui.sys.ComponentCtrl;
//import org.zkoss.zk.ui.sys.ComponentsCtrl;
//import org.zkoss.zk.ui.sys.ContentRenderer;
//import org.zkoss.zk.ui.sys.HtmlPageRenders;
//import org.zkoss.zk.ui.sys.PropertyAccess;
//import org.zkoss.zk.ui.sys.StringPropertyAccess;
//
//public class AbsTag extends AbstractComponent implements DynamicPropertied, RawId {
//	protected String _tagnm;
//	private Map<String, Object> _props;
//	private static HashMap<String, PropertyAccess> _properties;
//
//	protected AbsTag(String tagname) {
//		if (tagname != null && tagname.length() != 0) {
//			this._tagnm = tagname;
//		} else {
//			throw new IllegalArgumentException("A tag name is required");
//		}
//	}
//
//	protected AbsTag() {
//	}
//
//	public String getSclass() {
//		return (String) this.getDynamicProperty("class");
//	}
//
//	public void setSclass(String sclass) {
//		this.setDynamicProperty("class", sclass);
//	}
//
//	public String getStyle() {
//		return (String) this.getDynamicProperty("style");
//	}
//
//	public void setStyle(String style) {
//		this.setDynamicProperty("style", style);
//	}
//
//	public String getAccesskey() {
//		return (String) this.getDynamicProperty("accesskey");
//	}
//
//	public void setAccesskey(String accesskey) throws WrongValueException {
//		this.setDynamicProperty("accesskey", accesskey);
//	}
//
//	public boolean isContenteditable() {
//		Boolean b = (Boolean) this.getDynamicProperty("contenteditable");
//		return b != null && b;
//	}
//
//	public void setContenteditable(boolean contenteditable) throws WrongValueException {
//		this.setDynamicProperty("contenteditable", contenteditable ? contenteditable : null);
//	}
//
//	public String getDir() {
//		return (String) this.getDynamicProperty("dir");
//	}
//
//	public void setDir(String dir) throws WrongValueException {
//		this.setDynamicProperty("dir", dir);
//	}
//
//	public boolean isDraggable() {
//		Boolean b = (Boolean) this.getDynamicProperty("draggable");
//		return b != null && b;
//	}
//
//	public void setDraggable(boolean draggable) throws WrongValueException {
//		this.setDynamicProperty("draggable", draggable ? draggable : null);
//	}
//
//	public boolean isHidden() {
//		Boolean b = (Boolean) this.getDynamicProperty("hidden");
//		return b != null && b;
//	}
//
//	public void setHidden(boolean hidden) throws WrongValueException {
//		this.setDynamicProperty("hidden", hidden ? hidden : null);
//	}
//
//	public String getLang() {
//		return (String) this.getDynamicProperty("lang");
//	}
//
//	public void setLang(String lang) throws WrongValueException {
//		this.setDynamicProperty("lang", lang);
//	}
//
//	public boolean isSpellcheck() {
//		Boolean b = (Boolean) this.getDynamicProperty("spellcheck");
//		return b != null && b;
//	}
//
//	public void setSpellcheck(boolean spellcheck) throws WrongValueException {
//		this.setDynamicProperty("spellcheck", spellcheck ? spellcheck : null);
//	}
//
//	public Integer getTabindex() {
//		return (Integer) this.getDynamicProperty("tabindex");
//	}
//
//	public void setTabindex(Integer tabindex) throws WrongValueException {
//		this.setDynamicProperty("tabindex", tabindex);
//	}
//
//	public String getTitle() {
//		return (String) this.getDynamicProperty("title");
//	}
//
//	public void setTitle(String title) throws WrongValueException {
//		this.setDynamicProperty("title", title);
//	}
//
//	public String getTag() {
//		return this._tagnm;
//	}
//
//	public boolean hasDynamicProperty(String name) {
//		return ComponentsCtrl.isReservedAttribute(name);
//	}
//
//	public Object getDynamicProperty(String name) {
//		return this._props != null ? this._props.get(name) : null;
//	}
//
//	public void setDynamicProperty(String name, Object value) throws WrongValueException {
//		if (name == null) {
//			throw new WrongValueException("name is required");
//		} else if (!this.hasDynamicProperty(name)) {
//			throw new WrongValueException("Attribute not allowed: " + name + "\nSpecify the ZK namespace if you want to use special ZK attributes");
//		} else {
//			String sval = Objects.toString(value);
//			if ("style".equals(name)) {
//				sval = this.filterStyle(sval);
//				this.setDynaProp(name, sval);
//			} else if ("src".equals(name)) {
//				EncodedURL url = new EncodedURL(sval);
//				this.setDynaProp(name, url);
//				sval = (String) url.getValue();
//			} else if ("textContent".equals(name)) {
//				this.setDynaProp(name, sval);
//				if (!this.getChildren().isEmpty()) {
//					this.invalidate();
//				}
//			} else {
//				this.setDynaProp(name, value);
//			}
//
//			this.smartUpdate("dynamicProperty", new String[]{name, sval}, true);
//		}
//	}
//
//	private String getEncodedURL(String src) {
//		if (src == null) {
//			return "data:image/gif;base64,R0lGODlhAQABAIAAAP///////yH5BAUUAAEALAAAAAABAAEAAAICTAEAOw==";
//		} else {
//			Desktop dt = this.getDesktop();
//			return dt != null ? dt.getExecution().encodeURL(src) : "";
//		}
//	}
//
//	private String filterStyle(String style) {
//		if (style != null) {
//			int j = HTMLs.getSubstyleIndex(style, "display");
//			if (j >= 0) {
//				super.setVisible(!"none".equals(HTMLs.getSubstyleValue(style, j)));
//				return style;
//			}
//		}
//
//		if (!this.isVisible()) {
//			int len = style != null ? style.length() : 0;
//			if (len == 0) {
//				return "display:none;";
//			}
//
//			if (style.charAt(len - 1) != ';') {
//				style = style + ';';
//			}
//
//			style = style + "display:none;";
//		}
//
//		return style;
//	}
//
//	private void setDynaProp(String name, Object value) {
//		if (value == null) {
//			if (this._props != null) {
//				this._props.remove(name);
//			}
//		} else {
//			if (this._props == null) {
//				this._props = new LinkedHashMap();
//			}
//
//			this._props.put(name, value);
//		}
//
//	}
//
//	protected boolean shallHideId() {
//		return false;
//	}
//
//	public boolean setVisible(boolean visible) {
//		boolean old = super.setVisible(visible);
//		if (old != visible) {
//			String style = this.getStyle();
//			if (visible) {
//				if (style != null) {
//					int j = HTMLs.getSubstyleIndex(style, "display");
//					if (j >= 0) {
//						String val = HTMLs.getSubstyleValue(style, j);
//						if ("none".equals(val)) {
//							String newstyle = style.substring(0, j);
//							int k = style.indexOf(59, j + 7);
//							if (k >= 0) {
//								newstyle = newstyle + style.substring(k + 1);
//							}
//
//							this.setDynaProp("style", newstyle);
//						}
//					}
//				}
//			} else if (style == null) {
//				this.setDynaProp("style", "display:none;");
//			} else {
//				int j = HTMLs.getSubstyleIndex(style, "display");
//				if (j >= 0) {
//					String val = HTMLs.getSubstyleValue(style, j);
//					if (!"none".equals(val)) {
//						String newstyle = style.substring(0, j) + "display:none;";
//						int k = style.indexOf(59, j + 7);
//						if (k >= 0) {
//							newstyle = newstyle + style.substring(k + 1);
//						}
//
//						this.setDynaProp("style", newstyle);
//					}
//				} else {
//					int len = style.length();
//					if (len > 0 && style.charAt(len - 1) != ';') {
//						(new StringBuilder()).append(style).append(';').toString();
//					}
//
//					this.setDynaProp("style", style + "display:none;");
//				}
//			}
//		}
//
//		return old;
//	}
//
//	public String getWidgetClass() {
//		return "zhtml.Widget";
//	}
//
//	public void redraw(Writer out) throws IOException {
//		if (this._tagnm == null) {
//			throw new UiException("The tag name is not initialized yet");
//		} else {
//			Execution exec = Executions.getCurrent();
//			if (exec != null && !exec.isAsyncUpdate((Page) null) && HtmlPageRenders.isDirectContent(exec)) {
//				TagRenderContext rc = PageRenderer.getTagRenderContext(exec);
//				boolean rcRequired = rc == null;
//				Object ret = null;
//				if (rcRequired) {
//					ret = PageRenderer.beforeRenderTag(exec);
//					rc = PageRenderer.getTagRenderContext(exec);
//				}
//
//				out.write(this.getPrologHalf(false));
//				rc.renderBegin(this, this.getClientEvents(), this.getSpecialRendererOutput(this), false);
//				this.redrawChildrenDirectly(rc, exec, out);
//				out.write(this.getEpilogHalf());
//				rc.renderEnd(this);
//				if (rcRequired) {
//					out.write(rc.complete());
//					PageRenderer.afterRenderTag(exec, ret);
//				}
//
//			} else {
//				super.redraw(out);
//			}
//		}
//	}
//
//	protected void redrawChildrenDirectly(TagRenderContext rc, Execution exec, Writer out) throws IOException {
//		Component next;
//		for (Component child = this.getFirstChild(); child != null; child = next) {
//			next = child.getNextSibling();
//			if (((ComponentCtrl) child).getExtraCtrl() instanceof DirectContent) {
//				((ComponentCtrl) child).redraw(out);
//			} else {
//				HtmlPageRenders.setDirectContent(exec, false);
//				rc.renderBegin(child, (Map) null, this.getSpecialRendererOutput(child), true);
//				HtmlPageRenders.outStandalone(exec, child, out);
//				rc.renderEnd(child);
//				HtmlPageRenders.setDirectContent(exec, true);
//			}
//		}
//
//	}
//
//	protected void renderProperties(ContentRenderer renderer) throws IOException {
//		super.renderProperties(renderer);
//		this.render(renderer, "prolog", this.getPrologHalf(false));
//		this.render(renderer, "epilog", this.getEpilogHalf());
//	}
//
//	String getPrologHalf(boolean hideUuidIfNoId) {
//		StringBuilder sb = (new StringBuilder(128)).append('<').append(this._tagnm);
//		if (!hideUuidIfNoId && !this.shallHideId() || this.getId().length() > 0) {
//			sb.append(" id=\"").append(this.getUuid()).append('"');
//		}
//
//		if (this._props != null) {
//			for (Map.Entry me : this._props.entrySet()) {
//				if (!"textContent".equals(me.getKey())) {
//					Object v = me.getValue();
//					if (v instanceof DeferredValue) {
//						v = ((DeferredValue) v).getValue();
//					}
//
//					sb.append(' ').append(me.getKey()).append("=\"").append(XMLs.encodeAttribute(Objects.toString(v))).append('"');
//				}
//			}
//		}
//
//		if (!this.isOrphanTag()) {
//			sb.append('/');
//		}
//
//		sb.append('>');
//		Object textContent = this.getDynamicProperty("textContent");
//		if (textContent != null) {
//			sb.append(XMLs.escapeXML((String) textContent));
//		}
//
//		return sb.toString();
//	}
//
//	String getEpilogHalf() {
//		return this.isOrphanTag() ? "</" + this._tagnm + '>' : "";
//	}
//
//	protected boolean isChildable() {
//		return this.isOrphanTag();
//	}
//
//	protected boolean isOrphanTag() {
//		return !HTMLs.isOrphanTag(this._tagnm);
//	}
//
//	public PropertyAccess getPropertyAccess(String prop) {
//		PropertyAccess pa = (PropertyAccess) _properties.get(prop);
//		return pa != null ? pa : super.getPropertyAccess(prop);
//	}
//
//	public Object clone() {
//		AbsTag clone = (AbsTag) super.clone();
//		if (clone._props != null) {
//			clone._props = new LinkedHashMap(clone._props);
//		}
//
//		return clone;
//	}
//
//	public String toString() {
//		return "[" + this._tagnm + ' ' + super.toString() + ']';
//	}
//
//	public Object getExtraCtrl() {
//		return new AbsTag.ExtraCtrl();
//	}
//
//	static {
//		addClientEvent(org.zkoss.zhtml.impl.AbstractTag.class, "onClick", 0);
//		_properties = new HashMap(5);
//		_properties.put("id", new StringPropertyAccess() {
//			public void setValue(Component cmp, String value) {
//				((org.zkoss.zhtml.impl.AbstractTag) cmp).setId(value);
//			}
//
//			public String getValue(Component cmp) {
//				return ((org.zkoss.zhtml.impl.AbstractTag) cmp).getId();
//			}
//		});
//		_properties.put("sclass", new StringPropertyAccess() {
//			public void setValue(Component cmp, String value) {
//				((org.zkoss.zhtml.impl.AbstractTag) cmp).setSclass(value);
//			}
//
//			public String getValue(Component cmp) {
//				return ((org.zkoss.zhtml.impl.AbstractTag) cmp).getSclass();
//			}
//		});
//		_properties.put("style", new StringPropertyAccess() {
//			public void setValue(Component cmp, String value) {
//				((org.zkoss.zhtml.impl.AbstractTag) cmp).setStyle(value);
//			}
//
//			public String getValue(Component cmp) {
//				return ((org.zkoss.zhtml.impl.AbstractTag) cmp).getStyle();
//			}
//		});
//		_properties.put("visible", new BooleanPropertyAccess() {
//			public void setValue(Component cmp, Boolean value) {
//				((org.zkoss.zhtml.impl.AbstractTag) cmp).setVisible(value);
//			}
//
//			public Boolean getValue(Component cmp) {
//				return ((org.zkoss.zhtml.impl.AbstractTag) cmp).isVisible();
//			}
//		});
//	}
//
//	protected class ExtraCtrl implements DirectContent {
//		protected ExtraCtrl() {
//		}
//	}
//
//	private class EncodedURL implements DeferredValue, Serializable {
//		private String _src;
//
//		public EncodedURL(String src) {
//			this._src = src;
//		}
//
//		public Object getValue() {
//			return AbsTag.this.getEncodedURL(this._src);
//		}
//	}
//}
