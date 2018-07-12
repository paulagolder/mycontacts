package org.lerot.mycontact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class mcAttributes extends mcDataObject
{

	public static mcAttributes getAttributes(int id)
	{
		mcAttributes atlist = new mcAttributes();
		String attkey = "*";
		String query = "select * from attributeValues where cid=" + id +" order by root ";
		PreparedStatement st;
		
		try
		{
			st = con.prepareStatement(query);
			ResultSet resset = st.executeQuery();
			while (resset.next())
			{
				String attroot = resset.getString("root");
				String attqual = resset.getString("qualifier");
				String attupdate = resset.getString("update_dt");
				mcAttribute anattribute = new mcAttribute(id, attroot,attqual,attupdate);
				anattribute.load(resset);
				atlist.put(anattribute.getKey(), anattribute);
			}
		} catch (Exception e)
		{
			System.out.println(" error in get Attributes " + attkey + " :" +query);
		}
		return atlist;

	}

	LinkedHashMap<String, mcAttribute> attributelist;

	public mcAttributes()
	{
		attributelist = new LinkedHashMap<String, mcAttribute>();
	}

	public Set<Entry<String, mcAttribute>> entrySet()
	{

		return attributelist.entrySet();
	}

	public mcAttribute find(String aroot,String aqualifier)
	{
		for (Entry<String, mcAttribute> anentry : attributelist.entrySet())
		{
			mcAttribute anattribute = anentry.getValue();
			if (anattribute.getRoot().equalsIgnoreCase(aroot)  && anattribute.getQualifier().equalsIgnoreCase(aqualifier) )
				return anattribute;
		}
		return null;
	}

	public mcAttribute find(String akey)
	{
		String part[] =  mcUtilities.parseLabel(akey);
		mcAttribute rootatt = find(part[0],part[1]);
		if(rootatt==null)
		{
			mcAttributes allatt= findAllbyRoot(part[0]);
			 rootatt= allatt.get(0);
					
		}
		return rootatt;
	}
	
	
	
	private mcAttribute get(int i)
	{
		int j=1;
		for (Entry<String, mcAttribute> anentry : attributelist.entrySet())
		{
			mcAttribute anattribute = anentry.getValue();
			if(anattribute!=null && j> i) return anattribute;
			j++;
		}
		return null;
	}

	public mcAttributes findAllbyRoot(String attroot)
	{
		mcAttributes list = new mcAttributes();
		for (Entry<String, mcAttribute> anentry : attributelist.entrySet())
		{
			mcAttribute anattribute = anentry.getValue();
			if (anattribute.getRoot().equalsIgnoreCase(attroot)   )
				list.put(anattribute);
		}
		return list;
	}
	
	public mcAttributes findAllnoQualifier(String attroot)
	{
		mcAttributes list = new mcAttributes();
		if(!attroot.contains("mobile"))//paul fix could be cleaner
				{
		mcAttribute anattribute =  get(attroot);
		if(anattribute!=null)
	    	list.put(anattribute);
				}
		return list;
	}
	
	public Vector<mcAttribute> filterAttributes(String key)
	{
		Vector<mcAttribute> list = new Vector<mcAttribute>();

		for (Entry<String, mcAttribute> anentry : attributelist.entrySet())
		{
			mcAttribute anattribute = anentry.getValue();
			String root = anattribute.getRoot();
			if (root != null && root.equals(key)) list.add(anattribute);
		}
		return list;
	}

	public mcAttribute get(String attkey)
	{
		return attributelist.get(attkey);
	}

	void put(String attkey, mcAttribute anattribute)
	{
		attributelist.put(attkey, anattribute);
	}

	void put(mcAttribute anattribute)
	{
		attributelist.put(anattribute.getKey(), anattribute);
	}

	public void remove(String attkey)
	{
		attributelist.remove(attkey);

	}

	public int size()
	{
		return attributelist.size();
	}

	public static mcAttributes FindByAttributeValue(String root, String tid)
	{
		mcAttributes atlist = new mcAttributes();

		String query = "select * from attributeValues where root= ? and value=? order by root ";
		PreparedStatement st;
		try
		{
			st = con.prepareStatement(query);
			st.setString(1, root);
			st.setString(2, tid);
			ResultSet resset = st.executeQuery();
			while (resset.next())
			{
				String attroot = resset.getString("root");
				String attqual = resset.getString("qualifier");
				int cid  = resset.getInt("cid");
				mcAttribute anattribute = new mcAttribute(cid, attroot,attqual);
				anattribute.load(resset);
				atlist.put(anattribute.getKey(), anattribute);
			}
		} catch (Exception e)
		{
			System.out.println(" error in get Attributes " +root + " " +tid);
		}
		return atlist;
	}
	
	

	public boolean containsVcardValue(String value)
	{
		for (Entry<String, mcAttribute> anentry : attributelist.entrySet())
		{
			mcAttribute anattribute = anentry.getValue();
			if(anattribute.containsValue(value) )return true;
		}
		return false;
	}
	
	public boolean matchesVcardValue(String value)
	{
		for (Entry<String, mcAttribute> anentry : attributelist.entrySet())
		{
			mcAttribute anattribute = anentry.getValue();
			if(anattribute.matchesVcardValue(value) )return true;
		}
		//System.out.println(" no match found for " +value + " " );
		return false;
	}

	

}
