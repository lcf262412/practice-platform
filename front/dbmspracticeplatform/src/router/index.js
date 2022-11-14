import store from '@/store';
import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 页面文件注册
const login = r => require.ensure([], () => r(require('../views/LoginPage')), 'login');
const modeChoose = r => require.ensure([], () => r(require('../views/ModeChoose')), 'modeChoose');
const practicMode = r => require.ensure([], () => r(require('../views/practice/PracticeMode')), 'practiceMode');
const questPage = r => require.ensure([], () => r(require('../views/quest/QuestPage')), 'questMode');
const paperChoose = r => require.ensure([], () => r(require('../views/quest/PaperChoose')), 'questMode');
const paperInformation = r => require.ensure([], () => r(require('../views/quest/PaperInformation')), 'questMode');
const teacherMode = r => require.ensure([], () => r(require('../components/TeacherMode')), 'teacherMode');
const adminMode = r => require.ensure([], () => r(require('../components/AdminMode')), 'adminMode');
const teacherMenu = r => require.ensure([], () => r(require('../views/teacher/TeacherMenu')), 'teacherMode');
const question = r => require.ensure([], () => r(require('../views/Question')), 'teacherMode');
const addQuestion = r => require.ensure([], () => r(require('../views/AddQuestion')), 'teacherMode');
const questionContent = r => require.ensure([], () => r(require('../views/QuestionContent')), 'teacherMode');
const exercise = r => require.ensure([], () => r(require('../views/Exercise')), 'teacherMode');
const exerciseContent = r => require.ensure([], () => r(require('../views/ExerciseContent')), 'teacherMode');
const addExercise = r => require.ensure([], () => r(require('../views/AddExercise')), 'teacherMode');
const modifyExerQuest = r => require.ensure([], () => r(require('../views/ModifyExerQuest')), 'teacherMode');
const addExerQuest = r => require.ensure([], () => r(require('../views/AddExerQuest')), 'teacherMode');
const releaseExer = r => require.ensure([], () => r(require('../views/ReleaseExer')), 'teacherMode');
const modifyQuest = r => require.ensure([], () => r(require('../views/ModifyQuest')), 'teacherMode');
const classManager = r => require.ensure([], () => r(require('../views/Class')), 'teacherMode');
const classStudent = r => require.ensure([], () => r(require('../views/ClassStudent')), 'teacherMode');
const studentAnswer = r => require.ensure([], () => r(require('../views/StudentAnswer')), 'teacherMode');
const student = r => require.ensure([], () => r(require('../views/Student')), 'teacherMode');
const modifyTestCases = r => require.ensure([], () => r(require('../views/ModifyTestCases')), 'teacherMode');
const adminMenu = r => require.ensure([], () => r(require('../views/admin/AdminMenu')), 'adminMode');
const teacherManage = r => require.ensure([], () => r(require('../views/TeacherManage')), 'adminMode');
const studentManage = r => require.ensure([], () => r(require('../views/StudentManage')), 'adminMode');
const dbConnectManage = r => require.ensure([], () => r(require('../views/DbConnectManage')), 'adminMode');
const exerciseManage = r => require.ensure([], () => r(require('../views/ExerciseManage')), 'adminMode');
const questionManage = r => require.ensure([], () => r(require('../views/QuestionManage')), 'adminMode');
const modifyPassword = r => require.ensure([], () => r(require('../views/ModifyPassword')), 'userMode');
const teacherDbManage = r => require.ensure([], () => r(require('../views/practice/TeacherDbManage')), 'practiceMode');


