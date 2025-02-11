package mpc.html;

import lombok.Getter;
import mpc.fs.UF;
import mpc.log.L;
import mpu.core.RW;
import mpu.str.Sb;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ReportHtml implements IDocHTML {
	@Getter
	private final Sb sb = new Sb();

	@Override
	public Sb sb() {
		return sb;
	}

	@Override
	public String toString() {
		return sb().toString();
	}

	public void write() {
		Path reportFile = Paths.get("report.html");
		RW.write(reportFile, sb());
		L.info("Report stored " + UF.ln(reportFile));
	}
}
