package com.beolnix;

import com.jcabi.aether.Aether;
import org.junit.Test;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by DAtmakin on 11/6/2015.
 */
public class SimpleTestCase {

    @Test
    public void resolveTestCase() throws Exception {
        File local = new File("target/tmp");
        if (!local.exists()) {
            local.mkdirs();
        }
        RemoteRepository repo = new RemoteRepository(
                "oss.sonatype.org",
                "default",
                "https://oss.sonatype.org/content/repositories/snapshots/"
        );
//        repo.setAuthentication(new Authentication("guest", "guest"));


        Collection<RemoteRepository> remotes = Arrays.asList(repo);
        Collection<Artifact> deps = new Aether(remotes, local).resolve(
                new DefaultArtifact("com.jcabi", "jcabi-aether", "", "jar", "1.0-SNAPSHOT"),
                "runtime"
        );
        for (Artifact artifact : deps) {
            System.out.println("\n\n\n" + artifact.getFile().getAbsolutePath() + "\n\n\n");
        }
    }
}
