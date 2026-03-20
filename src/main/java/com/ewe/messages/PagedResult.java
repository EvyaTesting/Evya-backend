package com.ewe.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PagedResult<T> {

	private Collection<T> content;
	private long totalElements;
	private int number;
	private int numberOfElements;
	private int size;
	private int totalPages;
	private Sort sort;
	private boolean hasContent;
	private boolean first;
	private boolean last;
	private boolean hasNext;
	private boolean hasPrevious;

	private Pageable nextPageable;
	private Pageable previousPageable;

	private int startPage;
	private int endPage;

	private List<Integer> pages = new ArrayList<Integer>();

	public PagedResult(Page<T> page) {
		this.content = page.getContent();
		this.totalElements = page.getTotalElements();
		this.number = page.getNumber();
		this.numberOfElements = page.getNumberOfElements();
		this.size = page.getSize();
		this.sort = page.getSort();
		this.totalPages = page.getTotalPages();
		this.hasContent = page.hasContent();

		this.hasPrevious = page.hasPrevious();
		this.hasNext = page.hasNext();
		this.first = page.isFirst();
		this.last = page.isLast();

		this.nextPageable = page.nextPageable();
		this.previousPageable = page.previousPageable();

		if (this.totalPages <= 5) {
			this.startPage = 1;
			this.endPage = this.totalPages;
		} else {
			if (this.number <= 3) {
				this.startPage = 1;
				this.endPage = 5;
			} else if (this.number + 1 >= this.totalPages) {
				this.startPage = this.totalPages - 4;
				this.endPage = this.totalPages;
			} else {
				this.startPage = this.number - 2;
				this.endPage = this.number + 2;
			}
		}
		for (int i = 1; i <= this.endPage; i++) {
			this.pages.add(i);

		}
		
		

		this.pages = this.pages.subList(Math.max(this.pages.size() - 5, 0), this.pages.size());
	}

	public Collection<T> getContent() {
		return content;
	}

	public void setContent(Collection<T> content) {
		this.content = content;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumberOfElements() {
		return numberOfElements;
	}

	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public boolean isHasContent() {
		return hasContent;
	}

	public void setHasContent(boolean hasContent) {
		this.hasContent = hasContent;
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

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public boolean isHasPrevious() {
		return hasPrevious;
	}

	public void setHasPrevious(boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	public Pageable getNextPageable() {
		return nextPageable;
	}

	public void setNextPageable(Pageable nextPageable) {
		this.nextPageable = nextPageable;
	}

	public Pageable getPreviousPageable() {
		return previousPageable;
	}

	public void setPreviousPageable(Pageable previousPageable) {
		this.previousPageable = previousPageable;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public List<Integer> getPages() {
		return pages;
	}

	public void setPages(List<Integer> pages) {
		this.pages = pages;
	}

	@Override
	public String toString() {
		return "PagedResult [content=" + content + ", totalElements=" + totalElements + ", number=" + number
				+ ", numberOfElements=" + numberOfElements + ", size=" + size + ", totalPages=" + totalPages + ", sort="
				+ sort + ", hasContent=" + hasContent + ", first=" + first + ", last=" + last + ", hasNext=" + hasNext
				+ ", hasPrevious=" + hasPrevious + ", nextPageable=" + nextPageable + ", previousPageable="
				+ previousPageable + "]";
	}

}