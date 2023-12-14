package com.onepiece.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.onepiece.service.DownloadBorrowedManga;
import com.onepiece.service.DownloadMangaDetails;


@Controller
public class DownloadMangaController {


    @GetMapping("/downloadBorrowedMangas/pdf")
    public ModelAndView downloadBorrowedMangasPdf() {
    	View view = new DownloadBorrowedManga();
		return new ModelAndView(view);
    }
    
    @GetMapping("/downloadMangaDetails/xls")
	public ModelAndView downloadMangaXls() {
		View view = new DownloadMangaDetails();
		return new ModelAndView(view);
	}
}
