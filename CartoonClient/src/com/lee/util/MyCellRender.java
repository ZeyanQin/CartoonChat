package com.lee.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("serial")
public class MyCellRender extends JLabel implements ListCellRenderer<String>{

	public Component getListCellRendererComponent(JList<? extends String> list,
			String value, int index, boolean isSelected, boolean cellHasFocus) {
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));// add empty border
		
		if (value != null) {
			setText(value.toString());
			setIcon(new ImageIcon("images/1.jpg"));
		}
		if (isSelected) {
			setBackground(Color.BLUE);
			setForeground(Color.BLACK);
		} else {
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
		}
		setEnabled(list.isEnabled());
		setFont(new Font("andylee", Font.ROMAN_BASELINE, 13));
		setOpaque(true);
		
		return this;
	}

}
