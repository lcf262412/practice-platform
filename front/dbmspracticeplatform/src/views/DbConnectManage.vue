<template>
  <!--系统数据库连接管理界面-->
  <el-container class="paper-page">
    <el-header>
      <common-header></common-header>
    </el-header>

    <div>
      <el-header class="paper-header">
        <div class="page-title3"><h1>连接详情</h1></div>
      </el-header>
    </div>
    <el-descriptions :column="1" border>
      <el-descriptions-item>
        <template slot="label">数据库IP</template>
        {{ descriptions.IP }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">数据库端口</template>
        {{ descriptions.port }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">数据库名称</template>
        {{ descriptions.dbName }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">数据库用户名</template>
        {{ descriptions.username }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template slot="label">数据库密码</template>
        {{ descriptions.password }}
      </el-descriptions-item>
    </el-descriptions>
    <el-dialog title="修改数据库信息" :visible.sync="dialogFormVisible">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item label="IP" :label-width="formLabelWidth" prop="IP">
          <el-input v-model="form.IP" placeholder="请输入"></el-input>
        </el-form-item>

        <el-form-item label="端口" :label-width="formLabelWidth" prop="port">
          <el-input v-model="form.port" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item
          label="数据库名称"
          prop="dbName"
          :label-width="formLabelWidth"
        >
          <el-input v-model="form.dbName" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item
          label="用户名"
          prop="username"
          :label-width="formLabelWidth"
        >
          <el-input v-model="form.username" placeholder="请输入"></el-input>
        </el-form-item>
        <el-form-item
          label="密码"
          prop="password"
          :label-width="formLabelWidth"
        >
          <el-input v-model="form.password" placeholder="请输入"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>

        <el-button type="primary" @click="modifyClick('form')">确 定</el-button>
      </div>
    </el-dialog>
    <div>
      <el-button @click="returnLast" style="padding-right: 25px"
        >返回</el-button
      >
      <el-button @click="modifyForm" type="primary">修改</el-button>
      <el-button @click="dialogFormVisible1 = true" type="primary"
        >更新连接</el-button
      >
    </div>
    <el-dialog title="确认修改信息" :visible.sync="dialogFormVisible1">
      <div id="warning" style="font-size: 20px; color: red">
        请认真核对数据库名称等关键信息,如有出错，将只能进行人工部署！
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible1 = false">取 消</el-button>

        <el-button type="primary" @click="refreshClick">确 定</el-button>
      </div>
    </el-dialog>
  </el-container>
</template>

<script>
import CommonHeader from "../components/CommonHeader.vue";
import { getDbConnectProps, setDbConnectProps, refreshDb } from "@/api/api.js";

export default {
  components: {
    CommonHeader,
  },
  data() {
    return {
      rules: {
        IP: [
          {
            required: true,
            message: "IP不能为空",
          },
        ],
        port: [
          {
            required: true,
            message: "端口不能为空",
          },
        ],
        dbName: [
          {
            required: true,
            message: "数据库名称不能为空",
          },
          {
            max: 30,
            message: "数据库名称不能超过30位",
          },
        ],
        username: [
          {
            required: true,
            message: "用户名不能为空",
          },
          {
            max: 30,
            message: "用户名不能超过30位",
          },
        ],
        password: [
          {
            require: true,
            message: "密码不能为空",
          },
          {
            min: 8,
            message: "密码不能小于8位",
          },
          {
            max: 30,
            message: "密码不能超过30位",
          },
        ],
      },
      descriptions: {
        password: "",
        port: "",
        IP: "",
        dbName: "",
        params: "",
        url: "",
        username: "",
      },
      req: {
        id: this.$route.query.questionId,
      },
      form: {
        dbName: "",
        IP: "",
        port: "",
        username: "",
        password: "",
      },
      dialogFormVisible: false,
      dialogFormVisible1: false,
      formLabelWidth: "120px",
    };
  },
  mounted() {
    getDbConnectProps().then((res) => {
      //查询当前连接信息

      const { code, result } = res.data;
      if (code === "0000") {
        this.descriptions = result;
      }
    });
  },
  methods: {
    modifyForm() {
      //点击修改按钮
      this.form.dbName = this.descriptions.dbName;
      this.form.IP = this.descriptions.IP;
      this.form.port = this.descriptions.port;
      this.form.username = this.descriptions.username;
      this.form.password = this.descriptions.password;
      this.dialogFormVisible = true;
    },
    modifyClick(formName) {
      //修改当前连接
      this.$refs[formName].validate((valid) => {
        if (valid) {
          setDbConnectProps(this.form).then((res) => {
            const { code, result } = res.data;
            if (code == "0000") {
              this.$router.go(0);
            } else {
              this.$alert(result.errmessage);
            }
            this.dialogFormVisible = false;
          });
        } else {
          return false;
        }
      });
    },

    refreshClick() {
      //重置连接

      refreshDb()
        .then((res) => {
          const { code, result } = res.data;
          if (code == "0000") {
          } else {
            this.$alert(result.errmessage);
          }
        })
        .catch((err) => {
          this.$alert("刷新失败");
        });
      this.$router.go(0);
    },
    returnLast() {
      this.$router.push({ path: "/admin/adminMenu" });
    },
  },
};
</script>

<style>
.el-header {
  padding: 0;
}
#button {
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
