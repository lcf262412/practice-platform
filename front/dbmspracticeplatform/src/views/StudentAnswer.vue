<template>
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div clas="page-title_2">
          <h1>
            学生姓名:{{ this.$route.query.studentName }} 学号:{{
              this.$route.query.studentId
            }}
            当前选择试卷:
          </h1>
        </div>
        <div class="select-exer">
          <el-select
            v-model="nowExer"
            placeholder="请选择试卷"
            @change="selectExer"
          >
            <el-option
              v-for="(item, index) in AllExerAnswer"
              :key="index"
              :label="item.name"
              :value="item.exerciseId"
            >
            </el-option>
          </el-select>
        </div>

        <div style="margin-left: auto; color: red">
          <h3>总分{{ studentScore }}(满分:{{ sumScore }})</h3>
        </div>
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
        :fit="true"
        style="width: 100%"
      >
        <el-table-column prop="orderId" label="序号" width="60">
        </el-table-column>
        <el-table-column prop="questionId" label="题目编号" width="100">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="questionClick(scope.row)">{{
                scope.row.questionId
              }}</a>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="dbName" label="大题名称"> </el-table-column>

        <el-table-column prop="refanswer" label="参考答案" width="400">
        </el-table-column>

        <el-table-column prop="answer" label="学生答案" width="400">
        </el-table-column>
        <el-table-column prop="idea" label="作答思路" width="400">
        </el-table-column>
        <el-table-column prop="isRight" label="是否正确" width="100">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="rightClick(scope.row)">{{
                formatBoolean(scope.row.isRight)
              }}</a>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="score" label="得分" width="100">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="scoreClick(scope.row)">{{
                scope.row.score
              }}</a>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-dialog title="修改分数" :visible.sync="dialogFormVisible">
        <el-form :model="form">
          <el-form-item label="分数" :label-width="formLabelWidth">
            <el-input
              v-model="form.score"
              placeholder="请输入分数，在0-100之间"
            ></el-input>
          </el-form-item>
        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>

          <el-button type="primary" @click="modifyScoreClick">确 定</el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";

import { getStudentExerAnswer, getExerSumScore } from "@/api/api.js";
import { getStudentAllAnswer } from "@/api/api.js";
import { modifyStudentScore } from "@/api/api.js";
import { modifyStudentAnswer } from "@/api/api.js";
export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      itemkey: "",
      req: {
        id: this.$store.state.username,
      },
      request: {
        studentId: "",
        exerciseId: "",
        questionId: "",
        score: "",
      },
      nowExer: "",
      AllExerAnswer: [],
      tableData: [],
      tableLable: {
        refanswer: "参考答案",
        score: "得分",
        isRight: "是否正确",
        questionId: "题目编号",
        answer: "做答结果",
        orderId: "题目顺序序号",
        questionClass: "题目类别",
        idea: "作答思路",
        dbName: "所在数据库",
        title: "题目标题",
      },
      sumScore: 0,
      studentScore: 0,
      dialogFormVisible: false,
      form: {
        score: "",
      },

      formLabelWidth: "120px",
    };
  },
  mounted() {
    getStudentAllAnswer({ studentId: this.$route.query.studentId }).then(
      (res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.AllExerAnswer = result;
          if (sessionStorage.getItem("nowExer")) {
            this.nowExer = parseInt(sessionStorage.getItem("nowExer"));
          } else {
            this.nowExer = this.AllExerAnswer[0].exerciseId;
          }

          this.getSelectExerAnswer();
          this.getSelectExerSumScore();
        } else {
        }
      }
    );
  },
  destroyed() {
    sessionStorage.setItem("nowExer", "");
  },
  methods: {
    formatBoolean: function (cellValue) {
      //格式化Boolean值
      var ret = ""; //你想在页面展示的值

      if (cellValue) {
        ret = "正确"; //根据自己的需求设定
      } else if (cellValue == false) {
        ret = "错误";
      } else {
        ret = "";
      }

      return ret;
    },
    //查询当前选择试卷做答结果
    getSelectExerAnswer() {
      getStudentExerAnswer({
        studentId: this.$route.query.studentId,
        exerciseId: this.nowExer,
      }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.tableData = result;
          let obj = this.AllExerAnswer.filter((obj) => {
            return obj.exerciseId == this.nowExer;
          });
          this.studentScore = obj[0].sum;
        }
      });
    },
    //查询某套试卷总分
    getSelectExerSumScore() {
      getExerSumScore({ exerciseId: this.nowExer }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.sumScore = result;
        }
      });
    },
    selectExer(value) {
      sessionStorage.setItem("nowExer", this.nowExer);
      this.getSelectExerAnswer();
      this.getSelectExerSumScore();
    },
    questionClick(row) {
      this.$router.push({
        path: "/questionContent",
        query: { questionId: row.questionId },
      });
    },
    scoreClick(row) {
      this.dialogFormVisible = true;
      this.request.studentId = this.$route.query.studentId;
      this.request.exerciseId = this.nowExer;
      this.request.questionId = row.questionId;
    },
    rightClick(row) {
      modifyStudentAnswer({
        studentId: this.$route.query.studentId,
        exerciseId: this.nowExer,
        questionId: row.questionId,
        answer: row.answer,
        isRight: !row.isRight,
        idea: row.idea,
        score: row.score,
      }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("修改成功", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert("失败");
        }
      });
    },
    modifyScoreClick() {
      this.request.score = this.form.score;
      if (this.form.score < 0 || this.form.score > 100) {
        this.$alert("分值不能小于0超过100！");
      } else {
        modifyStudentScore(this.request).then((res) => {
          const { code, result } = res.data;
          if (code === "0000") {
            this.$alert("修改成功", {
              confirmButtonText: "确认",
              callback: (action) => {
                this.$router.go(0);
              },
            });
          } else {
            this.$alert("失败");
          }
        });
        this.dialogFormVisible = false;
      }
    },
  },
};
</script>

<style>
.el-header {
  padding: 0;
}

.el-table .cell {
  white-space: pre-wrap;
}
#addQuestion {
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
.page-title_2 {
  margin-left: 20px;
  margin-right: 5px;
}
.paper-main {
  margin-top: 10px;
  background-color: white;
  border-radius: 10px;

  box-shadow: 1px 1px 1px rgb(154, 154, 154);
}
</style>
