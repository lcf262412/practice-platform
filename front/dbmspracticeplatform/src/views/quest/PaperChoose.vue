<template>
  <el-container class="paper-page">
    <el-header class="paper-header">
      <div class="page-title4"><h1>试卷选择</h1></div>
    </el-header>
    <el-main class="paper-main">
      <!-- 学生试卷选择表 -->
      <el-table
        v-if="this.$store.state.role == 'student'"
        :data="tableData"
        :header-cell-style="{
          fontSize: '18px',
        }"
        :cell-style="{
          fontFamily: 'MicroSoft YaHei',
          fontSize: '18px',
        }"
        :fit="true"
        style="width: 100%"
        @sort-change="sortChange"
        @row-click="handleRowClick"
      >
        <el-table-column sortable="custom" prop="exerciseId" label="试卷编号">
        </el-table-column>
        <el-table-column sortable="custom" prop="name" label="试卷名称">
        </el-table-column>
        <el-table-column sortable="custom" prop="startTime" label="起始日期">
        </el-table-column>
        <el-table-column sortable="custom" prop="endTime" label="截止日期">
        </el-table-column>
        <el-table-column
          prop="describe"
          label="试卷描述"
          :show-overflow-tooltip="true"
          sortable="custom"
          width="400"
        ></el-table-column>
        <el-table-column
          disabled
          prop="isTest"
          label="是否考试"
          sortable="custom"
          :formatter="formatBoolean"
        ></el-table-column>
        <el-table-column sortable="custom" prop="total_score" label="试卷总分">
        </el-table-column>
        <el-table-column sortable="custom" prop="get_score" label="试卷得分">
        </el-table-column>
      </el-table>

      <!-- 教师试卷选择表 -->
      <el-table
        v-if="this.$store.state.role == 'teacher'"
        :header-cell-style="{
          fontSize: '18px',
        }"
        :cell-style="{
          fontFamily: 'MicroSoft YaHei',
          fontSize: '18px',
        }"
        :default-sort="{ prop: 'e_id' }"
        :data="tableData"
        :fit="true"
        @sort-change="sortChange"
        style="width: 100%"
      >
        <el-table-column prop="e_id" label="试卷编号" sortable="custom">
        </el-table-column>
        <el-table-column prop="e_name" label="试卷名称" sortable="custom">
          <template slot-scope="scope">
            <div>
              <a href="javascript:;" @click="titleClick(scope.row)">{{
                scope.row.e_name
              }}</a>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="t_name" label="创建人" sortable="custom">
        </el-table-column>
        <el-table-column prop="t_id" label="创建人编号" sortable="custom">
        </el-table-column>
        <el-table-column prop="describe" label="说明" sortable="custom">
        </el-table-column>
      </el-table>
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 50, 100, 200]"
        :page-size="pagesize"
        layout="total,jumper,prev, pager, next,sizes"
        :total="total"
      ></el-pagination>
    </el-main>
  </el-container>
</template>

<script>
import { getClassAllExers, getOneStudentById, getAllExer,userLogout } from "@/api/api";

