package nl.itslars.kosmos.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.itslars.kosmos.enums.GameRule;
import nl.itslars.mcpenbt.tags.ByteTag;
import nl.itslars.mcpenbt.tags.CompoundTag;
import nl.itslars.mcpenbt.tags.IntTag;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class LevelDatFile {

    private final File file;
    private final CompoundTag parentCompoundTag;

    public void setGameRule(GameRule gameRule, int value) {
        parentCompoundTag.change(gameRule.getLevelDatName(), new IntTag(gameRule.getLevelDatName(), value));
    }

    public void setGameRule(GameRule gameRule, boolean value) {
        parentCompoundTag.change(gameRule.getLevelDatName(), new ByteTag(gameRule.getLevelDatName(), (byte) (value ? 1 : 0)));
    }

    public void setCheatsEnabled(boolean value) {
        parentCompoundTag.change("commandsEnabled", new ByteTag("commandsEnabled", (byte) (value ? 1 : 0)));
    }
}
