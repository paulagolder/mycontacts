package org.lerot.mycontact.gui;
import org.lerot.mycontact.gui.widgets.jswHorizontalLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;

import org.lerot.mycontact.mcAttribute;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcSelectorBox;
import org.lerot.mycontact.mcTagListDataType;
import org.lerot.mycontact.mcTextListDataType;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.mctagList;
import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswCheckbox;
import org.lerot.mycontact.gui.widgets.jswDropDownBox;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswLabel;
import org.lerot.mycontact.gui.widgets.jswScrollPane;
import org.lerot.mycontact.gui.widgets.jswTable;
import org.lerot.mycontact.gui.widgets.jswTextField;
import org.lerot.mycontact.gui.widgets.jswVerticalPanel;

public class editListPanel extends jswVerticalPanel implements
ActionListener
{
private static final long serialVersionUID = 1L;

private jswDropDownBox taglistbox;
private String  tag;
jswTextField searchfield;
private jswTable atttable;


public editListPanel()
{

jswHorizontalPanel header = new jswHorizontalPanel();
jswLabel heading = new jswLabel(" Edit Lists (Tags) ");
header.add(" FILLW ", heading);
this.add(header);
//this.setTag("trace");

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
		.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
scrollableTextArea
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
this.add(" SCROLLH ", scrollableTextArea);

this.repaint();
mcdb.topgui.mainpanel.repaint();
mcdb.topgui.getContentPane().validate();
//taglist.addActionListener(this, "selectlist");
}

@Override
public void actionPerformed(ActionEvent evt)
{
String action = evt.getActionCommand().toUpperCase();

if (action.equals("SELECTLIST"))
{
	 //System.out.println("action " + action);
	 tag = taglistbox.getSelectedValue();
	 //System.out.println("tag " + tag);
	  displaylist(tag);
	
} 
else if (action.startsWith("REMOVE"))
{
	 String cnstr = action.substring(6);	
	 System.out.println("remove " + cnstr);
	 mcContact selcon = mcdb.selbox.getAllcontactlist().FindbystrID(cnstr);
	 mcAttribute tagatt = selcon.getAttribute("tags", "");
	 Set<String> tags = tagatt.getTags();
	 tags.remove(tag);
	 String tagstr =  mcTagListDataType.makeTagString(tags);
	 tagatt.setValue(tagstr);
	 tagatt.dbupsertAttribute();
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

	mcContacts found =  mcdb.selbox.searchTag(tag);
	//System.out.println("found "+ found.size());
	atttable.removeAll();
	int i=0;
//	atttable.addCell(aname,i,0);
//++;
	for(Entry<String, mcContact> contactentry : found.entrySet())
	{
		
		mcContact ct = contactentry.getValue();
		//System.out.println("contactfound " +ct.toString());
		String cname = contactentry.getValue().getName();
		atttable.addCell(cname,i,0);
		jswButton removecontact = new jswButton(this, "REMOVE",
				"REMOVE:" + ct.getIDstr());
		atttable.addCell(removecontact, i, 2);
		i++;
	}
	atttable.repaint();
}





}

