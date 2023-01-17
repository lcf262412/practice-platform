<template>
  <div class="sql-result">
    <el-card class="box-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-button
            v-if="dataList[0].ResultType === 'QUERY'"
            type="primary"
            @click="exportTable2Xlsx(dataList[1])"
            :disabled="!canExport"
            >导出EXCEL</el-button
          >
          <el-button
            v-if="dataList[0].ResultType === 'QUERY'"
            type="primary"
            :disabled="!canExport"
            @click="exportTable2CSV(dataList[0])"
            >导出CSV</el-button
          >

          <el-button
            v-if="dataList[0].ResultType === 'QUERY'"
            type="primary"
            v-show="dataList[0].supportUpdate"
            @click="addContent"
            >增加</el-button
          >
          <el-button
            v-if="dataList[0].ResultType === 'QUERY'"
            type="primary"
            v-show="dataList[0].supportUpdate"
            @click="deleteContent"
            >删除</el-button
          >
          <el-button
            v-if="dataList[0].ResultType === 'QUERY'"
            type="primary"
            v-show="dataList[0].supportUpdate"
            @click="editContent"
            >修改</el-button
          >
          <el-button
            v-if="dataList[0].ResultType === 'QUERY'"
            type="primary"
            v-show="dataList[0].supportUpdate"
            @click="doExecuteSql"
            >提交</el-button
          >
          <el-button
            v-show="cancelFlag && dataList[0].supportUpdate"
            type="info"
            @click="cancelClick"
            >取消</el-button
          >
        </div>
      </template>

      <div>
        <div class="font-xs text-gray" style="white-space: pre-wrap">
          执行语句：{{ dataList[0].code }}
        </div>
        <div
          v-if="dataList[0].ResultType && dataList[0].ResultType === 'ERROR'"
        >
          <div class="font-xs text-gray" style="white-space: pre-wrap">
            执行失败：{{ dataList[0].errmsg }}
          </div>
        </div>
        <div
          v-else-if="
            dataList[0].ResultType && dataList[0].ResultType === 'QUERY'
          "
        >
          <el-table
            :key="ref"
            class="result-table"
            :class="getClass(dataList[1])"
            :data="dataList[0].rowList"
            style="width: 100%"
            type="index"
            ref="table"
            :row-class-name="tableRowClassName"
            :cell-class-name="CellColor"
            @cell-dblclick="handleDoubleClick"
            @selection-change="handleSelectionChange"
          >
            <el-table-column v-if="deleteFlag" type="selection" width="55">
            </el-table-column>
            <el-table-column
              v-for="(col, idx) in calcTableColumn(dataList[0].rowList)"
              :key="idx"
              :prop="col.prop"
              :label="col.label"
              :show-overflow-tooltip="true"
              min-width="180"
            >
              <template slot-scope="scope">
                <el-input
                  v-if="editFlag && scope.row[scope.column.property].edit"
                  size="mini"
                  id="input-info"
                  ref="input"
                  autosize
                  type="textarea"
                  v-model="scope.row[col.prop].value"
                  @blur="
                    saveClick(
                      $event,
                      scope.row,
                      col.prop,
                      scope.column,
                      scope.$index
                    )
                  "
                  @focus="getContentClick($event, scope.row)"
                  @keyup.up.native="upClick($event, scope.row, scope.column)"
                  @keyup.down.native="
                    downClick($event, scope.row, scope.column)
                  "
                  @keyup.right.native="
                    rightClick($event, scope.row, scope.column)
                  "
                  @keyup.left.native="
                    leftClick($event, scope.row, scope.column)
                  "
                  @keydown.tab.native="
                    tabClick($event, scope.row, scope.column)
                  "
                ></el-input>

                <span
                  v-else-if="editFlag"
                  class="edit-input"
                  style="white-space: pre-wrap"
                  >{{ scope.row[col.prop].value }}</span
                >
                <span v-else style="white-space: pre-wrap">{{
                  scope.row[col.prop]
                }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else>
          <div class="font-xs text-gray">执行成功！</div>
          <div class="font-xs text-gray">
            影响行数：{{ dataList[0].nRowsAffected }}
          </div>
          <el-button
            v-show="dataList[2].activeTabName == 0 && dataList[2].editFlag"
            type="primary"
            @click="returnClick"
            >返回</el-button
          >
        </div>
      </div>
    </el-card>

    <el-dialog title="选择唯一列" :visible.sync="dialogFormVisible">
      <el-checkbox
        :indeterminate="isIndeterminate"
        v-model="checkAll"
        @change="handleCheckAllChange"
      >
        全选
      </el-checkbox>
      <el-checkbox-group v-model="checkedOptions" @change="handleCheckedChange">
        <el-checkbox
          v-for="item in allCheckOptions"
          :label="item"
          :key="item"
          >{{ item }}</el-checkbox
        >
      </el-checkbox-group>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelSummit">取 消</el-button>

        <el-button type="primary" @click="createAllSql">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog
      title="即将执行SQL:"
      :visible.sync="dialogFormVisible1"
      @close="returnSelect"
    >
      <div class="msg">{{ all_sql }}</div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="returnSelect">取 消</el-button>

        <el-button type="primary" @click="finalClick">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { table2Excel, JSonToCSV } from "@/util/utils";
import { init } from "events";

export default {
  name: "oneResult",
  setup() {},
  props: ["dataList", "changeEdit", "sqlold", "lazy"],
  data() {
    return {
      ref: "",
      canExport: true,
      statusText: "请在输入框输入sql，点击运行，即可查看运行结果",
      activeTabName: 0,
      addLineIndex: 0,
      tableProps: [],
      cancelFlag: false,
      deleteFlag: false,
      dataList_a: this.dataList,
      editModel: {
        insert: false,
        delete: false,
        update: false,
      },
      rowId: [],
      editFlag: false,
      keyFlag: false, //方向键移动到边界第一次标志
      keyUpFlag: false, //方向键按下标志
      table_old: [],
      table_edit: [],
      sql_old: "",
      all_sql: "",
      multipleSelection: [],
      dialogFormVisible: false,
      dialogFormVisible1: false,
      allCheckOptions: [],
      checkAll: false,
      isIndeterminate: true,
      checkedOptions: [],

      updateResult: [],
      cellContent_old: "",
      cellContent_new: "",
    };
  },
  filters: {
    errorTextFilter: function (v) {
      if (!v) {
        return "-";
      }
      const idx = v.indexOf("]");
      return v.substring(idx + 1);
    },
  },

  mounted() {},
  watch: {
    changeEdit(newEdit, oldEdit) {
      this.editFlag = false;
    },
  },
  methods: {
    init(from, to) {
      for (let j = from; j < to; j++) {
        let item = this.dataList[0].rowList[j];

        for (let i in item) {
          item[i] = {
            value: item[i] == null ? "" : item[i],
            edit: false,
            id: j + 1,
            modify: false,
          };
        }
      }
    },

    //添加行
    addContent() {
      if (this.editModel.delete == false && this.editModel.update == false) {
        this.cancelFlag = true;

        this.$emit("sqloldClick", this.dataList[1]);
        this.canExport = false;
        let obj = {};
        let props = [];
        if (this.dataList[0]) {
          for (let i in this.dataList[0].metaDataList) {
            props.push(this.dataList[0].metaDataList[i].columnName);
            this.$set(obj, props[i], "");
          }
        }
        // 这里报unexpected mutaion，但是不影响运行
        this.dataList[0].rowList.push(obj);
        this.table_old = JSON.parse(JSON.stringify(this.dataList[0].rowList));
        if (this.editModel.insert == false) {
          this.init(0, this.dataList[0].rowList.length - 1);
        }
        this.init(
          this.dataList[0].rowList.length - 1,
          this.dataList[0].rowList.length
        );

        this.editFlag = true;
        this.addLineIndex++;
        this.editModel.insert = true;
      } else {
        this.$alert("请选择正确模式");
      }
    },
    //删除行
    deleteContent() {
      if (this.editModel.insert == false && this.editModel.update == false) {
        this.cancelFlag = true;
        this.deleteFlag = true;
        this.$emit("sqloldClick", this.dataList[1]);
        this.canExport = false;
        if (this.editModel.delete == false) {
          this.init(0, this.dataList[0].rowList.length);
        }
        this.editFlag = true;
        this.editModel.delete = true;
      } else {
        this.$alert("请选择正确模式");
      }
    },
    //修改
    editContent() {
      if (this.editModel.insert == false && this.editModel.delete == false) {
        this.cancelFlag = true;

        this.$emit("sqloldClick", this.dataList[1]);
        this.canExport = false;
        if (this.editModel.update == false) {
          this.init(0, this.dataList[0].rowList.length);
        }
        this.editModel.update = true;
        this.table_old = JSON.parse(JSON.stringify(this.dataList[0].rowList));
        this.editFlag = true;
      } else {
        this.$alert("请选择正确模式");
      }
    },
    //生成insertSql
    createInsertSql(row, count, props) {
      let sql =
        "insert into " + this.dataList[0].metaDataList[0].TableName + "(";
      let flag = false;
      for (let i = 0; i < count; i++) {
        if (row[props[i]].value != "") {
          sql = sql + props[i];
          flag = true;
          sql = sql + ",";
        }
      }
      sql = sql.substring(0, sql.length - 1);
      sql = sql + ") values(";
      for (let i = 0; i < count; i++) {
        if (row[props[i]].value != "") {
          sql = sql + "'" + row[props[i]].value + "'";

          sql = sql + ",";
        }
      }
      sql = sql.substring(0, sql.length - 1);
      sql = sql + ");";
      if (flag == false) {
        sql = "";
      }
      return sql;
    },
    //生成删除sql
    createDeleteSql(row, selections) {
      let sql =
        "delete from " + this.dataList[0].metaDataList[0].TableName + " where";

      for (let i = 0; i < selections.length; i++) {
        sql =
          sql +
          " " +
          selections[i] +
          "=" +
          "'" +
          row[selections[i]].value +
          "'" +
          " AND";
      }
      sql = sql.substring(0, sql.length - 4);
      sql = sql + ";";
      return sql;
    },
    //生成修改sql
    createUpdateSql(selections, update) {
      let sql = "update " + this.dataList[0].metaDataList[0].TableName + " set";
      sql =
        sql +
        " " +
        update.prop +
        "=" +
        "'" +
        update.content_new +
        "'" +
        " where ";

      for (let i = 0; i < selections.length; i++) {
        sql =
          sql +
          selections[i] +
          "=" +
          "'" +
          this.table_old[update.row_id - 1][selections[i]].value +
          "'" +
          " AND ";
      }
      sql = sql.substring(0, sql.length - 5);
      sql = sql + ";";
      return sql;
    },
    //点击提交
    doExecuteSql() {
      let props = [];
      this.$emit("sqloldClick", this.dataList[1]);
      for (let i in this.dataList[0].metaDataList) {
        props.push(this.dataList[0].metaDataList[i].columnName);
      }
      if (this.editModel.insert == true) {
        for (let i = this.addLineIndex; i > 0; i--) {
          let size = this.dataList[0].rowList.length;
          let row = this.dataList[0].rowList[size - i];

          let sql = this.createInsertSql(
            row,
            this.dataList[0].columnCount,
            props
          );
          this.all_sql = this.all_sql + sql;
        }
        this.$emit("flagClick", "edit");
        this.$emit("execClick1", this.all_sql);

        this.all_sql = "";

        this.cancelFlag = false;
        this.addLineIndex = 0;
        this.editModel.insert = false;
        this.editModel.delete = false;
        this.editModel.update = false;
        this.canExport = true;
      } else if (
        this.editModel.delete == true ||
        this.editModel.update == true
      ) {
        if (
          this.multipleSelection.length == 0 &&
          this.updateResult.length == 0
        ) {
          this.$alert("请选择删除或修改内容");
        } else {
          this.allCheckOptions = props;
          this.dialogFormVisible = true;
        }
      }
    },

    //撤销暂存修改
    cancelClick() {
      this.$emit("sqloldClick", this.dataList[1]);
      this.$emit("execClick1", this.sqlold);

      this.addLineIndex = 0;
      this.cancelFlag = false;
      this.isShow = false;
      this.deleteFlag = false;
      this.canExport = true;
      this.editModel.insert = false;
      this.editModel.delete = false;
      this.editModel.update = false;
      this.updateResult = [];
    },
    //取消提交
    cancelSummit() {
      this.dialogFormVisible = false;
      this.checkedOptions = [];
      this.checkAll = false;
    },
    //点击生成将要执行的SQL
    createAllSql() {
      let rows = this.multipleSelection;
      if (this.editModel.delete == true && this.checkedOptions.length > 0) {
        this.dialogFormVisible1 = true;
        for (let i = 0; i < rows.length; i++) {
          const sql = this.createDeleteSql(rows[i], this.checkedOptions);
          this.all_sql = this.all_sql + sql + "\n";
        }
      } else if (
        this.editModel.update == true &&
        this.checkedOptions.length > 0
      ) {
        this.dialogFormVisible1 = true;
        for (let i = 0; i < this.updateResult.length; i++) {
          const sql = this.createUpdateSql(
            this.checkedOptions,
            this.updateResult[i]
          );
          this.all_sql = this.all_sql + sql + "\n";
        }
      } else {
        this.$alert("请选择至少一列");
      }
    },
    //重选唯一列
    returnSelect() {
      this.dialogFormVisible1 = false;
      this.all_sql = "";
    },
    //最终确定提交
    finalClick() {
      this.$emit("flagClick", "edit");
      this.$emit("execClick1", this.all_sql);
      this.all_sql = "";
      this.checkedOptions = [];
      this.updateResult = [];
      this.cancelFlag = false;
      this.dialogFormVisible = false;
      this.dialogFormVisible1 = false;
      this.deleteFlag = false;
      this.editModel.insert = false;
      this.editModel.delete = false;
      this.editModel.update = false;
      this.canExport = true;
    },
    //返回查看结果
    returnClick() {
      this.$emit("execClick1", this.sqlold);
      this.$emit("returnClick");
    },

    // 填充表格数据
    calcTableColumn(rows) {
      let list = [];
      let props = [];
      if (!rows) {
        return list;
      }
      if (this.dataList[0]) {
        for (let i in this.dataList[0].metaDataList) {
          props.push(this.dataList[0].metaDataList[i].columnName);
        }
      }

      for (let key in props) {
        const item = {
          prop: props[key],
          label: props[key],
        };
        list.push(item);
      }

      return list;
    },
    //获取表格行号
    tableRowClassName({ row, rowIndex }) {},
    //单元格回调
    CellColor({ row, column, rowIndex, columnIndex }) {
      if (
        this.editModel.update == true &&
        row[column.property] &&
        row[column.property].modify == true
      ) {
        return "modifyCell";
      }
      if (this.editModel.update == true) {
        return "pointerCell";
      }
      if (
        this.editModel.insert == true &&
        row[column.property] &&
        row[column.property].id >
          this.dataList[0].rowList.length - this.addLineIndex
      ) {
        return "pointerCell";
      }
    },
    // 双击
    handleDoubleClick(row, column, cell, event) {
      if (this.editModel.update == true) {
        row[column.property].edit = true;
        this.ref = Math.random();
        this.$nextTick(() => {
          this.$refs["input"][0].focus();
        });
      } else if (this.editModel.insert == true) {
        if (
          row[column.property].id >
          this.dataList[0].rowList.length - this.addLineIndex
        ) {
          row[column.property].edit = true;
          this.ref = Math.random();
          this.$nextTick(() => {
            this.$refs["input"][0].focus();
          });
        }
      }
    },

    //上方向键
    upClick(event, row, column) {
      const input = document.getElementById("input-info");
      if (row[column.property].id >= 2) {
        if (
          input.selectionStart <=
          this.$refs["input"][0].value.toString().indexOf("\n") + 1
        ) {
          if (
            this.editModel.update == true ||
            (row[column.property].id >
              this.dataList[0].rowList.length - this.addLineIndex + 1 &&
              this.editModel.insert == true)
          ) {
            row[column.property].edit = false;
            this.ref = Math.random();

            this.dataList[0].rowList[row[column.property].id - 2][
              column.property
            ].edit = true;
            this.$nextTick(() => {
              this.$refs["input"][0].focus();
            });
          }
        }
      }
    },
    //下方向键
    downClick(event, row, column) {
      if (row[column.property].id <= this.dataList[0].rowList.length - 1) {
        row[column.property].edit = false;
        this.ref = Math.random();

        this.dataList[0].rowList[row[column.property].id][
          column.property
        ].edit = true;
        this.$nextTick(() => {
          this.$refs["input"][0].focus();
        });
      }
    },
    //左方向键
    leftClick(event, row, column) {
      let props = Object.keys(row);
      let index = props.indexOf(column.property);

      const input = document.getElementById("input-info");

      if (index > 0) {
        if (input.selectionStart == 0) {
          row[column.property].edit = false;
          row[props[index - 1]].edit = true;
          this.ref = Math.random();
          this.$nextTick(() => {
            this.$refs["input"][0].focus();
          });
        }
      }
    },
    //右方向键
    rightClick(event, row, column) {
      let props = Object.keys(row);
      let index = props.indexOf(column.property);

      const input = document.getElementById("input-info");
      if (index < props.length - 1) {
        if (
          input.selectionStart == this.$refs["input"][0].value.toString().length
        ) {
          row[column.property].edit = false;
          row[props[index + 1]].edit = true;
          this.ref = Math.random();
          this.$nextTick(() => {
            this.$refs["input"][0].focus();
          });
        }
      }
    },
    //tab键
    tabClick(event, row, column) {
      let props = Object.keys(row);
      let index = props.indexOf(column.property);

      event.preventDefault();

      if (index < props.length - 1) {
        row[column.property].edit = false;
        row[props[index + 1]].edit = true;
        this.ref = Math.random();
        this.$nextTick(() => {
          this.$refs["input"][0].focus();
        });
      } else {
        row[column.property].edit = false;
        this.dataList[0].rowList[row[column.property].id][props[0]].edit = true;
        this.ref = Math.random();
        this.$nextTick(() => {
          this.$refs["input"][0].focus();
        });
      }
    },
    //input获得焦点
    getContentClick(event, row) {
      if (this.editModel.update == true) {
        this.cellContent_old = event.target.value;
      }
    },
    //input失去焦点
    saveClick(event, row, props, column, index) {
      row[column.property].edit = false;
      this.keyFlag = false;
      if (this.editModel.update == true) {
        this.cellContent_new = event.target.value;
        if (this.cellContent_old != this.cellContent_new) {
          const item = {
            content_old: this.cellContent_old,
            content_new: this.cellContent_new,
            row_id: index + 1,
            prop: props,
          };
          this.table_old[index][props].value = this.cellContent_old;

          row[column.property].modify = true;
          this.updateResult.push(item);
          this.cellContent_old = "";
          this.cellContent_new = "";
        }
      }
    },
    //删除多选框选中事件
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    //唯一项多选框全选事件
    handleCheckAllChange(val) {
      this.checkedOptions = val ? this.allCheckOptions : [];

      this.isIndeterminate = false;
    },
    //唯一项多选框选中事件
    handleCheckedChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.allCheckOptions.length;

      this.isIndeterminate =
        checkedCount > 0 && checkedCount < this.allCheckOptions.length;
    },
    // 按照index命名表格css class
    getClass(index) {
      return "result-table-" + index;
    },

    // 查询结果导出为xlsx
    exportTable2Xlsx(index) {
     
    
      table2Excel(index);
    },

    // 查询结果导出为csv
    exportTable2CSV(item) {
      let timeStamp = new Date().valueOf();
      let fileName = "结果_" + timeStamp;

      JSonToCSV.setDataConver({
        data: item.rowList,
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
    },
  },
};
</script>
<style>
.sql-result {
  min-height: 180px;
  border: 1px solid #dddddd;
  border-top: 0;
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
  padding: 10px;
}
.msg {
  white-space: pre-wrap;
}
.modifyCell {
  background: rgb(29, 177, 169);
  cursor: pointer;
}
.pointerCell {
  cursor: pointer;
}
</style>
