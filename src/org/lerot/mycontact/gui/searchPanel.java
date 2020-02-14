package org.lerot.mycontact.gui;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.lerot.gui.widgets.jswButton;
import org.lerot.gui.widgets.jswHorizontalPanel;
import org.lerot.gui.widgets.jswLabel;
import org.lerot.gui.widgets.jswPanel;
import org.lerot.gui.widgets.jswScrollPane;
import org.lerot.gui.widgets.jswStyle;
import org.lerot.gui.widgets.jswStyles;
import org.lerot.gui.widgets.jswTable;
import org.lerot.gui.widgets.jswTextField;
import org.lerot.gui.widgets.jswVerticalPanel;
import org.lerot.mycontact.mcAttribute;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.gui.selectorBox;
import org.lerot.mycontact.mcdb;

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
				mcdb.selbox.clearSearchResultList();
                mcdb.selbox.setSearchterm("");
			}
			else 
			{
				mcdb.selbox.searchresultlist = mcdb.selbox.searchAttribute(searchterm);
			   // mcdb.selbox.browsecontactlist = mcdb.selbox.searchresultlist;
			}
			// if (!searcherror)
			{
				searchfield.clear();
			}
		} 
		else if (action.startsWith("TAGSELECTION"))
		{
			mcContacts sellist = mcdb.selbox.getSearchResultList();
			for(Entry<String, mcContact> contactentry : sellist.entrySet())
			{
				mcContact scontact = contactentry .getValue();
				mcAttribute tags = scontact.getAttribute("tags","");
				if(tags == null)
				{
					tags = new mcAttribute(scontact.getID(), "tags", "");
				}
				tags.addTag("selected");
				tags.dbupdateAttribute();
			}
		}
		else if (action.startsWith("SEARCHTAGS"))
		{
			String searchterm = searchfield.getText();
			if(searchterm.isEmpty())
			{
				mcdb.selbox.clearSearchResultList();
                mcdb.selbox.setSearchterm("");
			}
			else 
				mcdb.selbox.searchresultlist = mcdb.selbox.searchTag(searchterm);
			// if (!searcherror)
			{
				searchfield.clear();
			}
		} else if (action.startsWith("REMOVE:"))
		{
			System.out.println("search panel remove " + action);
			String selcon = action.substring(7);
			mcdb.selbox.removesearchcontact(selcon);
			mcdb.topgui.refreshView();

		} else if (action.startsWith("VIEW:"))
		{
			String selcon = action.substring(5);
			mcdb.topgui.mode = "BROWSE";
			mcdb.selbox.setSelcontact(selcon);
			//mcContact scon = mcdb.selbox.getSelcontact();
			mcdb.topgui.refreshView();
		} else
			System.out.println("search action " + action + " unrecognised ");
		mcdb.topgui.asearchpanel.makesearchPanel(mcdb.selbox, parentlistener);
		mcdb.topgui.getContentPane().validate();
	}

	public void makesearchPanel(selectorBox selbox, ActionListener alistener)
	{
		jswStyle scrollstyle = jswStyles.getStyles("allstyles").getStyle("jswScrollPaneStyles");
		Color bcolor = scrollstyle.getColor("backgroundColor", Color.BLUE);
		setBackground(bcolor);
		parentlistener = alistener;
		jswVerticalPanel searchpanel = this;
		//this.setTag("trace");
		//this.setBorder(setLineBorder(Color.red, 4));
		searchpanel.removeAll();
		jswHorizontalPanel idbox = new jswHorizontalPanel("idbox", false);
	
		//idbox.setTag("trace");
		searchpanel.add(idbox);
		jswLabel idpanel1 = new jswLabel("Enter Search Term:");
		idbox.add(idpanel1);
		
		searchfield = new jswTextField("Search for");
	//	searchfield.setBorder(jswPanel.setLineBorder(Color.red, 5));
		if(selbox.getSearchterm()!= null && !selbox.getSearchterm().isEmpty())
		{
		searchfield.setText(selbox.getSearchterm());
		}
		idbox.add(" FILLW WIDTH=30 ", searchfield);
		searchfield.setEnabled(true);
		searchfield.setVisible(true);
		jswButton searchbutton = new jswButton(this, "Search","SEARCHATTRIBUTES");
		idbox.add(" RIGHT ", searchbutton);
		jswButton searchtagbutton = new jswButton(this, "Search Tags","SEARCHTAGS");
		idbox.add(" RIGHT ", searchtagbutton);
		jswHorizontalPanel summary = new jswHorizontalPanel();
		mcContacts sellist = selbox.getSearchResultList();
		if(sellist.size()>0)
		{
			jswButton tagbutton = new jswButton(this, "Tag selection","TAGSELECTION");
			summary.add(" LEFT ", tagbutton);	
		}
		jswLabel summ = new jswLabel(" Total Found ="
				+ sellist.size());
		summary.add(" FILLW ", summ);
		searchpanel.add(summary);
		jswTable resulttable = new jswTable("contactsfound",
				mcdb.topgui.tablestyles);
	
		if (sellist.size() == 0)
		{
			searchpanel.add(" FILLW ", resulttable);
			resulttable.addCell(new jswLabel(" no contact selected "), 0, 0);
		} else 
		{
			int row=0;
		
			for (mcContact acontact : selbox.getSearchResultList()
					.makeOrderedContactsVector())
			{
				jswLabel atid = new jswLabel(acontact.getIDstr());
				jswLabel atTID = new jswLabel(acontact.getTID());
				resulttable.addCell(atid, row, 0);
				resulttable.addCell(atTID, " FILLW ", row, 1);
				jswButton viewcontact = new jswButton(this, "VIEW", "VIEW:"
						+ acontact.getIDstr());
				resulttable.addCell(viewcontact, row, 2);
				jswButton removecontact = new jswButton(this, "REMOVE",
						"REMOVE:" + acontact.getIDstr());
				resulttable.addCell(removecontact, row, 3);
				row++;
			}
			
			
			
			if (sellist.size() <10)
		{
			searchpanel.add(" FILLW ", resulttable);
		
		} else
		{
			resulttable.setBackground(Color.lightGray);
			jswScrollPane scrollableTextArea = new jswScrollPane(resulttable,
					-10, -10);
			scrollableTextArea.setName("resultscroll");
			scrollableTextArea
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollableTextArea
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			searchpanel.add(" FILLH ", scrollableTextArea);
			//scrollableTextArea.setBorder(setLineBorder(Color.red, 4));
		}}
		resulttable.repaint();
		searchpanel.repaint();
		mcdb.topgui.pack();
		mcdb.topgui.setVisible(true);
		mcdb.topgui.mainpanel.repaint();
		this.repaint();
		mcdb.topgui.getContentPane().validate();
	}

}
