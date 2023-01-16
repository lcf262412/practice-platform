<template>
  <el-container class="outter-container">
    <el-main>
      <el-container class="inner-container">
        <el-aside width="37%">
          <el-tabs
            v-model="activeName"
            type="border-card"
            @tab-click="handleTabClick"
          >
            <el-tab-pane label="题目描述" name="first">
              <p>{{ dbMeta }}</p>
              <br />
              <p>题目{{ orderId }}：{{ questionContent }}</p>
              <component :is="showCorrect"></component>
            </el-tab-pane>
            <el-tab-pane label="解析提示" name="second" v-if="flag"
              ><p>{{ hint }}</p></el-tab-pane
            >
          </el-tabs>
        </el-aside>

        <el-main class="content">
          <el-input
            type="textarea"
            placeholder="请在此作答"
            v-model="textareaAnswer"
            id="answerInput"
          >
          </el-input>
          <el-input
            type="textarea"
            placeholder="可以在此写下你的思路"
            maxlength="100"
            show-word-limit
            v-model="textareaIdea"
            id="ideaInput"
          >
          </el-input>
        </el-main>
      </el-container>
    </el-main>

    <el-footer height="60px">
      <el-button
        class="quest-button"
        :disabled="orderId == 1"
        @click="jumpToPrevious"
        id="previous"
        round
        >上一题</el-button
      >
      <el-button
        class="quest-button"
        :disabled="orderId == paperLength"
        @click="jumpToNext"
        id="next"
        round
        >下一题</el-button
      >
      <el-button class="quest-button" @click="backToQuestionList" round
        >返回题目列表</el-button
      >
      <el-button class="quest-button submit" @click="submit"
        >提&nbsp;&nbsp;交</el-button
      >
    </el-footer>
  </el-container>
</template>

<script>
import SqlEditor from "@/components/SqlEditor";
import CorrectSign from "@/components/CorrectSign.vue";
import {
  getQuestionDetailsById,
  getQuestionDetailsByOrder,
  submitQuestAnswer,
  getAnswerRecord,
} from "@/api/api.js";

