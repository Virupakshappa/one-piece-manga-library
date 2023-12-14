package com.onepiece.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import com.onepiece.dao.MangaDAO;
import com.onepiece.model.Manga;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DownloadMangaDetails extends AbstractXlsView {

	public DownloadMangaDetails() {

	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		MangaDAO mangaDao = new MangaDAO();
		List<Manga> mangas = mangaDao.adminSearchedManga();

		Sheet sheet = workbook.createSheet("new sheet");

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);

		Row headerRow = sheet.createRow(0);
		Cell mangaIDCell = headerRow.createCell(0);
		mangaIDCell.setCellValue("Manga ID");
		mangaIDCell.setCellStyle(headerStyle);

		Cell animeNameCell = headerRow.createCell(1);
		animeNameCell.setCellValue("Anime Name");
		animeNameCell.setCellStyle(headerStyle);

		Cell writtenByCell = headerRow.createCell(2);
		writtenByCell.setCellValue("Written By");
		writtenByCell.setCellStyle(headerStyle);

		Cell categoryCell = headerRow.createCell(3);
		categoryCell.setCellValue("Category");
		categoryCell.setCellStyle(headerStyle);

		Cell borrowedByCell = headerRow.createCell(4);
		borrowedByCell.setCellValue("BorrowedBy");
		borrowedByCell.setCellStyle(headerStyle);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 6));

		CellStyle subHeaderStyle = workbook.createCellStyle();
		subHeaderStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		subHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		Row subHeaderRow = sheet.createRow(1);

		Cell studentNameCell = subHeaderRow.createCell(4);
		studentNameCell.setCellValue("Student Name");
		studentNameCell.setCellStyle(subHeaderStyle);

		Cell emailCell = subHeaderRow.createCell(5);
		emailCell.setCellValue("Email");
		emailCell.setCellStyle(subHeaderStyle);

		Cell collegeNameCell = subHeaderRow.createCell(6);
		collegeNameCell.setCellValue("College Name");
		collegeNameCell.setCellStyle(subHeaderStyle);

		for (Manga manga : mangas) {
		    int rowIndex = mangas.indexOf(manga) + 2;
		    Row dataRow = sheet.createRow(rowIndex);
		    dataRow.createCell(0).setCellValue(manga.getMangaID());
		    dataRow.createCell(1).setCellValue(manga.getAnimeName());
		    dataRow.createCell(2).setCellValue(manga.getWrittenBy());
		    dataRow.createCell(3).setCellValue(manga.getCategory());

		    if (manga.getBorrowedBy() != null) {
		        dataRow.createCell(4).setCellValue(manga.getBorrowedBy().getFirstName());
		        dataRow.createCell(5).setCellValue(manga.getBorrowedBy().getEmail());
		        dataRow.createCell(6).setCellValue(manga.getBorrowedBy().getCollegeName());
		    } else {
		        for (int i = 4; i <= 6; i++) {
		            dataRow.createCell(i).setCellValue("Not Borrowed");
		        }
		    }
		}


	}
}
