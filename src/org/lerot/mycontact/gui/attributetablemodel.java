package org.lerot.mycontact.gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class attributetablemodel extends AbstractTableModel
{

	private static final long serialVersionUID = 1L;

	private String[] columnNames = { "Root", "Qualifier", "value", "Selected" };

	private Vector<Vector<Object>> data = new Vector<Vector<Object>>();

	// public final Object[] longValues = { "", new Integer(20), new Float(20),
	// new Float(20), Boolean.TRUE };

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public int getRowCount()
	{
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		return ((Vector<?>) data.get(row)).get(col);
	}

	public String getColumnName(int col)
	{
		return columnNames[col];
	}

	public Class<? extends Object> getColumnClass(int c)
	{
		return getValueAt(0, c).getClass();
	}

	public void setValueAt(Object value, int row, int col)
	{
		((Vector<Object>) data.get(row)).setElementAt(value, col);
		fireTableCellUpdated(row, col);
	}

	public boolean isCellEditable(int row, int col)
	{
		if (3 == col || 1 == col)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public void insertData(Object[] values)
	{
		data.add(new Vector<Object>());
		for (int i = 0; i < values.length; i++)
		{
			((Vector<Object>) data.get(data.size() - 1)).add(values[i]);
		}
		fireTableDataChanged();
	}

	public void removeRow(int row)
	{
		data.removeElementAt(row);
		fireTableDataChanged();
	}
}
