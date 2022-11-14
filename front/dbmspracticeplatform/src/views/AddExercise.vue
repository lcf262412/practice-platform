<template>
  <!--创建试卷页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>创建试卷</h1></div>
      </el-header>
    </div>
    <el-form ref="form" :model="form" label-width="120px" :rules="rules">
      <el-form-item label="试卷名称" prop="name">
        <el-input type="textarea" autosize v-model="form.name"></el-input>
      </el-form-item>

      <el-form-item label="试卷说明">
        <el-input type="textarea" autosize v-model="form.describe"></el-input>
      </el-form-item>
    </el-form>
    <div class="button">
      <el-button @click="returnLast">返回</el-button>
      <el-button type="primary" @click="addNewExercise('form')">创建</el-button>
    </div>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { addExercise } from "@/api/api.js";

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
            message: "试卷名称不能为空",
          },
          {
            max: 30,
            message: "试卷名称长度不能超过30位",
          },
        ],
      },
      form: {
        name: "",
        describe: "",
        teacherId: this.$store.state.username,
      },

      formLabelWidth: "120px",
    };
  },
  mounted() {},
  methods: {
    addNewExercise(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          addExercise(this.form).then((res) => {
            const { code, result } = res.data;
            if (code == "0000") {
              this.$alert("创建成功", {
                confirmButtonText: "确定",

                callback: (action) => {
                  this.$router.push("/exercise");
                },
              });
            } else {
              this.$alert("创建失败", result.errmessage);
            }
          });
        } else {
          return false;
        }
      });
    },

    returnLast() {
      this.$router.go(-1);
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
