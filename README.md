## Project description
Integration tests for OSGI plugins [newyear-plugin](https://github.com/beolnix/marvin-newyear-plugin/) and [echo-plugin](https://github.com/beolnix/marvin-echo-plugin/) of [marvin](https://github.com/beolnix/marvin/) bot.
Project can be used to create integration tests for your own OSGI plugins of [marvin](https://github.com/beolnix/marvin/) bot.

## Project details
| Version | State | Source code | Binaries |
| --- | --- | --- | --- |
| 0.2 | Stable | [0.2-release](https://github.com/beolnix/marvin-osgi-plugins-int-test/releases/tag/0.2-release) | [marvin-osgi-plugins-int-test-0.2.jar](http://nexus.beolnix.com/service/local/repositories/releases/content/com/beolnix/marvin/marvin-osgi-plugins-int-test/0.2/marvin-osgi-plugins-int-test-0.2.jar) |
| 0.3-SNAPSHOT | In dev | [master](https://github.com/beolnix/marvin-osgi-plugins-int-test) |  |

## Requirements
* JDK 8
* Maven 3.1.1
* Groovy 2.4.4

## Build from source
Just execute the following command and may the force be with you:
```
mvn clean install
``` 

## Usage 
Just add maven dependency
```xml
<dependency>
    <groupId>com.beolnix.marvin</groupId>
    <artifactId>osgi-plugins-int-test</artifactId>
    <version>0.1</version>
    <scope>test</scope>
</dependency>
```
And extend OSGIPluginsIntegrationTest class and implement test. Simple example:
```groovy
class EchoPluginIntegrationTest extends OSGIPluginsIntegrationTest {

    private static List<RemoteRepository> remotes = Arrays.asList(
            new RemoteRepository(
                    "beolnix-snapshots",
                    "default",
                    "http://nexus.beolnix.com/content/repositories/snapshots/"
            )
    );

    EchoPluginIntegrationTest() {
        super(remotes, "com.beolnix.marvin", "echo-plugin", "0.2-SNAPSHOT")
    }

    @Test
    public void test() {
        IMOutgoingMessage[] outMsges
        def imSessionManager = [
                sendMessage: { msg ->
                    outMsges = msg
                }
        ] as IMSessionManager

        imPlugin.setIMSessionManager(imSessionManager)
        imPlugin.process(getHelpMsg())

        assertNotNull(outMsges)
        assertTrue(outMsges.length > 0)
    }

    def getHelpMsg() {
        new IMIncomingMessageBuilder()
                .withAutor("Sam")
                .withBotName("testBot")
                .withCommand(false)
                .withCommandSymbol("!")
                .withConferenceName("test_conf")
                .withProtocol("IRC")
                .withRawMessageBody("yo")
                .build()
    }

}
```

## More examples
Please find more examples in test `src/test` folder of this project