package udav_net_exp.utils_check_domain;

import mpu.X;
import mpu.core.RW;
import mpc.log.L;
import mpu.str.SPLIT;
import udav_net_client.AHttp;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class ShowTopLevelDomain {

	public static void main(String[] args) throws IOException {
		String url = "https://data.iana.org/TLD/tlds-alpha-by-domain.txt";

		String get = AHttp.GET(url);

		List<String> all = SPLIT.allByNL(get);

		write(all, 4);
		write(all, 3);
		write(all, 2);
		write(all, 1);

//		P.exit(write2file);

	}

	private static void write(List<String> all, int len) throws IOException {
		all = all.stream().filter(l -> l.length() == len).collect(Collectors.toList());
		if (X.empty(all)) {
			L.warn("Domain with length {} not found", len);
			return;
		}
		Path write2file = Paths.get("top-level-domain." + len + ".txt");
		RW.write_(write2file, all);
	}
}
