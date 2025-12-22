package agh.model.util;

import java.util.*;

public class MultiValueHashMap<K, V> {
    private final HashMap<K, ArrayList<V>> map = new HashMap<>();

    public void put(K key, V value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }
//    zwraca osobna liste ktora nie jest powiazana z mapa
//    public List<V> get(K key) {
//        return map.getOrDefault(key, new ArrayList<>());
//    }

    // jak nie istnieje zwraca null
    public List<V> get(K key) {
        return map.get(key);
    }

    public void remove(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) return;

        list.remove(value);
        if (list.isEmpty()) {
            map.remove(key);
        }
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public Set<K> keySet(){
        return map.keySet();
    }

    public void remove(K key) {
        map.remove(key);
    }

    public Collection<ArrayList<V>> values() {
        return map.values();
    }

    public List<V> computeIfAbsent(K key) {
        return map.computeIfAbsent(key, k -> new ArrayList<>());
    }

}





