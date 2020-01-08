package org.lerot.gui.widgets;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

public class jswToggleButton extends jswPanel
{
	private static final long serialVersionUID = 1L;
	JToggleButton button;

	public jswToggleButton(ActionListener al, String label)
	{
		this(al, label, label);
	}

	public jswToggleButton(ActionListener al, String label, String command)
	{
		super("TB" + label);
		button = new JToggleButton(label);
		button.setFont(new Font("SansSerif", Font.BOLD, 11));
		int l = label.length() * 8 + 30;
		Dimension d = new Dimension(l, 24);
		button.setPreferredSize(d);
		button.setMaximumSize(d);
		button.setMinimumSize(d);
		button.addActionListener(al);
		button.setActionCommand(command);
		add(button);
		applyStyles(button);
		button.setVisible(true);
	}

	public void addActionListener(ActionListener al)
	{
		button.addActionListener(al);

	}

	public void addActionListener(ActionListener al, String actionmessage)
	{
		button.addActionListener(al);
		button.setActionCommand(actionmessage);
	}

	public void setActionCommand(String command)
	{
		button.setActionCommand(command);

	}

}
