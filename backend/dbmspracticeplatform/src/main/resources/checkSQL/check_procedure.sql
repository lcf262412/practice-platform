CREATE FUNCTION replacefirst(source nvarchar2, find nvarchar2, repl nvarchar2) RETURNS nvarchar2
    LANGUAGE plpgsql NOT SHIPPABLE
    AS $$
    declare  ResultVar nvarchar2(10000)  ;-- return value  
    declare  pos int               ;   -- find the first position              ;  
BEGIN
    /*
	pos := instr( source,find,1,1);  
    if  pos = 0 then  return  source;   end if ;
    ResultVar := overlay(source  placing repl from  pos for  char_length(find));  
	*/
	ResultVar := regexp_replace(source,find,repl,'i'); 
    return ResultVar;  
END ;$$;

^^^separator^^^

create table check_error(starttime TIMESTAMP, endtime TIMESTAMP, exceptions TEXT);

^^^separator^^^

CREATE OR REPLACE procedure check_other(answer nvarchar2, correct nvarchar2, tableName nvarchar2, OUT rt integer)
    AS 
	DECLARE str1 NVARCHAR2(1000);
	declare str nvarchar2(1000);
	DECLARE check_answer nvarchar2(1000);
	DECLARE typen nvarchar2(6);
	DECLARE i int;
	DECLARE j int;
	declare  error_message varchar(1000);
    declare  exception_context varchar(1000);
	begin
		
	--清除前一阶段未删除的临时表
	if exists(select * from pg_catalog.pg_tables where pg_tables.tablename='tmp1' and schemaname like '%'||pg_current_sessid())
	then drop table tmp1;
	end if;
	
	if exists(select * from pg_catalog.pg_tables where pg_tables.tablename='tmp2' and schemaname like '%'||pg_current_sessid())
	then	drop table tmp2;
	end if;
	
	if exists(select * from pg_catalog.pg_tables where pg_tables.tablename='tmp3' and schemaname like '%'||pg_current_sessid())
	then	drop table tmp3;
	end if;
		
	if exists(select * from pg_catalog.pg_tables where pg_tables.tablename='tmp4' and schemaname like '%'||pg_current_sessid())
	then	drop table tmp4;
    end if;
	  
	--判断标准答案和学生答案是否为同一语句
	answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		
	if left(answer,6) != left(ltrim(correct),6)
	then
		rollback;
		rt:= 0;
	end if;
		
	--执行，判断语句是否存在问题--判断语法
	check_answer:=replace(answer,';',' ');
		
	typen:=left(ltrim(check_answer),6);
	if typen='delete'		
	then
		check_answer:=replacefirst(check_answer,'delete','select * ');
		EXECUTE 'select * from ('|| check_answer || ') a';
	end if;
		
	if typen='update'
	then
		check_answer:=replacefirst(check_answer,'update',' ');
		check_answer:=replacefirst(check_answer,'where','and');
		check_answer:=replacefirst(check_answer,'set','where');
		check_answer:=concat('select * from ',check_answer);
		str1 := concat(concat('select * from (',check_answer),') a');
		EXECUTE str1;
	end if;
		
	if typen='insert'		
	then	
		check_answer:=replacefirst(check_answer,'insert',' ');
		check_answer:=replacefirst(check_answer,'into',' ');
		i:=instr(check_answer, '(', 1, 1);
		j:=instr(check_answer, ')', 1, 1);
		check_answer:=overlay(check_answer placing ' ' from i for j-i+1);
		check_answer:=replacefirst(check_answer,'values',' ');
		i:=instr(check_answer, '(', 1, 1);
		
		if i!=0
		then
			j:=instr(check_answer, ')', 1, 1);
			check_answer:=overlay(check_answer placing ' ' from i for j-i+1);
		end if;
			
		check_answer:='select * from ' || check_answer; 
		EXECUTE 'select * from (' || check_answer || ') a';
			
	end if;
	
    --创建临时表	
	str:='create TEMPORARY table tmp1 as select * from ' || tablename;        
	EXECUTE str;
	str:='create TEMPORARY table tmp2 as select * from ' || tablename;       
	EXECUTE str;
	
	--用临时表替换目标表
	answer := replace(answer,tablename,'tmp1');
	EXECUTE answer ;
		
	correct:=replace(correct,tablename,'tmp2');
	EXECUTE correct ;

	--判断输出临时表结果是否相同，相同表示作答正确
	CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
	CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;

	if not exists(select * from tmp3) and not exists(select * from tmp4)
	then
		rt:= 1;
	else
		rt:= 0;
	end if;
	rollback;
	exception
	    when others then
		GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		error_message := exception_context || ' error ' || sqlerrm;
		rollback;
		insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		rt:= 0;
	end;

