package org.lerot.gui.widgets;

//import java.awt.Component;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class jswOption extends jswPanel
{

	private static final long serialVersionUID = 1L;
	JPanel box;
	JRadioButton button;
	int compheight = 0;
	boolean vertical = true;

	public jswOption(String text, boolean vertical)
	{
		super(text);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		button = new JRadioButton(text);
		button.setSelected(false);
		add(button);
		this.vertical = vertical;
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		box = new JPanel();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (vertical) box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		else
			box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		add(box);
	}

	public void addComponent(jswPanel c)
	{
		box.add(c);
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (vertical)
		{
			compheight += c.jswGetHeight();
			setPreferredSize(new Dimension(0, compheight));
		} else
		{
			if (c.jswGetHeight() > compheight) compheight = c.jswGetHeight();
			setPreferredSize(new Dimension(0, compheight));
		}
	}

	public void addActionListener(ActionListener al, String command)
	{
		button.addActionListener(al);
		button.setActionCommand(command);
	}

	public String getText()
	{
		return button.getText();
	}

	@Override
	public boolean isSelected()
	{
		return button.isSelected();
	}

	@Override
	public void setEnabled(boolean e)
	{
		int nc = box.getComponentCount();
		for (int i = 0; i < nc; i++)
		{
			Component c = box.getComponent(i);
			c.setEnabled(e);
		}
		button.setEnabled(e);
	}

	public void setSelected()
	{
		setSelected(true);

	}

	public void setSelected(boolean sel)
	{
		button.setSelected(sel);
	}

	public void setText(String string)
	{
		button.setText(string);

	}

}