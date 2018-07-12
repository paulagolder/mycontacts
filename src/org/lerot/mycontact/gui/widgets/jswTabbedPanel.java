package org.lerot.mycontact.gui.widgets;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

public class jswTabbedPanel extends jswPanel
{

	private static final long serialVersionUID = 1L;
	JTabbedPane tabs;

	public jswTabbedPanel()
	{
		super("tabbedpane");
		tabs = new JTabbedPane();
		this.setEnabled(true);
		tabs.setEnabled(true);
		tabs.setVisible(true);
		this.setVisible(true);
		add(" FILLW ", tabs);
	}

	public void addTab(String name, jswPanel apanel)
	{
		tabs.addTab(name, apanel);
	}

	public void setSelectedComponent(jswPanel apanel)
	{
		tabs.setSelectedComponent(apanel);
	}

	public int getSelectedIndex()
	{
		// TODO Auto-generated method stub
		return tabs.getSelectedIndex();
	}

	public String getTitleAt(int index)
	{
		return tabs.getTitleAt(index);
	}

	public void addChangeListener(ChangeListener changeListener)
	{
		tabs.addChangeListener(changeListener);
	}

	public void setSelectedIndex(int i)
	{
		tabs.setSelectedIndex(i);

	}

}
