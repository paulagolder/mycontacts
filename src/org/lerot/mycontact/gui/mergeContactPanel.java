package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.lerot.mywidgets.jswButton;
import org.lerot.mywidgets.jswCheckbox;
import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswImage;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswTable;
import org.lerot.mywidgets.jswTextField;
import org.lerot.mywidgets.jswVerticalPanel;
import org.lerot.mycontact.mcAttribute;
import org.lerot.mycontact.mcAttributes;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcdb;

public class mergeContactPanel extends jswVerticalPanel implements
		ActionListener
{
	private static final long serialVersionUID = 1L;
	private ButtonGroup bg;
	private jswCheckbox[] checkboxlist;
	mcContacts found;
	private Vector<mcContact> mergefromlist;
	int nsearchcontacts;
	JRadioButton[] options;
	private jswLabel prog;
	jswTextField searchfield;
	private jswTable resulttable;

	public mergeContactPanel()
	{

		jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel(" Merge Contacts ");
		header.add(" FILLW ", heading);
		this.add(header);
		jswHorizontalPanel printbar = new jswHorizontalPanel();
		this.add(printbar);
		jswButton testbutton = new jswButton(this, "MERGE");
		printbar.add(" MIDDLE ", testbutton);
		resulttable = new jswTable("contactsfound", mcdb.topgui.tablestyles);

		this.add(resulttable);
		jswHorizontalPanel progressbar = new jswHorizontalPanel();
		this.add(progressbar);
		prog = new jswLabel(" Selecting contacts to merge");
		progressbar.add(" FILLW ", prog);
		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		// TreeSet<mcContact> found = new TreeSet<mcContact>();
		System.out.println("action " + action);
		if (action.equals("MERGE"))
		{
			mergefromlist = new Vector<mcContact>();
			mcContact mergeto = null;
			int row = 0;
			for (mcContact acontact : found.makeOrderedContactsVector())
			{
				if (options[row].isSelected())
				{
					System.out.println("merge to " + acontact);
					mergeto = acontact;
				}
				if (checkboxlist[row].isSelected())
				{
					mergefromlist.add(acontact);
				}
				row++;
			}
			if (mergeto != null && mergefromlist.size() > 0)
			{
				for (mcContact acontact : mergefromlist)
				{
					mergeto.mergeContact(acontact);
					System.out.println("deleteing" + acontact);
					mcdb.selbox.remove(acontact);
				}
			}
			System.out.println("merging");
		} else
			System.out.println("contact merge action " + action
					+ " unrecognised ");

		mcdb.topgui.getContentPane().validate();
	}

	private jswTable makeAttributePanel(mcContact contact, String selector)
	{
		jswTable attributepanel = new jswTable("attributes",
				mcdb.topgui.tablestyles);
		mcAttributes attributes = contact.getAttributes();
		int row = 0;

		for (Entry<String, mcAttribute> anentry : attributes.entrySet())
		{
			mcAttribute anattribute = anentry.getValue();
			if (anattribute != null)
			{

				String attributekey = anattribute.getKey();
				jswLabel alabel = new jswLabel(attributekey);
				attributepanel.addCell(alabel, row, 0);
				if (anattribute.isImage())
				{
					jswImage animage = new jswImage(anattribute.getValue());
					attributepanel.addCell(animage.DisplayImage(), row, 1);
				} else
				{
					String value = anattribute.getFormattedValue();
					attributepanel.addCell(new jswLabel(value), row, 1);
				}
				row++;

			}

		}
		return attributepanel;
	}

	public void refresh()
	{
		nsearchcontacts = mcdb.selbox.getSearchResultList().size();
		found = mcdb.selbox.getSearchResultList();

		resulttable.removeAll();
		if (nsearchcontacts == 0)
		{
			resulttable.addCell(new jswLabel(" no contact selected "), 0, 0);
		} else if (nsearchcontacts < 4)
		{
			checkboxlist = new jswCheckbox[nsearchcontacts];
			bg = new ButtonGroup();
			options = new JRadioButton[nsearchcontacts];
			// this.setPreferredSize(new Dimension(0, 800));
			// resulttable.setPreferredSize(new Dimension(0, 800));

			int row = 0;
			for (mcContact acontact : found.makeOrderedContactsVector())
			{
				jswLabel atid = new jswLabel(acontact.getIDstr());
				jswLabel atTID = new jswLabel(acontact.getTID());
				resulttable.addCell(atid, row, 0);
				resulttable.addCell(atTID, row, 1);
				jswTable atttable = makeAttributePanel(acontact, "");
				resulttable.addCell(atttable, row, 2);
				jswHorizontalPanel optionpanel = new jswHorizontalPanel();
				checkboxlist[row] = new jswCheckbox("from");
				options[row] = new JRadioButton("to", false);
				options[row].setToolTipText(acontact.getIDstr());
				options[row].addActionListener(this);
				bg.add(options[row]);
				optionpanel.add(checkboxlist[row]);
				optionpanel.add(options[row]);
				resulttable.addCell(optionpanel, row, 3);
				row++;
			}

		} else
		{
			resulttable.addCell(new jswLabel(" Too many contacts selected (>3) "),
					0, 0);
		}

		this.repaint();
		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}
}
