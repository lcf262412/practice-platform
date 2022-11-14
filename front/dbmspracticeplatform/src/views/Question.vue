<template>
  <!--试题管理页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>全部试题</h1></div>
        <el-button
          class="addQuestion"
          type="text"
          size="medium"
          @click="jumpToAdd"
          ><h1>添加题目</h1></el-button
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
        :key="itemkey"
        :data="tableData"
        style="width: 100%"
        :fit="true"
        @sort-change="sortChange"
      >
        <el-table-column
          prop="id"
          label="题目编号"
          width="120"
          sortable="custom"
        >
        </el-table-column>
        <el-table-column prop="name" label="出题教师姓名" sortable="custom">
        </el-table-column>
        <el-table-column
          prop="dbName"
          label="数据库"
          width="120"
          sortable="custom"
        >
        </el-table-column>
        <el-table-column prop="title" label="题目标题" sortable="custom">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="titleClick(scope.row)">{{
                scope.row.title
              }}</a>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          label="题目类型"
          width="120"
          prop="questionClass"
          sortable="custom"
        >
          <template slot-scope="scope">
            {{ classFormat(scope.row.questionClass) }}
          </template>
        </el-table-column>
        <el-table-column width="120">
          <template slot-scope="scope">
            <el-button @click="jumpToText(scope.row.id)"> 加入试卷</el-button>

            <el-dialog
              title="加入试卷"
              :visible.sync="dialogFormVisible"
              @close="$reset('formReset')"
            >
              <el-form :model="form" ref="formReset" :rules="rules">
                <el-form-item
                  label="选择加入试卷"
                  :label-width="formLabelWidth"
                  prop="exercise"
                >
                  <el-select
                    v-model="form.exercise"
                    placeholder="请选择试卷"
                    ref="ad"
                    @change="getvaluemethod($event)"
                  >
                    <el-option
                      v-for="item in allExer"
                      :key="item.e_id"
                      :label="item.e_name"
                      :value="item.e_id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item
                  label="设置题目分数"
                  :label-width="formLabelWidth"
                  prop="score"
                >
                  <el-input
                    v-model="form.score"
                    placeholder="默认为10分,分值0-100"
                    @change="inputScore($event)"
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  已经存在的试卷
                  <div v-for="item in questInExer" :key="item.e_id">
                    {{ item.e_name }}
                  </div>
                </el-form-item>
              </el-form>
              <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>

                <el-button type="primary" @click="addToExerClick('formReset')"
                  >确 定</el-button
                >
              </div>
            </el-dialog>
          </template>
        </el-table-column>
        <el-table-column width="100">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="modifyClick(scope.row)">修改</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="100">
          <template slot-scope="scope">
            <div>
              <a
                href="javascript:;"
                v-show="scope.row.questionClass == 12"
                @click="modifyCasesClick(scope.row)"
                >修改测试数据</a
              >
            </div>
          </template>
        </el-table-column>
        <el-table-column width="100">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="delClick(scope.row.id)">删除</a>
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

          <el-button type="primary" @click="delQuestionClick">确 定</el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { getAllQuestion, realDelQuest } from "@/api/api.js";
import { addQuestInExer } from "@/api/api.js";
import { getAllExer } from "@/api/api.js";
import { getExerByQuest } from "@/api/api.js";
export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      itemkey: true,
      formReset: 0,
      req: {
        id: this.$store.state.username,
      },
      request: {
        questionId: "",
        exerciseId: "",
        score: 10,
      },
      rules: {
        exercise: [
          {
            required: true,
            message: "试卷不能为空",
          },
        ],
      },
      questClass: [
        {
          id: 1,
          class: "select",
        },
        {
          id: 2,
          class: "DML",
        },
        {
          id: 3,
          class: "create table",
        },
        {
          id: 4,
          class: "alter table",
        },
        {
          id: 5,
          class: "create view",
        },
        {
          id: 6,
          class: "create index",
        },
        {
          id: 7,
          class: "create user/role",
        },
        {
          id: 8,
          class: "grant table",
        },
        {
          id: 9,
          class: "revoke table",
        },
        {
          id: 10,
          class: "grant user",
        },
        {
          id: 11,
          class: "revoke table",
        },
        {
          id: 12,
          class: "create procedure/function",
        },
      ],
      allExer: [],
      questInExer: [],
      currentPage: 1,
      pagesize: 50,
      total: 0,
      tableAllData: [],
      tableData: [],
      tableLable: {
        id: "题目编号",
        name: "出题教师姓名",
        dbName: "数据库",
        title: "题目标题",
        questionClass: "题目类型",
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
    this.getQuestions();
    getAllExer(this.req).then((res) => {
      //获得全部试卷信息

      this.allExer = res.data.result;
    });
  },

  methods: {
    classFormat: function (cellValue) {
      //格式化Boolean值
      var ret = ""; //你想在页面展示的值
      if (cellValue == 0) {
        return ret;
      }
      return this.questClass[cellValue - 1].class;
    },
    getQuestions() {
      //获得全部试题信息
      getAllQuestion().then((res) => {
        //获得全部试题信息

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
        this.getQuestions();
      }
    },
    //通过数组对象的某个属性进行排序
    sortList(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (property == "name" || property == "dbName" || property == "title") {
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
        if (property == "name" || property == "dbName" || property == "title") {
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

    delClick(id) {
      //点击删除按钮
      this.dialogFormVisible1 = true;
      this.rowId = id;
    },
    delQuestionClick() {
      //确认删除试题

      realDelQuest({ id: this.rowId }).then((res) => {
        //物理删除题目
        const { code, result } = res.data;
        if (code === "0000") {
          this.$router.go(0);
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    titleClick(row) {
      //跳转到题目详情
      this.$router.push({
        path: "/questionContent",
        query: { questionId: row.id },
      });
    },
    modifyCasesClick(row) {
      this.$router.push({
        path: "/modifyTestCases",
        query: { questionId: row.id, dbName: row.dbName },
      });
    },
    jumpToText(id) {
      //加入试卷显示弹窗
      this.dialogFormVisible = true;
      this.request.questionId = id;
      getExerByQuest({ questionId: id }).then((res) => {
        this.questInExer = res.data.result;
      });
    },
    jumpToAdd() {
      //跳转到添加试题
      this.$router.push({ path: "/addQuestion" });
    },
    getvaluemethod: function (event) {
      //绑定试卷Id

      this.request.exerciseId = event;
    },
    inputScore: function (event) {
      //绑定输入分数

      this.request.score = event;
    },
    modifyClick(row) {
      //跳转到修改试题
      this.$router.push({
        path: "/modifyQuest",
        query: { questionId: row.id },
      });
    },
    addToExerClick(formName) {
      //加入试卷
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (!this.request.score) {
            this.request.score = 10;
          }
          if (this.request.score < 0 || this.request.score > 100) {
            this.$alert("分值不能小于0或超过100！");
          } else {
            addQuestInExer(this.request)
              .then((res) => {
                const { code, result } = res.data;
                if (code == "0000") {
                  this.dialogFormVisible = false;
                  this.$alert("添加成功！");
                }
              })
              .catch((err) => {
                this.$alert("添加失败");
              });
          }
        } else {
          return false;
        }
      });
    },
  },
};
</script>

<style>
.el-header {
  padding: 0;
}
.addQuestion {
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
