package mpe.rt_exec;

import java.io.*;

// https://stackoverflow.com/questions/26830617/running-bash-commands-in-java
public class BashExec {

	public static void main(String[] args) throws IOException, InterruptedException {
		executeLines("java -jar /home/dav/Документы/mdmup.mj8.1.jar");
//		executeLines("java -jar /opt/tomcat-9/_etc/mdmup.mj8.jar");
	}
    public static void executeLines(String... lines) throws IOException, InterruptedException {

        File tempScript = createTempScript(lines);

        try {
            ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } finally {
            tempScript.delete();
        }
    }

    public static File createTempScript(String[] lines) throws IOException {
        File tempScript = File.createTempFile("script", null);

        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");

        for (String line : lines)
            printWriter.println(line);

        printWriter.close();

        return tempScript;
    }
}
