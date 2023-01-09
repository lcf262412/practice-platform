<template>
  <!--试卷详情页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3">
          <h1>
            {{ this.$route.query.exerciseName }} No:{{
              this.$route.query.exerciseId
            }}
          </h1>
        </div>
        <el-button
          class="publishExer"
          type="primary"
          size="medium"
          @click="returnExercise"
          >返回试卷列表</el-button
        >
        <el-button
          class="publishExer"
          type="text"
          size="medium"
          @click="jumpToRelease"
          ><h1>发布试卷</h1></el-button
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
      >
        <el-table-column prop="orderId" label="试题序号" width="120">
        </el-table-column>
        <el-table-column prop="questionId" label="试题编号" width="120">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="titleClick(scope.row)">{{
                scope.row.questionId
              }}</a>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="title" label="题目"> </el-table-column>
        <el-table-column
          prop="dbName"
          label="大题名称(数据库名称)"
          width="150px"
        >
        </el-table-column>
        <el-table-column prop="score" label="分值" width="150px">
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { getExerAllContent } from "@/api/api.js";
import { addQuestInExer } from "@/api/api.js";
import { getAllExer } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      itemkey: true,
      req: {
        id: "wxb",
      },
      request: {
        exerciseId: this.$route.query.exerciseId,
      },

      allExer: [],
      tableData: [],
      tableLable: {
        questionId: "试题编号",
        orderId: "试题序号",
        questionClass: "试题类别",
        dbName: "大题名称(数据库)",
        title: "试题标题",
        score: "分值",
      },
      dialogFormVisible: false,
      form: {
        exercise: "",
        score: "",
      },

      formLabelWidth: "120px",
    };
  },
  mounted() {
    getExerAllContent(this.request).then((res) => {
      //获得试卷全部试题信息
      const { code, result } = res.data;
      if (code === "0000") {
        this.tableData = result;
      }
    });
  },

  methods: {
    titleClick(row) {
      //跳转到题目详情

      this.$router.push({
        path: "/questionContent",
        query: { questionId: row.questionId },
      });
    },
    jumpToRelease() {
      //跳转到发布试卷
      this.$router.push({
        path: "/releaseExer",
        query: {
          exerciseId: this.$route.query.exerciseId,
          exerciseName: this.$route.query.exerciseName,
          describe: this.$route.query.describe,
        },
      });
    },
    returnExercise() {
      //返回试卷列表
      this.$router.push({ path: "/exercise" });
    },
  },
};
</script>

<style>
.el-header {
  padding: 0;
}
.publishExer {
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
