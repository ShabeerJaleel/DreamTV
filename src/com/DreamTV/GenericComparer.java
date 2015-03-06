package com.DreamTV;


public class GenericComparer implements Comparable<Object>{

	public int compareTo(Object another) {
		
		return toString().compareToIgnoreCase(another.toString());
	}

}
