package nl.itslars.kosmos.exception;

/**
 * Exception thrown by {@link nl.itslars.kosmos.objects.entity.Entity} (sub-)class(es), when support for certain
 * NBT values is not yet implemented.
 */
public class NotYetImplementedException extends RuntimeException {

    public NotYetImplementedException() {
        super("This operation is not yet implemented. Create an issue or pull request on GitHub.");
    }
}
