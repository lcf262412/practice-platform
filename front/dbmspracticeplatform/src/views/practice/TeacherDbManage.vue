<template>
  <el-container class="paper-page">
    <el-header class="paper-header">
      <div class="page-title3"><h1>数据库连接管理</h1></div>
      <el-button
        style="margin-left: auto"
        type="primary"
        @click="handleCreateClick"
        >创建实践数据库</el-button
      >
    </el-header>
    <el-main class="paper-main">
      <el-tabs v-model="activeName">
        <el-tab-pane label="实践数据库">
          <el-table
            :data="practiceTableData"
            :header-cell-style="{
              fontSize: '18px',
            }"
            :cell-style="{
              fontFamily: 'MicroSoft YaHei',
              fontSize: '18px',
            }"
            :fit="true"
            style="width: 100%"
          >
            <el-table-column label="实践数据库" width="120">
              <template slot-scope="scope">
                <el-button @click="jumpToPractice(scope.row.name)"
                  >连接</el-button
                >
              </template>
            </el-table-column>
            <el-table-column width="120">
              <template slot-scope="scope">
                <el-button @click="deleteDB(scope.row)">删除</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="数据库名称" width="180">
            </el-table-column>
            <el-table-column width="200" prop="db_info" label="数据库备注信息">
            </el-table-column>
            <el-table-column width="200">
              <template slot-scope="scope">
                <el-button @click="intoQuestDBClick(scope.row.name)"
                  >生成答题数据库</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="答题数据库">
          <el-table
            :data="questTableData"
            :header-cell-style="{
              fontSize: '18px',
            }"
            :cell-style="{
              fontFamily: 'MicroSoft YaHei',
              fontSize: '18px',
            }"
            style="width: 100%"
          >
            <el-table-column label="答题数据库" width="120">
              <template slot-scope="scope">
                <el-button @click="jumpToPractice(scope.row.name)"
                  >连接</el-button
                >
              </template>
            </el-table-column>
            <el-table-column width="120">
              <template slot-scope="scope">
                <el-button @click="deleteJudgeDB(scope.row)">删除</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="数据库名称" width="180">
            </el-table-column>
            <el-table-column prop="describe" label="说明"> </el-table-column>
            <el-table-column width="120">
              <template slot-scope="scope">
                <el-button @click="modifyDescribe(scope.row)">修改</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <el-dialog
        title="创建实践数据库"
        :visible.sync="showCreateDB"
        @close="$reset('form')"
      >
        <el-form :model="form" :rules="rules" ref="form">
          <el-form-item
            label="数据库名称"
            prop="name"
            :label-width="formLabelWidth"
          >
            <el-input v-model="form.name" placeholder="请输入名称"></el-input>
          </el-form-item>
          <el-form-item
            label="数据库备注信息"
            prop="db_info"
            :label-width="formLabelWidth"
          >
            <el-input
              v-model="form.db_info"
              placeholder="请输入数据库备注信息"
            ></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="showCreateDB = false">取 消</el-button>
          <el-button type="primary" @click="createDB('form')">创建</el-button>
        </div>
      </el-dialog>

      <el-dialog
        title="生成答题数据库"
        :visible.sync="showIntoQuestDB"
        @close="$reset('form1')"
      >
        <el-form :model="form1" ref="form1">
          <el-form-item
            label="数据库元数据说明"
            prop="describe"
            :label-width="formLabelWidth"
          >
            <el-input
              v-model="form1.describe"
              type="textarea"
              autosize
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="showIntoQuestDB = false">取 消</el-button>
          <el-button type="primary" @click="intoQuestDB">创建</el-button>
        </div>
      </el-dialog>

      <el-dialog title="修改答题数据库说明" :visible.sync="showModifyQuestDB">
        <el-form :model="form2">
          <el-form-item label="数据库元数据说明" :label-width="formLabelWidth">
            <el-input
              v-model="form2.describe"
              type="textarea"
              autosize
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="showModifyQuestDB = false">取 消</el-button>
          <el-button type="primary" @click="modifyQuestDB">确定</el-button>
        </div>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script>
import {
  getDatabasesByteacherId,
  getJudgeDatabasesByteacherId,
  deleteDatabase,
  addDatabase,
  transferJudge,
  deleteJudgeDatabase,
  modifyJudgeDatabase,
} from "@/api/api";

