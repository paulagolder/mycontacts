package org.lerot.mycontact.gui;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.lerot.gui.widgets.jswButton;
import org.lerot.gui.widgets.jswDropDownBox;
import org.lerot.gui.widgets.jswHorizontalPanel;
import org.lerot.gui.widgets.jswLabel;
import org.lerot.gui.widgets.jswScrollPane;
import org.lerot.gui.widgets.jswTable;
import org.lerot.gui.widgets.jswTextField;
import org.lerot.gui.widgets.jswVerticalPanel;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.mctagList;

public class editListPanel extends jswVerticalPanel implements
ActionListener
{
private static final long serialVersionUID = 1L;

private static final int YES = 0;

private jswDropDownBox taglistbox;
private String  tag;
jswTextField searchfield;
private jswTable atttable;

private jswButton deletebutton;

private mcContacts selectedcontacts;


public editListPanel()
{

jswHorizontalPanel header = new jswHorizontalPanel();
jswLabel heading = new jswLabel(" Edit Lists (Tags) ");
header.add(" FILLW ", heading);
this.add(header);

jswHorizontalPanel toolbar = new jswHorizontalPanel("toolbar",false);
deletebutton = new jswButton(this,"delete all","deleteall");
toolbar.add(deletebutton);
this.add(" FILLW ",toolbar);
jswHorizontalPanel progressbar = new jswHorizontalPanel("progressbar",false);

this.add(" FILLW ",progressbar);
taglistbox = new jswDropDownBox ("tags",true,true);
taglistbox.addActionListener(this,"selectlist");
progressbar.add(" FILLW ", taglistbox);
atttable = new jswTable("members",mcdb.topgui.tablestyles);
atttable.setBackground(Color.lightGray);
jswScrollPane scrollableTextArea = new jswScrollPane(atttable,
		-10, -10);
scrollableTextArea.setName("resultscroll");
scrollableTextArea
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
scrollableTextArea
		.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
this.add(" SCROLLH ", scrollableTextArea);

this.repaint();
mcdb.topgui.mainpanel.repaint();
mcdb.topgui.getContentPane().validate();

}

@Override
public void actionPerformed(ActionEvent evt)
{
String action = evt.getActionCommand().toUpperCase();

if (action.equals("SELECTLIST"))
{
	 tag = taglistbox.getSelectedValue();
	  displaylist(tag);
} 
else if (action.equals("DELETEALL"))
{
	 String cnstr = action.substring(7);	
	 System.out.println("delete all listed contact ");
	int dcount  = taglistbox.getItemCount();
		int n = JOptionPane.showConfirmDialog(this,
				"Do you want to delete "+dcount+ " contacts",
				"DELETE CONTACTS?", JOptionPane.YES_NO_OPTION);
		System.out.println("reply =" + n);
		if (n == YES)
		{
			selectedcontacts.deleteAllContacts();
		}
	 displaylist(tag);
} 
else if (action.startsWith("DELETE"))
{
	 String cnstr = action.substring(7);	
	 System.out.println("delete contact " + cnstr);
	 mcContact selcon = mcContacts.retrieveContact(cnstr);
		int n = JOptionPane.showConfirmDialog(this,
				"Do you want to delete contact:"+selcon.getName(),
				"DELETE CONTACT?", JOptionPane.YES_NO_OPTION);

		if (n == YES)
		{
			selcon.deleteContact();
			System.out.println("reply =" + n);
		}
	 displaylist(tag);
} else if (action.startsWith("REMOVE"))
{
	 String cnstr = action.substring(7);	
	 System.out.println("remove contact " + cnstr);
	 mcContact selcon = mcContacts.retrieveContact(cnstr);
		int n = JOptionPane.showConfirmDialog(this,
				"Do you want to remove tag "+tag+" from :"+selcon.getName(),
				"REMOVE CONTACT?", JOptionPane.YES_NO_OPTION);
		if (n == YES)
		{
			selcon.deleteTag(tag);
			System.out.println("reply =" + n);
		}
	 displaylist(tag);
} else
	System.out.println("contact  action " + action
			+ " unrecognised ");

mcdb.topgui.getContentPane().validate();
}



public void initialise()
{

	mctagList tags = new mctagList();
	tags.reloadTags();
	taglistbox.setList(tags.getTaglist());
	

this.repaint();
mcdb.topgui.mainpanel.repaint();
mcdb.topgui.getContentPane().validate();
//taglist.addActionListener(this, "selectlist");

}


public void displaylist(String tag)
{

	selectedcontacts =  mcdb.selbox.searchTag(tag);
	atttable.removeAll();
	int i=0;
	for(Entry<String, mcContact> contactentry : selectedcontacts.entrySet())
	{
		
		mcContact ct = contactentry.getValue();
		String cname = contactentry.getValue().getName();
		atttable.addCell(cname,i,0);
		jswButton removecontact = new jswButton(this, "REMOVE",
				"REMOVE:" + ct.getIDstr());
		atttable.addCell(removecontact, i, 2);
		jswButton deletecontact = new jswButton(this, "DELETE",
				"DELETE:" + ct.getIDstr());
		atttable.addCell(deletecontact, i, 3);
		i++;
	}
	atttable.repaint();
}





}

