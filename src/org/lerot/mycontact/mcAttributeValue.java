package org.lerot.mycontact;

public class mcAttributeValue
{
	static boolean isArrayData(String invalue)
	{
		if (invalue == null) return false;
		invalue = invalue.trim();
		if (invalue != null && invalue != "")
		{
			if (invalue.startsWith("{") && invalue.endsWith("}"))
			{
				return true;
			}
		}
		return false;
	}

	mcAttribute parent = null;

	String stringvalue;

	public mcAttributeValue(mcAttribute aparent, String avalue)
	{
		parent = aparent;
		stringvalue = avalue;
	}

	/*
	public mcAttributeValue(mcAttribute aparent, Vector<String> valuelist)
	{
		parent = aparent;
		stringvalue = "";
	}
	*/

	public String getFormattedValue()
	{
		if (isArrayType())
		{
			return mcKeyValueDataType.formattedArrayValue(stringvalue);
		} else
			return stringvalue;
	}

	public String getValue()
	{
		if(isArrayType() )
			{
			if(isArrayData(stringvalue)) return stringvalue;
			else
			{
				mcKeyValueDataType dt = (mcKeyValueDataType) parent.getType();
				String arraydata = dt.makeArray(stringvalue);
				return arraydata;
			}
			}
		return stringvalue;
	}

	public boolean isArrayType()
	{
		mcDataType type = parent.getType();
		if (type.isArrayType()) return true;
		return false;
	}

	public boolean isNull()
	{
		if (length() < 1) return true;
		else
			return false;
	}

	public int length()
	{
		if (stringvalue == null) return 0;
		else
			return stringvalue.length();
	}

	public void setValue(String newstr)
	{
		stringvalue = newstr;
	}

}
