package nl.itslars.kosmos.objects.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.itslars.mcpenbt.tags.CompoundTag;

/**
 * Class used to represent all tile entities. This class should become abstract in the future.
 * Every in-game tile entity that is stored in a chunk, is converted to an object that extends this class.
 * Right now, there are no directly implemented subclasses.
 * If you want to add a tile entity, feel free to do a PR on Git!
 */
@RequiredArgsConstructor
@Getter
public class TileEntity {

    private final CompoundTag parent;

}
