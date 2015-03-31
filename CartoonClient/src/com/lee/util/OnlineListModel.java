package com.lee.util;

import java.util.Vector;

import javax.swing.AbstractListModel;

public class OnlineListModel extends AbstractListModel<String> {

	private static final long serialVersionUID = 1L;
	private Vector<String> vector;
 
	
	public OnlineListModel(Vector<String> vector) {
		this.vector = vector;
	}


	public int getSize() {
		return vector.size();
	}

	public String getElementAt(int index) {
		return vector.get(index);
	}



}
