package com.beolnix;

import com.jcabi.aether.Aether;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class App {
    public static void main(String[] args) throws DependencyResolutionException {
        File local = new File("target/repo");
        if (!local.exists()) {
            local.mkdirs();
        }
        Collection<RemoteRepository> remotes = Arrays.asList(
                new RemoteRepository(
                        "maven-central",
                        "default",
                        "https://repo1.maven.org/maven2/"
                )
        );

        Collection<Artifact> deps = new Aether(remotes, local).resolve(
                new DefaultArtifact("junit", "junit-dep", "", "jar", "4.10"),
                "runtime"
        );
        System.out.println(deps);

    }
}
