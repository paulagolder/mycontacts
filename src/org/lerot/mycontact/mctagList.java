package org.lerot.mycontact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class mctagList extends mcDataObject
{

	static Map<String, Integer> taglist;

	public mctagList()
	{
		super();
	}

	public static void reloadTags()
	{
		selectAllTags();
	}

	public static void selectAllTags()
	{
		Map<String, Integer> ustaglist = new HashMap<String, Integer>();
		taglistcomparator comparator = new taglistcomparator(ustaglist);

		taglist = new TreeMap<String, Integer>(comparator);
		String query = " select * from attributeValues where root = 'tags'  ";
		PreparedStatement st;
		int k = 1;
		try
		{
			st = con.prepareStatement(query);
			ResultSet resset = st.executeQuery();
			while (resset.next())
			{
				mcAttribute tatt = new mcAttribute(k, "tags", "");
				tatt.load(resset);
				if (tatt.isType("textlist"))
				{
					Set<String> tagset = mcTextListDataType.getTags(tatt);
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

			taglist.putAll(ustaglist);
			//
			// System.out.println(taglist);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void renorm()
	{
		String query = " select * from attributeValues where root LIKE 'tags%'  ";
		PreparedStatement st;
		int k = 1;
		try
		{
			st = con.prepareStatement(query);
			ResultSet resset = st.executeQuery();
			while (resset.next())
			{
				mcAttribute tatt = new mcAttribute(k, "tags");
				tatt.load(resset);
				if (tatt.isType("textlist"))
				{
					Set<String> tagset = mcTextListDataType.getTags(tatt);
					System.out.println(tagset);
					mcTextListDataType.insertTags(tatt.getValue(), tagset);
					tatt.insertValues(tagset);
				}
			}
			st.close();

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public static Map<String, Integer> getAllTags()
	{
		return taglist;
	}

	public static void replaceall(String attkey, String mergefrom,
			String mergeto)
	{
		String query = " update attributeValues set value = replace(value, ?, ?)  where root LIKE ?  ";
		PreparedStatement st;
		try
		{
			st = con.prepareStatement(query);
			st.setString(1, "#" + mergefrom + ";");
			st.setString(2, "#" + mergeto + ";");
			st.setString(3, attkey + "%");
			st.executeUpdate();
			st.close();
		} catch (SQLException e)
		{
			e.printStackTrace();

		}

	}

	public static void delete(String attkey, String todelete)
	{
		String query = " update attributeValues set value = replace(value, ?, '')  where root LIKE ? ";
		PreparedStatement st;
		try
		{
			st = con.prepareStatement(query);
			st.setString(1, "#" + todelete + ";");
			st.setString(2, attkey);
			st.executeUpdate();
			st.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

	}
}
