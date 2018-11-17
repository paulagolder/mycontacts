package org.lerot.mycontact.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lerot.mycontact.mcAttribute;
import org.lerot.mycontact.mcAttributeType;
import org.lerot.mycontact.mcAttributeTypes;
import org.lerot.mycontact.mcAttributes;
import org.lerot.mycontact.mcContact;
import org.lerot.mycontact.mcContacts;
import org.lerot.mycontact.mcDateDataType;
import org.lerot.mycontact.mcLetter;
//import org.lerot.mycontact.mcMember;
import org.lerot.mycontact.mcPDF;
import org.lerot.mycontact.mcUtilities;
import org.lerot.mycontact.mcdb;
import org.lerot.mycontact.gui.widgets.TextTransfer;
import org.lerot.mycontact.gui.widgets.jswButton;
import org.lerot.mycontact.gui.widgets.jswDropDownBox;
import org.lerot.mycontact.gui.widgets.jswHorizontalPanel;
import org.lerot.mycontact.gui.widgets.jswImage;
import org.lerot.mycontact.gui.widgets.jswLabel;
import org.lerot.mycontact.gui.widgets.jswStyle;
import org.lerot.mycontact.gui.widgets.jswStyles;
import org.lerot.mycontact.gui.widgets.jswTable;
import org.lerot.mycontact.gui.widgets.jswThumbwheel;
import org.lerot.mycontact.gui.widgets.jswVerticalLayout;
import org.lerot.mycontact.gui.widgets.jswVerticalPanel;

public class browsePanel extends jswVerticalPanel implements ActionListener
{

	private static final long serialVersionUID = 1L;

	private static jswStyles tablestyles;

	private static jswStyles linktablestyles;

	private jswHorizontalPanel buttonpanel;

	// ActionListener plistener = null;
	mcContact selcontact;
	private String vcarddirectory = "";

	public browsePanel()
	{
		vcarddirectory = mcdb.topgui.desktop;
		tablestyles = makeTableStyles();
		linktablestyles = makeLinkTableStyles();
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		String action = evt.getActionCommand().toUpperCase();
		//System.out.println("action in browsepanel " + action);
		if (action.startsWith("VCARD"))
		{
			System.out.println(" export vcard " + selcontact.getTID());
			printVcard();
		} else if (action.startsWith("VIEW:"))
		{
			String selid = action.substring(5);
			mcContact selcon = mcdb.selbox.getAllcontactlist().FindbystrID(selid);
			mcdb.selbox.setSelcontact(selcon); // paul fiddling here
			mcdb.topgui.refresh();
		} else if (action.startsWith("USE:"))
		{
			System.out.println(" use address " + selcontact.getTID());
			String address = selcontact.makeBlockAddress("\n", true);

			String[] options = new String[] { "Letter", "Label", "Copy",
					"Cancel" };
			TextTransfer textTransfer = new TextTransfer();
			textTransfer.setClipboardContents(address);
			int n = JOptionPane.showOptionDialog(this,
					"Use this address\n" + address, "Address",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
					options, options[0]);
			System.out.println("action is " + n);
			if (n != 3)
			{
				if (n == 1)
				{
					printlabel(address);
				} else if (n == 0)
				{
					printletter(address);
				} else if (n == 2)
				{
					textTransfer.setClipboardContents(address);
				}
			}

		} else if (action.startsWith("MAIL:"))
		{
			String selatt = action.substring(5);
			email(selatt);

		} else if (action.startsWith("COPY:"))
		{
			String atkey = action.substring(5);
			mcAttribute selatt = selcontact.getAttributebyKey(atkey);
			String info = selatt.getValue();
			TextTransfer textTransfer = new TextTransfer();
			textTransfer.setClipboardContents(info);
		}
	}

