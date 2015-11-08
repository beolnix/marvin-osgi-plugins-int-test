package com.beolnix

import com.beolnix.marvin.config.api.ConfigurationProvider
import com.beolnix.marvin.config.api.model.PluginsSettings
import com.beolnix.marvin.plugins.api.IMPlugin
import com.beolnix.marvin.plugins.api.PluginsListener
import com.beolnix.marvin.plugins.api.PluginsManager
import com.beolnix.marvin.plugins.providers.osgi.FelixOSGIContainer
import com.beolnix.marvin.plugins.providers.osgi.OSGIPluginsProvider
import com.jcabi.aether.Aether
import org.apache.log4j.Logger
import org.junit.Test
import org.sonatype.aether.artifact.Artifact
import org.sonatype.aether.repository.RemoteRepository

import java.nio.file.Paths

import static java.nio.file.StandardCopyOption.*;
import org.sonatype.aether.util.artifact.DefaultArtifact

import java.nio.file.Files

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue;

/**
 * Created by beolnix on 08/11/15.
 */
class OSGIPluginsIntegrationTest {

    def logger = Logger.getLogger(OSGIPluginsIntegrationTest.class)

    def getPlugin(String group, String artifact, String version) {
        File local = new File("target/repo");
        if (!local.exists()) {
            local.mkdirs();
        }
        Collection<RemoteRepository> remotes = Arrays.asList(
                new RemoteRepository(
                        "beolnix-snapshots",
                        "default",
                        "http://nexus.beolnix.com/content/repositories/snapshots/"
                )
        );

        Collection<Artifact> deps = new Aether(remotes, local).resolve(
                new DefaultArtifact(group, artifact, "", "jar", version),
                "runtime"
        );
        return deps.get(0).file
    }

    def getConfigurationProv() {
        def configurationProv = [getPluginSettings: {
            PluginsSettings pluginsSettings = new PluginsSettings()
            pluginsSettings.cachePath = "target/cache"
            pluginsSettings.dirPath = "target/dir"
            pluginsSettings.libsPath = "lib"
            pluginsSettings.logsPath = "target/logs"
            pluginsSettings.systemDeployPath = "target/systemDeploy"
            pluginsSettings.pluginsDeployPath = "target/pluginsDeployPath"
            pluginsSettings.tmpPath = "target/tmpPath"
            pluginsSettings
        }] as ConfigurationProvider

        return configurationProv
    }

    def initContainerWith(PluginsListener listener) {
        def configurationProv = getConfigurationProv()

        def pluginsManager = [
                registerPluginsProvider: { pluginsProv ->

                }
        ] as PluginsManager

        def felixOsgiContainer = FelixOSGIContainer.createNewInstance(configurationProv)
        def osgiPluginsProvider = OSGIPluginsProvider.createNewInstance(configurationProv, felixOsgiContainer, pluginsManager)
        osgiPluginsProvider.registerPluginsListener(listener)
        deployPlugin()
    }

    @Test
    public void test() {
        boolean isPluginDeployed = false
        String pluginName = null
        def pluginsListener = [
                deployPlugin : { IMPlugin imPlugin ->
                    isPluginDeployed = true
                    pluginName = imPlugin.getPluginName()
                },
                onError: {String msg, Throwable err ->
                    err.printStackTrace()
                }
        ] as PluginsListener

        initContainerWith(pluginsListener)

        int checkAttempts = 30
        int currentCheckAttempt = 0

        while (currentCheckAttempt < checkAttempts && !isPluginDeployed) {
            sleep(1000)
            currentCheckAttempt += 1
        }

        assertTrue(isPluginDeployed)
        assertNotNull(pluginName)
    }

    def deployPlugin() {
        File pluginFile = getPlugin("com.beolnix.marvin", "newyear-plugin", "0.2-SNAPSHOT")
        File pluginDir = new File("target/pluginsDeployPath")
        pluginDir.mkdirs()
        Files.copy(new FileInputStream(pluginFile), Paths.get("target/pluginsDeployPath/marvin-newyear-plugin-0.2-SNAPSHOT.jar"), REPLACE_EXISTING)
        logger.info("artifact downloaded successfully")
    }
}