^^^separator^^^

CREATE OR REPLACE procedure check_select(answer nvarchar2, correct nvarchar2, OUT rt integer)
    AS
DECLARE  str1 NVARCHAR2(1000);
declare  check_answer nvarchar2(1000);
declare  str nvarchar2(1000);
declare  error_message varchar(1000);
declare  exception_context varchar(1000);
	BEGIN   
		--清除前一阶段未删除的临时表
		if exists(select * from pg_catalog.pg_tables where pg_tables.tablename='tmp1' and schemaname like '%'||pg_current_sessid())
		then drop table tmp1;
		end if;
		if exists(select * from pg_catalog.pg_tables where pg_tables.tablename='tmp2' and schemaname like '%'||pg_current_sessid())
		then	drop table tmp2;
		end if;
		if exists(select * from pg_catalog.pg_tables where pg_tables.tablename='tmp3' and schemaname like '%'||pg_current_sessid())
		then	drop table tmp3;
		end if;
		if exists(select * from pg_catalog.pg_tables where pg_tables.tablename='tmp4' and schemaname like '%'||pg_current_sessid())
		then	drop table tmp4;
        end if;
	
	    --判断是否为select语句
		answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
	
		if left( answer,6) != left(ltrim( correct),6)
		then	
		   rt:=0;
		ELSE
    		check_answer:=replace( answer,';',' ');
		    str1 := concat(concat('select * from (',check_answer),') a');
		    execute str1;
		
		    --执行，判断语句是否存在问题
			execute answer;
		    
			--将标准答案和学生答案的select结果输出到临时表中
	        str1 := concat('create TEMPORARY TABLE tmp1 as ',answer);
		    execute str1;

    		str1 := concat('create TEMPORARY TABLE tmp2 as ',correct);
		    execute str1;
		
            str:='select * from tmp1 union  select * from tmp2';
		    execute str;

            --判断输出临时表结果是否相同，相同表示作答正确
		    CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		    CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;

    		if not exists(select * from tmp3) and not exists(select * from tmp4)
		    then
				   rt:= 1;
		    else
				   rt:= 0;
		    end if;		
		end if;
		
		rollback;

		exception 
		   When  others THEN
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
			error_message := exception_context || ' error ' || sqlerrm;
			rollback;
			insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:=0;

	end;

^^^separator^^^
	
