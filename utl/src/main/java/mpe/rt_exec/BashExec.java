package mpe.rt_exec;

import java.io.*;

// https://stackoverflow.com/questions/26830617/running-bash-commands-in-java
public class BashExec {

    public static void execute(String... command) throws IOException, InterruptedException {

        File tempScript = createTempScript(command);

        try {
            ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } finally {
            tempScript.delete();
        }
    }

    public static File createTempScript(String[] command) throws IOException {
        File tempScript = File.createTempFile("script", null);

        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");

        for (String line : command)
            printWriter.println(line);

        printWriter.close();

        return tempScript;
    }
}
