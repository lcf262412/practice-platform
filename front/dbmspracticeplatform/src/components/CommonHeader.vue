<!-- 
  --功能模块通用Header
  --顶栏导航菜单
 -->
<template>
  <el-container>
    <el-header>
      <div class="header-aside">
        <img
          class="logo"
          src="../assets/logo-horizontal.png"
          alt="openGauss"
          height="80%"
        />
      </div>
      <el-menu
        class="el-menu-demo"
        mode="horizontal"
       
        background-color="#B2D9FB"
        text-color="#000000"
        active-text-color="#FFFFFF"
        :router="true"
      >
        <el-menu-item
          v-show="this.$store.state.role !== 'admin'"
          
          @click="handleClick('/practice')"
          >openGauss实践</el-menu-item
        >
        <el-menu-item
          v-show="this.$store.state.role !== 'admin'"
          
          @click="handleClick('/quest/choose-paper')"
          >能力评估</el-menu-item
        >
        <el-menu-item
          
          v-show="this.$store.state.role === 'teacher'"
          @click="handleClick('/teacher/teacherMenu')"
          >教师功能</el-menu-item
        >
        <el-menu-item
         
          v-show="this.$store.state.role === 'admin'"
          @click="handleClick('/admin/adminMenu')"
          >系统管理</el-menu-item
        >
      </el-menu>
      <!-- 右上角个人中心菜单 -->
      <el-dropdown @command="handleCommand">
        <span class="iconspan" trigger="click">
          <el-button class="el-dropdown-link" icon="el-icon-user" round
            >个人中心</el-button
          >
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="1">修改密码</el-dropdown-item>
          <el-dropdown-item command="2">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </el-header>
  </el-container>
</template>

<script>
  import { userLogout } from "@/api/api.js";
export default {
  data() {
    return {};
  },
  methods: {
    ifRole(grant) {
      if (
        this.$store.state.role === "teacher" ||
        this.$store.state.role === "admin"
      ) {
        alert("granted");
        return true;
      }
      return false;
    },
    handleSelect(key, keyPath) {
    },
    handleClick(path) {
      if (path === "/practice") {
        if (this.$store.state.role === "student") {
          this.$router.push(path);
        } else {
          this.$router.push("/db-manage");
        }
      } else {
        this.$router.push(path);
      }
    },
    handleCommand(command) {
      switch (command) {
        case "1":
          this.$router.push("/user/modifyPassword");
          break;
        case "2":
          // 退出登录
          var id_t=''
          if(sessionStorage.getItem('selectRole')){
            id_t=sessionStorage.getItem('selectRole')
          }
          else{
            id_t=this.$store.state.username
          }
          userLogout({id:id_t}).then((res) => {
              const { code, result } = res.data;
              if (code === "0000") {
                
                this.$store.commit("resetState");
                
                // 清理sessionStorage
                sessionStorage.clear();
                this.$resetSetItem('clear','22222')
                this.$router.push("/");
                
              }
            });
          
          break;
        default:
          break;
      }
    },
  },
};
</script>

<style lang="css">
.el-header {
  display: flex;
  flex-direction: row;
}

.header-aside {
  height: 100%;
  width: 250px;
  background-color: #b2d9fb;
}

.logo {
  margin-top: 5px;
  margin-left: 5px;
}
.el-dropdown-link {
  margin-top: 10px;
  background: rgb(98, 204, 204);
}
.iconspan {
}
.el-menu {
  width: 100%;
}

.el-menu-item {
  font-size: 20px;
}

.el-menu.el-menu--horizontal {
  border-bottom: 0px;
}

.el-dropdown {
  background-color: #b2d9fb;
}
</style>
