package com.onepiece.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.onepiece.model.Manga;

@Service
public class MangaService {



    public boolean isMangaOverdue(Manga manga) {
        LocalDate borrowDate = manga.getBorrowDate();

        // Check if the manga has been borrowed
        if (borrowDate == null) {
            // Manga has never been borrowed, so it's not overdue
            return false;
        }
        borrowDate.plusDays(3);
        return LocalDate.now().isAfter(borrowDate);
    }

}

