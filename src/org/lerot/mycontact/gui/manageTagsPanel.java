package org.lerot.mycontact.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.mctagList;
import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswCheckbox;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswLabel;
import org.lerot.mycontact.gui.widgets.jswScrollPane;
import org.lerot.mycontact.gui.widgets.jswStyle;
import org.lerot.mycontact.gui.widgets.jswStyles;
import org.lerot.mycontact.gui.widgets.jswTable;
import org.lerot.mycontact.gui.widgets.jswTextField;
import org.lerot.mycontact.gui.widgets.jswVerticalPanel;

public class manageTagsPanel extends jswVerticalPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final int YES = 0;
	jswTextField searchfield;
	int nsearchcontacts;
	mcContacts found;
	JRadioButton[] options;
	private jswCheckbox[] acheck;
	private mctagList tagList;
	private jswTable atttable;
	private jswStyles tagstablestyles;
	
	

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		System.out.println("action " + action);
		int row = 0;
		if (action.equals("MERGE"))
		{
			String mergeto = null;
			for (jswCheckbox checkbox : acheck)
			{
				if (checkbox != null && checkbox.isSelected())
				{
					if (mergeto == null)
					{
						mergeto = checkbox.getTag();
						System.out.println("merge to  " + mergeto);
					} else
					{
						System.out.println("merge  " + checkbox.getTag());
						row++;
					}
				}
			}
			if (row < 1) System.out.println("nothing to merge");
			else
			{
			
				mergeto.replace(";", "");
				mergeto = mergeto+";";
				for (jswCheckbox checkbox : acheck)
				{
					if (checkbox != null && checkbox.isSelected())
					{
						String mergefrom = checkbox.getTag();
						mergefrom.replace(";", "");
						mergefrom = mergefrom+";";
						if (!mergeto.equals(mergefrom))
						{
							tagList.replaceall("tags", mergefrom, mergeto);
						}
					}
				}
			}
			System.out.println("merging");
		} else if (action.equals("DELETE"))
		{

			for (jswCheckbox checkbox : acheck)
			{
				if (checkbox != null && checkbox.isSelected())
				{
					System.out.println("delete  " + checkbox.getTag());
					row++;
				}
			}
			if (row < 1) System.out.println("nothing to delete");
			else
			{
				int n = JOptionPane.showConfirmDialog(this,
						"Do you want to delete " + row + " tags?",
						"DELETE TAGS?", JOptionPane.YES_NO_OPTION);
				if (n == YES)
				{
					for (jswCheckbox checkbox : acheck)
					{
						if (checkbox != null && checkbox.isSelected())
						{
							String todelete = checkbox.getTag();
							(new mctagList()).delete("tags", todelete);
							
						}
					}
				}
			}
			System.out.println("deleting");
		} else if (action.equals("NORM"))
		{
			tagList.renorm();
			System.out.println("norminging");
		} else if (action.equals("RENAME"))
		{
			for (jswCheckbox checkbox : acheck)
			{
				if (checkbox != null && checkbox.isSelected())
				{
					System.out.println("renaming  " + checkbox.getTag());
					row++;
				}
			}
			if (row < 1) System.out.println("nothing to rename");
			String newtag = JOptionPane.showInputDialog("Enter New tag name");
			if(newtag!=null) 
			{
			newtag.replace("#", "");
			newtag.replace(";", "");
			newtag = "#"+newtag+";";
			if (newtag.length() > 4)
			{

				for (jswCheckbox checkbox : acheck)
				{
					if (checkbox != null && checkbox.isSelected())
					{
						String changefrom = checkbox.getTag();
						tagList.replaceall("tags", changefrom, newtag);
						row++;
					}
				}
			}
			}
			
		} else
			System.out.println("label print action " + action
					+ " unrecognised ");
		tagList.reloadTags();
	
		buildTagPanel();
		atttable.repaint();
		mcdb.topgui.getContentPane().validate();
	}

	public manageTagsPanel()
	{
		tagstablestyles = makeTagsTableStyles();
		tagList = new mctagList();
		jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel("Manage Tags");
		header.add(" FILLW ", heading);
		this.add(header);
		jswHorizontalPanel scorebar = new jswHorizontalPanel();
		this.add(scorebar);
		jswLabel score;
		tagList.reloadTags();
		//tagList.getAllTags();
		if (tagList == null || tagList.isEmpty())
		{
			score = new jswLabel("No tags found ");
		} else
			score = new jswLabel(" " + tagList.size()
					+ " different tags found ");
		scorebar.add(score);
		jswHorizontalPanel printbar = new jswHorizontalPanel();
		this.add(printbar);
		found = mcdb.selbox.getSearchResultList();
		jswButton testbutton = new jswButton(this, "MERGE");
		printbar.add(" MIDDLE ", testbutton);
		jswButton deletebutton = new jswButton(this, "DELETE");
		printbar.add(" MIDDLE ", deletebutton);
		jswButton renamebutton = new jswButton(this, "RENAME");
		printbar.add(" MIDDLE ", renamebutton);
		jswButton normbutton = new jswButton(this, "NORM");
		printbar.add(" MIDDLE ", normbutton);
		atttable = new jswTable("tags", tagstablestyles);
		jswScrollPane scrollableTextArea = new jswScrollPane(atttable,
				-4, -4);
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

	public void buildTagPanel()
	{
		atttable.removeAll();
		int row = 0;
		int col=0;
		acheck = new jswCheckbox[40];
		for (Entry<String, Integer> anentry : tagList.entrySet())
		{
			String attkey = anentry.getKey();
			int attcount = anentry.getValue();
			jswLabel alabel = new jswLabel(attkey);
			jswLabel acount = new jswLabel(attcount);
			atttable.addCell(alabel, row, col);
			atttable.addCell(acount, row, col+1);
			acheck[row] = new jswCheckbox("");
			acheck[row].setTag(attkey);
			atttable.addCell(acheck[row], row, col+2);
			row++;
			if (row>10) 
{
	row=0;
	col=col+3;
}
		}
		//atttable.addCell("", 0, col+6);
		//jswStyle colxstyle = mcdb.topgui.tablestyles.makeStyle("col_"+col+6);
		//colxstyle.putAttribute("horizontalAlignment", "RIGHT");
		//colxstyle.putAttribute("minwidth", "false");
	}

	public void refresh()
	{
		tagList.reloadTags();
		buildTagPanel();
	}
	
	private jswStyles makeTagsTableStyles()
	{
		jswStyles tablestyles = new jswStyles();

		jswStyle tablestyle = tablestyles.makeStyle("table");
		tablestyle.putAttribute("backgroundColor", "White");
		tablestyle.putAttribute("foregroundColor", "Green");
		tablestyle.putAttribute("borderWidth", "2");
		tablestyle.putAttribute("borderColor", "blue");

		jswStyle cellstyle = tablestyles.makeStyle("cell");
		cellstyle.putAttribute("backgroundColor", "#C0C0C0");
		cellstyle.putAttribute("foregroundColor", "Blue");
		cellstyle.putAttribute("borderWidth", "1");
		cellstyle.putAttribute("borderColor", "white");
		cellstyle.setHorizontalAlign("LEFT");
		cellstyle.putAttribute("fontsize", "14");

		jswStyle cellcstyle = tablestyles.makeStyle("cellcontent");
		cellcstyle.putAttribute("backgroundColor", "transparent");
		cellcstyle.putAttribute("foregroundColor", "Red");
		cellcstyle.setHorizontalAlign("LEFT");
		cellcstyle.putAttribute("fontsize", "11");

		jswStyle col0style = tablestyles.makeStyle("col_0");
		col0style.putAttribute("fontStyle", Font.BOLD);
		col0style.setHorizontalAlign("RIGHT");
		col0style.putAttribute("minwidth", "true");
		col0style.putAttribute("width", 10);
		col0style.putAttribute("foregroundColor", "green");

		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("LEFT");
		col1style.putAttribute("horizontalAlignment", "LEFT");

		jswStyle col2style = tablestyles.makeStyle("col_2");
		col2style.putAttribute("horizontalAlignment", "RIGHT");
		col2style.putAttribute("minwidth", "true");		
		 tablestyles.copyStyle("col_0","col_3");		
		tablestyles.copyStyle("col_1","col_4");;
		tablestyles.copyStyle("col_2","col_5");		
		tablestyles.copyStyle("col_0","col_6");
         tablestyles.copyStyle("col_1","col_7");
	     tablestyles.copyStyle("col_2","col_8"); 
	
		return tablestyles;
	}
}
