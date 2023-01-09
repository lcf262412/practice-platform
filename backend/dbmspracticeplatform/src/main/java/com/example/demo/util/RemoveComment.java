package com.example.demo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveComment {
	
	private Map<String, String> allMatches = new HashMap<String, String>(1);
	
	private Pattern tagCompile = Pattern.compile("(#S#\\d+#E#|#Q#\\d+#Q#)");
	
	private static Pattern quoteCompile = Pattern.compile("('(.*?)(?<!\')'(?:\\s|\\)|,|;|$))|(\"(.*?)(?<!\")\"(?:\\s|\\)|\\.|,|;|$))");
	
	private static Pattern databaseOrSchemaSQLCompile = Pattern.compile("(?i)(create|alter|drop)\\s*(database|schema)");

	private static Pattern setSchemaSQLCompile = Pattern.compile("(?i)SET(\\s*(session|local)\\s*|\\s*)(CURRENT_SCHEMA|SCHEMA|search_path)");

	private static Pattern changeMetaDataSQLCompile = Pattern.compile("(?i)(create|alter|drop|create or replace)\\s*(table|function|procedure|view|index)");

	private String getReplaceQuotesTag(Map<String, String> allMatchMap, String substring) {
        String tag = "#Q#" + (allMatchMap.size() + 1) + "#Q#";
        allMatchMap.put(tag, substring);
        return tag;
    }
	
	public String removeComment(String inputSQL) {
    	Matcher matcher;
        Pattern compile;
        String sql_no_begin_comments=inputSQL;
        compile = Pattern.compile("(?m)\\A\\s*(--.*?(" + "\n" + "|$)|\\/\\*.*?\\*\\/)", Pattern.DOTALL);
        while(true)
        {
            matcher = compile.matcher(sql_no_begin_comments);
            if (matcher.find()) {
                sql_no_begin_comments = matcher.replaceFirst("");
            }else{
                break;       
            }
        }
        compile = Pattern.compile("('(.*?)--(.*?)(?<!\')'(?:\\s|\\)|,|;|$))|(\"(.*?)--(.*?)(?<!\")\"(?:\\s|\\)|\\.|,|;|$))|"+
                                  "('(.*?)\\/\\*.*?\\*\\/(.*?)(?<!\')'(?:\\s|\\)|,|;|$))|(\"(.*?)\\/\\*.*?\\*\\/(.*?)(?<!\")\"(?:\\s|\\)|\\.|,|;|$))");
        matcher = compile.matcher(sql_no_begin_comments);
        int quotesOffset = 0;
        StringBuffer newsql;
        newsql =  new StringBuffer(sql_no_begin_comments.toString());
        while(matcher.find())
        {
            String originalStr = sql_no_begin_comments.substring(matcher.start(), matcher.end());
            String replaceTag = getReplaceQuotesTag(allMatches, originalStr);
            newsql.replace(matcher.start() + quotesOffset, 
                                         matcher.end() + quotesOffset, replaceTag);
            quotesOffset = quotesOffset + (replaceTag.length() - originalStr.length());
        }

        compile = Pattern.compile("(?i)^\\s*(CREATE\\s+FUNCTION|CREATE\\s+OR\\s+REPLACE\\s+FUNCTION|CREATE\\s+PROCEDURE|"
                        + "CREATE\\s+PACKAGE|CREATE\\s+OR\\s+REPLACE\\s+PACKAGE|"
                        + "CREATE\\s+OR\\s+REPLACE\\s+PROCEDURE|CREATE\\s+TRIGGER|"
                        + "CREATE\\s+OR\\s+REPLACE\\s+TRIGGER|(?<!\\w)DECLARE(?!\\w)|(?<!\\w)BEGIN(?!\\w))");
        matcher = compile.matcher(newsql.toString());
        if (!matcher.find()) {
            compile = Pattern.compile("(?m)(--.*?(" + "\n" + "|$)|\\/\\*.*?\\*\\/)", Pattern.DOTALL);
            matcher = compile.matcher(newsql.toString());
            newsql =  new StringBuffer(matcher.replaceAll(""));
        }else{
            compile = Pattern.compile("(?m)(?i)(END)(;?)\\s+(--.*?(" + "\n" + "|$)|\\/\\*.*?\\*\\/)+\\Z", Pattern.DOTALL);
            while(true)
            {
                matcher = compile.matcher(newsql.toString());
                if (matcher.find()) {
                    newsql =  new StringBuffer(matcher.replaceFirst("END;"));
                }else{
                    break;       
                }
            }
        }
        
        String qry = newsql.toString();
        String grp = null;
        compile = tagCompile;
        matcher = compile.matcher(newsql);
        while (matcher.find()) {
            grp = matcher.group(0);
            qry = qry.replace(grp, allMatches.get(grp));
        }
        return qry;
    }
	
	public static boolean forbiddenSQL(String inputSQL) {
		Pattern compile;
        compile = quoteCompile;
        Matcher matcher;
        matcher = compile.matcher(inputSQL);
        StringBuffer newsql =  new StringBuffer(inputSQL.toString());
        newsql =  new StringBuffer(matcher.replaceAll(""));
        compile = Pattern.compile("(?m)(--.*?(" + "\n" + "|$)|\\/\\*.*?\\*\\/)", Pattern.DOTALL);
        matcher = compile.matcher(newsql.toString());
        newsql =  new StringBuffer(matcher.replaceAll(""));
        compile = databaseOrSchemaSQLCompile;
        matcher = compile.matcher(newsql.toString());
        if (matcher.find()) return true;
        compile = setSchemaSQLCompile;
        matcher = compile.matcher(newsql.toString());
        return matcher.find();
	}
	
	public static boolean judgesql(String inputSQL) {
		Pattern compile;
        compile = quoteCompile;
        Matcher matcher;
        matcher = compile.matcher(inputSQL);
        
        StringBuffer newsql =  new StringBuffer(inputSQL.toString());
        newsql =  new StringBuffer(matcher.replaceAll(""));
        compile = Pattern.compile("(?m)(--.*?(" + "\n" + "|$)|\\/\\*.*?\\*\\/)", Pattern.DOTALL);
        matcher = compile.matcher(newsql.toString());
        newsql =  new StringBuffer(matcher.replaceAll(""));
        
        compile = changeMetaDataSQLCompile;
        matcher = compile.matcher(newsql.toString());
        if (matcher.find()) return true;
        
        return false;
	}
	
}
