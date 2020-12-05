[![](https://jitpack.io/v/MeItsLars/Kosmos.svg)](https://jitpack.io/#MeItsLars/Kosmos)
# Kosmos
Kosmos (named after the Greek word for 'world') is a Java API for accessing Minecraft Bedrock (MCPE) worlds.
It can be used to automate world changes/exporting, or to do high-scale world changes that default Minecraft commands do not support.
The current version is operational, but Kosmos is still being worked on. Pull Requests are highly appreciated!

# Licensing & Credits
Kosmos was developed by MeItsLars, while working for the Minecraft Marketplace team 'Team Workbench'.
Team Workbench and I have decided that this project may be used by anyone under the Apache License 2.0.
This means that you are allowed to use this project commercially for free, but you can't claim you made it.
Another important point is that I am not responsible for the effect of this code when you use it.

Kosmos accesses the LevelDB through a modified wrapper library, made by Dain and Tinfoiled.
[More information about LevelDB library can be found here.](https://github.com/MeItsLars/LevelDB-MCPE)

# Contribution
As mentioned, you're free to use this program. However, if you made something useful, or you changed the code to be better or faster, 
I'd highly appreciate it if you contributed it back! This way, Kosmos can grow in functionality, and everyone can profit! That's the idea behind this project :)

If you want to contribute, you can create a pull request. If you have questions, feel free to contact me on Discord (MeItsLars#0183). I probably won't respond to friend requests, but I'm in the [Bedrock OSS Discord](https://discord.gg/XjV87YN), Discord Den, and [Team Workbench public Discord](https://discord.gg/fJvZuqj), so you can message me via there!

# Issues/Feature requests
If you found a mistake in the code, or you want to request a new feature, you can either create a PR, create an Issue on GitHub, or message me on Discord!

# Usage
WARNING: Before using the Kosmos API on any world, I highly recommend you to BACKUP THE WORLD. I can NOT GUARANTEE that nothing will go wrong!

Kosmos was made in Java 8, and can be used using the following Maven dependency (via Jitpack):
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.MeItsLars</groupId>
    <artifactId>Kosmos</artifactId>
    <version>1.1.2</version>
</dependency>
```

Kosmos has a full JavaDoc documentation: https://javadoc.jitpack.io/com/github/MeItsLars/Kosmos/latest/javadoc/index.html

Using Kosmos is really easy, that's what it was made for. First, you need to open the world like this:
```java
File file = new File("PATH/TO/YOUR/WORLD");
File backup = new File("PATH/TO/YOUR/BACKUP/FOLDER");

try (WorldData world = World.open(file, backup)) {
  // The world has been opened! Do your stuff here...
  world.save();
} catch (IOException exception) {
  // Something went wrong while opening the world
}
```
Note that this is a try-with-resources, and therefore automatically calls ``close()`` on the ``WorldData`` object. It is important that you call ``close()`` after you are finished! Also, after performing your world operations, you should call ``save()`` on the ``WorldData`` object. If you don't do this, nothing will happen!

Now that you have the ``WorldData`` object, there are a few methods in there that cover most needs:
## Delete PlayerData
You can delete a single player from a world using:
```java
Set<Player> players = world.getPlayers();
// Get your desired player
Player player = ...
world.deletePlayer(player);
```
You can delete all playerdata at once using:
```java
world.deleteAllPlayers();
```

## Change world settings (cheats, gamerules)
You can easily change world settings via the ``LevelDatFile`` object like this:
```java
LevelDatFile levelDat = world.getLevelDatFile();
levelDat.setCheatsEnabled(false);
levelDat.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
levelDat.setGameRule(GameRule.MAX_COMMAND_CHAIN_LENGTH, 1000);
```

## Performing operations on the actual world
Kosmos also offers quite some functionality for changing the world's blocks.
All methods are accessible in the ``WorldData`` object. For more information per method, please read the documentation!
For all world operations, chunks are loaded and cached. They are only removed from the cache, when one of these methods is called:
```java
// On the WorldData object:
world.unloadChunks();
// On a Chunk object:
chunk.unload();
```
Again, make sure to save the chunk (or the entire world) first, before unloading chunks. Default world operations look like:
```java
world.getBlock(x, y, z);
world.setBlock(x, y, z, block);
world.fill(x1, y1, z1, x2, y2, z2, block);
world.replace(x1, y1, z1, x2, y2, z2, source, target);
```
For more info on all these methods, please read the method documentation in the WorldData class.

## Performance
The performance of this library in general is decent. It's not highly optimized, but it does the job. The only time performance might become an issue, is when filling/replacing a lot of blocks. These methods can probably be optimized quite a bit. If you manage to find a way to increase the speed, please create a Pull Request! :)
