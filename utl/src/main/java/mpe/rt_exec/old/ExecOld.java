package mpe.rt_exec.old;

import mpu.str.JOIN;
import mpe.core.P;
import mpu.IT;
import mpu.str.STR;
import mpe.rt.core.ExecThread;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ExecOld {
    public static final Logger L = LoggerFactory.getLogger(ExecOld.class);
    public final String command;

    private ExecResult result;

    public static List<String>[] execAlternative(String... command) throws IOException, InterruptedException {
        return execAlternative(null, command);
    }

    @Deprecated //See ExecThread
    public static List<String>[] execAlternative(File dir, String... command) throws IOException, InterruptedException {
        IT.notEmpty(command);
        if (command.length == 1) {
            command = command[0].split("\\s+");
        }
        Process proc = dir == null ? ExecThread.buildProcess(command) : ExecThread.buildProcess(dir, command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        List<String> list = new ArrayList<String>();
        List<String> listError = new ArrayList<String>();
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            list.add(s);
        }
        int exitVal = proc.waitFor();
        if (exitVal != 0) {
            while ((s = stdError.readLine()) != null) {
                listError.add(s);
            }
        }
        return new List[]{list, listError};
    }

    public ExecResult getResult() throws IOException, InterruptedException {
        if (result == null) {
            result = execResultAsArgs(command);
        }
        return result;
    }

    public static class ExecResult {
        private final List<String>[] result;

        public List<String> getOutStandart() {
            return result[0];
        }

        public List<String> getOutError() {
            return result[1];
        }

        public ExecResult(List<String>[] result) {
            this.result = result;
        }

        public String getLineWithString(String str) {
            for (String line : getOutStandart())
                if (line.contains(str)) {
                    return line;
                }
            return null;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Exec[" + getOutStandart().size() + "][" + getOutError().size() + "]" + STR.NL);
            sb.append("Standart output >>>\n");
            // getOutStandart().stream().collect(Collectors.joining(QMapBuilder.LINE_SEP);
            sb.append(JOIN.allByNL(getOutStandart()).toString());
            sb.append("\nStandart error >>>\n");
            // getOutError().stream().collect(Collectors.joining(QMapBuilder.LINE_SEP))
            sb.append(JOIN.allByNL(getOutError()).toString());
            return sb.toString();
        }
    }

    @Deprecated
    public ExecOld() {
        this.command = null;
    }

    public ExecOld(String command) {
        this.command = command;
    }

    public ExecOld exec() throws IOException, InterruptedException {
        L.info("Exec::" + command);
        L.info(getResult().toString());
        return this;
    }

    public static ExecResult execResultAsSingleString(List<String> command) throws IOException, InterruptedException {
        return execResultAsArgs(command.toArray(new String[0]));
    }

    public static ExecResult execResultAsSingleString(String command) throws IOException, InterruptedException {
        return execResultAsArgs(StringUtils.split(command, ' '));
    }

    public static ExecResult execResultAsArgs(String... command) throws IOException, InterruptedException {
        List<String>[] res = execAlternative(command);
        return new ExecResult(res);
    }

    public static ExecResult execAndPrintResultCmd(String command) throws IOException, InterruptedException {
        List<String>[] res = execAlternative(command.split("\\s++"));
        P.p(res[0]);
        P.p(res[1]);
        return new ExecResult(res);
    }

    public static ExecResult execAndPrintResult(String... command) throws IOException, InterruptedException {
        List<String>[] res = execAlternative(command);
        P.p(res[0]);
        P.p(res[1]);
        return new ExecResult(res);
    }

}