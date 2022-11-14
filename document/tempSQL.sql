--systemmanager数据库相关操作
CREATE USER tteacher1 with SYSADMIN PASSWORD 'tea_teacher1_123';

insert into sysmanager.sysuser(id, pwd, userclass) values ('teacher1', '12345678', 2);

insert into sysmanager.teacher(id, name, username, password) values('teacher1','教师1','tteacher1','tea_teacher1_123');

insert into sysmanager.stuclass(id, teacherid, semester) values ('班级1','teacher1','2022年秋季');

insert into sysmanager.exercise(teacherid, name, describe, ispublic, deleteflag) values ('teacher1','试卷1','模板试卷，用于能力评估', false, false);

insert into sysmanager.exerinclass(stuclassid,exerciseid,istest,starttime,endtime) values ('班级1',1,false,'2022-09-01 00:00:00','2030-12-31 23:59:59');

create database judgespj OWNER tteacher1;
create database judgexjgl OWNER tteacher1;

insert into sysmanager.database(name,userid,dbclass,useforstu) values ('judgespj','teacher1',2,false),('judgexjgl','teacher1',2,false);
insert into sysmanager.judgedatabase(name, describe) values ('judgespj', '数据库SPJ包含4个表：供应商表S（代码Sno，名称Sname，状态Status，所在城市City），零件表P（代码Pno，名称Pname，颜色Color，重量Weight），工程项目表J（代码Jno，名称Jname，所在城市City），供应情况表SPJ（供应商代码Sno，零件代码Pno，项目代码Jno，数量Qty）'),('judgexjgl', '数据库XJGL包含3个表：学生表Student(学号Sno，姓名Sname，性别Ssex，年龄Sage，系别Sdept),课程表Course(课程号Cno，课程名称Cname，先修课Cpno，学分Ccredit),成绩表SC(学号Sno,课程号Cno，成绩Grade)');