export default {
  data() {
    return {
      currentPage: 1,
      pagesize: 50,
      total: 0,
      tableAllData: [],
      tableData: [],
      classId: "",
    };
  },
  // 如何在同步方法里等待一个Promise对象的fullfilled？
  mounted() {
    // 通过获取单个学生信息接口拿到学生的所在班级
    if (this.$store.state.role === "student") {
      this.getStudentPaperList();
    } else {
      // 教师用户直接获取试卷列表
      this.getExerList();
    }
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
    // 学生获取试卷列表
    getStudentPaperList() {
      this.classId = this.studentClassId();
    },
    // 请求获取学生所在班级的全部试卷列表
    getExerList() {
      if (this.$store.state.role === "teacher") {
        getAllExer({ id: this.$store.state.username }).then((res) => {
          //获取全部试卷
          const { code, result } = res.data;
          if (code === "0000") {
            this.tableAllData = result;
            this.tableData = result.slice(
              (this.currentPage - 1) * this.pagesize,
              this.currentPage * this.pagesize
            );
            this.total = result.length;
          }

        });
      } else if (this.$store.state.role === "student") {
        let param = {
          stuClassId: this.classId,
          stuId: this.$store.state.username,
        };
        getClassAllExers(param).then((res) => {
          var { code, result } = res.data;
          if (code === "0000") {
            this.tableAllData = result;
            this.tableData = result.slice(
              (this.currentPage - 1) * this.pagesize,
              this.currentPage * this.pagesize
            );
            this.total = result.length;
          }
        });
      }
    },
    //排序切换
    sortChange(column, prop, order) {
      if (column.order === "descending") {
        this.tableAllData.sort(this.sortListDesc(column.column.property));

        this.tableData = this.tableAllData.slice(
          (this.currentPage - 1) * this.pagesize,
          this.currentPage * this.pagesize
        );
        this.total = this.tableAllData.length;
      } else if (column.order === "ascending") {
        this.tableAllData.sort(this.sortList(column.column.property));

        this.tableData = this.tableAllData.slice(
          (this.currentPage - 1) * this.pagesize,
          this.currentPage * this.pagesize
        );
        this.total = this.tableAllData.length;
      } else {
        this.getExerList();
      }
    },
    //通过数组对象的某个属性进行排序
    sortList(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (
          property == "e_id" ||
          property == "isTest" ||
          property == "exerciseId"
        ) {
          return value1 - value2;
        } else if (property == "describe") {
          if (a[property] == null) {
            return -1;
          }
          return a[property].localeCompare(b[property]);
        }
        return a[property].localeCompare(b[property]);
      };
    },

    //通过数组对象的某个属性进行倒序排列
    sortListDesc(property) {
      return function (a, b) {
        var value1 = a[property];
        var value2 = b[property];
        if (
          property == "e_id" ||
          property == "isTest" ||
          property == "exerciseId"
        ) {
          return value2 - value1;
        } else if (property == "describe") {
          if (b[property] == null) {
            return -1;
          }
          return b[property].localeCompare(a[property]);
        }
        return b[property].localeCompare(a[property]);
      };
    },

    //分页 初始页currentPage、初始每页数据数pagesize和数据testpage--->控制每页几条
    handleSizeChange(size) {
      this.pagesize = size;
      this.tableData = this.tableAllData.slice(
        (this.currentPage - 1) * this.pagesize,
        this.currentPage * this.pagesize
      );
      this.total = this.tableAllData.length;
    },
    // 控制页面的切换
    handleCurrentChange(currentPage) {
      this.currentPage = currentPage;
      this.tableData = this.tableAllData.slice(
        (this.currentPage - 1) * this.pagesize,
        this.currentPage * this.pagesize
      );
      this.total = this.tableAllData.length;
    },
    // 点击表格行事件回调方法
    handleRowClick(row) {
      sessionStorage.setItem("paperId", row.exerciseId);
      sessionStorage.setItem("isTest", row.isTest);
      this.$router.push({
        name: "paperInformation",
      });
    },
    /**
     * 获取学生所在班级
     * 调用获取单个学生信息接口
     * 用async/await等待接口返回结果
     * return classId
     */
    studentClassId() {
      let param = { id: this.$store.state.username };
      let classId = "";
      getOneStudentById(param).then((res) => {
        var { code, result } = res.data;
        if (code === "0000") {
          if(result==null){
            this.$alert("学生不存在！", {
              confirmButtonText: "确认",
              callback: (action) => {
                userLogout({id:this.$store.state.username}).then((res) => {
                  const { code, result } = res.data;
                  if (code === "0000") {
                    
                    this.$store.commit("resetState");
                    
                    // 清理sessionStorage
                    sessionStorage.clear();
                    
                    this.$resetSetItem('clear','22222')
                    this.$router.push("/");
                    
                  }
                });
              },
            });
          }
          else if(result.isactive==false){
            this.$alert("学生用户已禁用,无法进入该模块！", {
              confirmButtonText: "确认",
              callback: (action) => {
                userLogout({id:this.$store.state.username}).then((res) => {
                  const { code, result } = res.data;
                  if (code === "0000") {
                    
                    this.$store.commit("resetState");
                    
                    // 清理sessionStorage
                    sessionStorage.clear();
                    
                    this.$resetSetItem('clear','22222')
                    this.$router.push("/");
                    
                  }
                });
              },
            });
            
          }
          
          else{
            this.classId = result.classId;
            
            
            this.getExerList();
          }
          
        }
      });
      return classId;
    },
    delClick() {},
    publicClick() {},
    modifyClick() {},
    // 教师选择试卷点击回调
    titleClick(row) {
      sessionStorage.setItem("paperId", row.e_id);
      this.$router.push({
        name: "paperInformation",
      });
    },
    stateFormat1() {},
    stateFormat2() {},
  },
};
</script>

<style>
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
.paper-main {
  margin-top: 10px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 1px 1px 1px rgb(154, 154, 154);
}
.el-table .cell {
  cursor: pointer;
}
</style>
