package acquisition;

import java.util.Collection;

/**
 * Created by ceejay562 on 6/15/2016.
 */
public interface Collector<T, G> {

    Collection<T> mungee(Collection<G> source);

    void save(Collection<T> data);
}
