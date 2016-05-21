package cz.muni.fi.pv243.spatialtracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import lombok.AllArgsConstructor;

public class EnumMapper<T extends Enum<T>> {

    private Map<T, Integer> enumToId;
    private Map<Integer, T> idToEnum;

    public EnumMapper(Mapping<T>... mappings) {
        this.enumToId = new HashMap<>();
        this.idToEnum = new HashMap<>();
        for (Mapping<T> map : mappings) {
            this.enumToId.put(map.enumConst, map.id);
            this.idToEnum.put(map.id, map.enumConst);
        }
    }

    public T fromId(final int id) {
        T mapped = this.idToEnum.get(id);
        if (mapped != null) {
            return mapped;
        }
        throw new IllegalArgumentException("Unknown id: " + id);
    }

    public List<T> fromId(final List<Integer> ids) {
        return ids.stream().map(this::fromId).collect(toList());
    }

    public int toId(final T enumConst) {
        Integer id = this.enumToId.get(enumConst);
        if (id != null) {
            return id;
        }
        throw new IllegalArgumentException("Enum const: " + enumConst);
    }

    public List<Integer> toId(final List<T> enumConsts) {
        return enumConsts.stream().map(this::toId).collect(toList());
    }

    @AllArgsConstructor(access = PRIVATE)
    public static class Mapping<T extends Enum<T>> {

        private final int id;
        private final T enumConst;

        public static <T extends Enum<T>> Mapping<T> map(
                final int id, final T enumConst) {
            return new Mapping<>(id, enumConst);
        }
    }
}
