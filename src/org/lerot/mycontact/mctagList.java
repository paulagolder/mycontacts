package org.lerot.mycontact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class mctagList extends mcDataObject
{

	private  Map<String, Integer> taglist;

	public mctagList()
	{
		super();
	}

	public  void reloadTags()
	{
		selectAllTags();
	}

	public  void selectAllTags()
	{
		//System.out.println("reloading tags ");
		Map<String, Integer> ustaglist = new HashMap<String, Integer>();
		taglistcomparator comparator = new taglistcomparator(ustaglist);

		setTaglist(new TreeMap<String, Integer>(comparator));
		String query = " select * from attributeValues where root = 'tags'  ";
		PreparedStatement st;
		int k = 1;
		try
		{
			con =  datasource.getConnection();
			st = con.prepareStatement(query);
			ResultSet resset = st.executeQuery();
			while (resset.next())
			{
				mcAttribute tatt = new mcAttribute(k, "tags", "");
				tatt.load(resset);
				if (tatt.isType("taglist"))
				{
					Set<String> tagset = mcTagListDataType.getTags(tatt);
					for (String tag : tagset)
					{
						if (ustaglist.containsKey(tag))
						{
							ustaglist.put(tag, ustaglist.get(tag) + 1);
						} else
						{
							ustaglist.put(tag, 1);
						}
					}
					k++;
				}

			}
			st.close();

			getTaglist().putAll(ustaglist);
			//
			// System.out.println(taglist);
			datasource.disconnect();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public  void renorm()
	{
		String query = " select * from attributeValues where root =  'tags'   ";
		PreparedStatement st;
		int k = 1;
		try
		{
			con =  datasource.getConnection();
			st = con.prepareStatement(query);
			ResultSet resset = st.executeQuery();
			while (resset.next())
			{
				mcAttribute tatt = new mcAttribute(k, "tags","");
				tatt.load(resset);
				//System.out.print(tatt);
				//mcTextListDataType.getTags("value");
				if (tatt.isType("textlist"))
				{
					Set<String> tagset = mcTagListDataType.getTags(tatt);
					System.out.println(tagset);
					mcTagListDataType.insertTags(tatt.getValue(), tagset);
					tatt.insertValues(tagset);
				}
				else
				{
					System.out.println("===="+tatt);
					
					
				}
			}
			st.close();
			datasource.disconnect();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	///public  Map<String, Integer> getAllTags()
	//{
	//	return getTaglist();
	//}

	public void replaceall(String attkey, String mergefrom,
			String mergeto)
	{
		String query = " update attributeValues set value = replace(value, ?, ?)  where root LIKE ?  ";
		PreparedStatement st;
		try
		{
			con =  datasource.getConnection();
			st = con.prepareStatement(query);
			st.setString(1,  mergefrom );
			st.setString(2,  mergeto );
			st.setString(3, attkey + "%");
			System.out.println("After : " + st.toString());
			st.executeUpdate();
			st.close();
			datasource.disconnect();
		} catch (SQLException e)
		{
			e.printStackTrace();

		}

	}

	public void delete(String attkey, String todelete)
	{
		String query = " update attributeValues set value = replace(value, ?, '')  where root LIKE ? ";
		PreparedStatement st;
		try
		{
			con =  datasource.getConnection();
			st = con.prepareStatement(query);
			st.setString(1, "#" + todelete + ";");
			st.setString(2, attkey);
			st.executeUpdate();
			st.close();
			datasource.disconnect();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	public  Map<String, Integer> getTaglist()
	{
		return taglist;
	}

	public void setTaglist(Map<String, Integer> ataglist)
	{
		taglist = ataglist;
	}

	public boolean isEmpty()
	{
		if(taglist.size()<1) return true;
		return false;
	}

	public int size()
	{
		return taglist.size();
	}

	public Set<Entry<String, Integer>> entrySet()
	{
		return taglist.entrySet();
	}
}