COPY sysmanager.question (questionclass, content, answer, dbname, title, analysis, deleteflag, teacherid, targetname, initsql) FROM stdin;
1	查询年龄不大于19的“女”生的学号和姓名	select sno,sname from Student where not(sage>19) and ssex='女'	judgexjgl	查询年龄不大于19的“女”生的学号和姓名	\N	f	teacher1	\N	\N
2	将一个新学生元组（学号：200215228；姓名：陈冬；性别：男；年龄：18岁;所在系：IS）插入到Student表中	insert into Student  values('200215228','陈冬','男',18,'IS')	judgexjgl	向Student表中插入数据	\N	f	teacher1	Student	\N
2	删除学号为“200215122”的学生的选课记录	delete from Sc where Sno='200215122'	judgexjgl	删除选课记录	\N	f	teacher1	Sc	\N
2	将所有学生的年龄增加一岁	update  Student set Sage=Sage+1	judgexjgl	修改学生表	\N	f	teacher1	Student	\N
1	找出工程项目“J2”使用的各种零件号码及其数量总和（取一个别名）	SELECT Pno,SUM(Qty) Num  FROM SPJ WHERE Jno='J2'  GROUP BY Pno	judgespj	找出工程项目“J2”使用的各种零件号码及其数量总和（取一个别名）	字符串常量使用单引号，求和使用集函数SUM，分组使用GROUP BY。	f	teacher1	\N	\N
1	找出没有使用“天津”供应商供应“红”色零件的工程号码	SELECT Jno  FROM J  WHERE Jno NOT IN   \r\n(SELECT Jno  FROM SPJ, S,P\r\nWHERE SPJ.Sno = S.Sno and spj.pno=p.pno and color='红' AND S.City = '天津')	judgespj	找出没有使用“天津”供应商供应“红”色零件的工程号码	所有工程号码来自于表J而非SPJ，去掉使用了天津供应商供应红色零件的工程号码。	f	teacher1	\N	\N
1	求至少用了供应商“S1”所供应的全部零件的工程号	SELECT JNO\r\nFROM SPJ\r\nWHERE SNO='S1'\r\nGROUP BY JNO\r\nHAVING COUNT(DISTINCT PNO)=\r\n(\r\n\tSELECT COUNT(DISTINCT PNO)\r\n\tFROM SPJ\r\n\tWHERE SNO = 'S1'\r\n);	judgespj	求至少用了供应商“S1”所供应的全部零件的工程号	两种实现思路：使用NOT EXISTS双重否定，使用GROUP BY和HAVING。另外注意，如果供应商S1供应P1和P2，那么使用了S1供应P1和P2的工程才符合条件，不能挑选使用了P1和P2的工程号，因为有可能P1或P2不是S1供应的。例如，J1使用了S1供应的P1，但是使用了S2供应的P2，那么J1并不符合条件。	f	teacher1	\N	\N
1	查询“CS”系年龄大于本系平均年龄的学生的学号、姓名和年龄	select sno,sname,sage\r\nfrom student\r\nwhere Sdept='CS' and Sage > (select AVG(sage) from student where Sdept='CS')	judgexjgl	查询“CS”系年龄大于本系平均年龄的学生的学号、姓名和年龄	平均使用集函数AVG。	f	teacher1	\N	\N
1	查询至少选修了学号为“200215121”的学生所选全部课程的学生学号	SELECT DISTINCT Sno FROM SC u   WHERE NOT EXISTS  (SELECT * FROM SC v  WHERE v.Sno = '200215121' AND NOT EXISTS  (SELECT * FROM SC w WHERE w.Sno=u.Sno AND w.Cno=v.Cno))	judgexjgl	查询至少选修了学号为“200215121”的学生所选全部课程的学生学号	两种实现思路：使用NOT EXISTS双重否定，使用GROUP BY和HAVING。	f	teacher1	\N	\N
3	创建供应情况表SPJ，表名和列名要求使用题目中的英文，定义主码约束和全部外码约束，已有供应商表S，零件表P，工程项目表J，列Sno，Pno，Jno的类型均为char(5)，qty类型为int，主码约束为(Sno,Pno,Jno)，外码约束分别为：Sno对应S(Sno)，Pno对应P(Pno)，Jno对应J(Jno)	create table spj\n(\n  sno char(5),\n  pno char(5),\n  jno char(5),\n  qty int,\n  PRIMARY KEY (sno,pno,jno),  \n  FOREIGN KEY (sno) REFERENCES s(sno),\n  FOREIGN KEY (pno) REFERENCES p(pno),\n  FOREIGN KEY (jno) REFERENCES j(jno)\n);	judgespj	创建供应情况表SPJ	\N	f	teacher1	SPJ	create table j (\n    jno character(5) NOT NULL,\n    jname character(20),\n    city character(20),\n    PRIMARY KEY (jno)\n);\n\ncreate table p (\n    pno character(5) NOT NULL,\n    pname character(20),\n    color character(10),\n    weight integer,\n    PRIMARY KEY (pno)\n);\n\ncreate table s (\n    sno character(5) NOT NULL,\n    sname character(20),\n    status integer,\n    city character(20),\n    PRIMARY KEY (sno)\n);
1	查询所有供应商的名称和所在城市	select Sname,City from S	judgespj	查询所有供应商的名称和所在城市	test	f	teacher1	\N	\N
1	找出重量大于14的零件的名称,颜色,重量	select Pname,Color,Weight from P where weight>14	judgespj	找出重量大于14的零件的名称,颜色,重量	无	f	teacher1	\N	\N
1	找出使用“天津”供应商供应零件的工程号码	SELECT Jno  FROM SPJ, S  WHERE SPJ.Sno = S.Sno AND S.City = '天津'	judgespj	找出使用“天津”供应商供应零件的工程号码	test	f	teacher1	\N	\N
4	供应情况表SPJ1和供应情况表SPJ结构相同，请将SPJ1表中qty列数据类型由int改为BIGINT	alter table SPJ1 MODIFY qty BIGINT;	judgespj	修改SPJ1表qty列类型	\N	f	teacher1	spj1	create table spj1\n(\n  sno char(5),\n  pno char(5),\n  jno char(5),\n  qty int,\n  PRIMARY KEY (sno,pno,jno)  \n);
5	创建视图View_J1，找出Jno为J1的工程项目使用的零件代码Pno，供应商代码Sno，供应数量Qty	create view View_J1 AS \nSELECT Pno, Sno, Qty\nFROM SPJ\nwhere Jno='J1'	judgespj	创建视图View_J1	\N	f	teacher1	view_j1	\N
6	在供应商表S上按城市City升序建立索引Index_City	create index Index_City ON S(City ASC);	judgespj	建立索引Index_City	\N	f	teacher1	index_city	create table s (\n    sno character(5) NOT NULL,\n    sname character(20),\n    status integer,\n    city character(20),\n    PRIMARY KEY (sno)\n)
7	创建用户U1，登录密码为U_123456	create user U1 PASSWORD 'U_123456';	judgespj	创建用户U1，登录密码为U_123456	\N	f	teacher1	U1	\N
7	创建角色R1，登录密码为R_123456	CREATE ROLE R1 PASSWORD 'R_123456';	judgespj	创建角色R1，登录密码为R_123456	\N	f	teacher1	R1	\N
8	将供应情况表SPJ1的SELECT权限授予给用户U1	GRANT SELECT ON TABLE SPJ1 TO U1;	judgespj	将供应情况表SPJ1的SELECT权限授予给用户U1	\N	f	teacher1	spj1	create table spj1\n(\n  sno char(5),\n  pno char(5),\n  jno char(5),\n  qty int,\n  PRIMARY KEY (sno,pno,jno)  \n);
9	将角色R1拥有的供应情况表SPJ1的UPDATE权限收回	REVOKE UPDATE ON TABLE SPJ1 FROM R1;	judgespj	将角色R1拥有的供应情况表SPJ1的UPDATE权限收回	\N	f	teacher1	spj1	create table spj1\n(\n  sno char(5),\n  pno char(5),\n  jno char(5),\n  qty int,\n  PRIMARY KEY (sno,pno,jno)  \n); GRANT SELECT ON SPJ1 TO U1; GRANT UPDATE ON SPJ1 TO R1;
12	创建函数get_student_count，返回student学生表的人数信息。注意：函数名必须为get_student_count	create function get_student_count() return INTEGER\nAS\ndeclare ret INTEGER;\nBEGIN\n    EXECUTE IMMEDIATE 'select count(*) from student' USING out ret;\n    RETURN ret;\nend;	judgexjgl	创建函数get_student_count	\N	f	teacher1	get_student_count	\N
\.
;

