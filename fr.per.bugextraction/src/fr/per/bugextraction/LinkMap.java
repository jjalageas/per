package fr.per.bugextraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LinkMap implements Map{
	private Map<String, Set<String>> linksMap;

	public LinkMap(){
		linksMap = new HashMap<String, Set<String>>();
	}
	
	@Override
	public int size() {
		return linksMap.size();
	}

	@Override
	public boolean isEmpty() {
		return linksMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return linksMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return linksMap.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return linksMap.get(key);
	}

	@Override
	public Object put(Object key, Object value) {
		if (linksMap.containsKey(key))
			linksMap.get(key).add((String) value);
		else {
			Set<String> newSet = new HashSet<>();
			newSet.add((String)value);
			linksMap.put((String)key, newSet); 
		}
		return value;
	}

	@Override
	public Object remove(Object key) {
		return linksMap.remove(key);
	}

	@Override
	public void putAll(Map m) {
		linksMap.putAll(m);
	}

	@Override
	public void clear() {
		linksMap.clear();
	}

	@Override
	public Set keySet() {
		return linksMap.keySet();
	}

	@Override
	public Collection values() {
		return linksMap.values();
	}

	@Override
	public Set entrySet() {
		return linksMap.entrySet();
	}
	
	public boolean valueContains(String key, String singleValue) {
		return linksMap.get(key).contains(singleValue);
	}

	public Map<String, Set<String>> getLinksMap() {
		return linksMap;
	}
	
}