CREATE OR REPLACE procedure check_createTable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)
    AS
	declare  correctTableOid INTEGER;
	declare  TEMPTableOid INTEGER;
	declare  error_message varchar(1000);
    declare  exception_context varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		
		--判断是否为create table语句
		if not regexp_like(answer, '^create([ ]|chr(9)|chr(10)|chr(13))+?table', 'i')
	    then
		    rt:= 0;
		    rollback;
			return;
	    end if;
		
		raise notice 'check create table phase 1 ok';
		
		--如果存在关联表，创建相应的关联临时表
		if initSQL is NULL
		then
			NULL;
		else
			initSQL:=regexp_replace(initSQL,'(create([ ]|chr(9)|chr(10)|chr(13))+?)table',E'\\1TEMPORARY table','ig');
			EXECUTE initSQL;
		end if;
		--将作答表改为临时表
		answer:=regexp_replace(answer,'(create([ ]|chr(9)|chr(10)|chr(13))+?)table',E'\\1TEMPORARY table','i');
		
		EXECUTE answer;
		
		--判断创建对象是否为目标对象
		if not exists(select * from pg_class where pg_class.relname = lower(tableName) and pg_class.relnamespace = pg_my_temp_schema())
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create table phase 2 ok';
		
		--获取临时表和数据表的oid
		EXECUTE IMMEDIATE 'select oid from pg_class where relname = :1 and relnamespace = pg_my_temp_schema()' USING IN lower(tableName), OUT TEMPTableOid;
		
		EXECUTE IMMEDIATE 'select pg_class.oid from pg_class, pg_namespace where pg_class.relname = :1 and pg_class.relnamespace = pg_namespace.oid and pg_namespace.nspname = current_schema()' USING IN lower(tableName), OUT correctTableOid;
		
		--判断数据表的元数据是否存在差异
		CREATE TEMPORARY TABLE tmp1 as select relkind, relchecks, parttype from pg_class where pg_class.oid = TEMPTableOid;
		
		CREATE TEMPORARY TABLE tmp2 as select relkind, relchecks, parttype from pg_class where pg_class.oid = correctTableOid;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check create table phase 3 ok';
		
		--判断约束是否相同
		
		CREATE TEMPORARY TABLE tmp1 as select conname, contype, convalidated, confupdtype, confdeltype, confmatchtype, confkey, consrc from pg_constraint where conrelid = TEMPTableOid;
		
		CREATE TEMPORARY TABLE tmp2 as select conname, contype, convalidated, confupdtype, confdeltype, confmatchtype, confkey, consrc from pg_constraint where conrelid = correctTableOid;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
	    CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
	    if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check create table phase 4 ok';
		
		--判断列是否相同
		CREATE TEMPORARY TABLE tmp1 as select attname, atttypid, atttypmod, attnotnull from pg_attribute where attrelid = TEMPTableOid and attnum > 0 and attisdropped = false;
		
		CREATE TEMPORARY TABLE tmp2 as select attname, atttypid, atttypmod, attnotnull from pg_attribute where attrelid = correctTableOid and attnum > 0 and attisdropped = false;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
	    CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
	    if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create table phase 5 ok';
        rt:= 1;		
        rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;
	
^^^separator^^^

CREATE OR REPLACE procedure check_alterTable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)
    AS
	declare  correctTableOid INTEGER;
	declare  TEMPTableOid INTEGER;
	declare  error_message varchar(1000);
	declare  exception_context  varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		
		--判断是否为alter table语句
		--此处可优化：需要把连续空格变为一个空格
		if not regexp_like(answer, '^alter([ ]|chr(9)|chr(10)|chr(13))+?table', 'i')
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check alter table phase 1 ok';
		--准备测试临时表
		initSQL:=regexp_replace(initSQL,'(create([ ]|chr(9)|chr(10)|chr(13))+?)table',E'\\1TEMPORARY table','ig');
		
		EXECUTE initSQL;
		
		--判断测试临时表是否创建成功
		if not exists(select * from pg_class where pg_class.relname = lower(tableName) and pg_class.relnamespace = pg_my_temp_schema())
		then
		    rt:= 2;
			rollback;
			return;
		end if;
		
		raise notice 'check alter table phase 2 ok';
		
		EXECUTE answer;
		--获取临时表和数据表的oid
		EXECUTE IMMEDIATE 'select oid from pg_class where relname = :1 and relnamespace = pg_my_temp_schema()' USING IN lower(tableName), OUT TEMPTableOid;
		
		EXECUTE IMMEDIATE 'select pg_class.oid from pg_class, pg_namespace where pg_class.relname = :1 and pg_class.relnamespace = pg_namespace.oid and pg_namespace.nspname = current_schema()' USING IN lower(tableName), OUT correctTableOid;
		--判断数据表的元数据是否存在差异
		CREATE TEMPORARY TABLE tmp1 as select relkind, relchecks, parttype from pg_class where pg_class.oid = TEMPTableOid;
		
		CREATE TEMPORARY TABLE tmp2 as select relkind, relchecks, parttype from pg_class where pg_class.oid = correctTableOid;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check alter table phase 3 ok';
		
		--判断约束是否相同
		
		CREATE TEMPORARY TABLE tmp1 as select conname, contype, convalidated, confupdtype, confdeltype, confmatchtype, confkey, consrc from pg_constraint where conrelid = TEMPTableOid;
		
		CREATE TEMPORARY TABLE tmp2 as select conname, contype, convalidated, confupdtype, confdeltype, confmatchtype, confkey, consrc from pg_constraint where conrelid = correctTableOid;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
	    CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
	    if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check alter table phase 4 ok';
		
		--判断列是否相同
		CREATE TEMPORARY TABLE tmp1 as select attname, atttypid, atttypmod, attnotnull from pg_attribute where attrelid = TEMPTableOid and attnum > 0 and attisdropped = false;
		
		CREATE TEMPORARY TABLE tmp2 as select attname, atttypid, atttypmod, attnotnull from pg_attribute where attrelid = correctTableOid and attnum > 0 and attisdropped = false;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
	    CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
	    if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check alter table phase 5 ok';
        rt:= 1;		
        rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;

