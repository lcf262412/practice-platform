<template>
  <!--修改测试信息界面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>测试信息</h1></div>
        <el-button
          type="text"
          size="medium"
          @click="(dialogFormVisible = true), (modifyOrAdd = 1)"
          ><h1>新增测试数据</h1></el-button
        >
        <el-button
          v-show="notNull"
          type="text"
          size="medium"
          @click="jumpToDelAll"
          ><h1>删除全部测试数据</h1></el-button
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
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="test_case" label="样例参数" width="120">
        </el-table-column>

        <el-table-column width="120">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="modifyClick(scope.row)">修改</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="120">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="delClick(scope.row)">删除</a>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog title="测试样例" :visible.sync="dialogFormVisible">
        <el-form :model="form">
          <el-form-item>
            <el-input
              v-show="modifyOrAdd == 1"
              v-model="form.testCase"
              type="textarea"
            >
            </el-input>
            <el-input
              v-show="modifyOrAdd == 2"
              v-model="form.modifyTestCase"
              type="textarea"
            >
            </el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="(dialogFormVisible = false), (modifyOrAdd = 0)"
            >取 消</el-button
          >

          <el-button type="primary" @click="addOrModifyClick">确 定</el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import {
  addNewQuestion,
  addOneTestCase,
  updateOneTestCase,
  deleteOneTestCase,
  disconnectJudgeDb,
} from "@/api/api.js";
import {
  getTeacherAllJudge,
  connectJudgeDb,
  selectTestCases,
  deleteAllTestCase,
} from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      tableData: [],
      tableLable: {
        questionId: "题目编号",
        caseId: "用例编号",
        test_case: "测试参数",
      },
      notNull: true,
      modifyOrAdd: 0,
      caseId: "",
      dialogFormVisible: false,
      form: {
        testCase: "",
        modifyTestCase: "",
      },
    };
  },
  mounted() {
    var params = new Object();
    if (sessionStorage.getItem("selectRole")) {
      params.zjId = sessionStorage.getItem("selectRole");
    }
    params.teacherId = this.$store.state.username;
    params.dbName = this.$route.query.dbName;
    connectJudgeDb(params).then((res) => {
      const { code, result } = res.data;
      if (code === "0000") {
        selectTestCases({
          teacherId: params.zjId ? params.zjId : this.$store.state.username,
          questionId: this.$route.query.questionId,
        }).then((res) => {
          const { code, result } = res.data;
          if (code === "0000") {
            this.tableData = result.caseList;

            if (result.caseList.length == 0) {
              this.notNull = false;
            }
          } else {
            this.$alert(result.errmessage);
          }
        });
      } else {
        this.$alert(result.errmessage, {
          confirmButtonText: "确认",
          callback: (action) => {
            this.$router.go(-1);
          },
        });
      }
    });
  },
  destroyed() {
    var userId = "";
    if (sessionStorage.getItem("selectRole")) {
      userId = sessionStorage.getItem("selectRole");
    } else {
      userId = this.$store.state.username;
    }
    disconnectJudgeDb({ teacherId: userId }).then((res) => {
      const { code, result } = res.data;
      if (code === "0000") {
      } else {
        this.$alert(result.errmessage);
      }
    });
  },
  methods: {
    addOrModifyClick() {
      if (this.modifyOrAdd == 1) {
        var userId = "";
        if (sessionStorage.getItem("selectRole")) {
          userId = sessionStorage.getItem("selectRole");
        } else {
          userId = this.$store.state.username;
        }
        addOneTestCase({
          teacherId: userId,
          questionId: this.$route.query.questionId,
          testCase: this.form.testCase,
        }).then((res) => {
          const { code, result } = res.data;
          if (code === "0000") {
            this.dialogFormVisible = false;
            this.$router.go(0);
          } else {
            this.$alert(result.errmessage);
          }
        });
      } else if (this.modifyOrAdd == 2) {
        var userId = "";
        if (sessionStorage.getItem("selectRole")) {
          userId = sessionStorage.getItem("selectRole");
        } else {
          userId = this.$store.state.username;
        }
        updateOneTestCase({
          teacherId: userId,
          questionId: this.$route.query.questionId,
          testCase: this.form.modifyTestCase,
          caseId: this.caseId,
        }).then((res) => {
          const { code, result } = res.data;
          if (code === "0000") {
            this.dialogFormVisible = false;
            this.$router.go(0);
          } else {
            this.$alert(result.errmessage);
          }
        });
      }
    },

    jumpToDelAll() {
      //删除全部测试样例
      var userId = "";
      if (sessionStorage.getItem("selectRole")) {
        userId = sessionStorage.getItem("selectRole");
      } else {
        userId = this.$store.state.username;
      }
      deleteAllTestCase({
        teacherId: userId,
        questionId: this.$route.query.questionId,
      }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.dialogFormVisible = false;
          this.$router.go(0);
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    modifyClick(row) {
      //修改测试样例
      this.caseId = row.caseId;
      this.dialogFormVisible = true;
      this.modifyOrAdd = 2;
    },
    delClick(row) {
      //删除测试样例
      var userId = "";
      if (sessionStorage.getItem("selectRole")) {
        userId = sessionStorage.getItem("selectRole");
      } else {
        userId = this.$store.state.username;
      }
      deleteOneTestCase({
        teacherId: userId,
        questionId: this.$route.query.questionId,
        caseId: row.caseId,
      }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.dialogFormVisible = false;
          this.$router.go(0);
        } else {
          this.$alert(result.errmessage);
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
#button {
  margin-left: 120px;
  display: flex;
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
.paper-main {
  margin-top: 10px;
  background-color: white;
  border-radius: 10px;

  box-shadow: 1px 1px 1px rgb(154, 154, 154);
}
.page-title3 {
  margin-left: 20px;
  margin-right: auto;
}
.el-table .cell {
  white-space: pre-wrap;
}
</style>
