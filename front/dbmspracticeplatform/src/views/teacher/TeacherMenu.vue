<template>
  <div>
    <img id="background" src="../../assets/login-background.jpg" />
    <el-row>
      <el-col :span="6">
        <div class="grid-content bg-white" @click="jumpTo('/class')">
          <img class="mode-icon" src="../../assets/class.png" />
          <div class="mode-title">班级管理</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="grid-content bg-white" @click="jumpTo('/student')">
          <img class="mode-icon" src="../../assets/student.png" />
          <div class="mode-title">学生管理</div>
        </div>
      </el-col>

      <el-col :span="6">
        <div
          v-show="true"
          class="grid-content bg-white"
          @click="jumpTo('/question')"
        >
          <img class="mode-icon" src="../../assets/question.png" />
          <div class="mode-title">试题管理</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div
          v-show="true"
          class="grid-content bg-white"
          @click="jumpTo('/exercise')"
        >
          <img class="mode-icon" src="../../assets/exercise.png" />
          <div class="mode-title">试卷管理</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "ChooseMode",
  data() {
    return {};
  },
  methods: {
    jumpTo(path) {
      this.$router.push(path);
    },
  },
};
</script>

<style lang="css">
#background {
  display: flex;
  background: url("../../assets/login-background.jpg");
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
  border: 1px solid #bbbbbb;
  border-radius: 30px;
  height: 250px;
  width: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.row-bg {
  padding: 10px 0;
  background-color: #426ab9;
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
</style>
