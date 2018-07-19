package org.lerot.mycontact.gui.widgets;

//import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.lerot.mycontact.mcContact;

public class jswDropDownContactBox extends jswHorizontalPanel
{

	class ContactRenderer extends BasicComboBoxRenderer
	{

		private static final long serialVersionUID = 1L;

		public JComponent getListCellRendererComponent(JList list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			if (value instanceof mcContact)
			{
				mcContact foo = (mcContact) value;
				setText(foo.getTID());
			}

			return this;
		}
	}
	private static final long serialVersionUID = 1L;
	JComboBox<mcContact> datalist;
	JLabel label;

	DefaultComboBoxModel<mcContact> listModel;

	public boolean contains (mcContact target)
	{
		int fnd  = listModel.getIndexOf(target);
		if(fnd==-1) return false;
		else return true;
	}
	
	public jswDropDownContactBox(String inLabel, boolean haslabel,
			boolean hasborder, int width)
	{
		if (haslabel)
		{
			label = new JLabel();
			label.setText(inLabel);
			add("LEFT", label);
			label.setFont(new Font("SansSerif", Font.BOLD, 12));
		}
		listModel = new DefaultComboBoxModel<mcContact>();
		datalist = new JComboBox<mcContact>(listModel);
		datalist.setPreferredSize(new Dimension(width, 24));
		datalist.setRenderer(new ContactRenderer());
		// datalist.setEditable(true);
		setName(inLabel);
		if (hasborder)
		{
			if (haslabel) setBorder(setLineBorder());
			else
				setBorder(setcborder(inLabel));
		} else
			setBorder(setborder());
		add("FILLW", datalist);
		// datalist.addActionListener(new MyActionListener());
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

	public void addList(Vector<mcContact> list)
	{
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				listModel.addElement(list.get(i));
			}
			datalist.setSelectedIndex(0);
		}

	}

	public mcContact setNextValue()
	{
		mcContact selcon =  getSelectedValue();
		if(selcon==null)
		{
			datalist.setSelectedIndex(0);
		}
		else
		{
		  int currentindex = listModel.getIndexOf(selcon);
		  int nextindex = currentindex +1;
		  if(nextindex < listModel.getSize()) 
		  {
			  mcContact next = listModel.getElementAt(nextindex);
			  datalist.setSelectedItem(next);
		  }
		  else
		  {
			  datalist.setSelectedIndex(0);
		  }
		}
		return getSelectedValue();
	}

	public mcContact setPreviousValue()
	{
		mcContact selcon =  getSelectedValue();
		if(selcon==null)
		{
			datalist.setSelectedIndex(0);
		}
		else
		{
		  int currentindex = listModel.getIndexOf(selcon);
		  if(currentindex > 0) 
		  {
			  mcContact next = listModel.getElementAt(currentindex-1);
			  datalist.setSelectedItem(next);
		  }
		  else
		  {
			  datalist.setSelectedIndex(0);
		  }
		}
		return getSelectedValue();
	}
	
	public mcContact getSelectedValue()
	{
		if (datalist.getSelectedItem() != null)
		{
			return (mcContact) datalist.getSelectedItem();
		} else
			return null;
	}

	
	public boolean isSelected()
	{
		mcContact selcon = getSelectedValue();
		if(selcon == null) return false;
		else return true;
	}

	@Override
	public void setEnabled(boolean e)
	{
		label.setEnabled(e);
		datalist.setEnabled(e);
		// listModel.setEnabled(e);
	}

	public void setSelected(mcContact selitem)
	{
		if(contains(selitem))
	    	datalist.setSelectedItem(selitem);
		else
			datalist.setSelectedItem(0);
	}

	public void setSelected(int selindex)
	{
		datalist.setSelectedIndex(selindex);
	}

	public void clearList()
	{
		listModel.removeAllElements();
		datalist.removeAllItems();
	}

	public void replaceList(Vector<mcContact> list)
	{
		listModel.removeAllElements();
		datalist.removeAllItems();
		addList(list);
	}

	public int countSize()
	{
		return listModel.getSize();
	}

	public void setComboBoxEnabled(boolean b)
	{
		datalist.setEnabled(b);
	}

	public void removeActionListener(ActionListener al)
	{
		datalist.removeActionListener(al);
	}

	public void setActionCommand(String cmd)
	{
		datalist.setActionCommand(cmd);
	}

}
