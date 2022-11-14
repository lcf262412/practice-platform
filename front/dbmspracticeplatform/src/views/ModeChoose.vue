<template>
  <div
    v-loading.fullscreen.lock="fullscreenLoading"
    element-loading-text="拼命加载中"
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(0, 0, 0, 0.9)"
  >
    <img id="background" src="../assets/login-background.jpg" />
    <el-row align="middle">
      <el-col
        v-if="
          this.$store.state.role === 'teacher' ||
          this.$store.state.role === 'student'
        "
      >
        <div class="grid-content bg-white" @click="jumpTo('/practice')">
          <img class="mode-icon" src="../assets/practice-mode.png" />
          <div class="mode-title">openGauss实践</div>
        </div>
      </el-col>
      <el-col
        v-if="
          this.$store.state.role === 'teacher' ||
          this.$store.state.role === 'student'
        "
      >
        <div
          class="grid-content bg-white"
          @click="jumpTo('/quest/choose-paper')"
        >
          <img class="mode-icon" src="../assets/test-mode.png" />
          <div class="mode-title">能力评估</div>
        </div>
      </el-col>
      <el-col v-if="this.$store.state.role === 'teacher'">
        <div
          class="grid-content bg-white"
          @click="jumpTo('/teacher/teacherMenu')"
        >
          <img class="mode-icon" src="../assets/teacher-mode.png" />
          <div class="mode-title">教师功能</div>
        </div>
      </el-col>
      <el-col v-if="this.$store.state.role === 'admin'">
        <div class="grid-content bg-white" @click="jumpTo('/admin/adminMenu')">
          <img class="mode-icon" src="../assets/admin-mode.png" />
          <div class="mode-title">系统管理</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { init } from "events";

export default {
  name: "ChooseMode",
  data() {
    return {
      fullscreenLoading: false,
    };
  },
  mounted() {},
  methods: {
    jumpTo(path) {
      // 如果是实践模式，需要根据权限判断是否进入教师数据库管理页

      this.fullscreenLoading = true;

      if (path === "/practice") {
        if (this.$store.state.role === "student") {
          this.$router.push(path);
        } else {
          this.$router.push("/db-manage");
        }
      } else {
        this.$router.push(path);
      }

      //成功回调函数停止加载
    },
  },
};
</script>

<style lang="css">
#background {
  display: flex;
  background: url("../assets/login-background.jpg");
  width: 100%;
  height: 100%;
  position: fixed;
  left: 0;
  top: 0;
  background-size: 100% 100%;
}
.el-row {
  display: flex;
  margin-top: 150px;
  width: 100%;
}
.bg-white {
  background: #ffffff;
}
.grid-content {
  margin: auto;
  border: 1px solid #835555;
  border-radius: 30px;
  height: 250px;
  width: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.row-bg {
  padding: 10px 0;
  background-color: #37496e;
}
.mode-icon {
  margin-top: 30%;
  width: 100px;
}
.mode-title {
  font-size: 20px;
  font-weight: 100;
  margin-top: 15%;
}
.loading {
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
}
</style>
