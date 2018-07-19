package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.mctagList;
import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswCheckbox;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswLabel;
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
	private Map<String, Integer> taglist;
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
				for (jswCheckbox checkbox : acheck)
				{
					if (checkbox != null && checkbox.isSelected())
					{
						String mergefrom = checkbox.getTag();
						if (!mergeto.equals(mergefrom))
						{
							mctagList.replaceall("tags", mergefrom, mergeto);
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
							mctagList.delete("tags", todelete);
						}
					}
				}
			}
			System.out.println("deleting");
		} else if (action.equals("NORM"))
		{
			mctagList.renorm();
			System.out.println("norminging");
		} else if (action.equals("RENAME"))
		{
			for (jswCheckbox checkbox : acheck)
			{
				if (checkbox != null && checkbox.isSelected())
				{
					System.out.println("rename  " + checkbox.getTag());
					row++;
				}
			}
			if (row < 1) System.out.println("nothing to rename");
			String newtag = JOptionPane.showInputDialog("Enter New tag name");
			newtag.replace("#", "");
			newtag.replace(";", "");
			if (newtag.length() > 4)
			{

				for (jswCheckbox checkbox : acheck)
				{
					if (checkbox != null && checkbox.isSelected())
					{
						String changefrom = checkbox.getTag();
						mctagList.replaceall("tags", changefrom, newtag);
						row++;
					}
				}
			}
			// System.out.println("rename to " + path);
		} else
			System.out.println("label print action " + action
					+ " unrecognised ");
		mctagList.reloadTags();
		taglist = mctagList.getAllTags();
		buildTagPanel();
		atttable.repaint();
		mcdb.topgui.getContentPane().validate();
	}

	public manageTagsPanel()
	{

		jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel("Manage Tags");
		header.add(" FILLW ", heading);
		this.add(header);
		jswHorizontalPanel scorebar = new jswHorizontalPanel();
		this.add(scorebar);
		jswLabel score;
		if (taglist == null || taglist.isEmpty())
		{
			score = new jswLabel("No tags found ");
		} else
			score = new jswLabel(" " + taglist.size()
					+ " different tags found ");
		scorebar.add(score);
		jswHorizontalPanel printbar = new jswHorizontalPanel();
		this.add(printbar);
		found = mcdb.selbox.getSelectedcontactlist();
		jswButton testbutton = new jswButton(this, "MERGE");
		printbar.add(" MIDDLE ", testbutton);
		jswButton deletebutton = new jswButton(this, "DELETE");
		printbar.add(" MIDDLE ", deletebutton);
		jswButton renamebutton = new jswButton(this, "RENAME");
		printbar.add(" MIDDLE ", renamebutton);
		jswButton normbutton = new jswButton(this, "NORM");
		printbar.add(" MIDDLE ", normbutton);
		atttable = new jswTable("tags", mcdb.topgui.tablestyles);
		this.add(atttable);

		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}

	public void buildTagPanel()
	{
		atttable.removeAll();
		int row = 0;
		acheck = new jswCheckbox[40];
		for (Entry<String, Integer> anentry : taglist.entrySet())
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
		mctagList.reloadTags();
		taglist = mctagList.getAllTags();
		buildTagPanel();
	}
}
