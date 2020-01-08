package org.lerot.gui.widgets;

import java.awt.Component;
//import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;

public class jswPushButtonset extends jswPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;

	ActionListener actionlistener;
	ButtonGroup bg;
	int no = 0;
	jswPushButton[] options;
	String commandroot;

	public jswPushButtonset(ActionListener parentListener, String name,
			boolean isvertical, boolean border)
	{
		super("PBS:" + name);
		commandroot = name;
		actionlistener = parentListener;
		if (!isvertical)
		{
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setAlignmentY(Component.TOP_ALIGNMENT);
			// setMaximumSize(new Dimension(0, 45));
		} else
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		if (border)
		{
			setLineBorder();
			setBorder(setcborder(name));
		} else
		{

		}
		bg = new ButtonGroup();
		options = new jswPushButton[10];
		setName(name);
	}

	public jswPushButtonset(String name, boolean isvertical)
	{
		super("PBS:" + name);
		if (!isvertical)
		{
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setAlignmentY(Component.TOP_ALIGNMENT);
			// setMaximumSize(new Dimension(0, 45));
		} else
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		setLineBorder();
		setBorder(setcborder(name));
		bg = new ButtonGroup();
		options = new jswPushButton[10];
		setName(name);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Object action = e.getSource();
		for (int i = 0; i < no; i++)
		{
			jswPushButton on = options[i];
			if (on.isSelected())
			{
				// on.setEnabled(true);
			} else
			{
				// on.setEnabled(false);
			}
		}

		// System.out.println("pbs action  " + e.getActionCommand());
		actionlistener.actionPerformed(e);
	}

	public jswPushButton addNewOption(String text)
	{
		jswPushButton on = new jswPushButton(this, text);
		on.button.addActionListener(this);
		on.button.setActionCommand(commandroot + ":" + text);
		bg.add(on.button);
		options[no] = on;
		add(on);
		no = no + 1;
		return on;
	}

	public String getSelected()
	{
		for (int i = 0; i < no; i++)
		{
			jswPushButton on = options[i];
			if (on.isSelected()) return on.getText();
		}
		return "";
	}

	@Override
	public boolean isSelected()
	{
		return true;
	}

	public boolean isSelected(String selvalue)
	{
		String selectedstring = getSelected();
		if (selectedstring.equalsIgnoreCase(selvalue)) return true;
		else
			return false;
	}

	@Override
	public void setEnabled(boolean e)
	{
		for (int i = 0; i < no; i++)
		{
			jswPanel on = options[i];
			on.setEnabled(e);
		}
	}

	public void setSelected(String value)
	{
		for (int i = 0; i < no; i++)
		{
			jswPushButton on = options[i];
			if (on.getText().equalsIgnoreCase(value))
			{
				on.setSelected(true);
				on.setEnabled(true);
			} else
			{
				on.setEnabled(false);
				on.setSelected(false);
			}

		}

	}

}