insert into sysmanager.questinexer(exerciseid, questionid, orderid, score) values (1,1,1,10),(1,2,2,10),(1,3,3,10),(1,4,4,10),(1,5,5,10),(1,6,6,10),(1,7,7,10),(1,8,8,10),(1,9,9,10),(1,10,10,10),(1,11,11,10),(1,12,12,10),(1,13,13,10),(1,14,14,10),(1,15,15,10),(1,16,16,10),(1,17,17,10),(1,18,18,10),(1,19,19,10),(1,20,20,10),(1,21,21,10);

insert into sysmanager.sysuser(id, pwd, userclass) values('21031211001','12345678',3),('21031211002','12345678',3),('21031211003','12345678',3),('21031211004','12345678',3),('21031211005','12345678',3);

insert into sysmanager.student(id, name, grade, classid, dbname, username, password, isactive) values ('21031211001','学生1','2021级','班级1','practicedb1','s21031211001','stu_21031211001_123',true),('21031211002','学生2','2021级','班级1','practicedb1','s21031211002','stu_21031211002_123',true),('21031211003','学生3','2021级','班级1','practicedb1','s21031211003','stu_21031211003_123',true),('21031211004','学生4','2021级','班级1','practicedb1','s21031211004','stu_21031211004_123',true),('21031211005','学生5','2021级','班级1','practicedb1','s21031211005','stu_21031211005_123',true);

\c practicedb1
create user s21031211001 identified by 'stu_21031211001_123';
create user s21031211002 identified by 'stu_21031211002_123';
create user s21031211003 identified by 'stu_21031211003_123';
create user s21031211004 identified by 'stu_21031211004_123';
create user s21031211005 identified by 'stu_21031211005_123';

\c judgespj

--
-- openGauss database dump
--

SET statement_timeout = 0;
SET xmloption = content;
SET client_encoding = 'SQL_ASCII';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: u1; Type: SCHEMA; Schema: -; Owner: u1
--

SET search_path = public;

