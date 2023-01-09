<template>
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <el-button
          class="addStudent"
          type="text"
          size="medium"
          @click="jumpToAdd"
          ><h1>添加学生</h1></el-button
        >
        <el-button
          class="addStudent"
          type="text"
          size="medium"
          @click="jumpToImport"
          ><h1>批量导入学生</h1></el-button
        >

        <el-button
          class="addStudent"
          type="text"
          size="medium"
          @click="(multipleSelectFlag = true), (multipleSelectMode = 1)"
          ><h1>批量禁用</h1></el-button
        >
        <el-button
          class="addStudent"
          type="text"
          size="medium"
          @click="(multipleSelectFlag = true), (multipleSelectMode = 2)"
          ><h1>批量激活</h1></el-button
        >
        <el-button
          class="addStudent"
          type="text"
          size="medium"
          @click="(multipleSelectFlag = true), (multipleSelectMode = 3)"
          ><h1>批量删除</h1></el-button
        >
        <el-button
          v-show="multipleSelectFlag"
          type="primary"
          @click="confirmSome"
          >确定</el-button
        >
        <el-button v-show="multipleSelectFlag" @click="cancelSome"
          >取消</el-button
        >
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
        :key="itemkey"
        :data="tableData"
        :fit="true"
        ref="table"
        style="width: 100%"
        @sort-change="sortChange"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          v-if="multipleSelectFlag"
          type="selection"
          :selectable="checkForbid"
          width="55"
        >
        </el-table-column>
        <el-table-column prop="name" label="姓名" sortable="custom">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="studentClick(scope.row)">{{
                scope.row.name
              }}</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="学号" sortable="custom">
        </el-table-column>
        <el-table-column prop="classId" label="班级" sortable="custom">
        </el-table-column>
        <el-table-column prop="grade" label="年级" sortable="custom">
        </el-table-column>

        <el-table-column
          prop="isactive"
          label="是否激活"
          sortable="custom"
          :formatter="formatBoolean"
        >
        </el-table-column>

        <el-table-column>
          <template slot-scope="scope">
            <el-button @click="initPwdClick(scope.row.id)">
              初始化密码</el-button
            >
          </template>
        </el-table-column>
        <el-table-column label="禁用后可删除">
          <template slot-scope="scope">
            <div v-show="scope.row.isactive">
              <a href="javascript:;" @click="setForbidClick(scope.row)">禁用</a>
            </div>
            <div v-show="!scope.row.isactive">
              <a href="javascript:;" @click="setActiveClick(scope.row)">激活</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <el-button
              v-show="!scope.row.isactive"
              @click="delClick(scope.row.id)"
            >
              删除</el-button
            >
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
        :modal-append-to-body="false"
        title="添加学生"
        :visible.sync="dialogFormVisible"
        @close="$reset('formReset')"
      >
        <el-form :model="form" :rules="rules" ref="formReset">
          <el-form-item prop="name" label="姓名" :label-width="formLabelWidth">
            <el-input v-model="form.name" placeholder="请输入"></el-input>
          </el-form-item>
          <el-form-item label="学号" prop="id" :label-width="formLabelWidth">
            <el-input
              v-model="form.id"
              placeholder="请输入"
              @blur="checkStudentId"
            ></el-input>
          </el-form-item>
          <el-form-item label="班级" prop="class" :label-width="formLabelWidth">
            <el-select
              v-model="form.class"
              placeholder="请选择班级"
              style="width: 496px"
            >
              <el-option
                v-for="item in allClass"
                :key="item.id"
                :label="item.id"
                :value="item.id"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="年级" prop="grade" :label-width="formLabelWidth">
            <el-select
              v-model="form.grade"
              placeholder="请选择年级"
              style="width: 496px"
            >
              <el-option
                v-for="item in allGrade"
                :key="item.year"
                :label="item.grade"
                :value="item.grade"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>

          <el-button type="primary" @click="addStudentClick('formReset')"
            >确 定</el-button
          >
        </div>
      </el-dialog>
      <el-dialog title="确认删除" :visible.sync="dialogFormVisible1">
        <div id="warning" style="font-size: 20px; color: red">
          将彻底删除此条记录，请谨慎操作！
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible1 = false">取 消</el-button>

          <el-button type="primary" @click="delStudentClick">确 定</el-button>
        </div>
      </el-dialog>
      <el-dialog
        title="导入数据"
        :visible.sync="showImportDialog"
        @close="clearImport"
      >
        <el-upload
          class="upload-demo"
          ref="upload"
          action="#"
          :file-list="fileList"
          :auto-upload="false"
          :limit="1"
          :on-change="handleChange"
        >
          <el-button slot="trigger" type="primary">选取文件</el-button>
          <el-button
            style="margin-left: 10px"
            type="success"
            @click="submitUpload"
            >上传</el-button
          >
          <el-select v-model="importClass" placeholder="请选择导入的班级">
            <el-option
              v-for="item in allClass"
              :key="item.id"
              :label="item.id"
              :value="item.id"
            >
            </el-option>
          </el-select>
          <div slot="tip" id="el-upload__tip">
            请按照下方所示模板上传xls文件，注意表头、顺序。一次只能上传一个文件！
          </div>
        </el-upload>
        <el-table
          :header-cell-style="{
            fontSize: '18px',
          }"
          :cell-style="{
            fontFamily: 'MicroSoft YaHei',
            fontSize: '18px',
          }"
          :data="tableData1"
          :fit="true"
          style="width: 100%"
        >
          <el-table-column prop="name" label="姓名"> </el-table-column>
          <el-table-column prop="id" label="学号"> </el-table-column>

          <el-table-column prop="grade" label="年级"> </el-table-column>
        </el-table>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";