^^^separator^^^

CREATE OR REPLACE procedure check_createView(answer nvarchar2, viewName nvarchar2, OUT rt integer)
    AS
	declare  correctViewOid INTEGER;
	declare  TEMPViewOid INTEGER;
	declare  correctViewName varchar(1000);
	declare  error_message varchar(1000);
	declare  exception_context varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--需优化
		if not regexp_like(answer, '^create([ ]|chr(9)|chr(10)|chr(13))+?view', 'i')
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise info 'check create view phase 1 ok';
		--将作答语句改为临时视图
		answer:=regexp_replace(answer,'(create([ ]|chr(9)|chr(10)|chr(13))+?)view',E'\\1TEMPORARY view','i');
		
		EXECUTE answer;
		
		--判断创建对象是否为目标对象
		if not exists(select * from pg_class where pg_class.relname = lower(viewName) and pg_class.relnamespace = pg_my_temp_schema())
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create view phase 2 ok';
		--获取临时视图和标准答案视图的oid
		EXECUTE IMMEDIATE 'select oid from pg_class where relname = :1 and relnamespace = pg_my_temp_schema()' USING IN lower(viewName), OUT TEMPViewOid;
		
		EXECUTE IMMEDIATE 'select pg_class.oid from pg_class, pg_namespace where pg_class.relname = :1 and pg_class.relnamespace = pg_namespace.oid and pg_namespace.nspname = current_schema()' USING IN lower(viewName), OUT correctViewOid;
		
		--判断视图的元数据是否存在差异
		CREATE TEMPORARY TABLE tmp1 as select relkind, relnatts, relchecks, relhaspkey, parttype from pg_class where pg_class.oid = TEMPViewOid;
		
		CREATE TEMPORARY TABLE tmp2 as select relkind, relnatts, relchecks, relhaspkey, parttype from pg_class, pg_namespace where pg_class.oid = correctViewOid;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check create view phase 3 ok';
		
		--判断列是否存在差异
		CREATE TEMPORARY TABLE tmp1 as select attname, atttypid, atttypmod, attnotnull from pg_attribute where attrelid = TEMPViewOid and attnum > 0;
		
		CREATE TEMPORARY TABLE tmp2 as select attname, atttypid, atttypmod, attnotnull from pg_attribute where attrelid = correctViewOid and attnum > 0;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
	    CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
	    if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check create view phase 4 ok';
		
		--比对视图内的数据
		
		EXECUTE IMMEDIATE 'CREATE TEMPORARY TABLE tmp1 as select * from '||viewName;
		
		--获取标准视图的所在模式，并在视图名前加模式名，从而定位到标准视图而非临时视图
		correctViewName = current_schema()||'.'||viewName;
		
		EXECUTE IMMEDIATE 'CREATE TEMPORARY TABLE tmp2 as select * from '||correctViewName;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
	    CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
	    if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create view phase 5 ok';
        rt:= 1;		
        rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;
	
^^^separator^^^

