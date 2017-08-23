/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.util;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PageWrapper<T> {
    private final static int MAX_PAGE_ITEM_DISPLAY = 10;

    private Page<T> page;
    private boolean firstPage;
    private boolean hasPreviousPage;
    private boolean hasNextPage;
    private boolean lastPage;
    private String url;
    private List<PageItem> items;
    private int size;
    private int start;
    private int number;

    private int totalPages;

    public PageWrapper(Page<T> page, String url) {
        this.page = page;
        this.url = url;
        items = new ArrayList<>();
        number = page.getNumber() + 1;

        if (page.getTotalPages() <= MAX_PAGE_ITEM_DISPLAY) {
            start = 1;
            size = page.getTotalPages();
        } else {
            if (number <= MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY / 2) {
                start = 1;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else if (number >= page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY / 2) {
                start = page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY + 1;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else {
                start = number - MAX_PAGE_ITEM_DISPLAY / 2;
                size = MAX_PAGE_ITEM_DISPLAY;
            }
        }

        for (int i = 0; i < size; i++) {
            items.add(new PageItem(start + i, (start + i == number)));
        }
    }

    public int getTotalPages() {
        return this.page.getTotalPages();
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<PageItem> getItems() {
        return this.items;
    }

    public void setItems(List<PageItem> items) {
        this.items = items;
    }

    public List<T> getContent() {
        return this.page.getContent();
    }

    public int getSize() {
        return this.page.getSize();
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isFirstPage() {
        return this.page.isFirst();
    }

    public void setFirstPage(boolean firstPage) {
        this.firstPage = firstPage;
    }

    public boolean isLastPage() {
        return this.page.isLast();
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

    public boolean isHasPreviousPage() {
        return this.page.hasPrevious();
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return this.page.hasNext();
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    class PageItem {
        private int number;
        private boolean isCurrent;

        PageItem(int number, boolean isCurrent) {
            this.number = number;
            this.isCurrent = isCurrent;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public boolean isCurrent() {
            return isCurrent;
        }

        public void setCurrent(boolean current) {
            isCurrent = current;
        }
    }
}
