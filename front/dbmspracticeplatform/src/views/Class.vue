<template>
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>班级管理</h1></div>
        <el-button
          class="addQuestion"
          type="text"
          size="medium"
          @click="jumpToAdd"
          ><h1>添加班级</h1></el-button
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
        :fit="true"
        @sort-change="sortChange"
        style="width: 100%"
      >
        <el-table-column prop="id" label="班级名称" sortable="custom">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="studentClick(scope.row)">{{
                scope.row.id
              }}</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" sortable="custom">
        </el-table-column>

        <el-table-column sortable="fasle">
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
      <el-dialog
        title="创建班级"
        :visible.sync="dialogFormVisible"
        @close="$reset('formReset')"
      >
        <el-form :model="form" :rules="rules" ref="formReset">
          <el-form-item label="班级" prop="id" :label-width="formLabelWidth">
            <el-input v-model="form.id" placeholder="请输入班级"></el-input>
          </el-form-item>
          <el-form-item
            label="学期"
            prop="semester"
            :label-width="formLabelWidth"
          >
            <el-select
              v-model="form.semester"
              placeholder="请选择学期"
              style="width: 496px"
            >
              <el-option
                v-for="item in allSemester"
                :key="item.semester"
                :label="item.semester"
                :value="item.semester"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>

          <el-button type="primary" @click="addClassClick('formReset')"
            >添加</el-button
          >
        </div>
      </el-dialog>
      <el-dialog title="确认删除" :visible.sync="dialogFormVisible1">
        <div id="warning" style="font-size: 20px; color: red">
          将彻底删除此条记录，请谨慎操作！
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible1 = false">取 消</el-button>

          <el-button type="primary" @click="delClassClick">确 定</el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";

import { addQuestInExer } from "@/api/api.js";
import { addClass, delClass } from "@/api/api.js";
import { getExerByQuest } from "@/api/api.js";
import { getTeacherAllClass } from "@/api/api.js";
export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      itemkey: true,
      rules: {
        id: [
          {
            required: true,

            message: "班级编号不能为空",
          },
          {
            max: 30,
            message: "班级编号不能超过30位",
          },
        ],
        semester: [
          {
            required: true,
            message: "学期不能为空",
          },
        ],
      },
      req: {
        id: this.$store.state.username,
      },
      request: {
        teacherId: "",
        id: "",
        semester: "",
      },

      currentPage: 1,
      pagesize: 50,
      total: 0,
      tableAllData: [],
      tableData: [],
      tableLable: {
        semester: "学期",
        id: "班级名称",
      },
      rowId: "",
      dialogFormVisible: false,
      dialogFormVisible1: false,
      form: {
        id: "",
        semester: "",
      },
      allSemester: [],
      formLabelWidth: "120px",
    };
  },
  mounted() {
    this.getAllClass();
  },

  methods: {
    getAllClass() {
      getTeacherAllClass({ teacherId: this.$store.state.username }).then(
        (res) => {
          const { code, result } = res.data;
          if (code === "0000") {
            this.tableAllData = result;
            this.tableData = result.slice(
              (this.currentPage - 1) * this.pagesize,
              this.currentPage * this.pagesize
            );
            this.total = result.length;
          }
        }
      );
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
        this.getAllClass();
      }
    },
    //通过数组对象的某个属性进行排序
    sortList(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];

        return a[property].localeCompare(b[property]);
      };
    },

    //通过数组对象的某个属性进行倒序排列
    sortListDesc(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];

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

    jumpToAdd() {
      //创建班级显示弹窗
      this.dialogFormVisible = true;
      this.request.teacherId = this.$store.state.username;
      this.allSemester = [];
      let date = new Date();
      let year = date.getFullYear();
      let flag = 0;
      let month = date.getMonth() + 1;
      if (month >= 9) {
        flag = 1;
      }
      for (let i = 0; i < 6; i++) {
        if (flag == 1) {
          this.allSemester.push({
            year: year - i,
            semester: year - i + "年秋",
          });
          this.allSemester.push({
            year: year - i,
            semester: year - i + "年春",
          });
        } else {
          this.allSemester.push({
            year: year - i,
            semester: year - i + "年春",
          });
          this.allSemester.push({
            year: year - i,
            semester: year - i + "年秋",
          });
        }
      }
    },
    addClassClick(formName) {
      //创建班级
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.request.id = this.form.id;
          this.request.semester = this.form.semester;
          addClass(this.request).then((res) => {
            const { code, result } = res.data;
            if (code === "0000") {
              this.$router.go(0);

            } else {
              this.$alert(result.detail);
            }
            this.dialogFormVisible = false;
          });
        } else {
          return false;
        }
      });
    },
    delClick(id) {
      this.dialogFormVisible1 = true;
      this.rowId = id;
    },
    delClassClick() {
      delClass({ id: this.rowId }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$router.go(0);
        } else {
          this.$alert(result.errmessage);
        }
      });
    },

    studentClick(row) {
      //跳转到班级详情
      this.$router.push({ path: "/classStudent", query: { classId: row.id } });
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