CREATE OR REPLACE procedure check_createIndex(answer nvarchar2, indexName nvarchar2,initSQL nvarchar2, OUT rt integer)
    AS
	declare  correctIndexOid INTEGER;
	declare  TEMPIndexOid INTEGER;
	declare  error_message varchar(1000);
	declare  exception_context  varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		
		--判断是否为create index语句
		if not regexp_like(answer, '^create([ ]|chr(9)|chr(10)|chr(13))+?index', 'i')
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create index phase 1 ok';
		
		--准备测试临时表
		initSQL:=regexp_replace(initSQL,'(create([ ]|chr(9)|chr(10)|chr(13))+?)table',E'\\1TEMPORARY table','ig');
		
		EXECUTE initSQL;
		
		EXECUTE answer;
		
		--判断临时索引是否创建成功
		if not exists(select pg_class.oid from pg_class, pg_index where relname = lower(indexName) and relnamespace = pg_my_temp_schema())
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create index phase 2 ok';
		
		--获取临时索引和索引的oid
		EXECUTE IMMEDIATE 'select oid from pg_class where relname = :1 and relnamespace = pg_my_temp_schema()' USING IN lower(indexName), OUT TEMPIndexOid;
		
		EXECUTE IMMEDIATE 'select pg_class.oid from pg_class, pg_namespace where pg_class.relname = :1 and pg_class.relnamespace = pg_namespace.oid and pg_namespace.nspname = current_schema()' USING IN lower(indexName), OUT correctIndexOid;
		
		--判断索引属性是否相同
		CREATE TEMPORARY TABLE tmp1 as select relname, relkind, indnatts, indisunique, indisprimary, indisexclusion, indisclustered, indkey from pg_class, pg_index where pg_class.oid = indexrelid and pg_class.oid = TEMPIndexOid;
		
		CREATE TEMPORARY TABLE tmp2 as select relname, relkind, indnatts, indisunique, indisprimary, indisexclusion, indisclustered, indkey from pg_class, pg_index where pg_class.oid = indexrelid and pg_class.oid = correctIndexOid;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
			rt:= 0;
			rollback;
			return;
		end if;
		raise notice 'check create index phase 3 ok';
		rt:= 1;
		rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;
	
^^^separator^^^

CREATE OR REPLACE procedure check_createUser(answer nvarchar2, userName nvarchar2, OUT rt integer)
    as
	declare TEMPUserName nvarchar2(1000);
	declare  error_message varchar(1000);
	declare  exception_context  varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--判断是否为create user/role语句
		if not regexp_like(answer, '^create([ ]|chr(9)|chr(10)|chr(13))+?(user|role)', 'i')
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create user phase 1 ok';
		
		--将作答用户用户名修改
		TEMPUserName = userName||'_'||pg_current_sessid();
		answer:=replacefirst(answer,userName,TEMPUserName);
		
		EXECUTE answer;
		
		if not exists(select * from pg_authid where rolname = lower(TEMPUserName))
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create user phase 2 ok';
		
		CREATE TEMPORARY TABLE tmp1 as select rolinherit, rolcreaterole, rolcreatedb, rolcanlogin, rolreplication, rolauditadmin, rolsystemadmin, rolkind from pg_authid where rolname = lower(TEMPUserName);
		
		CREATE TEMPORARY TABLE tmp2 as select rolinherit, rolcreaterole, rolcreatedb, rolcanlogin, rolreplication, rolauditadmin, rolsystemadmin, rolkind from pg_authid where rolname = lower(userName);
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create user phase 3 ok';
        rt:= 1;		
        rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;
	
^^^separator^^^

