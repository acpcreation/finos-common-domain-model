package org.isda.cdm.filter;

import com.google.common.collect.ImmutableMap;
import com.regnosys.rosetta.common.util.HierarchicalPath;
import org.isda.cdm.Contract;
import java.util.*;
import java.util.function.Predicate;

/**
 * Decides whether a given rosettaKey should be added to eventEffects based on path to the rosettaKey.
 */
public class EventEffectPathFilter {

    static final PathClass EFFECTED_CONTRACT_PATH = new PathClass(HierarchicalPath.valueOf("eventEffect.effectedContract"), Contract.class);
    static final PathClass CONTRACT_PATH = new PathClass(HierarchicalPath.valueOf("eventEffect.contract"), Contract.class);

    static final List<String> EFFECTED_CONTRACT_REQUIRED_PATHS = Arrays.asList("primitive", "before");

    /**
     * Map containing predicates to determine whether a given rosettaKeyPath should be added to event effect path.
     */
    private static final Map<PathClass, Predicate<HierarchicalPath>> PATH_FILTERS = ImmutableMap.<PathClass, Predicate<HierarchicalPath>>builder()
            .put(EFFECTED_CONTRACT_PATH, (rosettaKeyPath) -> rosettaKeyPath.allElementPaths().containsAll(EFFECTED_CONTRACT_REQUIRED_PATHS))
            .put(CONTRACT_PATH, (rosettaKeyPath) -> !rosettaKeyPath.allElementPaths().containsAll(EFFECTED_CONTRACT_REQUIRED_PATHS))
            .build();

    /**
     * @return true to indicate that rosettaKey at the given rosettaKeyPath should be added to eventEffectsPath
     */
    public static boolean test(HierarchicalPath eventEffectsPath, Class<?> forClass, HierarchicalPath rosettaKeyPath) {
        return Optional.ofNullable(PATH_FILTERS.get(new PathClass(eventEffectsPath, forClass)))
                .map(predicate -> predicate.test(rosettaKeyPath))
                .orElse(true);
    }

    /**
     * Stores path and class
     */
    static class PathClass {
        private final HierarchicalPath path;
        private final Class<?> clazz;

        PathClass(HierarchicalPath path, Class<?> clazz) {
            this.path = path;
            this.clazz = clazz;
        }

        public HierarchicalPath getPath() {
            return path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathClass pathClass = (PathClass) o;
            return Objects.equals(path.allElementPaths(), pathClass.path.allElementPaths()) &&
                    Objects.equals(clazz, pathClass.clazz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, clazz);
        }
    }
}