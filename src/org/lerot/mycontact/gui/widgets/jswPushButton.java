package org.lerot.mycontact.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

public class jswPushButton extends jswPanel
{
	private static final long serialVersionUID = 1L;
	JToggleButton button;

	public jswPushButton(ActionListener al, String label)
	{
		this(al, label, label);
	}

	public jswPushButton(ActionListener al, String label, String command)
	{
		super(label);
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
		setStyle();
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

	public String getText()
	{
		// TODO Auto-generated method stub
		return button.getText();
	}

	public void setActionCommand(String command)
	{
		button.setActionCommand(command);

	}

	public void setSelected()
	{
		// button.t

	}

	public void setSelected(boolean b)
	{
		button.setSelected(b);

	}

	public void setStyle()
	{

		Font sfont = styles.getFont();
		button.setFont(sfont);
		button.setBorder(styles.getBorder());
		button.setForeground(styles.getColor("foregroundColor", Color.blue));
		button.setBackground(styles.getColor("backgroundColor", Color.red));

	}

}
