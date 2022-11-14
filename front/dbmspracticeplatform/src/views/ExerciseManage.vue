<template>
  <!--试卷管理页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>全部试卷</h1></div>
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
        <el-table-column prop="e_id" label="试卷编号" width="120">
        </el-table-column>
        <el-table-column prop="e_name" label="试卷名称" width="120">
        </el-table-column>
        <el-table-column prop="t_name" label="创建教师" width="120">
        </el-table-column>
        <el-table-column prop="t_id" label="创建教师编号"> </el-table-column>
        <el-table-column prop="describe" label="说明"> </el-table-column>
        <el-table-column
          prop="isPublic"
          label="状态"
          :formatter="formatBoolean"
        >
        </el-table-column>
        <el-table-column
          prop="deleteflag"
          label="是否删除"
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

import { getAllExercises, realDelExer, delExer } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      tableData: [],
      tableLable: {
        e_id: "试卷编号",
        e_name: "试卷名称",
        t_name: "创建教师",
        t_id: "创建教师编号",
        describe: "说明",
        isPublic: "状态",
        deleteflag: "是否删除",
      },
    };
  },
  mounted() {
    getAllExercises().then((res) => {
      //获取全部试卷
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
      if (cellValue) {
        ret = "公开"; //根据自己的需求设定
      } else {
        ret = "未公开";
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
      delExer({ id: row.e_id }).then((res) => {
        //逻辑删除试卷
        const { code, result } = res.data;
        if (code === "0000") {
          realDelExer({ id: row.e_id }).then((res) => {
            //物理删除试卷
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
