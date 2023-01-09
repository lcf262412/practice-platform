<template>
  <!--试题内容页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>题目详情</h1></div>
      </el-header>
    </div>
    <el-descriptions :column="1" border>
      <el-descriptions-item>
        <template slot="label">题目编号(题名)</template>
        {{ this.$route.query.questionId }} </el-descriptions-item
      >>
      <el-descriptions-item>
        <template slot="label">数据库(大题名)</template>
        {{ descriptions.dbName }} </el-descriptions-item
      >>
      <el-descriptions-item>
        <template slot="label">类型</template>
        {{ this.nowQuestClass }} </el-descriptions-item
      >>
      <el-descriptions-item>
        <template slot="label">涉及对象</template>
        {{ descriptions.targetName }} </el-descriptions-item
      >>
      <el-descriptions-item>
        <template slot="label">题目描述</template>
        {{ descriptions.describe }} </el-descriptions-item
      >>
      <el-descriptions-item>
        <template slot="label">题目</template>
        {{ descriptions.content }} </el-descriptions-item
      >>
      <el-descriptions-item>
        <template slot="label">参考解析</template>
        {{ descriptions.analysis }} </el-descriptions-item
      >>
      <el-descriptions-item>
        <template slot="label">参考答案</template>
        {{ descriptions.answer }} </el-descriptions-item
      >>
      <el-descriptions-item>
        <template slot="label">评判环境初始化</template>
        {{ descriptions.initSQL }} </el-descriptions-item
      >>
    </el-descriptions>

    <div>
      <el-button @click="returnLast" style="padding-right: 25px"
        >返回</el-button
      >
    </div>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { getQuestionTeacher } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      nowQuestClass: "",
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
          class: "revoke user",
        },
        {
          id: 12,
          class: "create procedure/function",
        },
      ],
      req: {
        id: this.$route.query.questionId,
      },
      descriptions: {
        title: "",
        dbName: "",
        name: "",
        questionClass: "",
        targetName: "",
        content: "",
        analysis: "",
        answer: "",
        describe: "",
        initSQL: "",
      },

      formLabelWidth: "120px",
    };
  },
  mounted() {
    getQuestionTeacher(this.req).then((res) => {
      //教师查询指定试题内容

      const { code, result } = res.data;
      if (code === "0000") {
        this.descriptions = result;
      }
      if (this.descriptions.questionClass > 0) {
        this.nowQuestClass =
          this.questClass[this.descriptions.questionClass - 1].class;
      }
    });
  },
  methods: {
    returnLast() {
      //返回上一页
      this.$router.go(-1);
    },
  },
};
</script>

<style>
.el-header {
  padding: 0;
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
</style>
