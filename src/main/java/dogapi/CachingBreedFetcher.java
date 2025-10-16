package dogapi;

import okhttp3.OkHttpClient;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private int callsMade = 0;
    private final Map<String, List<String>> subBreedCache = new HashMap<>();
    private final BreedFetcher fetcher;


    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (subBreedCache.containsKey(breed)) {
            return subBreedCache.get(breed);
        }

        try {
            List<String> subBreeds = fetcher.getSubBreeds(breed);
            subBreedCache.put(breed, subBreeds);
            callsMade++;
            return subBreeds;
        }
        catch (BreedNotFoundException e) {
            callsMade++;
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}