<!-- 数据库实践模式主界面 -->
<template>
  <el-container>
    <el-aside width="20%" @click.native="rightClickMenuClose">
      <!-- 元数据树 -->
      <el-button
        @click="getInSchemaData"
        class="refresh-button"
        icon="el-icon-refresh-left"
        >刷新</el-button
      >
      <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="label"
        empty-text="暂无可操作数据"
        @node-click="handleTreeNodeClick"
        @node-contextmenu="handleTreeRightClick"
        :default-expanded-keys="defualtArr"
        :default-checked-keys="defualtCheck"
      >
        <!-- 根据不同节点类型，定义不同的图标和右键函数 -->
        <span class="custom-tree-node" slot-scope="{ node }">
          <span class="tree-node-group">
            <!-- 根据元数据类型插入节点自定义图标 -->
            <img
              v-if="node.data.tag === 'table-list'"
              src="@/assets/table-folder.png"
              class="icon-tree-node icon-table-folder"
            />
            <img
              v-else-if="node.data.tag === 'table'"
              src="@/assets/table.png"
              class="icon-tree-node icon-table"
            />
            <img
              v-else-if="node.data.tag === 'view-list'"
              src="@/assets/view-folder.png"
              class="icon-tree-node icon-view-folder"
            />
            <img
              v-else-if="node.data.tag === 'view'"
              src="@/assets/view.png"
              class="icon-tree-node icon-view"
            />
            <img
              v-else-if="node.data.tag === 'function-list'"
              src="@/assets/function-folder.png"
              class="icon-tree-node icon-function-folder"
            />
            <img
              v-else-if="node.data.tag === 'function'"
              src="@/assets/function.png"
              class="icon-tree-node icon-function"
            />
            <img
              v-else-if="node.data.tag === 'columnList'"
              src="@/assets/columns.png"
              class="icon-tree-node icon-function"
            />
            <img
              v-else-if="node.data.tag === 'column'"
              src="@/assets/column.png"
              class="icon-tree-node icon-function"
            />
            <img
              v-else-if="node.data.tag === 'constraintList'"
              src="@/assets/constraint.png"
              class="icon-tree-node icon-function"
            />
            <img
              v-else-if="node.data.tag === 'constraint'"
              src="@/assets/constraint.png"
              class="icon-tree-node icon-function"
            />
            <img
              v-else-if="node.data.tag === 'indexList'"
              src="@/assets/indexes.png"
              class="icon-tree-node icon-function"
            />
            <img
              v-else-if="node.data.tag === 'index'"
              src="@/assets/index.png"
              class="icon-tree-node icon-function"
            />
            {{ node.label }}
          </span>
        </span>
      </el-tree>
      <!-- 用户信息显示 -->
      <el-card class="info-card">
        <div slot="header" class="clearfix">
          <span>当前用户信息</span>
        </div>
        <div v-if="zhujiaoFlag">用户名: {{ zhujiaoName }}</div>
        <div v-else>用户名: {{ this.$store.state.username }}</div>
        <div v-if="dbName">数据库: {{ dbName }}</div>
      </el-card>
      <!-- 元数据树右键菜单 -->
      <div
        :style="{ left: rightClickMenuX + 'px', top: rightClickMenuY + 'px' }"
        v-show="rightClickMenuShow"
        class="right-click-button-group"
      >
        <el-button
          v-show="this.node.data.tag === 'table'"
          class="right-click-menu-button"
          @click="queryTable"
          >查看数据
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'table'"
          class="right-click-menu-button"
          @click="showTableDesign"
          >查看表结构
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'table'"
          class="right-click-menu-button"
          @click="exportTable2Xlsx"
          >导出EXCEL
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'table'"
          class="right-click-menu-button"
          @click="exportTable2CSV"
          >导出CSV
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'table'"
          class="right-click-menu-button"
          @click="importData"
          >导入数据
        </el-button>

        <el-button
          v-show="this.node.data.tag === 'table-list'"
          class="right-click-menu-button"
          @click="createTable"
          >新建表
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'table'"
          class="right-click-menu-button"
          @click="dropTable"
          >删除表
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'view-list'"
          class="right-click-menu-button"
          @click="createView"
          >新建视图
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'view'"
          class="right-click-menu-button"
          @click="queryTable"
          >查看视图
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'view'"
          class="right-click-menu-button"
          @click="showViewDesign"
          >查看视图结构
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'view'"
          class="right-click-menu-button"
          @click="exportTable2Xlsx"
          >导出EXCEL
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'view'"
          class="right-click-menu-button"
          @click="exportTable2CSV"
          >导出CSV
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'view'"
          class="right-click-menu-button"
          @click="dropView"
          >删除视图
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'columnList'"
          class="right-click-menu-button"
          @click="addColumn"
          >添加列
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'column'"
          class="right-click-menu-button"
          @click="alterColumn"
          >修改列
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'column'"
          class="right-click-menu-button"
          @click="dropColumn"
          >删除列</el-button
        >
        <!-- 过程/函数相关 -->
        <el-button
          v-show="this.node.data.tag === 'function-list'"
          class="right-click-menu-button"
          @click="createFunction"
          >创建函数
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'function-list'"
          class="right-click-menu-button"
          @click="createProcedure"
          >创建过程
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'function'"
          class="right-click-menu-button"
          @click="getFunctionDDL"
          >查看源
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'function'"
          class="right-click-menu-button"
          @click="dropFunction"
          >删除过程/函数
        </el-button>
        <!-- 约束相关 -->
        <el-button
          v-show="this.node.data.tag === 'constraintList'"
          class="right-click-menu-button"
          @click="addConstraint"
          >添加约束
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'constraint'"
          class="right-click-menu-button"
          @click="dropConstraint"
          >删除约束
        </el-button>
        <!-- 索引相关 -->
        <el-button
          v-show="this.node.data.tag === 'indexList'"
          class="right-click-menu-button"
          @click="addIndex"
          >添加索引
        </el-button>
        <el-button
          v-show="this.node.data.tag === 'index'"
          class="right-click-menu-button"
          @click="dropIndex"
          >删除索引
        </el-button>
      </div>
    </el-aside>
    <el-main>
      <!-- SQL编辑器组件 -->
      <!-- 通过ref属性绑定子组件，通过this.$refs调用 -->
      <sql-editor
        ref="sqlEditor"
        code-text=""
        :editorButtonShow="true"
        :showSelectDatabase="false"
        @execClick="doExecuteSql"
        @explainFlagClick="explainChange"
        @execSelectedClick="doExecuteSql"
        @explainClick="doExecuteSql"
      ></sql-editor>
      <!-- 结果视图 -->
      <exec-result-view
        :dataList="executeResult"
        :changeEdit="changeEdit"
        @execClick1="doExecuteSql"
        @flagClick="isEdit"
        @tabsClick="tabsClick"
        @tabsChange="changeTabs"
      >
      </exec-result-view>
      <!-- 导入数据对话框 -->
      <el-dialog
        title="导入数据"
        :visible.sync="showImportDialog"
        @close="clearImport"
      >
        <el-upload
          class="upload-demo"
          ref="upload"
          action="#"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :file-list="fileList"
          :auto-upload="false"
          :limit="1"
          :on-change="handleChange"
        >
          <el-button slot="trigger" size="small" type="primary"
            >选取文件</el-button
          >
          <el-button
            style="margin-left: 10px"
            size="small"
            type="success"
            @click="submitUpload"
            >上传</el-button
          >
          <div slot="tip" class="el-upload__tip">
            只能上传csv/xls文件，一次只能上传一个文件！
          </div>
        </el-upload>
      </el-dialog>

      <!-- 表设计弹窗 -->
      <el-dialog
        title="表结构"
        :visible.sync="showTableDesignDialog"
        @close="tableConstraintList = []"
      >
        <div>
          <h1>{{ tableName }}</h1>
        </div>
        <el-table :data="tableDesignData" :fit="true">
          <el-table-column label="列" prop="columnName"></el-table-column>
          <el-table-column label="类型" prop="columnType"></el-table-column>
        </el-table>
        <div>
          <span style="white-space: pre-wrap">{{ message }}</span>
          <span
            v-for="item in tableConstraintList"
            :key="item.id"
            style="white-space: pre-wrap"
            >{{ item.constraint[0].pg_get_constraintdef }}</span
          >
        </div>
      </el-dialog>
      <el-dialog title="视图结构" :visible.sync="showViewDesignDialog">
        <div>
          <h1>{{ tableName }}</h1>
        </div>
        <el-table :data="tableDesignData" :fit="true">
          <el-table-column label="列" prop="columnName"></el-table-column>
          <el-table-column label="类型" prop="columnType"></el-table-column>
        </el-table>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import SqlEditor from "@/components/SqlEditor.vue";
