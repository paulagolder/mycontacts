package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;

import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcSelectorBox;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswLabel;
import org.lerot.mycontact.gui.widgets.jswScrollPane;
import org.lerot.mycontact.gui.widgets.jswTable;
import org.lerot.mycontact.gui.widgets.jswTextField;
import org.lerot.mycontact.gui.widgets.jswVerticalPanel;

public class searchPanel extends jswVerticalPanel implements ActionListener
{

	private static final long serialVersionUID = 1L;
	private ActionListener parentlistener;
	jswTextField searchfield;

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		if (action.startsWith("SEARCHATTRIBUTES"))
		{
			String searchterm = searchfield.getText();
			if(searchterm.isEmpty())
			{
				mcdb.selbox.clearSelectedContactList();
                mcdb.selbox.setSearchterm("");
			}
			else 
			   mcdb.selbox.searchAttribute(searchterm);
			// if (!searcherror)
			{
				searchfield.clear();
			}
		} 
		else if (action.startsWith("SEARCHTAGS"))
		{
			String searchterm = searchfield.getText();
			if(searchterm.isEmpty())
			{
				mcdb.selbox.clearSelectedContactList();
                mcdb.selbox.setSearchterm("");
			}
			else 
			mcdb.selbox.searchTag(searchterm);
			// if (!searcherror)
			{
				searchfield.clear();
			}
		} else if (action.startsWith("REMOVE:"))
		{
			System.out.println("search panel remove " + action);
			String selcon = action.substring(7);
			mcdb.selbox.removesearchcontact(selcon);
			mcdb.topgui.refresh();

		} else if (action.startsWith("VIEW:"))
		{
			String selcon = action.substring(5);
			mcdb.topgui.mode = "BROWSE";
			mcdb.selbox.setBrowseStatus("SELECTED");
			mcdb.selbox.setSelcontact(selcon);
		//	System.out.println("search panel view " + selcon);
			mcdb.topgui.refresh();
		} else
			System.out.println("search action " + action + " unrecognised ");
		mcdb.topgui.asearchpanel.makesearchPanel(mcdb.selbox, parentlistener);
		mcdb.topgui.getContentPane().validate();
	}

	public void makesearchPanel(mcSelectorBox selbox, ActionListener alistener)
	{
       
		parentlistener = alistener;
		jswVerticalPanel searchpanel = this;
		// this.setTag("trace");
		// this.setBorder(setLineBorder(Color.red, 4));
		searchpanel.removeAll();
		jswHorizontalPanel idbox = new jswHorizontalPanel("idbox", false);
		searchpanel.add(idbox);
		jswLabel idpanel1 = new jswLabel("Enter Search Term:");
		idbox.add(idpanel1);
		searchfield = new jswTextField("Search for");
		if(selbox.getSearchterm()!= null && !selbox.getSearchterm().isEmpty())
		{
		searchfield.setText(selbox.getSearchterm());
		}
		idbox.add(" FILLW ", searchfield);
		searchfield.setEnabled(true);
		jswButton searchbutton = new jswButton(this, "Search","SEARCHATTRIBUTES");
		idbox.add(" RIGHT ", searchbutton);
		jswButton searchtagbutton = new jswButton(this, "Search Tags","SEARCHTAGS");
		idbox.add(" RIGHT ", searchtagbutton);
		jswHorizontalPanel summary = new jswHorizontalPanel();
		mcContacts sellist = selbox.getSelectedcontactlist();
		jswLabel summ = new jswLabel(" Total Found ="
				+ sellist.size());
		summary.add(" FILLW ", summ);
		searchpanel.add(summary);
		jswTable resulttable = new jswTable("contactsfound",
				mcdb.topgui.tablestyles);
		resulttable.setTag("trace");
		if (sellist.size() == 0)
		{
			searchpanel.add(" FILLW ", resulttable);
			resulttable.addCell(new jswLabel(" no contact selected "), 0, 0);
		} else if (sellist.size() < 6)
		{
			searchpanel.add(" FILLW ", resulttable);
			int row = 0;
			for (mcContact acontact : selbox.getSelectedcontactlist()
					.makeOrderedContactsVector())
			{
				jswLabel atid = new jswLabel(acontact.getIDstr());
				jswLabel atTID = new jswLabel(acontact.getTID());
				resulttable.addCell(atid, row, 0);
				resulttable.addCell(atTID, " FILLW ", row, 1);
				jswButton viewcontact = new jswButton(this, "VIEW", "VIEW:"
						+ acontact.getTID());
				resulttable.addCell(viewcontact, row, 2);
				jswButton removecontact = new jswButton(this, "REMOVE",
						"REMOVE:" + acontact.getIDstr());
				resulttable.addCell(removecontact, row, 3);
				row++;
			}

		} else
		{
			jswScrollPane scrollableTextArea = new jswScrollPane(resulttable,
					-4, -4);
			scrollableTextArea.setName("resultscroll");
			scrollableTextArea
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollableTextArea
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			searchpanel.add(" SCROLLH ", scrollableTextArea);
			int row = 0;
			for (mcContact acontact : selbox.getSelectedcontactlist()
					.makeOrderedContactsVector())
			{
				if (acontact != null)
				{
					jswLabel atid = new jswLabel(acontact.getIDstr());
					jswLabel atTID = new jswLabel(acontact.getTID());
					resulttable.addCell(atid, row, 0);
					resulttable.addCell(atTID, " FILLW ", row, 1);
					jswButton viewcontact = new jswButton(this,
							"VIEW", "VIEW:" + acontact.getIDstr());
					resulttable.addCell(viewcontact, row, 2);
					jswButton removecontact = new jswButton(this, "REMOVE",
							"REMOVE:" + acontact.getIDstr());
					resulttable.addCell(removecontact, row, 3);
					row++;
				}
			}
		}
		resulttable.repaint();
		searchpanel.repaint();
		mcdb.topgui.mainpanel.repaint();
	}

}
