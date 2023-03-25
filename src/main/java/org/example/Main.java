package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        pullAndBuildSampleApplication();
        
    }

    private static void pullAndBuildSampleApplication() {
        try {
            Path cloneDirectory = Path.of("/home/user/stuff/kubernetes-config/kubernetes-config/src/main/resources/spring-petclinic");
            if (!Files.exists(cloneDirectory)) {
                String repoUrl = "git@github.com:spring-projects/spring-petclinic.git";
                Git.gitClone(cloneDirectory, repoUrl);
            }
            ProcessBuilder builder = new ProcessBuilder("./mvnw package");
            builder.directory(new File(cloneDirectory.toString()));
            Process pro = builder.start();
            System.out.println("Starting mvn install");
            pro.waitFor();
            System.out.println("Finished running mvn");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}