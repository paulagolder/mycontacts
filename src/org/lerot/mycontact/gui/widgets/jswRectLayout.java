package org.lerot.mycontact.gui.widgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class jswRectLayout extends jswLayout
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

	public jswRectLayout(int topshift, int leftshift, int bottomshift,
			int rightshift)
	{

		shifts = new Shifts(topshift, leftshift, bottomshift, rightshift);
	}

	public jswRectLayout()
	{
		shifts = new Shifts(0, 0, 0, 0);
	}

	@Override
	public void layoutContainer(Container parent)
	{
		Insets binsets = new Insets(0, 0, 0, 0);
		if (parent instanceof jswPanel)
		{
			((jswPanel) parent).getPanelname();
			Border aborder = ((JComponent) parent).getBorder();
			if (aborder != null) binsets = aborder.getBorderInsets(parent);
		}
		Insets insets = parent.getInsets();
		int x = insets.left + binsets.left + shifts.left;
		int y = insets.top + binsets.top + shifts.top;
		// Dimension parentSize = parent.getSize();
		// Dimension parentSize = parent.getParent().getSize();
		Dimension parentSize = parent.getSize();
		int usableWidth = parentSize.width - insets.left - insets.right
				- binsets.left - binsets.right - shifts.width;
		int availableHeight = parentSize.height - insets.top - insets.bottom
				- binsets.top - binsets.bottom - shifts.height;
		availableHeight = availableHeight - 0;
		for (Component ncomponent : parent.getComponents())
		{
			ncomponent.setBounds(x, y, usableWidth, availableHeight);
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
		if (parent instanceof jswPanel)
		{
			((jswPanel) parent).getPanelname();
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
		if (parent instanceof jswPanel)
		{
			((jswPanel) parent).getPanelname();
			Border aborder = ((JComponent) parent).getBorder();
			if (aborder != null) binsets = aborder.getBorderInsets(parent);
		}
		Dimension parentSize = parent.getParent().getSize();

		int prefheight = parentSize.height - insets.top - insets.bottom
				- binsets.top - binsets.bottom - shifts.height;
		int prefwidth = parentSize.width - insets.left - insets.right
				- binsets.left - binsets.right - shifts.width;
		return new Dimension(prefwidth, prefheight);
	}

}
