<template>
  <el-container>
    <el-header>
      <common-header></common-header>
    </el-header>
    <el-main class="main">
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
export default {
  components: {
    CommonHeader,
  },
  data() {
    return {};
  },
  methods: {
    changeNav() {
      var path = this.$route.path;
      this.data.activeIndex = path;
    },
  },
};
</script>

<style>
.el-container {
  height: 100%;
}
.el-header {
  padding: 0;
}
.main {
  padding: 0;
  height: 100%;
}
</style>
