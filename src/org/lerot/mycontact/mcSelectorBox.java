package org.lerot.mycontact;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;

import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswCheckbox;
import org.lerot.mycontact.gui.widgets.jswDropDownContactBox;
import org.lerot.mycontact.gui.widgets.jswDropDownBox;
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

	mcImports imported;
	private jswOptionset xoptionset;
	private jswCheckbox[] contactselector;
	private jswButton xrefreshbutton;
	private String xbrowsestatus = "BROWSE";
	private jswTextBox filterbox;
	private jswDropDownBox browseselectbox;
	private jswDropDownContactBox contactselectbox;
	private jswLabel allcontacts;
	private jswLabel browsecontacts;
	private jswOption xselectedcontacts;
	
	private mcContact selcontact;
	private JButton setposition;

	private mcContacts currentcontactlist;
	private mcContacts browsecontactlist;
	private mcContacts searchresultlist;
	private mcContacts allcontactlist;
	private String searchterm;
	private String browsefilter="all";
	private jswHorizontalPanel filterbar;


	private jswHorizontalPanel atitlebox;

	public mcSelectorBox(mcdb aparent, ActionListener al)
	{
      
		currentcontactlist = new mcContacts();
		browsecontactlist = new mcContacts();
		allcontactlist = new mcContacts();
		searchresultlist = new mcContacts();
		setposition = new JButton("freddy");
		setposition.addActionListener(al);
		filterbar = new jswHorizontalPanel();
		allcontacts = new jswLabel("All Contacts");
		filterbar.add(" LEFT ", allcontacts);
		browseselectbox = new jswDropDownBox("Select Group",true,true);
		
		browseselectbox.addActionListener(this, "BROWSESELECT");
		filterbar.add(" MIDDLE WIDTH=200 ", browseselectbox);
	
        browsecontacts = new 	jswLabel("Browsing:");
		
		filterbar.add(" RIGHT ", browsecontacts);
	
	
		this.add(filterbar);
		
		this.setVisible(true);
		
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
		contactselectbox = new jswDropDownContactBox("Select Contact", true,
				false, 240);
		//contactselectbox.setActionCommand("selectcontact");
		// contactselect.setEnabled(false);
		atitlebox.add(" MIDDLE ", contactselectbox);
		// atitlebox.setBorder(jswLabel.setLineBorder(Color.gray, 2));
		contactselectbox.addList(browsecontactlist.makeOrderedContactsVector());
		contactselectbox.addActionListener(this, "contactselected");
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

		 if (action.equals("BROWSESELECT"))
		{
			  allcontacts.setText("AllContacts =" + allcontactlist.size());
			   setBrowseFilter(browseselectbox);
			   browsecontactlist=searchTag(browsefilter);
			   browsecontacts.setText("Browse Contacts =" + browsecontactlist.size());
			  // System.out.println(" ***** " + browsefilter+" "+browsecontactlist.size());
			   contactselectbox.removeActionListener(this);
			   contactselectbox.setList(browsecontactlist.makeOrderedContactsVector());
			   contactselectbox.addActionListener(this, "contactselected");	 
			   //System.out.println(" started " + mcdb.started);
			   if(mcdb.started) contactselectbox.setSelected(0);
		}
		else if (action.equals("REFRESH"))
		{
			refreshSelection();
		} else if (action.equals("PREVIOUS"))
		{
			selcontact = contactselectbox.setPreviousValue();
			mcdb.topgui.aneditpanel.clearEdit();
		} else if (action.equals("NEXT"))
		{
			selcontact = contactselectbox.setNextValue();
			mcdb.topgui.aneditpanel.clearEdit();
		} else if (action.equals("CONTACTSELECTED"))
		{
			selcontact = contactselectbox.getSelectedValue();
			  //System.out.println(" contact selected " + selcontact);
		} else
			System.out.println("action  " + action
					+ " unrecognised in selectorbox ");
		//update();
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

	public void clearSearchResultList()
	{
		
		searchresultlist.clear();

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

	public mcContacts getSearchResultList()
	{
		return searchresultlist;
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
				if (keyCode == 10) contactselectbox.requestFocus();
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
	
	public void filterboxVisible(boolean b)
	{
		filterbox.setVisible(b);;
		
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
	/*	//setAllContacts("ref");
		filterbox.setAlert(false);
		//browsecontactlist.setGroupFilter(browseselect);
		browsecontactlist.clearError();
		browsecontactlist.filterContacts(allcontactlist);
		System.out.println(" browsecontacts refresh "
				+ browsecontactlist.size());
		if (browsecontactlist.textfiltererror)
		{
			System.out.println(" browse refresh textfiltererror ");
			filterbox.setAlert(true);
		}*/
	}
	
	public mcContacts getContactList(String[] strings )
	{
		mcContacts sellist = new mcContacts();
		//sellist.setGroupFilter(strings);
		sellist.clearError();
		sellist.filterContacts(allcontactlist);
		return sellist;		
	}

	public void refreshSelection()
	{
		refreshAllContacts("refsel");
		filterbox.setAlert(false);
		browsecontactlist.setGroupFilter(contactselector);
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
		searchresultlist.remove(selcontact);
		browsecontactlist.remove(selcontact);

	}

	public void removesearchcontact(String selcon)
	{
		mcContact delcontact = allcontactlist.FindbystrID(selcon);
		searchresultlist.remove(delcontact);
		 mcdb.selbox.setSelectedcontactlist(searchresultlist);
		////mcContacts currentsearchlist = browsecontactlist;
		//currentsearchlist = selectedcontactlist;

	}

	public mcContacts searchAttribute(String searchterm2)
	{
		mcContacts currentsearchlist = browsecontactlist;
		mcContacts found = currentsearchlist.searchAttribute(searchterm2);
		System.out.println(" contacts found="+found.size());
		searchterm = searchterm2;
		return found;
	}
	
	public mcContacts searchTag(String tag)
	{
		mcContacts found = new mcContacts();
		if(tag=="all") 
		{
			found = allcontactlist.createCopy();
			return found;
		}
		else if(tag=="selection") 
		{
			if(searchresultlist.size() >0)
			{
			found = searchresultlist.createCopy();
			}
			else 
				found = allcontactlist.createCopy();
			return found;
		}
		TreeSet<String> foundids =  allcontactlist.searchTags(tag);
		for (String id : foundids)
		{	
			//System.out.println(" foundid " + id);
			mcContact fcontact = this.FindbyID(id);
			if (fcontact != null)
				found.put(fcontact.getIDstr(), fcontact);
			else
				System.out.println(" problem with " + tag);
		}
		return found;
	}

	public void setAllcontactlist(mcContacts allcontactlist)
	{
		this.allcontactlist = allcontactlist;
	}

	public void refreshAllContacts(String flag)
	{
		allcontactlist.refreshAllContacts();
		System.out.println("refreshallcontacts "+flag);
		//String[] filter = { "group" };
		//allgrouplist = new mcContacts();
		//allgrouplist.setGroupFilter(filter);
		//allgrouplist.filterContacts(allcontactlist);
	}

	public void setBrowsecontactlist(mcContacts browsecontactlist)
	{
		this.browsecontactlist = browsecontactlist;
	}

	

	public void setEnabled(boolean b)
	{
		contactselectbox.setEnabled(b);
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
		selcontact = FindbyIDstr(selcon);
	}

	public void setSelectedcontactlist(mcContacts selectedcontactlist)
	{
		this.searchresultlist = selectedcontactlist;
	}

	public void update()
	{
		
		allcontacts.setText("All Contacts " + allcontactlist.size());
		browsecontacts.setText("Browse Contacts " + browsecontactlist.size());
		
			contactselectbox.setContactList(browsecontactlist);;
		
		if (contactselectbox.countSize() < 1)
		{
			//contactselect.setList(currentcontactlist.makeOrderedContactsVector());
		}
		if (selcontact != null)
		{
			contactselectbox.setSelected(selcontact);
		} else
		{
			if (contactselectbox.countSize() > 0)
			{
				contactselectbox.setSelected(0);
				selcontact = contactselectbox.getSelectedValue();
			}
		}
		contactselectbox.addActionListener(this);
		filterbox.clear();
		// System.out.println(" selcontact3c " + selcontact);
	}

	

	
	public void setBrowseFilter(jswDropDownBox sb)
	{
		browsefilter = sb.getSelectedValue();
		
	}

	public void setBrowseFilter(String string)
	{
		browsefilter = string;
		
	}

	public void setTaglist()
	{
		mctagList taglist = new mctagList();
        taglist.reloadTags();
        browseselectbox.addElement("all");
		browseselectbox.addElement("selection");
		browseselectbox.addElement("friend");
		browseselectbox.addList(taglist.getTaglist());
		
	}

	

	


	
	

	

}
