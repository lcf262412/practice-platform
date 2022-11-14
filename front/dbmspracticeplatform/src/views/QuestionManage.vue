<template>
  <!--题目管理页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>全部题目</h1></div>
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
        :fit="true"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="id" label="题目编号" width="120">
        </el-table-column>
        <el-table-column prop="title" label="题目标题" width="120">
        </el-table-column>
        <el-table-column prop="dbName" label="所属数据库" width="120">
        </el-table-column>
        <el-table-column prop="name" label="出题教师姓名"> </el-table-column>

        <el-table-column
          prop="questionClass"
          label="类型"
          :formatter="formatBoolean"
        >
        </el-table-column>
        <el-table-column
          prop="deleteflag"
          label="是否标记删除"
          :formatter="formatBoolean1"
        >
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="delClick(scope.row)">删除</a>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";

import { getAllQuestions, realDelQuest, delQuest } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      tableData: [],
      tableLable: {
        id: "题目编号",
        title: "题目标题",
        dbName: "所属数据库",
        questionClass: "题目类型",
        name: "出题教师姓名",
        deleteflag: "是否标记删除",
      },
    };
  },
  mounted() {
    getAllQuestions().then((res) => {
      //获取全部题目
      const { code, result } = res.data;
      if (code === "0000") {
        this.tableData = result;
      }
    });
  },

  methods: {
    formatBoolean: function (row, column, cellValue) {
      //格式化Boolean值
      var ret = ""; //你想在页面展示的值
      switch (cellValue) {
        case 1:
          ret = "select";
          break;
        case 2:
          ret = "DML";
          break;
        case 3:
          ret = "create table";
          break;
        case 4:
          ret = "alter table";
          break;
        case 5:
          ret = "create view";
          break;
        case 6:
          ret = "create index";
          break;
        case 7:
          ret = "create user/role";
          break;
        case 8:
          ret = "grant table";
          break;
        case 9:
          ret = "revoke table";
          break;
        case 10:
          ret = "grant user";
          break;
        case 11:
          ret = "revoke user";
          break;
        case 12:
          ret = "create procedure/function";
          break;
      }
      return ret;
    },
    formatBoolean1: function (row, column, cellValue) {
      //格式化Boolean值
      var ret = ""; //你想在页面展示的值
      if (cellValue) {
        ret = "已删除"; //根据自己的需求设定
      } else {
        ret = "未删除";
      }
      return ret;
    },

    delClick(row) {
      delQuest({ id: row.id }).then((res) => {
        //逻辑删除题目
        const { code, result } = res.data;
        if (code === "0000") {
          realDelQuest({ id: row.id }).then((res) => {
            //物理删除题目
            const { code, result } = res.data;
            if (code === "0000") {
              this.$router.go(0);
            }
          });
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
#addExercise {
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
