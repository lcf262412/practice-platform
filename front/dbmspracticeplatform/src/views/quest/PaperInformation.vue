<template>
  <el-container class="paper-page">
    <el-header class="paper-header">
      <div class="page-title3"><h1>试题选择</h1></div>
      <el-button class="back-list-btn" @click="backToPaperList" round
        >返回试卷列表</el-button
      >
    </el-header>
    <el-main class="paper-main">
      <el-table
        :header-cell-style="{
          fontSize: '18px',
        }"
        :cell-style="{
          fontFamily: 'MicroSoft YaHei',
          fontSize: '18px',
        }"
        :data="tableData"
        height="100%"
        style="width: 100%"
        @row-click="handleRowClick"
      >
        <el-table-column prop="orderId" label="题目序号" width="120">
        </el-table-column>

        <el-table-column prop="dbName" label="数据库" width="120">
        </el-table-column>
        <el-table-column
          prop="title"
          label="题目"
          :show-overflow-tooltip="true"
        ></el-table-column>
        <el-table-column prop="zscore" label="题目总分" width="120">
        </el-table-column>
        <el-table-column prop="sscore" label="题目得分" width="120">
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
import { getPapaerQuestion, getExerAllContents } from "@/api/api.js";

export default {
  data() {
    return {
      tableData: [],
      paperId: sessionStorage.getItem("paperId"),
    };
  },
  mounted() {
    this.getPapaerInformation();
  },
  methods: {
    // 获取试卷信息
    getPapaerInformation() {
      let param = {
        exerciseId: this.paperId,
        stuId: this.$store.state.username,
      };
      getExerAllContents(param).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.tableData = result;
          this.paperLength = result.length;
        }
      });
    },
    // 表格行点击事件回调方法
    handleRowClick(row) {
      // 跳转至对应题号的答题界面
      sessionStorage.setItem("questionId", row.questionId);
      // 记录一下试卷末尾序号，以在答题页面做翻页限制
      sessionStorage.setItem("paperLength", this.paperLength);
      this.$router.push({
        path: "/quest/answer-question",
        query: { orderId: row.orderId },
      });
    },
    // 返回试卷列表
    backToPaperList() {
      this.$router.push("/quest/choose-paper");
    },
  },
};
</script>

<style>
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
