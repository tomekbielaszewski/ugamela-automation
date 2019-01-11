package pl.grizwold;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class State {
    private static final Map<String, Object> state = new HashMap<>();

    public <T> Optional<T> get(String key, Class<T> clazz) {
        return Optional.ofNullable(state.get(key))
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    public State put(String key, Object value) {
        state.put(key, value);
        return this;
    }

    public State put(Pair pair) {
        return this.put(pair.key, pair.value);
    }

    public class Pair {
        public String key;
        public String value;
    }
}
