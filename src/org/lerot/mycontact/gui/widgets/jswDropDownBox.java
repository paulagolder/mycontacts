package org.lerot.mycontact.gui.widgets;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class jswDropDownBox extends jswHorizontalPanel
{

	private static final long serialVersionUID = 1L;
	JComboBox<String> datalist;
	JLabel label;
	DefaultComboBoxModel<String> listModel;

	public jswDropDownBox(String inLabel, boolean haslabel, boolean hasborder)
	{
		if (haslabel)
		{
			label = new JLabel();
			label.setText(inLabel);
			add("LEFT", label);
			label.setFont(new Font("SansSerif", Font.BOLD, 12));
		}
		listModel = new DefaultComboBoxModel<String>();
		datalist = new JComboBox<String>(listModel);
		datalist.setFont(new Font("SansSerif", Font.PLAIN, 12));
		datalist.setPreferredSize(new Dimension(100, 30));
		setName(inLabel);
		if (hasborder)
		{
			if (haslabel) setBorder(setLineBorder());
			else
				setBorder(setcborder(inLabel));
		} else
			setBorder(setborder());
		add("FILLW", datalist);
		// applyStyles(datalist);
		// applyStyles(label);

	}

	public void addActionListener(ActionListener c)
	{
		datalist.addActionListener(c);
	}

	public void addActionListener(ActionListener c, String actionlabel)
	{
		datalist.addActionListener(c);
		datalist.setActionCommand(actionlabel);
	}

	public void addList(Vector<String> list)
	{
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				listModel.addElement(list.get(i));
			}
		}
		datalist.setSelectedIndex(0);
	}

	public void addElement(String listelement)
	{
		listModel.addElement(listelement);
		datalist.setSelectedIndex(0);
	}

	public Dimension getPreferredSize()
	{
		Dimension d1 = datalist.getPreferredSize();
		Dimension d2 = new Dimension(0, 0);
		if (label != null)
		{
			d2 = label.getPreferredSize();
		}
		int width = d1.width + d2.width;
		int height = d1.height;
		if (d2.height > height) height = d2.height;
		return new Dimension(width, height);
	}

	public String getSelectedValue()
	{
		if (datalist.getSelectedItem() != null)
		{
			return (String) datalist.getSelectedItem();
		} else
			return null;
	}

	@Override
	public boolean isSelected()
	{
		return false;
	}

	@Override
	public void setEnabled(boolean e)
	{
		label.setEnabled(e);
		datalist.setEnabled(e);
	}

	public void setList(String str)
	{
		listModel.removeAllElements();
		if (str != null)
		{
			listModel.addElement(str);
			datalist.setSelectedIndex(0);
		}

	}

	public void setList(String[] list)
	{
		listModel.removeAllElements();
		if (list.length > 0)
		{
			for (int i = 0; i < list.length; i++)
			{
				listModel.addElement(list[i]);
			}
			datalist.setSelectedIndex(0);
		}

	}

	public void setList(Vector<String> list)
	{
		listModel.removeAllElements();
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				listModel.addElement(list.get(i));
			}
			datalist.setSelectedIndex(0);
		}

	}

	public void setSelected(String selitem)
	{
		datalist.setSelectedItem(selitem);
	}

	public void setPreferredize(Dimension dim)
	{

		datalist.setPreferredSize(dim);
	}

}
