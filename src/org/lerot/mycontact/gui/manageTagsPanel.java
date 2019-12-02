package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.mctagList;
import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswCheckbox;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswLabel;
import org.lerot.mycontact.gui.widgets.jswScrollPane;
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
		atttable = new jswTable("tags", mcdb.topgui.tablestyles);
		jswScrollPane scrollableTextArea = new jswScrollPane(atttable,
				-4, -4);
		scrollableTextArea.setName("resultscroll");
		scrollableTextArea
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollableTextArea
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.add(" SCROLLH ", scrollableTextArea);
		


		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}

	public void buildTagPanel()
	{
		atttable.removeAll();
		int row = 0;
		acheck = new jswCheckbox[40];
		for (Entry<String, Integer> anentry : tagList.entrySet())
		{
			String attkey = anentry.getKey();
			int attcount = anentry.getValue();
			jswLabel alabel = new jswLabel(attkey);
			jswLabel acount = new jswLabel(attcount);
			atttable.addCell(alabel, row, 0);
			atttable.addCell(acount, row, 1);
			acheck[row] = new jswCheckbox("");
			acheck[row].setTag(attkey);
			atttable.addCell(acheck[row], row, 2);
			row++;
		}

	}

	public void refresh()
	{
		tagList.reloadTags();
		buildTagPanel();
	}
}
