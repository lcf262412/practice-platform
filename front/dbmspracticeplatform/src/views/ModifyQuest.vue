<template>
  <!--创建试题界面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>修改题目</h1></div>
      </el-header>
    </div>
    <el-form ref="form" :model="form" label-width="120px" :rules="rules">
      <el-form-item label="题目标题" prop="title">
        <el-input type="textarea" autosize v-model="form.title"></el-input>
      </el-form-item>
      <el-form-item label="数据库名称" prop="dbName">
        <el-select v-model="form.dbName" placeholder="请选择数据库">
          <el-option label="SPJ" value="SPJ"></el-option>
          <el-option label="Salary" value="Salary"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="questionClass">
        <el-select v-model="form.questionClass" placeholder="请选择类型">
          <el-option
            v-for="item in questClass"
            :key="item.id"
            :label="item.class"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="涉及对象" prop="targetName">
        <el-input type="textarea" autosize v-model="form.targetName"></el-input>
      </el-form-item>
      <el-form-item label="题目描述" prop="describe">
        <el-input
          type="textarea"
          autosize
          v-model="form.describe"
          disabled
        ></el-input>
      </el-form-item>
      <el-form-item label="题目详情" prop="content">
        <el-input type="textarea" autosize v-model="form.content"></el-input>
      </el-form-item>
      <el-form-item label="参考解析" prop="analysis">
        <el-input type="textarea" autosize v-model="form.analysis"></el-input>
      </el-form-item>
      <el-form-item label="参考答案" prop="answer">
        <el-input type="textarea" autosize v-model="form.answer"></el-input>
      </el-form-item>
      <el-form-item label="评判环境初始化" prop="initSQL">
        <el-input type="textarea" autosize v-model="form.initSQL"></el-input>
      </el-form-item>
    </el-form>
    <div class="button">
      <el-button @click="returnLast">取 消</el-button>
      <el-button type="primary" @click="modifyQuest('form')">确 定</el-button>
    </div>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { addNewQuestion } from "@/api/api.js";
import { getQuestionTeacher } from "@/api/api.js";
import { modifyQuestion } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    var checkTName = (rule, value, callback) => {
      if (this.form.questionClass != 1 && this.form.questionClass != "") {
        if (!value) {
          return callback(new Error("该类型题目涉及对象不能为空"));
        }
      }
      return callback();
    };
    var checkISQL = (rule, value, callback) => {
      let check = [4, 6, 8, 9, 10, 11];
      if (check.includes(this.form.questionClass)) {
        if (!value) {
          return callback(new Error("该类型题目评判环境初始化不能为空"));
        }
      }
      return callback();
    };
    return {
      rules: {
        title: [
          {
            required: true,
            message: "标题不能为空",
          },
          {
            max: 30,
            message: "题目标题不能超过30位",
          },
        ],
        dbName: [
          {
            required: true,
            message: "数据库名称不能为空",
          },
        ],
        questionClass: [
          {
            required: true,
            message: "类型不能为空",
          },
        ],
        targetName: [
          {
            validator: checkTName,
          },
        ],
        content: [
          {
            required: true,
            message: "题目详情不能为空",
          },
        ],
        answer: [
          {
            required: true,
            message: "答案不能为空",
          },
        ],
        initSQL: [
          {
            validator: checkISQL,
          },
        ],
      },
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
      form: {
        title: "",
        dbName: "",
        questionClass: "",
        targetName: "",
        content: "",
        analysis: "",
        answer: "",
        initSQL: "",
        teacherId: "",
        teacherName: "",
        describe: "",
      },
      req: {
        id: this.$route.query.questionId,
      },
      request: {
        id: this.$route.query.questionId,
        dbName: "",
        questionClass: "",
        title: "",
        content: "",
        targetName: "",
        answer: "",
        initSQL: "",
        analysis: "",
      },

      formLabelWidth: "120px",
    };
  },
  mounted() {
    getQuestionTeacher(this.req).then((res) => {
      //教师查询指定试题内容

      const { code, result } = res.data;
      if (code === "0000") {
        this.form = result;
      }
    });
  },
  methods: {
    modifyQuest(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.request.dbName = this.form.dbName;
          this.request.questionClass = this.form.questionClass;
          this.request.title = this.form.title;
          this.request.content = this.form.content;
          this.request.targetName = this.form.targetName;
          this.request.answer = this.form.answer;
          this.request.initSQL = this.form.initSQL;
          this.request.analysis = this.form.analysis;
          modifyQuestion(this.request)
            .then((res) => {
              const { code, result } = res.data;
              if (code == "0000") {
                this.$alert("修改成功", {
                  confirmButtonText: "确认",
                  callback: (action) => {
                    this.$router.push("/question");
                  },
                });
              }
            })
            .catch((err) => {
              this.$alert("添加失败");
            });
        } else {
          return false;
        }
      });
    },

    returnLast() {
      this.$router.push({ path: "/question" });
    },
  },
};
</script>

<style>
.el-header {
  padding: 0;
}
.button {
  margin-left: 120px;
  display: flex;
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
