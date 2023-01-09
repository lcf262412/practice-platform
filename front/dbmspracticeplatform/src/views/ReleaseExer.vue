<template>
  <!--发布试卷页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <el-main class="paper-main">
      <el-descriptions title="试卷信息" :column="1" border>
        <el-descriptions-item>
          <template slot="label"> 试卷编号 </template>
          {{ this.$route.query.exerciseId }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label"> 试卷名称 </template>
          {{ this.$route.query.exerciseName }}
        </el-descriptions-item>
        <el-descriptions-item>
          <template slot="label"> 试卷说明 </template>
          {{ this.$route.query.describe }}
        </el-descriptions-item>
      </el-descriptions>
      <div class="paper-title5"><h3>试卷发布</h3></div>

      <el-form :model="form" label-width="120px" :rules="rules" ref="form">
        <el-form-item label="发布班级" prop="stuClassId">
          <el-select v-model="form.stuClassId" placeholder="请选择班级">
            <el-option
              v-for="item in allClass"
              :key="item.id"
              :label="item.id"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择日期时间"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择日期时间"
          >
          </el-date-picker>
        </el-form-item>

        <el-form-item label="是否考试" prop="isTest">
          <el-switch v-model="form.isTest"></el-switch>
        </el-form-item>
      </el-form>
      <div class="button">
        <el-button @click="returnLast">返回</el-button>
        <el-button type="primary" @click="releaseExercise('form')"
          >发布</el-button
        >
      </div>
      <div class="paper-title"><h3>已发布信息</h3></div>
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
        <el-table-column prop="stuClassId" label="班级名称" width="200">
        </el-table-column>
        <el-table-column prop="teacherId" label="班级所属教师编号" width="200">
        </el-table-column>
        <el-table-column
          prop="teacherName"
          label="班级所属教师名称"
          width="200"
        >
        </el-table-column>
        <el-table-column
          prop="isTest"
          label="是否考试"
          width="200"
          :formatter="formatBoolean"
        >
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="200">
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="200">
        </el-table-column>
        <el-table-column prop="semester" label="班级所属学期" width="200">
        </el-table-column>

        <el-table-column width="100">
          <template slot-scope="scope">
            <div>
              <a
                href="javascript:;"
                v-show="scope.row.cancelFlag"
                @click="delClassExerClick(scope.row)"
                >撤销发布</a
              >
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script>
import VueTagsInput from "@johmun/vue-tags-input";
import CommonHeader from "../components/CommonHeader.vue";
import { getExerAllContent, getExerUsedClass } from "@/api/api.js";
import { addClassExer, delClassExer } from "@/api/api.js";
import { getClassAllExer, getTeacherAllClass } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
    VueTagsInput,
  },
  data() {
    return {
      itemkey: true,
      req: {
        id: "wxb",
      },
      allClass: [],

      rules: {
        stuClassId: [
          {
            required: true,
            message: "班级编号不能为空",
          },
        ],
      },
      tableData: [],
      tableLable: {
        stuClassId: "班级编号",
        semester: "班级所属学期",
        startTime: "开始时间",
        endTime: "结束时间",
        isTest: "是否考试",
        teacherId: "班级所属教师编号",
        teacherName: "班级所属教师名称",

        cancelFlag: "是否可撤销",
      },

      form: {
        exerciseId: this.$route.query.exerciseId,
        stuClassId: "",
        startTime: "",
        endTime: "",
        isTest: "false",
      },

      formLabelWidth: "120px",
    };
  },
  mounted() {
    getExerUsedClass({
      teacherId: this.$store.state.username,
      exerciseId: this.$route.query.exerciseId,
    }).then((res) => {
      //获得已发布试卷信息
      const { code, result } = res.data;
      if (code === "0000") {
        this.tableData = result;
      }
    }),
      getTeacherAllClass({ teacherId: this.$store.state.username }).then(
        (res) => {
          //获得已发布试卷信息
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
        ret = "考试"; //根据自己的需求设定
      } else {
        ret = "练习";
      }
      return ret;
    },
    returnLast() {
      //返回上一页
      this.$router.go(-1);
    },
    getDateTime(datetime) {
      //组装时间
      var year = datetime.getFullYear();
      var month = datetime.getMonth() + 1;
      var strDate = datetime.getDate();
      var hour = datetime.getHours(); /*+8*/
      var minute = datetime.getMinutes();
      var second = datetime.getSeconds();
      var newdata =
        year +
        "-" +
        month +
        "-" +
        strDate +
        " " +
        hour +
        ":" +
        minute +
        ":" +
        second;
      return newdata;
    },
    releaseExercise(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.form.startTime && this.form.endTime) {
            if (this.form.startTime >= this.form.endTime) {
              this.$alert("开始日期处于结束日期之后！");
            } else {
              this.form.startTime = this.getDateTime(this.form.startTime);
              this.form.endTime = this.getDateTime(this.form.endTime);

              addClassExer(this.form).then((res) => {
                //添加班级试卷关系
                const { code, result } = res.data;
                if (code === "0000") {
                  this.$alert("发布成功", {
                    confirmButtonText: "确认",
                    callback: (action) => {
                      this.$router.go(0);
                    },
                  });
                } else {
                  this.$alert(result.state);
                }
              });
            }
          } else {
            this.$alert("请设置开始时间或结束时间！");
          }
        } else {
          return false;
        }
      });
    },
    delClassExerClick(row) {
      //删除班级试卷关系
      delClassExer({
        stuClassId: row.stuClassId,
        exerciseId: this.$route.query.exerciseId,
      }).then((res) => {
        //添加班级试卷关系
        const { code, result } = res.data;
        if (code === "0000") {
          this.$alert("撤销成功", {
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
  },
};
</script>

<style>
.el-header {
  padding: 0;
}
.button {
  margin-left: 120px;
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
.page-title5 {
  margin-left: 20px;
  margin-right: auto;
  font-size: medium;
}
.paper-main {
  margin-top: 10px;
  background-color: white;
  border-radius: 10px;

  box-shadow: 1px 1px 1px rgb(154, 154, 154);
}
</style>
