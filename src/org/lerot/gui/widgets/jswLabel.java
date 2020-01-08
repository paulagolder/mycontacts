/*
 * Created on 12-Jan-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.lerot.gui.widgets;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class jswLabel extends jswPanel
{

	private static final long serialVersionUID = 1L;
	JLabel label;

	public jswLabel()
	{
		super("L:");
		label = new JLabel();
		add("RIGHT", label);
		applyStyles(label);
		this.setEnabled(true);
	}

	public jswLabel(String inLabel)
	{
		super(inLabel);
		//this.setName(label);;
		label = new JLabel();
		label.setText(inLabel);
		add("RIGHT", label);
		applyStyles(label);
		this.setEnabled(true);
	}

	public jswLabel(int count)
	{
		super(count);
		String inlabel = String.valueOf(count);
		label = new JLabel();
		label.setText(inlabel);
		add("RIGHT", label);
		applyStyles(label);
		this.setEnabled(true);
	}

	public JLabel getLabel()
	{
		return label;
	}

	public String getText()
	{
		return label.getText();
	}

	@Override
	public boolean isSelected()
	{
		return false;
	}

	@Override
	public int jswGetWidth()
	{
		System.out.println(" setin width  " + label.getText());
		int setwidth = styles.getIntegerStyle("mywidth", -1);
		if (setwidth > 0) return setwidth;
		System.out.println(" setwidth " + setwidth);
		Dimension d = label.getPreferredSize();
		System.out.println(" prefered size w=" + d.width + " h=" + d.height);
		if (d.width > 0) return d.width;
		else
		{
			d = label.getMaximumSize();
			System.out.println(" maximum size w=" + d.width + " h=" + d.height);
			if (d.width > 0) return d.width;
			else
			{
				d = label.getMinimumSize();
				System.out.println(" minimum size w=" + d.width + " h="
						+ d.height);
				return d.width;
			}
		}
	}

	@Override
	public void setEnabled(boolean e)
	{
		label.setEnabled(e);
	}

	public void setIcon(ImageIcon icon)
	{
		label.setIcon(icon);

	}

	public void setSelected(boolean b)
	{
		;
	}

	public void setText(String t)
	{
		label.setText(t);
	}

}