CREATE OR REPLACE procedure check_grantUser(answer nvarchar2, userName nvarchar2, initSQL nvarchar2, OUT rt integer)
    as
	declare TEMPUserName nvarchar2(1000);
	declare  error_message varchar(1000);
	declare  exception_context  varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--判断是否为grant语句
		if lower(left(answer,5)) != 'grant'
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check grant user phase 1 ok';
		
		--将作答用户用户名修改
		TEMPUserName = userName||'_'||pg_current_sessid();
		
		--预处理，创建相应的用户
		initSQL:=replace(initSQL,userName,TEMPUserName);
		
		EXECUTE initSQL;
		
		if not exists(select * from pg_authid where rolname = lower(TEMPUserName))
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check grant user phase 2 ok';
		
		--替换作答语句中的用户名
		answer:=replacefirst(answer,userName,TEMPUserName);
		EXECUTE answer;
		
		CREATE TEMPORARY TABLE tmp1 as select a.rolname as rolename, b.rolname as membername from pg_auth_members, pg_authid as a, pg_authid as b where a.oid = roleid and b.oid = member and a.rolname = lower(TEMPUserName);
		
		CREATE TEMPORARY TABLE tmp2 as select a.rolname as rolename, b.rolname as membername from pg_auth_members, pg_authid as a, pg_authid as b where a.oid = roleid and b.oid = member and a.rolname = lower(userName);
		
		--设置，使tmp1和tmp2表中授予用户名相同
		UPDATE tmp1 set rolename = lower(userName) where rolename = lower(TEMPUserName);
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check grant user phase 3 ok';
        rt:= 1;		
        rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;

^^^separator^^^

CREATE OR REPLACE procedure check_revokeUser(answer nvarchar2, userName nvarchar2, initSQL nvarchar2, OUT rt integer)
    as
	declare TEMPUserName nvarchar2(1000);
	declare  error_message varchar(1000);
	declare  exception_context  varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--判断是否为revoke语句
		if lower(left(answer,6)) != 'revoke'
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check revoke user phase 1 ok';
		
		--将作答用户用户名修改
		TEMPUserName = userName||'_'||pg_current_sessid();
		
		--预处理，创建相应的用户并授权,这里beforeSQL有两条，第一条是创建语句，第二条是授权语句
		
		initSQL:=replace(initSQL,userName,TEMPUserName);
		EXECUTE initSQL;
		
		if not exists(select * from pg_authid where rolname = lower(TEMPUserName))
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise info 'check revoke user phase 2 ok';
		
		--替换作答语句中的用户名
		answer:=replacefirst(answer,userName,TEMPUserName);
		EXECUTE answer;
		
		CREATE TEMPORARY TABLE tmp1 as select a.rolname as rolename, b.rolname as membername from pg_auth_members, pg_authid as a, pg_authid as b where a.oid = roleid and b.oid = member and a.rolname = lower(TEMPUserName);
		
		CREATE TEMPORARY TABLE tmp2 as select a.rolname as rolename, b.rolname as membername from pg_auth_members, pg_authid as a, pg_authid as b where a.oid = roleid and b.oid = member and a.rolname = lower(userName);
		
		--设置，使tmp1和tmp2表中授予用户名相同
		UPDATE tmp1 set rolename = lower(userName) where rolename = lower(TEMPUserName);
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check revoke user phase 3 ok';
        rt:= 1;		
        rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;

^^^separator^^^

CREATE OR REPLACE procedure check_grantTable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)
    as
	declare  correctTableOid INTEGER;
	declare  TEMPTableOid INTEGER;
	declare  error_message varchar(1000);
	declare  exception_context varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--判断是否为grant语句
		if lower(left(answer,5)) != 'grant'
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check grant table phase 1 ok';
		
		--创建目标表相应的临时表
		initSQL:=regexp_replace(initSQL,'(create([ ]|chr(9)|chr(10)|chr(13))+?)table',E'\\1TEMPORARY table','ig');
		
		EXECUTE initSQL;
		
		if not exists(select * from pg_class where pg_class.relname = lower(tableName) and pg_class.relnamespace = pg_my_temp_schema())
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check grant table phase 2 ok';
		--获取目标表和临时表oid
		EXECUTE IMMEDIATE 'select oid from pg_class where relname = :1 and relnamespace = pg_my_temp_schema()' USING IN lower(tableName), OUT TEMPTableOid;
		
		EXECUTE IMMEDIATE 'select pg_class.oid from pg_class, pg_namespace where pg_class.relname = :1 and pg_class.relnamespace = pg_namespace.oid and pg_namespace.nspname = current_schema()' USING IN lower(tableName), OUT correctTableOid;
		
		EXECUTE answer;
		
		--判断临时表和数据表的权限是否相同
		CREATE TEMPORARY TABLE tmp1 as select relname, relacl from pg_class where pg_class.oid = TEMPTableOid;
		
		CREATE TEMPORARY TABLE tmp2 as select relname, relacl from pg_class where pg_class.oid = correctTableOid;
		
		--去除临时表关于创建用户的权限
		update tmp1 set relacl = array_remove(relacl, relacl[1]);
		update tmp2 set relacl = array_remove(relacl, relacl[1]);
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check grant table phase 3 ok';
		
		--判断列的权限是否相同
		CREATE TEMPORARY TABLE tmp1 as select attname, attacl from pg_attribute where attrelid = TEMPTableOid and attnum > 0 and attisdropped = false;
		
		CREATE TEMPORARY TABLE tmp2 as select attname, attacl from pg_attribute where attrelid = correctTableOid and attnum > 0 and attisdropped = false;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check grant table phase 4 ok';
        rt:= 1;		
        rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;

