package zklogapp.otr.jmx;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mpe.core.P;
import mpc.fs.UF;
import mpc.net.DLD;
import mpe.rt.SLEEP;
import mpe.rt.ValueOutStream;
import mpu.str.UST;
import mpu.str.USToken;
import zk_page.core.PageSP;

import java.io.IOException;

@RequiredArgsConstructor
public class JmxPropertyCollector {
	public final String url;
	public int every_ms = 1_000;

	public transient boolean work = false;
	public transient int tc = -1;


	@SneakyThrows
	public static void main(String[] args) {
		P.exit(JmxHikariPoolAttrs.getGroupStats("http://localhost:18080"));
		P.exit(JmxHikariPoolAttrs.ActiveConnections.getSinlgeValue("http://localhost:18080"));
//			Object o = Net.url2val("http://localhost:18080/jmx/servers/0/domains/com.zaxxer.hikari/mbeans/type%3DPool+%28main%29/attributes/ActiveConnections/", new Net.ValueOutStream(bytes -> UST.INT(USToken.bw(new String((byte[]) bytes).trim(), "<pre>", "</pre>").trim())));
//			P.exit(o);
//			P.exit("::" + o);
//			P.exit(Net.url2val("http://localhost:18080/jmx/servers/0/domains/com.zaxxer.hikari/mbeans/type%3DPool+%28main%29/attributes/ActiveConnections/", new Net.ValueOutStream()));
//			mpc.log.L.setLogLevel(Net.class, Level.ERROR);
//			createStarted("http://localhost:18080/jmx/servers/0/domains/com.zaxxer.hikari/mbeans/type%3DPool+%28main%29/attributes/ActiveConnections/");
	}

	public static JmxPropertyCollector createStarted(String url) {
		return new JmxPropertyCollector(url).collect();
	}

	@SneakyThrows
	public JmxPropertyCollector collect() {
		work = true;
		new Thread(() -> {
			while (work) {
				try {
					P.p(UF.fn(url) + ":" + parseIntValue(url));
				} catch (IOException e) {
					PageSP.L.error("Collect IO error", e);
				} catch (Throwable e) {
					PageSP.L.error("Collect CRYTICAL error", e);
					throw e;
				}
				if (tc == -1 || tc-- > 0) {
					SLEEP.sleep(every_ms);
				} else {
					break;
				}
			}
		}).start();
		return this;
	}

	public static Integer parseIntValue(String url) throws IOException {
		if (PageSP.L.isDebugEnabled()) {
			PageSP.L.debug("Get Jmx Attribute by path '%s'", url);
		}
		return (Integer) DLD.url2val(url, new ValueOutStream(bytes -> UST.INT(USToken.bw(new String((byte[]) bytes).trim(), "<pre>", "</pre>").trim())));
	}
}
