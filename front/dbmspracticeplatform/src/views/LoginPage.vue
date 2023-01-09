<!-- 首页，登录页面 -->

<template>
  <div
    v-loading="showLoad"
    id="background"
    src="../assets/login-background.jpg"
  >
    <img id="logo" src="../assets/logo-vertical.jpg" />
    <el-form
      :model="form"
      status-icon
      :rules="rules"
      ref="form"
      lable-width="100px"
      class="login-container"
    >
      <h3 class="login-title">登录</h3>
      <el-form-item
        label="账号"
        label-width="80px"
        prop="username"
        class="username"
      >
        <el-input
          autofocus
          type="input"
          v-model="form.username"
          autocomplete="off"
          placeholder="请输入账号"
          @keyup.enter.native="submitForm"
        ></el-input>
      </el-form-item>
      <el-form-item label="密码" label-width="80px" prop="password">
        <el-input
          type="password"
          v-model="form.password"
          autocomplete="off"
          placeholder="请输入密码"
          @keyup.enter.native="submitForm"
        ></el-input>
      </el-form-item>
      <el-form-item class="login-submit">
        <el-button
          type="primary"
          @click="submitForm"
          class="login-submit"
          :disabled="!canSubmit"
          >登录</el-button
        >
      </el-form-item>
    </el-form>
    <div>
      <el-dialog
        title="选择身份"
        :append-to-body="true"
        :visible.sync="selectRoleDialog"
        @close="returnToLogin"
      >
        <el-radio v-model="selectRole" label="1" border>学生</el-radio>
        <el-radio v-model="selectRole" label="2" border>助教</el-radio>
        <div slot="footer" class="dialog-footer">
          <el-button @click="selectRoleDialog = false">取 消</el-button>

          <el-button type="primary" @click="realLoad">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { login, userLogout } from "@/api/api.js";
import qs from "qs";

export default {
  name: "login",
  data() {
    return {
      form: {},
      rules: {
        username: [
          {
            required: true,
            message: "账号不能为空",
            trigger: "blur",
          },
          {
            required: true,
            max: 30,
            message: "名称长度不能超过30位",
          },
        ],
        password: [
          {
            required: true,
            message: "密码不能为空",
            trigger: "blur",
          },
          {
            required: true,
            max: 30,
            message: "密码长度不能超过30位",
          },
        ],
      },
      // 加载中效果控制
      showLoad: false,
      selectRoleDialog: false,
      selectRole: 0,
      username: "",
      teacherid: "",
    };
  },
  mounted() {
    // 清理vuex
    this.$store.commit("resetState");
    // 清理sessionStorage
    sessionStorage.clear();
    this.$resetSetItem("clear", "22222");
    localStorage.clear();
  },
  methods: {
    async submitForm() {
      var temp = {
        id: this.form.username,
        pwd: this.form.password,
      };

      var params = qs.stringify(temp);
      this.showLoad = true;
      login(params).then((res) => {
        var { code, message, result } = res.data;
        if (result.isactive == false) {
          this.$alert("学生用户已禁用", "登陆失败", {
            confirmButtonText: "确认",
            callback: (action) => {},
          });
        } else if (code === "0000") {
          if (result.userClass == 5) {
            this.selectRoleDialog = true;
            this.username = temp.id;
            this.teacherid = result.teacherid;
          } else {
            this.$store.commit("setUsername", temp.id);
            this.$resetSetItem("username", temp.id);
            sessionStorage.setItem("token", "1111");

            // 保存角色权限：1--管理员，2--教师，3--学生，4--访客，5--学生+教师，6--访客+教师
            switch (result.userClass) {
              case 1:
                this.$store.commit("setRole", "admin");
                // 管理员用户目前只开放管理功能，跳过模式选择

                break;
              case 2:
                this.$store.commit("setRole", "teacher");
                break;
              case 3:
                this.$store.commit("setRole", "student");
                break;
              case 4:
                this.$store.commit("setRole", "visitor");
                break;

              default:
                break;
            }
            if (result.userClass == 1) {
              this.$router.push("/admin/adminMenu");
            } else {
              this.$router.push("/choose");
            }
          }
        } else if (code === "1111") {
          // 登陆失败
          this.$alert(result.errmessage, "登陆失败", {
            confirmButtonText: "确认",
            callback: (action) => {},
          });
        }
      });
      this.showLoad = false;
    },
    //取消选择
    returnToLogin() {
      userLogout({ id: this.username }).then((res) => {
        const { code, result } = res.data;
        if (code === "0000") {

          this.$store.commit("resetState");

          // 清理sessionStorage
          sessionStorage.clear();
          this.$resetSetItem("clear", "22222");
          this.$router.go(0);
        }
      });
    },
    //选择后登录
    realLoad() {
      if (this.selectRole == 1) {
        this.$store.commit("setUsername", this.username);
        this.$resetSetItem("username", this.username);
        this.$store.commit("setRole", "student");
        sessionStorage.setItem("token", "1111");
        this.$router.push("/choose");
      } else if (this.selectRole == 2) {
        sessionStorage.setItem("selectRole", this.username);

        this.$store.commit("setUsername", this.teacherid);
        this.$resetSetItem("username", this.teacherid);
        this.$store.commit("setRole", "teacher");
        sessionStorage.setItem("token", "1111");
        this.$router.push("/choose");
      } else {
        this.$alert("请选择角色");
      }
    },
  },
  computed: {
    canSubmit() {
      const { username, password } = this.form;
      return Boolean(username && password);
    },
  },
};
</script>

<style lang="less" scoped>
#background {
  display: flex;
  align-items: center;
  background: url("../assets/login-background.jpg");
  width: 100%;
  height: 100%;
  position: fixed;
  left: 0;
  top: 0;
  background-size: 100% 100%;
}
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 15% auto;
  width: 350px;
  padding: 35px 35px 15px 35px;
  background-color: #ffffff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
  display: flex;
  flex-flow: column;
}
.login-title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}
.login-submit {
  margin: 10px auto 0px auto;
}
#logo {
  margin: 15%;
  height: 180px;
  position: relative;
  bottom: 10%;
}
</style>
