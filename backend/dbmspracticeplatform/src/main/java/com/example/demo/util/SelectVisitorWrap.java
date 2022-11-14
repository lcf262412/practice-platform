package com.example.demo.util;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.values.ValuesStatement;

public class SelectVisitorWrap implements SelectVisitor {
	
	private boolean hasSetOperations = false;
	
	/**
     * Checks for set operations.
     *
     * @return true, if successful
     */
    public boolean hasSetOperations() {
        return this.hasSetOperations;
    }

	@Override
	public void visit(PlainSelect arg0) {
	}

	@Override
	public void visit(SetOperationList arg0) {
		this.hasSetOperations = true;
	}

	@Override
	public void visit(WithItem arg0) {
	}

	@Override
	public void visit(ValuesStatement arg0) {
	}

}
