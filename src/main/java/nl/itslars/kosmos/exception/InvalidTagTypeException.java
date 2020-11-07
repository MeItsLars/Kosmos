package nl.itslars.kosmos.exception;

import nl.itslars.mcpenbt.enums.TagType;

/**
 * Exception used when someone attempts to convert an NBT {@link nl.itslars.mcpenbt.tags.ListTag} to a Java list
 * in an {@link nl.itslars.kosmos.objects.entity.Entity}, but with an unsupported {@link TagType} value in the list.
 * See {@link nl.itslars.kosmos.objects.entity.Entity} for usage.
 */
public class InvalidTagTypeException extends RuntimeException {

    public InvalidTagTypeException(TagType tagType) {
        super("The given TagType: " + tagType.name() + " is not supported in this context.");
    }
}
