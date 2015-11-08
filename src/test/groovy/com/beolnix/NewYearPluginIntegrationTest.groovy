package com.beolnix

import com.beolnix.marvin.im.api.IMSessionManager
import com.beolnix.marvin.im.api.model.IMIncomingMessageBuilder
import com.beolnix.marvin.im.api.model.IMOutgoingMessage
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.sonatype.aether.repository.RemoteRepository

import static org.junit.Assert.assertNotNull;

/**
 * Created by beolnix on 09/11/15.
 */
class NewYearPluginIntegrationTest extends OSGIPluginsIntegrationTest {

    private static List<RemoteRepository> remotes = Arrays.asList(
            new RemoteRepository(
                    "beolnix-snapshots",
                    "default",
                    "http://nexus.beolnix.com/content/repositories/snapshots/"
            )
    );

    NewYearPluginIntegrationTest() {
        super(remotes, "com.beolnix.marvin", "newyear-plugin", "0.2-SNAPSHOT")
    }

    @Test
    public void test() {
        IMOutgoingMessage outMsg
        def imSessionManager = [
                sendMessage: { msg ->
                    outMsg = msg
                }
        ] as IMSessionManager

        imPlugin.setIMSessionManager(imSessionManager)
        imPlugin.process(getHelpMsg())

        assertNotNull(outMsg)
    }

    def getHelpMsg() {
        new IMIncomingMessageBuilder()
                .withAutor("Sam")
                .withBotName("testBot")
                .withCommand(true)
                .withCommandName("help")
                .withCommandAttributes(null)
                .withCommandSymbol("!")
                .withConference(true)
                .withConferenceName("test_conf")
                .withProtocol("IRC")
                .withRawMessageBody("!help")
                .build()
    }

}
