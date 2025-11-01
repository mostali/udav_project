//package zk_notes.control.maintbx.console;
//
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.Desktop;
//import org.zkoss.zk.ui.Page;
//
//import java.io.File;
//import java.lang.reflect.Field;
//import java.util.Collection;
//import java.util.Map;
//
//public class UZK {
//
//	public static void p(Object obj) {
//		System.out.println(obj);
//	}
//
//	// public static void sendRedirect(String message) {
//	// Executions.getCurrent().sendRedirect("index.zul");
//	// }
//
//	public static void createComponent(String message) {
//		// Executions.createComponentsDirectly(content, extension, parent,
//		// arg)("/mycombo.zul", parentComponent, null);
//	}
//
//	public static void printInfo() {
//
//		Desktop desk = USite.getDesktop();
//		UZK.p("Desktop :" + desk);
//
//		Collection<Page> pages = desk.getPages();
//		UZK.p("Found pages :" + pages.size());
//		for (Page page : pages) {
//			UZK.p("Page : " + page);
//			UZK.p("First root :" + page.getFirstRoot());
//			printComponents(page.getRoots());
//		}
//	}
//
//	public static void printComponents(Collection<Component> components) {
//		UZK.p("Found Components :" + components.size());
//		for (Component com : components) {
//			UZK.p("Com :" + com);
//		}
//	}
//
//	public static void printAttributes(Component component) {
//		Map<String, Object> mapAttributes = component.getAttributes();
//		UZK.p("Found Components Attributes :" + mapAttributes.size());
//		for (String attr : mapAttributes.keySet())
//			UZK.p(attr + " :" + mapAttributes.get(attr));
//	}
//
//	public static StringBuilder printObjectInfo(Object o) {
//		StringBuilder sb = new StringBuilder();
//		if (o == null) {
//			sb.append("Object is null");
//			return sb;
//		} else {
//			sb.append("Class :" + o.getClass().getName());
//		}
//		String SEP = File.separator;
//		Field[] fields = o.getClass().getDeclaredFields();
//		for (int i = 0; i < fields.length; i++) {
//			try {
//				sb.append(fields[i].getName() + " - " + fields[i].get(o)).append(SEP);
//			} catch (Exception e) {
//				sb.append("Object field :" + fields[i].getName() + " :error" + e.getClass().getName() + ":"
//						  + e.getMessage()).append(SEP);
//			}
//		}
//		return sb;
//	}
//
//}
