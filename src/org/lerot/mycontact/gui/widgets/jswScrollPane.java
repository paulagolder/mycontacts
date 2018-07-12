package org.lerot.mycontact.gui.widgets;

import javax.swing.JScrollPane;

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
		// setBorder(setLineBorder(Color.red, 4));
		// jswScrollLayout arlayout = new jswScrollLayout(yshift, xshift, 0, 0);
		// jswVerticalLayout arlayout = new jswVerticalLayout();// yshift,
		jswHorizontalLayout arlayout = new jswHorizontalLayout();
		// xshift,
		// 0, 0);
		this.setLayout(arlayout);
		// this.setTag("trace");
		window = new JScrollPane(target);
		// window.setBorder(setLineBorder(Color.yellow, 4));
		add(" FILLW ", window);
		// add(window);
	}

	public jswScrollPane(jswPanel atarget)
	{
		super("scrollpane");
		target = atarget;

		// jswRectLayout arlayout = new jswRectLayout();
		// jswVerticalLayout arlayout = new jswVerticalLayout();
		// jswScrollLayout arlayout = new jswScrollLayout(0, 0, 0, 0);
		jswHorizontalLayout arlayout = new jswHorizontalLayout();
		// setBorder(setLineBorder(Color.red, 4));
		this.setLayout(arlayout);
		// this.setTag("trace");
		window = new JScrollPane(target);
		// window.setBorder(setLineBorder(Color.yellow, 4));
		add(" FILLW ", window);
		// add(window);
	}

	public void setMyBounds(int x, int y, int w, int h)
	{
		window.setBounds(x, y, w, h);
		this.setBounds(x, y, w, h);
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
