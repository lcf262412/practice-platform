<template>
  <!--试卷管理页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>全部试卷</h1></div>
        <el-button
          class="addExercise"
          type="text"
          size="medium"
          @click="jumpToAdd"
          ><h1>创建试卷</h1></el-button
        >
      </el-header>
    </div>

    <el-main class="paper-main">
      <!-- 静态生成表格列 -->
      <el-table
        :header-cell-style="{
          fontSize: '18px',
        }"
        :cell-style="{
          fontFamily: 'MicroSoft YaHei',
          fontSize: '18px',
        }"
        :default-sort="{ prop: 'e_id' }"
        :key="itemkey"
        :fit="true"
        :data="tableData"
        @sort-change="sortChange"
        style="width: 100%"
      >
        <el-table-column
          prop="e_id"
          label="试卷编号"
          width="120"
          sortable="custom"
        >
        </el-table-column>
        <el-table-column
          prop="e_name"
          label="试卷名称"
          min-width="180"
          sortable="custom"
        >
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="titleClick(scope.row)">{{
                scope.row.e_name
              }}</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          sortable="custom"
          prop="t_name"
          label="创建人"
          width="120"
        >
        </el-table-column>
        <el-table-column sortable="custom" prop="t_id" label="创建人编号">
        </el-table-column>
        <el-table-column sortable="custom" prop="describe" label="说明">
        </el-table-column>
        <el-table-column
          sortable="custom"
          prop="isPublic"
          label="状态"
          :formatter="formatBoolean"
        >
        </el-table-column>
        <el-table-column width="120">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="modifyClick(scope.row)">{{
                stateFormat1(scope.row)
              }}</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="120">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="publicClick(scope.row)">{{
                stateFormat2(scope.row)
              }}</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="120">
          <template slot-scope="scope">
            <div v-if="scope.row.t_id == req.id">
              <a href="javascript:;" @click="delClick(scope.row.e_id)">删除</a>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 50, 100, 200]"
        :page-size="pagesize"
        layout="total,jumper,prev, pager, next,sizes"
        :total="total"
      ></el-pagination>
      <el-dialog title="确认删除" :visible.sync="dialogFormVisible1">
        <div id="warning" style="font-size: 20px; color: red">
          将彻底删除此条记录，请谨慎操作！
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible1 = false">取 消</el-button>

          <el-button type="primary" @click="delExerciseClick">确 定</el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { getAllQuestion, setExerPrivate, setExerPublic } from "@/api/api.js";
