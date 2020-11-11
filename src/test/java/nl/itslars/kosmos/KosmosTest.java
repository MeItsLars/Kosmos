package nl.itslars.kosmos;

import nl.itslars.kosmos.enums.Ability;
import nl.itslars.kosmos.enums.BlockType;
import nl.itslars.kosmos.enums.Dimension;
import nl.itslars.kosmos.enums.GameRule;
import nl.itslars.kosmos.objects.world.Block;
import nl.itslars.kosmos.objects.world.WorldData;
import org.iq80.leveldb.util.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

class KosmosTest {

    private static final File SAMPLE_WORLD_FILE = new File("./src/test/resources/sample_world");
    private static final File SAMPLE_WORLD_TEST_FILE = new File("./src/test/resources/sample_world_test");

    private WorldData currentTestWorld;

    @BeforeAll
    static void beforeAll() {
        if (SAMPLE_WORLD_TEST_FILE.exists()) {
            FileUtils.deleteRecursively(SAMPLE_WORLD_TEST_FILE);
        }
    }

    @BeforeEach
    void setUp() {
        FileUtils.copyRecursively(SAMPLE_WORLD_FILE, SAMPLE_WORLD_TEST_FILE);
        try {
            currentTestWorld = World.open(SAMPLE_WORLD_TEST_FILE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (currentTestWorld != null) {
            currentTestWorld.close();
            currentTestWorld = null;
        }
        FileUtils.deleteRecursively(SAMPLE_WORLD_TEST_FILE);
    }

    void reOpenTestWorld() {
        try {
            currentTestWorld.close();
            currentTestWorld = World.open(SAMPLE_WORLD_TEST_FILE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEntityCount() {
        AtomicInteger entityCount1 = new AtomicInteger(0);
        AtomicInteger tileEntityCount1 = new AtomicInteger(0);
        currentTestWorld.getChunk(0, 0).ifPresent(chunk -> {
            entityCount1.set(chunk.getEntities().size());
            tileEntityCount1.set(chunk.getTileEntities().size());
        });
        currentTestWorld.save();
        currentTestWorld.unloadChunks();
        reOpenTestWorld();
        AtomicInteger entityCount2 = new AtomicInteger(0);
        AtomicInteger tileEntityCount2 = new AtomicInteger(0);
        currentTestWorld.getChunk(0, 0).ifPresent(chunk -> {
            entityCount2.set(chunk.getEntities().size());
            tileEntityCount2.set(chunk.getTileEntities().size());
        });
        Assertions.assertEquals(entityCount1.get(), entityCount2.get());
        Assertions.assertEquals(tileEntityCount1.get(), tileEntityCount2.get());
    }

    @Test
    void testRemovePlayerData() {
        currentTestWorld.deleteAllPlayers();
        currentTestWorld.save();
        reOpenTestWorld();
        Assertions.assertTrue(currentTestWorld.getPlayers().isEmpty());
    }

    @Test
    void testToggleCheats() {
        currentTestWorld.getLevelDatFile().setCheatsEnabled(false);
        currentTestWorld.save();
        reOpenTestWorld();
        Assertions.assertFalse(currentTestWorld.getLevelDatFile().isCheatsEnabled());
        currentTestWorld.getLevelDatFile().setCheatsEnabled(true);
        currentTestWorld.save();
        reOpenTestWorld();
        Assertions.assertTrue(currentTestWorld.getLevelDatFile().isCheatsEnabled());
    }

    @Test
    void testBooleanGameRules() {
        currentTestWorld.getLevelDatFile().setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        currentTestWorld.getLevelDatFile().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        currentTestWorld.getLevelDatFile().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        currentTestWorld.getLevelDatFile().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        currentTestWorld.save();
        reOpenTestWorld();
        Assertions.assertFalse(currentTestWorld.getLevelDatFile().getBooleanGameRule(GameRule.COMMAND_BLOCK_OUTPUT));
        Assertions.assertFalse(currentTestWorld.getLevelDatFile().getBooleanGameRule(GameRule.SEND_COMMAND_FEEDBACK));
        Assertions.assertFalse(currentTestWorld.getLevelDatFile().getBooleanGameRule(GameRule.DO_DAYLIGHT_CYCLE));
        Assertions.assertTrue(currentTestWorld.getLevelDatFile().getBooleanGameRule(GameRule.DO_IMMEDIATE_RESPAWN));
    }

    @Test
    void testIntGameRules() {
        currentTestWorld.getLevelDatFile().setGameRule(GameRule.SPAWN_RADIUS, 100);
        currentTestWorld.getLevelDatFile().setGameRule(GameRule.MAX_COMMAND_CHAIN_LENGTH, 100000);
        currentTestWorld.save();
        reOpenTestWorld();
        Assertions.assertEquals(100, currentTestWorld.getLevelDatFile().getIntGameRule(GameRule.SPAWN_RADIUS));
        Assertions.assertEquals(100000, currentTestWorld.getLevelDatFile().getIntGameRule(GameRule.MAX_COMMAND_CHAIN_LENGTH));
    }

    @Test
    void testLevelDatAbilities() {
        currentTestWorld.getLevelDatFile().getAbilities().setAbility(Ability.BUILD, false);
        currentTestWorld.getLevelDatFile().getAbilities().setAbility(Ability.FLY_SPEED, 0.3f);
        currentTestWorld.getLevelDatFile().getAbilities().setAbility(Ability.PERMISSIONS_LEVEL, 3);
        currentTestWorld.save();
        reOpenTestWorld();
        Assertions.assertFalse(currentTestWorld.getLevelDatFile().getAbilities().getBooleanAbility(Ability.BUILD));
        Assertions.assertEquals(0.3f, currentTestWorld.getLevelDatFile().getAbilities().getFloatAbility(Ability.FLY_SPEED));
        Assertions.assertEquals(3, currentTestWorld.getLevelDatFile().getAbilities().getIntAbility(Ability.PERMISSIONS_LEVEL));
    }

    @Test
    void testChunkLoading() {
        Assertions.assertTrue(currentTestWorld.getChunk(0, 0).isPresent());
        Assertions.assertFalse(currentTestWorld.getCachedChunks().get(Dimension.OVERWORLD).isEmpty());
        currentTestWorld.unloadChunks();
        Assertions.assertTrue(currentTestWorld.getCachedChunks().get(Dimension.OVERWORLD).isEmpty());
    }

    @Test
    void testSetBlock() {
        int randomX = ThreadLocalRandom.current().nextInt(-15, 0);
        int randomY = ThreadLocalRandom.current().nextInt(0, 16);
        int randomZ = ThreadLocalRandom.current().nextInt(-15, 0);
        Optional<Block> blockOptional1 = currentTestWorld.setBlock(randomX, randomY, randomZ, BlockType.DIAMOND_BLOCK);
        currentTestWorld.save();
        currentTestWorld.unloadChunks();
        reOpenTestWorld();
        Optional<Block> blockOptional2 = currentTestWorld.getBlock(randomX, randomY, randomZ);
        Assertions.assertTrue(blockOptional1.isPresent());
        Assertions.assertTrue(blockOptional2.isPresent());
        Assertions.assertEquals(blockOptional1.get().getName(), blockOptional2.get().getName());
    }

    @Test
    void testFillBlocks() {
        currentTestWorld.fill(-1, 60, 15, -16, 69, 0, BlockType.DIAMOND_BLOCK);
        currentTestWorld.save();
        reOpenTestWorld();
        for (int x = -16; x <= -1; x++) {
            for (int y = 60; y <= 69; y++) {
                for (int z = 0; z <= 15; z++) {
                    Optional<Block> block = currentTestWorld.getBlock(x, y, z);
                    Assertions.assertTrue(block.isPresent());
                    Assertions.assertEquals(block.get().getName(), BlockType.DIAMOND_BLOCK.getNameSpacedId());
                }
            }
        }
    }

    @Test
    void testReplaceBlocks() {
        Set<String> goldBlocks = new HashSet<>();
        for (int x = -16; x <= -1; x++) {
            for (int y = 60; y <= 69; y++) {
                for (int z = 0; z <= 15; z++) {
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        currentTestWorld.setBlock(x, y, z, BlockType.GOLD_BLOCK);
                        goldBlocks.add(x + "," + y + "," + z);
                    } else {
                        currentTestWorld.setBlock(x, y, z, BlockType.DIAMOND_BLOCK);
                    }
                }
            }
        }
        currentTestWorld.replace(-16, 60, 0, -1, 69, 15, BlockType.GOLD_BLOCK, BlockType.EMERALD_BLOCK);
        currentTestWorld.save();
        reOpenTestWorld();
        for (int x = -16; x <= -1; x++) {
            for (int y = 60; y <= 69; y++) {
                for (int z = 0; z <= 15; z++) {
                    Optional<Block> block = currentTestWorld.getBlock(x, y, z);
                    Assertions.assertTrue(block.isPresent());
                    boolean isGold = goldBlocks.contains(x + "," + y + "," + z);
                    if (isGold) {
                        Assertions.assertEquals(block.get().getName(), BlockType.EMERALD_BLOCK.getNameSpacedId());
                    } else {
                        Assertions.assertEquals(block.get().getName(), BlockType.DIAMOND_BLOCK.getNameSpacedId());
                    }
                }
            }
        }
    }
}