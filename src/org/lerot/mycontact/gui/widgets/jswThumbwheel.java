/*
 * Created on 09-Jan-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.lerot.mycontact.gui.widgets;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class jswThumbwheel extends jswPanel
{

	private static final long serialVersionUID = 1L;
	int mheight = 60;
	// JCheckBox check;
	JSpinner value;

	public jswThumbwheel(String text, int inmin, int inmax)
	{
		super(text);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// check = new JCheckBox(text);
		// check.setSelected(false);
		// add(check);
		JLabel label = new JLabel("    " + text);
		add(label);
		int width = text.length() * 12 + 60;
		value = new JSpinner();
		Integer current = new Integer(inmin);
		Integer min = new Integer(inmin);
		Integer max = new Integer(inmax);
		Integer step = new Integer(1);
		SpinnerNumberModel m_numberSpinnerModel = new SpinnerNumberModel(
				current, min, max, step);
		value = new JSpinner(m_numberSpinnerModel);
		value.setMaximumSize(new Dimension(60, 30));
		value.setValue(new Integer(inmax));
		add(value);
		setMinimumSize(new Dimension(width, 42));
	}

	public int getValue()
	{
		return ((Integer) value.getValue()).intValue();
	}

	@Override
	public boolean isSelected()
	{
		return true;
	}

	@Override
	public void setEnabled(boolean e)
	{
		// check.setEnabled(e);
		value.setEnabled(e);
	}

	public void setValue(int v)
	{
		value.setValue(new Integer(v));
	}

}