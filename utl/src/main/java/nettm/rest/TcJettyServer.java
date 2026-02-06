//package nettm.rest;
//
//import mpu.Sys;
//import org.eclipse.jetty.server.Request;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class TcJettyServer {
//	public static void main(String[] args) {
//		new JettyServer(8085) {
//			@Override
//			protected TcJettyRootWorker newRootWorker() {
//				Class<TcWorker> workerClass = TcWorker.class;
//				return new TcJettyRootWorker(workerClass) {
//					@Override
//					protected TcServerSingleWorker newSingleWorker(String target, Request _jettyRequest, HttpServletRequest request, HttpServletResponse response) {
//						return new TcServerSingleWorker(workerClass, target, _jettyRequest, request, response) {
//							@Override
//							public void handle_request_as_long_oper_async() {
////								super.handle_request();
//								Sys.say("oo 123");
//							}
//						};
//					}
//				};
//			}
//		};
//	}
//}
