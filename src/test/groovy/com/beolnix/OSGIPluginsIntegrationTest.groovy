package com.beolnix

import com.beolnix.marvin.config.api.ConfigurationProvider
import com.beolnix.marvin.config.api.model.PluginsSettings
import com.beolnix.marvin.plugins.api.IMPlugin
import com.beolnix.marvin.plugins.api.PluginsListener
import com.beolnix.marvin.plugins.api.PluginsManager
import com.beolnix.marvin.plugins.api.PluginsProvider
import com.beolnix.marvin.plugins.providers.osgi.FelixOSGIContainer
import com.beolnix.marvin.plugins.providers.osgi.OSGIPluginsProvider
import com.jcabi.aether.Aether
import org.apache.log4j.Logger
import org.junit.Before
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
abstract class OSGIPluginsIntegrationTest {

    private final List<RemoteRepository> repos;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private Integer artifactDeployTimeoutInSecs = 60;

    protected PluginsProvider pluginsProvider
    protected String pluginDeployPath
    protected IMPlugin imPlugin

    public OSGIPluginsIntegrationTest(List<RemoteRepository> repos, String groupId, String artifactId, String version) {
        this.repos = repos
        this.groupId = groupId
        this.artifactId = artifactId
        this.version = version
        this.pluginDeployPath = "target/${artifactId}_deployPath"
    }

    public OSGIPluginsIntegrationTest(List<RemoteRepository> repos, String groupId, String artifactId, String version, Integer artifactDeployTimeoutInSecs) {
        this(repos, groupId, artifactId, version)
        this.artifactDeployTimeoutInSecs = artifactDeployTimeoutInSecs
    }

    def logger = Logger.getLogger(OSGIPluginsIntegrationTest.class)

    @Before
    public void initialization() {
        def configurationProv = getConfigurationProv()

        def pluginsManager = [
                registerPluginsProvider: {}
        ] as PluginsManager

        def felixOsgiContainer = FelixOSGIContainer.createNewInstance(configurationProv)
        this.pluginsProvider = OSGIPluginsProvider.createNewInstance(configurationProv, felixOsgiContainer, pluginsManager)

        boolean isPluginDeployed = false
        String pluginName = null

        def pluginsListener = [
                deployPlugin : { IMPlugin imPlugin ->
                    isPluginDeployed = true
                    pluginName = imPlugin.getPluginName()
                    this.imPlugin = imPlugin
                },
                onError: {String msg, Throwable err ->
                    err.printStackTrace()
                }
        ] as PluginsListener

        this.pluginsProvider.registerPluginsListener(pluginsListener)

        deployPlugin()

        int currentCheckAttempt = 0

        while (currentCheckAttempt < this.artifactDeployTimeoutInSecs && !isPluginDeployed) {
            sleep(1000)
            currentCheckAttempt += 1
        }

        assertTrue(isPluginDeployed)
        assertNotNull(pluginName)
    }

    def File getPlugin() {
        File local = new File("target/repo");
        if (!local.exists()) {
            local.mkdirs();
        }

        Collection<Artifact> deps = new Aether(this.repos, local).resolve(
                new DefaultArtifact(this.groupId, this.artifactId, "", "jar", this.version),
                "runtime"
        );


        for (Artifact artifact : deps) {
            if (artifact.getArtifactId().equals(this.artifactId)) {
                return artifact.file
            }
        }

        return null;
    }

    def getConfigurationProv() {
        def configurationProv = [getPluginSettings: {
            PluginsSettings pluginsSettings = new PluginsSettings()
            pluginsSettings.cachePath = "target/${this.artifactId}_cache"
            pluginsSettings.dirPath = "target/dir"
            pluginsSettings.libsPath = "lib"
            pluginsSettings.logsPath = "target/logs"
            pluginsSettings.systemDeployPath = "target/systemDeploy"
            pluginsSettings.pluginsDeployPath = this.pluginDeployPath
            pluginsSettings.tmpPath = "target/tmpPath"
            pluginsSettings
        }] as ConfigurationProvider

        return configurationProv
    }



    def deployPlugin() {
        File pluginFile = getPlugin()
        File pluginDir = new File(this.pluginDeployPath)
        pluginDir.mkdirs()
        Files.copy(new FileInputStream(pluginFile), Paths.get("${this.pluginDeployPath}/${this.artifactId}-${this.version}.jar"), REPLACE_EXISTING)
        logger.info("artifact downloaded successfully")
    }
}
