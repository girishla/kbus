package com.bigmantra.natco.service;

import com.bigmantra.natco.models.Expense;

import java.util.ArrayList;

/**
 * Created by Girish Lakshmanan on 8/21/19.
 */

public class ExpenseBuilder {
    private Expense expense;
    private String categoryId;
    private String createdBy;
    private ArrayList<byte[]> photoList;

    public ExpenseBuilder() {

    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public ArrayList<byte[]> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<byte[]> photoList) {
        this.photoList = photoList;
    }
}
