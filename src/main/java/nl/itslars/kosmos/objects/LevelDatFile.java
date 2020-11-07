package nl.itslars.kosmos.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.itslars.kosmos.enums.GameRule;
import nl.itslars.mcpenbt.tags.ByteTag;
import nl.itslars.mcpenbt.tags.CompoundTag;
import nl.itslars.mcpenbt.tags.IntTag;
import nl.itslars.mcpenbt.tags.Tag;

import java.io.File;
import java.util.Optional;

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

    public boolean getBooleanGameRule(GameRule gameRule) {
        Optional<Tag> tagOptional = parentCompoundTag.getByName(gameRule.getLevelDatName());
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("The given GameRule is not in the level.dat");

        return tagOptional.get().getAsByte().getValue() == (byte) 1;
    }

    public int getIntGameRule(GameRule gameRule) {
        Optional<Tag> tagOptional = parentCompoundTag.getByName(gameRule.getLevelDatName());
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("The given GameRule is not in the level.dat");

        return tagOptional.get().getAsInt().getValue();
    }

    public void setCheatsEnabled(boolean value) {
        parentCompoundTag.change("commandsEnabled", new ByteTag("commandsEnabled", (byte) (value ? 1 : 0)));
    }

    public boolean isCheatsEnabled() {
        Optional<Tag> tagOptional = parentCompoundTag.getByName("commandsEnabled");
        if (!tagOptional.isPresent()) throw new IllegalArgumentException("Cheat settings were not found in leve.dat");

        return tagOptional.get().getAsByte().getValue() == (byte) 1;
    }
}