^^^separator^^^

CREATE OR REPLACE procedure check_revokeTable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)
    as
	declare  correctTableOid INTEGER;
	declare  TEMPTableOid INTEGER;
	declare  error_message varchar(1000);
	declare  exception_context varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--判断是否为revoke语句
		if lower(left(answer,6)) != 'revoke'
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check revoke table phase 1 ok';
		
		--创建目标表相应的临时表并执行相应的授权操作
		initSQL:=regexp_replace(initSQL,'(create([ ]|chr(9)|chr(10)|chr(13))+?)table',E'\\1TEMPORARY table','ig');
		
		EXECUTE initSQL;
		
		if not exists(select * from pg_class where pg_class.relname = lower(tableName) and pg_class.relnamespace = pg_my_temp_schema())
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check revoke table phase 2 ok';
		
		EXECUTE IMMEDIATE 'select oid from pg_class where relname = :1 and relnamespace = pg_my_temp_schema()' USING IN lower(tableName), OUT TEMPTableOid;
		
		EXECUTE IMMEDIATE 'select pg_class.oid from pg_class, pg_namespace where pg_class.relname = :1 and pg_class.relnamespace = pg_namespace.oid and pg_namespace.nspname = current_schema()' USING IN lower(tableName), OUT correctTableOid;
		
		EXECUTE answer;
		
		--判断临时表和数据表的权限是否相同
		CREATE TEMPORARY TABLE tmp1 as select relname, relacl from pg_class where pg_class.oid = TEMPTableOid;
		
		CREATE TEMPORARY TABLE tmp2 as select relname, relacl from pg_class where pg_class.oid = correctTableOid;
		
		--去除临时表关于创建用户的权限
		update tmp1 set relacl = array_remove(relacl, relacl[1]);
		update tmp2 set relacl = array_remove(relacl, relacl[1]);
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check revoke table phase 3 ok';
		
		--判断列的权限是否相同
		CREATE TEMPORARY TABLE tmp1 as select attname, attacl from pg_attribute where attrelid = TEMPTableOid and attnum > 0 and attisdropped = false;
		
		CREATE TEMPORARY TABLE tmp2 as select attname, attacl from pg_attribute where attrelid = correctTableOid and attnum > 0 and attisdropped = false;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		drop table tmp1;
		drop table tmp2;
		drop table tmp3;
		drop table tmp4;
		
		raise notice 'check revoke table phase 4 ok';
        rt:= 1;		
        rollback;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;

^^^separator^^^

create table test_case(caseId serial, questionId int, test_case text);

^^^separator^^^

create or replace procedure execute_user_SQL(answer nvarchar2(1000)) AS
		DECLARE 
			PRAGMA AUTONOMOUS_TRANSACTION;
		BEGIN
			EXECUTE answer;
		END;
		
^^^separator^^^

CREATE OR REPLACE procedure check_func_answer(correctFuncName nvarchar2, TEMPFuncName nvarchar2, inputArg text, out rt integer)
AS
DECLARE
    TYPE decl IS REF CURSOR;
	correctCur decl;
	TEMPCur decl;
	correctResult correctCur%ROWTYPE;
	TEMPResult TEMPCur%ROWTYPE;
