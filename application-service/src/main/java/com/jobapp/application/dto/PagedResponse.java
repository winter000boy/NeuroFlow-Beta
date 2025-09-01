package com.jobapp.application.dto;

import java.util.List;

/**
 * Generic paged response DTO
 * Requirements: 2.4, 4.1, 4.2
 */
public class PagedResponse<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;
    
    // Constructors
    public PagedResponse() {}
    
    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.empty = content == null || content.isEmpty();
    }
    
    // Getters and Setters
    public List<T> getContent() {
        return content;
    }
    
    public void setContent(List<T> content) {
        this.content = content;
        this.empty = content == null || content.isEmpty();
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
        this.first = page == 0;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
        if (totalElements > 0) {
            this.totalPages = (int) Math.ceil((double) totalElements / size);
            this.last = page >= totalPages - 1;
        }
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
        if (size > 0) {
            this.totalPages = (int) Math.ceil((double) totalElements / size);
            this.last = page >= totalPages - 1;
        }
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        this.last = page >= totalPages - 1;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    public boolean isLast() {
        return last;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public boolean isEmpty() {
        return empty;
    }
    
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    
    public boolean hasContent() {
        return !empty;
    }
    
    public boolean hasNext() {
        return !last;
    }
    
    public boolean hasPrevious() {
        return !first;
    }
    
    @Override
    public String toString() {
        return "PagedResponse{" +
                "content=" + (content != null ? content.size() + " items" : "null") +
                ", page=" + page +
                ", size=" + size +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", first=" + first +
                ", last=" + last +
                ", empty=" + empty +
                '}';
    }
}