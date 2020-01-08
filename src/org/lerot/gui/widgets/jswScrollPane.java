package org.lerot.gui.widgets;

import java.awt.Color;

import javax.swing.JScrollPane;

import org.lerot.mycontact.mcdb;

public class jswScrollPane extends jswPanel
{

	private static final long serialVersionUID = 1L;
	JScrollPane window;
	private jswPanel target;
	int xshift = 0;
	int yshift = 0;

	public jswScrollPane(jswPanel atarget, int xshift, int yshift)
	{
		super("scrollpane");
		target = atarget;	
		jswHorizontalLayout arlayout = new jswHorizontalLayout();
		this.setLayout(arlayout);
		jswStyle scrollstyle = jswStyles.getStyles("allstyles").getStyle("jswScrollPaneStyles");
		Color bcolor = scrollstyle.getColor("backgroundColor", Color.BLUE);
		setBackground(bcolor);
		window = new JScrollPane(target);
		add(" FILLW ", window);
	}

	public jswScrollPane(jswPanel atarget)
	{
		super("scrollpane");
		target = atarget;
		jswHorizontalLayout arlayout = new jswHorizontalLayout();
		this.setLayout(arlayout);
		window = new JScrollPane(target);
		add(" FILLW ", window);
	}

	public void setMyBounds(int x, int y, int w, int h)
	{
		int deltaw =12; //ALLOW FOR SCROLLBAR
		window.setBounds(x, y, w-x-deltaw, h-y);
		this.setBounds(x, y, w-x-deltaw, h-y);
		// System.out.format(" setting bounds %d %d %d %d %n ", x, y, w, h);
	}

	public void setHorizontalScrollBarPolicy(int policy)
	{
		window.setHorizontalScrollBarPolicy(policy);
	}

	public void setVerticalScrollBarPolicy(int policy)
	{
		window.setVerticalScrollBarPolicy(policy);
	}

}
