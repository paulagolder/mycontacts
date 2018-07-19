package org.lerot.mycontact;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Vector;

import org.dom4j.Node;
import org.dom4j.io.SAXReader;
//import org.apache.commons.lang3.StringEscapeUtils;
//import org.dom4j.Document;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

public class mcPDF
{

	private static Font largeBold = new Font(Font.FontFamily.HELVETICA, 16,
			Font.BOLD);
	private static Font smallNormal = new Font(Font.FontFamily.HELVETICA, 10,
			Font.NORMAL);
	private static Font selectedfont;
	private static Map<String, Map<String, String>> labeltemplates = null;
	Document document;
	PdfWriter writer;
	PdfContentByte canvas;
	int a4h = 842;
	int a4w = 595;
	private float lm, rm, bm, tm, tw, th;
	private String title;
	private int nrows;
	private int ncols;
	private int ncells;
	private float cellheight;
	//private float cellwidth;
	private float[] collist;
	//private float padding;
	private float pb;
	private float pr;
	private float pl;
	private float pt;
	private boolean rotate;
	

	public mcPDF(File outfile, String atitle, String layout)
	{
		title = atitle;
		if (labeltemplates == null)
		{
			labeltemplates = readTemplates();
		}
		setLayout(layout);
		document = new Document(PageSize.A4, lm, rm, tm, bm);
		try
		{
			writer = PdfWriter.getInstance(document,
					new FileOutputStream(outfile));
			// canvas = writer.getDirectContent();
		} catch (FileNotFoundException | DocumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		document.open();
		
	}

	private void addMetaData()
	{
		document.addTitle(title);
		document.addSubject("generated from mcContacts");
		document.addAuthor("author");
	}

	public String makeBlockAddress(mcContact acontact,boolean showuk)
	{
		String ostr = "";
		//String name = acontact.getName("title fn mn sn ");
		//if (!name.trim().isEmpty()) ostr += name + "\n";

		String address = acontact.makeBlockAddress("\n",showuk);
		if (address != null && !address.isEmpty())
		{
				ostr += address;
		} else
		{
			System.out.println(" no address for :" + acontact);
			ostr = null;
		}

		return ostr;
	}

	public int makeLabelPage(String address, int startcell)
	{
		addMetaData();
		document.newPage();

		int k = 0;
		int p = 1;

		try
		{
			document.newPage();
			PdfPTable table = new PdfPTable(ncols);
			table.setTotalWidth(tw);
			table.setLockedWidth(true);
			table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

			table.setWidths(collist);
			Paragraph p0 = new Paragraph();
			p0.add(new Phrase(" ", selectedfont));
			PdfPCell c0 = new PdfPCell(p0);
			c0.setFixedHeight(cellheight);
			c0.setPaddingLeft(pl);
			c0.setPaddingRight(pr);
			c0.setPaddingTop(pt);
			c0.setPaddingBottom(pb);
			c0.setBorder(Rectangle.NO_BORDER);
			if (startcell > 1)
			{
				for (int i = 1; i < startcell; i++)
				{
					table.addCell(c0);
				}
			}
			k = startcell - 1;

			if (address != null && !address.isEmpty())
			{
				Paragraph p1 = new Paragraph();
				p1.add(new Phrase(address, selectedfont));
				PdfPCell c1 = new PdfPCell(p1);
				c1.setFixedHeight(cellheight);
				c1.setPaddingLeft(pl);
				c1.setPaddingRight(pr);
				c1.setPaddingTop(pt);
				c1.setPaddingBottom(pb);			 
				c1.setBorder(Rectangle.NO_BORDER);
				if (rotate)
				{
					c1.setRotation(90);
				}
				table.addCell(c1);
			}

			int rowused = k % ncols;
			System.out.println(" remainder is " + rowused);
			if (rowused > 0)
			{
				for (int i = 0; i < ncols - rowused; i++)
				{

					table.addCell(c0);
				}
			}
			table.completeRow();
			document.add(table);
		} catch (DocumentException e)
		{
			e.printStackTrace();
		}
		document.close();
		return p;
	}

	public int makeLabelsPages(mcContacts searchresults, int startcell, boolean showuk)
	{
		addMetaData();
		document.newPage();

		int k = 0;
		int p = 1;

		try
		{
			document.newPage();
			PdfPTable table = new PdfPTable(ncols);
			table.setTotalWidth(tw);
			table.setLockedWidth(true);
			table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

			table.setWidths(collist);
			Paragraph p0 = new Paragraph();
			p0.add(new Phrase(" ", selectedfont));
			PdfPCell c0 = new PdfPCell(p0);
			c0.setFixedHeight(cellheight);
			c0.setPaddingLeft(pl);
			c0.setPaddingRight(pr);
			c0.setPaddingTop(pt);
			c0.setPaddingBottom(pb);
			c0.setBorder(Rectangle.NO_BORDER);
			if (startcell > 1)
			{
				for (int i = 1; i < startcell; i++)
				{
					table.addCell(c0);
				}
			}
			k = startcell - 1;
			for (mcContact acontact : searchresults.makeOrderedContactsVector())
			{
				acontact.fillContact();
				String address = makeBlockAddress(acontact,showuk);
				if (address != null && !address.isEmpty())
				{
					Paragraph p1 = new Paragraph();
					p1.add(new Phrase(address, selectedfont));
					PdfPCell c1 = new PdfPCell(p1);
					c1.setFixedHeight(cellheight);
					c1.setPaddingLeft(pl);
					c1.setPaddingRight(pr);
					c1.setPaddingTop(pt);
					c1.setPaddingBottom(pb);		
					c1.setBorder(Rectangle.NO_BORDER);
					if (rotate)
					{
						c1.setRotation(90);
					}

					table.addCell(c1);
					k++;
					if (k == ncells)
					{
						document.add(table);
						document.newPage();
						table = new PdfPTable(ncols);
						table.setTotalWidth(tw);
						table.setLockedWidth(true);
						table.setWidths(collist);
						k = 0;
						p++;
					}
				}
			}
			int rowused = k % ncols;
			System.out.println(" remainder IS " + rowused);
			if (rowused > 0)
			{
				for (int i = 0; i < ncols - rowused; i++)
				{
					table.addCell(c0);
				}
			}
			table.completeRow();
			document.add(table);
		} catch (DocumentException e)
		{
			e.printStackTrace();
		}
		document.close();
		return p;
	}

	private String makeBlockAddress(mcContact acontact)
	{
		return makeBlockAddress(acontact,false);
	}

	public Map<String, Map<String, String>> readTemplates()
	{
		try
		{
			HashMap<String, Map<String, String>> addlist = new HashMap<String, Map<String, String>>();
			SAXReader reader = new SAXReader();
			org.dom4j.Document doc = reader
					.read(mcdb.topgui.dotcontacts + "/labelTemplates.xml");
			List<Node> nodes = doc.selectNodes("/labels/label");
			for (Node node : nodes)
			{
				Map<String, String> vlines = new HashMap<String, String>();
				String key = node.valueOf("@key");
				List<Node> lines = node.selectNodes("parameter");
				for (Node line : lines)
				{
					String pkey = line.valueOf("@key");
					String pvalue = line.valueOf("@value");
					vlines.put(pkey, pvalue);
				}
				addlist.put(key, vlines);
			}
			return addlist;

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void setLayout(String layout)
	{
		Map<String, String> tlayout = labeltemplates.get("3by3");
		collist = new float[] { 1, 1, 1 };
		if (layout.contains("3 x 7"))
		{
			tlayout = labeltemplates.get("3by7");
			collist = new float[] { 1, 1, 1 };
		} else if (layout.contains("2 x 4"))
		{
			tlayout = labeltemplates.get("2by4");
			collist = new float[] { 1, 1 };
		} else if (layout.contains("3 x 3"))
		{
			tlayout = labeltemplates.get("3by3");
			collist = new float[] { 1, 1, 1 };
		}
		else if (layout.contains("A4 Envelope"))
		{
			tlayout = labeltemplates.get("1by1");
			collist = new float[] { 1};
		}

		rotate = mcUtilities.toBoolean(tlayout.get("rotate"));
		nrows = mcUtilities.toInteger(tlayout.get("nrows"));
		ncols = mcUtilities.toInteger(tlayout.get("ncols"));
		lm = mcUtilities.toInteger(tlayout.get("leftmargin"));
		tm = mcUtilities.toInteger(tlayout.get("topmargin"));
		pt = mcUtilities.toInteger(tlayout.get("toppadding"));
		pl = mcUtilities.toInteger(tlayout.get("leftpadding"));
		pr = mcUtilities.toInteger(tlayout.get("rightpadding"));
		pb = mcUtilities.toInteger(tlayout.get("bottompadding"));
		bm = mcUtilities.toInteger(tlayout.get("bottommargin"));
		rm = mcUtilities.toInteger(tlayout.get("rightmargin"));
		ncells = nrows * ncols;
		tw = a4w - lm - rm;
		th = a4h - tm - bm;
		cellheight = th / nrows;
		//cellwidth = tw / ncols;
		selectedfont = smallNormal;
		if (tlayout.get("font").equalsIgnoreCase("largeBold"))
		{
			selectedfont = largeBold;
		}
		

	}

	

	public void setTitle(String atitle)
	{
		title = atitle;

	}

	public int makeLabelsPages(mcContacts selectedcontactlist, int sp)
	{
	
		return makeLabelsPages(selectedcontactlist,sp,false);
	}

}
