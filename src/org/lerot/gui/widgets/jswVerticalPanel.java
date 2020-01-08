package org.lerot.gui.widgets;

import java.awt.Component;

public class jswVerticalPanel extends jswContainer
{

	private static final long serialVersionUID = 1L;

	public jswVerticalPanel()
	{
		super("VP:");
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setLayout(new jswVerticalLayout());
		// setBorder(setLineBorder(1));
	}

	public jswVerticalPanel(String title, boolean titledborder)
	{
		super("VP:" + title);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setLayout(new jswVerticalLayout());
		// if(title.length()>0)
		{
			setName(title);
			if (titledborder) setBorder(setcborder(title));
		}
	}

	public void addComponent(jswPanel c)
	{
		super.add(c);
		//int h = c.jswGetHeight();
		//cheight += h;
		//if (c.jswGetWidth() > cwidth) cwidth = c.jswGetWidth();
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		//c.setPreferredSize(new Dimension(cwidth, cheight));
	}

	public void setInsets()
	{
		// TODO Auto-generated method stub

	}

}