export default {
  data() {
    return {
      rules: {
        name: [
          {
            required: true,
            message: "数据库名称不能为空！",
          },
          {
            max: 30,
            message: "数据库名称不能超过30位",
          },
        ],
      },
      practiceTableData: [],
      questTableData: [],
      classId: "",
      activeName: 0,
      showCreateDB: false,
      showIntoQuestDB: false,
      showModifyQuestDB: false,
      formLabelWidth: "130px",
      form: { name: "", db_info: "" },
      form1: {
        name: "",
        describe: "",
      },
      form2: { describe: "", name: "" },
    };
  },
  // 如何在同步方法里等待一个Promise对象的fullfilled？
  async mounted() {
    this.pracDatabaseList();
    this.questDatabaseList();
  },
  methods: {
    // 获取实践数据库列表
    pracDatabaseList() {
      let param = { teacherId: this.$store.state.username };
      getDatabasesByteacherId(param).then((res) => {
        var { code, result } = res.data;
        if (code === "0000") {
          // 实践数据库表内容
          let pracTableData = [];
          // 遍历结果表，获取类型为实践数据库的项
          for (let item of result) {
            if (item.dbClass === 1) {
              pracTableData.push(item);
            }
          }
          this.practiceTableData = pracTableData;
        }
      });
    },
    // 获取教师的答题数据库信息
    questDatabaseList() {
      let param = { teacherId: this.$store.state.username };
      getJudgeDatabasesByteacherId(param).then((res) => {
        var { code, result } = res.data;
        if (code === "0000") {
          this.questTableData = result;
        }
      });
    },

    //跳转至对应数据库的实践页面
    jumpToPractice(dbName) {
      sessionStorage.setItem("teacherDbName", dbName);
      this.$router.push({
        name: "practiceMode",
      });
    },
    // 打开创建实践数据库窗口
    handleCreateClick() {
      this.showCreateDB = true;
    },

    // 创建实践数据库
    createDB(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.showCreateDB = false;
          let params = {
            name: this.form.name,
            teacherId: this.$store.state.username,
            database_info: this.form.db_info,
          };
          addDatabase(params).then((res) => {
            let { code, message, result } = res.data;

            if (code === "0000") {
              this.$alert("创建实践数据库成功", "提示", {
                confirmButtonText: "确认",
                callback: (action) => {
                  this.pracDatabaseList();
                },
              });
            } else {
              this.$alert(result.errmessage, "失败", {
                confirmButtonText: "确认",
                callback: (action) => {},
              });
            }
          });
        } else {
          return false;
        }
      });
    },

    // 删除实践数据库
    deleteDB(row) {
      this.$confirm("是否删除实践数据库?", "请谨慎操作", {
        confirmButtonText: "确认",
        cancelButtonText: "取消",
        callback: (action) => {
          if (action == "confirm") {
            deleteDatabase({ name: row.name }).then((res) => {
              let { code, message, result } = res.data;
              if (code === "0000") {
                this.$alert("删除数据库成功", "提示", {
                  confirmButtonText: "确认",
                  callback: (action) => {
                    this.$router.go(0);
                  },
                });
              } else {
                this.$alert(result.errmessage, "错误", {
                  confirmButtonText: "确认",
                  callback: (action) => {},
                });
              }
            });
          }
        },
      });
    },
    // 删除答题数据库
    deleteJudgeDB(row) {
      this.$confirm("是否删除答题数据库？", "请谨慎操作", {
        confirmButtonText: "确认",
        cancelButtonText: "取消",
        callback: (action) => {
          if (action == "confirm") {
            deleteJudgeDatabase({ name: row.name }).then((res) => {
              let { code, message, result } = res.data;
              if (code === "0000") {
                this.$alert("删除数据库成功", "提示", {
                  confirmButtonText: "确认",
                  callback: (action) => {
                    this.questDatabaseList();
                  },
                });
              } else {
                this.$alert(result.errmessage, "错误", {
                  confirmButtonText: "确认",
                  callback: (action) => {},
                });
              }
            });
          } else {
          }
        },
      });
    },
    //生成答题数据库点击事件
    intoQuestDBClick(name) {
      this.form1.name = name;
      this.showIntoQuestDB = true;
    },
    //修改答题数据库说明点击事件
    modifyDescribe(row) {
      this.form2.name = row.name;
      this.showModifyQuestDB = true;
      this.form2.describe = row.describe;
    },
    //修改答题数据库说明
    modifyQuestDB() {
      modifyJudgeDatabase(this.form2).then((res) => {
        let { code, result } = res.data;
        if (code === "0000") {
          this.$alert("答题数据库说明修改成功", "提示", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.questDatabaseList();
            },
          });
        } else {
          this.$alert(result.errmessage, "提示", {
            confirmButtonText: "确认",
            callback: (action) => {},
          });
        }
        this.showModifyQuestDB = false;
      });
    },
    // 实践数据库转为答题数据库
    intoQuestDB() {
      transferJudge(this.form1).then((res) => {
        let { code, result } = res.data;
        if (code === "0000") {
          this.$alert("答题数据库生成成功", "提示", {
            confirmButtonText: "确认",
            callback: (action) => {
              this.$router.go(0);
            },
          });
        } else {
          this.$alert(result.errmessage, "提示", {
            confirmButtonText: "确认",
            callback: (action) => {},
          });
        }
        this.showIntoQuestDB = false;
      });
    },
  },
};
</script>

<style>
.el-main {
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
.paper-main {
  height: 570px;
  margin-top: 10px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 1px 1px 1px rgb(154, 154, 154);
}
.box-card {
  width: 99%;
}
.el-card__header {
  padding: 3px 20px;
}
</style>
