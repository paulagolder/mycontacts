package org.lerot.mycontact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class mcContact extends mcDataObject implements Comparable<mcContact>
{

	mcAttributes attributes = null;
	int CID = 0;
	private String kind = null;
	// Map<String, mcMember> memberof = null;
	// Map<String, mcMember> members = null;
	String name = null;
	String TID = "new contact";
	Timestamp update = null;
	 List noprintlist = new ArrayList();
	  
	String user = null;


	public mcContact()
	{
		super();
		attributes = new mcAttributes();
		noprintlist.add("kind");
		noprintlist.add("tid");
	}

	public mcContact(int id)
	{
		super();
		CID = id;
		attributes = new mcAttributes();
	}

	public mcContact(mcImportContact impcontact)
	{
		CID = mcContacts.getNewID();
		String fn = "";
		attributes = new mcAttributes();
		for (Entry<String, mcImportAttribute> arec : impcontact.getAttributes()
				.entrySet())
		{
			mcImportAttribute imatt = arec.getValue();
			String aroot = imatt.getRoot();
			String aqual = imatt.getQualifier();
			if (aroot.equalsIgnoreCase("tid"))
			{
				TID = imatt.getValue();
			}
			if (aroot.equalsIgnoreCase("name"))
			{
				fn = imatt.getValue();
			}
			mcAttribute newatt = createAttribute(aroot, aqual,
					imatt.getValue());
			attributes.put(newatt);
		}
		if ((TID == null || TID.isEmpty()) && !fn.isEmpty())
		{
			TID = fn;
			mcAttribute newatt = createAttribute("tid", "", fn);
			attributes.put(newatt);
		}
	}

	public void addMember(String vstr, mcContact selcontact)
	{
		createAttribute(vstr, "", selcontact.getTID());
	}

	@Override
	public int compareTo(mcContact othercontact)
	{
		return TID.compareTo(othercontact.TID);
	}

	public boolean containsValue(String testvalue)
	{
		String tid = getTID();
		if (mcUtilities.containedBy(testvalue, tid)) { return true; }
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (anattribute.containsValue(testvalue)) { return true; }
		}
		return false;

	}

	public mcAttribute createAttribute(String attroot, String attqual)
	{
		String root = attroot;
		String qualifier = attqual;
		mcAttribute oldatt = getAttribute(attroot, qualifier);
		int k = 1;
		while (oldatt != null)
		{
			qualifier = "alt" + k;
			oldatt = getAttribute(root, qualifier);
			k++;
		}
		mcAttribute newatt = new mcAttribute(CID, root, qualifier);
		attributes.put(newatt);
		return newatt;
	}

	public mcAttribute createAttribute(String root, String qualifier,
			String value)
	{
		mcAttribute newatt = createAttribute(root, qualifier);
		newatt.setValue(value);
		newatt.setUpdate();
		return newatt;
	}

	private void createAttribute(String aroot, String aqual, String avalue,
			String updated)
	{
		mcAttribute newatt = createAttribute(aroot, aqual, avalue);
		newatt.setUpdate(updated);
	}

	public void deleteAttributebyKey(String attkey)
	{
		int id = this.CID;
		String part[] = mcUtilities.parseLabel(attkey.toLowerCase());
		doDelete("attributeValues", " cid=" + id + " and  root  = '" + part[0]
				+ "' and qualifier ='" + part[1] + "' ;");
		System.out.println(
				" deleting attribute from  " + this.TID + " " + attkey);
		attributes.remove(attkey);
	}

	public void deleteContact()
	{
		int id = this.CID;
		mcdb.selbox.remove(this);
		doDelete("attributeValues", " cid=" + id);
		System.out.println(" deleting contact  " + this.TID);
	}

	public void disconnectContact(mcContact selcontact)
	{

		// paul fix
		// doDelete("memberof", " parentid = " + this.CID + " and memberid = "
		// + selcontact.CID);
		// this.fillMembers();
		// selcontact.fillMembers();
	}

	public void fillContact()
	{
		int id = this.CID;
		attributes = mcAttributes.getAttributes(id);
		if (attributes == null)
		{
			System.out.println(" no attributes for contact " + id + " +TID");
		}

	}

	public Map<String, mcAttribute> filterAttributes(String filter)
	{
		Map<String, mcAttribute> result = new HashMap<String, mcAttribute>();
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (filter == null)
			{
				result.put(anattribute.getKey(), anattribute);

			} else if (anattribute.getKey().contains(filter))
			{
				result.put(anattribute.getKey(), anattribute);
			}

		}
		return result;
	}

	public mcAttribute getAddress()
	{
		mcAttribute anattribute = getAttributebyKey("address");
		if (anattribute == null)
		{
			mcAttribute org = getAttributebyKey("org");
			if (org != null)
			{
				String orgtid = org.getValue();
				mcContact orgcontact = mcdb.selbox.getAllcontactlist()
						.FindbyTID(orgtid);
				if (orgcontact != null)
				{
					anattribute = orgcontact.getAttributebyKey("address");
				}
			}
		}
		return anattribute;
	}

	public mcAttribute getAttribute(String attroot, String qualifier)
	{

		mcAttribute foundatt = attributes.find(attroot, qualifier);
		return foundatt;
	}

	public mcAttribute getAttributebyKey(String atkey)
	{
		return attributes.find(atkey);
	}

	public mcAttributes getAttributes()
	{
		if (attributes == null || attributes.size() == 0)
		{
			attributes = mcAttributes.getAttributes(this.CID);
		}
		return attributes;
	}

	public Map<String, mcAttribute> getAttributesbyKey(String attkey)
	{
		Map<String, mcAttribute> result = new HashMap<String, mcAttribute>();
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (attkey.equalsIgnoreCase(anattribute.getKey()))
			{
				result.put(anattribute.getKey(), anattribute);
			}
		}
		return result;
	}

	public Map<String, mcAttribute> getAttributesbyRoot(String attroot)
	{
		Map<String, mcAttribute> result = new HashMap<String, mcAttribute>();
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (attroot.equalsIgnoreCase(anattribute.getRoot()))
			{
				result.put(anattribute.getKey(), anattribute);
			}
		}
		return result;
	}

	public String getAttributeValue(String root, String qual)
	{
		mcAttribute anatt = attributes.get(root + "/" + qual);
		if (anatt == null) return null;
		String attvalue = anatt.getValue();
		return attvalue;
	}

	public String getAttributeValuebyKey(String attkey)
	{
		String[] part = mcUtilities.parseLabel(attkey);
		return getAttributeValue(part[0], part[1]);
	}

	public Vector<String> getAttributeValues(String attkey)
	{
		Vector<String> result = new Vector<String>();
		int k = 0;
		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			if (attkey.equalsIgnoreCase(anattribute.getKey()))
			{
				result.add(anattribute.getFormattedValue());
				k++;
			} else if (attkey.equalsIgnoreCase(anattribute.getRoot()))
			{
				result.add(anattribute.getFormattedValue());
				k++;
			}
		}
		if (k > 0)
			return result;
		else
			return null;
	}

	public void getContact(int id)
	{
		ArrayList<Map<String, String>> rowlist = doQuery(
				"select * from proup where cid=" + id);
		if (rowlist.size() == 1)
		{
			load(rowlist.get(0));
		}
	}

	public void getContact(String tid)
	{
		ArrayList<Map<String, String>> rowlist = doQuery(
				"select * from proup where tid='" + tid + "' ");
		if (rowlist.size() == 1)
		{
			load(rowlist.get(0));
		}

	}

	public int getID()
	{
		return this.CID;
	}

	public String getIDstr()
	{
           String ids = ("000000" + this.CID);
           int l = ids.length();
		   return ids.substring(l-6);
	}
	
	public static String makeIDstr(String id)
	{
           String ids = ("000000" + id);
           int l = ids.length();
		   return ids.substring(l-6);
	}
	
	public String getSimpleIDstr()
	{
		
		return ""+this.CID;
	}


	private String getlastupdate()
	{
		String lastupdate = null;
		for (Entry<String, mcAttribute> arec : attributes.entrySet())
		{
			mcAttribute imatt = arec.getValue();
			String updatedt = imatt.getUpdate();
			if (lastupdate == null) lastupdate = updatedt;
			if (lastupdate != null && updatedt != null)
			{
				if (lastupdate.compareTo(updatedt) < 0) lastupdate = updatedt;
			}
		}
		return lastupdate;
	}

	public String getName()
	{
		String name = TID;
		mcAttribute nameat = attributes.find("name");
		if (nameat != null) name = nameat.getFormattedValue();
		return name;
	}

	public String getName(String fmt)
	{
		String name = TID;
		mcAttribute nameat = attributes.find("name");
		String fname=null;
		if(nameat!=null) 
		 fname = nameat.getFormattedValue(fmt).trim();
		if (fname != null && !fname.isEmpty()) name = fname;
		return name;
	}

	public String getPosition()
	{
		mcAttribute posit = attributes.find("position");
		if (posit != null) return posit.getValue();
		return null;
	}

	public Set<String> getTags()
	{
		mcAttribute tagat = getAttributebyKey("tags");
		if (tagat == null) return null;
		Set<String> tags = mcTextListDataType.getTags(tagat);
		return tags;
	}

	public String getTID()
	{
		return TID;
	}

	public String getKind()
	{
		if (kind == null || kind.isEmpty())
			return "person";
		else
			return kind;
	}

	public Timestamp getUpdate()
	{
		return update;
	}

	public String getUser()
	{
		return user;
	}

	public boolean hasAttributeByValue(String value)
	{

		for (Entry<String, mcAttribute> anat : attributes.entrySet())
		{
			mcAttribute anattribute = anat.getValue();
			String testvalue = anattribute.getValue();
			if (value.equalsIgnoreCase(testvalue)) { return true; }
		}
		return false;
	}

	public boolean hasTag(String atag)
	{
		mcAttribute tagat = getAttributebyKey("tags");
		if (tagat == null) return false;
		mcDataType dt = tagat.getDataType();

		return dt.valueContained(tagat.getValue(), atag);

	}

	public void insertAttribute(String root)
	{
		mcAttribute newatt = new mcAttribute(CID, root);
		newatt.dbinsertAttribute();
	}

	public void insertAttribute(String newroot, String newqual)
	{
		mcAttribute newatt = new mcAttribute(CID, newroot);
		newatt.dbinsertAttribute();
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public int insertNewContact()
	{
		int nid = mcContacts.getNewID();
		// int nid = getID();
		PreparedStatement st;
		String query = "insert into attributeValues(cid, root,qualifier,value )  values(?, ?,?,?)";

		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, nid);
			st.setString(2, "tid");
			st.setString(3, "");
			st.setString(4, getTID());
			st.executeUpdate();
			st.close();
			String query2 = "insert into attributeValues(cid, root,qualifier,value )  values(?, ?,?,?)";

			st = con.prepareStatement(query2);
			st.setInt(1, nid);
			st.setString(2, "kind");
			st.setString(3, "");
			st.setString(4, getKind());
			st.executeUpdate();

			st.close();
			String query3 = "insert into attributeValues(cid, root,qualifier,value )  values(?, ?,?,?)";

			st = con.prepareStatement(query3);
			st.setInt(1, nid);
			st.setString(2, "name");
			st.setString(3, "");
			st.setString(4, getTID());
			st.executeUpdate();

			st.close();
			return nid;

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public boolean isList()
	{
		if (kind.endsWith("list")) return true;
		return false;
	}

	void load(Map<String, String> inmap)
	{
		if (inmap.containsKey("cid")) CID = Integer.valueOf(inmap.get("cid"));
		if (inmap.containsKey("tid")) TID = inmap.get("tid");
		// if (inmap.containsKey("name")) name = inmap.get("name");
		if (inmap.containsKey("type")) kind = inmap.get("kind");
		if (inmap.containsKey("user")) user = inmap.get("user");
	}

	void load(ResultSet inmap)
	{
		try
		{
			CID = inmap.getInt("cid");
			TID = inmap.getString("tid");
			kind = inmap.getString("type");
			user = inmap.getString("user");
			update = inmap.getTimestamp("update");
		} catch (SQLException e)
		{

		}

	}

	public void loadXML(Element ct, int contactnumber)
	{
		kind = ct.getAttribute("kind");
		TID = ct.getAttribute("tid");
		CID = contactnumber;
	
		NodeList nl = ct.getChildNodes();
		if (nl != null && nl.getLength() > 0)
		{
			for (int i = 0; i < nl.getLength(); i++)
			{
				if(nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
				Node at = (Node)( nl.item(i));			
				mcAttribute anatt = mcAttribute.attributefromXML(at);
				anatt.loadXML(at);
				attributes.put(anatt);
				}
			}
		}
	}

	public String makeBlockAddress(String string)
	{

		return string;
	}

	public String makeBlockAddress(String sep, boolean showuk)
	{
		String ostr = "";
		String fmt = "Title Fn Mn Sn ";
		String name = getName(fmt);
		if (!name.trim().isEmpty()) ostr += name + sep;
		String position = getPosition();
		if (position != null) ostr += position + sep;
		mcAttribute address = getAddress();

		if (address != null && !address.isEmpty())
		{
			ostr += ((mcAddressDataType) address.getDataType())
					.getBlockFormattedValue(address.getValue(), sep, showuk);
		} else
		{
			System.out.println(" no address for :" + this);
			// ostr = null;
		}

		return ostr;
	}

	public boolean matches(mcContact testcontact)
	{
		String reftid = getTID();
		String testtid = testcontact.getTID();
		if (!reftid.equalsIgnoreCase(testtid)) return false;
		String existingtype = getKind();
		String importedtype = testcontact.getKind();
		if (!existingtype.equalsIgnoreCase(importedtype)) return false;
		for (Entry<String, mcAttribute> entry : attributes.entrySet())
		{
			mcAttribute existingatt = entry.getValue();
			mcAttribute importedatt = testcontact.getAttributebyKey(existingatt.getKey());
			if (!existingatt.matches(importedatt)) return false;
		}
		return true;
	}

	public int mergeContact(mcContact fcontact)
	{
		int unmatched = 0;
		for (Entry<String, mcAttribute> anentry : fcontact.attributes
				.entrySet())
		{
			mcAttribute aniattribute = anentry.getValue();
			if (aniattribute != null)
			{
				if (!containsValue(aniattribute.getValue()))
				{
					updateAttributebyKey(aniattribute.getKey(),
							aniattribute.getValue());
				}
			}
		}
		return unmatched;
	}
	
	public int updateContact(mcContact fcontact)
	{
		//int unmatched = 0;
		//Timestamp updated = getUpdate();
		//Timestamp fupdated = fcontact.getUpdate();
		int updates=0;
		
		for (Entry<String, mcAttribute> anentry : fcontact.attributes
				.entrySet())
		{
			mcAttribute aniattribute = anentry.getValue();
			if (aniattribute != null)
			{
				//String anupdated = aniattribute.getUpdate();
				String root = aniattribute.getRoot();
				String qual = aniattribute.getQualifier();
				mcAttribute matchedatt = getAttribute(root, qual);
				if (matchedatt != null)
				{
					if (matchedatt.matches(aniattribute))
					{

					} else
					{
						String maupdate = matchedatt.getUpdate();
						String aniupdate = aniattribute.getUpdate();
						if(maupdate.compareTo(aniupdate)<0)
						{
						matchedatt.updateAttribute(aniattribute);
						updates++;
						}
					}
				} else
				{
					putAttribute(aniattribute);
					updates ++;
				}
			}
		}
		return updates;
	}

	public void putAttribute(mcAttribute newatt)
	{
		attributes.put(newatt);

	}

	public mcAttributes refreshAttributes()
	{
		attributes = mcAttributes.getAttributes(this.CID);
		return attributes;
	}

	public void removeAttributebyKey(String key)
	{
		attributes.remove(key);

	}

	public void setAttributes(mcAttributes attributes)
	{
		this.attributes = attributes;
	}

	public void setID(int iD)
	{
		CID = iD;
	}

	public void setTID(String tID)
	{
		TID = tID;
	}

	public void setKind(String group)
	{
		this.kind = group;
	}

	public void setUpdate()
	{

		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(
				now.getTime());
		setUpdate(currentTimestamp);
	}

	public void setUpdate(Timestamp update)
	{
		this.update = update;
	}

	@Override
	public String toString()
	{
		String outstring = "contact :" + CID + " " + TID + "   ";
		return outstring;
	}

	public String toVcard()
	{
		mcMappings mappings = mcdb.topgui.currentcon.createMappings("export",
				"Vcard");
		Vector<String> attkeys = mappings.getKeys();
		return toVcard(attkeys, mappings,false);
	}

	public String toVcard(Vector<String> attkeys, mcMappings mappings, boolean owndrive)
	{
		mcContact acontact = this;
		mcAttributes attributes = acontact.getAttributes();
		String outcard = "";
		boolean isok = false;
		for (String attkey : attkeys)
		{
			mcAttribute anattribute = attributes.get(attkey);
			if (anattribute != null && anattribute.getFormattedValue() != null
					&& !anattribute.getFormattedValue().isEmpty()
					&& !attkey.equalsIgnoreCase("name"))
				isok = true;
			
		}

		if (isok)
		{
			
			// System.out.println(" Printing " + acontact + " "
			// + attributes.size());
			String nl = "\n";
			String cid = acontact.getTID();
       
			outcard += "BEGIN:VCARD" + nl;
			outcard += "VERSION:4.0" + nl;
			outcard += "PRODID:MYContacts.lerot/version " + mcdb.version + nl;
			String rev = acontact.getlastupdate();
			outcard += "REV:" + rev + nl;
			if (cid == null)
			{
				mcAttribute nameatt = acontact.getAttributebyKey("name");
				if (nameatt != null) cid = nameatt.getFormattedValue();
			}
			outcard += ("FN:" + cid.toLowerCase() + nl);
			for (String attroot : attkeys)
			{
				boolean nocategory = true;
				mcAttributes anattributes = attributes.findAllbyRoot(attroot);
				if(owndrive)
				{
					anattributes = attributes.findAllnoQualifier(attroot);
				}
				String foriegnlabel = mappings.get(attroot).toUpperCase();
				
				int replication = 1;
				if (foriegnlabel != null)
				{

					for (Entry<String, mcAttribute> entry : anattributes
							.entrySet())
					{
						mcAttribute anattribute = entry.getValue();
						if(owndrive)  // paul to fix with new vcardtype
						{
							if(foriegnlabel.contains("TYPE"))
								foriegnlabel ="MOBILE";
						}
						if (foriegnlabel.contains("ENCODING=b"))
						{

							String rawvalue = anattribute.getValue();
							String[] encodedlines = mcImageDataType
									.getVCardEncodedLines(foriegnlabel, rawvalue);
							for (String line : encodedlines)
							{
								if (line != null) outcard += (line) + nl;
							}

						} else
						{
							String vvalue = anattribute.getVcardValue();
							String vqual = anattribute.getQualifier();
							if (vqual != null && !vqual.isEmpty())
							{
								if (mcUtilities.isNumeric(vqual))
								{
								//	vqual = "" + replication;
									replication++;
								}
								vqual = ";TYPE=" + vqual.toUpperCase();
							}

							if (vvalue != null && !vvalue.isEmpty()
									&& !vvalue.equalsIgnoreCase("null"))
							{
								if (foriegnlabel.equalsIgnoreCase("categories"))
									nocategory = false;
								outcard += (foriegnlabel + vqual + ":" + vvalue
										+ nl);
							}
						}
					}
					if (foriegnlabel.equalsIgnoreCase("categories"))
					{
						if (nocategory)
						{
					//		String vvalue = "other";
					//		outcard += (foriegnlabel + ": " + vvalue + nl); paul to fix
						}
					}
				}
			}
			outcard += ("END:VCARD" + nl);
			outcard += ("");
		} else
		{
			System.out.println(
					"Not  Printing " + acontact + " " + attributes.size());
		}
		return outcard;
	}

	public String toXML(Vector<String> attkeys)
	{
		String xmlout = "<contact tid='" + StringEscapeUtils.escapeXml(getTID())
				+ "' kind='" + getKind() + "' CID='" + getID() + "'   >" + "\n";
		for (String attkey : attkeys)
		{
			mcAttributes sublist = attributes.findAllbyRoot(attkey);
			for (Entry<String, mcAttribute> anentry : sublist.entrySet())
			{
				mcAttribute anattribute = anentry.getValue();
				if(anattribute.isImage())
				{
					System.out.println(" is image");
					String xmlatt = anattribute.toXML();
					xmlout += xmlatt;
				}
				else if(noprintlist.contains(anattribute.getRoot()))
					;
				else
				{
				String xmlatt = anattribute.toXML();
				xmlout += xmlatt;
				}
			}
		}

		xmlout += "</contact>" + "\n";
		return xmlout;
	}
	
	public String toXCard(Vector<String> attkeys, mcMappings notusedhere)
	{
		String xmlout = "<contact id='" + StringEscapeUtils.escapeXml(getTID())
				+ "' type='" + getKind() + "'   >" + "\n";
		for (String attkey : attkeys)
		{
			mcAttribute anattribute = attributes.get(attkey);
			if (anattribute != null)
			{
				String xmlatt = anattribute.toXML();
				xmlout += xmlatt;
			}
		}

		xmlout += "</contact>" + "\n";
		return xmlout;
	}

	public void updateAttribute(String attroot, String attqual, String newvalue)
	{
		mcAttribute anattribute = getAttribute(attroot, attqual);

		if (anattribute == null)
		{
			anattribute = createAttribute(attroot, attqual);
			attributes.put(anattribute);
		}
		anattribute.setValue(newvalue);
	}

	public void updateAttributebyKey(String editattributekey,
			String newattributevalue)
	{
		String[] part = mcUtilities.parseLabel(editattributekey);
		updateAttribute(part[0], part[1], newattributevalue);
	}

	public void updateContact()
	{
		for (Entry<String, mcAttribute> arec : attributes.entrySet())
		{

			mcAttribute imatt = arec.getValue();
			imatt.setID(this.getID());
			if (!imatt.getRoot().equalsIgnoreCase("prodid/")
					&& !imatt.getRoot().equalsIgnoreCase("version/"))
				imatt.dbupsertAttribute();
		}
	}

	public void updateContactTID(String text)
	{
		this.TID = text;
		updateTID(text);
		System.out.println(" updating " + this.TID);
	}

	public void updateContactKIND(String newtype)
	{
		PreparedStatement st;
		String query = "update attributeValues set 'value' = ? where cid= ? and root='kind'  and qualifier= '' ";
		System.out.println(" updating " + newtype + " " + CID + query);
		try
		{
			st = con.prepareStatement(query);
			st.setString(1, newtype);
			st.setInt(2, CID);
			st.executeUpdate();

			st.close();

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public boolean updateFromImport(mcImportContact impcontact)
	{
		String impupdate = impcontact.getimpupdate();
		// System.out.println( " impcontact update :"+impupdate );
		// System.out.println( " current contact update :"+ update );
		boolean isupdated = false;
		for (Entry<String, mcImportAttribute> arec : impcontact.getAttributes()
				.entrySet())
		{
			mcImportAttribute imatt = arec.getValue();
			String aroot = imatt.getRoot();
			String aqual = imatt.getQualifier();
			String value = imatt.getValue().trim();
		
			// System.out.println( " import attribute " +aroot+"/"+aqual);
			mcAttributes foundatts = attributes.findAllbyRoot(aroot);
			
			if (foundatts == null || foundatts.size()==0)
			{
				createAttribute(aroot, aqual, value, impupdate);
				isupdated = true;
				System.out.println(" create new attribute a " + aroot + "/ "
						+ aqual + "=" + value);
			} else
			{
				
				if (foundatts.matchesVcardValue(value))
				{
					
				} else
				{
					System.out.println(
							" values in " + getTID()+ " dont match in attributes  " + aroot);
					mcAttribute foundatt = attributes.find(aroot, aqual);
					if (foundatt != null)
					{
						String oldupdatedt = foundatt.getUpdate();
						if (oldupdatedt == null
								|| impupdate.compareTo(oldupdatedt) > 0)
						{
							foundatt.setValue(value);
							foundatt.setUpdate(impupdate);
							System.out.println(
									" newest value used " + value + " dt "
											+ oldupdatedt + " iu " + impupdate);
							isupdated = true;
						}
						else
						{
							System.out.println(
									" unchged  value " + value + " dt "
											+ oldupdatedt + " iu " + impupdate);
						}
					} else
					{
						createAttribute(aroot, aqual, value, impupdate);
						isupdated = true;
						System.out.println(" create new attribute b " + aroot
								+ "/ " + aqual + "=" + value);
					}
				}
			}
		}
		return isupdated;
	}

	public void updateTID(String newtid)
	{

		PreparedStatement st;
		String query = "update attributeValues set 'value' = ? where cid= ? and root='tid'  and qualifier= '' ";

		try
		{
			st = con.prepareStatement(query);
			st.setString(1, newtid);
			st.setInt(2, CID);
			st.executeUpdate();
			st.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	

	
}
