package org.lerot.mycontact.gui.widgets;

//import java.awt.Component;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;

public class jswHorizontalPanel extends jswContainer
{

	private static final long serialVersionUID = 1L;

	public jswHorizontalPanel()
	{
		super("HP:");
		setLayout(new jswHorizontalLayout());
		// setBorder(setLineBorder(Color.pink, 2));
	}

	public jswHorizontalPanel(String title, boolean titledborder)
	{
		super("HP:" + title);
		setLayout(new jswHorizontalLayout());
		// if(title.length()>0)
		{
			setName(title);
			if (titledborder) setBorder(setcborder(title));
		}
	}

	public void addComponent(jswPanel c)
	{
		super.add(c);
		int w = c.jswGetWidth();
		cwidth += w;
		if (c.jswGetHeight() > cheight) cheight = c.jswGetHeight();
		c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		c.setPreferredSize(new Dimension(cwidth, cheight));
	}

	public void setEnabled(boolean e)
	{
		int nc = getComponentCount();
		for (int i = 0; i < nc; i++)
		{
			Component c = getComponent(i);
			c.setEnabled(e);
		}

	}
}
