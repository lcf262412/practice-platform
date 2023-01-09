package com.example.demo.tool;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DBMetaDataTool {
	
	private Connection connection;
	private DatabaseMetaData databaseMetaData;
	private String queryUserSchemaSQL = "SELECT oid, nspname from pg_namespace where ((oid >= 16384 or nspname LIKE 'public')" + 
			"and nspname  NOT LIKE 'pg_%') and has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
	
	private String queryAllFuncSQL = "SELECT pr.oid oid, pr.proname objname FROM pg_proc pr "
			+ "WHERE has_function_privilege(pr.oid, 'EXECUTE') and pr.pronamespace= %d";
	
	private String queryAllTableSQL = "select tbl.oid oid, tbl.relname relname from pg_class tbl "
			+ "where tbl.relkind = 'r' and tbl.parttype ='n'and tbl.relnamespace = %d and has_table_privilege(tbl.oid,'SELECT');";
	
	private String queryAllViewSQL = "SELECT c.oid, c.relname AS viewname FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and c.relnamespace = %d and has_table_privilege(c.oid,'SELECT');";
	
	private String queryAllColumnInTableSQL = "WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = %d and relkind = 'r' and parttype ='n'), " + 
			"attr AS (select c.attnum as columnidx,c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.attrelid as tableoid from pg_attribute c "
			+ "where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0)" + 
			"select t.tableid as tableid ,c.columnidx,c.name,c.displaycolumns FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) ORDER BY t.tableid ,c.columnidx;";
	
	private String queryAllColumnInViewSQL = "WITH tbl AS ( select oid as viewid,relnamespace as namespaceid from pg_class where relnamespace =%d and relkind = 'v')," + 
			"attr AS ( select c.attnum as columnidx, c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.attrelid as tableoid from pg_attribute c "
			+ "where c.attrelid in (select viewid from tbl) and c.attisdropped = 'f' and c.attnum > 0)" + 
			"select t.viewid as viewid ,c.columnidx,c.name,c.displaycolumns FROM tbl t LEFT JOIN attr c ON(t.viewid = c.tableoid) ORDER BY t.viewid ,c.columnidx;";
	
	private String queryAllConstraintSQL = "SELECT c.conrelid as tableid, c.oid as constraintid, c.conname  as constraintname, c.contype as constrainttype "
			+ "FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=%d and cl.parttype not in ('p','v') and c.conrelid <> 0;";
	
	private String queryAllIndexSQL = "SELECT i.indrelid as tableId, i.indexrelid as oid, ci.relname as indexname FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) "
			+ "LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) WHERE t.relkind = 'r' and t.parttype ='n' and ci.relnamespace = %d";
	
	public DBMetaDataTool(Connection connection) throws SQLException {
		this.connection = connection;
		this.databaseMetaData = this.connection.getMetaData();
	}
	
	public List<String> getAllSchemas() throws SQLException {
		ResultSet rs = databaseMetaData.getSchemas();
		List<String> schemaList = new ArrayList<>();
		
		while(rs.next()) {
			schemaList.add(rs.getString(1)+":"+rs.getString(2));
		}
		
		return schemaList;
	}
	
	public List<Map<String, Object>> getUserSchemas() throws SQLException {
		Statement statement = connection.createStatement();
		
		ResultSet rs = statement.executeQuery(queryUserSchemaSQL);
		List<Map<String, Object>> schemaList = new ArrayList<>();
		
		while(rs.next()) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("oid", rs.getLong(1));
			modelMap.put("schemaName", rs.getObject(2));
			schemaList.add(modelMap);
		}
		
		statement.close();
		
		return schemaList;
	}
	
	public Map<String, Object> getSchemaMetaData(long schemaOid) throws SQLException{
		Map<String, Object> modelMap = new HashMap<>();
		modelMap.put("tableList", getAllTableMetaData(schemaOid));
		modelMap.put("viewList", getAllViewMetaData(schemaOid));
		modelMap.put("funcList", getAllFuncMetaData(schemaOid));
		
		return modelMap;
	}
	
	private List<Map<String, Object>> getAllTableMetaData(long schemaOid) throws SQLException{
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(String.format(Locale.ENGLISH, queryAllTableSQL, schemaOid));
		List<Map<String, Object>> tableList = new ArrayList<>();
		Map<Long, List<Map<String, Object>>> columnListMap = new HashMap<>();
		Map<Long, List<Map<String, Object>>> constraintListMap = new HashMap<>();
		Map<Long, List<Map<String, Object>>> indexListMap = new HashMap<>();
		
		while(rs.next()) {
			Map<String, Object> modelMap = new HashMap<>();
			long tableOid = rs.getLong(1);
			
			modelMap.put("oid", tableOid);
			modelMap.put("tablename", rs.getString(2));
			
			List<Map<String, Object>> columnList = new ArrayList<>();
			modelMap.put("columnList", columnList);
			columnListMap.put(tableOid, columnList);
			
			List<Map<String, Object>> constraintList = new ArrayList<>();
			modelMap.put("constraintList", constraintList);
			constraintListMap.put(tableOid, constraintList);
			
			List<Map<String, Object>> indexList = new ArrayList<>();
			modelMap.put("indexList", indexList);
			indexListMap.put(tableOid, indexList);
			
			tableList.add(modelMap);
		}
		
		statement.close();
		
		getAllColumnInTableBySchemaOid(schemaOid, columnListMap);
		getAllConstraintBySchemaOid(schemaOid, constraintListMap);
		getAllIndexBySchemaOid(schemaOid, indexListMap);
		
		return tableList;
	}
	
	private List<Map<String, Object>> getAllViewMetaData(long schemaOid) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(String.format(Locale.ENGLISH, queryAllViewSQL, schemaOid));
		List<Map<String, Object>> viewList = new ArrayList<>();
		Map<Long, List<Map<String, Object>>> columnListMap = new HashMap<>();
		
		while(rs.next()) {
			Map<String, Object> modelMap = new HashMap<>();
			long viewOid = rs.getLong(1);
			
			modelMap.put("oid", viewOid);
			modelMap.put("viewname", rs.getString(2));
			
			List<Map<String, Object>> columnList = new ArrayList<>();
			modelMap.put("columnList", columnList);
			columnListMap.put(viewOid, columnList);
			
			viewList.add(modelMap);
		}
		
		statement.close();
		
		getAllColumnInViewBySchemaOid(schemaOid, columnListMap);
		
		return viewList;
	}
	
	private List<Map<String, Object>> getAllFuncMetaData(long schemaOid) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(String.format(Locale.ENGLISH, queryAllFuncSQL, schemaOid));
		List<Map<String, Object>> funcList = new ArrayList<>();
		
		while(rs.next()) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("oid", rs.getLong(1));
			modelMap.put("funcname", rs.getString(2));
			funcList.add(modelMap);
		}
		
		statement.close();
		
		return funcList;
	}
	
	private void getAllColumnInTableBySchemaOid(long schemaOid, Map<Long, List<Map<String, Object>>> columnListMap) throws SQLException {
		if (columnListMap.isEmpty())
			return;
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(String.format(Locale.ENGLISH, queryAllColumnInTableSQL, schemaOid));
		
		while(rs.next()) {
			Map<String, Object> modelMap = new HashMap<>();
			long tableOid = rs.getLong(1);
			modelMap.put("columnidx", rs.getInt(2));
			modelMap.put("columnname", rs.getString(3));
			modelMap.put("columndisplay", rs.getObject(4));
			
			List<Map<String, Object>> columnList = columnListMap.get(tableOid);
			if (columnList == null)
				continue;
			columnList.add(modelMap);
		}
		
		statement.close();
	}
	
    private void getAllColumnInViewBySchemaOid(long schemaOid, Map<Long, List<Map<String, Object>>> columnListMap) throws SQLException {
    	if (columnListMap.isEmpty())
			return;
    	Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(String.format(Locale.ENGLISH, queryAllColumnInViewSQL, schemaOid));
		
		while(rs.next()) {
			Map<String, Object> modelMap = new HashMap<>();
			long viewOid = rs.getLong(1);
			modelMap.put("columnidx", rs.getInt(2));
			modelMap.put("columnname", rs.getString(3));
			modelMap.put("columndisplay", rs.getObject(4));
			
			List<Map<String, Object>> columnList = columnListMap.get(viewOid);
			if (columnList == null)
				continue;
			columnList.add(modelMap);
		}
		
		statement.close();
	}
	
    private void getAllConstraintBySchemaOid(long schemaOid, Map<Long, List<Map<String, Object>>> constraintListMap) throws SQLException{
    	if (constraintListMap.isEmpty())
			return;
    	Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(String.format(Locale.ENGLISH, queryAllConstraintSQL, schemaOid));
		
		while(rs.next()) {
			Map<String, Object> modelMap = new HashMap<>();
			long tableOid = rs.getLong(1);
			modelMap.put("constraintoid", rs.getInt(2));
			modelMap.put("constraintname", rs.getString(3));
			
			List<Map<String, Object>> constraintList = constraintListMap.get(tableOid);
			if (constraintList == null)
				continue;
			constraintList.add(modelMap);
		}
		
		statement.close();
	}
    
    private void getAllIndexBySchemaOid(long schemaOid, Map<Long, List<Map<String, Object>>> indexListMap) throws SQLException {
    	if (indexListMap.isEmpty())
			return;
    	Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(String.format(Locale.ENGLISH, queryAllIndexSQL, schemaOid));
		
		while(rs.next()) {
			Map<String, Object> modelMap = new HashMap<>();
			long tableOid = rs.getLong(1);
			modelMap.put("indexoid", rs.getInt(2));
			modelMap.put("indexname", rs.getString(3));
			
			List<Map<String, Object>> indexList = indexListMap.get(tableOid);
			if (indexList == null)
				continue;
			indexList.add(modelMap);
		}
		
		statement.close();
	}
	
}
