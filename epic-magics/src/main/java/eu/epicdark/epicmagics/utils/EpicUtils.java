package eu.epicdark.epicmagics.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EpicUtils {
	
	public static <T> ArrayList<T> cloneList(ArrayList<T> list) {
		return new ArrayList<T>(list);
	}

	@SuppressWarnings("unchecked")
	public static <T> HashSet<T> cloneHashSet(HashSet<T> set) {
		return (HashSet<T>) set.clone();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<T> cloneSet(Set<T> set) {
		return (Set<T>) ((HashSet<T>) set).clone();
	}
	
}