const routes = [
  {
    path: '/main',
    name: 'Main',
    component: ()=> import('../views/MainPage.vue'),
    children: [
      {
        path: '/db-manage',
        name: 'teacherDbManage',
        component: teacherDbManage,
      },
      {
        path: '/practice',
        name: 'practiceMode',
        // meta: { hidden: ture },
        component: practicMode,
      },
      {
        path: '/quest/answer-question',
        name: 'questPage',
        component: questPage,
      },
      {
        path: '/quest/choose-paper',
        name: 'paperChoose',
        component: paperChoose,
      },
      {
        path: '/quest/paper-info',
        name: 'paperInformation',
        component: paperInformation,
      },
      {
        path: '/teacher',
        name: 'teacherMode',
        component: teacherMode,
      },    
      {
        path: '/admin',
        name: 'adminMode',
        component: adminMode,
      }, 
    ]
  },
  {
    path: '/teacher/teacherMenu',
    name: 'TeacherMenu',
    component: teacherMenu,
  },
  {
    path: '/question',
    name: 'Question',
    component: question,
  },
  {
    path: '/addQuestion',
    name: 'AddQuestion',
    component: addQuestion,
  },
  {
    path: '/modifyQuest',
    name: 'ModifyQuest',
    component: modifyQuest,
  },
  {
    path: '/modifyTestCases',
    name: 'ModifyTestCases',
    component: modifyTestCases,
  },
  {
    path: '/questionContent',
    name: 'questionContent',
    component: questionContent,
  },
  {
    path: '/exercise',
    name: 'Exercise',
    component: exercise,
  },
  {
    path: '/exerciseContent',
    name: 'ExerciseContent',
    component: exerciseContent,
  },
  {
    path: '/addExercise',
    name: 'AddExercise',
    component: addExercise,
  },
  {
    path: '/modifyExerQuest',
    name: 'ModifyExerQuest',
    component: modifyExerQuest,
  },
  {
    path: '/addExerQuest',
    name: 'AddExerQuest',
    component: addExerQuest,
  },
  {
    path: '/releaseExer',
    name: 'ReleaseExer',
    component: releaseExer,
  },
  {
    path: '/class',
    name: 'ClassManager',
    component: classManager,
  },
  {
    path: '/classStudent',
    name: 'ClassStudent',
    component: classStudent,
  },
  {
    path: '/student',
    name: 'Student',
    component: student,
  },
  {
    path: '/studentAnswer',
    name: 'StudentAnswer',
    component: studentAnswer,
  },
  {
    path: '/admin/adminMenu',
    name: 'AdminMenu',
    component: adminMenu,
  }, 
  {
    path: '/admin/teacherManage',
    name: 'TeacherManage',
    component: teacherManage,
  },
  {
    path: '/admin/studentManage',
    name: 'StudentManage',
    component: studentManage,
  },
  {
    path: '/admin/dbConnectManage',
    name: 'DbConnectManage',
    component: dbConnectManage,
  },
  {
    path: '/admin/exerciseManage',
    name: 'ExerciseManage',
    component: exerciseManage,
  },
  {
    path: '/admin/questionManage',
    name: 'QuestionManage',
    component: questionManage,
  },
  {
    path: '/user',
    name: 'User',
    component: ()=> import('../views/User.vue'),
  },
  {
    path: '/user/modifyPassword',
    name: 'ModifyPassword',
    component: modifyPassword,
  },
  {
    path: '/',
    name: 'login',
    component: login,
  },
  {
    path: '/choose',
    name: 'modeChoose',
    component: modeChoose,
  },
]

const router = new VueRouter({
  base: '/vue/',
  mode: 'history',
  routes,
})

// 路由守卫
router.beforeEach(( to, from, next ) => {
  const token = sessionStorage.getItem('token');
  const username=sessionStorage.getItem('username')
  store.dispatch('onLoading', true);
  // 如果token不存在，即未登录，则跳回到登录界面
  if (to.name !== 'login' && !token) {
    next ({ name: 'login'})
  }
  else if(to.name=='login'&&username){
    next({name:from.name})
  }
  else {
    next();
  } 
    
})
router.afterEach((to, from) => {
  setTimeout(function() {
    store.dispatch('onLoading', false);
  }, 1000);
});
const ruleMapping = {}

export function initDynamicRoutes () {
    
  /**
   * 根据二级权限，对路由规则进行动态添加
   * this.router.options.routes可以拿到初始化时配置的路由规则
   * this.$route 可以拿到当前路由信息 （包括路由路径，参数）
   */
  const currentRoutes = router.options.routes;
  const rightList = this.$store.state.rightList;
  rightList.forEach ( item =>{ // 如果是没有子路由的话，就直接添加进去；如果有子路由的话就进入二级权限遍历
    if (item.path) {
      const temp = ruleMapping[item.path];
      // 路由规则中添加元数据meta
      temp.meta = item.rights;
      currentRoutes[1].children.push(temp);
    }

    item.children.forEach ( item => { // item 二级权限
      const temp = ruleMapping[item.path];
      // 路由规则中添加元数据meta
      temp.meta = item.rights;
      currentRoutes[1].children.push(temp);
    })
  })

  router.addRoutes(currentRoutes);
}

export default router