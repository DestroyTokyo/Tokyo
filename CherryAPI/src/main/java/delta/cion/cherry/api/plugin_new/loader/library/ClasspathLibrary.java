package delta.cion.cherry.api.plugin_new.loader.library;

public interface ClasspathLibrary {

    void register(LibraryStore store) throws LibraryLoadingException;
}