BEGIN
    OPEN correctCur FOR 'call '||correctFuncName||'('||inputArg||');';
	OPEN TEMPCur FOR 'call '||TEMPFuncName||'('||inputArg||');';
	
	rt := 0;
	LOOP 
	    FETCH correctCur INTO correctResult;
		FETCH TEMPCur INTO TEMPResult;
		IF correctCur%NOTFOUND AND TEMPCur%NOTFOUND THEN
		    rt := 1;
			EXIT;
		ELSIF NOT (correctCur%FOUND and TEMPCur%FOUND) THEN
		    rt := 0;
			EXIT;
		END IF;
		
		IF correctResult != TEMPResult THEN
		    rt := 0;
			EXIT;
		END IF;
		
	END LOOP;
	CLOSE correctCur;
	CLOSE TEMPCur;
END;

^^^separator^^^

CREATE OR REPLACE PROCEDURE check_createfunc(answer nvarchar2, funcName nvarchar2, questId integer, OUT rt integer)
AS 	declare  TEMPFuncName nvarchar2(1000);
	declare  error_message varchar(1000);
	declare  exception_context varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--判断是否为create function或create procedure语句
		if not regexp_like(answer, '^create([ ]|chr(9)|chr(10)|chr(13))+?(function|procedure)', 'i')
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create func phase 1 ok';
		
		if lower(trim(substring(answer from '%(((p|P)(r|R)(o|O)(c|C)(e|E)(d|D)(u|U)(r|R)(e|E))|((f|F)(u|U)(n|N)(c|C)(t|T)(i|I)(o|O)(n|N)))#"%#"[(]%[)]%((a|A)(s|S))%' for '#'))) != lower(funcName)
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create func phase 2 ok';
		
		--修改存储过程/函数名称
		TEMPFuncName := funcName||'_'||pg_current_sessid();
		answer:= replacefirst(answer, funcName, TEMPFuncName);
		
		--执行作答答案
		execute_user_SQL(answer);
		
		if not exists(select oid from pg_proc where proname = TEMPFuncName)
		then
		    rt:= 0;
			rollback;
			EXECUTE 'drop function IF EXISTS '||TEMPFuncName;
			return;
		end if;
		
		raise notice 'check create func phase 3 ok';
		
		--比对临时func和func元数据
		
		CREATE TEMPORARY TABLE tmp1 as select prolang, provariadic, proisagg, proiswindow, prosecdef, proleakproof, proisstrict, proretset, pronargs, prorettype, proargtypes, proargmodes, proargnames, prokind, propackage from pg_proc where proname = TEMPFuncName;
		
		CREATE TEMPORARY TABLE tmp2 as select prolang, provariadic, proisagg, proiswindow, prosecdef, proleakproof, proisstrict, proretset, pronargs, prorettype, proargtypes, proargmodes, proargnames, prokind, propackage from pg_proc where proname = funcName;
		
		CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;
		
		if exists(select * from tmp3) or exists(select * from tmp4)
		then
			rt:= 0;
			rollback;
			EXECUTE 'drop function IF EXISTS '||TEMPFuncName;
			return;
		end if;
		raise notice 'check create func phase 4 ok';
		
		IF exists(select pronargs from pg_proc where proname = funcName and pronargs = 0) then
		    EXECUTE IMMEDIATE 'call check_func_answer(:1,:2,:3,:4)' USING IN funcName, IN TEMPFuncName, IN NULL, IN OUT rt;
		END IF;
		
		FOR test_case IN select test_case from test_case where questionId = questId
		LOOP
		    EXECUTE IMMEDIATE 'call check_func_answer(:1,:2,:3,:4)' USING IN funcName, IN TEMPFuncName, IN test_case.test_case, IN OUT rt;
			IF rt = 0 THEN
			    EXIT;
			END IF;
		END LOOP;
		
		raise notice 'check create func phase 5 ok';
		rollback;
		EXECUTE 'drop function IF EXISTS '||TEMPFuncName;
		--出错处理
		exception
	        when others then
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
		    error_message := exception_context || ' error ' || sqlerrm;
			rollback;
			EXECUTE 'drop function IF EXISTS '||TEMPFuncName;
		    insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:= 0;
	end;

^^^separator^^^