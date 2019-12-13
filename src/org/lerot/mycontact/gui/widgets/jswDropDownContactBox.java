package org.lerot.mycontact.gui.widgets;


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
import org.lerot.mycontact.mcContacts;

public class jswDropDownContactBox extends jswHorizontalPanel
{

	class ContactRenderer extends BasicComboBoxRenderer
	{

		private static final long serialVersionUID = 1L;

		@Override
		public JComponent getListCellRendererComponent(JList list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			if (value instanceof mcContact)
			{
				mcContact foo = (mcContact) value;
				setText(foo.getName());
			}

			return this;
		}
	}
	private static final long serialVersionUID = 1L;
	JComboBox<mcContact> contactddbox;
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
		contactddbox = new JComboBox<mcContact>(listModel);
		contactddbox.setPreferredSize(new Dimension(width, 24));
		contactddbox.setRenderer(new ContactRenderer());
		setName(inLabel);
		if (hasborder)
		{
			if (haslabel) setBorder(setLineBorder());
			else
				setBorder(setcborder(inLabel));
		} else
			setBorder(setborder());
		add("FILLW", contactddbox);
	}

	public void addActionListener(ActionListener c)
	{
		contactddbox.addActionListener(c);
	}

	public void addActionListener(ActionListener c, String actionlabel)
	{
		contactddbox.addActionListener(c);
		contactddbox.setActionCommand(actionlabel);
	}

	public void addList(Vector<mcContact> list)
	{
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				listModel.addElement(list.get(i));
			}
			contactddbox.setSelectedIndex(0);
		}

	}

	public mcContact setNextValue()
	{
		mcContact selcon =  getSelectedValue();
		if(selcon==null)
		{
			contactddbox.setSelectedIndex(0);
		}
		else
		{
		  int currentindex = contactddbox.getSelectedIndex();
		  if(currentindex<0 )currentindex=0;
		  int nextindex = currentindex +1;
		  if(nextindex < contactddbox.getModel().getSize()) 
		  {
			  mcContact next =contactddbox.getItemAt(nextindex);
			  contactddbox.setSelectedItem(next);
		  }
		  else
		  {
			  contactddbox.setSelectedIndex(0);
		  }
		}
		return getSelectedValue();
	}

	public mcContact setPreviousValue()
	{
		mcContact selcon =  getSelectedValue();
		if(selcon==null)
		{
			contactddbox.setSelectedIndex(0);
		}
		else
		{
			  int currentindex = contactddbox.getSelectedIndex();
		  if(currentindex > 0) 
		  {
			 int nextindex = currentindex-1;
			  mcContact next = contactddbox.getItemAt(nextindex);
			  contactddbox.setSelectedItem(next);
		  }
		  else
		  {
			  contactddbox.setSelectedIndex(0);
		  }
		}
		return getSelectedValue();
	}
	
	public mcContact getSelectedValue()
	{
		if (contactddbox.getSelectedItem() != null)
		{
			return (mcContact) contactddbox.getSelectedItem();
		} else
			return null;
	}

	
	@Override
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
		contactddbox.setEnabled(e);
		// listModel.setEnabled(e);
	}

	public void setSelected(mcContact selitem)
	{
		if(contains(selitem))
	    	contactddbox.setSelectedItem(selitem);
		else
			contactddbox.setSelectedItem(0);
	}

	public void setSelected(int selindex)
	{
		contactddbox.setSelectedIndex(selindex);
	}

	public void clearList()
	{
		listModel.removeAllElements();
		contactddbox.removeAllItems();
	}

	public void setList(Vector<mcContact> list)
	{
		DefaultComboBoxModel<mcContact> newModel = new  DefaultComboBoxModel<mcContact>();
		if (list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				newModel.addElement(list.get(i));
			}
		}
		contactddbox.setModel( newModel );
		if(list.size() > 0)
			contactddbox.setSelectedIndex(0);
		else
			contactddbox.setSelectedIndex( -1);
	}
	
	public void setContactList(mcContacts contactlist)
	{
		Vector<mcContact> cv =  contactlist.makeOrderedContactsVector();
		setList(cv);
	}

	public int countSize()
	{
		return contactddbox.getModel().getSize();
	}

	public void setComboBoxEnabled(boolean b)
	{
		contactddbox.setEnabled(b);
	}

	public void removeActionListener(ActionListener al)
	{
		contactddbox.removeActionListener(al);
	}

	public void setActionCommand(String cmd)
	{
		contactddbox.setActionCommand(cmd);
	}

}
