package top.doublewin.core.builder;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 集合对象构造器
 * </p>
 *
 * @author migro
 * @since 2020/10/21 9:43
 */
public class DataBuilder {


    /**
     * Map构造器
     * @param
     * @return
     */
    public static  <K, V> MapBuilder<K, V> map(){
        return new MapBuilder<K,V>();
    }




    public static class MapBuilder<K, V> {

        private Map<K, V> map;
        private boolean underConstruction;

        private MapBuilder() {
            this.map = new HashMap<K,V>();
            underConstruction = true;
        }

        public DataBuilder.MapBuilder<K, V> put(K k, V v) {
            if (!underConstruction) {
                throw new IllegalStateException("Underlying map has already been built");
            }
            map.put(k, v);
            return this;
        }

        public Map<K, V> build() {
            if (!underConstruction) {
                throw new IllegalStateException("Underlying map has already been built");
            }
            underConstruction = false;
            return map;
        }
    }

}
