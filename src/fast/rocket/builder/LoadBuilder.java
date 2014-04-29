package fast.rocket.builder;

public interface LoadBuilder<T> {
    /**
     * Load an uri.
     * @param uri Uri to load. This may be a http(s), file, or content uri.
     * @return
     */
    public T load(String uri);

    /**
     * Load an url using the given an HTTP method such as GET,POST,PUT or DEELETE.
     * @param method HTTP method such as GET or POST.
     * @param url Url to load.
     * @return
     */

    public T load(int method, String url);
}
