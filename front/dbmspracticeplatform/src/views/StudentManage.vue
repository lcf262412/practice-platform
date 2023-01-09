<template>
  <!--学生用户管理页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title_2"><h1>全部学生</h1></div>
      </el-header>
      <el-header class="paper-header">
        <h3 class="page-title_2">关键字:</h3>
        <div class="page-title_2">
          <el-input
            maxlength="30"
            placeholder="请输入学号/姓名查询"
            v-model="studentId_Name"
            clearable
            @clear="clearClick"
          >
          </el-input>
        </div>

        <el-button class="select-student" type="primary" @click="searchStudent"
          >搜索</el-button
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
        :fit="true"
        :data="tableData"
        @sort-change="sortChange"
        style="width: 100%"
      >
        <el-table-column prop="name" label="姓名" sortable="custom">
        </el-table-column>
        <el-table-column prop="id" label="学号" sortable="custom">
        </el-table-column>

        <el-table-column prop="grade" label="年级" sortable="custom">
        </el-table-column>
        <el-table-column prop="classId" label="班级编号" sortable="custom">
        </el-table-column>
        <el-table-column prop="t_id" label="教师编号" sortable="custom">
        </el-table-column>

        <el-table-column prop="t_name" label="教师姓名" sortable="custom">
        </el-table-column>

        <el-table-column
          prop="isactive"
          label="激活状态"
          :formatter="formatBoolean"
          sortable="custom"
        >
        </el-table-column>
        <el-table-column
          prop="hasTeacher"
          label="是否有教师权限"
          :formatter="formatBoolean1"
          sortable="custom"
        >
        </el-table-column>

        <el-table-column>
          <template slot-scope="scope">
            <div v-if="!scope.row.hasTeacher">
              <a href="javascript:;" @click="studentToTeacherClick(scope.row)"
                >授予教师权限</a
              >
            </div>
            <div v-if="scope.row.hasTeacher">
              <a href="javascript:;" @click="revokeClick(scope.row)"
                >撤销教师权限</a
              >
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
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";

import {
  getStudents,
  realDelStudent,
  delTeacher,
  getStudentByIdOrName,
  grandStudentTeacher,
  getStudentById,
  getStudentByName,
} from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      currentPage: 1,
      pagesize: 50,
      total: 0,
      tableAllData: [],
      tableData: [],
      tableLable: {
        t_id: "教师编号",
        classId: "班级编号",
        t_name: "教师姓名",
        grade: "年级",
        isactive: "激活状态",
        name: "学生姓名",
        id: "学生编号",
        hasTeacher: "是否有教师权限",
      },

      studentId_Name: "",
    };
  },
  mounted() {
    this.getAllStudents();
  },

  methods: {
    //查询全部学生
    getAllStudents() {
      getStudents().then((res) => {
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
        this.getAllStudents();
      }
    },
    //通过数组对象的某个属性进行排序
    sortList(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (property == "isactive" || property == "hasTeacher") {
          return value1 - value2;
        }
        return a[property].localeCompare(b[property]);
      };
    },

    //通过数组对象的某个属性进行倒序排列
    sortListDesc(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (property == "isactive" || property == "hasTeacher") {
          return value2 - value1;
        }
        return b[property].localeCompare(a[property]);
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

    formatBoolean: function (row, column, cellValue) {
      //格式化Boolean值
      var ret = ""; //你想在页面展示的值
      if (cellValue) {
        ret = "激活"; //根据自己的需求设定
      } else {
        ret = "未激活";
      }
      return ret;
    },
    formatBoolean1: function (row, column, cellValue) {
      //格式化Boolean值
      var ret = ""; //你想在页面展示的值
      if (cellValue) {
        ret = "是"; //根据自己的需求设定
      } else {
        ret = "否";
      }
      return ret;
    },
    clearClick() {
      //清空输入栏
      getStudents().then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {

          this.tableData = result;
        }
      });
    },
    searchStudent() {
      //搜索学生
      getStudentByIdOrName({ nameorid: this.studentId_Name }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.tableData = result;
        }
      });
    },
    studentToTeacherClick(row) {
      grandStudentTeacher({ id: row.id }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("授予权限成功！");
          this.$alert("授权成功", {
            confirmButtonText: "确定",

            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert(result.detail);
        }
      });
    },
    //撤销教师权限
    revokeClick(row) {
      delTeacher({ id: row.id }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("撤销成功", {
            confirmButtonText: "确定",

            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert(result.detail);
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
#teacherButton {
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
  margin-top: 5px;
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

.select-student {
  margin-left: 5px;
}
</style>
