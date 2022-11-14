<template>
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title1">
          <h1>
            班级:{{ this.$route.query.classId }} 总人数:{{ classStudentNum }}
          </h1>
        </div>
        <el-button
          v-show="allForbid != 0"
          class="addStudent"
          type="text"
          size="medium"
          @click="jumpToAdd"
          ><h1>添加学生</h1></el-button
        >
        <el-button
          v-show="allForbid != 0"
          class="addStudent"
          type="text"
          size="medium"
          @click="jumpToImport"
          ><h1>批量导入学生</h1></el-button
        >
        <el-button
          v-show="allForbid == 1 && classNotNull"
          class="addStudent"
          type="text"
          size="medium"
          @click="jumpToClear"
          ><h1>禁用班级全部学生</h1></el-button
        >
        <el-button
          v-show="allForbid == 2 && classNotNull"
          class="addStudent"
          type="text"
          size="medium"
          @click="jumpToActive"
          ><h1>激活班级全部学生</h1></el-button
        >
        <el-button
          v-show="allForbid == 2 && classNotNull"
          class="addStudent"
          type="text"
          size="medium"
          @click="jumpToWarning"
          ><h1>删除班级全部学生</h1></el-button
        >
        <el-button class="addStudent" type="primary" @click="returnClass"
          >返回班级列表</el-button
        >
      </el-header>
      <el-header class="paper-header">
        <h3 class="page-title2">关键字:</h3>
        <div class="page-title2">
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
        <el-button
          v-show="multipleSelectFlag"
          type="primary"
          @click="confirmSome"
          >确定</el-button
        >
        <el-button v-show="multipleSelectFlag" @click="cancelSome"
          >取消</el-button
        >
        <el-button
          v-show="classNotNull"
          class="addStudent"
          type="text"
          size="medium"
          @click="(multipleSelectFlag = true), (multipleSelectMode = 1)"
          ><h1>批量禁用</h1></el-button
        >
        <el-button
          v-show="classNotNull"
          class="addStudent"
          type="text"
          size="medium"
          @click="(multipleSelectFlag = true), (multipleSelectMode = 2)"
          ><h1>批量激活</h1></el-button
        >
        <el-button
          v-show="classNotNull"
          class="addStudent"
          type="text"
          size="medium"
          @click="(multipleSelectFlag = true), (multipleSelectMode = 3)"
          ><h1>批量删除</h1></el-button
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
        <el-table-column label="序号">
          <template slot-scope="scope">
            {{ scope.$index + 1 }}
          </template>
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
        <el-table-column prop="grade" label="年级" sortable="custom">
        </el-table-column>
        <el-table-column
          sortable="custom"
          prop="isactive"
          label="是否激活"
          :formatter="formatBoolean"
        >
        </el-table-column>

        <el-table-column>
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
          <el-form-item label="姓名" prop="name" :label-width="formLabelWidth">
            <el-input v-model="form.name" placeholder="请输入"></el-input>
          </el-form-item>
          <el-form-item label="学号" prop="id" :label-width="formLabelWidth">
            <el-input
              v-model="form.id"
              placeholder="请输入"
              @blur="checkStudentId"
            ></el-input>
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
          <el-button @click="(dialogFormVisible1 = false), (delAllFlag = false)"
            >取 消</el-button
          >

          <el-button type="primary" @click="delStudentClick">确 定</el-button>
        </div>
      </el-dialog>
      <el-dialog title="导入数据" :visible.sync="showImportDialog">
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
  getClassAllStudent,
  getStudentByIdOrName,
  getStudentById,
  getStudentByName,
  someStudentsForbid,
  someStudentsDelete,
  someStudentsActive,
} from "@/api/api.js";
import {
  addStudent,
  setStudentActive,
  realDelStudent,
  getStuDb,
  importStudents,
  downloadTemplate,
} from "@/api/api.js";
import {
  delStudent,
  forbidClassStudent,
  activeClassStudent,
  delClassStudent,
} from "@/api/api.js";
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
            message: "姓名长度不能超过30位",
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
        grade: [
          {
            required: true,
            message: "年级不能为空",
          },
        ],
      },
      multipleSelection: [],
      multipleSelectFlag: false,
      selectNotNull: false,
      multipleSelectMode: 0,
      classStudentNum: 0,
      allForbid: 0,
      classNotNull: true,
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
      currentPage: 1,
      pagesize: 50,
      total: 0,
      tableAllData: [],
      tableData: [],
      tableLable: {
        grade: "年级",

        id: "学生编号",
        name: "学生姓名",
        isactive: "是否激活",
      },
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
      dialogFormVisible: false,
      dialogFormVisible1: false,
      showImportDialog: false,
      rowId: "",
      fileList: [],
      dbName: "",
      dbNames: [],
      allGrade: [],
      delAllFlag: false,
      form: {
        name: "",
        id: "",
        grade: "",
      },

      formLabelWidth: "120px",
    };
  },
  created() {
    this.clearFlag = false;
  },
  mounted() {
    this.getClassStudents();
    this.getStudentDb();
  },

  methods: {
    //查询学生全部实践数据库
    getStudentDb() {
      getStuDb().then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.dbNames = result;
        }
      });
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
    //查询班级全部学生
    getClassStudents() {
      getClassAllStudent({ classId: this.$route.query.classId }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.tableAllData = result;
          this.tableData = result.slice(
            (this.currentPage - 1) * this.pagesize,
            this.currentPage * this.pagesize
          );
          this.total = result.length;
          if (result.length == 0) {
            this.classNotNull = false;
          }
          this.classStudentNum = result.length;
          for (let i = 0; i < result.length; i++) {
            if (result[i].isactive == true) {
              this.allForbid = 1;
              break;
            }
          }
          if (this.allForbid != 1) {
            this.allForbid = 2;
          }
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
        this.getClassStudents();
      }
    },
    //通过数组对象的某个属性进行排序
    sortList(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (property == "name" || property == "grade") {
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
        if (property == "name" || property == "grade") {
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
      //this.getTeacherStudents()
    },

    returnClass() {
      //返回班级列表
      this.$router.push("/class");
    },
    clearClick() {
      //清空输入栏
      getClassAllStudent({ classId: this.$route.query.classId }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.tableData = result;
        }
      });
    },
    searchStudent() {
      //搜索学生
      getStudentByIdOrName({
        nameorid: this.studentId_Name,
        classId: this.$route.query.classId,
      }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.tableData = result;
        }
      });
    },
    jumpToAdd() {
      this.dialogFormVisible = true;
      this.request.classId = this.$route.query.classId;
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
    jumpToImport() {
      this.showImportDialog = true;
    },
    submitUpload() {
      //上传文件
      var fd = new FormData();
      fd.append("file", this.fileList[0].raw);
      fd.append("classId", this.$route.query.classId);
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
          this.$alert(result.errmessage + "  " + "失败原因：" + result.detail);
        }
      });
    },
    downloadTemplate() {
      //下载模板
      downloadTemplate().then((res) => {
        const link = document.createElement("a");

        let blob = new Blob([res.data], {
          type: "application/vnd.ms-excel,charset=utf-8",
        });
        let _fileName = "importStudentTemplates.xls";
        link.style.display = "none";
        link.href = window.URL.createObjectURL(blob);
        link.download = _fileName;
        document.body.appendChild(link);
        link.click();
        // 释放内存
        document.body.removeChild(link);
        window.URL.revokeObjectURL(link.href);
      });
    },
    jumpToClear() {
      forbidClassStudent({ classId: this.$route.query.classId }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("禁用成功", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.$router.go(0);
            },
          });
        }
      });
    },
    jumpToActive() {
      activeClassStudent({ classId: this.$route.query.classId }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("激活成功", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.$router.go(0);
            },
          });
        }
      });
    },
    jumpToWarning() {
      this.dialogFormVisible1 = true;
      this.delAllFlag = true;
    },

    handleChange(file, fileList) {
      this.fileList = fileList;
    },
    studentClick(row) {
      this.$router.push({
        path: "/studentAnswer",
        query: { studentId: row.id, studentName: row.name },
      });
    },
    addStudentClick(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.request.id = this.form.id;
          this.request.name = this.form.name;
          this.request.grade = this.form.grade;
          addStudent(this.request).then((res) => {
            const { code, result } = res.data;
            if (code === "0000") {
              this.$alert("添加成功", {
                confirmButtonText: "确认",
                callback: (action) => {
                  this.tableData = result;
                  this.$router.go(0);
                },
              });
            } else {
              this.$alert(result.errmessage);
            }
            this.dialogFormVisible = false;
          });
        } else {
          return false;
        }
      });
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
      if (this.delAllFlag == true) {
        delClassStudent({ classId: this.$route.query.classId }).then((res) => {
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
      }
    },
  },
};
</script>

<style>
.el-header {
  padding: 0;
}
#addQuestion {
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
.page-title2 {
  margin-left: 5px;
  margin-right: 5px;
}
.page-title1 {
  margin-left: 5px;
  margin-right: auto;
}
.paper-main {
  margin-top: 10px;
  background-color: white;
  border-radius: 10px;

  box-shadow: 1px 1px 1px rgb(154, 154, 154);
}

.addStudent {
  display: flex;
  justify-content: flex-end;
}
.select-student {
  margin-left: 5px;
  margin-right: auto;
}
#el-upload__tip {
  font-size: 20px;
}
</style>
