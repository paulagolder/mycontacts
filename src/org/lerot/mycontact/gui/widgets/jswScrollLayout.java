package org.lerot.mycontact.gui.widgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class jswScrollLayout extends jswLayout
{

	private Shifts shifts;

	class Shifts
	{
		public Shifts(int topshift, int leftshift, int bottomshift,
				int rightshift)
		{
			top = topshift;
			left = leftshift;
			height = bottomshift;
			width = rightshift;
		}
		int top;
		int left;
		int height;
		int width;
	}

	public jswScrollLayout(int topshift, int leftshift, int bottomshift,
			int rightshift)
	{

		shifts = new Shifts(topshift, leftshift, bottomshift, rightshift);
	}

	public jswScrollLayout()
	{
		shifts = new Shifts(0, 0, 0, 0);
	}

	@Override
	public void layoutContainer(Container gparent)
	{
		Container parent = (Container) gparent.getComponent(0);
		boolean trace = true;
		if (parent instanceof jswPanel)
		{
			if (((jswPanel) parent).getTag().equals("trace"))
			{
				System.out.println("tracing");
				trace = true;
			}
		}
		Container p2 = parent;
		while (p2 != null)
		{
			Dimension d = p2.getSize();
			System.out.format(" p2 %s %d %d %n", p2.getClass().getSimpleName(),
					d.width, d.height);
			p2 = p2.getParent();
		}
		Insets binsets = new Insets(0, 0, 0, 0);
		String pname = "fred";
		if (parent instanceof jswPanel)
		{
			pname = ((jswPanel) parent).getPanelname();
			Border aborder = ((JComponent) parent).getBorder();
			if (aborder != null) binsets = aborder.getBorderInsets(parent);
		}
		Insets insets = parent.getInsets();
		int x = insets.left + binsets.left + shifts.left;
		int y = insets.top + binsets.top + shifts.top;
		// Dimension parentSize = parent.getSize();
		// Dimension parentSize = parent.getParent().getSize();
		Dimension parentSize = gparent.getSize();
		int usableWidth = parentSize.width - insets.left - insets.right
				- binsets.left - binsets.right - shifts.width;
		int availableHeight = parentSize.height - insets.top - insets.bottom
				- binsets.top - binsets.bottom - shifts.height;
		availableHeight = availableHeight - 0;
		// System.out.println(" laying out insets=" + insets);
		// System.out.println(" laying out binsets=" + binsets);
		// System.out.println(" laying out parent=" + pname + " : "
		// + parent.getClass().getSimpleName());
		int k = 1;
		Component ncomponent = parent.getComponent(0);
		{
			int useheight = availableHeight;
			Dimension maxsize = ncomponent.getMaximumSize();
			if (maxsize.height < availableHeight) useheight = maxsize.height;
			ncomponent.setBounds(x, y, usableWidth, useheight);
			parent.setBounds(x, y, usableWidth, useheight);
			System.out.println("     " + k + " "
					+ ncomponent.getClass().getSimpleName()
					+ ":: scroll layout " + pname + "  w=" + usableWidth
					+ " h=" + useheight);
			k++;
		}
		// System.out.println(" ");
	}

	@Override
	public Dimension minimumLayoutSize(Container parent)
	{
		parent.getComponentCount();
		Insets binsets = new Insets(0, 0, 0, 0);
		parent.getComponentCount();
		Insets insets = parent.getInsets();
		String pname = "fred";
		if (parent instanceof jswPanel)
		{
			pname = ((jswPanel) parent).getPanelname();
			Border aborder = ((JComponent) parent).getBorder();
			if (aborder != null) binsets = aborder.getBorderInsets(parent);
		}

		int w = 0;
		int h = 0;
		for (Component comp : parent.getComponents())
		{
			if (!comp.isVisible()) continue;
			Dimension d = comp.getMinimumSize();
			if (w < d.width)
			{
				w = d.width;
			}
			if (h < d.height)
			{
				h = d.height;
			}
		}
		return new Dimension(w + insets.left + insets.right + binsets.left
				+ binsets.right, h + insets.top + insets.bottom + binsets.top
				+ binsets.bottom);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent)
	{
		Insets binsets = new Insets(0, 0, 0, 0);
		parent.getComponentCount();
		Insets insets = parent.getInsets();
		String pname = "fred";
		if (parent instanceof jswPanel)
		{
			pname = ((jswPanel) parent).getPanelname();
			Border aborder = ((JComponent) parent).getBorder();
			if (aborder != null) binsets = aborder.getBorderInsets(parent);
		}

		int w = 0;
		int h = 0;
		Dimension parentSize = parent.getParent().getSize();

		int prefheight = parentSize.height - insets.top - insets.bottom
				- binsets.top - binsets.bottom - shifts.height;
		int prefwidth = parentSize.width - insets.left - insets.right
				- binsets.left - binsets.right - shifts.width;
		return new Dimension(prefwidth, prefheight);
	}

}
