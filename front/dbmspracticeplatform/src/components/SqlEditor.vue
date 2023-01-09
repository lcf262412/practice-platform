<!-- SQL语句编辑器组件 -->
<template>
  <div>
    <div class="giao-rows justify-between">
      <div class="margin-bottom-xs">
        <div class="edit-btn-box" v-if="editorButtonShow">
          <el-button
            class="sql-edit-btn"
            type="text"
            icon="el-icon-video-play"
            @click="doExecuteSql"
            >全部执行</el-button
          >
          <el-button
            class="sql-edit-btn"
            type="text"
            icon="el-icon-caret-right"
            @click="doExecuteSelectedSql"
            >执行</el-button
          >
          <el-button
           
            class="sql-edit-btn"
            type="text"
            icon="el-icon-document-add"
            @click="showImportSQLDialog=true"
            >导入SQL文件
          </el-button>
          <el-button
           
            class="sql-edit-btn"
            type="text"
            icon="el-icon-download"
            @click="saveSQLClick"
            >保存为SQL文件
          </el-button>
        
          <el-button
            class="sql-edit-btn"
            type="text"
            icon="el-icon-timer"
            @click="explainSql"
            >执行计划</el-button
          >
          <el-button
            class="sql-edit-btn"
            type="text"
            icon="el-icon-delete"
            @click="cleanSqlContent"
            >清空内容</el-button
          >
          <el-button
            class="sql-edit-btn"
            type="text"
            icon="el-icon-question"
            @click="openHelp"
            >参考手册</el-button
          >
        </div>
        <!-- 导入SQL对话框 -->
      <el-dialog title="导入SQL文件" :visible.sync="showImportSQLDialog" @close="clearImport">
        <el-upload
          class="upload-demo"
          ref="upload"
          action="#"
         
          :file-list="fileListSQL"
          :auto-upload="false"
          :limit="1"
          :on-change="handleChangeSQL"
        >
          <el-button slot="trigger" size="small" type="primary"
            >选取文件</el-button
          >
          <el-button
            style="margin-left: 10px"
            size="small"
            type="success"
            @click="importSQLClick"
            >确定</el-button
          >
          <div slot="tip" class="el-upload__tip">
            只能上传sql文件，一次只能上传一个文件！
          </div>
        </el-upload>
      </el-dialog>
      </div>

      <div>
        <slot name="butRight"></slot>
      </div>
    </div>
    <div id="editor" ref="editor" class="monaco-editor" onblur="editorBlur" />
  </div>
</template>

<script>
// 引入全局实例
import * as monaco from "monaco-editor/esm/vs/editor/editor.api.js";
import { format } from "sql-formatter";
import { checkFileEncoder } from "@/api/api.js";
import saveAs from "file-saver"

// 尝试获取全局实例