	void email(String atkey)
	{
		mcAttribute selatt = selcontact.getAttributebyKey(atkey);
		String emailaddress = selatt.getValue();

		Desktop desktop;
		if (Desktop.isDesktopSupported() && (desktop = Desktop.getDesktop())
				.isSupported(Desktop.Action.MAIL))
		{
			URI mailto;
			String subject = "email from " + mcdb.topgui.user;
			subject = subject.replace(" ", "%20");
			String body = mcLetter.getSalutation(selcontact) + "%0D%0A";
			body = body.replace(" ", "%20");
			body = body.replace("&", "%20");
			try
			{
				String target = "mailto:" + emailaddress + "?subject=" + subject
						+ "&body=" + body;
				// String converted = URLDecoder.encode(target, "UTF-8");
				mailto = new URI(target);
				desktop.mail(mailto);
			} catch (URISyntaxException | IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else
		{
			// TODO fallback to some Runtime.exec(..) voodoo?
			throw new RuntimeException(
					"desktop doesn't support mailto; mail is dead anyway ;)");
		}
	}

	private jswTable makeAttributePanel(mcContact selcontact, String selector)
	{

		jswTable attributepanel = new jswTable("attributes", tablestyles);

		if (selcontact != null)
		{
			mcAttributes attributes = selcontact.getAttributes();
			if (attributes.size() < 1)
			{
				selcontact.fillContact();
				attributes = selcontact.getAttributes();
			}

			int row = 0;
			mcAttributeTypes attributetypes = mcdb.topgui.attributetypes;
			if (attributetypes == null)
			{
				attributepanel.addCell(new jswLabel("no attributes"), 0, 1);

			}
			int attcount = 0;
			for (Entry<String, mcAttributeType> anentry : attributetypes
					.entrySet())
			{

				String attkey = anentry.getKey();
				mcAttributeType attype = anentry.getValue();
				if (attype.getDisplaygroup().contains(selector))
				{
					Vector<mcAttribute> allattributes = attributes
							.filterAttributes(attkey);
					for (mcAttribute anattribute : allattributes)
					{
						if (anattribute != null)
						{
							attcount = attcount + 1;
							String attributekey = anattribute.getKey();
							jswLabel alabel = new jswLabel(attributekey);
							attributepanel.addCell(alabel, row, 0);
							if (anattribute.isImage())
							{
								jswImage animage = new jswImage(
										anattribute.getValue());
								attributepanel.addCell(animage.DisplayImage(),
										row, 1);
							} else
							{
								if (anattribute.isType("date"))
								{
									// System.out.println(" in bday");
								}
								String value = anattribute.getFormattedValue();// paul
																				// change
																				// was
																				// formatted
																				// value
								attributepanel.addCell(new jswLabel(value), row,
										1);
							}
							if (anattribute.isType("address")
									&& selector.contains("B"))
							{
								jswButton usecontact = new jswButton(this,
										"USE", "USE:" + anattribute.getKey());
								attributepanel.addCell(usecontact, row, 2);

							} else if (anattribute.isType("email")
									&& selector.contains("B"))
							{
								jswButton usecontact = new jswButton(this,
										"MAIL", "MAIL:" + anattribute.getKey());
								attributepanel.addCell(usecontact, row, 2);

							} else if (anattribute.isType("phone")
									&& selector.contains("B"))
							{
								jswButton usecontact = new jswButton(this,
										"COPY", "COPY:" + anattribute.getKey());
								attributepanel.addCell(usecontact, row, 2);

							} else if (anattribute.isType("cellphone")
									&& selector.contains("B"))
							{
								jswButton usecontact = new jswButton(this,
										"COPY", "COPY:" + anattribute.getKey());
								attributepanel.addCell(usecontact, row, 2);

							} else if (anattribute.getRoot().equals("note")
									&& selector.contains("B"))
							{
								jswButton usecontact = new jswButton(this,
										"COPY", "COPY:" + anattribute.getKey());
								attributepanel.addCell(usecontact, row, 2);

							} else
							{
								jswLabel dummylabel = new jswLabel("");
								attributepanel.addCell(dummylabel, row, 2);
							}
						}
						row++;
					}
				}
			}
			if (attcount == 0)
			{
				attributepanel.addCell(new jswLabel(""), 0, 1);
			}

		} else
		{
			attributepanel.addCell(new jswLabel("disconnected membership"), 0,
					1);
		}
		return attributepanel;
	}

	private mcContacts makeLinkedFromList(mcContact selcontact, String selector)
	{
		mcContacts list = new mcContacts();
		if (selcontact != null)
		{
			mcAttributes getlinked = mcAttributes.FindByAttributeValue(selector,
					selcontact.getTID());
			for (Entry<String, mcAttribute> anentry : getlinked.entrySet())
			{
				mcAttribute anattribute = anentry.getValue();
				int cid = anattribute.getCid();
				mcContact linkedcontact = mcdb.selbox.FindbyID(cid);
				if (linkedcontact != null) list.put(linkedcontact);
			}
			return list;
		} else
		{
			return null;
		}
	}

	private jswVerticalPanel makeLinkedFromPanel(mcContact selcontact,
			mcContacts list, String title)
	{

		jswVerticalPanel frame = new jswVerticalPanel();
		jswLabel memberheading = new jswLabel(title);
		frame.add(memberheading);

		jswTable memberpanel = new jswTable(title, linktablestyles);

		if (list != null)
		{
			int row = 0;
			for (Entry<String, mcContact> acontactentry : list.getContactlist()
					.entrySet())
			{
				mcContact acontact = acontactentry.getValue();

				if (acontact != null)
				{
					String value = acontact.getTID();
					jswLabel alabel = new jswLabel(value);
					memberpanel.addCell(alabel, row, 0);
					memberpanel.addCell(acontact.getIDstr(), row, 1);
					jswButton viewcontact = new jswButton(this, "VIEW",
							"VIEW:" + acontact.getIDstr());
					memberpanel.addCell(viewcontact, row, 2);
					if (selcontact.hasAttributeByValue(acontact.getTID()))
					{
						jswLabel arefll = new jswLabel("found");
						memberpanel.addCell(arefll, row, 3);
					}
					row++;
				}

			}
			if (row == 0)
			{
				return null;
			} else
			{
				frame.add(memberpanel);
				return frame;
			}
		} else
		{
			return null;
		}
	}

	private jswVerticalPanel makeLinkedFromPanel(mcContact selcontact,
			String selector, String title)
	{

		jswVerticalPanel frame = new jswVerticalPanel();
		jswLabel memberheading = new jswLabel(title);
		frame.add(memberheading);

		jswTable memberpanel = new jswTable(selector, linktablestyles);

		if (selcontact != null)
		{
			mcAttributes getlinked = mcAttributes.FindByAttributeValue(selector,
					selcontact.getTID());
			int row = 0;
			for (Entry<String, mcAttribute> anentry : getlinked.entrySet())
			{

				// String attkey = anentry.getKey();
				mcAttribute anattribute = anentry.getValue();
				int cid = anattribute.getCid();
				mcContact linkedcontact = mcdb.selbox.FindbyID(cid);

				// if(selcontact.hasAttributeByValue(linkedcontact.getTID()))
				// continue;

				if (linkedcontact != null)
				{
					String value = linkedcontact.getTID();
					jswLabel alabel = new jswLabel(value);
					memberpanel.addCell(alabel, row, 0);
					jswLabel aqual = new jswLabel(anattribute.getQualifier());
					memberpanel.addCell(aqual, row, 1);
					jswButton viewcontact = new jswButton(this, "VIEW",
							"VIEW:" + cid);
					memberpanel.addCell(viewcontact, row, 2);
					if (selcontact.hasAttributeByValue(linkedcontact.getTID()))
					{
						jswLabel arefll = new jswLabel("found");
						memberpanel.addCell(arefll, row, 3);
					}
				} else
				{

					String value = "New Linked Contact";

					jswLabel alabel = new jswLabel(value);
					memberpanel.addCell(alabel, row, 0);
					jswLabel aqual = new jswLabel(anattribute.getQualifier());
					memberpanel.addCell(aqual, row, 1);
					jswLabel avalue = new jswLabel(" No Link ");
					memberpanel.addCell(avalue, row, 2);
				}
				row++;
			}
			if (row == 0)
			{
				return null;
			} else
			{
				frame.add(memberpanel);
				return frame;
			}
		} else
		{
			return null;
		}
	}

	private jswStyles makeLinkTableStyles()
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

		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("LEFT");
		col1style.putAttribute("horizontalAlignment", "LEFT");

		jswStyle col2style = tablestyles.makeStyle("col_2");
		col2style.putAttribute("horizontalAlignment", "RIGHT");
		col2style.putAttribute("minwidth", "true");

		return tablestyles;
	}

	private mcContacts makeLinkToList(mcContact selcontact, String selector)
	{

		mcContacts list = new mcContacts();
		int row = 0;
		if (selcontact != null)
		{
			Map<String, mcAttribute> attributes = selcontact
					.getAttributesbyRoot(selector);
			if (attributes.size() < 1) { return null; }
			for (Entry<String, mcAttribute> anentry : attributes.entrySet())
			{
				mcAttribute anattribute = anentry.getValue();
				String value = anattribute.getValue();
			//	System.out.println(" found " + anattribute.getRoot() + " "
			//			+ anattribute.getQualifier() + " " + value);
				mcContact linkedcontact = mcdb.selbox.FindbyTID(value);
				if (linkedcontact != null)
				{
					list.put(linkedcontact);
					row++;
				}
			}

			if (row == 0)
			{
				return null;
			} else
			{
				return list;
			}
		} else
		{
			return null;
		}

	}

	private jswVerticalPanel makeLinkToPanel(mcContact selcontact,
			mcContacts list, String title)
	{

		jswVerticalPanel frame = new jswVerticalPanel();
		jswLabel memberheading = new jswLabel(title);
		frame.add(memberheading);
		jswTable memberpanel = new jswTable(title, linktablestyles);

		if (list != null)
		{
			int row = 0;

			for (Entry<String, mcContact> anentry : list.getContactlist()
					.entrySet())
			{

				mcContact acontact = anentry.getValue();

				if (acontact != null)
				{
					jswLabel alabel = new jswLabel(acontact.getTID());
					memberpanel.addCell(alabel, row, 0);

					jswButton viewcontact = new jswButton(this, "VIEW",
							"VIEW:" + acontact.getIDstr());
					jswTable memberattributes = makeAttributePanel(acontact,
							"S");

					memberpanel.addCell(memberattributes, " FILLW ", row, 1);
					memberpanel.addCell(viewcontact, row, 2);

					row++;
				}
			}

			if (row == 0)
			{
				return null;
			} else
			{
				frame.add(memberpanel);
				return frame;
			}
		} else
		{
			return null;
		}

	}

	private jswVerticalPanel makeLinkToPanel(mcContact selcontact,
			String selector, String title)
	{

		jswVerticalPanel frame = new jswVerticalPanel();
		jswLabel memberheading = new jswLabel(title);
		frame.add(memberheading);
		jswTable memberpanel = new jswTable(selector, linktablestyles);

		if (selcontact != null)
		{
			Map<String, mcAttribute> attributes = selcontact
					.getAttributesbyRoot(selector);
			if (attributes.size() < 1) { return null; }

			int row = 0;

			for (Entry<String, mcAttribute> anentry : attributes.entrySet())
			{

				String attkey = anentry.getKey();

				mcAttribute anattribute = anentry.getValue();
				String qual = anattribute.getQualifier();
				String value = anattribute.getValue();
				String qvalue = value;
				if (!qual.isEmpty() && !mcUtilities.isNumeric(qual))
				{
					qvalue = value + "(" + qual + ")";
				}
				mcContact linkedcontact = mcdb.selbox.FindbyTID(value);

				if (linkedcontact != null)
				{
					jswLabel alabel = new jswLabel(qvalue);
					memberpanel.addCell(alabel, row, 0);

					jswButton viewcontact = new jswButton(this, "VIEW",
							"VIEW:" + linkedcontact.getIDstr());
					jswTable memberattributes = makeAttributePanel(
							linkedcontact, "S");

					memberpanel.addCell(memberattributes, " FILLW ", row, 1);
					memberpanel.addCell(viewcontact, row, 2);
				} else
				{
					if (value == null || value.equalsIgnoreCase("null"))
					{
						value = "No Linked Contact";
					}
					jswLabel alabel = new jswLabel(value);
					memberpanel.addCell(alabel, row, 0);
					jswLabel aqual = new jswLabel(anattribute.getQualifier());
					memberpanel.addCell("", row, 1);
					jswLabel avalue = new jswLabel(" No Link ");
					memberpanel.addCell(avalue, row, 2);
				}
				row++;
			}

			if (row == 0)
			{
				return null;
			} else
			{
				frame.add(memberpanel);
				return frame;
			}
		} else
		{
			return null;
		}

	}

	private jswStyles makeTableStyles()
	{
		jswStyles tablestyles = new jswStyles();
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

		jswStyle col1style = tablestyles.makeStyle("col_1");
		col1style.putAttribute("fontStyle", Font.BOLD);
		col1style.setHorizontalAlign("LEFT");

		jswStyle tablestyle = tablestyles.makeStyle("table");
		tablestyle.putAttribute("backgroundColor", "White");
		tablestyle.putAttribute("foregroundColor", "Green");
		tablestyle.putAttribute("borderWidth", "2");
		tablestyle.putAttribute("borderColor", "blue");

		jswStyle col2style = tablestyles.makeStyle("col_2");
		col2style.putAttribute("horizontalAlignment", "RIGHT");
		col2style.putAttribute("minwidth", "true");
		// col2style.putAttribute("minwidth", "true");

		return tablestyles;
	}

	private void printlabel(String address)
	{
		
		JFileChooser fc = new JFileChooser(mcdb.letterfolder);
		fc.setDialogTitle("Specify a file to save label");
		String labelname = mcLetter.makeFileName(selcontact);
		fc.setSelectedFile(
				new File(mcdb.letterfolder + "/" + labelname + ".pdf"));
	;
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF",
				"pdf");
		fc.setFileFilter(filter);
	;
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			mcContacts thiscontactlist = new mcContacts();
			// String address = selcontact.makeBlockAddress("\n");
			thiscontactlist.put(selcontact);
			File fileToSave = fc.getSelectedFile();
			String filepath = fileToSave.getPath();
			File afile = new File(filepath);
			jswVerticalPanel panel = new jswVerticalPanel();
			JPanel options = new JPanel();
			options.add(new JButton("OK"));
			options.add(new JButton("Cancel"));
			JTextArea addressarea = new JTextArea(6, 30);
			addressarea.setText(address);
			jswDropDownBox pagelayout = new jswDropDownBox("Layout", true,
					false);
			for (Entry<String, Map<String, String>> entry: mcdb.labeltemplates.entrySet())
			{
			    pagelayout.addElement(entry.getKey() );
			}
			jswThumbwheel startpos = new jswThumbwheel("Starting Position", 1,
					10);
			startpos.setValue(1);
			pagelayout.setPreferredSize(new Dimension(100, 50));
			panel.add(new JLabel("Address"));
			panel.add(addressarea);
			panel.add(startpos);
			panel.add(pagelayout);
			JDialog dialog = null;
			JOptionPane optionPane = new JOptionPane();
			optionPane.setMessage("");
			optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
			optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
			optionPane.add(panel);
			dialog = optionPane.createDialog(null, "Print Label");
			dialog.setVisible(true);
			int pane = (int) optionPane.getValue();
			if (pane == 0)
			{
				int sp = startpos.getValue();
				address = addressarea.getText();
				String sellayout = pagelayout.getSelectedValue();
				System.out.println("selectedlayout "+sellayout);
				mcPDF labelpages = new mcPDF(afile, "Lerot Contacts Labels");
				labelpages.setLayout(sellayout);
				int ncount = labelpages.makeLabelPage(address, sp);
			}

		} else
		{
			System.out.println("Open command cancelled by user.");
		}
	}

	private void printletter(String address)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Specify a file to save Letter");
		String lettername = mcLetter.makeFileName(selcontact);
		fc.setSelectedFile(
				new File(mcdb.letterfolder + "/" + lettername + ".odt"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("ODT",
				"odt");
		fc.setFileFilter(filter);
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			String template = templateselector();
			if (template != null)
			{
				File fileToSave = fc.getSelectedFile();
				String filepath = fileToSave.getPath();
				mcdb.letterfolder = fileToSave.getParent();
				try
				{
					// String address = selcontact.makeBlockAddress("\n");
					String salutation = mcLetter.getSalutation(selcontact);
					String printdate = mcDateDataType.getNow("dd MMM yyyy");
					mcLetter letter = new mcLetter();
					letter.setOutputFileName(filepath);
					letter.setTemplateFileName(
							mcdb.topgui.dotcontacts + template);
					letter.setVariable("address", address);
					letter.setVariable("salutation", salutation);
					letter.setVariable("printdate", printdate);
					letter.printLetter();

				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		} else
		{
			System.out.println("Open command cancelled by user.");
		}
	}

	private void printVcard()
	{

		String name = selcontact.getName("sn fn");
		name = name.replace(" ", "");
		String date = mcDateDataType.getNow("_yyyyMMdd");
		String fname = vcarddirectory + "/" + name + date + ".vcf";
		File vfile = new File(fname);
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Specify a file for Vcard");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("vcf",
				"vcf");
		fc.setFileFilter(filter);
		fc.setSelectedFile(vfile);
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			String vout = selcontact.toVcard();
			File fileToSave = fc.getSelectedFile();
			String filepath = fileToSave.getPath();

			try
			{
				vcarddirectory = fileToSave.getParentFile().getCanonicalPath();
				Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filepath), "utf-8"));
				writer.write(vout);
				writer.close();
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void showBrowsePanel()
	{
		jswVerticalPanel mainpanel = this;
		selcontact = mcdb.selbox.getSelcontact();
		mainpanel.removeAll();
		mainpanel.setLayout(new jswVerticalLayout());
		// new jswHorizontalPanel();
		String group = selcontact.getKind();
		mcAttributes attributes = selcontact.getAttributes();
		jswHorizontalPanel idbox = new jswHorizontalPanel("idbox", false);
		mainpanel.add(idbox);
		if (attributes != null)
		{
			mcAttribute photoatt = attributes.find("photo");
			if (photoatt != null)
			{
				jswImage animage = new jswImage(photoatt.getValue());
				animage.setTargetheight(40);
				idbox.add(animage.DisplayImage());
			}
		}
		jswLabel idpanel1 = new jswLabel(" ");
		idbox.add(idpanel1);
		idpanel1.setText(selcontact.getIDstr());
		jswLabel idpanel2;
		jswLabel idpanel3;
		idpanel2 = new jswLabel(" ");
		idbox.add(idpanel2);
		idpanel2.setText(selcontact.getName());
		idpanel3 = new jswLabel(" ");
		idbox.add("RIGHT", idpanel3);
		idpanel3.setText(group);
		jswButton vcardexport = new jswButton(this, "VCard");
		idbox.add("RIGHT", vcardexport);
		selcontact.fillContact();
		jswTable contactattributes = makeAttributePanel(selcontact, "B");
		if (contactattributes != null) mainpanel.add(contactattributes);
		int row = 0;

		jswVerticalPanel arelationslist = makeLinkToPanel(selcontact, "related",
				"Related to");
		if (arelationslist != null) mainpanel.add(arelationslist);
		jswVerticalPanel amemberstlist = makeLinkToPanel(selcontact, "member",
				"Has Members");
		if (amemberstlist != null) mainpanel.add(amemberstlist);
		jswVerticalPanel aorglist = makeLinkToPanel(selcontact, "org",
				"Member Of");
		if (aorglist != null) mainpanel.add(aorglist);

		mcContacts relationlist = makeLinkToList(selcontact, "related");
		mcContacts memberoflist = makeLinkToList(selcontact, "member");
		mcContacts hasmemberslist = makeLinkToList(selcontact, "org");

		if (relationlist != null)
			System.out.println(" rel=" + relationlist.size());
		if (memberoflist != null)
			System.out.println(" mem=" + memberoflist.size());
		if (hasmemberslist != null)
			System.out.println(" org=" + hasmemberslist.size());

		mcContacts exrelnlist = makeLinkedFromList(selcontact, "related");
		mcContacts exmemberoflist = makeLinkedFromList(selcontact, "org");
		mcContacts exhasmemberslist = makeLinkedFromList(selcontact, "member");
		exhasmemberslist.remove(hasmemberslist);
		exmemberoflist.remove(memberoflist);
		exrelnlist.remove(relationlist);

		if (!exrelnlist.isEmpty())
		{
			jswVerticalPanel exrelnpanel = makeLinkedFromPanel(selcontact,
					exrelnlist, "ex-related");
			mainpanel.add(exrelnpanel);
		}

		if (!exmemberoflist.isEmpty())
		{
			jswVerticalPanel exmemberofpanel = makeLinkedFromPanel(selcontact,
					exmemberoflist, "ex-HasMembers");
			mainpanel.add(exmemberofpanel);
		}

		if (!exhasmemberslist.isEmpty())
		{
			jswVerticalPanel exhasmemberspanel = makeLinkedFromPanel(selcontact,
					exhasmemberslist, "ex-memberof");
			mainpanel.add(exhasmemberspanel);
		}

		Dimension d = mainpanel.getMinimumSize();
		Rectangle fred = mainpanel.getBounds();
		fred.width = d.width;
		fred.height = d.height + 60;
		Rectangle actual = mcdb.topgui.getBounds();
		if (fred.width > actual.width) actual.width = fred.width;
		if (fred.height + 60 > actual.height) actual.height = fred.height + 60;
		mcdb.topgui.setBounds(actual);
		mcdb.topgui.setVisible(true);
		mainpanel.repaint();
		mcdb.topgui.getContentPane().validate();
	}

	String templateselector()
	{
		jswDropDownBox templatelist = new jswDropDownBox("template", true,
				false);
		for (String text : mcLetter.getTemplateList())
		{
			templatelist.addElement(text);
		}
		JDialog dialog = null;
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage("");
		optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
		optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		optionPane.add(templatelist);
		dialog = optionPane.createDialog(null, "Select Template");
		dialog.setVisible(true);
		int pane = (int) optionPane.getValue();
		if (pane == 0)
			return templatelist.getSelectedValue();
		else
			return null;
	}

}
