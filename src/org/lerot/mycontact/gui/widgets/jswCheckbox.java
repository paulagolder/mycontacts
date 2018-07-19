/*
 * Created on 09-Jan-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.lerot.mycontact.gui.widgets;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

public class jswCheckbox extends jswPanel
{

	private static final long serialVersionUID = 1L;
	JCheckBox check;

	public jswCheckbox(String label)
	{
		super(label);
		setAlignmentX(JComponent.LEFT_ALIGNMENT);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		int width = label.length() * 12 + 30;
		check = new JCheckBox(label);
		check.setSelected(false);
		check.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		add(check);
		setMinimumSize(new Dimension(width, 40));
	}

	public jswCheckbox(ActionListener c, String label)
	{
		super(label);
		setAlignmentX(JComponent.LEFT_ALIGNMENT);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		int width = label.length() * 12 + 30;
		check = new JCheckBox(label);
		check.setSelected(false);
		check.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		check.addActionListener(c);
		check.setActionCommand(label);
		add(check);
		setMinimumSize(new Dimension(width, 40));
	}

	public void addActionListener(ActionListener c, String actionlabel)
	{
		check.addActionListener(c);
		check.setActionCommand(actionlabel);
	}

	@Override
	public boolean isSelected()
	{
		return check.isSelected();
	}

	@Override
	public void setEnabled(boolean e)
	{
		check.setEnabled(e);
	}

	public void setSelected(boolean b)
	{
		check.setSelected(b);
	}

	public String getLabel()
	{
		return check.getText();
	}
}