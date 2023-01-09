package com.example.demo.util;

import java.io.StringReader;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.WithItem;

public class JSQLParserUtils {
	
	/**
     * Checks if is query result edit supported.
     *
     * @param query the query
     * @return true, if is query result edit supported
     * @throws DatabaseCriticalException the database critical exception
     */
    public static boolean isQueryResultEditSupported(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select selectStatement = null;
        SelectVisitorWrap selVisitor = new SelectVisitorWrap();
        try {
            Statement stmt = parserManager.parse(new StringReader(query));
            if (!(stmt instanceof Select)) {
                return false;
            }
            selectStatement = (Select) stmt;

            PlainSelect ps = null;
            if (selectStatement.getSelectBody() instanceof PlainSelect) {
                ps = (PlainSelect) selectStatement.getSelectBody();
            }
            selectStatement.getSelectBody().accept(selVisitor);

            if (!selVisitor.hasSetOperations() && !isMoreThanOneTableinFrom(ps) && !isGroupByExists(ps)) {
                if (!isWithItemExists(selectStatement) && isAllowedSelectItems(ps) && !isHavingExists(ps)
                        && !isIntoExists(ps)) {
                    return true;
                }
            }
        } catch (OutOfMemoryError |JSQLParserException e1) {
            return false;
        }

        return false;
    }
    
    /**
     * Checks if is more than one tablein from.
     *
     * @param ps the ps
     * @return true, if is more than one tablein from
     */
    private static boolean isMoreThanOneTableinFrom(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        TablesNamesFinderAdapter tablesNamesFinder = new TablesNamesFinderAdapter();
        int tableCount = tablesNamesFinder.getTableList(ps).size();
        if ((tableCount != 1) || tablesNamesFinder.isSelectFromFunction() || tablesNamesFinder.isJoinExists()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Checks if is group by exists.
     *
     * @param ps the ps
     * @return true, if is group by exists
     */
    private static boolean isGroupByExists(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        GroupByElement groupBy = ps.getGroupBy();
        if (groupBy == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Checks if is with item exists.
     *
     * @param selectStatement the select statement
     * @return true, if is with item exists
     */
    private static boolean isWithItemExists(Select selectStatement) {
        if (selectStatement == null) {
            return false;
        }
        List<WithItem> withItemList = selectStatement.getWithItemsList();
        if (withItemList != null) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Checks if is allowed select items.
     *
     * @param ps the ps
     * @return true, if is allowed select items
     */
    private static boolean isAllowedSelectItems(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        ExpressionVisitorAdapterWrap expVisitor = new ExpressionVisitorAdapterWrap();
        List<SelectItem> sel = ps.getSelectItems();
        if (sel != null) {
            for (int index = 0; index < sel.size(); index++) {
                if (sel.get(index) instanceof AllColumns || sel.get(index) instanceof AllTableColumns) {
                    continue;
                }

                SelectExpressionItem selExpItem;
                if (sel.get(index) instanceof SelectExpressionItem)
                    selExpItem = (SelectExpressionItem) sel.get(index);
                else {
                	continue;
                }
                /* For Aliases */
                if (selExpItem.getAlias() != null) {
                    return false;
                } else {
                    selExpItem.getExpression().accept(expVisitor);
                    if (expVisitor.hasNonEditableSelectItem()) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }
    
    /**
     * Checks if is having exists.
     *
     * @param ps the ps
     * @return true, if is having exists
     */
    private static boolean isHavingExists(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        Expression having = ps.getHaving();
        if (having == null) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean isIntoExists(PlainSelect ps) {
        if (ps == null) {
            return false;
        }
        List<Table> into = ps.getIntoTables();
        if (into == null) {
            return false;
        } else {
            return true;
        }
    }
    
}
