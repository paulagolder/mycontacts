package org.lerot.mycontact.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JButton;

import org.lerot.mywidgets.jswButton;
import org.lerot.mywidgets.jswDropDownBox;
import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswPanel;
import org.lerot.mywidgets.jswStyle;
import org.lerot.mywidgets.jswStyles;
import org.lerot.mywidgets.jswTextBox;
import org.lerot.mywidgets.jswTextField;
import org.lerot.mywidgets.jswVerticalPanel;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcImports;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.mctagList;
import org.lerot.mycontact.gui.widgets.jswDropDownContactBox;

public class selectorBox extends jswVerticalPanel
		implements ActionListener, FocusListener, KeyListener
{

	private static final long serialVersionUID = 1L;

	mcImports imported;

	private jswTextField filterbox;
	private jswDropDownBox tagselectbox;
	jswDropDownContactBox contactselectbox;
	private jswLabel allcontacts;
	private jswLabel browsecontacts;
	private mcContact selcontact;
	private JButton setposition;
	private mcContacts currentcontactlist;
	public mcContacts browsecontactlist;
	public mcContacts searchresultlist;
	private mcContacts allcontactlist;
	private String searchterm;
	private String browsefilter = "all";
	private jswHorizontalPanel filterbar;
	private jswHorizontalPanel selectbox;

	private mctagList taglist;

	public selectorBox(mcdb aparent, ActionListener al)
	{
        this.applyStyles(createStyle());
      
		currentcontactlist = new mcContacts();
		browsecontactlist = new mcContacts();
		allcontactlist = new mcContacts();
		searchresultlist = new mcContacts();
		setposition = new JButton("freddy");
		setposition.addActionListener(al);
		filterbar = new jswHorizontalPanel();
		allcontacts = new jswLabel("All Contacts");
		filterbar.add(" LEFT ", allcontacts);
		tagselectbox = new jswDropDownBox(this,"Select Group");
		tagselectbox.addActionListener(this, "BROWSESELECT");
		filterbar.add(" MIDDLE WIDTH=200 ", tagselectbox);
		browsecontacts = new jswLabel("Browsing:");
		filterbar.add(" RIGHT ", browsecontacts);
		this.add(filterbar);
		this.setVisible(true);

		selectbox = new jswHorizontalPanel();

		jswButton previous = new jswButton(this, "Previous");
		selectbox.add(" LEFT ", previous);

		filterbox = new jswTextField("stuff");
		filterbox.setEnabled(true);
		filterbox.textbox.setMinimumSize(new Dimension(40, 15));
		filterbox.addFocusListener(this);
		filterbox.addKeyListener(this);
		filterbox.setBorder(jswStyle.makeLineBorder(Color.gray, 1));
		selectbox.add(" WIDTH=90 ", filterbox);
		jswButton filterbutton = new jswButton(this, ">", "NEXTFILTER");
		//filterbutton.setPreferredSize(new Dimension(100, 15));
		filterbutton.setMinimumSize(new Dimension(40, 30));
		filterbutton.setMaximumSize(new Dimension(40, 30));
		selectbox.add("  ", filterbutton);

		contactselectbox = new jswDropDownContactBox("Select Contact", true,
				false, 240);
		selectbox.add(" FILLW=100 ", contactselectbox);
		contactselectbox.addList(browsecontactlist.makeOrderedContactsVector());
		contactselectbox.addActionListener(this, "contactselected");
		jswButton next = new jswButton(this, "Next");
		selectbox.add(" RIGHT ", next);

		this.add(selectbox);

		this.setBorder(jswStyle.makeLineBorder(Color.red, 2));

	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
        Object activeobject = evt.getSource();
    //    System.out.println(
	//			"source =  " + activeobject.toString());
		if (action.equals("BROWSESELECT"))
		{
			setBrowseFilter(tagselectbox);
			browsecontactlist = searchTag(browsefilter);
			selcontact = null;
			update();
		} else if (action.equals("REFRESH"))
		{
			// refreshSelection();
		} else if (action.equals("PREVIOUS"))
		{
			selcontact = contactselectbox.setPreviousValue();
			mcdb.topgui.aneditpanel.clearEdit();
		} else if (action.equals("NEXTFILTER"))
		{
			findnext();
			mcdb.topgui.aneditpanel.clearEdit();
		} else if (action.equals("NEXT"))
		{
			selcontact = contactselectbox.setNextValue();
			mcdb.topgui.aneditpanel.clearEdit();
		} else if (action.equals("CONTACTSELECTED"))
		{
			selcontact = contactselectbox.getSelectedValue();
			update2();

		} else
			System.out.println(
					"action  " + action + " unrecognised in selectorbox ");
		// update();
		mcdb.topgui.refreshView();
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
		// browsecontactlist.filterContacts(allcontactlist);
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

	public void findnext()
	{
		String filter = filterbox.getText();
		selcontact = contactselectbox.findNext(filter);
		contactselectbox.setSelected(selcontact);
		contactselectbox.contactddbox.setSelectedItem(selcontact);
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		Object source = e.getSource();
		if (source instanceof javax.swing.JTextField)
		{
			findnext();
			mcdb.topgui.refreshView();
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
				// System.out.println("keypressed 2 ");
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
		selectbox.setVisible(b);
	}

	public void filterboxVisible(boolean b)
	{
		filterbox.setVisible(b);
	}

	public void put(mcContact newcontact)
	{
		allcontactlist.put(newcontact);
		currentcontactlist.put(newcontact);
	}

	public void refreshAll()
	{
		/*
		 * //setAllContacts("ref"); filterbox.setAlert(false);
		 * //browsecontactlist.setGroupFilter(browseselect);
		 * browsecontactlist.clearError();
		 * browsecontactlist.filterContacts(allcontactlist);
		 * System.out.println(" browsecontacts refresh " +
		 * browsecontactlist.size()); if (browsecontactlist.textfiltererror) {
		 * System.out.println(" browse refresh textfiltererror ");
		 * filterbox.setAlert(true); }
		 */
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
		//// mcContacts currentsearchlist = browsecontactlist;
		// currentsearchlist = selectedcontactlist;

	}

	public mcContacts searchAttribute(String searchterm2)
	{
		mcContacts currentsearchlist = browsecontactlist;
		mcContacts found = currentsearchlist.searchAttribute(searchterm2);
		searchterm = searchterm2;
		return found;
	}

	public mcContacts searchTag(String tag)
	{
		mcContacts found = new mcContacts();
		if (tag == null)
		{		
			return null;
		} else if (tag == "all")
		{
			allcontactlist.refreshAllContacts();
			found = allcontactlist.createCopy();
			return found;
		} else if (tag == "selection")
		{
			if (searchresultlist.size() > 0)
			{
				found = searchresultlist.createCopy();
			} else
				found = allcontactlist.createCopy();
			return found;
		} else if (tag.startsWith("."))
		{
			TreeSet<String> foundids = mcContacts.searchAllTags(tag);
			for (String id : foundids)
			{
				mcContact fcontact = mcContact.getContact(id);
				found.put(fcontact.getIDstr(), fcontact);
			}
			return found;
		} else
		{
			TreeSet<String> foundids = allcontactlist.searchTags(tag);
			for (String id : foundids)
			{
				// System.out.println(" foundid " + id);
				mcContact fcontact = this.FindbyID(id);
				if (fcontact != null)
					found.put(fcontact.getIDstr(), fcontact);
				else
					System.out.println(" problem with " + tag);
			}
			return found;
		}
	}

	public void setAllcontactlist(mcContacts allcontactlist)
	{
		this.allcontactlist = allcontactlist;
	}

	public void refreshAllContacts(String flag)
	{
		allcontactlist.refreshAllContacts();
		//System.out.println("refreshallcontacts .." + flag);
		update();
	}

	public void setBrowsecontactlist(mcContacts browsecontactlist)
	{
		this.browsecontactlist = browsecontactlist;
	}

	@Override
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
		browsecontactlist = searchTag(browsefilter);
		contactselectbox.removeActionListener(this);
		contactselectbox.setContactList(browsecontactlist);
		contactselectbox.addActionListener(this, "contactselected");
		allcontacts.setText("All Contacts " + allcontactlist.size());
		browsecontacts.setText("Browse Contacts " + browsecontactlist.size());
		if (selcontact != null)
		{
			contactselectbox.setSelected(selcontact);
		} else
		{
			if (contactselectbox.countSize() > 0)
			{
				contactselectbox.setSelected(0);
			} else
				contactselectbox.setSelected(-1);
		}
		filterbox.clear();
	}

	public void update2()
	{
		contactselectbox.removeActionListener(this);
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
		// filterbox.clear();
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
		taglist = new mctagList();
		taglist.reloadTags();
		tagselectbox.removeActionListener(this);
		tagselectbox.addItem("all");
		tagselectbox.addItem("selection");
		tagselectbox.addItem("friend");
		tagselectbox.addList(taglist.getTaglist());
		tagselectbox.addActionListener(this);

	}

	public Vector<String> getTaglist()
	{
		return taglist.toList();
	}
	
	public jswStyle createStyle()
	{
		jswStyles panelstyles = new jswStyles("selectorbox");		
		jswStyle jswBorderStyle = panelstyles.makeStyle("borderstyle");
		jswBorderStyle.putAttribute("borderWidth", "1");
		// jswBorderStyle.putAttribute("borderColor", "#C0C0C0");
		jswBorderStyle.putAttribute("borderColor", "black");

		jswStyle hpanelStyle = panelstyles.makeStyle("hpanelstyle");
		hpanelStyle.putAttribute("padding", "5");
		hpanelStyle.putAttribute("borderWidth", "2");
		hpanelStyle.putAttribute("borderColor", "red");
		hpanelStyle.putAttribute("height", "100");
		jswStyle pbStyle = panelstyles.makeStyle("jswPushButton");
		pbStyle.putAttribute("backgroundColor", "#C0C0C0");
		pbStyle.putAttribute("fontsize", "10");
		pbStyle.putAttribute("foregroundColor", "black");
		jswStyle greenfont = panelstyles.makeStyle("greenfont");
		greenfont.putAttribute("foregroundColor", "green");
		return hpanelStyle;
	}

}
