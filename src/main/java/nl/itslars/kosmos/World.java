package nl.itslars.kosmos;

import lombok.Getter;
import nl.itslars.kosmos.enums.Dimension;
import nl.itslars.kosmos.objects.entity.Player;
import nl.itslars.kosmos.objects.world.ChunkPreset;
import nl.itslars.kosmos.objects.world.WorldData;
import nl.itslars.mcpenbt.NBTUtil;
import nl.itslars.mcpenbt.tags.CompoundTag;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.shaded.guava.io.Files;
import org.iq80.leveldb.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class for representing the LevelDB storage communication for a world.
 */
@Getter
public class World {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");

    // The LevelDB storage
    private final DB db;
    // The level.dat file
    private final File levelDat;
    // The WorldData object
    private WorldData worldData;

    private World(File directory) throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        // Load the LevelDB and level.dat file
        this.db = Iq80DBFactory.factory.open(new File(directory, "db"), options);
        this.levelDat = new File(directory, "level.dat");

        // Load the world
        loadWorld();
    }

    /**
     * Loads all world data into the {@link #worldData} variable:
     * - Players
     * - Player Pointers
     * - Chunks
     */
    private void loadWorld() {
        worldData = new WorldData(this, levelDat);

        DBIterator iterator = db.iterator();
        iterator.seekToFirst();

        // Loop through all entries in the LevelDB database
        Map.Entry<byte[], byte[]> entry;

        try {
            entry = iterator.next();
        } catch (NoSuchElementException e) {
            return;
        }

        while (entry != null) {
            byte[] key = entry.getKey();
            byte[] value = entry.getValue();
            String keyName = new String(key);

            if (keyName.equals("~local_player") || keyName.startsWith("player_server")) {
                // Check if the key represents a local player or server-side player and if so, add it to the world data
                CompoundTag compoundTag = (CompoundTag) NBTUtil.read(false, value);
                Player player = new Player(compoundTag, key);
                worldData.addPlayer(player, key);
            } else if (keyName.startsWith("player")) {
                // Check if the key represents a pointer to a non-local player and if so, add it to the world data
                CompoundTag compoundTag = (CompoundTag) NBTUtil.read(false, value);
                compoundTag.getByName("ServerId").ifPresent(tag -> {
                    byte[] pointer = tag.getAsString().getValue().getBytes();
                    worldData.addPlayerPointer(key, pointer);
                });
            } else if (keyName.matches("^[a-zA-Z]*$") || keyName.startsWith("map_")) {
                // Check if the key represents a data attribute and if so, ignore it
                // This check can NOT be removed, otherwise the next chunk load may trigger an exception
            } else if (key.length >= 8 && key.length <= 14) {
                // Check if the key represents chunk data and if so, add a ChunkPreset to the world data
                int chunkX = ByteBuffer.wrap(key, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                int chunkZ = ByteBuffer.wrap(key, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                Dimension dimension = Dimension.OVERWORLD;
                if (key.length > 10) {
                    int dimensionID = ByteBuffer.wrap(key, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    dimension = Dimension.fromId(dimensionID);
                }

                Dimension finalDimension = dimension;
                worldData.getChunkPresets().get(finalDimension).computeIfAbsent(chunkX, x -> new HashMap<>())
                        .put(chunkZ, new ChunkPreset(worldData, chunkX, chunkZ, finalDimension));
            }

            entry = null;
            while (entry == null) {
                try {
                    entry = iterator.next();
                } catch (NoSuchElementException e) {
                    return;
                } catch (NumberFormatException e) {
                    System.out.println("WARNING: Possibly critical failure. LevelDB failed to parse an entry!");
                    entry = iterator.next();
                }
            }
        }
    }

    /**
     * Closes the connection to the LevelDB storage
     * @throws IOException Thrown if closing the connection failed
     */
    public void close() throws IOException {
        db.close();
    }

    /**
     * Opens a world from the given world directory. Note that this directory is not the location of the database,
     * but the location of the entire world
     * @param directory The world directory
     * @param backupDirectory The directory that this world is backed up into, before opening it
     * @return A newly instantiated WorldData object
     * @throws IOException When opening the world failed
     */
    public static WorldData open(File directory, File backupDirectory) throws IOException {
        // Check if the directory was a proper world directory, based on the levelname.txt file
        File worldName = new File(directory, "levelname.txt");
        if (!worldName.exists()) {
            throw new IllegalArgumentException("Invalid world directory.");
        }

        // Backup world
        if (backupDirectory != null) {
            if (!backupDirectory.exists()) {
                throw new IllegalArgumentException("The provided backup directory does not exist!");
            }
            String levelName = Files.readFirstLine(worldName, Charset.defaultCharset());
            File backupFile = new File(backupDirectory, levelName + "_" + directory.getName() + "_" + DATE_FORMAT.format(Date.from(Instant.now())));
            backupFile.mkdir();
            FileUtils.copyDirectoryContents(directory, backupFile);
        }

        // Initiate and return a new World (and WorldData) object
        return new World(directory).getWorldData();
    }
}