import {
  getTeacherAllStudent,
  someStudentsForbid,
  someStudentsDelete,
  someStudentsActive,
} from "@/api/api.js";
import { getTeacherAllClass, initUserPwd } from "@/api/api.js";
import { addStudent } from "@/api/api.js";
import { delStudent, setStudentActive } from "@/api/api.js";
import {
  getStudentById,
  realDelStudent,
  getStudentByIdOrName,
} from "@/api/api.js";
import { getStudentByName, importStudents } from "@/api/api.js";
export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      itemkey: true,
      formReset: 0,
      rules: {
        name: [
          {
            required: true,

            message: "姓名不能为空",
          },
          {
            max: 30,
            message: "姓名不能超过30位",
          },
        ],
        id: [
          {
            required: true,

            message: "学号不能为空",
          },
          {
            max: 30,
            message: "学号不能超过30位",
          },
        ],
        class: [
          {
            required: true,
            max: 30,
            message: "班级不能为空",
          },
        ],
        grade: [
          {
            required: true,
            max: 30,
            message: "年级不能为空",
          },
        ],
      },
      req: {
        id: this.$store.state.username,
      },
      request: {
        id: "",
        name: "",
        grade: "",
        classId: "",
        dbName: "practicedb1",
      },
      studentId_Name: "",

      allClass: [],
      importClass: "",
      allGrade: [],
      currentPage: 1,
      pagesize: 50,
      total: 0,
      tableData: [],

      tableLable: {
        grade: "年级",
        id: "学生编号",
        name: "学生姓名",
        isactive: "是否激活",
        classId: "班级名称",
      },
      tableAllData: [],
      tableData1: [
        {
          id: "xxx",
          pwd: "xxx",
          name: "张三",
          grade: "20xx",
        },
      ],
      tableLable1: {
        id: "学号",
        pwd: "初始密码",
        name: "姓名",
        grade: "年级",
      },
      rowId: "",
      fileList: [],
      dialogFormVisible: false,
      dialogFormVisible1: false,
      showImportDialog: false,
      form: {
        name: "",
        id: "",
        grade: "",
        class: "",
      },
      multipleSelection: [],
      multipleSelectFlag: false,
      multipleSelectMode: 0,
      formLabelWidth: "120px",
    };
  },
  mounted() {
    this.getTeacherStudents();
    getTeacherAllClass({ teacherId: this.$store.state.username }).then(
      (res) => {
        const { code, result } = res.data;
        if (code === "0000") {

          this.allClass = result;
        }
      }
    );
  },

  methods: {
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

    //查询教师全部学生
    getTeacherStudents() {
      getTeacherAllStudent({ teacherId: this.$store.state.username }).then(
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
        this.getTeacherStudents();
      }
    },
    //通过数组对象的某个属性进行排序
    sortList(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (
          property == "name" ||
          property == "classId" ||
          property == "grade"
        ) {
          return a[property].localeCompare(b[property]);
        }
        return value1 - value2;
      };
    },

    //通过数组对象的某个属性进行倒序排列
    sortListDesc(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (
          property == "name" ||
          property == "classId" ||
          property == "grade"
        ) {
          return b[property].localeCompare(a[property]);
        }
        return value2 - value1;
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
      //this.getTeacherStudents()
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
    //初始化密码为12345678
    initPwdClick(id) {
      this.$confirm("是否初始化该学生密码？", "请谨慎操作", {
        confirmButtonText: "确认",
        cancelButtonText: "取消",
        callback: (action) => {
          if (action == "confirm") {
            initUserPwd({ id: id }).then((res) => {
              const { code, result } = res.data;
              if (code === "0000") {
                this.$alert("初始化成功", {
                  confirmButtonText: "确认",
                  callback: (action) => {
                    this.$router.go(0);
                  },
                });
              } else {
                this.$alert(result.errmessage);
              }
            });
          } else {
          }
        },
      });
    },
    jumpToAdd() {
      //点击添加学生
      this.dialogFormVisible = true;
      this.allGrade = [];
      let date = new Date();
      let year = date.getFullYear();
      for (let i = 0; i < 6; i++) {
        this.allGrade.push({ year: year - i + "", grade: year - i + "级" });
      }
    },
    checkStudentId() {
      //自动生成年级
      let grade = this.form.id.substring(0, 4);
      this.form.grade = "";

      for (let i = 0; i < this.allGrade.length; i++) {
        if (this.allGrade[i].year === grade) {
          this.form.grade = this.allGrade[i].grade;
          break;
        }
      }
    },
    studentClick(row) {
      //点击学生
      this.$router.push({
        path: "/studentAnswer",
        query: { studentId: row.id, studentName: row.name },
      });
    },
    addStudentClick(formName) {
      //确认添加学生
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.request.id = this.form.id;
          this.request.name = this.form.name;
          this.request.grade = this.form.grade;
          this.request.classId = this.form.class;

          addStudent(this.request).then((res) => {
            const { code, result } = res.data;
            if (code === "0000") {
              this.$alert("添加成功", {
                confirmButtonText: "确认",
                callback: (action) => {
                  this.$router.go(0);
                },
              });
            } else {
              this.$alert(result.detail);
            }
          });
          this.dialogFormVisible = false;
        } else {
          return false;
        }
      });
    },
    jumpToImport() {
      this.showImportDialog = true;
    },
    submitUpload() {
      //上传文件
      if (!this.importClass) {
        this.$alert("请选择班级！");
      } else if (this.fileList.length == 0) {
        this.$alert("请选择文件！");
      } else {
        var fd = new FormData();
        fd.append("file", this.fileList[0].raw);
        fd.append("classId", this.importClass);
        fd.append("dbName", "practicedb1");
        importStudents(fd).then((res) => {
          var { code, result } = res.data;
          if (code === "0000") {
            this.$alert("上传成功", {
              confirmButtonText: "确认",
              callback: (action) => {
                this.$router.go(0);
              },
            });
          } else {
            this.$alert(
              result.errmessage + "  " + "失败原因：" + result.detail
            );
          }
        });
      }
    },
    handleChange(file, fileList) {
      this.fileList = fileList;
    },
    clearImport() {
      this.importClass = "";
      this.fileList = [];
    },
    //取消批量操作
    cancelSome() {
      this.multipleSelectFlag = false;
      this.$refs["table"].clearSelection();
    },
    //确认批量操作
    confirmSome() {
      if (this.multipleSelection.length == 0) {
        this.$alert("请选择要操作的数据");
      } else {
        if (this.multipleSelectMode == 1) {
          let request = [];
          for (let item of this.multipleSelection) {
            request.push({ stuId: item.id, stuName: item.name });
          }
          someStudentsForbid(request).then((res) => {
            const { code, result } = res.data;
            if (code === "0000") {
              this.$alert("禁用成功", {
                confirmButtonText: "确认",
                callback: (action) => {
                  this.$router.go(0);
                },
              });
            } else {
              this.$alert("禁用失败");
            }
          });
        } else if (this.multipleSelectMode == 2) {
          let request = [];
          for (let item of this.multipleSelection) {
            request.push({ stuId: item.id, stuName: item.name });
          }
          someStudentsActive(request).then((res) => {
            const { code, result } = res.data;
            if (code === "0000") {
              this.$alert("激活成功", {
                confirmButtonText: "确认",
                callback: (action) => {
                  this.$router.go(0);
                },
              });
            } else {
              this.$alert("激活失败");
            }
          });
        } else {
          this.$confirm("将彻底删除选中记录，请谨慎操作！", {
            confirmButtonText: "确认",
            cancelButtonText: "取消",
            type: "warning",
            callback: (action) => {
              if (action == "confirm") {
                let request = [];
                for (let item of this.multipleSelection) {
                  request.push({ stuId: item.id, stuName: item.name });
                }
                someStudentsDelete(request).then((res) => {
                  const { code, result } = res.data;
                  if (code === "0000") {
                    this.$alert("删除成功", {
                      confirmButtonText: "确认",
                      callback: (action) => {
                        this.$router.go(0);
                      },
                    });
                  } else {
                    this.$alert(result.detail);
                  }
                });
              } else {
              }
            },
          });
        }
      }
    },
    //删除多选框选中事件
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    //复选框是否禁用
    checkForbid(row, rowIndex) {
      if (this.multipleSelectMode == 1) {
        if (row.isactive == true) {
          return true;
        } else {
          return false;
        }
      } else if (this.multipleSelectMode == 2) {
        if (row.isactive == false) {
          return true;
        } else {
          return false;
        }
      } else {
        if (row.isactive == false) {
          return true;
        } else {
          return false;
        }
      }
    },
    setForbidClick(row) {
      delStudent({ id: row.id }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("禁用成功", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    setActiveClick(row) {
      setStudentActive({ id: row.id }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("激活成功", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert(result.errmessage);
        }
      });
    },
    delClick(id) {
      this.dialogFormVisible1 = true;
      this.rowId = id;
    },
    delStudentClick() {
      realDelStudent({ id: this.rowId }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("删除成功", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert(result.detail);
        }
      });
    },
    clearClick() {
      getTeacherAllStudent({ teacherId: this.$store.state.username }).then(
        (res) => {
          const { code, result } = res.data;
          if (code === "0000") {

            this.tableData = result;
          }
        }
      );
    },
    searchStudent() {
      getStudentByIdOrName({
        nameorid: this.studentId_Name,
        teacherId: this.$store.state.username,
      }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.tableData = result;
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

.select-student {
  margin-left: 5px;
}
.addStudent {
  margin-left: auto;
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
  margin-bottom: 0px;
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
