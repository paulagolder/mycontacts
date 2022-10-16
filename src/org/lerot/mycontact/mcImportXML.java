package org.lerot.mycontact;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lerot.mywidgets.jswLabel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class mcImportXML extends mcImports
{
	boolean test = true;
	int maxcid = 0;
	int newobjects=0;
	int changedobjects =0;
	jswLabel message;
	static String[] Nfields = { "sn", "fn", "mn", "title", "sufix" };
	static String[] ADRfields = { "pobox", "extaddr", "street", "city",
			"county", "postcode", "country" };

	public mcImportXML(String importfilename)
	{
		super(importfilename, "xml");
		
	}

	public LinkedHashMap<String, mcImportexception> importall(boolean test,
			jswLabel inmessage)
	{
		System.out.println(" import xml "+ importfilename);
		message = inmessage;
		if (message != null)
			message.setText(" importing xml " + importfilename);
		mappings = mcdb.topgui.currentcon.createMappings("import", "XML");
		if (!test)
		{
		//	initialiseImportDataTable(importfilename, importtype);
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		LinkedHashMap<String, mcImportexception> exceptions = new LinkedHashMap<String, mcImportexception>();
		mcAttribute anattribute = new mcAttribute(0);
		Vector<String> attkeylist = anattribute .dbloadAttributeKeyList();
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(importfilename);
			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("contact");
			int k = 1;
			if (nl != null && nl.getLength() > 0)
			{
				for (int i = 0; i < nl.getLength(); i++)
				{
					Element el = (Element) nl.item(i);
					mcContact icontact = new mcContact();
					icontact.loadXML(el, i);
			//		System.out.println(" imported contact :" + icontact);
					String ct = icontact.toXML(attkeylist);
					//System.out.println(ct+"/n/n");
					mcContact existingcontact = mcdb.selbox.getAllcontactlist().FindbyTID(icontact.getTID());
					if (existingcontact == null)
					{
						System.out.println(" make new  contact :" + icontact);
						mcContact newcontact = new mcContact(icontact);
					
						int cid = newcontact.insertNewContact();
						newcontact.updateContact();
    					newobjects++;
					} else
					{
	//		            System.out.println(" found "+existingcontact);
						if (icontact.matches(existingcontact))
						{
	//						System.out.println(" ignoring contact :"
	//						 +icontact+" no changes");
						} else
						{
							System.out.println(" importing contact :"
									 +icontact+" with changes");
							if (!test)
							{
								int changes = existingcontact
										.updateContact(icontact);
								 if(changes>0)
								 {
								 existingcontact.updateContact();
								 System.out.println(
											" updated contact  :" + icontact + " "+changes);
								 changedobjects++;
								 }
							}
							k++;
						}
					}
				}
				System.out
						.println(" imported: " + newobjects+ " Contacts  and updated :"+changedobjects);
			}

		} catch (ParserConfigurationException pce)
		{
			pce.printStackTrace();
		} catch (SAXException se)
		{
			se.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		if (message != null)
		{
			message.setText("loaded  " + " contacts " + getCurrentposition());
			message.repaint();
		}
		return exceptions;
	}

}
