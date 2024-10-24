//package zk_os.srv;
//
//import j2html.tags.ContainerTag;
//import mpu.Sys;
//
//import static j2html.TagCreator.*;
//
///**
// * @author dav 13.09.2021
// */
//public class UHtml {
//	public static void main(String[] args) {
//		ContainerTag win = tag("window").with(h1("asd"));
//		Sys.exit(win.render());
//		Sys.exit(tag("window").attr("ss", "vv").render());
//		String r = body(
//				h1("Hello, World!"),
//				img().withSrc("/img/hello.png")
//		).render();
//		Sys.p(r);
//	}
//}
