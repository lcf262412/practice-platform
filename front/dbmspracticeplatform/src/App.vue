<template>
  <router-view />
</template>

<script>
import { initDynamicRoutes } from "@/router/index";
import { userLogout, logUpdate } from "@/api/api.js";

import qs from "qs";
import axios from "axios";

export default {
  name: "App",
  data() {
    return {
      _beforeUnload_time: 0,
      _gap_time: 0,
      closeT: false,
    };
  },
  mounted() {
    // 刷新更新计时器
    this.newValue = sessionStorage.getItem("username");
    
    if (this.newValue != null) {
      this.closeT = false;
      this.loginUpDate();
    }
    // 设置计时器触发监听
    window.addEventListener("setItem", (e) => {
      
      this.newValue = sessionStorage.getItem("username");
      if (this.newValue != "") {
        this.closeT = false;
        this.loginUpDate();
      }
    });
    // 设置计时器清除监听
    window.addEventListener("clear", (e) => {
    
      clearInterval(this.cancelTimes);
    });
   
  },
  watch: {
    // 计时器更新失败后不再更新
    closeT(newValue) {
      if (newValue == true) {
        
        clearInterval(this.cancelTimes);
      }
    },
  },
  created() {
    
    // 在页面加载时读取sessionStorage里的状态信息
    if (sessionStorage.getItem("store")) {
      this.$store.replaceState(
        Object.assign(
          {},
          this.$store.state,
          JSON.parse(sessionStorage.getItem("store"))
        )
      );
    }
   
    // 在页面刷新时将vuex里的信息保存到sessionStorage里
    // beforeunload事件在页面刷新时先触发

    window.addEventListener("beforeunload", (e) => {
      sessionStorage.setItem("store", JSON.stringify(this.$store.state));
      this.beforeunloadHandler(e);
    });

    window.addEventListener("unload", (e) => this.unloadHandler(e));
    // 在vue组件创建时调用添加动态路由的方法
  },
  destroyed() {
    window.removeEventListener("beforeunload", (e) => {
      this.beforeunloadHandler(e);
    });

    window.removeEventListener("unload", (e) => this.unloadHandler(e));
    
    
  },
  methods: {
    beforeunloadHandler(e) {
      const storage = window.localStorage;
      storage.clear();
      this._beforeUnload_time = new Date().getTime();
    },
    // 计时器
    loginUpDate() {
      
      return (this.cancelTimes = setInterval(() => {
        var date = new Date();
        var h = date.getHours();
        var m = date.getMinutes();
        var s = date.getSeconds();

        var userId = "";
        if (sessionStorage.getItem("selectRole")) {
          userId = sessionStorage.getItem("selectRole");
        } else {
          userId = this.$store.state.username;
        }
        
        logUpdate({ id: userId }).then((res) => {
          const { code, result } = res.data;
          if (code === "0000") {
            
          } else {
            this.$alert(result.errmessage);
            this.closeT = true;
          }
        });
      }, 60000));
    },
    unloadHandler(e) {
      this._gap_time = new Date().getTime() - this._beforeUnload_time;

      
      //判断是窗口关闭还是刷新
      if (this._gap_time <= 5) {
        //如果是登录状态，关闭窗口前，移除用户
        var id_t = "";
        clearInterval(this.loginUpDate);
        if (sessionStorage.getItem("selectRole")) {
          id_t = sessionStorage.getItem("selectRole");
        } else {
          id_t = this.$store.state.username;
        }
        var params = { id: id_t };
        var req = qs.stringify(params);

        fetch("/user/logout?" + req, {
          method: "get",

          headers: { "Content-Type": "application/json" },
          keepalive: true,
        });

      }
    },
  },
};
</script>

<style>
html,
body {
  margin: 0px;
  padding: 0px;
  height: 100%;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0px;
}
</style>