--
-- Name: check_altertable(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_altertable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctTableOid INTEGER;
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
/


ALTER PROCEDURE public.check_altertable(answer nvarchar2, tablename nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createfunc(nvarchar2, nvarchar2, integer); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createfunc(answer nvarchar2, funcName nvarchar2, questId integer, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  TEMPFuncName nvarchar2(1000);
	declare  error_message varchar(1000);
	declare  exception_context varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--判断是否为create function或create procedure语句
		if left(answer,15) != 'create function' and left(answer,16) != 'create procedure'
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create func phase 1 ok';
		
		if lower(trim(substring(answer from '%(procedure|function)#"%#"[(]%(begin|BEGIN)%' for '#'))) != lower(funcName)
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
/


ALTER PROCEDURE public.check_createfunc(answer nvarchar2, funcname nvarchar2, questid integer, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createindex(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createindex(answer nvarchar2, indexName nvarchar2,initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctIndexOid INTEGER;
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
/


ALTER PROCEDURE public.check_createindex(answer nvarchar2, indexname nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createtable(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createtable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctTableOid INTEGER;
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
/


ALTER PROCEDURE public.check_createtable(answer nvarchar2, tablename nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createuser(nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createuser(answer nvarchar2, userName nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare TEMPUserName nvarchar2(1000);
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
/


ALTER PROCEDURE public.check_createuser(answer nvarchar2, username nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createview(nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createview(answer nvarchar2, viewName nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctViewOid INTEGER;
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
/


ALTER PROCEDURE public.check_createview(answer nvarchar2, viewname nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_func_answer(nvarchar2, nvarchar2, text); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_func_answer(correctFuncName nvarchar2, TEMPFuncName nvarchar2, inputArg text, out rt integer)  NOT SHIPPABLE
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
/


ALTER PROCEDURE public.check_func_answer(correctfuncname nvarchar2, tempfuncname nvarchar2, inputarg text, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_granttable(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_granttable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctTableOid INTEGER;
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
/


ALTER PROCEDURE public.check_granttable(answer nvarchar2, tablename nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_grantuser(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_grantuser(answer nvarchar2, userName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare TEMPUserName nvarchar2(1000);
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
/


ALTER PROCEDURE public.check_grantuser(answer nvarchar2, username nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_other(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_other(answer nvarchar2, correct nvarchar2, tableName nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	DECLARE str1 NVARCHAR2(1000);
	declare str nvarchar2(1000);
	DECLARE check_answer nvarchar2(1000);
	DECLARE typen nvarchar2(6);
	DECLARE i int;
	DECLARE j int;
	declare error_message varchar(1000);
  declare exception_context varchar(1000);
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
		commit;
		rt:= 0;
	end if;
		
	--执行，判断语法方面是否存在问题
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
/


ALTER PROCEDURE public.check_other(answer nvarchar2, correct nvarchar2, tablename nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_revoketable(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_revoketable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctTableOid INTEGER;
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
/


ALTER PROCEDURE public.check_revoketable(answer nvarchar2, tablename nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_revokeuser(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_revokeuser(answer nvarchar2, userName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare TEMPUserName nvarchar2(1000);
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
/


ALTER PROCEDURE public.check_revokeuser(answer nvarchar2, username nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_select(nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_select(answer nvarchar2, correct nvarchar2, OUT rt integer)  NOT SHIPPABLE
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
    		--执行，判断语法是否存在问题
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
/


ALTER PROCEDURE public.check_select(answer nvarchar2, correct nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: execute_user_sql(nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE execute_user_sql(answer nvarchar2(1000))  NOT SHIPPABLE
 AS 	DECLARE 
			PRAGMA AUTONOMOUS_TRANSACTION;
		BEGIN
			EXECUTE answer;
		END;
/


ALTER PROCEDURE public.execute_user_sql(answer nvarchar2) OWNER TO tteacher1;

--
-- Name: replacefirst(nvarchar2, nvarchar2, nvarchar2); Type: FUNCTION; Schema: public; Owner: tteacher1
--

CREATE FUNCTION replacefirst(source nvarchar2, find nvarchar2, repl nvarchar2) RETURNS nvarchar2
    LANGUAGE plpgsql NOT SHIPPABLE
 AS $$
    declare  ResultVar nvarchar2(10000)  ;-- return value  
    declare  pos int               ;   -- find the first position  
BEGIN
    /*
	pos := instr( source,find,1,1);  
    if  pos = 0 then  return  source;   end if ;
    ResultVar := overlay(source  placing repl from  pos for  char_length(find));  
	*/
	ResultVar := regexp_replace(source,find,repl,'i'); 
    return ResultVar;  
END ;$$;


ALTER FUNCTION public.replacefirst(source nvarchar2, find nvarchar2, repl nvarchar2) OWNER TO tteacher1;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: check_error; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE check_error (
    starttime timestamp without time zone,
    endtime timestamp without time zone,
    exceptions text
)
WITH (orientation=row, compression=no);


ALTER TABLE public.check_error OWNER TO tteacher1;

--
-- Name: j; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE j (
    jno character(5) NOT NULL,
    jname character(20),
    city character(20)
)
WITH (orientation=row, compression=no);


ALTER TABLE public.j OWNER TO tteacher1;

--
-- Name: p; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE p (
    pno character(5) NOT NULL,
    pname character(20),
    color character(10),
    weight integer
)
WITH (orientation=row, compression=no);


ALTER TABLE public.p OWNER TO tteacher1;

--
-- Name: s; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE s (
    sno character(5) NOT NULL,
    sname character(20),
    status integer,
    city character(20)
)
WITH (orientation=row, compression=no);


ALTER TABLE public.s OWNER TO tteacher1;

--
-- Name: spj; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE spj (
    sno character(5) NOT NULL,
    pno character(5) NOT NULL,
    jno character(5) NOT NULL,
    qty integer
)
WITH (orientation=row, compression=no);


ALTER TABLE public.spj OWNER TO tteacher1;

--
-- Name: spj1; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE spj1 (
    sno character(5) NOT NULL,
    pno character(5) NOT NULL,
    jno character(5) NOT NULL,
    qty bigint
)
WITH (orientation=row, compression=no);


ALTER TABLE public.spj1 OWNER TO tteacher1;

--
-- Name: test_case; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE test_case (
    caseid integer NOT NULL,
    questionid integer,
    test_case text
)
WITH (orientation=row, compression=no);


ALTER TABLE public.test_case OWNER TO tteacher1;

--
-- Name: view_j1; Type: VIEW; Schema: public; Owner: tteacher1
--

CREATE VIEW view_j1(pno,sno,qty) AS
    SELECT spj.pno, spj.sno, spj.qty FROM spj WHERE (spj.jno = 'J1'::bpchar);


ALTER VIEW public.view_j1 OWNER TO tteacher1;

--
-- Name: test_case_caseid_seq; Type: SEQUENCE; Schema: public; Owner: tteacher1
--

CREATE SEQUENCE test_case_caseid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.test_case_caseid_seq OWNER TO tteacher1;

--
-- Name: test_case_caseid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: tteacher1
--

ALTER SEQUENCE test_case_caseid_seq OWNED BY test_case.caseid;


--
-- Name: caseid; Type: DEFAULT; Schema: public; Owner: tteacher1
--

ALTER TABLE test_case ALTER COLUMN caseid SET DEFAULT nextval('test_case_caseid_seq'::regclass);


--
-- Data for Name: check_error; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY check_error (starttime, endtime, exceptions) FROM stdin;
\.
;

--
-- Data for Name: j; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY j (jno, jname, city) FROM stdin;
J1   	三建              	北京              
J2   	一汽              	长春              
J3   	弹簧厂           	天津              
J4   	造船厂           	天津              
J5   	机车厂           	唐山              
J6   	无线电厂        	常州              
J7   	半导体厂        	南京              
\.
;

--
-- Data for Name: p; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY p (pno, pname, color, weight) FROM stdin;
P1   	螺母              	红       	13
P2   	螺栓              	绿       	17
P3   	螺丝刀           	蓝       	14
P4   	螺丝刀           	红       	15
P5   	凸轮              	蓝       	40
P6   	齿轮              	红       	30
\.
;

--
-- Data for Name: s; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY s (sno, sname, status, city) FROM stdin;
S1   	精益              	20	天津              
S2   	盛锡              	10	北京              
S3   	东方红           	30	北京              
S4   	丰泰盛           	20	天津1             
S5   	为民              	30	上海              
\.
;

--
-- Data for Name: spj; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY spj (sno, pno, jno, qty) FROM stdin;
S1   	P1   	J1   	200
S1   	P1   	J3   	100
S1   	P1   	J4   	700
S1   	P1   	J6   	200
S1   	P2   	J1   	200
S1   	P2   	J2   	100
S2   	P2   	J6   	100
S2   	P3   	J1   	400
S2   	P3   	J5   	100
\.
;

--
-- Data for Name: spj1; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY spj1 (sno, pno, jno, qty) FROM stdin;
\.
;

--
-- Data for Name: test_case; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY test_case (caseid, questionid, test_case) FROM stdin;
\.
;

--
-- Name: j_pkey; Type: CONSTRAINT; Schema: public; Owner: tteacher1; Tablespace: 
--

ALTER TABLE j
    ADD CONSTRAINT j_pkey PRIMARY KEY (jno);


--
-- Name: spj_jno_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tteacher1
--

ALTER TABLE spj
    ADD CONSTRAINT spj_jno_fkey FOREIGN KEY (jno) REFERENCES j(jno);


--
-- Name: p_pkey; Type: CONSTRAINT; Schema: public; Owner: tteacher1; Tablespace: 
--

ALTER TABLE p
    ADD CONSTRAINT p_pkey PRIMARY KEY (pno);


--
-- Name: spj_pno_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tteacher1
--

ALTER TABLE spj
    ADD CONSTRAINT spj_pno_fkey FOREIGN KEY (pno) REFERENCES p(pno);


--
-- Name: s_pkey; Type: CONSTRAINT; Schema: public; Owner: tteacher1; Tablespace: 
--

ALTER TABLE s
    ADD CONSTRAINT s_pkey PRIMARY KEY (sno);


--
-- Name: spj_sno_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tteacher1
--

ALTER TABLE spj
    ADD CONSTRAINT spj_sno_fkey FOREIGN KEY (sno) REFERENCES s(sno);


--
-- Name: index_city; Type: INDEX; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE INDEX index_city ON s USING btree (city) TABLESPACE pg_default;


--
-- Name: spj1_pkey; Type: CONSTRAINT; Schema: public; Owner: tteacher1; Tablespace: 
--

ALTER TABLE spj1
    ADD CONSTRAINT spj1_pkey PRIMARY KEY (sno, pno, jno);


--
-- Name: spj_pkey; Type: CONSTRAINT; Schema: public; Owner: tteacher1; Tablespace: 
--

ALTER TABLE spj
    ADD CONSTRAINT spj_pkey PRIMARY KEY (sno, pno, jno);


--
-- Name: spj1; Type: ACL; Schema: public; Owner: tteacher1
--

REVOKE ALL ON TABLE spj1 FROM PUBLIC;
REVOKE ALL ON TABLE spj1 FROM tteacher1;
GRANT SELECT,INSERT,REFERENCES,DELETE,TRIGGER,TRUNCATE,UPDATE ON TABLE spj1 TO tteacher1;


create user U1 PASSWORD 'U_123456';
CREATE ROLE R1 PASSWORD 'R_123456';
GRANT SELECT ON TABLE spj1 TO u1;
GRANT UPDATE ON SPJ1 TO R1;
REVOKE UPDATE ON TABLE SPJ1 FROM R1;



--
-- openGauss database dump complete
--

\c judgexjgl
--
-- openGauss database dump
--

SET statement_timeout = 0;
SET xmloption = content;
SET client_encoding = 'SQL_ASCII';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public;

--
-- Name: check_altertable(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_altertable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctTableOid INTEGER;
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
/


ALTER PROCEDURE public.check_altertable(answer nvarchar2, tablename nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createfunc(nvarchar2, nvarchar2, integer); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createfunc(answer nvarchar2, funcName nvarchar2, questId integer, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  TEMPFuncName nvarchar2(1000);
	declare  error_message varchar(1000);
	declare  exception_context varchar(1000);
	BEGIN
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		--判断是否为create function或create procedure语句
		if left(answer,15) != 'create function' and left(answer,16) != 'create procedure'
		then
		    rt:= 0;
			rollback;
			return;
		end if;
		
		raise notice 'check create func phase 1 ok';
		
		if lower(trim(substring(answer from '%(procedure|function)#"%#"[(]%(begin|BEGIN)%' for '#'))) != lower(funcName)
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
/


ALTER PROCEDURE public.check_createfunc(answer nvarchar2, funcname nvarchar2, questid integer, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createindex(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createindex(answer nvarchar2, indexName nvarchar2,initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctIndexOid INTEGER;
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
/


ALTER PROCEDURE public.check_createindex(answer nvarchar2, indexname nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createtable(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createtable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctTableOid INTEGER;
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
/


ALTER PROCEDURE public.check_createtable(answer nvarchar2, tablename nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createuser(nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createuser(answer nvarchar2, userName nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare TEMPUserName nvarchar2(1000);
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
/


ALTER PROCEDURE public.check_createuser(answer nvarchar2, username nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_createview(nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_createview(answer nvarchar2, viewName nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctViewOid INTEGER;
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
/


ALTER PROCEDURE public.check_createview(answer nvarchar2, viewname nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_func_answer(nvarchar2, nvarchar2, text); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_func_answer(correctFuncName nvarchar2, TEMPFuncName nvarchar2, inputArg text, out rt integer)  NOT SHIPPABLE
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
/


ALTER PROCEDURE public.check_func_answer(correctfuncname nvarchar2, tempfuncname nvarchar2, inputarg text, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_granttable(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_granttable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctTableOid INTEGER;
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
/


ALTER PROCEDURE public.check_granttable(answer nvarchar2, tablename nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_grantuser(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_grantuser(answer nvarchar2, userName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare TEMPUserName nvarchar2(1000);
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
/


ALTER PROCEDURE public.check_grantuser(answer nvarchar2, username nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_other(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_other(answer nvarchar2, correct nvarchar2, tableName nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	DECLARE str1 NVARCHAR2(1000);
	declare str nvarchar2(1000);
	DECLARE check_answer nvarchar2(1000);
	DECLARE typen nvarchar2(6);
	DECLARE i int;
	DECLARE j int;
	declare error_message varchar(1000);
  declare exception_context varchar(1000);
	begin
		
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
	  
	answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
		
	if left(answer,6) != left(ltrim(correct),6)
	then
		commit;
		rt:= 0;
	end if;
		
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
		
	str:='create TEMPORARY table tmp1 as select * from ' || tablename;        
	EXECUTE str;
	str:='create TEMPORARY table tmp2 as select * from ' || tablename;       
	EXECUTE str;
	
	answer := replace(answer,tablename,'tmp1');
	EXECUTE answer ;
		
	correct:=replace(correct,tablename,'tmp2');
	EXECUTE correct ;

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
		insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		rt:= 0;
		rollback;
	end;
/


ALTER PROCEDURE public.check_other(answer nvarchar2, correct nvarchar2, tablename nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_revoketable(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_revoketable(answer nvarchar2, tableName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare  correctTableOid INTEGER;
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
/


ALTER PROCEDURE public.check_revoketable(answer nvarchar2, tablename nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_revokeuser(nvarchar2, nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_revokeuser(answer nvarchar2, userName nvarchar2, initSQL nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 	declare TEMPUserName nvarchar2(1000);
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
/


ALTER PROCEDURE public.check_revokeuser(answer nvarchar2, username nvarchar2, initsql nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: check_select(nvarchar2, nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE check_select(answer nvarchar2, correct nvarchar2, OUT rt integer)  NOT SHIPPABLE
 AS 
DECLARE  str1 NVARCHAR2(1000);
declare  check_answer nvarchar2(1000);
declare  str nvarchar2(1000);
declare  error_message varchar(1000);
declare  exception_context varchar(1000);
	BEGIN   
		 
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
	
	    answer:=rtrim(ltrim(answer,chr(9)||chr(10)||chr(13)||' '),chr(9)||chr(10)||chr(13)||' ');
	
		if left( answer,6) != left(ltrim( correct),6)
		then	
		   rt:=0;
		ELSE
    		check_answer:=replace( answer,';',' ');
		    str1 := concat(concat('select * from (',check_answer),') a');
		    execute str1;
		
		    execute answer;
		
	      str1 := concat('create TEMPORARY TABLE tmp1 as ',answer);
		    execute str1;

    		str1 := concat('create TEMPORARY TABLE tmp2 as ',correct);
		    execute str1;
		
        str:='select * from tmp1 union  select * from tmp2';
		    execute str;

		    CREATE TEMPORARY TABLE  tmp3 as  select * from tmp1 except select * from tmp2;
		    CREATE TEMPORARY TABLE  tmp4 as  SELECT * from tmp2 except select * from tmp1;

    		if not exists(select * from tmp3) and not exists(select * from tmp4)
		    then
				   rt:= 1;
		    else
				   rt:= 0;
		    end if;		
		end if;
		

		exception 
		   When  others THEN
		    GET STACKED DIAGNOSTICS exception_context:= PG_EXCEPTION_CONTEXT;
			  error_message := exception_context || ' error ' || sqlerrm;
			  insert into check_error(starttime, endtime, exceptions) values (now(), clock_timestamp(), error_message);
		    rt:=0;

	end;
/


ALTER PROCEDURE public.check_select(answer nvarchar2, correct nvarchar2, OUT rt integer) OWNER TO tteacher1;

--
-- Name: execute_user_sql(nvarchar2); Type: PROCEDURE; Schema: public; Owner: tteacher1
--

CREATE PROCEDURE execute_user_sql(answer nvarchar2(1000))  NOT SHIPPABLE
 AS 	DECLARE 
			PRAGMA AUTONOMOUS_TRANSACTION;
		BEGIN
			EXECUTE answer;
		END;
/


ALTER PROCEDURE public.execute_user_sql(answer nvarchar2) OWNER TO tteacher1;

--
-- Name: get_student_count(); Type: FUNCTION; Schema: public; Owner: tteacher1
--

CREATE FUNCTION get_student_count() RETURNS integer
    LANGUAGE plpgsql NOT SHIPPABLE
 AS $$
declare ret INTEGER;
BEGIN
    EXECUTE IMMEDIATE 'select count(*) from student' USING out ret;
    RETURN ret;
end$$;


ALTER FUNCTION public.get_student_count() OWNER TO tteacher1;

--
-- Name: replacefirst(nvarchar2, nvarchar2, nvarchar2); Type: FUNCTION; Schema: public; Owner: tteacher1
--

CREATE FUNCTION replacefirst(source nvarchar2, find nvarchar2, repl nvarchar2) RETURNS nvarchar2
    LANGUAGE plpgsql NOT SHIPPABLE
 AS $$
    declare  ResultVar nvarchar2(10000)  ;-- return value  
    declare  pos int               ;   -- find the first position  
BEGIN
    /*
	pos := instr( source,find,1,1);  
    if  pos = 0 then  return  source;   end if ;
    ResultVar := overlay(source  placing repl from  pos for  char_length(find));  
	*/
	ResultVar := regexp_replace(source,find,repl,'i'); 
    return ResultVar;  
END ;$$;


ALTER FUNCTION public.replacefirst(source nvarchar2, find nvarchar2, repl nvarchar2) OWNER TO tteacher1;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: check_error; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE check_error (
    starttime timestamp without time zone,
    endtime timestamp without time zone,
    exceptions text
)
WITH (orientation=row, compression=no);


ALTER TABLE public.check_error OWNER TO tteacher1;

--
-- Name: course; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE course (
    cno integer NOT NULL,
    cname character(20) NOT NULL,
    cpno integer,
    ccredit integer
)
WITH (orientation=row, compression=no);


ALTER TABLE public.course OWNER TO tteacher1;

--
-- Name: student; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE student (
    sno character(10) NOT NULL,
    sname character(10) NOT NULL,
    ssex character(5),
    sage integer,
    sdept character(10)
)
WITH (orientation=row, compression=no);


ALTER TABLE public.student OWNER TO tteacher1;

--
-- Name: is_student; Type: VIEW; Schema: public; Owner: tteacher1
--

CREATE VIEW is_student(sno,sname,sdept) AS
    SELECT student.sno, student.sname, student.sdept FROM student WHERE (student.sdept = 'CS'::bpchar);


ALTER VIEW public.is_student OWNER TO tteacher1;

--
-- Name: is_student1; Type: VIEW; Schema: public; Owner: tteacher1
--

CREATE VIEW is_student1(sno,sname,sdept) AS
    SELECT student.sno, student.sname, student.sdept FROM student WHERE (student.sdept = 'CS'::bpchar);


ALTER VIEW public.is_student1 OWNER TO tteacher1;

--
-- Name: sc; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE sc (
    sno character(10) NOT NULL,
    cno integer NOT NULL,
    grade integer
)
WITH (orientation=row, compression=no);


ALTER TABLE public.sc OWNER TO tteacher1;

--
-- Name: test_case; Type: TABLE; Schema: public; Owner: tteacher1; Tablespace: 
--

CREATE TABLE test_case (
    caseid integer NOT NULL,
    questionid integer,
    test_case text
)
WITH (orientation=row, compression=no);


ALTER TABLE public.test_case OWNER TO tteacher1;

--
-- Name: test_case_caseid_seq; Type: SEQUENCE; Schema: public; Owner: tteacher1
--

CREATE SEQUENCE test_case_caseid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.test_case_caseid_seq OWNER TO tteacher1;

--
-- Name: test_case_caseid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: tteacher1
--

ALTER SEQUENCE test_case_caseid_seq OWNED BY test_case.caseid;


--
-- Name: caseid; Type: DEFAULT; Schema: public; Owner: tteacher1
--

ALTER TABLE test_case ALTER COLUMN caseid SET DEFAULT nextval('test_case_caseid_seq'::regclass);


--
-- Data for Name: check_error; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY check_error (starttime, endtime, exceptions) FROM stdin;
\.
;

--
-- Data for Name: course; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY course (cno, cname, cpno, ccredit) FROM stdin;
1	数据库           	5	4
2	数学              	\N	2
3	信息系统        	1	4
4	操作系统        	6	3
5	数据结构        	7	4
6	数据处理        	\N	2
7	PASCAL语言        	6	4
\.
;

--
-- Data for Name: sc; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY sc (sno, cno, grade) FROM stdin;
200215121 	1	92
200215122 	1	60
200215123 	1	90
200215121 	2	85
200215122 	2	90
200215121 	3	88
200215122 	3	80
200215124 	4	80
200215125 	6	66
\.
;

--
-- Data for Name: student; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY student (sno, sname, ssex, sage, sdept) FROM stdin;
200215121 	李勇    	男  	21	IS        
200215122 	刘晨    	女  	20	IS        
200215123 	王敏    	女  	19	MA        
200215124 	李四    	\N	\N	IS        
200215125 	王五    	\N	\N	IS        
200215126 	赵六    	男  	18	CS        
200215127 	钱七    	女  	-1700	CS        
200215128 	钱八    	男  	19	CS        
200215129 	钱九    	男  	-10	Cs        
\.
;

--
-- Data for Name: test_case; Type: TABLE DATA; Schema: public; Owner: tteacher1
--

COPY test_case (caseid, questionid, test_case) FROM stdin;
\.
;

--
-- openGauss database dump complete
--


