<template>
  <div class="sql-result">
    <div v-if="!dataList || dataList.length === 0">
      {{ statusText }}
    </div>
    <el-tabs
      v-else
      v-model="dataList[0].activeTabName"
      @tab-click="closeAll"
      @tab-remove="removeResult"
    >
      <el-tab-pane name="关闭所有" label="关闭所有"></el-tab-pane>
      <el-tab-pane name="执行情况" label="执行情况">
        执行情况:
        <div v-if="!errorFlag">执行成功！</div>
        <div v-else v-for="(item, index) in errorResults" :key="index">
          第{{ item }}条语句出错，具体原因请查看相应标签页！
        </div>
      </el-tab-pane>
      <el-tab-pane
        v-for="(item, index) in dataList"
        :key="index"
        :name="index + ''"
        closable
      >
        <span slot="label" v-if="item.ResultType == 'ERROR'" style="color: red"
          >语句{{ index + 1 }}</span
        >
        <span slot="label" v-else style="color: black"
          >语句{{ index + 1 }}</span
        >
        <vue-lazy-component>
          <one-result
            :dataList="[item, index, dataList[0]]"
            :changeEdit="changeEdit"
            :sqlold="sql_old"
            @execClick1="doExecuteSql"
            @flagClick="isEdit"
            @sqloldClick="getSql"
            @returnClick="clearSql"
          >
          </one-result>
        </vue-lazy-component>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { table2Excel, JSonToCSV } from "@/util/utils";
import { init } from "events";
import OneResult from "@/components/OneResult.vue";
import { component as VueLazyComponent } from "@xunlei/vue-lazy-component";

export default {
  components: { OneResult, "vue-lazy-component": VueLazyComponent },
  name: "execResultView",
  setup() {},
  props: ["dataList", "changeEdit"],
  data() {
    return {
      statusText: "请在输入框输入sql，点击运行，即可查看运行结果",
      sql_old: "",
      lazy: false,
      errorResults: [],
      errorFlag: false,
    };
  },

  mounted() {},
  watch: {
    dataList(oldValue, newValue) {
      this.errorResults = [];
      this.errorFlag = false;
      for (let i in oldValue) {
        if (oldValue[i].ResultType == "ERROR") {
          this.errorResults.push(parseInt(i) + 1);
          this.errorFlag = true;
        }
      }
    },
  },
  methods: {
    //关闭标签页
    removeResult(target) {
      this.$emit("tabsChange", target);
    },
    //关闭全部标签页
    closeAll(tab) {
      if (tab.name == "关闭所有") {
        let target = "all";
        this.$emit("tabsChange", target);
      } else {
        this.lazy = true;
      }
    },
    doExecuteSql(arg) {
      this.$emit("execClick1", arg);
    },
    isEdit(arg) {
      this.$emit("flagClick", arg);
    },
    getSql(index) {
      this.sql_old = this.dataList[index].code;
    },
    clearSql() {
      this.sql_old = "";
    },
  },
};
</script>
<style>
.sql-result {
  min-height: 180px;
  border: 1px solid #dddddd;
  border-top: 0;
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
  padding: 10px;
}
</style>
