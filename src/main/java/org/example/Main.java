package org.example;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        //pullAndBuildSampleApplication();
        try {
            deploySampleApplication();
        } catch (IOException | ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deploySampleApplication() throws IOException, ApiException {
        // Read yaml configuration file, and deploy it
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        // Example yaml file can be found in $REPO_DIR/test-svc.yaml
        File file = new File("configs/service.yaml");
        V1Service yamlSvc = (V1Service) Yaml.load(file);

        // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of
        // CoreV1API
        CoreV1Api api = new CoreV1Api();
        V1Service createResult =
                api.createNamespacedService("default", yamlSvc, null, null, null, null);

        System.out.println(createResult);

        V1Service deleteResult =
                api.deleteNamespacedService(
                        Objects.requireNonNull(yamlSvc.getMetadata()).getName(),
                        "default",
                        null,
                        null,
                        null,
                        null,
                        null,
                        new V1DeleteOptions());
        System.out.println(deleteResult);
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