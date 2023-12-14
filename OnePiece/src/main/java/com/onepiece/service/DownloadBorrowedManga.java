package com.onepiece.service;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.onepiece.dao.StudentDAO;
import com.onepiece.model.Manga;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
public class DownloadBorrowedManga extends AbstractPdfView {



	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
	        HttpServletRequest request, HttpServletResponse response) throws Exception {

	    HttpSession session = request.getSession();

	    String studentEmail = (String) session.getAttribute("studentEmail");
	    StudentDAO studentDao = new StudentDAO();

	    List<Manga> borrowedMangas = studentDao.getBorrowedMangas(studentEmail);

	    Font blueFont = new Font(Font.HELVETICA, 14, Font.NORMAL, Color.BLUE);
	    Paragraph title = new Paragraph("ONE PIECE-THE MANGA LIBRARY \n"+ "You have borrowed these mangas:\n\n", blueFont);
	    title.setAlignment(Element.ALIGN_CENTER);
	    document.add(title);

	    for (int i = 0; i < borrowedMangas.size(); i++) {
	        Manga manga = borrowedMangas.get(i);

	        PdfPTable table = new PdfPTable(2);
	        table.setWidthPercentage(100);

	        PdfPCell mangaCoverCell = new PdfPCell();
	        if (manga.getCoverFileName() != null && !manga.getCoverFileName().isEmpty()) {
	            String imagePath = "/Users/veeru/eclipse-workspace/OnePiece/src/main/resources/static/images/" + manga.getCoverFileName(); // Replace with the actual image path
	            Image mangaCover = Image.getInstance(imagePath);
	            mangaCover.scaleAbsolute(100, 100);
	            mangaCoverCell.addElement(mangaCover);
	        }
	        mangaCoverCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        mangaCoverCell.setVerticalAlignment(Element.ALIGN_TOP);
            mangaCoverCell.setBorderColor(Color.WHITE);


	        PdfPCell mangaDetailsCell = new PdfPCell();
	        mangaDetailsCell.addElement(new Phrase("Manga " + (i + 1) + ":"));
	        mangaDetailsCell.addElement(new Phrase("MangaID: " + manga.getMangaID()));
	        mangaDetailsCell.addElement(new Phrase("Anime Name: " + manga.getAnimeName()));
	        mangaDetailsCell.addElement(new Phrase("Category: " + manga.getCategory()));
	        mangaDetailsCell.addElement(new Phrase("Written By: " + manga.getWrittenBy()));

	        mangaDetailsCell.setBorderColor(Color.WHITE);

	        table.addCell(mangaCoverCell);
	        table.addCell(mangaDetailsCell);

	        document.add(table);

	        document.add(new Phrase("\n"));
	    }

	    document.close();
	}

}