import {
  getSchemaMetadata,
  execSql,
  getUserSchema,
  connectDBStudent,
  connectDBTeacher,
} from "@/api/api";
import { uploadTable, disConnect } from "@/api/api.js";
import ExecResultView from "@/components/ExecResultView.vue";
import { json2Xlsx, JSonToCSV } from "@/util/utils";

export default {
  components: {
    SqlEditor,
    ExecResultView,
  },
  data() {
    return {
      // 用来保存后端接口返回的元数据JSON
      schemaMetaData: {},
      treeData: [],
      defualtArr: [],
      defualtCheck: [],
      selected: "",
      treeProps: {
        children: "children",
        label: "label",
      },
      changeEdit: 0,
      // 表设计弹窗数据
      tableDesignData: [],
      editorButtonShow: true,
      // 编辑器代码字符串
      codeText: "",
      // 编辑器sql执行结果
      executeResult: [],

      // 临时结果，用于表格导出这种不在页面上显示的sql执行
      tempResult: [],
      schemaOid: "",
      rightClickMenuShow: false,
      rightClickMenuX: "",
      rightClickMenuY: "",
      optionData: "",
      node: { parent: {}, key: "", data: { tag: "" } },
      tree: {},
      showImportDialog: false,
      showCreateFunctionDialog: false,
      showFunctionDDLDialog: false,
      showTableDesignDialog: false,
      showViewDesignDialog: false,
      viewDataflag: false,
      // 存储过程创建的文本框
      textCreateFuntion: "",
      textFunctionDDL: "",
      fileList: [],

      // 导入数据时记录表名
      tableName: "",
      editFlag: false,
      tabsClickFlag: false,
      // 教师用户需要记录数据库名称，学生用户不需要
      dbName: sessionStorage.getItem("teacherDbName"),
      //查看表约束
      message: "约束如下:\n",
      tableConstraintList: [],
      explainFlag: false,
      zhujiaoFlag: false,
      zhujiaoName: "",
    };
  },
  mounted() {
    // 连接数据库
    this.connectToDatabase();
    window.addEventListener("beforeunload", (e) => {
      this.beforeunloadHandler(e);
    });
    if (sessionStorage.getItem("selectRole")) {
      this.zhujiaoFlag = true;
      this.zhujiaoName = sessionStorage.getItem("selectRole");
    }
  },
  destroyed() {
    var userID = "";
    if (sessionStorage.getItem("selectRole")) {
      userID = sessionStorage.getItem("selectRole");
    } else {
      userID = this.$store.state.username;
    }
    disConnect({ userId: userID }).then((res) => {
      const { code, result } = res.data;
      if (code === "0000") {
      }
    });

    window.removeEventListener("beforeunload", (e) => {
      this.beforeunloadHandler(e);
    });
  },

  methods: {
    // 树形结构回调函数
    handleTreeNodeClick(data) {
      this.selected = data.label;
      this.rightClickMenuClose();
    },
    //
    beforeunloadHandler(e) {
      var userID = "";
      if (sessionStorage.getItem("selectRole")) {
        userID = sessionStorage.getItem("selectRole");
      } else {
        userID = this.$store.state.username;
      }
      disConnect({ userId: userID }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
        }
      });
    },
    // 获取模式内元数据
    getInSchemaData() {
      var params = new Object();
      if (sessionStorage.getItem("selectRole")) {
        params.userId = this.zhujiaoName;
      } else {
        params.userId = this.$store.state.username;
      }

      params.schemaOid = this.schemaOid;

      getSchemaMetadata(params).then((res) => {
        var { code, result } = res.data;
        if (code === "0000") {
          this.schemaMetaData = result.schemaMetaDataMap;
          this.treeData = this.formatTreeData(result.schemaMetaDataMap);
          this.defualtArr = [];
          for (let item of this.treeData) {
            this.defualtArr.push(item.label);
          }
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    // 学生连接数据库
    connectToDatabase() {
      let params = new Object();
      // 角色为学生，发起学生连接数据库请求
      if (this.$store.state.role === "student") {
        params.studentId = this.$store.state.username;
        // 接口改动，这些参数不再需要
        connectDBStudent(params).then((res) => {
          const { code, result } = res.data;
          if (code === "0000") {
            // 成功连接后自动获取用户模式oid
            this.setUserSchema();
          } else if (code === "1111") {
            this.$alert(result.errmessage, {
              confirmButtonText: "确认",
              callback: (action) => {
                this.$router.push("/choose");
              },
            });
          }
        });
      } else if (this.$store.state.role === "teacher") {
        params.teacherId = this.$store.state.username;
        params.dbName = this.dbName;
        // 教师连接数据库
        if (sessionStorage.getItem("selectRole")) {
          params.zjId = sessionStorage.getItem("selectRole");
        }
        connectDBTeacher(params).then((res) => {
          const { code, message, result } = res.data;
          if (code === "0000") {
            // 成功连接后自动获取用户模式oid
            this.setUserSchema();
          } else if (code === "1111") {
            this.$alert(result.errmessage, {
              confirmButtonText: "确认",
              callback: (action) => {
                this.$router.push("/db-manage");
              },
            });
          }
        });
      }
    },
    // 设置用户模式
    setUserSchema() {
      let params = new Object();
      if (sessionStorage.getItem("selectRole")) {
        params.userId = this.zhujiaoName;
      } else {
        params.userId = this.$store.state.username;
      }
      // 获取用户所有模式
      getUserSchema(params).then((res) => {
        const { code, message, result } = res.data;
        if (code === "0000" && result.userSchemaList.length > 0) {
          for (let item of result.userSchemaList) {
            // 返回显示除了公共模式之外的模式列表，学生除了公共模式之外只有自己的个人模式
            if (item.schemaName == result.currentSchemaName) {
              // 记录待操作模式的oid
              this.schemaOid = item.oid;
            }
          }
          // 获取模式内元数据
          this.getInSchemaData();
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    //编辑判断
    isEdit(arg) {
      this.editFlag = true;
    },
    //执行计划识别
    explainChange() {
      this.explainFlag = true;
    },
    // sql语句执行
    doExecuteSql(arg) {
      // 执行结果数组赋空
      var sql = arg;
      let refresh = false;
      var params = new Object();
      if (sessionStorage.getItem("selectRole")) {
        params.userId = this.zhujiaoName;
      } else {
        params.userId = this.$store.state.username;
      }

      params.code = sql;
      params.isexplain = false;
      if (this.explainFlag == true) {
        params.isexplain = true;
      }
      execSql(params).then((res) => {
        var { code, message, result } = res.data;
        if (code === "0000") {
          if (this.viewDataflag == true) {
            for (let item of result.resultList) {
              this.executeResult.push(item);

              if (item.Isrefresh == "true") {
                refresh = true;
              }
            }
            this.$set(
              this.executeResult[0],
              "activeTabName",
              (this.executeResult.length - 1).toString()
            );
          } else {
            this.executeResult = result.resultList;

            for (let item of this.executeResult) {
              if (item.Isrefresh == "true") {
                refresh = true;
                break;
              }
            }
            if (this.editFlag == true) {
              this.$set(this.executeResult[0], "editFlag", true);
            } else {
              this.$set(this.executeResult[0], "editFlag", false);
            }
            this.$set(this.executeResult[0], "activeTabName", "0");
            this.changeEdit = (this.changeEdit + 1) % 2;
            this.editFlag = false;
          }
          this.viewDataflag = false;
          if (refresh == true) {
            this.getInSchemaData();
          }
        } else {
          this.$alert(result.errmessage, {
            confirmButtonText: "确认",
            callback: (action) => {},
          });
        }
      });
      this.explainFlag = false;
    },
    //过滤数据
    tabFilter: function (tabs, target) {
      let result = [];
      for (let i = 0; i < tabs.length; i++) {
        if (i != target) {
          result.push(tabs[i]);
        }
      }
      return result;
    },
    //tab变化
    tabsClick(index) {},
    //修改tab导航栏
    changeTabs(target) {
      let tabs = this.executeResult;
      let activeName = this.executeResult[0].activeTabName;
      if (target == "all") {
        this.executeResult = [];
        return;
      }
      if (activeName === target) {
        if (target == this.executeResult.length - 1) {
          activeName = parseInt(target) - 1 + "";
        } else {
          activeName = parseInt(target) + "";
        }
      } else if (activeName > target) {
        activeName = activeName - 1 + "";
      }

      this.executeResult[0].activeTabName = activeName;
      this.executeResult = this.tabFilter(tabs, target);
      if (this.executeResult.length != 0) {
        if (!this.executeResult[0].activeTabName) {
          this.executeResult[0].activeTabName = activeName;
        }
      }
    },
    /**
     * 元数据树格式化函数
     * @param {Array} MapJSON 接口响应返回的schemaMetaDataMap对象
     */
    formatTreeData(MapJSON) {
      // 分别用来包装视图、数据表、函数的树节点数据对象
      let viewMeta, tableMeta, functionMeta;

      for (var key in MapJSON) {
        // 视图元数据处理
        if (key === "viewList") {
          viewMeta = { label: "视图", tag: "view-list", children: [] };
          // 遍历视图数组
          const { viewList } = MapJSON;
          for (let i in viewList) {
            // 根据视图名创建新节点
            viewMeta.children[i] = {
              label: viewList[i].viewname,
              tag: "view",
              oid: viewList[i].oid,
              columnList: [],
            };
            // 取出该表、约束列、索引元数据
            const { columnList } = viewList[i];
            for (let j in columnList) {
              // 遍历列数组，创建列节点
              // 视图的列信息不在前端展示，仅在此保存
              viewMeta.children[i].columnList[j] = {
                columnName: columnList[j].columnname,
                columnType: columnList[j].columndisplay,
              };
            }
          }
        }

        // 表元数据处理
        else if (key === "tableList") {
          tableMeta = { label: "数据表", tag: "table-list", children: [] };
          // 遍历表数组
          const { tableList } = MapJSON;
          for (let i in tableList) {
            // 根据表名创建新节点，每个表对应一个子节点
            tableMeta.children[i] = {
              // 下列数据均在this.node.data里面
              label: tableList[i].tablename,
              tag: "table",
              oid: tableList[i].oid,
              children: [
                { label: "列", tag: "columnList", children: [] },
                { label: "约束", tag: "constraintList", children: [] },
                { label: "索引", tag: "indexList", children: [] },
              ],
            };
            // 取出该表的约束、列、索引元数据
            const { constraintList, columnList, indexList } = tableList[i];
            for (let j in columnList) {
              // 遍历列数组，创建列节点
              tableMeta.children[i].children[0].children[j] = {
                label: columnList[j].columnname,
                tag: "column",
                columnName: columnList[j].columnname,
                columnType: columnList[j].columndisplay,
              };
            }
            for (let j in constraintList) {
              // 遍历约束数组，创建约束节点
              tableMeta.children[i].children[1].children[j] = {
                label: constraintList[j].constraintname,
                tag: "constraint",
              };
            }
            for (let j in indexList) {
              // 遍历索引数组，创建索引节点
              tableMeta.children[i].children[2].children[j] = {
                label: indexList[j].indexname,
                tag: "index",
              };
            }
          }
        }

        // 函数元数据处理
        else if (key === "funcList") {
          functionMeta = {
            label: "过程/函数",
            tag: "function-list",
            children: [],
          };
          // 遍历函数数组
          const { funcList } = MapJSON;
          for (let i in funcList) {
            // 根据表名创建新节点
            functionMeta.children[i] = {
              label: funcList[i].funcname,
              tag: "function",
              oid: funcList[i].oid,
            };
          }
        }
      }
      let treeData = [functionMeta, tableMeta, viewMeta];
      return treeData;
    },
    // 元数据右键点击事件
    handleTreeRightClick(e, data, n, t) {
      this.rightClickMenuShow = false;
      this.rightClickMenuX = e.x; // 让右键菜单出现在鼠标右键的位置
      this.rightClickMenuY = e.y;
      this.optionData = data;
      this.node = n; // 将当前节点保存
      this.tree = t;
      this.rightClickMenuShow = true; // 展示右键菜单
      if (data.tag === "table") {
        this.tableName = data.label;
      }
    },
    // 元数据右键菜单关闭
    rightClickMenuClose(event) {
      this.rightClickMenuShow = false;
    },
    // 新建表
    createTable() {
      // 保存已有的编辑器内容
      let oldSql = this.$refs.sqlEditor.editor.getValue();
      // 保存现有的末行编号
      let oldLineCount = this.$refs.sqlEditor.editor?.getModel().getLineCount();
      // 所需ddl拼接到原字符串之前
      let ddl =
        "\n\n" +
        "-- 请根据需求修改如下SQL模板并选择执行\n" +
        "/* CREATE TABLE 表名1(\n" +
        "    列名1 INT, --数值型：INT,DOUBLE,DECIMAL(7,2) 等\n" +
        "    列名2 CHAR(10), --字符型：CHAR(位数),VARCHAR(位数) 等\n" +
        "    列名3 INT DEFAULT 60, -- 默认值\n" +
        "    列名4 VARCHAR(8) NOT NULL, --非空\n" +
        "    列名5 VARCHAR(8) UNIQUE, --唯一\n" +
        "    CHECK (列名3 >= 0 AND 列名3 <= 100), --自定义约束\n" +
        "    PRIMARY KEY (主码列名1), --主码\n" +
        "    FOREIGN KEY (外码列名1) REFERENCES 表名2(码) --外码\n" +
        "); */\n";
      let newSql = oldSql + ddl;
      // 将拼接好的新字符串传给输入框
      this.$refs.sqlEditor.editor.setValue(newSql);
      // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
      this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
    },
    // 导入数据到指定表格
    importData() {
      this.showImportDialog = true;
    },
    // 查询表
    queryTable() {
      // 先关闭右键菜单
      this.rightClickMenuShow = false;
      this.viewDataflag = true;
      let sql = "SELECT * FROM " + this.node.key + ";";
      this.doExecuteSql(sql);
    },
    // 删除表
    dropTable() {
      this.rightClickMenuShow = false;
      let sql = "DROP TABLE " + this.node.key + ";";
      this.doExecuteSql(sql);
    },
    // 导出表为xlsx
    exportTable2Xlsx() {
      // 先执行SELECT语句获取全表数据
      this.rightClickMenuShow = false;
      let sql = "SELECT * FROM " + this.node.key + ";";

      var params = new Object();
      if (sessionStorage.getItem("selectRole")) {
        params.userId = this.zhujiaoName;
      } else {
        params.userId = this.$store.state.username;
      }
      params.code = sql;
      params.isexplain = false;
      execSql(params).then((res) => {
        let { code, result } = res.data;
        if (code === "0000") {
          // 调用JSON转EXCEL方法导出表格
          json2Xlsx(result.resultList[0].rowList, this.node.key);
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    // 导出表为csv
    exportTable2CSV() {
      // 先执行SELECT语句获取全表数据
      this.rightClickMenuShow = false;
      let sql = "SELECT * FROM " + this.node.key + ";";

      var params = new Object();
      if (sessionStorage.getItem("selectRole")) {
        params.userId = this.zhujiaoName;
      } else {
        params.userId = this.$store.state.username;
      }
      params.code = sql;
      params.isexplain = false;
      execSql(params).then((res) => {
        let { code, result } = res.data;
        if (code === "0000") {
          // 用表名作为文件名
          let fileName = this.node.key;
          JSonToCSV.setDataConver({
            data: result.resultList[0].rowList,
            fileName,
            columns: {
              title: [],
              key: [],
              formatter: function (n, v) {
                if (n === "amont" && !isNaN(Number(v))) {
                  v = v + "";
                  v = v.split(".");
                  v[0] = v[0].replace(/(\d)(?=(?:\d{3})+$)/g, "$1,"); // 千分位的设置
                  return v.join(".");
                }
                if (n === "proportion") return v + "%";
              },
            },
          });
        } else {
          this.$message(result.errmessage);
        }
      });
    },

    // 新建视图
    createView() {
      let oldSql = this.$refs.sqlEditor.editor.getValue();
      // 保存现有的末行编号
      let oldLineCount = this.$refs.sqlEditor.editor?.getModel().getLineCount();
      // 所需ddl拼接到原字符串之前
      let ddl =
        "\n\n" +
        "-- 请根据需求修改如下SQL模板并选择执行\n" +
        "/* CREATE OR REPLACE VIEW 视图名1(列名1, 列名2)\n" +
        "    AS SELECT 列名3, 列名4 FROM 表名1; --查询语句 */";
      let newSql = oldSql + ddl;
      // 将拼接好的新字符串传给输入框
      this.$refs.sqlEditor.editor.setValue(newSql);
      // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
      this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
    },

    // 删除视图
    dropView() {
      this.rightClickMenuShow = false;
      let sql = "DROP VIEW " + this.node.key + ";";
      this.doExecuteSql(sql);
    },

    // 增加列
    addColumn() {
      let oldSql = this.$refs.sqlEditor.editor.getValue();
      // 保存现有的末行编号
      let oldLineCount = this.$refs.sqlEditor.editor?.getModel().getLineCount();
      // 所需ddl拼接到原字符串之前
      let ddl =
        "\n\n" +
        "-- 请参考并修改如下SQL模板并选择执行\n" +
        "/* ALTER TABLE 表名1 ADD COLUMN 新列名1 INT default 0 NOT NULL; --增加一个整型，缺省值为0的非空新列 */\n";
      let newSql = oldSql + ddl;
      // 将拼接好的新字符串传给输入框
      this.$refs.sqlEditor.editor.setValue(newSql);
      // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
      this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
    },
    // 删除列
    dropColumn() {
      // 确认点击节点来自表的子级
      if (this.node.parent.parent.data.tag === "table") {
        // 取到所属表名
        var tableName = this.node.parent.parent.data.label;
      }
      // 拼接SQL
      let sql =
        "ALTER TABLE " + tableName + " DROP COLUMN " + this.node.key + ";";
      this.doExecuteSql(sql);
    },
    // 修改列
    alterColumn() {
      let oldSql = this.$refs.sqlEditor.editor.getValue();
      // 保存现有的末行编号
      let oldLineCount = this.$refs.sqlEditor.editor?.getModel().getLineCount();
      // 所需ddl拼接到原字符串之前
      let ddl =
        "\n\n" +
        "-- 请参考并修改如下SQL模板并选择执行\n" +
        "/* ALTER TABLE 表名1 ALTER COLUMN 列名1 TYPE REAL; -- 修改列名1数据类型为REAL单精度浮点数 */\n";
      let newSql = oldSql + ddl;
      // 将拼接好的新字符串传给输入框
      this.$refs.sqlEditor.editor.setValue(newSql);
      // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
      this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
    },

    //创建函数
    createFunction() {
      let oldSql = this.$refs.sqlEditor.editor.getValue();

      // 保存现有的末行编号
      let oldLineCount = this.$refs.sqlEditor.editor?.getModel().getLineCount();

      // 创建函数模板
      let ddl =
        "\n\n" +
        "-- 请根据需求修改如下SQL模板并选择执行\n" +
        "/* CREATE OR REPLACE FUNCTION function_name ( 入参1 integer, 入参2 integer )  --函数名及入参\n\n" +
        "   	RETURNS integer --返回值类型\n" +
        "	    LANGUAGE PLPGSQL\n\n" +
        "   AS  $$\n" +
        "   DECLARE\n\n" +
        "    	-- 声明区\n\n" +
        "   BEGIN\n\n" +
        "    	-- 执行区\n\n" +
        "   END;$$\n" +
        "/\n" +
        "--此处“/”为功能分割标记，请勿删除\n" +
        "*/";

      let newSql = oldSql + ddl;
      // 将拼接好的新字符串传给输入框
      this.$refs.sqlEditor.editor.setValue(newSql);
      // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
      this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
    },

    // 创建过程
    createProcedure() {
      let oldSql = this.$refs.sqlEditor.editor.getValue();
      // 保存现有的末行编号
      let oldLineCount = this.$refs.sqlEditor.editor?.getModel().getLineCount();

      // 创建过程模板
      let ddl =
        "\n\n" +
        "-- 请根据需求修改如下SQL模板并编译\n" +
        "/*CREATE OR REPLACE PROCEDURE procedure_name ( 入参1 integer, 入参2 integer)  --过程名及入参\n\n" +
        "  AS\n" +
        "  DECLARE\n\n" +
        "     -- 声明区\n\n" +
        "  BEGIN\n\n" +
        "     -- 执行区\n\n" +
        "  END;\n" +
        "/\n" +
        "--此处“/”为功能分割标记，请勿删除\n" +
        "*/";

      let newSql = oldSql + ddl;
      // 将拼接好的新字符串传给输入框
      this.$refs.sqlEditor.editor.setValue(newSql);

      // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
      this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
    },

    // 过程/函数编译（就是执行）
    doCompile() {
      this.showCreateFunctionDialog = false;
      var sql = this.textCreateFuntion;
      var params = new Object();
      if (sessionStorage.getItem("selectRole")) {
        params.userId = this.zhujiaoName;
      } else {
        params.userId = this.$store.state.username;
      }
      params.code = sql;
      params.isexplain = false;
      execSql(params).then((res) => {
        var { code, result } = res.data;
        if (code === "0000") {
          this.executeResult = result.resultList;
        } else {
          this.$alert(result.errmessage, {
            confirmButtonText: "确认",
            callback: (action) => {},
          });
        }
      });
    },

    //删除函数
    dropFunction() {
      let sql = "DROP FUNCTION " + this.node.key + ";";
      this.doExecuteSql(sql);
    },

    // 查看函数源
    getFunctionDDL() {
      this.textFunctionDDL = "";
      // 通过系统表查询函数的DDL
      let sql = "SELECT * FROM pg_get_functiondef(" + this.node.data.oid + ");";
      var params = new Object();
      if (sessionStorage.getItem("selectRole")) {
        params.userId = this.zhujiaoName;
      } else {
        params.userId = this.$store.state.username;
      }
      params.code = sql;
      params.isexplain = false;
      execSql(params).then((res) => {
        var { code, result } = res.data;
        if (code === "0000") {
          this.showFunctionDDLDialog = true;
          var ddl = result.resultList[0].rowList[0].definition;
          let oldSql = this.$refs.sqlEditor.editor.getValue();
          // 保存现有的末行编号
          let oldLineCount = this.$refs.sqlEditor.editor
            ?.getModel()
            .getLineCount();

          // 将获取到的DDL展示在sql编辑器
          ddl =
            "\n\n" +
            "/*" +
            ddl +
            "\n" +
            "/\n" +
            "--此处“/”为功能分割标记，请勿删除\n" +
            "*/";
          let newSql = oldSql + ddl;
          // 将拼接好的新字符串传给输入框
          this.$refs.sqlEditor.editor.setValue(newSql);
          // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
          this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
        } else {
          this.$alert(result.errmessage, {
            confirmButtonText: "确认",
            callback: (action) => {},
          });
        }
      });
    },

    // 添加约束
    addConstraint() {
      let oldSql = this.$refs.sqlEditor.editor.getValue();
      // 保存现有的末行编号
      let oldLineCount = this.$refs.sqlEditor.editor?.getModel().getLineCount();
      // 所需ddl拼接到原字符串之前
      let ddl =
        "\n\n" +
        "-- 请参考并修改如下SQL模板并选择执行\n" +
        "/* ALTER TABLE 表名1 ADD CONSTRAINT 约束名1\n" +
        "      CHECK (列名3 >= 0 AND 列名3 <= 100); --自定义约束\n" +
        "      -- 或 PRIMARY KEY (主码列名1) --主码\n" +
        "      -- 或 FOREIGN KEY (外码列名1) REFERENCES 表名2(码) --外码*/\n";
      let newSql = oldSql + ddl;
      // 将拼接好的新字符串传给输入框
      this.$refs.sqlEditor.editor.setValue(newSql);
      // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
      this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
    },

    // 删除约束
    dropConstraint() {
      // 确认点击节点来自表的子级
      if (this.node.parent.parent.data.tag === "table") {
        // 取到所属表名
        var tableName = this.node.parent.parent.data.label;
      }
      // 拼接SQL
      let sql =
        "ALTER TABLE " +
        tableName +
        " DROP CONSTRAINT IF EXISTS " +
        this.node.key +
        ";";
      this.doExecuteSql(sql);
    },

    // 添加索引
    addIndex() {
      let oldSql = this.$refs.sqlEditor.editor.getValue();
      // 保存现有的末行编号
      let oldLineCount = this.$refs.sqlEditor.editor?.getModel().getLineCount();
      // 所需ddl拼接到原字符串之前
      let ddl =
        "\n\n" +
        "-- 请参考并修改如下SQL模板并选择执行\n" +
        "/* CREATE INDEX 索引名1 ON 表名1 (索引列1);\n" +
        "*/\n";
      let newSql = oldSql + ddl;
      // 将拼接好的新字符串传给输入框
      this.$refs.sqlEditor.editor.setValue(newSql);
      // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
      this.$refs.sqlEditor.editor.revealLineInCenter(oldLineCount + 5);
    },

    // 删除索引
    dropIndex() {
      // 拼接SQL
      let sql = "DROP INDEX IF EXISTS " + this.node.key + ";";
      this.doExecuteSql(sql);
    },

    // 查看表设计
    showTableDesign() {
      // 初始化置空
      this.tableDesignData = [];
      this.tableName = this.node.label;

      var constraintList = [];

      for (let i = 0; i < this.schemaMetaData.tableList.length; i++) {
        if (this.schemaMetaData.tableList[i].tablename == this.tableName) {
          constraintList = this.schemaMetaData.tableList[i].constraintList;
          break;
        }
      }

      let sql_all = "";
      for (let item of constraintList) {
        let oid = item.constraintoid;
        let sql = ` select pg_get_constraintdef(${oid});`;
        sql_all = sql_all + sql;
      }
      var params = new Object();
      if (sessionStorage.getItem("selectRole")) {
        params.userId = this.zhujiaoName;
      } else {
        params.userId = this.$store.state.username;
      }
      params.code = sql_all;
      params.isexplain = false;
      execSql(params).then((res) => {
        var { code, message, result } = res.data;

        if (code === "0000") {
          for (let i in result.resultList) {
            result.resultList[i].rowList[0].pg_get_constraintdef =
              result.resultList[i].rowList[0].pg_get_constraintdef + "\n";
            this.tableConstraintList.push({
              id: i,
              constraint: result.resultList[i].rowList,
            });
          }
        } else {
          this.$alert(result.errmessage, {
            confirmButtonText: "确认",
            callback: (action) => {},
          });
        }
      });

      this.showTableDesignDialog = true;
      for (let item of this.node.data.children[0].children) {
        this.tableDesignData.push(item);
      }
    },

    // 查看视图设计
    showViewDesign() {
      // 初始化置空
      this.tableDesignData = [];
      this.tableName = this.node.label;

      this.showViewDesignDialog = true;
      this.tableDesignData = this.node.data.columnList;
    },

    submitUpload() {
      //上传文件
      var fd = new FormData();
      fd.append("file", this.fileList[0].raw);
      fd.append("userId", this.$store.state.username);
      fd.append("tableName", this.tableName);

      uploadTable(fd).then((res) => {
        var { code, result } = res.data;
        if (code === "0000") {
          this.$alert("成功");
        } else {
          this.$alert(result.errmessage);
        }
      });
    },

    handlePreview() {},

    handleRemove() {},
    handleChange(file, fileList) {
      this.fileList = fileList;
    },
    clearImport() {
      this.fileList = [];
    },
  },
};
</script>

<style lang="css" scoped>
.el-container {
  background-color: #eeeeee;
}

.el-container .el-main {
  margin-left: 0.7vw;
  padding: 0;
}

.refresh-button {
  width: 100%;
}

.el-tree {
  /* el-tree高度 = 100% - （刷新按钮高度 + 用户信息卡高度）*/
  height: calc(100% - 200px);
  overflow: auto;
}

/* 深度作用选择器>>> 将样式作用到所有子级及以下的对象上 */
.el-tree-node__label {
  font-size: 20px;
  font-family: "MicroSoft Yahei";
  font-weight: lighter;
}

.right-click-menu-button {
  width: 140px;
  margin-left: 0px;
  font-size: 15px;
  border-radius: 0;
}

.right-click-button-group {
  z-index: 9999;
  position: fixed;
  width: 100px;
  height: auto;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
}

.icon-tree-node {
  height: 100%;
  margin-right: 5px;
}

.tree-node-group {
  display: flex;
  flex-direction: row;
}

.info-card {
  color: grey;
  font-size: 14px;
}

/* 这两个不加deep不起作用，原因未知 */
.el-card__header {
  padding: 3px 20px;
}

.el-card__body {
  padding: 5px 20px;
  bottom: 0;
}

.el-card__body > * {
  padding: 2px 0px;
}
</style>
