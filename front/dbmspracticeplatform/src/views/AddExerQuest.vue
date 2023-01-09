<template>
  <!--添加试卷题目页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>添加试题</h1></div>
        <el-button
          class="publishExer"
          type="primary"
          size="medium"
          @click="returnExercise"
          >返回试卷详情</el-button
        >
        <h1>
          {{ this.$route.query.exerciseName }} No:{{
            this.$route.query.exerciseId
          }}
        </h1>
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
        <el-table-column prop="id" label="题目编号" width="120">
        </el-table-column>
        <el-table-column prop="name" label="出题教师姓名" width="120">
        </el-table-column>
        <el-table-column prop="dbName" label="数据库" width="120">
        </el-table-column>
        <el-table-column prop="title" label="题目标题">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="titleClick(scope.row)">{{
                scope.row.title
              }}</a>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="题目类型" width="120">
          <template slot-scope="scope">
            {{ classFormat(scope.row.questionClass) }}
          </template>
        </el-table-column>

        <el-table-column width="120">
          <template slot-scope="scope">
            <el-button @click="jumpToText(scope.row.id)"> 加入试卷</el-button>

            <el-dialog
              title="加入试卷"
              :visible.sync="dialogFormVisible"
              @close="$reset('formReset')"
            >
              <el-form :model="form" ref="formReset">
                <el-form-item
                  label="设置题目分数"
                  :label-width="formLabelWidth"
                  prop="score"
                >
                  <el-input
                    v-model="form.score"
                    placeholder="默认为10分,分值0-100"
                    @change="inputScore($event)"
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  <div>
                    <h3 v-if="isExist == 2">试卷已存在该试题</h3>
                    <h3 v-else-if="isExist == 1">试卷可添加该试题</h3>
                    <h3 v-else>正在查询是否可添加</h3>
                  </div>
                </el-form-item>
              </el-form>

              <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>

                <el-button type="primary" @click="addToExerClick"
                  >确 定</el-button
                >
              </div>
            </el-dialog>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { getAllQuestion } from "@/api/api.js";
import { addQuestInExer } from "@/api/api.js";
import { getAllExer } from "@/api/api.js";
import { getExerByQuest } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      itemkey: true,
      formReset: 0,
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
        id: this.$store.state.username,
      },
      request: {
        questionId: "",
        exerciseId: this.$route.query.exerciseId,
        score: 10,
      },
      allExer: [],
      questInExer: [],
      isExist: 0,
      tableData: [],
      tableLable: {
        id: "题目编号",
        name: "出题教师姓名",
        dbName: "数据库",
        title: "题目标题",
        questionClass: "题目类型",
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
    getAllQuestion().then((res) => {
      //获得全部试题信息

      const { code, result } = res.data;
      if (code === "0000") {
        this.tableData = result;
      }
    });
    getAllExer(this.req).then((res) => {
      //获得全部试卷信息

      this.allExer = res.data.result;
    });
  },

  methods: {
    titleClick(row) {
      //跳转到题目详情
      this.$router.push({
        path: "/questionContent",
        query: { questionId: row.id },
      });
    },
    classFormat: function (cellValue) {
      //格式化Boolean值
      var ret = ""; //你想在页面展示的值
      if (cellValue == 0) {
        return ret;
      }
      return this.questClass[cellValue - 1].class;
    },
    jumpToText(id) {
      //加入试卷显示弹窗
      this.dialogFormVisible = true;
      this.request.questionId = id;
      this.isExist = 0;
      getExerByQuest({ questionId: id }).then((res) => {
        const { code, result } = res.data;
        if (code == "0000") {
          this.questInExer = res.data.result;

          for (let i in this.questInExer) {
            if (this.questInExer[i].e_id == this.$route.query.exerciseId) {
              this.isExist = 2;
            }
          }
          if (this.isExist != 2) {
            this.isExist = 1;
          }
        }
      });
    },

    jumpToAdd() {
      //跳转到添加试题
      this.$router.push({ path: "/addQuestion" });
    },

    inputScore: function (event) {
      //绑定输入分数

      this.request.score = event;
    },

    addToExerClick() {
      //加入试卷

      if (!this.request.score) {
        this.request.score = 10;
      }
      if (this.request.score < 0 || this.request.score > 100) {
        this.$alert("分值不能小于0超过100！");
      } else {
        addQuestInExer(this.request)
          .then((res) => {
            this.$alert("添加成功");
            this.dialogFormVisible = false;
          })
          .catch((err) => {
            this.$alert("添加失败");
          });
      }
    },
    returnExercise() {
      //返回试卷详情
      this.$router.push({
        path: "/modifyExerQuest",
        query: {
          exerciseId: this.$route.query.exerciseId,
          exerciseName: this.$route.query.exerciseName,
        },
      });
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