export default {
  components: {
    SqlEditor,
    CorrectSign,
  },
  mounted() {
    // 组件挂载时查找试题详情
    this.questionDetails();
  },
  created() {},
  data() {
    return {
      flag: true,
      activeName: "first",
      paperId: sessionStorage.getItem("paperId"),
      paperLength: sessionStorage.getItem("paperLength"),
      orderId: this.$route.query.orderId,
      questionId: "",
      editorButtonShow: false,
      textareaIdea: "",
      textareaAnswer: "",
      dbMeta: "",
      questionContent: "",
      hint: "暂无",
      answerRecord: {
        score: "",
        isRight: false,
        answer: "",
        idea: "",
      },
      correctFlag: false,
      showCorrect: "",
    };
  },
  computed: {},
  watch: {
    // 控制正确作答提示组件是否出现
    correctFlag: function () {
      this.showCorrect = this.correctFlag ? "CorrectSign" : "";
    },
  },
  methods: {
    handleTabClick(tab, event) {},
    // 获得当前题目详情
    questionDetails() {
      // 根据序号查询题目详情
      let params = new Object();
      params.exerciseId = this.paperId;
      params.orderId = this.$route.query.orderId;
      let isTest = sessionStorage.getItem("isTest");
      if (isTest === "true" && this.$store.state.role === "student") {
        this.flag = false;
      }
      getQuestionDetailsByOrder(params).then((res) => {
        if (res.data.code === "0000") {
          this.dbMeta = res.data.result.describe;
          this.questionContent = res.data.result.content;
          this.hint = res.data.result.analysis;
          this.questionId = res.data.result.questionId;
          // 获取作答记录
          this.getRecord();
        }
      });
    },
    // 作答提交
    submit() {
      if (this.isEmptyStr(this.textareaAnswer)) {
        this.$alert("答案不能为空", "提示", {
          confirmButtonText: "确认",
          callback: (action) => {},
        });
      } else {
        var params = new Object();
        params.exerciseId = this.paperId;
        params.questionId = this.questionId;
        params.studentId = this.$store.state.username;
        params.answer = this.textareaAnswer;
        params.idea = this.textareaIdea;

        submitQuestAnswer(params).then((res) => {
          var { code, result } = res.data;
          if (code === "0000") {
            if (result["judge result"] === false) {
              // 作答结果不正确
              this.$alert("作答错误", "提示", {
                confirmButtonText: "确认",
                callback: (action) => {},
              });
              this.correctFlag = false;
            } else if (result["judge result"] === true) {
              // 作答结果正确
              this.$alert("作答正确！", "提示", {
                confirmButtonText: "确认",
                callback: (action) => {},
              });
              this.correctFlag = true;
            }
          } else {
            this.$alert(result.errmessage);
          }
        });
      }
    },

    // 上一题
    jumpToPrevious() {
      this.$router
        .push({
          path: "/quest/answer-question",
          query: { orderId: --this.orderId },
        })
        .catch((err) => {
          err;
        });
      
      // 网页刷新以重新挂载（待优化）
      window.location.reload();
    },
    // 下一题
    jumpToNext() {
      this.$router
        .push({
          path: "/quest/answer-question",
          query: { orderId: ++this.orderId },
        })
        .catch((err) => {
          err;
        });
        
      // 网页刷新以重新挂载（待优化）
      window.location.reload();
    },
    // 获取答案记录
    getRecord() {
      var params = new Object();
      params.exerciseId = this.paperId;
      params.questionId = this.questionId;
      params.studentId = this.$store.state.username;

      getAnswerRecord(params).then((res) => {
        if (res.data.code === "0000") {
          if (res.data.result) {
            this.answerRecord.score = res.data.result.score;
            this.answerRecord.isRight = res.data.result.isRight;
            this.correctFlag = res.data.result.isRight;
            this.answerRecord.answer = res.data.result.answer;
            this.answerRecord.idea = res.data.result.idea;
          }

          if (this.answerRecord.isRight) {
          }

          this.textareaAnswer = this.answerRecord.answer;
          this.textareaIdea = this.answerRecord.idea;
        }
      });
    },
    // 字符串判空
    isEmptyStr(s) {
      if (s == null || s === "") {
        return true;
      }
      return false;
    },
    // 返回题目列表
    backToQuestionList() {
      this.$router.push({
        name: "paperInformation",
      });
    },
  },
};
</script>

<style lang="css">
:root {
  --mainHeight: calc(100vh - (60px + 60px));
}

.outter-container {
  height: 100%;
  background-color: #eeeeee;
}

.outter-container .el-main {
  padding: 0;
}

.inner-container {
  height: var(--mainHeight);
  padding: 0;
}

.inner-container .el-aside {
  top: 0;
  bottom: 0;
  height: 100%;
}

.el-tabs {
  height: 100%;
}

.el-tabs--border-card {
  box-shadow: 0px 0px;
  border: 0px;
}

.el-tabs__item {
  font-size: 20px;
  font-weight: 1000;
}

p {
  font-family: "MicroSoft Yahei";
  font-size: 18px;
}

.content {
  margin: 0 0 0 0.7vw;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.el-textarea__inner {
  font-size: 18px;
  height: 100%;
}

#answerInput {
  height: calc(var(--mainHeight) * 0.61);
}

#ideaInput {
  height: calc(var(--mainHeight) * 0.38);
}

.el-footer {
  display: flex;
  padding: 0;
}

.quest-button {
  margin: auto auto;
  font-size: 20px;
}

#previous {
  margin-left: 30px;
  margin-right: 20px;
}

#next {
  margin-left: 30px;
}

.submit {
  margin-right: 20px;
}
</style>
