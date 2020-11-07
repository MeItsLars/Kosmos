package nl.itslars.kosmos.exception;

/**
 * Thrown when an NBT tag is retrieved from an {@link nl.itslars.kosmos.objects.entity.Entity}, but when the
 * entity did not have the corresponding tag.
 */
public class NoSuchTagException extends RuntimeException {

    public NoSuchTagException(String name) {
        super("The entity did not have the given tag: '" + name + "'");
    }
}
