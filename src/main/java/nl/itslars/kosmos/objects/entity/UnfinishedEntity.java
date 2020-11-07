package nl.itslars.kosmos.objects.entity;

import nl.itslars.mcpenbt.tags.CompoundTag;

/**
 * Class for representing entities that do not have a dedicated class for themselves (yet)
 * If you want to add an entity, feel free to create a PR on Git!
 */
public class UnfinishedEntity extends Entity {

    public UnfinishedEntity(CompoundTag parentCompoundTag) {
        super(parentCompoundTag);
    }
}
