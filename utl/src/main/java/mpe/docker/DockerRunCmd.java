package mpe.docker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mpu.core.ARG;
import mpu.IT;
import org.apache.tools.ant.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DockerRunCmd {
    public final String img;
    @Getter
    @Setter
    private boolean isNetworkHost, isInterractive;

    @Getter
    @Setter
    private List<String> runImageArgs;

    public static DockerRunCmd of(String img) {
        return new DockerRunCmd(img);
    }

    public DockerRunCmd setRunImageArgsAsArgs(String... runImgArgs) {
        setRunImageArgs(Arrays.asList(runImgArgs));
        return this;
    }

    public DockerRunCmd setRunImageArgsAsSingleCmd(String runImgArgs) {
        if (this.runImageArgs == null) {
            this.runImageArgs = new ArrayList<>();
        }
        setRunImageArgs(StringUtils.split(runImgArgs, ' '));
        return this;
    }

    private List<String> cmd = null;

    public List<String> getCmd(boolean... recreate) {
        if (cmd != null && ARG.isDefNotEqTrue(recreate)) {
            return cmd;
        }
        ArrayList cmd = new ArrayList();
        cmd.add("docker");
        cmd.add("run");
        if (isNetworkHost()) {
            cmd.add("--network");
            cmd.add("host");
        }
        if (isInterractive()) {
            cmd.add("-i");
        }
        cmd.add(IT.NE(img, "set docker image"));
        if (runImageArgs != null) {
            cmd.addAll(runImageArgs);
        }
        return this.cmd=cmd;
    }

    @Override
    public String toString() {
        return getCmd().stream().collect(Collectors.joining(" "));
    }
}
