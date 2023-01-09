<template>
  <!--教师用户管理页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>全部教师</h1></div>
        <el-button
          class="teacherButton"
          type="text"
          size="medium"
          @click="dialogFormVisible = true"
          ><h1>创建教师</h1></el-button
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
        <el-table-column prop="id" label="教师编号" sortable="custom">
        </el-table-column>
        <el-table-column prop="name" label="教师名称" sortable="custom">
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="delClick(scope.row.id)">删除</a>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-dialog
        title="创建教师"
        :visible.sync="dialogFormVisible"
        @close="$reset('formReset')"
      >
        <el-form :model="form" :rules="rules" ref="formReset">
          <el-form-item label="姓名" prop="name" :label-width="formLabelWidth">
            <el-input v-model="form.name" placeholder="请输入"></el-input>
          </el-form-item>

          <el-form-item label="编号" prop="id" :label-width="formLabelWidth">
            <el-input v-model="form.id" placeholder="请输入"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>

          <el-button type="primary" @click="addTeacherClick('formReset')"
            >确 定</el-button
          >
        </div>
      </el-dialog>
      <el-dialog title="确认删除" :visible.sync="dialogFormVisible1">
        <div id="warning" style="font-size: 20px; color: red">
          将彻底删除此条记录，操作！
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible1 = false">取 消</el-button>

          <el-button type="primary" @click="delTeacherClick">确 定</el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";

import { addNewTeacher, delTeacher, getTeachers } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      rules: {
        name: [
          {
            required: true,
            message: "姓名不能为空",
          },
          {
            max: 30,
            message: "姓名长度不能超过30位",
          },
        ],
        id: [
          {
            required: true,
            message: "教师编号不能为空",
          },
          {
            max: 30,
            message: "教师编号不能超过30位",
          },
        ],
      },
      tableData: [],
      tableLable: {
        id: "教师编号",
        name: "教师名称",
      },
      delRowId: "",
      formLabelWidth: "120px",
      dialogFormVisible: false,
      dialogFormVisible1: false,
      form: {
        id: "",
        name: "",
      },
    };
  },
  mounted() {
    this.getAllTeachers();
  },

  methods: {
    getAllTeachers() {
      getTeachers().then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {

          this.tableData = result;
        }
      });
    },
    //排序切换
    sortChange(column, prop, order) {
      if (column.order === "descending") {
        this.tableData.sort(this.sortListDesc(column.column.property));
      } else if (column.order === "ascending") {
        this.tableData.sort(this.sortList(column.column.property));
      } else {
        this.getAllTeachers();
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
    delClick(id) {
      (this.dialogFormVisible1 = true), (this.delRowId = id);
    },
    delTeacherClick() {
      //物理删除教师用户
      delTeacher({ id: this.delRowId }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("删除成功", {
            confirmButtonText: "确定",

            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    addTeacherClick(formName) {
      //添加教师
      this.$refs[formName].validate((valid) => {
        if (valid) {
          addNewTeacher({ id: this.form.id, name: this.form.name }).then(
            (res) => {
              const { code, result } = res.data;
              if (code === "0000") {

                this.$alert("创建成功", {
                  confirmButtonText: "确定",

                  callback: (action) => {
                    this.$router.go(0);
                  },
                });
              } else {
                this.$alert(result.detail);
              }
            }
          );
          this.dialogFormVisible = false;
        } else {
          return false;
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
.teacherButton {
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
