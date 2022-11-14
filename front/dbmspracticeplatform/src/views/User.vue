<!-- 没用，用来测试，地址是/vue/user -->
<template>
  <div>
    <div>I'm User {{ userName }}</div>
    <button @click="handleClick">测试</button>
  </div>
</template>

<script>
// qs将JSON转为key=value的形式
import qs from "qs";

// 解构出来data.js里的getMenu
import { getData } from "@/api/api.js";
import { getMenu } from "@/api/api.js";
import { modifyQuestionOrder } from "@/api/api.js";

export default {
  name: "User",
  data() {
    return {
      userName: "",
    };
  },
  mounted() {
    this.testFunc();
    window.addEventListener("beforeunload", this.solveBeforeUnload);
  },
  beforeDestroy() {
  },
  destroyed() {
    window.removeEventListener("beforeunload", this.solveBeforeUnload);
  },
  methods: {
    solveBeforeUnload() {},
    handleClick(e, n, t) {
    },
    test() {
      var jsonObj = { a: 1 };
      jsonObj.b = [];

      for (let i = 0; i < 10; i++) {
        jsonObj.b[i] = i;
      }

    },
    testFunc() {
      let a = {};
      let obj = a.temp?.data;
    },

    //import进来的方法需要在methods包装后供页面事件回调

    // Json转换为CSV的测试
    testJson2CSV() {
      JSonToCSV.setDataConver({
        data: [
          { name: "张三", amont: "323433.56", proportion: 33.4 },
          { name: "李四", amont: "545234.43", proportion: 55.45 },
        ],
        fileName: "test",
        columns: {
          title: [],
          key: [],
          formatter: function (n, v) {
            if (n === "amont" && !isNaN(Number(v))) {
              v = v + "";
              v = v.split(".");
              v[0] = v[0].replace(/(\d)(?=(?:\d{3})+$)/g, "$1,"); // 千分位的设置
              return v.join(".");
            }
            if (n === "proportion") return v + "%";
          },
        },
      });
    },
  },
};
</script>
