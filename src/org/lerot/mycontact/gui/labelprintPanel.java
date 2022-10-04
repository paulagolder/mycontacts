package org.lerot.mycontact.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lerot.mywidgets.jswButton;
import org.lerot.mywidgets.jswCheckbox;
import org.lerot.mywidgets.jswDropDownBox;
import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswTextField;
import org.lerot.mywidgets.jswThumbwheel;
import org.lerot.mywidgets.jswVerticalPanel;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcLetter;
import org.lerot.mycontact.mcPDF;
import org.lerot.mycontact.mcdb;

public class labelprintPanel extends jswVerticalPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private ActionListener parentlistener;
	jswTextField searchfield;

	private jswButton selbutton;
	private jswTextField selectedfile;
	private File exportfile;
	private jswLabel prog;
	private jswDropDownBox layoutpanel;
	private jswThumbwheel startpos;
	private jswLabel selectedcontacts;
	private jswCheckbox showcountry;
	private mcPDF labelpages;
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		// TreeSet<mcContact> found = new TreeSet<mcContact>();
		System.out.println("action " + action);
		if (action.equals("SELECT"))
		{
			JFileChooser fc = new JFileChooser(mcdb.letterfolder);
			fc.setDialogTitle("Specify a file to save label");
			String labelname = mcLetter.makeFileName("Labels");
			fc.setSelectedFile(
					new File(mcdb.letterfolder + "/" + labelname + ".pdf"));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF",
					"pdf");
			fc.setDialogTitle("Specify a file to save");
			fc.setFileFilter(filter);
			int returnVal = fc.showSaveDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File fileToSave = fc.getSelectedFile();
				selectedfile.setText(fileToSave.getPath());
				exportfile = fileToSave;
				String filename = selectedfile.getText();
				File afile = new File(filename);
				//labelpages = new mcPDF(afile, "Lerot Contacts Labels");
						
			} else
			{
				System.out.println("Open command cancelled by user.");
			}
		} else if (action.startsWith("PRINT"))
		{
			prog.setText(" Printing ");
			mcdb.labeltemplates =  mcPDF.readTemplates();
			String filename = selectedfile.getText();
			File afile = new File(filename);
			int sp = startpos.getValue();
			boolean showcountryselected = showcountry.isSelected();
			String pagelayout = layoutpanel.getSelectedValue();
			labelpages = new mcPDF(afile, "Lerot Contacts Labels");
			labelpages.setLayout(pagelayout);
			mcContacts sellist = mcdb.selbox.getBrowsecontactlist();
			System.out.println("label print : " + sellist.size());
			int ncount = labelpages.makeLabelsPages(
					sellist, sp,showcountryselected);
			prog.setText(" Printing complete " + ncount + " pages");
		} else
			System.out.println("label print action " + action
					+ " unrecognised ");

		mcdb.topgui.getContentPane().validate();
	}

	public labelprintPanel()
	{
		int ncontacts = mcdb.selbox.countAll();
		int nbrowsecontacts = mcdb.selbox.getBrowsecontactlist().size();
		//String searchterm = mcdb.selbox.getSearchterm();
		this.removeAll();
		jswHorizontalPanel header = new jswHorizontalPanel();
		jswLabel heading = new jswLabel(" Print address Labels ");
		header.add(" FILLW ", heading);
		this.add(header);
		jswHorizontalPanel filterbar = new jswHorizontalPanel();
		
		selectedcontacts = new jswLabel("Selected Contacts "
				+ nbrowsecontacts);
		
		filterbar.add(" MIDDLE ", selectedcontacts);
		add(filterbar);
		jswHorizontalPanel filebar = new jswHorizontalPanel();
		selbutton = new jswButton(this, "Select");
		filebar.add(" LEFT ", selbutton);
		selectedfile = new jswTextField();
		selectedfile.setText("Output File");
		selectedfile.setEnabled(true);
		this.add(filebar);
		filebar.add(" LEFT WIDTH=200  ", selectedfile);
		jswHorizontalPanel optionbar = new jswHorizontalPanel();
		layoutpanel = new jswDropDownBox(this,"Select layout");
		for (Entry<String, Map<String, String>> entry: mcdb.labeltemplates.entrySet())
		{
		   // layoutpanel.addElement(entry.getKey() ); // paul fixing
		}
		optionbar.add(layoutpanel);
		startpos = new jswThumbwheel("Start Position", 1, 10);
		startpos.setValue(1);
		optionbar.add(startpos);
		this.add(optionbar);
		jswHorizontalPanel countrybar = new jswHorizontalPanel();
		showcountry = new jswCheckbox(this, "Show UK?");
		showcountry.setEnabled(true);
		showcountry.setSelected(false);
		countrybar.add(showcountry);
		this.add(countrybar);
		jswHorizontalPanel printbar = new jswHorizontalPanel();
		this.add(printbar);
		jswButton testbutton = new jswButton(this, "PRINT");
		printbar.add(" MIDDLE ", testbutton);
		jswHorizontalPanel progressbar = new jswHorizontalPanel();
		this.add(progressbar);
		prog = new jswLabel(" Selecting source and output file ");
		progressbar.add(" FILLW ", prog);

		mcdb.topgui.mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();

	}

	public void refresh()
	{

		int nbrowsecontacts = mcdb.selbox.getBrowsecontactlist().size();
		//String searchterm = mcdb.selbox.getSearchterm();
		selectedcontacts.setText("Selected contacts (" + nbrowsecontacts + ")");

	}

}
