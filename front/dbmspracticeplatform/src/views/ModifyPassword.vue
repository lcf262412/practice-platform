<template>
  <!--试题内容页面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title4"><h1>修改密码</h1></div>
        <div slot="header" class="clearfix">
          <span>当前用户信息</span>
        </div>
        <div v-if="zhujiaoFlag">用户名: {{ zhujiaoName }}</div>
        <div v-else>用户名: {{ this.$store.state.username }}</div>
      </el-header>
    </div>
    <el-form :model="form" :rules="rules" ref="form">
      <el-form-item label="旧密码" prop="oldPwd" :label-width="formLabelWidth">
        <el-input v-model="form.oldPwd" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPwd" :label-width="formLabelWidth">
        <el-input
          v-model="form.newPwd"
          placeholder="请输入"
          type="password"
        ></el-input>
      </el-form-item>
      <el-form-item
        label="确认密码"
        prop="newPwdConfirm"
        :label-width="formLabelWidth"
      >
        <el-input
          v-model="form.newPwdConfirm"
          placeholder="请输入"
          @blur="checkNewPwd"
          type="password"
        ></el-input>
        <div v-show="changeAgainFlag">两次输入密码不一致！</div>
      </el-form-item>
    </el-form>

    <div>
      <el-button class="button1" @click="returnLast">返回</el-button>
      <el-button class="button2" type="primary" @click="modifyClick('form')"
        >确定</el-button
      >
    </div>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { modifyUserPwd, userLogout } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      rules: {
        oldPwd: [
          {
            required: true,
            message: "密码不能为空",
          },
          {
            max: 30,
            message: "密码长度不能超过30位",
          },
          {
            min: 8,
            message: "密码长度不能小于8位",
          },
        ],
        newPwd: [
          {
            required: true,
            message: "密码不能为空",
          },
          {
            max: 30,
            message: "密码长度不能超过30位",
          },
          {
            min: 8,
            message: "密码长度不能小于8位",
          },
        ],
        newPwdConfirm: [
          {
            required: true,
            message: "密码不能为空",
          },
          {
            max: 30,
            message: "密码长度不能超过30位",
          },
          {
            min: 8,
            message: "密码长度不能小于8位",
          },
        ],
      },

      form: {
        id: this.$store.state.username,
        oldPwd: "",
        newPwd: "",
        newPwdConfirm: "",
      },
      changeAgainFlag: false,
      formLabelWidth: "120px",
      zhujiaoFlag: false,
      zhujiaoName: "",
    };
  },
  mounted() {
    if (sessionStorage.getItem("selectRole")) {
      this.zhujiaoFlag = true;
      this.zhujiaoName = sessionStorage.getItem("selectRole");
    }
  },
  methods: {
    returnLast() {
      //返回上一页
      this.$router.go(-1);
    },
    modifyClick(formName) {
      //修改密码
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.form.newPwd != this.form.newPwdConfirm) {
            this.$alert("两次输入密码不一致");
          } else {
            if (this.zhujiaoFlag == true) {
              this.form.id = this.zhujiaoName;
            }
            modifyUserPwd(this.form).then((res) => {
              const { code, result } = res.data;
              if (code === "0000") {
                this.$alert("修改成功！", {
                  confirmButtonText: "确认",
                  callback: (action) => {
                    var id_t = "";
                    if (this.zhujiaoFlag == true) {
                      id_t = this.zhujiaoName;
                    } else {
                      id_t = this.$store.state.username;
                    }
                    userLogout({ id: id_t }).then((res) => {
                      const { code, result } = res.data;
                      if (code === "0000") {

                        this.$store.commit("resetState");
                        // 清理sessionStorage
                        sessionStorage.clear();
                        this.$router.push("/");
                      }
                    });
                  },
                });
              } else {
                this.$alert(result.errmessage);
              }
            });
          }
        } else {
          return false;
        }
      });
    },
    checkNewPwd() {
      //确认密码

      if (this.form.newPwd != this.form.newPwdConfirm) {
        this.changeAgainFlag = true;
      } else if (this.form.newPwd == this.form.newPwdConfirm) {
        this.changeAgainFlag = false;
      }
    },
  },
};
</script>

<style>
.el-header {
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
.page-title4 {
  margin-left: 20px;
}
.button1 {
  margin-left: 120px;
}
.button2 {
  margin-left: 10px;
}
</style>
