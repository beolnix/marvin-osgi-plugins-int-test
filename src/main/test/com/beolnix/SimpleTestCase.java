package com.beolnix;

import com.jcabi.aether.Aether;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by DAtmakin on 11/6/2015.
 */
public class SimpleTestCase {

    Logger logger = LoggerFactory.getLogger(SimpleTestCase.class);

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

        assertFalse(deps.isEmpty());

        for (Artifact artifact : deps) {
            assertNotNull(artifact);
            assertNotNull(artifact.getFile());
            logger.info("downloaded dep: " + artifact.getFile().getAbsolutePath());
        }
    }
}