import { addQuestInExer, realDelExer } from "@/api/api.js";
import { getAllExer } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      itemkey: true,
      req: {
        id: this.$store.state.username,
      },

      allExer: [],
      currentPage: 1,
      pagesize: 50,
      total: 0,
      tableAllData: [],
      tableData: [],
      tableLable: {
        e_id: "试卷编号",
        e_name: "试卷名称",
        t_name: "创建人",
        t_id: "创建人编号",
        describe: "说明",
        isPublic: "状态",
      },
      dialogFormVisible: false,
      dialogFormVisible1: false,
      rowId: "",
      form: {
        exercise: "",
        score: "",
      },

      formLabelWidth: "120px",
    };
  },
  mounted() {
    this.getAllExers();
  },

  methods: {
    getAllExers() {
      //获取教师全部试卷
      getAllExer(this.req).then((res) => {
        //获取全部试卷
        const { code, result } = res.data;
        if (code === "0000") {
          this.tableAllData = result;
          this.tableData = result.slice(
            (this.currentPage - 1) * this.pagesize,
            this.currentPage * this.pagesize
          );
          this.total = result.length;
        }

      });
    },
    //排序切换
    sortChange(column, prop, order) {
      if (column.order === "descending") {
        this.tableAllData.sort(this.sortListDesc(column.column.property));

        this.tableData = this.tableAllData.slice(
          (this.currentPage - 1) * this.pagesize,
          this.currentPage * this.pagesize
        );
        this.total = this.tableAllData.length;
      } else if (column.order === "ascending") {
        this.tableAllData.sort(this.sortList(column.column.property));

        this.tableData = this.tableAllData.slice(
          (this.currentPage - 1) * this.pagesize,
          this.currentPage * this.pagesize
        );
        this.total = this.tableAllData.length;
      } else {
        this.getAllExers();
      }
    },
    //通过数组对象的某个属性进行排序
    sortList(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (
          property == "e_name" ||
          property == "t_name" ||
          property == "describe"
        ) {
          if (a[property] == null) {
            return -1;
          }
          return a[property].localeCompare(b[property]);
        }
        return value1 - value2;
      };
    },

    //通过数组对象的某个属性进行倒序排列
    sortListDesc(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (
          property == "e_name" ||
          property == "t_name" ||
          property == "describe"
        ) {
          if (b[property] == null) {
            return -1;
          }
          return b[property].localeCompare(a[property]);
        }
        return value2 - value1;
      };
    },

    //分页 初始页currentPage、初始每页数据数pagesize和数据testpage--->控制每页几条
    handleSizeChange(size) {
      this.pagesize = size;
      this.tableData = this.tableAllData.slice(
        (this.currentPage - 1) * this.pagesize,
        this.currentPage * this.pagesize
      );
      this.total = this.tableAllData.length;
    },
    // 控制页面的切换
    handleCurrentChange(currentPage) {
      this.currentPage = currentPage;
      this.tableData = this.tableAllData.slice(
        (this.currentPage - 1) * this.pagesize,
        this.currentPage * this.pagesize
      );
      this.total = this.tableAllData.length;
    },
    jumpToAdd() {
      //跳转到添加页面
      this.$router.push({ path: "/addExercise" });
    },
    formatBoolean: function (row, column, cellValue) {
      //格式化Boolean值
      var ret = ""; //你想在页面展示的值
      if (cellValue) {
        ret = "公开"; //根据自己的需求设定
      } else {
        ret = "未公开";
      }
      return ret;
    },
    stateFormat1(row) {
      //根据权限显示是否可修改
      let state = row.isPublic;
      let teacherId = row.t_id;

      if (teacherId === this.$store.state.username) {
        return "修改";
      } else {
        return null;
      }
    },
    stateFormat2(row) {
      //根据权限显示是否可公开
      let state = row.isPublic;
      let teacherId = row.t_id;

      if (teacherId === this.$store.state.username && state === false) {
        return "公开";
      } else if (teacherId === this.$store.state.username && state === true) {
        return "隐私";
      } else {
        return null;
      }
    },
    modifyClick(row) {
      //跳转到修改
      this.$router.push({
        path: "/modifyExerQuest",
        query: { exerciseName: row.e_name, exerciseId: row.e_id },
      });
    },
    publicClick(row) {
      //设置公开
      if (row.isPublic === false) {
        setExerPublic({ id: row.e_id }).then((res) => {
          const { code, result } = res.data;
          if (code === "0000") {
            this.$alert("公开成功", {
              confirmButtonText: "确认",
              callback: (action) => {
                this.$router.go(0);
              },
            });
          } else {
            this.$alert(result.errmessage);
          }
        });
      } else {
        setExerPrivate({ id: row.e_id }).then((res) => {
          //设置隐私
          const { code, result } = res.data;
          if (code === "0000") {
            this.$alert("隐私成功", {
              confirmButtonText: "确认",
              callback: (action) => {
                this.$router.go(0);
              },
            });
          } else {
            this.$alert(result.errmessage);
          }
        });
      }
    },
    delClick(id) {
      this.dialogFormVisible1 = true;
      this.rowId = id;
    },
    delExerciseClick() {
      //物理删除试卷
      realDelExer({ id: this.rowId }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("删除成功", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    titleClick(row) {
      //跳转到试卷详情
      this.$router.push({
        path: "/exerciseContent",
        query: {
          exerciseName: row.e_name,
          exerciseId: row.e_id,
          describe: row.describe,
        },
      });
    },
  },
};
</script>

<style>
.el-header {
  padding: 0;
}
.addExercise {
  display: flex;
  justify-content: flex-end;
}
.paper-page {
  background-color: #eeeeee;
  padding: 10px;
}
.paper-header {
  background-color: white;
  border-radius: 10px;
  box-shadow: 1px 1px 1px rgb(154, 154, 154);
  display: flex;
  flex-direction: row;
  align-items: center;
}
.page-title3 {
  margin-left: 20px;
  margin-right: auto;
}
.paper-main {
  margin-top: 10px;
  background-color: white;
  border-radius: 10px;

  box-shadow: 1px 1px 1px rgb(154, 154, 154);
}
</style>
