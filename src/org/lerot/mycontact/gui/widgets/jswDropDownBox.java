package org.lerot.mycontact.gui.widgets;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.ComboBoxModel;
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
	}

	public void addActionListener(ActionListener c)
	{
		datalist.addActionListener(c);
	}
	
	
	public void removeActionListener(ActionListener c)
	{
		datalist.removeActionListener(c);
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
		int delta =20; //extrawidth for dropdownarrow 
		if (label != null)
		{
			d2 = label.getPreferredSize();
		}
		int width = d1.width + d2.width;
		int height = d1.height;
		if (d2.height > height) height = d2.height;
		return new Dimension(width-delta, height);
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
	
	public void removeAll()
	{
		listModel.removeAllElements();
		

	}

	public void setList(String str)
	{
		 DefaultComboBoxModel<String> newModel = new  DefaultComboBoxModel<String>();
		listModel.removeAllElements();
		if (str != null)
		{
			newModel.addElement(str);
			//datalist.setSelectedIndex(0);
		}
		datalist.setModel( newModel );
		datalist.setSelectedIndex(0);
	}

	public void setList(String[] list)
	{
		 DefaultComboBoxModel<String> newModel = new  DefaultComboBoxModel<String>();
		//listModel.removeAllElements();
		if (list.length > 0)
		{
			for (int i = 0; i < list.length; i++)
			{
				newModel.addElement(list[i]);
			}
		}
		datalist.setSelectedIndex(0);
		datalist.setModel( newModel );
	}

	public void setList(Vector<String> list)
	{
		DefaultComboBoxModel<String> newModel = new  DefaultComboBoxModel<String>();
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				newModel.addElement(list.get(i));
			}
		}
		datalist.setSelectedIndex(0);
		datalist.setModel( newModel );
	}
	
	public void setList(Map<String, Integer> tags)
	{
		DefaultComboBoxModel<String> newModel = new  DefaultComboBoxModel<String>();
		for (Entry<String, Integer> tag : tags.entrySet())
		{
				newModel.addElement(tag.getKey());
		}
		datalist.setModel( newModel );	
		datalist.setSelectedIndex(0);
	}
	public void addList(Map<String, Integer> taglist)
	{   
		if(taglist == null) return;
		if(taglist.size()<1) return;
		DefaultComboBoxModel<String> newModel = (DefaultComboBoxModel)datalist.getModel();
		for (Entry<String, Integer> tag : taglist.entrySet())		{
				newModel.addElement(tag.getKey());
		}
		datalist.setModel( newModel );	
		datalist.setSelectedIndex(0);
		
	}

	public void setSelected(String selitem)
	{
		datalist.setSelectedItem(selitem);
	}

	public void setPreferredize(Dimension dim)
	{

		datalist.setPreferredSize(dim);
	}

	public int  getItemCount()
	{
		return datalist.getItemCount();
	}





}
