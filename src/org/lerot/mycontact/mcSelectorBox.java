package org.lerot.mycontact;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JButton;

import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswCheckbox;
import org.lerot.mycontact.gui.widgets.jswDropDownContactBox;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswLabel;
import org.lerot.mycontact.gui.widgets.jswOption;
import org.lerot.mycontact.gui.widgets.jswOptionset;
import org.lerot.mycontact.gui.widgets.jswTextBox;
import org.lerot.mycontact.gui.widgets.jswVerticalPanel;

public class mcSelectorBox extends jswVerticalPanel implements ActionListener,
		FocusListener, KeyListener
{

	private static final long serialVersionUID = 1L;

	private int importcount;
	private int importrownumber = 1;
	mcImports imported;
	private jswOptionset optionset;
	private jswCheckbox[] cb;
	private jswButton refreshbutton;
	private String browsestatus = "BROWSE";
	private jswTextBox filterbox;
	private jswDropDownContactBox contactselect;
	private jswOption allcontacts;
	private jswOption browsecontacts;
	private jswOption selectedcontacts;
	private boolean person = true;
	private boolean group = true;
	private boolean whitelist = false;
	private boolean blacklist = false;
	private boolean graylist = false;
	private mcContact selcontact;
	private JButton setposition;

	private mcContacts currentcontactlist;
	private mcContacts browsecontactlist;
	private mcContacts selectedcontactlist;
	private mcContacts allcontactlist;
	private mcContacts allgrouplist;
	private String searchterm;
	private jswHorizontalPanel filterbar;
	private jswHorizontalPanel selecttypebox;

	private jswHorizontalPanel atitlebox;

	public mcSelectorBox(mcdb aparent, ActionListener al)
	{

		currentcontactlist = new mcContacts();
		browsecontactlist = new mcContacts();
		allcontactlist = new mcContacts();
		selectedcontactlist = new mcContacts();
		setposition = new JButton("freddy");
		setposition.addActionListener(al);
		// setposition.setActionCommand("abcedefg");
		filterbar = new jswHorizontalPanel();
		optionset = new jswOptionset("source", false, this);
		allcontacts = optionset.addNewOption("All Contacts", false);
		allcontacts.addActionListener(this, "SOURCE:ALL");
		allcontacts.setTag("all");
		browsecontacts = optionset.addNewOption("Browse Contacts", false);
		browsecontacts.addActionListener(this, "SOURCE:BROWSE");
		browsecontacts.setTag("browse");
		int noselected = selectedcontactlist.size();
		selectedcontacts = optionset.addNewOption("Selected Contacts", false);
		selectedcontacts.setTag("selected");
		selectedcontacts.addActionListener(this, "SOURCE:SELECTED");
		if (noselected < 1) selectedcontacts.setEnabled(false);
		else
			selectedcontacts.setEnabled(true);
		filterbar.add(" LEFT ", allcontacts);
		filterbar.add(" MIDDLE ", browsecontacts);
		browsecontacts.setSelected(true);
		filterbar.add(" RIGHT ", selectedcontacts);
		this.add(filterbar);
		selecttypebox = new jswHorizontalPanel();

		cb = new jswCheckbox[5];
		cb[0] = new jswCheckbox(this, "person");
		cb[0].setSelected(true);
		selecttypebox.add(" LEFT ", cb[0]);
		cb[1] = new jswCheckbox(this, "group");
		cb[1].setSelected(true);
		selecttypebox.add(" LEFT ", cb[1]);
		refreshbutton = new jswButton(this, "Refresh");
		selecttypebox.add(" MIDDLE ", refreshbutton);
		cb[2] = new jswCheckbox(this, "whitelist");
		selecttypebox.add(" RIGHT ", cb[2]);
		cb[3] = new jswCheckbox(this, "graylist");
		selecttypebox.add(" RIGHT ", cb[3]);
		cb[4] = new jswCheckbox(this, "blacklist");
		selecttypebox.add(" RIGHT ", cb[4]);
		this.add(selecttypebox);
		// if (!browsestatus.equals("BROWSE"))
		// {
		// this.setVisible(false);
		// this.setBackground(Color.yellow);
		//
		// } else
		this.setVisible(true);
		System.out.println(" browsestatus =" + browsestatus);
		atitlebox = new jswHorizontalPanel();
		jswButton previous = new jswButton(this, "Previous");
    
		atitlebox.add(" LEFT ", previous);

		filterbox = new jswTextBox(null);
		filterbox.setPreferredSize(new Dimension(100, 15));
		filterbox.setMinimumSize(new Dimension(40, 15));
		// filterbox.setMaximumSize(new Dimension(40, 15));
		filterbox.addFocusListener(this);
		filterbox.addKeyListener(this);
		atitlebox.add(" MIDDLE HEIGHT=15  ", filterbox);
		contactselect = new jswDropDownContactBox("Select Contact", true,
				false, 240);
		contactselect.setActionCommand("selectcontact");
		// contactselect.setEnabled(false);
		atitlebox.add(" MIDDLE ", contactselect);
		// atitlebox.setBorder(jswLabel.setLineBorder(Color.gray, 2));
		contactselect.addList(browsecontactlist.makeOrderedContactsVector());
		contactselect.addActionListener(this, "contactselected");
		// contactselect.setSelected(parent.selcontact);
		jswButton next = new jswButton(this, "Next");
		atitlebox.add(" RIGHT ", next);

		this.add(atitlebox);
        
		this.setBorder(jswLabel.setLineBorder(Color.gray, 2));

	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		//System.out.println(" ***** " + action);
		String oldbrowsestatus = browsestatus;
		if (action.startsWith("SOURCE:"))
		{
			String vstr = action.substring(7);
			browsestatus = vstr;
			if (!oldbrowsestatus.equals(browsestatus))
			{
				switch (browsestatus)
				{
				case "BROWSE":
					currentcontactlist = browsecontactlist;
					break;
				case "ALL":
					currentcontactlist = allcontactlist;
					break;
				case "SELECTED":
					currentcontactlist = selectedcontactlist;
				}
				mcContact oldcontact = selcontact;
				contactselect.replaceList(currentcontactlist
						.makeOrderedContactsVector());
				selcontact = currentcontactlist.FindbyID(oldcontact.getID());
				//if(contactselect.)
				contactselect.setSelected(selcontact);
				//em.out.println(" ***** " + selcontact);
			}

		} else if (action.equals("REFRESH"))
		{
			refreshSelection();
		} else if (action.equals("PREVIOUS"))
		{
			selcontact = contactselect.setPreviousValue();
			mcdb.topgui.aneditpanel.clearEdit();
			// mcdb.topgui.refresh();
		} else if (action.equals("NEXT"))
		{
			selcontact = contactselect.setNextValue();
			mcdb.topgui.aneditpanel.clearEdit();
	
			// mcdb.topgui.refresh();
		} else if (action.equals("CONTACTSELECTED"))
		{
			selcontact = contactselect.getSelectedValue();
			// mcdb.topgui.refresh();
		} else if (action.equals("PERSON"))
		{
			person = !person;

		} else if (action.equals("GROUP"))
		{
			group = !group;

		} else if (action.equals("WHITELIST"))
		{
			whitelist = !whitelist;

		} else if (action.equals("BLACKLIST"))
		{
			blacklist = !blacklist;

		} else if (action.equals("GRAYLIST"))
		{
			graylist = !graylist;

		} else
			System.out.println("action  " + action
					+ " unrecognised in selectorbox ");
		update();
		mcdb.topgui.refresh();
	}
	
	public void addtoGroupfilter(String filter)
	{
		browsecontactlist.addtoGroupfilter(filter);
	}

	public void clearGroupfilter()
	{
		browsecontactlist.clearGroupfilter();
	}

	public void clearSelectedContactList()
	{
		
		selectedcontactlist.clear();

	}

	public int countAll()
	{
		return allcontactlist.size();
	}

	public void filterContacts()
	{
		browsecontactlist.filterContacts(allcontactlist);
	}

	public mcContact FindbyID(String id)
	{
		return allcontactlist.FindbyID(id);
	}
	
	public mcContact FindbyID(int cid)
	{
		return allcontactlist.FindbyID(cid);
	}
	
	public mcContact FindbyIDstr(String selid)
	{
		return allcontactlist.FindbyID(selid);
	}

	public mcContact FindbyTID(String sname)
	{
		return allcontactlist.FindbyTID(sname);
	}

	public mcContact findFirst()
	{
		return currentcontactlist.findFirst();
	}

	public mcContact findName(String aname)
	{
		return allcontactlist.findName(aname);
	}

	public mcContact findName2(String aname)
	{
		return allcontactlist.findName2(aname);
	}

	public mcContact findName3(String aname)
	{
		return allcontactlist.findName3(aname);
	}

	public mcContact findPhone(String attkey, String text)
	{
		return allcontactlist.findPhone(attkey, text);
	}

	public mcContact findValue(String attkey, String text)
	{
		return allcontactlist.findValue(attkey, text);
	}

	@Override
	public void focusGained(FocusEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void focusLost(FocusEvent e)
	{

		Object source = e.getSource();
		if (source instanceof javax.swing.JTextField)
		{
			String filter = ((javax.swing.JTextField) source).getText();
			currentcontactlist.setTextfilter(filter);
			selcontact = currentcontactlist.findFilterContact();
			mcdb.topgui.refresh();
		}
	}

	public mcContact get(String selectid)
	{
		return allcontactlist.get(selectid);
	}

	public mcContacts getAllcontactlist()
	{
		return allcontactlist;
	}

	public mcContacts getallGroups()
	{
		return allgrouplist;
	}
	
	public mcContacts getBrowsecontactlist()
	{
		return browsecontactlist;
	}

	public String getSearchterm()
	{
		return searchterm;
	}

	public mcContact getSelcontact()
	{
		return selcontact;
	}

	public mcContacts getSelectedcontactlist()
	{
		return selectedcontactlist;
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int keyCode = e.getKeyCode();
		Object source = e.getSource();
		if (source instanceof javax.swing.JTextField)
		{
			if (keyCode == 10 || keyCode == 8)
			{
				System.out.println("keypressed  2 ");
				if (keyCode == 10) contactselect.requestFocus();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	public Vector<mcContact> makeContactsVector()
	{
		return currentcontactlist.makeOrderedContactsVector();
	}

	private void makeEvent(String cmd)
	{
		setposition.setActionCommand(cmd);
		setposition.doClick();
	}

	public void navVisible(boolean b)
	{
		atitlebox.setVisible(b);
	}

	public void put(mcContact newcontact)
	{
		allcontactlist.put(newcontact);
		currentcontactlist.put(newcontact);
	}

	public void xrefreshContactList()
	{
		currentcontactlist.filterContacts(allcontactlist);
	}
	
	public void refreshAll()
	{
		setAllContacts();
		filterbox.setAlert(false);
		browsecontactlist.setGroupFilter(cb);
		browsecontactlist.clearError();
		browsecontactlist.filterContacts(allcontactlist);
		System.out.println(" browsecontacts refresh "
				+ browsecontactlist.size());
		if (browsecontactlist.textfiltererror)
		{
			System.out.println(" browse refresh textfiltererro ");
			filterbox.setAlert(true);
		}
	}
	
	public mcContacts getContactList(String[] strings )
	{
		mcContacts sellist = new mcContacts();
		sellist.setGroupFilter(strings);
		sellist.clearError();
		sellist.filterContacts(allcontactlist);
		return sellist;		
	}

	public void refreshSelection()
	{
		setAllContacts();
		filterbox.setAlert(false);
		browsecontactlist.setGroupFilter(cb);
		browsecontactlist.clearError();
		browsecontactlist.filterContacts(allcontactlist);
		System.out.println(" browsecontacts refresh "
				+ browsecontactlist.size());
		if (browsecontactlist.textfiltererror)
		{
			System.out.println(" browse refresh textfiltererro ");
			filterbox.setAlert(true);
		}

	}

	public void remove(mcContact selcontact)
	{
		allcontactlist.remove(selcontact);
		selectedcontactlist.remove(selcontact);
		allgrouplist.remove(selcontact);

	}

	public void removesearchcontact(String selcon)
	{
		mcContact delcontact = allcontactlist.FindbyID(selcon);
		selectedcontactlist.remove(delcontact);

	}

	public mcContacts searchAttribute(String searchterm2)
	{
		mcContacts currentsearchlist = browsecontactlist;
		//mcContacts found = allcontactlist.searchAttribute(searchterm2);
		switch (browsestatus)
		{
		case "BROWSE":
			currentsearchlist = browsecontactlist;
			break;
		case "ALL":
			currentsearchlist = allcontactlist;
			break;
		case "SELECTED":
			currentsearchlist = selectedcontactlist;
		}
		mcContacts found = currentsearchlist.searchAttribute(searchterm2);
		if (found.size() > 0)
		{
			selectedcontactlist = found;
			searchterm = searchterm2;
			currentcontactlist = found;
			setBrowseStatus("SELECTED");
			mcdb.topgui.refresh();
		}
		else
		{
			selectedcontactlist.clear();
			searchterm = searchterm2;
		}
		return found;
	}
	
	public mcContacts searchTag(String searchterm2)
	{
		mcContacts currentsearchlist=browsecontactlist;
		//mcContacts found = allcontactlist.searchTag(searchterm2);
		switch (browsestatus)
		{
		case "BROWSE":
			currentsearchlist = browsecontactlist;
			break;
		case "ALL":
			currentsearchlist = allcontactlist;
			break;
		case "SELECTED":
			currentsearchlist = selectedcontactlist;
		}
		mcContacts found = currentsearchlist.searchTag(searchterm2);
		
		if (found.size() > 0)
		{
			selectedcontactlist = found;
			searchterm = searchterm2;
			setBrowseStatus("SELECTED");
			mcdb.topgui.refresh();
		}else
		{
			selectedcontactlist.clear();
			searchterm = searchterm2;
		}
		return found;
	}

	public void setAllcontactlist(mcContacts allcontactlist)
	{
		this.allcontactlist = allcontactlist;
	}

	public void setAllContacts()
	{
		allcontactlist.selectAllContacts();
		String[] filter = { "group" };
		allgrouplist = new mcContacts();
		allgrouplist.setGroupFilter(filter);
		allgrouplist.filterContacts(allcontactlist);
	}

	public void setBrowsecontactlist(mcContacts browsecontactlist)
	{
		this.browsecontactlist = browsecontactlist;
	}

	public void setBrowseStatus(String browsestatus2)
	{
		String oldebrowsestatus = browsestatus;
		browsestatus = browsestatus2;
		if (oldebrowsestatus == null || !oldebrowsestatus.equals(browsestatus)
				|| currentcontactlist.size() < 1)
		{
			switch (browsestatus)
			{
			case "BROWSE":
				currentcontactlist = browsecontactlist;
				break;
			case "ALL":
				currentcontactlist = allcontactlist;
				break;
			case "SELECTED":
				currentcontactlist = selectedcontactlist;
			}
			
		}
		contactselect.removeActionListener(this);
		contactselect.replaceList(currentcontactlist.makeOrderedContactsVector());
		contactselect.addActionListener(this);
	}

	public void setEnabled(boolean b)
	{
		contactselect.setEnabled(b);
	}

	public void setMode(String string)
	{
		// TODO Auto-generated method stub
		// not needed ?
	}

	public void setSearchterm(String searchterm)
	{
		this.searchterm = searchterm;
	}

	public void setSelcontact(mcContact aselcontact)
	{
		this.selcontact = aselcontact;
	}

	public void setSelcontact(String selcon)
	{
		selcontact = FindbyID(selcon);
	}

	public void setSelectedcontactlist(mcContacts selectedcontactlist)
	{
		this.selectedcontactlist = selectedcontactlist;
	}

	public void update()
	{
		allcontacts.setText("All Contacts " + allcontactlist.size());
		browsecontacts.setText("Browse Contacts " + browsecontactlist.size());
		int noselected = selectedcontactlist.size();
		selectedcontacts.setText("Selected Contacts " + noselected);
		if (noselected < 1)
		{
			selectedcontacts.setEnabled(false);
			refreshbutton.setEnabled(false);
		} else
		{
			selectedcontacts.setEnabled(true);
			refreshbutton.setEnabled(true);
		}

		if (person) cb[0].setSelected(true);
		else
			cb[0].setSelected(false);
		if (group) cb[1].setSelected(true);
		else
			cb[1].setSelected(false);
		if (whitelist) cb[2].setSelected(true);
		else
			cb[2].setSelected(false);
		if (graylist) cb[3].setSelected(true);
		else
			cb[3].setSelected(false);
		if (blacklist) cb[4].setSelected(true);
		else
			cb[4].setSelected(false);

		// contactselectactive = false;
		optionset.setSelected(browsestatus);
		switch (browsestatus)
		{
		case "BROWSE":
			selecttypebox.setEnabled(true);
			break;
		case "ALL":
			selecttypebox.setEnabled(false);
			break;
		case "SELECTED":
			selecttypebox.setEnabled(false);
		}
		contactselect.removeActionListener(this);
		if (contactselect.countSize() < 1)
		{
			contactselect.replaceList(currentcontactlist.makeOrderedContactsVector());
		}
		if (selcontact != null)
		{
			contactselect.setSelected(selcontact);
		} else
		{
			if (contactselect.countSize() > 0)
			{
				contactselect.setSelected(0);
				selcontact = contactselect.getSelectedValue();
			}
		}
		contactselect.addActionListener(this);
		filterbox.clear();
		// System.out.println(" selcontact3c " + selcontact);
	}

	

	

	
	

	

}