export default {
  name: "sqlEditor",
  components: {},
  props: {
    dbNameList: {
      type: Array,
      default: () => [],
    },
    // 外部传入的内容，用于实现双向绑定
    codeText: String,
    // 外部传入的语法类型
    language: {
      type: String,
      default: "sql",
    },
    isImportSql: {
      type: Boolean,
    },
    codeHubTitle: String,
    editorButtonShow: Boolean,
    showSelectDatabase: Boolean,
  },
  data() {
    return {
      code: "",
      // 编辑器实例
      editor: null,
      dbName: "postgres",
      showImportSQLDialog: false,
      fileListSQL:[],
    };
  },
  watch: {
    codeText: {
      immediate: true,
      handler(newVal, oldVal) {
        if (this.editor && newVal != oldVal) {
          this.editor.setValue(newVal.toString());
        }
      },
    },
  },
  mounted() {
    // 初始化
    this.initEditor();
  },
  methods: {
    //选取文件处理
    handleChangeSQL(file, fileList) {
      this.fileListSQL = fileList;
    },
    //保存为SQL文件
    saveSQLClick(){
      var data =  this.editor.getValue();
      let str = new Blob([data], {type: 'text/plain'});
      // 注意这里要手动写上文件的后缀名
      saveAs(str, `导出文件.sql`);
    },
    //点击确认导入
    importSQLClick(){
      let file=this.fileListSQL[0].raw
      const reader=new FileReader()
      var ddl=''
      var encoder=''
      let _that=this
      var fd=new FormData()
      fd.append('file',this.fileListSQL[0].raw)
      
      checkFileEncoder(fd).then((res) => {
        var { code, result } = res.data;
        if (code === "0000") {

          encoder=result['文件编码格式']
          if(result['文件编码格式']=='GB2312'){
            encoder='GBK'
          }
          reader.readAsText(file,encoder)
          reader.onload=function(){
            ddl=this.result
            let oldSql = _that.editor.getValue();
            // 保存现有的末行编号
            let oldLineCount = _that.editor?.getModel().getLineCount();
            // 所需ddl拼接到原字符串之前
            
            let newSql = oldSql + ddl;
            // 将拼接好的新字符串传给输入框
            _that.editor.setValue(newSql);
            // 编辑器聚焦跳转到之前的末行，也就是新插入内容的首行
            _that.editor.revealLineInCenter(oldLineCount + 5);
            
          }
        } else {
          this.$alert(result.errmessage);
        }
      });
      

     
      this.showImportSQLDialog=false
      

    },
    //关闭清空
    clearImport(){
      this.fileListSQL=[]
    },
    editorBlur() {
    },
    // 输出选定语句的查询计划
    explainSql(){
      var sql = this.getSelected();
      if (sql === "") {
        return;
      } else {
      }
      this.$emit('explainFlagClick')
      this.$emit('explainClick', sql);
    },
    cleanSqlContent() {
      this.editor.setValue("");
      this.$emit("cleanCllick");
    },
    getSql() {
      // 通过this.editor.getValue()获取实时内容
      let sql = this.editor.getValue() || "";
      sql = sql.trim();
      if (sql === "") {
        this.$message("请输入执行语句");
      }
      return sql;
    },
    getSelected() {
      let sql = window.getSelection().toString();
      sql = sql.trim();
      if (sql === "") {
        this.$message("请用鼠标划取语句");
      }
      return sql;
    },
    doSaveAsSql() {
      const sql = this.getSql();
      if (sql === "") {
        return;
      }
      this.$emit("saveAsClick", sql);
    },
    doSaveSql() {
      const sql = this.getSql();
      if (sql === "") {
        return;
      }
      this.$emit("saveClick", sql);
    },
    // 全部执行
    doExecuteSql() {
      const sql = this.getSql();
      if (sql === "") {
        return;
      }
     
      this.$emit("execClick", sql);
    },
    // 选择执行
    doExecuteSelectedSql() {
      const sql = this.getSelected();
      if (sql === "") {
        return;
      }
      this.$emit("execSelectedClick", sql);
    },
    // MonacoEditor初始化
    initEditor() {
      let that = this;
      monaco.editor.defineTheme("custom", {
        base: "vs",
        inherit: true,
        rules: [{}],
        colors: {
          // 相关颜色属性配置
          "editor.background": "#ffffff", //背景色
          "editorCursor.foreground": "#8B0000", //光标颜色
          "editor.lineHighlightBackground": "#A5F39E", //高亮当前行颜色
          "editorGutter.background": "#c6c6c6", //侧栏（行号区域）背景颜色
          "editorLineNumber.foreground": "#008800", //行号高亮颜色
          "editor.selectionBackground": "#65C2FD", //选中背景颜色
          "editor.selectionForeground": "#FFFFFF", //选中内容颜色
        },
      });

      monaco.editor.setTheme("custom");

      this.editor = monaco.editor.create(that.$refs.editor, {
        value: that.codeText || that.code,
        language: "sql",
        theme: "custom",
        lineNumbers: "on",
        selectOnLineNumbers: true, //显示行号
        roundedSelection: false,
        readOnly: false, // 只读
        cursorStyle: "line", //光标样式
        automaticLayout: true, //自动布局
        glyphMargin: false, //字形边缘
        useTabStops: false,
        fontSize: 14, //字体大小
        autoIndent: true, //自动布局
        quickSuggestionsDelay: 500, //代码提示延时
        minimap: {
          enabled: false, // 不要小地图
        },
      });

      //编辑器随窗口自适应
      window.addEventListener("resize", function () {
        that.editor.layout();
      });

      this.codeSuggestion();
    },
    // 代码补全，实际未生效，不知道如何生效
    codeSuggestion() {
      monaco.languages.registerCompletionItemProvider("sql", {
        provideCompletionItems: function (model, position) {
          // 获取当前行数
          const line = position.lineNumber;

          // 获取当前列数
          const column = position.column;
          // 获取当前输入行的所有内容
          const content = model.getLineContent(line);
          // 通过下标来获取当前光标后一个内容，即为刚输入的内容
          const sym = content[column - 2];
          var textUntilPosition = model.getValueInRange({
            startLineNumber: 1,
            startColumn: 1,
            endLineNumber: position.lineNumber,
            endColumn: position.column,
          });
          var word = model.getWordUntilPosition(position);
          var range = {
            startLineNumber: position.lineNumber,
            endLineNumber: position.lineNumber,
            startColumn: word.startColumn,
            endColumn: word.endColumn,
          };
          //---------------------------------------------------
          //上面的代码仅仅是为了获取sym，即提示符
          //---------------------------------------------------
          var suggestions = [];
          if (sym == "$") {
            //...
            //拦截到用户输入$，开始设置提示内容，同else中代码一致，自行拓展
          } else {
            //直接提示，以下为sql语句关键词提示
            var sqlStr = [
              "SELECT",
              "FROM",
              "WHERE",
              "AND",
              "OR",
              "LIMIT",
              "ORDER BY",
              "GROUP BY",
              "DROP",
              "LIKE",
            ];
            for (var i in sqlStr) {
              suggestions.push({
                label: sqlStr[i], // 显示的提示内容
                kind: monaco.languages.CompletionItemKind["Function"], // 用来显示提示内容后的不同的图标
                insertText: sqlStr[i], // 选择后粘贴到编辑器中的文字
                detail: "", // 提示内容后的说明
                range: range,
              });
            }
          }
          return {
            suggestions: suggestions,
          };
        },
        triggerCharacters: ["$", ""],
      });
    },
    // sql格式化，格式化结果换行太多，故屏蔽了该功能
    formatSql() {
      let sqlContent = this.editor.getValue();
      this.editor.setValue(format(sqlContent));
    },
    // 新标签页打开参考手册
    openHelp(){
      window.open('https://opengauss.org/zh/docs/2.1.0/docs/Developerguide/SQL%E5%8F%82%E8%80%83.html');
    },
  },
};
</script>

<style lang="css" scoped>
.monaco-editor {
  height: 35vh;
}

.CodeMirror {
  border: 1px solid black;
  height: 800px;
}

.CodeMirror-scroll {
  height: 800px;
  overflow-y: hidden;
  overflow-x: auto;
}

.edit-btn-box {
  display: flex;
}

.giao-rows .sql-edit-btn {
  background-color: #ffffff;
  border-radius: 0;
  color: black;
  margin: 0px;
  padding: 6px 8px;
  font-size: 20px;
  font-weight: bold;
  font-family: "MicroSoft Yahei";
}

.giao-rows .sql-edit-btn:hover {
  background-color: #000000;
  color: #ffffff;
}

.switch-btn .el-input__inner {
  height: 28px;
  line-height: 28px;
  border-color: #0e52b6;
  border-radius: 0;
  color: #0e52b6;
  font-weight: bold;
}

.switch-btn .el-input__icon {
  line-height: 28px;
}
</style>
